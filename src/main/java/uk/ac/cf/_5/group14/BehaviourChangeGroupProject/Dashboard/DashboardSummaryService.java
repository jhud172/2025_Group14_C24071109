package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Dashboard;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTask;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTaskRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Dashboard.dto.DashboardSummaryDto;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleOccurrence;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleOccurrenceRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

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
        LocalDate startOfWeek = today.with(WeekFields.ISO.dayOfWeek(), 1);
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        List<CalendarTask> weekTasks = calendarTaskRepository.findByUserAndDateBetween(user, startOfWeek, endOfWeek);
        List<ScheduleOccurrence> weekWorkouts = scheduleOccurrenceRepository.findByUserAndDateBetween(user, startOfWeek, endOfWeek);

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

            week.add(new DashboardSummaryDto.WeekDaySummary(
                cursor,
                DAY_LABEL.format(cursor),
                DAY_NUMBER.format(cursor),
                dayTasks.size(),
                dayWorkouts.size(),
                cursor.equals(today)
            ));

            cursor = cursor.plusDays(1);
        }

        return new DashboardSummaryDto(tasksDueToday, workoutsDueToday, today, week);
    }
}
