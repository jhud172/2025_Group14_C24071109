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
import org.springframework.web.bind.annotation.SessionAttribute;

import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Config.DevModeProperties;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleOccurrence;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleOccurrenceRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserService;

@Controller
@RequestMapping("/exercise-log")
public class ExerciseLogController {

    @Autowired
    private ExerciseLogService service;

    @Autowired
    private ScheduleOccurrenceRepository occurrenceRepo;

    @Autowired
    private PdfService pdfService;

    private final UserService userService;
    private final uk.ac.cf._5.group14.BehaviourChangeGroupProject.Level.LevelService levelService;

    @Autowired
    public ExerciseLogController(UserService userService,
                                 uk.ac.cf._5.group14.BehaviourChangeGroupProject.Level.LevelService levelService) {
        this.userService = userService;
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
        ExerciseLogForm emptyExerciseLogForm = new ExerciseLogForm();
        emptyExerciseLogForm.setDate(LocalDate.now());
        model.addAttribute("exerciseLog", emptyExerciseLogForm);
        model.addAttribute("user", user);
        return "exercise-log/exercise-log-form";
    }

    // Save form
    @PostMapping
    public String save(@ModelAttribute("exerciseLog") ExerciseLogForm form) {
        if (devModeProperties.isDevMode()) {
            return "redirect:/dev-mode/unauthorized";
        }
        User user = authHelper.getAuthenticatedUser();
        service.saveLog(form, user);
        // Award points for logging exercise
        levelService.addPoints(user, 10);
        return "redirect:/calendar";
    }

    // List
    @GetMapping("/list")
    public String listLogsForUser(@SessionAttribute("user") User user, Model model) {
        if (devModeProperties.isDevMode()) {
            return "redirect:/dev-mode/unauthorized";
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
        model.addAttribute("log", service.getLogById(id));
        return "exercise-log/exercise-log-view";
    }

    @GetMapping("/add-occurrence")
    public String addForOccurrence(@RequestParam Long occId, Model model) {
        if (devModeProperties.isDevMode()) {
            return "redirect:/dev-mode/unauthorized";
        }
        ExerciseLogForm form = new ExerciseLogForm();
        ScheduleOccurrence occ = occurrenceRepo.findById(occId).orElse(null);
        if (occ != null) {
            form.setOccurrenceId(occId);
            form.setDate(occ.getDate());
            String name = (occ.getExercise() != null)
                    ? occ.getExercise().getName()
                    : occ.getCustomExercise().getName();
            form.setExerciseType(name);
        }
        model.addAttribute("exerciseLog", form);
        return "exercise-log/exercise-log-form";
    }

    @GetMapping("/add-calendar")
    public String addCalendarExercise(@RequestParam Long taskId, Model model) {
        if (devModeProperties.isDevMode()) {
            return "redirect:/dev-mode/unauthorized";
        }
        ExerciseLogForm form = new ExerciseLogForm();
        form.setCalendarTaskId(taskId);
        model.addAttribute("exerciseLog", form);
        return "exercise-log/exercise-log-form";
    }

    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportPdf(@SessionAttribute("user") User user) {
        if (devModeProperties.isDevMode()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<ExerciseLog> logs = service.getLogsByUser(user);
        byte[] pdf = pdfService.generateLogsPdf(logs);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=exercise_logs.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
