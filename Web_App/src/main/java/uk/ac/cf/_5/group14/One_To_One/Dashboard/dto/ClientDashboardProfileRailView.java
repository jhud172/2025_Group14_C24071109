package uk.ac.cf._5.group14.One_To_One.Dashboard.dto;

import java.util.List;

public class ClientDashboardProfileRailView {

    private final ClientDashboardIdentityView identity;
    private final String bio;
    private final int points;
    private final int level;
    private final List<String> milestones;
    private final String focusTitle;
    private final String focusBody;
    private final int workoutStreak;

    public ClientDashboardProfileRailView(ClientDashboardIdentityView identity,
                                          String bio,
                                          int points,
                                          int level,
                                          List<String> milestones,
                                          String focusTitle,
                                          String focusBody,
                                          int workoutStreak) {
        this.identity = identity;
        this.bio = bio;
        this.points = points;
        this.level = level;
        this.milestones = milestones;
        this.focusTitle = focusTitle;
        this.focusBody = focusBody;
        this.workoutStreak = workoutStreak;
    }

    public ClientDashboardIdentityView getIdentity() {
        return identity;
    }

    public String getBio() {
        return bio;
    }

    public int getPoints() {
        return points;
    }

    public int getLevel() {
        return level;
    }

    public List<String> getMilestones() {
        return milestones;
    }

    public String getFocusTitle() {
        return focusTitle;
    }

    public String getFocusBody() {
        return focusBody;
    }

    public int getWorkoutStreak() {
        return workoutStreak;
    }
}
