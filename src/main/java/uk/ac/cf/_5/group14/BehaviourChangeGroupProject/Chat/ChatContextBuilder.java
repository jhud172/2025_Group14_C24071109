package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Chat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTask;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTaskRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.DailyCompletionStatus;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.DailyStreakDaySummary;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.DailyStreakService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleOccurrence;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleOccurrenceRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;

/**
 * Builds structured context for the chat page and AI requests.
 * Provides personalised summaries for zero-blank state and context injection.
 */
@Service
public class ChatContextBuilder {

    private static final Logger log = LoggerFactory.getLogger(ChatContextBuilder.class);

    private final CalendarTaskRepository calendarTaskRepository;
    private final ScheduleOccurrenceRepository scheduleOccurrenceRepository;
    private final DailyStreakService dailyStreakService;
    private final Clock clock;

    public ChatContextBuilder(
            CalendarTaskRepository calendarTaskRepository,
            ScheduleOccurrenceRepository scheduleOccurrenceRepository,
            DailyStreakService dailyStreakService,
            Clock clock
    ) {
        this.calendarTaskRepository = calendarTaskRepository;
        this.scheduleOccurrenceRepository = scheduleOccurrenceRepository;
        this.dailyStreakService = dailyStreakService;
        this.clock = clock;
    }

    /**
     * Builds the page summary DTO for the chat page model and /chat/context endpoint.
     */
    public ChatSummaryDto buildSummary(User user) {
        LocalDate today = LocalDate.now(clock);
        LocalTime now = LocalTime.now(clock);

        String greeting = computeGreeting(now, user);

        // Today's tasks
        int tasksDone = 0;
        int tasksTotal = 0;
        try {
            List<CalendarTask> todayTasks = calendarTaskRepository.findByUserAndDateOrderByTime(user, today);
            tasksTotal = todayTasks.size();
            tasksDone = (int) todayTasks.stream().filter(t -> Boolean.TRUE.equals(t.getCompleted())).count();
        } catch (Exception e) {
            log.warn("Could not load today's tasks for chat summary", e);
        }

        // Today's schedule occurrences (workouts)
        int workoutsDone = 0;
        int workoutsTotal = 0;
        String nextWorkoutName = null;
        String nextWorkoutDate = null;
        try {
            List<ScheduleOccurrence> todayOccurrences = scheduleOccurrenceRepository.findByUserAndDate(user, today);
            workoutsTotal = todayOccurrences.size();
            workoutsDone = (int) todayOccurrences.stream().filter(ScheduleOccurrence::isCompleted).count();

            // Find next workout: first future occurrence after today
            List<ScheduleOccurrence> active = scheduleOccurrenceRepository.findActiveByUser(user);
            ScheduleOccurrence next = active.stream()
                    .filter(o -> o.getDate() != null && o.getDate().isAfter(today))
                    .min(Comparator.comparing(ScheduleOccurrence::getDate))
                    .orElse(null);
            if (next != null) {
                nextWorkoutDate = next.getDate().toString();
                String exerciseName = next.getExercise() != null ? next.getExercise().getName() : null;
                if (exerciseName == null && next.getCustomExercise() != null) {
                    exerciseName = next.getCustomExercise().getName();
                }
                nextWorkoutName = next.getScheduleName() != null ? next.getScheduleName() :
                        (exerciseName != null ? exerciseName : "Workout");
            }
        } catch (Exception e) {
            log.warn("Could not load workout data for chat summary", e);
        }

        // Streak: consecutive days (going back from yesterday) where completion >= 1 item done
        int streakDays = 0;
        try {
            LocalDate streakEnd = today.minusDays(1);
            LocalDate streakStart = streakEnd.minusDays(13); // look back up to 14 days
            if (!streakEnd.isBefore(streakStart)) {
                List<DailyStreakDaySummary> range = dailyStreakService.calculateRange(user, streakStart, streakEnd, today);
                // Count consecutive days from yesterday backwards where status is GREEN or ORANGE
                for (int i = range.size() - 1; i >= 0; i--) {
                    DailyStreakDaySummary day = range.get(i);
                    if (day.status() == DailyCompletionStatus.GREEN || day.status() == DailyCompletionStatus.ORANGE) {
                        streakDays++;
                    } else {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Could not compute streak for chat summary", e);
        }

        // Today's completion percentage
        int totalItems = tasksTotal + workoutsTotal;
        int doneItems = tasksDone + workoutsDone;
        int completionPct = totalItems > 0 ? (doneItems * 100) / totalItems : 0;

        return new ChatSummaryDto(
                greeting,
                today.toString(),
                tasksDone,
                tasksTotal,
                workoutsDone,
                workoutsTotal,
                completionPct,
                nextWorkoutName,
                nextWorkoutDate,
                streakDays
        );
    }

    private String computeGreeting(LocalTime now, User user) {
        String name = (user != null && user.getFirstName() != null && !user.getFirstName().isBlank())
                ? ", " + user.getFirstName()
                : "";
        int hour = now.getHour();
        if (hour >= 5 && hour < 12) return "Good morning" + name;
        if (hour >= 12 && hour < 17) return "Good afternoon" + name;
        if (hour >= 17 && hour < 21) return "Good evening" + name;
        return "Hey" + name;
    }

    /**
     * Returns the time-of-day theme token for use in data attributes and CSS.
     */
    public String computeTimeTheme(LocalTime now) {
        int hour = now.getHour();
        if (hour >= 5 && hour < 12) return "morning";
        if (hour >= 12 && hour < 17) return "midday";
        if (hour >= 17 && hour < 21) return "evening";
        return "night";
    }
}
