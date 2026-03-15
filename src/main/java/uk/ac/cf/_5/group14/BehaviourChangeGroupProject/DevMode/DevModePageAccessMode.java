package uk.ac.cf._5.group14.BehaviourChangeGroupProject.DevMode;

public enum DevModePageAccessMode {
    ENABLED,
    DISABLED,
    RESTRICTED;

    public boolean blocksAccess() {
        return this != ENABLED;
    }
}
