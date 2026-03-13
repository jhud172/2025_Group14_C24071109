package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Dashboard.dto;

public class ClientDashboardActionUpcomingView {

    private final boolean visible;
    private final String typeLabel;
    private final String title;
    private final String helperText;
    private final String href;
    private final String targetIso;
    private final String statusLabel;
    private final boolean countdownEnabled;

    public ClientDashboardActionUpcomingView(boolean visible,
                                             String typeLabel,
                                             String title,
                                             String helperText,
                                             String href,
                                             String targetIso,
                                             String statusLabel,
                                             boolean countdownEnabled) {
        this.visible = visible;
        this.typeLabel = typeLabel;
        this.title = title;
        this.helperText = helperText;
        this.href = href;
        this.targetIso = targetIso;
        this.statusLabel = statusLabel;
        this.countdownEnabled = countdownEnabled;
    }

    public boolean isVisible() {
        return visible;
    }

    public String getTypeLabel() {
        return typeLabel;
    }

    public String getTitle() {
        return title;
    }

    public String getHelperText() {
        return helperText;
    }

    public String getHref() {
        return href;
    }

    public String getTargetIso() {
        return targetIso;
    }

    public String getStatusLabel() {
        return statusLabel;
    }

    public boolean isCountdownEnabled() {
        return countdownEnabled;
    }
}
