package uk.ac.cf._5.group14.One_To_One.Chat;

import java.time.LocalDate;
import java.util.List;

public record ChatContext(
        LocalDate today,
        LocalDate requestedDate,
        List<String> todaysTasks,
        List<String> todaysScheduledItems,
        List<String> requestedDateItems,
        List<RecentWorkout> recentWorkouts,
        List<String> recentNotes,
        Integer points,
        Integer level,
        MultiDayInsights multiDayInsights
) {
    /** Backwards-compatible constructor without multiDayInsights. */
    public ChatContext(
            LocalDate today,
            LocalDate requestedDate,
            List<String> todaysTasks,
            List<String> todaysScheduledItems,
            List<String> requestedDateItems,
            List<RecentWorkout> recentWorkouts,
            List<String> recentNotes,
            Integer points,
            Integer level
    ) {
        this(today, requestedDate, todaysTasks, todaysScheduledItems,
             requestedDateItems, recentWorkouts, recentNotes, points, level, null);
    }

    public record RecentWorkout(
            LocalDate date,
            String name,
            boolean completed,
            int totalSets
    ) {}

    /**
     * Aggregated statistics for a rolling period (7 or 30 days).
     */
    public record MultiDayInsights(
            int periodDays,
            int tasksCompleted,
            int tasksTotal,
            int workoutsCompleted,
            int workoutsTotal,
            int missedSessions,
            String trendNote
    ) {}
}
