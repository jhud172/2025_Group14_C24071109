package uk.ac.cf._5.group14.One_To_One.CalendarData;

import java.time.LocalDate;

public final class DailyCompletionCalculator {

    private DailyCompletionCalculator() {
    }

    /**
     * Computes the completion status based on the raw completion counts.
     *
     * RED (skipped) is added in a later checklist item.
     */
    public static DailyCompletionStatus computeStatus(LocalDate date, int completedItems, int totalItems, LocalDate today) {
        if (totalItems <= 0) {
            return DailyCompletionStatus.GREY;
        }
        if (completedItems <= 0 && date != null && today != null && date.isBefore(today)) {
            return DailyCompletionStatus.RED;
        }
        if (completedItems >= totalItems) {
            return DailyCompletionStatus.GREEN;
        }
        if (completedItems > 0 && completedItems < totalItems) {
            // Distinguish today (in-progress = BLUE) from past partial (ORANGE)
            if (date != null && today != null && date.equals(today)) {
                return DailyCompletionStatus.BLUE;
            }
            return DailyCompletionStatus.ORANGE;
        }
        return DailyCompletionStatus.GREY;
    }

    public static int computeCompletionPercentage(
            int completedTasks,
            int totalTasks,
            int completedWorkouts,
            int totalWorkouts
    ) {
        int totalItems = Math.max(0, totalTasks) + Math.max(0, totalWorkouts);
        if (totalItems == 0) {
            return 0;
        }
        int completedItems = Math.max(0, completedTasks) + Math.max(0, completedWorkouts);
        if (completedItems <= 0) {
            return 0;
        }

        int clampedCompleted = Math.min(completedItems, totalItems);
        return (clampedCompleted * 100) / totalItems;
    }
}
