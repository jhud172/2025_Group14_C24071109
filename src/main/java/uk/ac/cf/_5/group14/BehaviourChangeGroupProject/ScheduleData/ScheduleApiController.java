package uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleApiController {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private ScheduleEntryService scheduleEntryService;

    @Autowired
    private ScheduleRepository scheduleRepository;

    /**
     * Get schedule metadata (frequency, active days, etc.)
     */
    @GetMapping("/{id}/metadata")
    public ResponseEntity<Map<String, Object>> getScheduleMetadata(
            @PathVariable Long id,
            @SessionAttribute("user") User user) {
        
        Schedule schedule = scheduleService.findById(id);
        if (schedule == null) {
            return ResponseEntity.notFound().build();
        }

        // Check access rights
        if (!canAccessSchedule(user, schedule)) {
            return ResponseEntity.status(403).build();
        }

        List<ScheduleEntry> entries = scheduleEntryService.getEntriesBySchedule(schedule);
        
        // Calculate metadata
        long uniqueDays = entries.stream()
                .map(ScheduleEntry::getDayOfWeek)
                .distinct()
                .count();
        
        int totalExercises = entries.size();
        
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("sessionsPerWeek", uniqueDays);
        metadata.put("activeDays", uniqueDays);
        metadata.put("totalExercises", totalExercises);
        metadata.put("restDays", 7 - uniqueDays);
        
        return ResponseEntity.ok(metadata);
    }

    /**
     * Get schedule preview with weekly structure
     */
    @GetMapping("/{id}/preview")
    public ResponseEntity<Map<String, Object>> getSchedulePreview(
            @PathVariable Long id,
            @SessionAttribute("user") User user) {
        
        Schedule schedule = scheduleService.findById(id);
        if (schedule == null) {
            return ResponseEntity.notFound().build();
        }

        // Check access rights
        if (!canAccessSchedule(user, schedule)) {
            return ResponseEntity.status(403).build();
        }

        List<ScheduleEntry> entries = scheduleEntryService.getEntriesBySchedule(schedule);
        
        Map<String, Object> preview = new HashMap<>();
        preview.put("id", schedule.getId());
        preview.put("name", schedule.getName());
        preview.put("description", schedule.getDescription());
        
        // Convert entries to a simple format for the preview
        List<Map<String, Object>> entryMaps = entries.stream()
                .map(entry -> {
                    Map<String, Object> entryMap = new HashMap<>();
                    entryMap.put("dayOfWeek", entry.getDayOfWeek());
                    entryMap.put("orderNumber", entry.getOrderNumber());
                    
                    if (entry.getExercise() != null) {
                        Map<String, String> exercise = new HashMap<>();
                        exercise.put("name", entry.getExercise().getName());
                        entryMap.put("exercise", exercise);
                    }
                    
                    if (entry.getCustomExercise() != null) {
                        Map<String, String> customExercise = new HashMap<>();
                        customExercise.put("name", entry.getCustomExercise().getName());
                        entryMap.put("customExercise", customExercise);
                    }
                    
                    return entryMap;
                })
                .collect(Collectors.toList());
        
        preview.put("entries", entryMaps);
        
        return ResponseEntity.ok(preview);
    }

    /**
     * Duplicate a schedule
     */
    @PostMapping("/{id}/duplicate")
    public ResponseEntity<Map<String, Object>> duplicateSchedule(
            @PathVariable Long id,
            @SessionAttribute("user") User user) {
        
        Schedule original = scheduleService.findById(id);
        if (original == null) {
            return ResponseEntity.notFound().build();
        }

        // Check access rights - can only duplicate own schedules
        if (!original.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        // Create duplicate
        Schedule duplicate = new Schedule();
        duplicate.setName(original.getName() + " (Copy)");
        duplicate.setDescription(original.getDescription());
        duplicate.setUser(user);
        
        scheduleService.save(duplicate);

        // Copy entries
        List<ScheduleEntry> originalEntries = scheduleEntryService.getEntriesBySchedule(original);
        for (ScheduleEntry originalEntry : originalEntries) {
            ScheduleEntry newEntry = new ScheduleEntry();
            newEntry.setSchedule(duplicate);
            newEntry.setExercise(originalEntry.getExercise());
            newEntry.setCustomExercise(originalEntry.getCustomExercise());
            newEntry.setDayOfWeek(originalEntry.getDayOfWeek());
            newEntry.setOrderNumber(originalEntry.getOrderNumber());
            scheduleEntryService.save(newEntry);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("id", duplicate.getId());
        response.put("name", duplicate.getName());
        response.put("success", true);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Check if user can access a schedule
     */
    private boolean canAccessSchedule(User user, Schedule schedule) {
        // Can access if it's their own schedule
        if (schedule.getUser().getId().equals(user.getId())) {
            return true;
        }
        
        // Can access if it's a trainer-shared schedule
        // For now, we'll allow access if the schedule exists
        // In a real implementation, check trainer-client relationship
        return true;
    }
}
