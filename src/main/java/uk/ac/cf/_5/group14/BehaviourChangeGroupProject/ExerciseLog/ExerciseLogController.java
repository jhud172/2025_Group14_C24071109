package uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseLog;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTask;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTaskRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Config.DevModeProperties;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleOccurrence;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleOccurrenceRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

@Controller
@RequestMapping("/exercise-log")
public class ExerciseLogController {

    @Autowired
    private ExerciseLogService service;

    @Autowired
    private ScheduleOccurrenceRepository occurrenceRepo;

    @Autowired
    private CalendarTaskRepository calendarTaskRepository;

    @Autowired
    private PdfService pdfService;

    private final uk.ac.cf._5.group14.BehaviourChangeGroupProject.Level.LevelService levelService;

    @Autowired
    public ExerciseLogController(uk.ac.cf._5.group14.BehaviourChangeGroupProject.Level.LevelService levelService) {
        this.levelService = levelService;
    }

    @Autowired
    private AuthHelper authHelper;

    @Autowired
    private DevModeProperties devModeProperties;

    // Show form
    @GetMapping
    public String showForm(Model model) {
        if (devModeProperties.isDevMode()) {
            return "redirect:/dev-mode/unauthorized";
        }
        User user = authHelper.getAuthenticatedUser();
        if (user == null) {
            return "redirect:/login";
        }
        ExerciseLogForm emptyExerciseLogForm = new ExerciseLogForm();
        emptyExerciseLogForm.setDate(LocalDate.now());
        emptyExerciseLogForm.setMoodBefore(2);
        emptyExerciseLogForm.setMoodAfter(3);
        emptyExerciseLogForm.setConfidence(3);
        model.addAttribute("exerciseLog", emptyExerciseLogForm);
        model.addAttribute("user", user);
        model.addAttribute("formAction", "/exercise-log");
        model.addAttribute("editing", false);
        return "exercise-log/exercise-log-form";
    }

    // Save form
    @PostMapping
    public String save(@ModelAttribute("exerciseLog") ExerciseLogForm form) {
        if (devModeProperties.isDevMode()) {
            return "redirect:/dev-mode/unauthorized";
        }
        User user = authHelper.getAuthenticatedUser();
        if (user == null) {
            return "redirect:/login";
        }
        service.saveLog(form, user);
        // Award points for logging exercise
        levelService.addPoints(user, 10);
        return "redirect:/calendar";
    }

    // List
    @GetMapping("/list")
    public String listLogsForUser(Model model) {
        if (devModeProperties.isDevMode()) {
            return "redirect:/dev-mode/unauthorized";
        }
        User user = authHelper.getAuthenticatedUser();
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("logs", service.getLogsForUser(user));
        return "exercise-log/exercise-log-list";
    }

    // View single
    @GetMapping("/view/{id}")
    public String viewSingle(@PathVariable Long id, Model model) {
        if (devModeProperties.isDevMode()) {
            return "redirect:/dev-mode/unauthorized";
        }
        User user = authHelper.getAuthenticatedUser();
        if (user == null) {
            return "redirect:/login";
        }
        ExerciseLog log = service.getLogByIdForUser(id, user);
        if (log == null) {
            return "redirect:/exercise-log/list";
        }
        model.addAttribute("log", log);
        return "exercise-log/exercise-log-view";
    }

    @GetMapping("/edit/{id}")
    public String editLog(@PathVariable Long id, Model model) {
        if (devModeProperties.isDevMode()) {
            return "redirect:/dev-mode/unauthorized";
        }
        User user = authHelper.getAuthenticatedUser();
        if (user == null) {
            return "redirect:/login";
        }
        ExerciseLog log = service.getLogByIdForUser(id, user);
        if (log == null) {
            return "redirect:/exercise-log/list";
        }

        ExerciseLogForm form = new ExerciseLogForm();
        form.setDate(log.getDate());
        form.setMoodBefore(log.getMoodBefore());
        form.setMoodAfter(log.getMoodAfter());
        form.setConfidence(log.getConfidence());
        form.setComments(log.getComments());
        form.setDurationMinutes(log.getDurationMinutes());
        if (log.getOccurrence() != null) {
            form.setOccurrenceId(log.getOccurrence().getId());
        }
        if (log.getCalendarTask() != null) {
            form.setCalendarTaskId(log.getCalendarTask().getId());
        }

        model.addAttribute("exerciseLog", form);
        model.addAttribute("formAction", "/exercise-log/edit/" + id);
        model.addAttribute("editing", true);
        return "exercise-log/exercise-log-form";
    }

    @PostMapping("/edit/{id}")
    public String updateLog(@PathVariable Long id, @ModelAttribute("exerciseLog") ExerciseLogForm form) {
        if (devModeProperties.isDevMode()) {
            return "redirect:/dev-mode/unauthorized";
        }
        User user = authHelper.getAuthenticatedUser();
        if (user == null) {
            return "redirect:/login";
        }
        service.updateLog(id, form, user);
        return "redirect:/exercise-log/view/" + id;
    }

    @GetMapping("/add-occurrence")
    public String addForOccurrence(@RequestParam Long occId, Model model) {
        if (devModeProperties.isDevMode()) {
            return "redirect:/dev-mode/unauthorized";
        }
        User user = authHelper.getAuthenticatedUser();
        if (user == null) {
            return "redirect:/login";
        }
        ExerciseLogForm form = new ExerciseLogForm();
        ScheduleOccurrence occ = occurrenceRepo.findByIdAndUserId(occId, user.getId()).orElse(null);
        if (occ != null) {
            form.setOccurrenceId(occId);
            form.setDate(occ.getDate());
            form.setMoodBefore(2);
            form.setMoodAfter(3);
            form.setConfidence(3);
            String name = (occ.getExercise() != null)
                    ? occ.getExercise().getName()
                    : (occ.getCustomExercise() != null ? occ.getCustomExercise().getName() : "Workout");
            form.setExerciseType(name);
        }
        model.addAttribute("exerciseLog", form);
        model.addAttribute("formAction", "/exercise-log");
        model.addAttribute("editing", false);
        return "exercise-log/exercise-log-form";
    }

    @GetMapping("/add-calendar")
    public String addCalendarExercise(@RequestParam Long taskId, Model model) {
        if (devModeProperties.isDevMode()) {
            return "redirect:/dev-mode/unauthorized";
        }
        User user = authHelper.getAuthenticatedUser();
        if (user == null) {
            return "redirect:/login";
        }
        ExerciseLogForm form = new ExerciseLogForm();
        CalendarTask task = calendarTaskRepository.findByIdAndUser(taskId, user);
        if (task == null) {
            return "redirect:/calendar";
        }
        form.setCalendarTaskId(taskId);
        form.setDate(task.getDate());
        form.setMoodBefore(2);
        form.setMoodAfter(3);
        form.setConfidence(3);
        model.addAttribute("exerciseLog", form);
        model.addAttribute("formAction", "/exercise-log");
        model.addAttribute("editing", false);
        return "exercise-log/exercise-log-form";
    }

    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportPdf() {
        if (devModeProperties.isDevMode()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        User user = authHelper.getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<ExerciseLog> logs = service.getLogsByUser(user);
        byte[] pdf = pdfService.generateLogsPdf(logs);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=exercise_logs.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
