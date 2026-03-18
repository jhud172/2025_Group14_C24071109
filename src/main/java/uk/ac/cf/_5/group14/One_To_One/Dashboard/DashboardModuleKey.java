package uk.ac.cf._5.group14.One_To_One.Dashboard;

import java.util.Arrays;
import java.util.Optional;

public enum DashboardModuleKey {
    QUICK_ACTIONS("quick_actions"),
    HEALTH_SUMMARY("health_summary"),
    PHYSICAL_CONDITIONS("physical_conditions"),
    EXERCISE_PREFERENCES("exercise_preferences"),
    RECENT_EXERCISE_LOGS("recent_exercise_logs"),
    HEALTH_RECORD_CHARTS("health_record_charts"),
    EXERCISE_LOG_CHARTS("exercise_log_charts");

    private final String key;

    DashboardModuleKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static Optional<DashboardModuleKey> fromKey(String key) {
        if (key == null) {
            return Optional.empty();
        }
        return Arrays.stream(values())
                .filter(v -> v.key.equals(key))
                .findFirst();
    }
}
