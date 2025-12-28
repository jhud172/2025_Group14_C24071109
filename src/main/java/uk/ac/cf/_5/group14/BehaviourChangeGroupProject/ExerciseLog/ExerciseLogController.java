package uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseLog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleOccurrence;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleOccurrenceRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserService;

import java.time.LocalDate;
import java.util.List;

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

    public ExerciseLogController(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    private AuthHelper authHelper;

    // Show form
    @GetMapping
    public String showForm(Model model) {
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
        User user = authHelper.getAuthenticatedUser();
        service.saveLog(form, user);
        return "redirect:/calendar";
    }

    // List
    @GetMapping("/list")
    public String listLogsForUser(@SessionAttribute("user") User user, Model model) {
        model.addAttribute("logs", service.getLogsForUser(user));
        return "exercise-log/exercise-log-list";
    }

    // View single
    @GetMapping("/view/{id}")
    public String viewSingle(@PathVariable Long id, Model model) {
        model.addAttribute("log", service.getLogById(id));
        return "exercise-log/exercise-log-view";
    }

    @GetMapping("/add-occurrence")
    public String addForOccurrence(@RequestParam Long occId, Model model) {
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
        ExerciseLogForm form = new ExerciseLogForm();
        form.setCalendarTaskId(taskId);
        model.addAttribute("exerciseLog", form);
        return "exercise-log/exercise-log-form";
    }

    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportPdf(@SessionAttribute("user") User user) {
        List<ExerciseLog> logs = service.getLogsByUser(user);
        byte[] pdf = pdfService.generateLogsPdf(logs);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=exercise_logs.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
