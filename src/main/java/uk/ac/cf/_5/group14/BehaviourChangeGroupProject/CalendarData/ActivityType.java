package uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData;

public enum ActivityType {
    GYM("Gym", "🏋️"),
    SWIM("Swim", "🏊"),
    RUN("Run", "🏃"),
    BIKE("Bike", "🚴"),
    CUSTOM("Custom", "⚡");

    private final String label;
    private final String icon;

    ActivityType(String label, String icon) {
        this.label = label;
        this.icon = icon;
    }

    public String getLabel() {
        return label;
    }

    public String getIcon() {
        return icon;
    }
}
