package uk.ac.cf._5.group14.One_To_One.CalendarData;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import uk.ac.cf._5.group14.One_To_One.ScheduleData.ScheduleOccurrence;

/**
 * DTO representing a single day cell in the calendar view.
 * This model eliminates map key mismatches by providing a unified structure for each day.
 * All data for a day is pre-computed and accessible through simple getters,
 * removing the need for complex Thymeleaf map lookups.
 */
public class CalendarDayModel {
    private final LocalDate localDate;
    private final String isoDate;
    private final int dayOfMonth;
    private final List<CalendarTask> tasks;
    private final List<ScheduleOccurrence> occurrences;
    private final DayStatus status;
    private final String statusValue;
    private final boolean today;
    private final boolean tomorrow;
    private final boolean past;
    
    public CalendarDayModel(LocalDate localDate, LocalDate today, 
                           List<CalendarTask> tasks, 
                           List<ScheduleOccurrence> occurrences) {
        this.localDate = localDate;
        this.isoDate = localDate.toString();
        this.dayOfMonth = localDate.getDayOfMonth();
        this.tasks = tasks != null ? new ArrayList<>(tasks) : new ArrayList<>();
        this.occurrences = occurrences != null ? new ArrayList<>(occurrences) : new ArrayList<>();
        this.status = computeStatus(localDate, today, this.tasks, this.occurrences);
        this.statusValue = status.getValue();
        this.today = status == DayStatus.TODAY;
        this.tomorrow = status == DayStatus.TOMORROW;
        this.past = status == DayStatus.PAST_ZERO || status == DayStatus.PAST_INCOMPLETE;
    }
    
    private DayStatus computeStatus(LocalDate date, LocalDate today, 
                                    List<CalendarTask> tasks, 
                                    List<ScheduleOccurrence> occurrences) {
        if (date.isEqual(today)) {
            return DayStatus.TODAY;
        }
        if (date.isEqual(today.plusDays(1))) {
            return DayStatus.TOMORROW;
        }
        if (date.isBefore(today)) {
            int totalItems = tasks.size() + occurrences.size();
            return totalItems == 0 ? DayStatus.PAST_ZERO : DayStatus.PAST_INCOMPLETE;
        }
        return DayStatus.FUTURE;
    }
    
    public LocalDate getLocalDate() {
        return localDate;
    }
    
    public String getIsoDate() {
        return isoDate;
    }
    
    public int getDayOfMonth() {
        return dayOfMonth;
    }
    
    public List<CalendarTask> getTasks() {
        return tasks;
    }
    
    public List<ScheduleOccurrence> getOccurrences() {
        return occurrences;
    }
    
    public int getTaskCount() {
        return tasks.size();
    }
    
    public int getOccurrenceCount() {
        return occurrences.size();
    }
    
    public int getTotalItemCount() {
        return tasks.size() + occurrences.size();
    }
    
    public boolean isEmpty() {
        return tasks.isEmpty() && occurrences.isEmpty();
    }
    
    public DayStatus getStatus() {
        return status;
    }
    
    public String getStatusValue() {
        return statusValue;
    }
    
    public boolean isToday() {
        return today;
    }
    
    public boolean isTomorrow() {
        return tomorrow;
    }
    
    public boolean isPast() {
        return past;
    }
    
    /**
     * @return true if this is today (pre-computed property for templates)
     */
    public boolean getToday() {
        return today;
    }
    
    /**
     * @return true if this is tomorrow (pre-computed property for templates)
     */
    public boolean getTomorrow() {
        return tomorrow;
    }
    
    /**
     * @return true if this date is in the past (pre-computed property for templates)
     */
    public boolean getPast() {
        return past;
    }
    
    /**
     * Enum representing the status of a day relative to today.
     */
    public enum DayStatus {
        TODAY("today"),
        TOMORROW("tomorrow"),
        PAST_ZERO("past-zero"),
        PAST_INCOMPLETE("past-incomplete"),
        FUTURE("future");
        
        private final String value;
        
        DayStatus(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
    }
}
