package uk.ac.cf._5.group14.One_To_One.ScheduleData;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a pre-built schedule template that users can apply
 */
@Getter
@Setter
public class ScheduleTemplate {
    private String id;
    private String name;
    private String description;
    private ScheduleType scheduleType;
    private int dayCount;
    private Map<Integer, List<String>> dayWorkouts; // day -> list of workout names/descriptions
    
    public ScheduleTemplate(String id, String name, String description, ScheduleType scheduleType, int dayCount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.scheduleType = scheduleType;
        this.dayCount = dayCount;
        this.dayWorkouts = new HashMap<>();
    }
    
    /**
     * Add a workout to a specific day
     * @param day Day index (1-based)
     * @param workoutName Name/description of the workout
     */
    public void addWorkout(int day, String workoutName) {
        dayWorkouts.computeIfAbsent(day, k -> new ArrayList<>()).add(workoutName);
    }
    
    /**
     * Get day label for this template
     */
    public String getDayLabel(int day) {
        if (scheduleType == ScheduleType.WEEKLY) {
            String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
            return day >= 1 && day <= 7 ? days[day - 1] : "Day " + day;
        } else {
            return "Day " + day;
        }
    }
}
