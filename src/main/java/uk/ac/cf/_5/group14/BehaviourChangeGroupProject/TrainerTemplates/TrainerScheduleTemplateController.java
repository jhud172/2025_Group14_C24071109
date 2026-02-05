package uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerTemplates;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CustomExerciseData.CustomExerciseRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseData.ExerciseRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerClient.TrainerClientLink;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerClient.TrainerClientLinkService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.Role;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Checkins.WeeklyCheckInService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/trainer/templates")
public class TrainerScheduleTemplateController {

    private final AuthHelper authHelper;
    private final UserService userService;
    private final TrainerScheduleTemplateService templateService;
    private final TrainerClientLinkService trainerClientLinkService;
    private final ExerciseRepository exerciseRepository;
    private final CustomExerciseRepository customExerciseRepository;
    private final WeeklyCheckInService weeklyCheckInService;

    public TrainerScheduleTemplateController(AuthHelper authHelper,
                                             UserService userService,
                                             TrainerScheduleTemplateService templateService,
                                             TrainerClientLinkService trainerClientLinkService,
                                             ExerciseRepository exerciseRepository,
                                             CustomExerciseRepository customExerciseRepository,
                                             WeeklyCheckInService weeklyCheckInService) {
        this.authHelper = authHelper;
        this.userService = userService;
        this.templateService = templateService;
        this.trainerClientLinkService = trainerClientLinkService;
        this.exerciseRepository = exerciseRepository;
        this.customExerciseRepository = customExerciseRepository;
        this.weeklyCheckInService = weeklyCheckInService;
    }

    private User currentUserOrThrow() {
        User sessionUser = authHelper.getAuthenticatedUser();
        if (sessionUser != null) {
            return sessionUser;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Not authenticated");
        }
        User user = userService.findByUsername(auth.getName());
        if (user == null) {
            throw new AccessDeniedException("User not found");
        }
        return user;
    }

    @GetMapping
    public ModelAndView index() {
        User trainer = currentUserOrThrow();
        if (trainer.getRole() != Role.TRAINER) {
            return new ModelAndView("redirect:/access-denied");
        }
        ModelAndView mav = new ModelAndView("trainer/templates/index");
        mav.addObject("pageTitle", "Trainer Templates");
        mav.addObject("templates", templateService.listForTrainer(trainer));
        return mav;
    }

    @GetMapping("/create")
    public ModelAndView createForm() {
        User trainer = currentUserOrThrow();
        if (trainer.getRole() != Role.TRAINER) {
            return new ModelAndView("redirect:/access-denied");
        }
        ModelAndView mav = new ModelAndView("trainer/templates/edit");
        mav.addObject("pageTitle", "Create Template");
        mav.addObject("template", new TrainerScheduleTemplate());
        mav.addObject("entries", List.of());
        mav.addObject("questions", List.of());
        mav.addObject("exercises", exerciseRepository.findAll());
        mav.addObject("customExercises", customExerciseRepository.findByUserIdOrderByNameAsc(trainer.getId()));
        return mav;
    }

    @PostMapping("/create")
    public ModelAndView create(@RequestParam String name,
                               @RequestParam(required = false) String description,
                               @RequestParam(required = false) String tags) {
        User trainer = currentUserOrThrow();
        if (trainer.getRole() != Role.TRAINER) {
            return new ModelAndView("redirect:/access-denied");
        }
        TrainerScheduleTemplate template = templateService.createTemplate(trainer, name, description, tags);
        return new ModelAndView("redirect:/trainer/templates/" + template.getId() + "/edit");
    }

    @GetMapping("/{id}/edit")
    public ModelAndView edit(@PathVariable Long id) {
        User trainer = currentUserOrThrow();
        if (trainer.getRole() != Role.TRAINER) {
            return new ModelAndView("redirect:/access-denied");
        }
        TrainerScheduleTemplate template = templateService.getForTrainer(trainer, id);
        ModelAndView mav = new ModelAndView("trainer/templates/edit");
        mav.addObject("pageTitle", "Edit Template");
        mav.addObject("template", template);
        mav.addObject("entries", template.getEntries());
        mav.addObject("questions", weeklyCheckInService.listQuestions(template.getId()));
        mav.addObject("exercises", exerciseRepository.findAll());
        mav.addObject("customExercises", customExerciseRepository.findByUserIdOrderByNameAsc(trainer.getId()));
        return mav;
    }

    @PostMapping("/{id}/edit")
    public ModelAndView update(@PathVariable Long id,
                               @RequestParam String name,
                               @RequestParam(required = false) String description,
                               @RequestParam(required = false) String tags,
                               @RequestParam(defaultValue = "false") boolean archived) {
        User trainer = currentUserOrThrow();
        if (trainer.getRole() != Role.TRAINER) {
            return new ModelAndView("redirect:/access-denied");
        }
        templateService.updateTemplate(trainer, id, name, description, tags, archived);
        return new ModelAndView("redirect:/trainer/templates/" + id + "/edit");
    }

    @PostMapping("/{id}/clone")
    public ModelAndView cloneTemplate(@PathVariable Long id) {
        User trainer = currentUserOrThrow();
        if (trainer.getRole() != Role.TRAINER) {
            return new ModelAndView("redirect:/access-denied");
        }
        TrainerScheduleTemplate clone = templateService.cloneTemplate(trainer, id);
        return new ModelAndView("redirect:/trainer/templates/" + clone.getId() + "/edit");
    }

    @PostMapping("/{id}/entries")
    public ModelAndView addEntry(@PathVariable Long id,
                                 @RequestParam int dayOfWeek,
                                 @RequestParam(required = false) String timeWindowStart,
                                 @RequestParam(required = false) String timeWindowEnd,
                                 @RequestParam TrainerScheduleTemplateEntryType type,
                                 @RequestParam String title,
                                 @RequestParam(required = false) String defaultsJson,
                                 @RequestParam(required = false) String intensityLabel,
                                 @RequestParam(required = false) Integer intensityLevel,
                                 @RequestParam(required = false) Long exerciseId,
                                 @RequestParam(required = false) Long customExerciseId) {
        User trainer = currentUserOrThrow();
        if (trainer.getRole() != Role.TRAINER) {
            return new ModelAndView("redirect:/access-denied");
        }
        TrainerScheduleTemplateEntry entry = new TrainerScheduleTemplateEntry();
        entry.setDayOfWeek(dayOfWeek);
        entry.setTimeWindowStart(parseTime(timeWindowStart));
        entry.setTimeWindowEnd(parseTime(timeWindowEnd));
        entry.setType(type);
        entry.setTitle(title.trim());
        entry.setDefaultsJson(trimToNull(defaultsJson));
        entry.setIntensityLabel(trimToNull(intensityLabel));
        entry.setIntensityLevel(intensityLevel);
        if (exerciseId != null) {
            exerciseRepository.findById(exerciseId).ifPresent(entry::setExercise);
        }
        if (customExerciseId != null) {
            customExerciseRepository.findByIdAndUserId(customExerciseId, trainer.getId())
                    .ifPresent(entry::setCustomExercise);
        }
        templateService.addEntry(trainer, id, entry);
        return new ModelAndView("redirect:/trainer/templates/" + id + "/edit");
    }

    @PostMapping("/{id}/entries/{entryId}/delete")
    public ModelAndView deleteEntry(@PathVariable Long id, @PathVariable Long entryId) {
        User trainer = currentUserOrThrow();
        if (trainer.getRole() != Role.TRAINER) {
            return new ModelAndView("redirect:/access-denied");
        }
        templateService.deleteEntry(trainer, id, entryId);
        return new ModelAndView("redirect:/trainer/templates/" + id + "/edit");
    }

    @PostMapping("/{id}/questions")
    public ModelAndView addQuestion(@PathVariable Long id,
                                    @RequestParam String prompt,
                                    @RequestParam(defaultValue = "true") boolean required) {
        User trainer = currentUserOrThrow();
        if (trainer.getRole() != Role.TRAINER) {
            return new ModelAndView("redirect:/access-denied");
        }
        weeklyCheckInService.addQuestion(trainer, id, prompt, required);
        return new ModelAndView("redirect:/trainer/templates/" + id + "/edit");
    }

    @PostMapping("/{id}/questions/{questionId}/delete")
    public ModelAndView deleteQuestion(@PathVariable Long id, @PathVariable Long questionId) {
        User trainer = currentUserOrThrow();
        if (trainer.getRole() != Role.TRAINER) {
            return new ModelAndView("redirect:/access-denied");
        }
        weeklyCheckInService.deleteQuestion(trainer, id, questionId);
        return new ModelAndView("redirect:/trainer/templates/" + id + "/edit");
    }

    @GetMapping("/{id}/apply")
    public ModelAndView applyForm(@PathVariable Long id,
                                  @RequestParam(required = false) Long clientId,
                                  @RequestParam(required = false) String start,
                                  @RequestParam(required = false) String end,
                                  @RequestParam(required = false) String idempotent) {
        User trainer = currentUserOrThrow();
        if (trainer.getRole() != Role.TRAINER) {
            return new ModelAndView("redirect:/access-denied");
        }
        TrainerScheduleTemplate template = templateService.getForTrainer(trainer, id);
        List<TrainerClientLink> activeLinks = trainerClientLinkService.getActiveClientsForTrainer(trainer.getId());
        ModelAndView mav = new ModelAndView("trainer/templates/apply");
        mav.addObject("pageTitle", "Apply Template");
        mav.addObject("template", template);
        mav.addObject("activeLinks", activeLinks);
        mav.addObject("selectedClientId", clientId);
        boolean idempotentFlag = idempotent == null || Boolean.parseBoolean(idempotent);
        mav.addObject("idempotent", idempotentFlag);

        if (clientId != null && start != null && end != null) {
            LocalDate startDate = LocalDate.parse(start);
            LocalDate endDate = LocalDate.parse(end);
            mav.addObject("preview", templateService.previewApply(trainer, id, clientId, startDate, endDate, idempotentFlag));
            mav.addObject("start", start);
            mav.addObject("end", end);
        }
        return mav;
    }

    @PostMapping("/{id}/apply")
    public ModelAndView applyTemplate(@PathVariable Long id,
                                      @RequestParam Long clientId,
                                      @RequestParam String start,
                                      @RequestParam String end,
                                      @RequestParam(required = false, defaultValue = "false") String idempotent) {
        User trainer = currentUserOrThrow();
        if (trainer.getRole() != Role.TRAINER) {
            return new ModelAndView("redirect:/access-denied");
        }
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        boolean idempotentFlag = Boolean.parseBoolean(idempotent);
        templateService.applyTemplate(trainer, id, clientId, startDate, endDate, idempotentFlag);
        return new ModelAndView("redirect:/trainer/templates/" + id + "/apply?clientId=" + clientId + "&start=" + start + "&end=" + end + "&idempotent=" + idempotentFlag);
    }

    private LocalTime parseTime(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return LocalTime.parse(value);
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isBlank() ? null : trimmed;
    }
}
