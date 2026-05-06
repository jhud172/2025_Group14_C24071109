package uk.ac.cf._5.group14.One_To_One.DayHealthData;

import org.springframework.stereotype.Service;
import uk.ac.cf._5.group14.One_To_One.BehaviourMemoryData.BehaviourMemoryService;
import uk.ac.cf._5.group14.One_To_One.CalendarData.CalendarTask;
import uk.ac.cf._5.group14.One_To_One.CalendarData.CalendarTaskService;
import uk.ac.cf._5.group14.One_To_One.StrengthLog.Service.WorkoutScheduleService;
import uk.ac.cf._5.group14.One_To_One.StrengthLog.Service.WorkoutSessionService;
import uk.ac.cf._5.group14.One_To_One.StrengthLog.WorkoutSession;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Service
public class DayHealthService {

    private final CalendarTaskService taskService;
    private final WorkoutScheduleService workoutScheduleService;
    private final WorkoutSessionService workoutSessionService;
    private final DayHealthAiService aiService;
    private final BehaviourMemoryService behaviourMemoryService;

    public DayHealthService(CalendarTaskService taskService,
                            WorkoutScheduleService workoutScheduleService,
                            WorkoutSessionService workoutSessionService,
                            DayHealthAiService aiService,
                            BehaviourMemoryService behaviourMemoryService) {
        this.taskService = taskService;
        this.workoutScheduleService = workoutScheduleService;
        this.workoutSessionService = workoutSessionService;
        this.aiService = aiService;
        this.behaviourMemoryService = behaviourMemoryService;
    }

    /**
     * Generates a context-aware Day Health analysis for a specific calendar day.
     * Must include previous days, the current day, and upcoming days.
     */
    public String getDayHealth(User user, LocalDate date) {
        DayHealthAdvice advice = getDayHealthAdvice(user, date);
        if (advice == null || advice.primaryMessage() == null || advice.primaryMessage().isBlank()) {
            return null;
        }
        return advice.primaryMessage().trim();
    }

    /**
     * Provides structured Day Health content for rendering:
     * - short primary message
     * - exactly 2 bullet suggestions
     * - optional watch-out message when tomorrow is heavy
     */
    public DayHealthAdvice getDayHealthAdvice(User user, LocalDate date) {
        if (user == null || user.getId() == null || date == null) {
            return null;
        }

        LocalDate prevStart = date.minusDays(7);
        LocalDate prevEnd = date.minusDays(1);
        LocalDate nextStart = date.plusDays(1);
        LocalDate nextEnd = date.plusDays(7);

        Map<LocalDate, List<CalendarTask>> prevTasks = taskService.getTasksByRange(user, prevStart, prevEnd);
        Map<LocalDate, List<CalendarTask>> todayTasksMap = taskService.getTasksByRange(user, date, date);
        Map<LocalDate, List<CalendarTask>> nextTasks = taskService.getTasksByRange(user, nextStart, nextEnd);

        DaySummary today = buildSummary(user, date, todayTasksMap.getOrDefault(date, List.of()));

        List<DaySummary> previousDays = buildSummaries(user, prevStart, prevEnd, prevTasks);
        List<DaySummary> upcomingDays = buildSummaries(user, nextStart, nextEnd, nextTasks);

        String context = buildContext(previousDays, today, upcomingDays);

        if (behaviourMemoryService != null) {
            var maybeMem = behaviourMemoryService.maybeGetAiContext(user);
            if (maybeMem.isPresent()) {
                context = context + "\n\n" + maybeMem.get();
            }
        }

        String ai = aiService.suggestDayHealth(date, context);
        String primary = (ai != null && !ai.isBlank())
            ? ai.trim()
            : fallbackPrimary(user.getId(), date, today, previousDays, upcomingDays);

        List<String> suggestions = buildSuggestions(user.getId(), date, today, previousDays, upcomingDays);
        String watchOut = buildWatchOut(user.getId(), date, upcomingDays);

        return new DayHealthAdvice(primary, suggestions, watchOut);
    }

    static int computeRotationIndex(long userId, LocalDate date, int poolSize) {
        if (poolSize <= 0) {
            return 0;
        }
        int hash = Objects.hash(userId, date);
        return Math.floorMod(hash, poolSize);
    }

    private List<DaySummary> buildSummaries(User user,
                                           LocalDate start,
                                           LocalDate end,
                                           Map<LocalDate, List<CalendarTask>> tasksByDate) {
        if (start == null || end == null || start.isAfter(end)) {
            return List.of();
        }

        java.util.ArrayList<DaySummary> out = new java.util.ArrayList<>();
        LocalDate d = start;
        while (!d.isAfter(end)) {
            List<CalendarTask> tasks = tasksByDate == null ? List.of() : tasksByDate.getOrDefault(d, List.of());
            out.add(buildSummary(user, d, tasks));
            d = d.plusDays(1);
        }
        return out;
    }

    private DaySummary buildSummary(User user, LocalDate date, List<CalendarTask> tasks) {
        int totalTasks = tasks == null ? 0 : tasks.size();
        int completedTasks = tasks == null ? 0 : (int) tasks.stream().filter(CalendarTask::getCompleted).count();

        int scheduledWorkouts = 0;
        if (date != null) {
            int dow = date.getDayOfWeek().getValue();
            var schedules = workoutScheduleService.findByUserAndDayOfWeek(user, dow);
            scheduledWorkouts = schedules == null ? 0 : schedules.size();
        }

        int completedWorkouts = 0;
        List<WorkoutSession> sessions = workoutSessionService.findByUserAndDate(user, date);
        if (sessions != null) {
            completedWorkouts = (int) sessions.stream().filter(WorkoutSession::isCompleted).count();
        }

        return new DaySummary(date, totalTasks, completedTasks, scheduledWorkouts, completedWorkouts);
    }

    private String buildContext(List<DaySummary> previousDays, DaySummary today, List<DaySummary> upcomingDays) {
        StringBuilder sb = new StringBuilder();

        sb.append("Previous 7 days (most recent):\n");
        if (previousDays == null || previousDays.isEmpty()) {
            sb.append("(none)\n");
        } else {
            for (DaySummary s : previousDays) {
                sb.append(formatSummaryLine(s)).append("\n");
            }
        }

        sb.append("\nCurrent day:\n");
        sb.append(formatSummaryLine(today)).append("\n");

        sb.append("\nNext 7 days planned load:\n");
        if (upcomingDays == null || upcomingDays.isEmpty()) {
            sb.append("(none)\n");
        } else {
            for (DaySummary s : upcomingDays) {
                sb.append(formatSummaryLine(s)).append("\n");
            }
        }

        DaySummary heavy = findHeaviest(upcomingDays);
        if (heavy != null) {
            sb.append("\nHeaviest upcoming day: ").append(labelFor(heavy.date())).append(" (planned items: ")
                    .append(heavy.plannedItems()).append(")\n");
        }

        return sb.toString();
    }

    private static String fallbackPrimary(long userId, LocalDate date, DaySummary today, List<DaySummary> previous, List<DaySummary> upcoming) {
        int idx = computeRotationIndex(userId, date, 3);

        double prevAvg = averageCompletionRate(previous);
        int todayPlanned = today == null ? 0 : today.plannedItems();
        boolean todayHeavy = todayPlanned >= 6;

        DaySummary heavy = findHeaviest(upcoming);
        boolean upcomingHeavy = heavy != null && heavy.plannedItems() >= 6;

        if (upcomingHeavy) {
            List<String> pool = List.of(
                "A heavier day is coming up soon. Keep today simple and protect recovery so you can stay consistent.",
                "Your next week has a heavier spike. Aim for steady effort today so tomorrow doesn't feel like a reset.",
                "There's a heavier day ahead. Keep the plan repeatable and save your biggest push for when it matters."
            );
            return pool.get(idx);
        }

        if (todayHeavy) {
            List<String> pool = List.of(
                "Today is a fuller day. Pick one priority first, then keep the rest short and repeatable.",
                "You've got a lot planned today. Start with the anchor task, then scale volume if energy dips.",
                "It's a packed day. Win early with one completion, then keep momentum with small steps."
            );
            return pool.get(idx);
        }

        if (prevAvg < 0.45) {
            List<String> pool = List.of(
                "The last week looks a bit heavy relative to completions. Today is a good day to simplify and rebuild momentum.",
                "Your recent completion pattern suggests load might be high. Keep today light and focus on consistency.",
                "Recent days look like they've been tough to finish. Aim for a smaller, winnable plan today."
            );
            return pool.get(idx);
        }

        List<String> pool = List.of(
            "Your plan looks manageable. Keep a steady pace and protect recovery to stay consistent.",
            "Today looks doable. Keep it simple: one priority first, then smooth, repeatable effort.",
            "The day looks balanced. Keep momentum with small wins and don't overcomplicate the plan."
        );
        return pool.get(idx);
    }

    private static List<String> buildSuggestions(long userId, LocalDate date, DaySummary today, List<DaySummary> previous, List<DaySummary> upcoming) {
        int idx = computeRotationIndex(userId, date, 3);

        double prevAvg = averageCompletionRate(previous);
        int todayPlanned = today == null ? 0 : today.plannedItems();

        List<String> firstPool;
        if (todayPlanned >= 6) {
            firstPool = List.of(
                "Start with your Daily Focus and get one completion early.",
                "Pick the smallest 'must-do' first and finish it before expanding the plan.",
                "Win the first 20 minutes: one task or one workout block, then reassess."
            );
        } else {
            firstPool = List.of(
                "Pick one priority and complete it early.",
                "Keep your first win small, then build momentum.",
                "Choose a single anchor item and finish it before adding extras."
            );
        }

        List<String> secondPool;
        if (prevAvg < 0.45) {
            secondPool = List.of(
                "If the plan feels heavy, reduce scope and keep sessions short and repeatable.",
                "Aim for consistency over volume: fewer items, done reliably.",
                "Scale down friction: shorten workouts or split tasks into smaller steps."
            );
        } else {
            secondPool = List.of(
                "Protect recovery with short breaks between items.",
                "Keep the pace steady and avoid stacking too many hard items back-to-back.",
                "If energy dips, finish one more small item rather than forcing a big push."
            );
        }

        ArrayList<String> out = new ArrayList<>(2);
        out.add(firstPool.get(idx));
        out.add(secondPool.get(idx));
        return out;
    }

    private static String buildWatchOut(long userId, LocalDate date, List<DaySummary> upcoming) {
        if (date == null || upcoming == null || upcoming.isEmpty()) {
            return null;
        }

        DaySummary tomorrow = upcoming.get(0);
        if (tomorrow == null) {
            return null;
        }

        int planned = tomorrow.plannedItems();
        if (planned < 6) {
            return null;
        }

        int idx = computeRotationIndex(userId, date.plusDays(1), 3);
        String label = labelFor(tomorrow.date());
        List<String> pool = List.of(
            "Tomorrow looks heavy (" + planned + " planned items). Prep one thing tonight and protect your start.",
            "Tomorrow is stacked (" + planned + " planned items). Keep today simple so you don't carry fatigue into it.",
            "Tomorrow has a big load (" + planned + " planned items). Consider moving one item or planning a recovery slot."
        );
        return pool.get(idx);
    }

    private static double averageCompletionRate(List<DaySummary> summaries) {
        if (summaries == null || summaries.isEmpty()) {
            return 0.0;
        }

        double sum = 0.0;
        int count = 0;
        for (DaySummary s : summaries) {
            if (s == null) {
                continue;
            }
            int planned = s.plannedItems();
            if (planned <= 0) {
                continue;
            }
            int completed = Math.max(0, s.completedTasks()) + Math.max(0, s.completedWorkouts());
            sum += (double) completed / (double) planned;
            count++;
        }
        return count == 0 ? 0.0 : (sum / count);
    }

    private static String formatSummaryLine(DaySummary s) {
        if (s == null) return "--";
        return labelFor(s.date()) + ": " +
                "tasks " + s.completedTasks() + "/" + s.totalTasks() + ", " +
                "workouts " + s.completedWorkouts() + "/" + s.scheduledWorkouts() + ", " +
                "planned items " + s.plannedItems();
    }

    private static String labelFor(LocalDate d) {
        if (d == null) return "--";
        String day = d.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.UK);
        return day + " " + d;
    }

    private static DaySummary findHeaviest(List<DaySummary> upcoming) {
        if (upcoming == null || upcoming.isEmpty()) return null;
        return upcoming.stream()
                .max(Comparator.comparingInt(DaySummary::plannedItems))
                .orElse(null);
    }

    public record DayHealthAdvice(String primaryMessage, List<String> suggestions, String watchOut) {
        public DayHealthAdvice {
            if (suggestions == null || suggestions.size() != 2) {
                throw new IllegalArgumentException("DayHealthAdvice.suggestions must contain exactly 2 items");
            }
        }
    }

    record DaySummary(LocalDate date,
                      int totalTasks,
                      int completedTasks,
                      int scheduledWorkouts,
                      int completedWorkouts) {

        int plannedItems() {
            return Math.max(0, totalTasks) + Math.max(0, scheduledWorkouts);
        }
    }
}
