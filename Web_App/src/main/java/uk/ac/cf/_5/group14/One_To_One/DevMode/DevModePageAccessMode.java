package uk.ac.cf._5.group14.One_To_One.DevMode;

public enum DevModePageAccessMode {
    ENABLED,
    DISABLED,
    RESTRICTED;

    public boolean blocksAccess() {
        return this != ENABLED;
    }
}
