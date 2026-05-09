package uk.ac.cf._5.group14.One_To_One.ScheduleData;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import uk.ac.cf._5.group14.One_To_One.CalendarData.CalendarTask;
import uk.ac.cf._5.group14.One_To_One.CalendarData.CalendarTaskRepository;
import uk.ac.cf._5.group14.One_To_One.CustomExerciseData.CustomExerciseRepository;
import uk.ac.cf._5.group14.One_To_One.ExerciseData.ExerciseRepository;
import uk.ac.cf._5.group14.One_To_One.Security.CurrentUser;
import uk.ac.cf._5.group14.One_To_One.Users.User;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleApiController.class);
    private static final long UNDO_WINDOW_SECONDS = 30;
    private final ConcurrentMap<String, UndoOperation> undoOperations = new ConcurrentHashMap<>();

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private ScheduleEntryService scheduleEntryService;

    @Autowired
    private ScheduleOccurrenceRepository scheduleOccurrenceRepository;

    @Autowired
    private ScheduleAppliedRepository scheduleAppliedRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private CustomExerciseRepository customExerciseRepository;

    @Autowired
    private CalendarTaskRepository calendarTaskRepository;

    /**
     * Get schedule metadata (frequency, active days, etc.)
     */
    @GetMapping("/{id}/metadata")
    public ResponseEntity<Map<String, Object>> getScheduleMetadata(
            @PathVariable Long id,
            @CurrentUser(required = false) User user) {
        
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        
        Schedule schedule = scheduleService.findById(id);
        if (schedule == null) {
            return ResponseEntity.notFound().build();
        }

        // Check access rights - only owner can access for now
        if (schedule.getUser() == null || !schedule.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        Map<String, Object> metadata = calculateMetadata(schedule);
        return ResponseEntity.ok(metadata);
    }

    /**
     * Get metadata for multiple schedules in a single request (batch)
     */
    @GetMapping("/metadata/batch")
    public ResponseEntity<Map<String, Map<String, Object>>> getBatchMetadata(
            @RequestParam List<Long> ids,
            @CurrentUser(required = false) User user) {
        
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        Map<String, Map<String, Object>> result = new HashMap<>();
        
        for (Long id : ids) {
            Schedule schedule = scheduleService.findById(id);
            if (schedule != null && schedule.getUser() != null && 
                schedule.getUser().getId().equals(user.getId())) {
                result.put(id.toString(), calculateMetadata(schedule));
            }
        }
        
        return ResponseEntity.ok(result);
    }

    /**
     * Calculate metadata for a schedule
     */
    private Map<String, Object> calculateMetadata(Schedule schedule) {
        List<ScheduleEntry> entries = scheduleEntryService.getEntriesBySchedule(schedule);

        java.util.TreeSet<Integer> activeDaySet = new java.util.TreeSet<>();
        for (ScheduleEntry entry : entries) {
            if (entry == null) continue;
            int day = entry.getDayOfWeek();
            if (day >= 1 && day <= 7) {
                activeDaySet.add(day);
            }
        }
        List<Integer> activeDayIndexes = new java.util.ArrayList<>(activeDaySet);

        List<String> activeDayLabels = activeDayIndexes.stream()
            .map(day -> switch (day) {
                case 1 -> "Mon";
                case 2 -> "Tue";
                case 3 -> "Wed";
                case 4 -> "Thu";
                case 5 -> "Fri";
                case 6 -> "Sat";
                case 7 -> "Sun";
                default -> "Day " + day;
            })
            .collect(Collectors.toList());
        
        // Calculate metadata
        long activeDaysCount = activeDayIndexes.size();
        
        int totalExercises = entries.size();
        int maxConsecutiveDays = 0;
        if (!activeDayIndexes.isEmpty()) {
            List<Integer> doubled = new java.util.ArrayList<>(activeDayIndexes);
            doubled.addAll(activeDayIndexes.stream().map(day -> day + 7).toList());
            int streak = 1;
            for (int i = 1; i < doubled.size(); i++) {
                if (doubled.get(i) - doubled.get(i - 1) == 1) {
                    streak += 1;
                    maxConsecutiveDays = Math.max(maxConsecutiveDays, streak);
                } else {
                    streak = 1;
                }
            }
            maxConsecutiveDays = Math.min(7, Math.max(maxConsecutiveDays, 1));
        }

        boolean noRestDays = activeDaysCount >= 7;

        boolean imbalancedStructure = false;
        if (activeDayIndexes.size() >= 3) {
            int largestGap = 0;
            for (int i = 0; i < activeDayIndexes.size(); i++) {
                int current = activeDayIndexes.get(i);
                int nextBase = (i + 1 < activeDayIndexes.size()) ? activeDayIndexes.get(i + 1) : activeDayIndexes.get(0);
                int next = (i + 1 < activeDayIndexes.size()) ? nextBase : nextBase + 7;
                largestGap = Math.max(largestGap, next - current - 1);
            }
            imbalancedStructure = largestGap >= 3;
        }

        List<Map<String, String>> healthWarnings = new java.util.ArrayList<>();
        if (noRestDays) {
            healthWarnings.add(Map.of(
                    "key", "no-rest-days",
                    "label", "No rest days",
                    "description", "This schedule trains all 7 days; consider adding at least one recovery day."
            ));
        }
        if (maxConsecutiveDays >= 5) {
            healthWarnings.add(Map.of(
                    "key", "long-streak",
                    "label", "Long training streak",
                    "description", "This plan has " + maxConsecutiveDays + " consecutive training days."
            ));
        }
        if (imbalancedStructure) {
            healthWarnings.add(Map.of(
                    "key", "imbalanced",
                    "label", "Imbalanced structure",
                    "description", "Training load is unevenly distributed across the week."
            ));
        }
        
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("sessionsPerWeek", activeDaysCount);
        metadata.put("activeDays", activeDaysCount);
        metadata.put("activeDayIndexes", activeDayIndexes);
        metadata.put("activeDayLabels", activeDayLabels);
        metadata.put("totalExercises", totalExercises);
        metadata.put("restDays", Math.max(0, 7 - activeDaysCount));
        metadata.put("noRestDays", noRestDays);
        metadata.put("maxConsecutiveDays", maxConsecutiveDays);
        metadata.put("imbalancedStructure", imbalancedStructure);
        metadata.put("healthWarnings", healthWarnings);
        
        return metadata;
    }

    /**
     * Get schedule preview with weekly structure
     */
    @GetMapping("/{id}/preview")
    public ResponseEntity<Map<String, Object>> getSchedulePreview(
            @PathVariable Long id,
            @CurrentUser(required = false) User user) {
        
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        
        Schedule schedule = scheduleService.findById(id);
        if (schedule == null) {
            return ResponseEntity.notFound().build();
        }

        // Check access rights - only owner can access for now
        if (schedule.getUser() == null || !schedule.getUser().getId().equals(user.getId())) {
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
            @CurrentUser(required = false) User user) {
        
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        
        Schedule original = scheduleService.findById(id);
        if (original == null) {
            return ResponseEntity.notFound().build();
        }

        // Check access rights - can only duplicate own schedules
        if (original.getUser() == null || !original.getUser().getId().equals(user.getId())) {
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

    @PostMapping("/{id}/deployment/impact")
    public ResponseEntity<Map<String, Object>> deploymentImpact(
            @PathVariable Long id,
            @CurrentUser(required = false) User user,
            @RequestBody Map<String, Object> request) {

        Schedule schedule = validateOwnedSchedule(id, user);
        if (schedule == null) {
            if (user == null) return ResponseEntity.status(401).build();
            return ResponseEntity.status(403).build();
        }

        DeploymentWindow window = resolveWindow(request);
        if (window == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid deployment window"));
        }

        String strategy = normalizeStrategy((String) request.get("strategy"));
        ImpactComputation impact = computeImpact(schedule, user, window, strategy);
        return ResponseEntity.ok(impact.toMap());
    }

    @PostMapping("/{id}/deployment/apply")
    @Transactional
    public ResponseEntity<Map<String, Object>> applyDeployment(
            @PathVariable Long id,
            @CurrentUser(required = false) User user,
            @RequestBody Map<String, Object> request) {

        Schedule schedule = validateOwnedSchedule(id, user);
        if (schedule == null) {
            if (user == null) return ResponseEntity.status(401).build();
            return ResponseEntity.status(403).build();
        }

        DeploymentWindow window = resolveWindow(request);
        if (window == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid deployment window"));
        }

        String strategy = normalizeStrategy((String) request.get("strategy"));
        ImpactComputation impact = computeImpact(schedule, user, window, strategy);

        Set<LocalDate> conflictDays = new HashSet<>(impact.conflictDates);
        List<ScheduleOccurrenceSnapshot> removedSnapshots = new ArrayList<>();
        if ("replace".equals(strategy) && !impact.existingConflicts.isEmpty()) {
            for (ScheduleOccurrence existing : impact.existingConflicts) {
                removedSnapshots.add(ScheduleOccurrenceSnapshot.from(existing));
            }
            scheduleOccurrenceRepository.deleteAll(impact.existingConflicts);
        }

        int created = 0;
        List<Long> createdOccurrenceIds = new ArrayList<>();
        for (PlannedOccurrence planned : impact.plannedOccurrences) {
            if ("skip".equals(strategy) && conflictDays.contains(planned.date())) {
                continue;
            }

            ScheduleOccurrence occ = new ScheduleOccurrence();
            occ.setUser(user);
            occ.setSchedule(schedule);
            occ.setScheduleName(schedule.getName());
            occ.setDate(planned.date());
            if (planned.entry().getExercise() != null) {
                occ.setExercise(planned.entry().getExercise());
            }
            if (planned.entry().getCustomExercise() != null) {
                occ.setCustomExercise(planned.entry().getCustomExercise());
            }
            if (occ.getExercise() == null && occ.getCustomExercise() == null) {
                continue;
            }

            ScheduleOccurrence saved = scheduleOccurrenceRepository.save(occ);
            if (saved.getId() != null) {
                createdOccurrenceIds.add(saved.getId());
            }
            created += 1;
        }

        ScheduleApplied applied = new ScheduleApplied();
        applied.setSchedule(schedule);
        applied.setUser(user);
        applied.setDateApplied(window.start());
        applied.setShownOnCalendar(true);
        applied.setRequiresLogging(false);
        applied.setDurationWeeks(window.weeks());
        scheduleAppliedRepository.save(applied);

        String undoToken = UUID.randomUUID().toString();
        undoOperations.put(
            undoToken,
            new UndoOperation(
                user.getId(),
                schedule.getId(),
                applied.getId(),
                createdOccurrenceIds,
                removedSnapshots,
                java.time.Instant.now().plusSeconds(UNDO_WINDOW_SECONDS)
            )
        );

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", true);
        response.put("created", created);
        response.put("replaced", "replace".equals(strategy) ? impact.existingConflicts.size() : 0);
        response.put("skipped", "skip".equals(strategy) ? impact.skippedByStrategy : 0);
        response.put("conflictDates", impact.conflictDates.stream().map(LocalDate::toString).toList());
        response.put("windowStart", window.start().toString());
        response.put("windowEnd", window.end().toString());
        response.put("strategy", strategy);
        response.put("undoToken", undoToken);
        response.put("undoExpiresInSeconds", UNDO_WINDOW_SECONDS);

        LOGGER.info(
            "schedule_deploy_applied userId={} scheduleId={} scope={} strategy={} start={} end={} created={} replaced={} skipped={} conflicts={} undoToken={}",
            user.getId(),
            schedule.getId(),
            window.scope(),
            strategy,
            window.start(),
            window.end(),
            created,
            "replace".equals(strategy) ? impact.existingConflicts.size() : 0,
            "skip".equals(strategy) ? impact.skippedByStrategy : 0,
            impact.conflictDates.size(),
            undoToken
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/deployment/undo")
    @Transactional
    public ResponseEntity<Map<String, Object>> undoDeployment(
            @PathVariable Long id,
            @CurrentUser(required = false) User user,
            @RequestBody Map<String, Object> request) {

        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        String undoToken = request.get("undoToken") instanceof String token ? token : null;
        if (undoToken == null || undoToken.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing undo token"));
        }

        UndoOperation operation = undoOperations.remove(undoToken);
        if (operation == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Undo token is invalid or expired"));
        }

        if (operation.expiresAt().isBefore(java.time.Instant.now())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Undo window expired"));
        }

        if (!Objects.equals(operation.userId(), user.getId()) || !Objects.equals(operation.scheduleId(), id)) {
            return ResponseEntity.status(403).build();
        }

        if (operation.appliedId() != null) {
            scheduleAppliedRepository.findById(operation.appliedId()).ifPresent((applied) -> {
                if (applied.getUser() != null && Objects.equals(applied.getUser().getId(), user.getId())) {
                    scheduleAppliedRepository.delete(applied);
                }
            });
        }

        if (!operation.createdOccurrenceIds().isEmpty()) {
            List<ScheduleOccurrence> createdOccurrences = scheduleOccurrenceRepository.findAllById(operation.createdOccurrenceIds()).stream()
                    .filter(occ -> occ.getUser() != null && Objects.equals(occ.getUser().getId(), user.getId()))
                    .toList();
            if (!createdOccurrences.isEmpty()) {
                scheduleOccurrenceRepository.deleteAll(createdOccurrences);
            }
        }

        if (!operation.removedSnapshots().isEmpty()) {
            Schedule schedule = scheduleService.findById(id);
            for (ScheduleOccurrenceSnapshot snapshot : operation.removedSnapshots()) {
                ScheduleOccurrence restored = snapshot.toOccurrence(user, schedule, exerciseRepository, customExerciseRepository);
                scheduleOccurrenceRepository.save(restored);
            }
        }

        LOGGER.info(
                "schedule_deploy_undo userId={} scheduleId={} undoToken={} restoredOccurrences={} removedCreatedOccurrences={}",
                user.getId(),
                id,
                undoToken,
                operation.removedSnapshots().size(),
                operation.createdOccurrenceIds().size()
        );

        return ResponseEntity.ok(Map.of("success", true));
    }

    private Schedule validateOwnedSchedule(Long id, User user) {
        if (user == null) return null;
        Schedule schedule = scheduleService.findById(id);
        if (schedule == null || schedule.getUser() == null) return null;
        if (!Objects.equals(schedule.getUser().getId(), user.getId())) return null;
        return schedule;
    }

    private String normalizeStrategy(String strategy) {
        if (strategy == null) return "merge";
        String normalized = strategy.trim().toLowerCase();
        if ("replace".equals(normalized) || "skip".equals(normalized)) {
            return normalized;
        }
        return "merge";
    }

    private DeploymentWindow resolveWindow(Map<String, Object> request) {
        // Parse recurrence config if provided
        RecurrenceConfig recurrence = parseRecurrenceConfig(request.get("recurrence"));
        
        LocalDate now = LocalDate.now();
        LocalDate selectedDate = parseDate(request.get("selectedDate"));
        LocalDate startDateInput = parseDate(request.get("startDate"));
        LocalDate anchor = selectedDate != null ? selectedDate : (startDateInput != null ? startDateInput : now);

        // If recurrence config exists, use it to determine the window
        if (recurrence != null) {
            LocalDate endDate = recurrence.endDate();
            String repeat = recurrence.repeat();
            
            // Determine end date based on repeat type
            if (endDate == null) {
                // Forever mode - default to 1 year for practical limits
                endDate = anchor.plusYears(1);
            }
            
            // Calculate weeks for backward compatibility
            int weeks = (int) Math.ceil((double) java.time.temporal.ChronoUnit.DAYS.between(anchor, endDate) / 7.0);
            if (weeks < 1) weeks = 1;
            if (weeks > 52) weeks = 52;
            
            return new DeploymentWindow(anchor, endDate, weeks, repeat, recurrence);
        }
        
        // Fallback to old scope-based logic for backward compatibility
        String scope = request.get("scope") instanceof String s ? s.trim().toLowerCase() : "week";

        int weeks = parseInt(request.get("weeks"), 1);
        if (weeks < 1) weeks = 1;
        if (weeks > 52) weeks = 52;

        if ("forward".equals(scope)) {
            int forwardWeeks = parseInt(request.get("weeks"), 8);
            if (forwardWeeks < 1) forwardWeeks = 8;
            if (forwardWeeks > 52) forwardWeeks = 52;
            LocalDate end = anchor.plusWeeks(forwardWeeks).minusDays(1);
            return new DeploymentWindow(anchor, end, forwardWeeks, "forward", null);
        }

        if ("weeks".equals(scope)) {
            LocalDate end = anchor.plusWeeks(weeks).minusDays(1);
            return new DeploymentWindow(anchor, end, weeks, "weeks", null);
        }

        LocalDate weekStart = anchor.with(DayOfWeek.MONDAY);
        LocalDate weekEnd = weekStart.plusDays(6);
        return new DeploymentWindow(weekStart, weekEnd, 1, "week", null);
    }
    
    @SuppressWarnings("unchecked")
    private RecurrenceConfig parseRecurrenceConfig(Object raw) {
        if (!(raw instanceof Map)) {
            return null;
        }
        
        Map<String, Object> map = (Map<String, Object>) raw;
        String repeat = map.get("repeat") instanceof String s ? s : null;
        if (repeat == null || repeat.isBlank()) {
            return null;
        }
        
        Integer interval = null;
        if (map.get("interval") instanceof Number n) {
            interval = n.intValue();
        } else if (map.get("interval") instanceof String s) {
            try {
                interval = Integer.parseInt(s.trim());
            } catch (NumberFormatException ignored) {}
        }
        
        String unit = map.get("unit") instanceof String s ? s : null;
        LocalDate endDate = parseDate(map.get("endDate"));
        
        return new RecurrenceConfig(repeat, interval, unit, endDate);
    }

    private LocalDate parseDate(Object raw) {
        if (!(raw instanceof String value) || value.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException ignored) {
            return null;
        }
    }

    private int parseInt(Object raw, int fallback) {
        if (raw instanceof Number number) {
            return number.intValue();
        }
        if (raw instanceof String text) {
            try {
                return Integer.parseInt(text.trim());
            } catch (NumberFormatException ignored) {
                return fallback;
            }
        }
        return fallback;
    }

    private ImpactComputation computeImpact(Schedule schedule, User user, DeploymentWindow window, String strategy) {
        List<ScheduleEntry> entries = scheduleEntryService.getEntriesBySchedule(schedule);
        List<PlannedOccurrence> plannedOccurrences = new ArrayList<>();

        // Determine how to generate occurrences based on the window scope (recurrence type)
        String scope = window.scope();
        
        if ("forever".equals(scope) || "daily".equals(scope) || "weekly".equals(scope) || 
            "monthly".equals(scope) || "yearly".equals(scope) || "custom".equals(scope)) {
            // Use new recurrence-based generation
            plannedOccurrences = generateRecurrenceOccurrences(window, entries);
        } else {
            // Use old day-of-week based generation for backward compatibility
            LocalDate cursor = window.start();
            while (!cursor.isAfter(window.end())) {
                int dayOfWeek = cursor.getDayOfWeek().getValue();
                for (ScheduleEntry entry : entries) {
                    if (entry.getDayOfWeek() == dayOfWeek) {
                        plannedOccurrences.add(new PlannedOccurrence(cursor, entry));
                    }
                }
                cursor = cursor.plusDays(1);
            }
        }

        Set<LocalDate> plannedDates = plannedOccurrences.stream().map(PlannedOccurrence::date).collect(Collectors.toSet());

        List<ScheduleOccurrence> existingRange = plannedDates.isEmpty()
                ? List.of()
                : scheduleOccurrenceRepository.findByUserAndDateBetween(user, window.start(), window.end()).stream()
                    .filter(occ -> plannedDates.contains(occ.getDate()))
                    .toList();

        List<CalendarTask> taskConflicts = plannedDates.isEmpty()
            ? List.of()
            : calendarTaskRepository.findByUserAndDateBetween(user, window.start(), window.end()).stream()
                .filter(task -> task != null && task.getDate() != null && plannedDates.contains(task.getDate()))
                .toList();

        Map<LocalDate, Long> conflictsByDate = existingRange.stream()
                .collect(Collectors.groupingBy(ScheduleOccurrence::getDate, Collectors.counting()));

        Map<LocalDate, Long> taskConflictsByDate = taskConflicts.stream()
            .collect(Collectors.groupingBy(CalendarTask::getDate, Collectors.counting()));

        for (Map.Entry<LocalDate, Long> entry : taskConflictsByDate.entrySet()) {
            conflictsByDate.merge(entry.getKey(), entry.getValue(), (left, right) -> left + right);
        }

        List<LocalDate> conflictDates = conflictsByDate.keySet().stream().sorted().toList();

        long skippedByStrategy = 0;
        if ("skip".equals(strategy) && !conflictDates.isEmpty()) {
            Set<LocalDate> conflictDaySet = new HashSet<>(conflictDates);
            skippedByStrategy = plannedOccurrences.stream()
                    .filter(p -> conflictDaySet.contains(p.date()))
                    .count();
        }

        return new ImpactComputation(window, strategy, plannedOccurrences, existingRange, taskConflicts, conflictDates, conflictsByDate, skippedByStrategy);
    }
    
    /**
     * Generate occurrences based on recurrence pattern
     */
    private List<PlannedOccurrence> generateRecurrenceOccurrences(DeploymentWindow window, List<ScheduleEntry> entries) {
        List<PlannedOccurrence> occurrences = new ArrayList<>();
        String repeat = window.scope();
        LocalDate start = window.start();
        LocalDate end = window.end();
        
        if ("daily".equals(repeat) || "forever".equals(repeat)) {
            // Every day within the range - match schedule entries by day of week
            LocalDate cursor = start;
            while (!cursor.isAfter(end)) {
                int dayOfWeek = cursor.getDayOfWeek().getValue();
                for (ScheduleEntry entry : entries) {
                    if (entry.getDayOfWeek() == dayOfWeek) {
                        occurrences.add(new PlannedOccurrence(cursor, entry));
                    }
                }
                cursor = cursor.plusDays(1);
            }
        } else if ("weekly".equals(repeat)) {
            // Once per week - on the matching days of week
            LocalDate cursor = start;
            while (!cursor.isAfter(end)) {
                int dayOfWeek = cursor.getDayOfWeek().getValue();
                for (ScheduleEntry entry : entries) {
                    if (entry.getDayOfWeek() == dayOfWeek) {
                        occurrences.add(new PlannedOccurrence(cursor, entry));
                    }
                }
                cursor = cursor.plusDays(1);
            }
        } else if ("monthly".equals(repeat)) {
            // Once per month - place each schedule entry on the same week/day pattern relative to month start
            LocalDate cursor = start;
            
            while (!cursor.isAfter(end)) {
                LocalDate monthStart = cursor.withDayOfMonth(1);
                LocalDate monthEnd = cursor.withDayOfMonth(cursor.lengthOfMonth());
                
                // For each entry, figure out which week of the month it falls in the original schedule
                for (ScheduleEntry entry : entries) {
                    int targetDayOfWeek = entry.getDayOfWeek();
                    
                    // Find all occurrences of this day of week in this month
                    LocalDate dayCursor = monthStart;
                    
                    while (!dayCursor.isAfter(monthEnd)) {
                        if (dayCursor.getDayOfWeek().getValue() == targetDayOfWeek) {
                            // Add occurrence for each matching day (allows multiple per month if schedule has multiple entries)
                            if (!dayCursor.isBefore(start) && !dayCursor.isAfter(end)) {
                                occurrences.add(new PlannedOccurrence(dayCursor, entry));
                            }
                        }
                        dayCursor = dayCursor.plusDays(1);
                    }
                }
                
                // Move to next month
                cursor = cursor.plusMonths(1).withDayOfMonth(1);
            }
        } else if ("yearly".equals(repeat)) {
            // Once per year - place schedule entries on the same dates each year
            // Generate occurrences for the start date's week pattern, repeated yearly
            int startYear = start.getYear();
            int endYear = end.getYear();
            
            for (int year = startYear; year <= endYear; year++) {
                // For each year, place the schedule entries on the same week as the start date
                LocalDate yearStart = start.withYear(year);
                if (yearStart.isAfter(end)) break;
                
                // Find the week containing this date
                LocalDate weekStart = yearStart.with(DayOfWeek.MONDAY);
                
                // Place each entry on its designated day of week within this week
                for (ScheduleEntry entry : entries) {
                    int targetDayOfWeek = entry.getDayOfWeek();
                    LocalDate occurrenceDate = weekStart.plusDays(targetDayOfWeek - 1);
                    
                    if (!occurrenceDate.isBefore(start) && !occurrenceDate.isAfter(end)) {
                        occurrences.add(new PlannedOccurrence(occurrenceDate, entry));
                    }
                }
            }
        } else if ("custom".equals(repeat)) {
            // Custom interval - use interval and unit from recurrence config
            RecurrenceConfig config = window.recurrence();
            if (config != null && config.interval() != null && config.unit() != null) {
                int interval = config.interval();
                String unit = config.unit();
                
                LocalDate cursor = start;
                while (!cursor.isAfter(end)) {
                    // Add occurrences for all matching days at this position
                    int dayOfWeek = cursor.getDayOfWeek().getValue();
                    for (ScheduleEntry entry : entries) {
                        if (entry.getDayOfWeek() == dayOfWeek) {
                            occurrences.add(new PlannedOccurrence(cursor, entry));
                        }
                    }
                    
                    // Advance by the interval
                    if ("days".equals(unit) || "day".equals(unit)) {
                        cursor = cursor.plusDays(interval);
                    } else if ("weeks".equals(unit) || "week".equals(unit)) {
                        cursor = cursor.plusWeeks(interval);
                    } else if ("months".equals(unit) || "month".equals(unit)) {
                        cursor = cursor.plusMonths(interval);
                    } else if ("years".equals(unit) || "year".equals(unit)) {
                        cursor = cursor.plusYears(interval);
                    } else {
                        // Default to weeks
                        cursor = cursor.plusWeeks(interval);
                    }
                }
            } else {
                // Fallback to daily if no config
                LocalDate cursor = start;
                while (!cursor.isAfter(end)) {
                    int dayOfWeek = cursor.getDayOfWeek().getValue();
                    for (ScheduleEntry entry : entries) {
                        if (entry.getDayOfWeek() == dayOfWeek) {
                            occurrences.add(new PlannedOccurrence(cursor, entry));
                        }
                    }
                    cursor = cursor.plusDays(1);
                }
            }
        }
        
        return occurrences;
    }

    private record DeploymentWindow(LocalDate start, LocalDate end, int weeks, String scope, RecurrenceConfig recurrence) {
        // Constructor with recurrence
        public DeploymentWindow(LocalDate start, LocalDate end, int weeks, String scope, RecurrenceConfig recurrence) {
            this.start = start;
            this.end = end;
            this.weeks = weeks;
            this.scope = scope;
            this.recurrence = recurrence;
        }
        
        // Constructor without recurrence for backward compatibility
        public DeploymentWindow(LocalDate start, LocalDate end, int weeks, String scope) {
            this(start, end, weeks, scope, null);
        }
    }
    
    private record RecurrenceConfig(String repeat, Integer interval, String unit, LocalDate endDate) {
        public RecurrenceConfig {
            // Normalize repeat type
            repeat = repeat != null ? repeat.toLowerCase().trim() : "forever";
            // Default interval to 1 if not specified for custom
            if ("custom".equals(repeat) && interval == null) {
                interval = 1;
            }
            // Normalize unit
            unit = unit != null ? unit.toLowerCase().trim() : "weeks";
        }
    }

    private record PlannedOccurrence(LocalDate date, ScheduleEntry entry) {}

    private static final class ImpactComputation {
        private final DeploymentWindow window;
        private final String strategy;
        private final List<PlannedOccurrence> plannedOccurrences;
        private final List<ScheduleOccurrence> existingConflicts;
        private final List<CalendarTask> taskConflicts;
        private final List<LocalDate> conflictDates;
        private final Map<LocalDate, Long> conflictsByDate;
        private final long skippedByStrategy;

        private ImpactComputation(
                DeploymentWindow window,
                String strategy,
                List<PlannedOccurrence> plannedOccurrences,
                List<ScheduleOccurrence> existingConflicts,
                List<CalendarTask> taskConflicts,
                List<LocalDate> conflictDates,
                Map<LocalDate, Long> conflictsByDate,
                long skippedByStrategy
        ) {
            this.window = window;
            this.strategy = strategy;
            this.plannedOccurrences = plannedOccurrences;
            this.existingConflicts = existingConflicts;
            this.taskConflicts = taskConflicts;
            this.conflictDates = conflictDates;
            this.conflictsByDate = conflictsByDate;
            this.skippedByStrategy = skippedByStrategy;
        }

        private Map<String, Object> toMap() {
            long added = plannedOccurrences.size();
            long replaced = "replace".equals(strategy) ? existingConflicts.size() : 0;
            long skipped = "skip".equals(strategy) ? skippedByStrategy : 0;

            Map<String, Object> summary = new LinkedHashMap<>();
            summary.put("plannedEntries", plannedOccurrences.size());
            summary.put("added", Math.max(0, added - skipped));
            summary.put("replaced", replaced);
            summary.put("skipped", skipped);
            summary.put("existingConflicts", existingConflicts.size());
            summary.put("taskConflicts", taskConflicts.size());

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("windowStart", window.start().toString());
            response.put("windowEnd", window.end().toString());
            response.put("scope", window.scope());
            response.put("weeks", window.weeks());
            response.put("strategy", strategy);
            response.put("summary", summary);
            response.put("conflictDates", conflictDates.stream().map(LocalDate::toString).toList());
            response.put(
                    "conflictsByDate",
                    conflictsByDate.entrySet().stream().collect(Collectors.toMap(
                            entry -> entry.getKey().toString(),
                            Map.Entry::getValue
                    ))
            );

            return response;
        }
    }

    private record UndoOperation(
            Long userId,
            Long scheduleId,
            Long appliedId,
            List<Long> createdOccurrenceIds,
            List<ScheduleOccurrenceSnapshot> removedSnapshots,
            java.time.Instant expiresAt
    ) {}

    private record ScheduleOccurrenceSnapshot(
            LocalDate date,
            Long exerciseId,
            Long customExerciseId,
            String scheduleName,
            boolean completed,
            boolean missed,
            java.time.Instant missedAt,
            Long trainerTemplateId,
            Long trainerTemplateEntryId
    ) {
        private static ScheduleOccurrenceSnapshot from(ScheduleOccurrence occurrence) {
            return new ScheduleOccurrenceSnapshot(
                    occurrence.getDate(),
                    occurrence.getExercise() != null ? occurrence.getExercise().getId() : null,
                    occurrence.getCustomExercise() != null ? occurrence.getCustomExercise().getId() : null,
                    occurrence.getScheduleName(),
                    occurrence.isCompleted(),
                    occurrence.isMissed(),
                    occurrence.getMissedAt(),
                    occurrence.getTrainerTemplateId(),
                    occurrence.getTrainerTemplateEntryId()
            );
        }

        private ScheduleOccurrence toOccurrence(
            User user,
            Schedule schedule,
            ExerciseRepository exerciseRepository,
            CustomExerciseRepository customExerciseRepository
        ) {
            ScheduleOccurrence occurrence = new ScheduleOccurrence();
            occurrence.setUser(user);
            occurrence.setSchedule(schedule);
            occurrence.setScheduleName(scheduleName != null ? scheduleName : (schedule != null ? schedule.getName() : "Schedule"));
            occurrence.setDate(date);
            occurrence.setCompleted(completed);
            occurrence.setMissed(missed);
            occurrence.setMissedAt(missedAt);
            occurrence.setTrainerTemplateId(trainerTemplateId);
            occurrence.setTrainerTemplateEntryId(trainerTemplateEntryId);

            if (exerciseId != null) {
                exerciseRepository.findById(exerciseId).ifPresent(occurrence::setExercise);
            }
            if (customExerciseId != null) {
                customExerciseRepository.findById(customExerciseId).ifPresent(occurrence::setCustomExercise);
            }
            return occurrence;
        }
    }
}
