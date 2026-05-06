package uk.ac.cf._5.group14.One_To_One.QuickActions;

import java.util.List;

public enum QuickActionBuiltIn {
    START_WORKOUT("Start workout", "START_WORKOUT", true),
    LOG_NUTRITION("Log nutrition", "LOG_NUTRITION", true),
    OPEN_CALENDAR("Open calendar", "OPEN_CALENDAR", true),
    PROGRESS_CHECK("Progress check", "PROGRESS_CHECK", true);

    private final String label;
    private final String actionKey;
    private final boolean defaultActive;

    QuickActionBuiltIn(String label, String actionKey, boolean defaultActive) {
        this.label = label;
        this.actionKey = actionKey;
        this.defaultActive = defaultActive;
    }

    public String label() {
        return label;
    }

    public String actionKey() {
        return actionKey;
    }

    public boolean defaultActive() {
        return defaultActive;
    }

    public static List<QuickActionBuiltIn> defaults() {
        return List.of(values());
    }
}
