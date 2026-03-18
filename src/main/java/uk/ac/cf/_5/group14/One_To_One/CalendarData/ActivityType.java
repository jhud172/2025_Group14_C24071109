package uk.ac.cf._5.group14.One_To_One.CalendarData;

public enum ActivityType {
    GYM("Gym", "ðŸ‹ï¸"),
    SWIM("Swim", "ðŸŠ"),
    RUN("Run", "ðŸƒ"),
    BIKE("Bike", "ðŸš´"),
    CUSTOM("Custom", "âš¡");

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
