package uk.ac.cf._5.group14.One_To_One.Chat;

/**
 * DTO for the chat page summary shown in the zero-blank state and live metrics panel.
 * Also used as the JSON body for the /chat/context endpoint.
 */
public record ChatSummaryDto(
        String greeting,
        String todayIso,
        int tasksDone,
        int tasksTotal,
        int workoutsDone,
        int workoutsTotal,
        int completionPct,
        String nextWorkoutName,
        String nextWorkoutDate,
        int streakDays,
        // 7-day rolling window
        int sevenDayTasksCompleted,
        int sevenDayTasksTotal,
        int sevenDayWorkoutsCompleted,
        int sevenDayWorkoutsTotal,
        int sevenDayMissedSessions,
        // 30-day rolling window
        int thirtyDayTasksCompleted,
        int thirtyDayTasksTotal,
        int thirtyDayWorkoutsCompleted,
        int thirtyDayWorkoutsTotal,
        int thirtyDayMissedSessions,
        String trendNote
) {
    /** Backwards-compatible factory without multi-day fields (all zeroed). */
    public static ChatSummaryDto of(
            String greeting, String todayIso,
            int tasksDone, int tasksTotal,
            int workoutsDone, int workoutsTotal,
            int completionPct,
            String nextWorkoutName, String nextWorkoutDate,
            int streakDays
    ) {
        return new ChatSummaryDto(greeting, todayIso, tasksDone, tasksTotal,
                workoutsDone, workoutsTotal, completionPct,
                nextWorkoutName, nextWorkoutDate, streakDays,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, null);
    }
}
