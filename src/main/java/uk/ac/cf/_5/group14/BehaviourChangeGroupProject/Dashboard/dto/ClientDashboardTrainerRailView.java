package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Dashboard.dto;

import java.util.List;

public class ClientDashboardTrainerRailView {

    private final String state;
    private final String statusLabel;
    private final ClientDashboardIdentityView identity;
    private final String bio;
    private final int points;
    private final int level;
    private final List<String> milestones;
    private final String phaseLabel;
    private final String relationshipLabel;
    private final String summaryLine;
    private final String messageHref;
    private final String planHref;
    private final String manageHref;
    private final String planCtaLabel;
    private final ClientDashboardTrainerContextView context;

    public ClientDashboardTrainerRailView(String state,
                                          String statusLabel,
                                          ClientDashboardIdentityView identity,
                                          String bio,
                                          int points,
                                          int level,
                                          List<String> milestones,
                                          String phaseLabel,
                                          String relationshipLabel,
                                          String summaryLine,
                                          String messageHref,
                                          String planHref,
                                          String manageHref,
                                          String planCtaLabel,
                                          ClientDashboardTrainerContextView context) {
        this.state = state;
        this.statusLabel = statusLabel;
        this.identity = identity;
        this.bio = bio;
        this.points = points;
        this.level = level;
        this.milestones = milestones;
        this.phaseLabel = phaseLabel;
        this.relationshipLabel = relationshipLabel;
        this.summaryLine = summaryLine;
        this.messageHref = messageHref;
        this.planHref = planHref;
        this.manageHref = manageHref;
        this.planCtaLabel = planCtaLabel;
        this.context = context;
    }

    public String getState() {
        return state;
    }

    public String getStatusLabel() {
        return statusLabel;
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

    public String getPhaseLabel() {
        return phaseLabel;
    }

    public String getRelationshipLabel() {
        return relationshipLabel;
    }

    public String getSummaryLine() {
        return summaryLine;
    }

    public String getMessageHref() {
        return messageHref;
    }

    public String getPlanHref() {
        return planHref;
    }

    public String getManageHref() {
        return manageHref;
    }

    public String getPlanCtaLabel() {
        return planCtaLabel;
    }

    public ClientDashboardTrainerContextView getContext() {
        return context;
    }

    public boolean isActive() {
        return "ACTIVE".equalsIgnoreCase(state);
    }

    public boolean isRequested() {
        return "REQUESTED".equalsIgnoreCase(state);
    }

    public boolean isEmpty() {
        return "EMPTY".equalsIgnoreCase(state);
    }
}
