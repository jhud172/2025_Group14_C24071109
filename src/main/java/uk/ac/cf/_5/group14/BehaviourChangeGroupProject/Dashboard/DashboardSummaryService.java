package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Dashboard;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTask;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTaskRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Dashboard.dto.DashboardSummaryDto;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleOccurrence;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleOccurrenceRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

@Service
public class DashboardSummaryService {

    private static final DateTimeFormatter DAY_LABEL = DateTimeFormatter.ofPattern("EEE", Locale.UK);
    private static final DateTimeFormatter DAY_NUMBER = DateTimeFormatter.ofPattern("d");

    private final CalendarTaskRepository calendarTaskRepository;
    private final ScheduleOccurrenceRepository scheduleOccurrenceRepository;

    public DashboardSummaryService(CalendarTaskRepository calendarTaskRepository,
                                   ScheduleOccurrenceRepository scheduleOccurrenceRepository) {
        this.calendarTaskRepository = calendarTaskRepository;
        this.scheduleOccurrenceRepository = scheduleOccurrenceRepository;
    }

    @Transactional(readOnly = true)
    public DashboardSummaryDto getSummary(User user) {
        LocalDate today = LocalDate.now();
        // Show next 7 days starting from today (not ISO week)
        LocalDate startOfWeek = today;
        LocalDate endOfWeek = today.plusDays(6);
        LocalDate lastWeekStart = startOfWeek.minusDays(7);
        LocalDate lastWeekEnd = startOfWeek.minusDays(1);

        List<CalendarTask> weekTasks = calendarTaskRepository.findByUserAndDateBetween(user, startOfWeek, endOfWeek);
        List<ScheduleOccurrence> weekWorkouts = scheduleOccurrenceRepository.findByUserAndDateBetween(user, startOfWeek, endOfWeek);
        
        List<ScheduleOccurrence> lastWeekWorkouts = scheduleOccurrenceRepository.findByUserAndDateBetween(user, lastWeekStart, lastWeekEnd);

        Map<LocalDate, List<CalendarTask>> tasksByDate = weekTasks.stream()
            .collect(Collectors.groupingBy(CalendarTask::getDate));
        Map<LocalDate, List<ScheduleOccurrence>> workoutsByDate = weekWorkouts.stream()
            .collect(Collectors.groupingBy(ScheduleOccurrence::getDate));

        int tasksDueToday = tasksByDate.getOrDefault(today, List.of()).stream()
            .filter(task -> !Boolean.TRUE.equals(task.getCompleted()))
            .toList()
            .size();

        int workoutsDueToday = workoutsByDate.getOrDefault(today, List.of()).stream()
            .filter(occ -> !occ.isCompleted())
            .toList()
            .size();

        List<DashboardSummaryDto.WeekDaySummary> week = new ArrayList<>();
        LocalDate cursor = startOfWeek;
        for (int i = 0; i < 7; i++) {
            List<CalendarTask> dayTasks = tasksByDate.getOrDefault(cursor, List.of());
            List<ScheduleOccurrence> dayWorkouts = workoutsByDate.getOrDefault(cursor, List.of());

            int tasksCompletedCount = (int) dayTasks.stream()
                    .filter(task -> Boolean.TRUE.equals(task.getCompleted()))
                    .count();
            int workoutsCompletedCount = (int) dayWorkouts.stream()
                    .filter(ScheduleOccurrence::isCompleted)
                    .count();
            int totalCount = dayTasks.size() + dayWorkouts.size();
            int completedCount = tasksCompletedCount + workoutsCompletedCount;

            String dayStatus;
            if (cursor.equals(today)) {
                dayStatus = "today";
            } else if (cursor.isAfter(today)) {
                dayStatus = "upcoming";
            } else if (totalCount == 0) {
                dayStatus = "clear";
            } else if (totalCount > 0 && completedCount == totalCount) {
                dayStatus = "completed";
            } else {
                dayStatus = "missed";
            }
            
            List<String> taskTitles = dayTasks.stream()
                .map(CalendarTask::getTitle)
                .toList();
            
            List<String> workoutTitles = dayWorkouts.stream()
                .map(ScheduleOccurrence::getScheduleName)
                .toList();

            week.add(new DashboardSummaryDto.WeekDaySummary(
                cursor,
                DAY_LABEL.format(cursor),
                DAY_NUMBER.format(cursor),
                dayTasks.size(),
                dayWorkouts.size(),
                tasksCompletedCount,
                workoutsCompletedCount,
                cursor.equals(today),
                cursor.equals(today.plusDays(1)),
                dayStatus,
                taskTitles,
                workoutTitles
            ));

            cursor = cursor.plusDays(1);
        }

        Set<LocalDate> recentCompleted = loadCompletedSet(user, today);
        int logsThisWeekCount = (int) weekWorkouts.stream().filter(ScheduleOccurrence::isCompleted).count();
        int lastWeekLogsCount = (int) lastWeekWorkouts.stream().filter(ScheduleOccurrence::isCompleted).count();
        int workoutStreak = computeWorkoutStreak(recentCompleted, today);
        String charlieContext = calculateCharlieContext(user, today, logsThisWeekCount, workoutStreak);

        return new DashboardSummaryDto(tasksDueToday, workoutsDueToday, today, week,
                computeWeeklyWorkoutsCompleted(user, today),
                workoutStreak,
                computeDaysSinceLastWorkout(recentCompleted, today),
                user.isSubscriptionStatus(),
                logsThisWeekCount,
                lastWeekLogsCount,
                charlieContext);
    }

    private int computeWeeklyWorkoutsCompleted(User user, LocalDate today) {
        LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        return scheduleOccurrenceRepository
                .findCompletedDatesByUserAndDateBetween(user, monday, today).size();
    }

    private int computeWorkoutStreak(Set<LocalDate> completedSet, LocalDate today) {
        int streak = 0;
        int startOffset = completedSet.contains(today) ? 0 : 1;
        for (int i = startOffset; i <= 89; i++) {
            if (completedSet.contains(today.minusDays(i))) {
                streak++;
            } else {
                break;
            }
        }
        return streak;
    }

    private int computeDaysSinceLastWorkout(Set<LocalDate> completedSet, LocalDate today) {
        List<LocalDate> sorted = completedSet.stream()
                .sorted((a, b) -> b.compareTo(a))
                .toList();
        if (sorted.isEmpty()) {
            return -1;
        }
        LocalDate mostRecent = sorted.get(0);
        return (int) (today.toEpochDay() - mostRecent.toEpochDay());
    }

    private Set<LocalDate> loadCompletedSet(User user, LocalDate today) {
        List<LocalDate> dates = scheduleOccurrenceRepository
                .findCompletedDatesByUserAndDateBetween(user, today.minusDays(89), today);
        return new HashSet<>(dates);
    }

    private String calculateCharlieContext(User user, LocalDate today, int logsThisWeekCount, int workoutStreak) {
        java.time.LocalTime currentTime = java.time.LocalTime.now();
        int currentHour = currentTime.getHour();
        String timeOfDay = currentHour < 12 ? "morning" : (currentHour < 17 ? "afternoon" : "evening");
        
        if ("morning".equals(timeOfDay)) {
            return logsThisWeekCount == 0 ? "morning-low" : "morning-strong";
        } else if ("evening".equals(timeOfDay)) {
            return logsThisWeekCount == 0 ? "evening-none" : "evening-strong";
        } else {
            // afternoon
            if (workoutStreak > 0 && logsThisWeekCount > 2) {
                return "celebrate";
            } else {
                return "midday-encourage";
            }
        }
    }
}
