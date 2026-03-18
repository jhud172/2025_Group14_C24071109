package uk.ac.cf._5.group14.One_To_One.ScheduleData;

import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uk.ac.cf._5.group14.One_To_One.ExerciseData.Exercise;
import uk.ac.cf._5.group14.One_To_One.ExerciseData.ExerciseRepository;
import uk.ac.cf._5.group14.One_To_One.ExerciseData.ExerciseService;
import uk.ac.cf._5.group14.One_To_One.ScheduleData.ScheduleEntryService;
import uk.ac.cf._5.group14.One_To_One.TrainerClient.TrainerClientLink;
import uk.ac.cf._5.group14.One_To_One.TrainerClient.TrainerClientLinkRepository;
import uk.ac.cf._5.group14.One_To_One.TrainerClient.TrainerClientLinkStatus;
import uk.ac.cf._5.group14.One_To_One.Users.User;
import uk.ac.cf._5.group14.One_To_One.Users.UserRepository;
import uk.ac.cf._5.group14.One_To_One.Workout.Workout;
import uk.ac.cf._5.group14.One_To_One.Workout.WorkoutRepository;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/schedules")
public class ScheduleController {

    @Autowired
    private final ScheduleEntryService scheduleEntryService;

    public ScheduleController(ScheduleService scheduleService,
                              ScheduleEntryService scheduleEntryService) {
        this.scheduleService = scheduleService;
        this.scheduleEntryService = scheduleEntryService;
    }

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private ScheduleEntryRepository scheduleEntryRepository;

    @Autowired
    private ScheduleOccurrenceService scheduleOccurrenceService;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private WorkoutRepository workoutRepository;

    @Autowired
    private ScheduleOccurrenceRepository scheduleOccurrenceRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private ScheduleAppliedRepository scheduleAppliedRepository;

    @Autowired
    private TrainerClientLinkRepository trainerClientLinkRepository;

    @Autowired
    private uk.ac.cf._5.group14.One_To_One.Security.AccessGuard accessGuard;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ScheduleTemplateService scheduleTemplateService;

    @GetMapping("")
    public String listSchedules(@SessionAttribute("user") User user, Model model) {
        List<Schedule> all = scheduleService.findByUser(user);
        List<ScheduleApplied> active = scheduleAppliedRepository.findByUser(user);
        User trainer = getActiveTrainer(user);
        List<Schedule> shared = trainer != null ? scheduleService.findByUser(trainer) : List.of();

        model.addAttribute("schedules", all);
        model.addAttribute("activeSchedules", active);
        model.addAttribute("sharedSchedules", shared);
        model.addAttribute("currentUserId", user.getId());

        return "schedule/list";
    }

    @Transactional
    @GetMapping("/{id}/delete")
    public String deleteSchedule(
            @PathVariable Long id,
            @SessionAttribute("user") User user) {
        Schedule schedule = scheduleRepository.findById(id).orElse(null);
        if (schedule == null || !schedule.getUser().getId().equals(user.getId())) {
            return "redirect:/schedules?error";
        }
        scheduleEntryRepository.deleteByScheduleId(id);
        scheduleOccurrenceRepository.deleteBySchedule(schedule);
        scheduleAppliedRepository.deleteBySchedule(schedule);
        scheduleRepository.delete(schedule);
        return "redirect:/schedules?deleted";
    }

    @GetMapping("/{id}/deactivate")
    public String deactivateSchedule(
            @PathVariable Long id,
            @SessionAttribute("user") User user
    ) {
        Schedule schedule = scheduleService.findById(id);
        if (schedule == null || !schedule.getUser().equals(user)) {
            return "redirect:/schedules?error";
        }
        scheduleOccurrenceRepository.deleteBySchedule(schedule);

        return "redirect:/schedules?deactivated";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("schedule", new Schedule());
        return "schedule/create";
    }

    @PostMapping("/create")
    public String createSubmit(
            @ModelAttribute Schedule schedule,
            @SessionAttribute("user") User user
    ) {
        schedule.setUser(user);
        scheduleService.save(schedule);
        return "redirect:/schedules/" + schedule.getId() + "/entries";
    }

    @Transactional
    @GetMapping("/applied/{appliedId}/remove")
    public String removeApplied(@PathVariable Long appliedId,
                                @SessionAttribute("user") User user) {
        ScheduleApplied applied = scheduleAppliedRepository.findById(appliedId).orElse(null);
        if (applied == null || applied.getUser().getId() != user.getId()) {
            return "redirect:/schedules?error";
        }
        scheduleOccurrenceRepository.deleteByScheduleIdAndUserId(
                applied.getSchedule().getId(),
                user.getId()
        );
        scheduleAppliedRepository.delete(applied);
        return "redirect:/schedules";
    }


    @GetMapping("/{id:\\d+}/entries")
    public String entryForm(@PathVariable Long id,
                            @SessionAttribute("user") User user,
                            Model model) {

        Schedule schedule = scheduleService.findById(id);
        if (!isOwner(user, schedule)) {
            return "redirect:/schedules?notfound";
        }

        model.addAttribute("exercises", exerciseRepository.findAll());
        model.addAttribute("schedule", schedule);
        model.addAttribute("entry", new ScheduleEntry());
        model.addAttribute("entries", scheduleEntryService.getEntries(id));

        return "schedule/add-entry";
    }

    @PostMapping("/{id}/entries")
    public String entrySubmit(
            @PathVariable Long id,
            @SessionAttribute("user") User user,
            @ModelAttribute ScheduleEntry entry
    ) {
        Schedule schedule = scheduleService.findById(id);
        if (!isOwner(user, schedule)) {
            return "redirect:/schedules?error";
        }
        entry.setSchedule(schedule);

        scheduleEntryService.save(entry);

        return "redirect:/schedules/" + id + "/entries";
    }

    @PostMapping("/{id}/apply")
    public String applyScheduleToCalendar(
            @PathVariable Long id,
            @RequestParam LocalDate startDate,
            @RequestParam int weeks,
            @SessionAttribute("user") User user
    ) {
        Schedule schedule = findAccessibleSchedule(user, id);
        if (schedule == null) {
            return "redirect:/schedules?error";
        }
        List<ScheduleEntry> entries = scheduleEntryService.getEntriesBySchedule(schedule);
        ScheduleApplied applied = new ScheduleApplied();
        applied.setSchedule(schedule);
        applied.setUser(user);
        applied.setDateApplied(startDate);
        applied.setDurationWeeks(Math.max(1, weeks));
        applied.setShownOnCalendar(true);
        applied.setRequiresLogging(false);
        scheduleAppliedRepository.save(applied);
        for (int week = 0; week < weeks; week++) {
            for (ScheduleEntry entry : entries) {
                LocalDate date = startDate
                        .plusWeeks(week)
                        .with(java.time.DayOfWeek.of(entry.getDayOfWeek()));
                ScheduleOccurrence occ = new ScheduleOccurrence();
                occ.setUser(user);
                occ.setSchedule(schedule);
                occ.setScheduleName(schedule.getName());
                occ.setDate(date);
                occ.setExercise(entry.getExercise());
                occ.setCustomExercise(entry.getCustomExercise());
                scheduleOccurrenceRepository.save(occ);
            }
        }

        return "redirect:/calendar";
    }

    @GetMapping("/{id}/apply")
    public String showApplyForm(@PathVariable Long id,
                                @SessionAttribute("user") User user,
                                Model model) {
        Schedule schedule = findAccessibleSchedule(user, id);
        if (schedule == null) {
            return "redirect:/schedules?error";
        }
        model.addAttribute("schedule", schedule);
        return "schedule/apply";
    }

    @PostMapping("/applied/{appliedId}/settings")
    public String updateAppliedSettings(
            @PathVariable Long appliedId,
            @RequestParam(defaultValue = "false") boolean shownOnCalendar,
            @RequestParam(defaultValue = "false") boolean requiresLogging,
            @SessionAttribute("user") User user
    ) {
        ScheduleApplied applied = scheduleAppliedRepository.findById(appliedId).orElse(null);
        if (applied == null || !applied.getUser().getId().equals(user.getId())) {
            return "redirect:/schedules?error";
        }

        boolean previousShown = applied.isShownOnCalendar();
        applied.setShownOnCalendar(shownOnCalendar);
        applied.setRequiresLogging(requiresLogging);
        if (applied.getDurationWeeks() < 1) {
            applied.setDurationWeeks(4);
        }
        scheduleAppliedRepository.save(applied);

        if (previousShown && !shownOnCalendar) {
            scheduleOccurrenceRepository.deleteByScheduleIdAndUserId(
                    applied.getSchedule().getId(),
                    user.getId()
            );
        }

        if (!previousShown && shownOnCalendar) {
            LocalDate start = applied.getDateApplied() != null ? applied.getDateApplied() : LocalDate.now();
            LocalDate end = start.plusWeeks(applied.getDurationWeeks()).minusDays(1);
            scheduleOccurrenceService.generateOccurrencesForSchedule(
                    applied.getSchedule(),
                    user,
                    start,
                    end,
                    1
            );
        }

        return "redirect:/schedules";
    }

    @PostMapping("/{id}/update")
    public String updateSchedule(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @SessionAttribute("user") User user
    ) {
        Schedule schedule = scheduleService.findById(id);
        if (!isOwner(user, schedule)) {
            return "redirect:/schedules?error";
        }
        String trimmedName = name == null ? "" : name.trim();
        if (trimmedName.isBlank()) {
            return "redirect:/schedules/" + id + "/entries?error";
        }
        schedule.setName(trimmedName);
        String cleanedDescription = description != null ? description.trim() : null;
        schedule.setDescription(cleanedDescription == null || cleanedDescription.isBlank() ? null : cleanedDescription);
        scheduleService.save(schedule);
        return "redirect:/schedules/" + id + "/entries?updated";
    }

    @GetMapping("/builder")
    public String builderPage(
            @SessionAttribute("user") User user,
            Model model
    ) {
        List<Workout> workouts = workoutRepository.findByUserId(user.getId());
        model.addAttribute("workouts", workouts);

        model.addAttribute("days", new String[]{"Mon","Tue","Wed","Thu","Fri","Sat","Sun"});
        model.addAttribute("schedule", new Schedule());
        model.addAttribute("templates", scheduleTemplateService.getAllTemplates());

        return "schedule/builder";
    }


    @PostMapping("/builder/save")
    public String saveBuilder(
            @RequestParam String payload,
            @RequestParam String name,
            @RequestParam(required = false, defaultValue = "WEEKLY") String scheduleType,
            @RequestParam(required = false, defaultValue = "WEEKLY_REPEAT") String rotationMode,
            @RequestParam(required = false, defaultValue = "7") Integer customDayCount,
            @RequestParam(required = false) String templateId,
            @SessionAttribute("user") User user
    ) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        Map<String, List<Long>> weekMap = mapper.readValue(payload, new TypeReference<>() {});

        Schedule schedule = new Schedule();
        schedule.setName(name);
        schedule.setUser(user);
        
        // Set schedule type and rotation mode with error handling
        try {
            schedule.setScheduleType(ScheduleType.valueOf(scheduleType));
        } catch (IllegalArgumentException e) {
            // Log warning and default to WEEKLY for invalid schedule type
            System.err.println("Warning: Invalid schedule type '" + scheduleType + "', defaulting to WEEKLY");
            schedule.setScheduleType(ScheduleType.WEEKLY);
        }
        
        try {
            schedule.setRotationMode(RotationMode.valueOf(rotationMode));
        } catch (IllegalArgumentException e) {
            // Log warning and default to WEEKLY_REPEAT for invalid rotation mode
            System.err.println("Warning: Invalid rotation mode '" + rotationMode + "', defaulting to WEEKLY_REPEAT");
            schedule.setRotationMode(RotationMode.WEEKLY_REPEAT);
        }
        
        schedule.setCustomDayCount(customDayCount != null ? customDayCount : 7);
        schedule.setTemplateId(templateId);
        
        scheduleService.save(schedule);

        for (Map.Entry<String, List<Long>> entry : weekMap.entrySet()) {

            int dayIndex = dayNameToInt(entry.getKey());
            List<Long> ids = entry.getValue();

            for (int order = 0; order < ids.size(); order++) {
                Long exerciseId = ids.get(order);

                Exercise ex = exerciseRepository.findById(exerciseId).orElse(null);
                if (ex == null) continue;

                ScheduleEntry se = new ScheduleEntry();
                se.setSchedule(schedule);
                se.setDayOfWeek(dayIndex);
                se.setOrderNumber(order);
                se.setExercise(ex);

                scheduleEntryService.save(se);
            }
        }

        return "redirect:/schedules";
    }

    private int dayNameToInt(String d) {
        return switch (d) {
            case "Mon" -> 1;
            case "Tue" -> 2;
            case "Wed" -> 3;
            case "Thu" -> 4;
            case "Fri" -> 5;
            case "Sat" -> 6;
            case "Sun" -> 7;
            default -> 1;
        };
    }

    private boolean isOwner(User user, Schedule schedule) {
        return schedule != null
                && schedule.getUser() != null
                && user != null
                && schedule.getUser().getId().equals(user.getId());
    }

    private User getActiveTrainer(User user) {
        if (user == null || user.getId() == null) {
            return null;
        }
        return trainerClientLinkRepository
                .findFirstByClientUserIdAndStatusOrderByUpdatedAtDesc(user.getId(), TrainerClientLinkStatus.ACTIVE)
                .map(TrainerClientLink::getTrainerUserId)
                .flatMap(userRepository::findById)
                .orElse(null);
    }

    private boolean isTrainerShared(User user, Schedule schedule) {
        if (schedule == null || schedule.getUser() == null || user == null) {
            return false;
        }
        return accessGuard.canClientAccessTrainer(user.getId(), schedule.getUser().getId());
    }

    private Schedule findAccessibleSchedule(User user, Long id) {
        Schedule schedule = scheduleService.findById(id);
        if (schedule == null) {
            return null;
        }
        if (isOwner(user, schedule) || isTrainerShared(user, schedule)) {
            return schedule;
        }
        return null;
    }


}
