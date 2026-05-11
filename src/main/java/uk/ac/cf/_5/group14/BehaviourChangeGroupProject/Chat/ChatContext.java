package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Chat;

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
        Integer level
) {
    public record RecentWorkout(
            LocalDate date,
            String name,
            boolean completed,
            int totalSets
    ) {}
}
