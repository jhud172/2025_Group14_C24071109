package uk.ac.cf._5.group14.One_To_One.ScheduleData;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for managing pre-built schedule templates
 */
@Service
public class ScheduleTemplateService {
    
    private final List<ScheduleTemplate> templates;
    
    public ScheduleTemplateService() {
        this.templates = new ArrayList<>();
        initializeTemplates();
    }
    
    /**
     * Get all available templates
     */
    public List<ScheduleTemplate> getAllTemplates() {
        return new ArrayList<>(templates);
    }
    
    /**
     * Get a specific template by ID
     */
    public ScheduleTemplate getTemplateById(String id) {
        return templates.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Initialize built-in templates
     */
    private void initializeTemplates() {
        // Push/Pull/Legs (6-day)
        ScheduleTemplate ppl = new ScheduleTemplate(
                "ppl-6day",
                "Push/Pull/Legs",
                "Classic 6-day bodybuilding split focusing on movement patterns",
                ScheduleType.WEEKLY,
                7
        );
        ppl.addWorkout(1, "Push (Chest, Shoulders, Triceps)");
        ppl.addWorkout(2, "Pull (Back, Biceps)");
        ppl.addWorkout(3, "Legs (Quads, Hamstrings, Calves)");
        ppl.addWorkout(4, "Push (Chest, Shoulders, Triceps)");
        ppl.addWorkout(5, "Pull (Back, Biceps)");
        ppl.addWorkout(6, "Legs (Quads, Hamstrings, Calves)");
        // Day 7 is rest
        templates.add(ppl);
        
        // Upper/Lower 4-day
        ScheduleTemplate upperLower = new ScheduleTemplate(
                "upper-lower-4day",
                "Upper/Lower Split",
                "4-day split alternating upper and lower body",
                ScheduleType.WEEKLY,
                7
        );
        upperLower.addWorkout(1, "Upper Body (Push Focus)");
        upperLower.addWorkout(2, "Lower Body (Squat Focus)");
        upperLower.addWorkout(4, "Upper Body (Pull Focus)");
        upperLower.addWorkout(5, "Lower Body (Deadlift Focus)");
        templates.add(upperLower);
        
        // Full Body 3-day
        ScheduleTemplate fullBody = new ScheduleTemplate(
                "full-body-3day",
                "Full Body 3x/Week",
                "Beginner-friendly full body routine",
                ScheduleType.WEEKLY,
                7
        );
        fullBody.addWorkout(1, "Full Body A");
        fullBody.addWorkout(3, "Full Body B");
        fullBody.addWorkout(5, "Full Body C");
        templates.add(fullBody);
        
        // Bodybuilding Bro Split
        ScheduleTemplate broSplit = new ScheduleTemplate(
                "bro-split-5day",
                "Bodybuilding Split",
                "Traditional 5-day bodybuilding split by muscle group",
                ScheduleType.WEEKLY,
                7
        );
        broSplit.addWorkout(1, "Chest Day");
        broSplit.addWorkout(2, "Back Day");
        broSplit.addWorkout(3, "Shoulder Day");
        broSplit.addWorkout(4, "Leg Day");
        broSplit.addWorkout(5, "Arm Day");
        templates.add(broSplit);
        
        // Strength 3-Day
        ScheduleTemplate strength = new ScheduleTemplate(
                "strength-3day",
                "Strength Training",
                "3-day strength-focused program",
                ScheduleType.WEEKLY,
                7
        );
        strength.addWorkout(1, "Squat & Press");
        strength.addWorkout(3, "Deadlift & Bench");
        strength.addWorkout(5, "Volume Day");
        templates.add(strength);
        
        // Daily Routine
        ScheduleTemplate daily = new ScheduleTemplate(
                "daily-simple",
                "Daily Routine",
                "Simple daily workout routine",
                ScheduleType.DAILY,
                1
        );
        daily.addWorkout(1, "Daily Workout");
        templates.add(daily);
    }
}
