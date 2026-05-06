package uk.ac.cf._5.group14.One_To_One.HomePage;

import java.time.LocalDate;
import java.util.List;

public record MiniWeekDay(
        LocalDate date,
        String dayLabel,
        String prettyDate,
        int taskCount,
        int scheduledCount,
        boolean today,
        boolean tomorrow,
        String dayStatus,
        boolean hasUncompletedWorkout,
        List<String> taskTitles,
        List<String> workoutTitles
) {
}
