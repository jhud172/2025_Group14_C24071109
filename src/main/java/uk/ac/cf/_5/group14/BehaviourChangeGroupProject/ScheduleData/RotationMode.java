package uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData;

/**
 * Defines how a custom schedule rotates across calendar weeks
 */
public enum RotationMode {
    /**
     * Schedule repeats weekly (default for weekly schedules)
     */
    WEEKLY_REPEAT,
    
    /**
     * Schedule rotates continuously across weeks (e.g., 5-day split continues across calendar weeks)
     */
    CONTINUOUS_ROTATION,
    
    /**
     * No rotation - schedule applies once
     */
    NONE
}
