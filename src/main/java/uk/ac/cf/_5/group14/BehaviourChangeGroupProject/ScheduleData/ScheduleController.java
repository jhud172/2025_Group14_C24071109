package uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData;

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
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseData.Exercise;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseData.ExerciseRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseData.ExerciseService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleEntryService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Workout.Workout;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Workout.WorkoutRepository;

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

    @GetMapping("")
    public String listSchedules(@SessionAttribute("user") User user, Model model) {
        List<Schedule> all = scheduleService.findByUser(user);
        List<ScheduleApplied> active = scheduleAppliedRepository.findByUser(user);

        model.addAttribute("schedules", all);
        model.addAttribute("activeSchedules", active);

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
    public String entryForm(@PathVariable Long id, Model model) {

        Schedule schedule = scheduleService.findById(id);
        if (schedule == null) {
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
            @ModelAttribute ScheduleEntry entry
    ) {
        Schedule schedule = scheduleService.findById(id);
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
        Schedule schedule = scheduleService.findById(id);
        List<ScheduleEntry> entries = scheduleEntryService.getEntriesBySchedule(schedule);
        ScheduleApplied applied = new ScheduleApplied();
        applied.setSchedule(schedule);
        applied.setUser(user);
        applied.setDateApplied(startDate);
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
    public String showApplyForm(@PathVariable Long id, Model model) {
        Schedule schedule = scheduleService.findById(id);
        model.addAttribute("schedule", schedule);
        return "schedule/apply";
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

        return "schedule/builder";
    }


    @PostMapping("/builder/save")
    public String saveBuilder(
            @RequestParam String payload,
            @RequestParam String name,
            @SessionAttribute("user") User user
    ) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        Map<String, List<Long>> weekMap = mapper.readValue(payload, new TypeReference<>() {});

        Schedule schedule = new Schedule();
        schedule.setName(name);
        schedule.setUser(user);
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


}
