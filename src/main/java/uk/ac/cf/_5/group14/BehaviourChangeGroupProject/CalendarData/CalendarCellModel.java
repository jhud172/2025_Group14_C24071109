package uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData;

/**
 * Wrapper for a calendar grid cell, which may contain either a real day or be a placeholder.
 * This model eliminates template-based padding calculations and ensures the grid structure
 * is defined entirely in Java code, preventing misalignment issues.
 */
public class CalendarCellModel {
    private final boolean placeholder;
    private final CalendarDayModel dayModel;
    
    /**
     * Creates a placeholder cell (empty cell for grid alignment).
     */
    public CalendarCellModel() {
        this.placeholder = true;
        this.dayModel = null;
    }
    
    /**
     * Creates a cell containing a real day.
     * @param dayModel The day model for this cell
     */
    public CalendarCellModel(CalendarDayModel dayModel) {
        if (dayModel == null) {
            throw new IllegalArgumentException("dayModel cannot be null for non-placeholder cells");
        }
        this.placeholder = false;
        this.dayModel = dayModel;
    }
    
    /**
     * @return true if this cell is a placeholder (no day data)
     */
    public boolean isPlaceholder() {
        return placeholder;
    }
    
    /**
     * @return true if this cell contains a real day
     */
    public boolean isRealDay() {
        return !placeholder;
    }
    
    /**
     * @return The day model, or null if this is a placeholder
     */
    public CalendarDayModel getDayModel() {
        return dayModel;
    }
}
