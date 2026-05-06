package uk.ac.cf._5.group14.One_To_One.CalendarData;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import uk.ac.cf._5.group14.One_To_One.ScheduleData.ScheduleOccurrence;

/**
 * Service for building calendar day models.
 * This centralizes the logic for constructing day cells, eliminating map key mismatches
 * and ensuring consistent data structures throughout the calendar views.
 */
@Service
public class CalendarDayModelBuilder {
    
    /**
     * Builds a list of CalendarCellModel for a month view with placeholders.
     * The returned list includes leading and trailing placeholder cells to ensure
     * proper grid alignment. The grid will always have 35 or 42 cells (5 or 6 rows).
     * 
     * @param year The year
     * @param month The month (1-12)
     * @param today Today's date for status computation
     * @param tasksByDate Map of tasks keyed by LocalDate
     * @param occurrencesByDate Map of occurrences keyed by LocalDate
     * @return List of CalendarCellModel including placeholders and real days
     */
    public List<CalendarCellModel> buildMonthCells(
            int year, 
            int month,
            LocalDate today,
            Map<LocalDate, List<CalendarTask>> tasksByDate,
            Map<LocalDate, List<ScheduleOccurrence>> occurrencesByDate) {
        
        LocalDate firstDay = LocalDate.of(year, month, 1);
        int lengthOfMonth = firstDay.lengthOfMonth();
        
        // Calculate leading placeholders (1=Monday, 7=Sunday)
        // We need (dayOfWeek - 1) placeholders before the 1st
        int leadingPlaceholders = firstDay.getDayOfWeek().getValue() - 1;
        
        // Calculate total cells needed
        int totalDays = leadingPlaceholders + lengthOfMonth;
        int totalCells = totalDays <= 35 ? 35 : 42;
        int trailingPlaceholders = totalCells - totalDays;
        
        List<CalendarCellModel> cells = new ArrayList<>(totalCells);
        
        // Add leading placeholders
        for (int i = 0; i < leadingPlaceholders; i++) {
            cells.add(new CalendarCellModel());
        }
        
        // Add real day cells
        for (int dayOfMonth = 1; dayOfMonth <= lengthOfMonth; dayOfMonth++) {
            LocalDate date = firstDay.withDayOfMonth(dayOfMonth);
            List<CalendarTask> tasks = tasksByDate != null ? tasksByDate.get(date) : null;
            List<ScheduleOccurrence> occurrences = occurrencesByDate != null ? occurrencesByDate.get(date) : null;
            
            CalendarDayModel dayModel = new CalendarDayModel(date, today, tasks, occurrences);
            cells.add(new CalendarCellModel(dayModel));
        }
        
        // Add trailing placeholders
        for (int i = 0; i < trailingPlaceholders; i++) {
            cells.add(new CalendarCellModel());
        }
        
        // Assert postcondition: grid size is exactly 35 or 42
        assert cells.size() == 35 || cells.size() == 42 : 
            "Month grid must have exactly 35 or 42 cells, got " + cells.size();
        
        return cells;
    }
    
    /**
     * @deprecated Use buildMonthCells() instead for proper grid alignment with placeholders.
     * This method is kept for backward compatibility but will be removed in a future version.
     */
    @Deprecated
    public List<CalendarDayModel> buildMonthDays(
            int year, 
            int month,
            LocalDate today,
            Map<LocalDate, List<CalendarTask>> tasksByDate,
            Map<LocalDate, List<ScheduleOccurrence>> occurrencesByDate) {
        
        LocalDate firstDay = LocalDate.of(year, month, 1);
        int lengthOfMonth = firstDay.lengthOfMonth();
        
        List<CalendarDayModel> days = new ArrayList<>(lengthOfMonth);
        
        for (int dayOfMonth = 1; dayOfMonth <= lengthOfMonth; dayOfMonth++) {
            LocalDate date = firstDay.withDayOfMonth(dayOfMonth);
            List<CalendarTask> tasks = tasksByDate != null ? tasksByDate.get(date) : null;
            List<ScheduleOccurrence> occurrences = occurrencesByDate != null ? occurrencesByDate.get(date) : null;
            
            days.add(new CalendarDayModel(date, today, tasks, occurrences));
        }
        
        return days;
    }
    
    /**
     * Builds a list of CalendarDayModel for a week view.
     * @param weekStart The start date of the week (Monday)
     * @param today Today's date for status computation
     * @param tasksByDate Map of tasks keyed by LocalDate
     * @param occurrencesByDate Map of occurrences keyed by LocalDate
     * @return List of CalendarDayModel for each day in the week (7 days)
     */
    public List<CalendarDayModel> buildWeekDays(
            LocalDate weekStart,
            LocalDate today,
            Map<LocalDate, List<CalendarTask>> tasksByDate,
            Map<LocalDate, List<ScheduleOccurrence>> occurrencesByDate) {
        
        List<CalendarDayModel> days = new ArrayList<>(7);
        
        for (int i = 0; i < 7; i++) {
            LocalDate date = weekStart.plusDays(i);
            List<CalendarTask> tasks = tasksByDate != null ? tasksByDate.get(date) : null;
            List<ScheduleOccurrence> occurrences = occurrencesByDate != null ? occurrencesByDate.get(date) : null;
            
            days.add(new CalendarDayModel(date, today, tasks, occurrences));
        }
        
        return days;
    }
    
    /**
     * Converts a list of CalendarDayModel to a map keyed by ISO date string.
     * This is provided for backward compatibility if needed, but the list should be
     * used directly in templates to avoid key-mismatch issues.
     * 
     * @param days List of calendar day models
     * @return Map keyed by ISO date string (yyyy-MM-dd)
     */
    public Map<String, CalendarDayModel> toIsoDateMap(List<CalendarDayModel> days) {
        Map<String, CalendarDayModel> map = new HashMap<>();
        if (days != null) {
            for (CalendarDayModel day : days) {
                map.put(day.getIsoDate(), day);
            }
        }
        return map;
    }
}
