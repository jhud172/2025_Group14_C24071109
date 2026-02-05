package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Goals;

import java.time.LocalDate;

public class GoalAdherenceWeek {

    private final LocalDate weekStart;
    private final int plannedCount;
    private final int completedCount;
    private final int percent;
    private final boolean streakContribution;

    public GoalAdherenceWeek(LocalDate weekStart, int plannedCount, int completedCount, int percent, boolean streakContribution) {
        this.weekStart = weekStart;
        this.plannedCount = plannedCount;
        this.completedCount = completedCount;
        this.percent = percent;
        this.streakContribution = streakContribution;
    }

    public LocalDate getWeekStart() {
        return weekStart;
    }

    public int getPlannedCount() {
        return plannedCount;
    }

    public int getCompletedCount() {
        return completedCount;
    }

    public int getPercent() {
        return percent;
    }

    public boolean isStreakContribution() {
        return streakContribution;
    }
}
