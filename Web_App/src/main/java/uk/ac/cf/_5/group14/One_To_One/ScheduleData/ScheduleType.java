package uk.ac.cf._5.group14.One_To_One.ScheduleData;

/**
 * Defines the type of training schedule structure
 */
public enum ScheduleType {
    /**
     * Traditional weekly schedule (Monday-Sunday)
     */
    WEEKLY,
    
    /**
     * Single day schedule optimized for simple routine assignment
     */
    DAILY,
    
    /**
     * Custom schedule with user-defined number of days (e.g., 5-day split)
     */
    CUSTOM
}
