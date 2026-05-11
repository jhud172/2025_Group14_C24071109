package uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData;

import java.time.LocalDate;

public record DailyStreakDaySummary(
        LocalDate date,
        DailyCompletionStatus status,
        int completionPercentage,
        int completedTasks,
        int totalTasks,
        int completedWorkouts,
        int totalWorkouts,
        int logsNeeded
) {
}
