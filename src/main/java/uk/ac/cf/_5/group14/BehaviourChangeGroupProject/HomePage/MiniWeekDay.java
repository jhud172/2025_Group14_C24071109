package uk.ac.cf._5.group14.BehaviourChangeGroupProject.HomePage;

import java.time.LocalDate;

public record MiniWeekDay(
        LocalDate date,
        String dayLabel,
        String prettyDate,
        int taskCount,
        int scheduledCount,
        boolean today,
        boolean hasUncompletedWorkout
) {
}
