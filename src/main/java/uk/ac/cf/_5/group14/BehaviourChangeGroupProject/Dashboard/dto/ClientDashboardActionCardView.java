package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Dashboard.dto;

import java.util.List;

public class ClientDashboardActionCardView {

    private final String key;
    private final String eyebrow;
    private final String title;
    private final String description;
    private final String href;
    private final String ctaLabel;
    private final String tone;
    private final String icon;
    private final String badgeLabel;
    private final List<String> stats;
    private final String supportTitle;
    private final String supportText;
    private final int priority;
    private final String highlightValue;
    private final String highlightLabel;
    private final String targetIso;
    private final boolean timed;
    private final boolean missed;
    private final String stateLabel;

    public ClientDashboardActionCardView(String key,
                                         String eyebrow,
                                         String title,
                                         String description,
                                         String href,
                                         String ctaLabel,
                                         String tone,
                                         String icon,
                                         String badgeLabel,
                                         List<String> stats,
                                         String supportTitle,
                                         String supportText,
                                         int priority,
                                         String highlightValue,
                                         String highlightLabel,
                                         String targetIso,
                                         boolean timed,
                                         boolean missed,
                                         String stateLabel) {
        this.key = key;
        this.eyebrow = eyebrow;
        this.title = title;
        this.description = description;
        this.href = href;
        this.ctaLabel = ctaLabel;
        this.tone = tone;
        this.icon = icon;
        this.badgeLabel = badgeLabel;
        this.stats = stats;
        this.supportTitle = supportTitle;
        this.supportText = supportText;
        this.priority = priority;
        this.highlightValue = highlightValue;
        this.highlightLabel = highlightLabel;
        this.targetIso = targetIso;
        this.timed = timed;
        this.missed = missed;
        this.stateLabel = stateLabel;
    }

    public String getKey() {
        return key;
    }

    public String getEyebrow() {
        return eyebrow;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getHref() {
        return href;
    }

    public String getCtaLabel() {
        return ctaLabel;
    }

    public String getTone() {
        return tone;
    }

    public String getIcon() {
        return icon;
    }

    public String getBadgeLabel() {
        return badgeLabel;
    }

    public List<String> getStats() {
        return stats;
    }

    public String getSupportTitle() {
        return supportTitle;
    }

    public String getSupportText() {
        return supportText;
    }

    public int getPriority() {
        return priority;
    }

    public String getHighlightValue() {
        return highlightValue;
    }

    public String getHighlightLabel() {
        return highlightLabel;
    }

    public String getTargetIso() {
        return targetIso;
    }

    public boolean isTimed() {
        return timed;
    }

    public boolean isMissed() {
        return missed;
    }

    public String getStateLabel() {
        return stateLabel;
    }
}
