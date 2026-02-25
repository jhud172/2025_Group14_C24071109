package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Chat;

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
        int streakDays
) {}
