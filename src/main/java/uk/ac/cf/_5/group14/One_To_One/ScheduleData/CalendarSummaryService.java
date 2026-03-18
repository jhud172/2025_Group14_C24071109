package uk.ac.cf._5.group14.One_To_One.ScheduleData;

import org.springframework.stereotype.Service;

import uk.ac.cf._5.group14.One_To_One.CalendarData.CalendarTask;
import uk.ac.cf._5.group14.One_To_One.CalendarData.CalendarTaskRepository;
import uk.ac.cf._5.group14.One_To_One.Nutrition.DailyNutritionLog;
import uk.ac.cf._5.group14.One_To_One.Nutrition.DailyNutritionLogRepository;
import uk.ac.cf._5.group14.One_To_One.StrengthLog.Repository.SetLogRepository;
import uk.ac.cf._5.group14.One_To_One.StrengthLog.WorkoutSession;
import uk.ac.cf._5.group14.One_To_One.StrengthLog.Repository.WorkoutSessionRepository;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class CalendarSummaryService {

    private static final int LOAD_SCORE_MAX = 20;

    private final WorkoutSessionRepository workoutSessionRepository;
    private final SetLogRepository setLogRepository;
    private final DailyNutritionLogRepository nutritionLogRepository;
    private final CalendarTaskRepository calendarTaskRepository;
    private final ScheduleOccurrenceRepository scheduleOccurrenceRepository;

    public CalendarSummaryService(
            WorkoutSessionRepository workoutSessionRepository,
            SetLogRepository setLogRepository,
            DailyNutritionLogRepository nutritionLogRepository,
            CalendarTaskRepository calendarTaskRepository,
            ScheduleOccurrenceRepository scheduleOccurrenceRepository
    ) {
        this.workoutSessionRepository = workoutSessionRepository;
        this.setLogRepository = setLogRepository;
        this.nutritionLogRepository = nutritionLogRepository;
        this.calendarTaskRepository = calendarTaskRepository;
        this.scheduleOccurrenceRepository = scheduleOccurrenceRepository;
    }

    public List<CalendarDaySummary> getSummary(User user, LocalDate start, LocalDate end) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("Authenticated user is required");
        }
        if (start == null || end == null) {
            throw new IllegalArgumentException("Start and end dates are required");
        }
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("End date must be on or after start date");
        }

        List<WorkoutSession> sessions = workoutSessionRepository.findByUserAndDateBetweenOrderByDateDesc(user, start, end);
        Map<LocalDate, Boolean> hasWorkoutByDate = new HashMap<>();
        for (WorkoutSession session : sessions) {
            if (session.getDate() == null) continue;
            if (session.isCompleted()) {
                hasWorkoutByDate.put(session.getDate(), true);
            }
        }

        Map<LocalDate, Long> setCounts = new HashMap<>();
        for (SetLogRepository.SetLogDailyTotal total : setLogRepository.countCompletedSetsByUserAndDateRange(user, start, end)) {
            setCounts.put(total.getDate(), total.getTotal());
        }

        Set<LocalDate> nutritionDates = new HashSet<>();
        for (DailyNutritionLog log : nutritionLogRepository.findByUserAndDateBetweenOrderByDateAsc(user, start, end)) {
            if (log.getDate() != null) {
                nutritionDates.add(log.getDate());
            }
        }

        Map<LocalDate, Long> taskCounts = new HashMap<>();
        for (CalendarTask task : calendarTaskRepository.findByUserAndDateBetween(user, start, end)) {
            if (task.getDate() != null) {
                taskCounts.merge(task.getDate(), 1L, Long::sum);
            }
        }

        Map<LocalDate, Long> occurrenceCounts = new HashMap<>();
        for (ScheduleOccurrence occurrence : scheduleOccurrenceRepository.findByUserAndDateBetween(user, start, end)) {
            if (occurrence.getDate() != null) {
                occurrenceCounts.merge(occurrence.getDate(), 1L, Long::sum);
            }
        }

        return buildSummaryRange(start, end, hasWorkoutByDate, setCounts, nutritionDates, taskCounts, occurrenceCounts);
    }

    private List<CalendarDaySummary> buildSummaryRange(
            LocalDate start,
            LocalDate end,
            Map<LocalDate, Boolean> hasWorkoutByDate,
            Map<LocalDate, Long> setCounts,
            Set<LocalDate> nutritionDates,
            Map<LocalDate, Long> taskCounts,
            Map<LocalDate, Long> occurrenceCounts
    ) {
        List<CalendarDaySummary> results = new java.util.ArrayList<>();
        LocalDate cursor = start;
        while (!cursor.isAfter(end)) {
            boolean hasWorkout = hasWorkoutByDate.getOrDefault(cursor, false);
            boolean hasNutrition = nutritionDates.contains(cursor);
            long setCount = setCounts.getOrDefault(cursor, 0L);
            int loadScore = computeLoadScore(setCount, hasWorkout,
                    taskCounts.getOrDefault(cursor, 0L),
                    occurrenceCounts.getOrDefault(cursor, 0L));

            results.add(new CalendarDaySummary(cursor, loadScore, hasWorkout, hasNutrition, 0));
            cursor = cursor.plusDays(1);
        }
        return results;
    }

    private int computeLoadScore(long setCount, boolean hasWorkout, long taskCount, long occurrenceCount) {
        if (setCount > 0) {
            return (int) Math.min(LOAD_SCORE_MAX, setCount);
        }
        int scheduledCount = (int) (taskCount + occurrenceCount);
        int heuristic = Math.min(3, scheduledCount);
        int base = hasWorkout ? 2 : 0;
        return base + heuristic;
    }

    public record CalendarDaySummary(
            LocalDate date,
            int loadScore,
            boolean hasWorkout,
            boolean hasNutrition,
            int prHitCount
    ) {
    }
}
