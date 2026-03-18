package uk.ac.cf._5.group14.One_To_One.TrainerAssignments;

public class TrainerAdherenceStats {
    private final int assignedWorkouts;
    private final int completedWorkouts;
    private final double adherenceRate;

    public TrainerAdherenceStats(int assignedWorkouts, int completedWorkouts, double adherenceRate) {
        this.assignedWorkouts = assignedWorkouts;
        this.completedWorkouts = completedWorkouts;
        this.adherenceRate = adherenceRate;
    }

    public int getAssignedWorkouts() {
        return assignedWorkouts;
    }

    public int getCompletedWorkouts() {
        return completedWorkouts;
    }

    public double getAdherenceRate() {
        return adherenceRate;
    }
}
