package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Dashboard;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTask;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTaskRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Dashboard.dto.TimelineMonthDto;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleOccurrence;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleOccurrenceRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TimelineDataService {

    private final CalendarTaskRepository calendarTaskRepository;
    private final ScheduleOccurrenceRepository scheduleOccurrenceRepository;

    public TimelineDataService(CalendarTaskRepository calendarTaskRepository,
                               ScheduleOccurrenceRepository scheduleOccurrenceRepository) {
        this.calendarTaskRepository = calendarTaskRepository;
        this.scheduleOccurrenceRepository = scheduleOccurrenceRepository;
    }

    /**
     * Get 12-month timeline data for the user.
     * Aggregates completed logs, sessions, and tasks for each month.
     *
     * @param user The user to get timeline data for
     * @return List of TimelineMonthDto for past 12 months (in chronological order)
     */
    @Transactional(readOnly = true)
    public List<TimelineMonthDto> getTimelineData(User user) {
        LocalDate today = LocalDate.now();
        LocalDate twelvMonthsAgo = today.minusMonths(12);

        // Fetch completed logs (schedule occurrences) from the past 12 months
        List<ScheduleOccurrence> completedLogs = scheduleOccurrenceRepository
                .findByUserAndDateBetween(user, twelvMonthsAgo, today).stream()
                .filter(ScheduleOccurrence::isCompleted)
                .toList();

        // Fetch completed tasks from the past 12 months
        List<CalendarTask> completedTasks = calendarTaskRepository
                .findByUserAndDateBetween(user, twelvMonthsAgo, today).stream()
                .filter(task -> Boolean.TRUE.equals(task.getCompleted()))
                .toList();

        // Group logs by year-month
        Map<YearMonth, List<ScheduleOccurrence>> logsByMonth = completedLogs.stream()
                .collect(Collectors.groupingBy(log -> YearMonth.from(log.getDate())));

        // Group tasks by year-month
        Map<YearMonth, List<CalendarTask>> tasksByMonth = completedTasks.stream()
                .collect(Collectors.groupingBy(task -> YearMonth.from(task.getDate())));

        // Build timeline for 12 months
        List<TimelineMonthDto> timeline = new ArrayList<>();
        YearMonth cursor = YearMonth.from(twelvMonthsAgo);
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMM");

        for (int i = 0; i < 12; i++) {
            List<ScheduleOccurrence> monthLogs = logsByMonth.getOrDefault(cursor, List.of());
            List<CalendarTask> monthTasks = tasksByMonth.getOrDefault(cursor, List.of());

            // Count sessions (workouts) - each log represents one session
            int totalSessions = (int) monthLogs.stream()
                    .filter(log -> log.getExercise() != null)
                    .count();

            // All logs
            int totalLogs = monthLogs.size();

            // All completed tasks
            int totalTasks = monthTasks.size();

            timeline.add(new TimelineMonthDto(
                    cursor.format(monthFormatter),
                    totalLogs,
                    totalSessions,
                    totalTasks
            ));

            cursor = cursor.plusMonths(1);
        }

        return timeline;
    }
}
