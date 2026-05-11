package uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerLibrary;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerClient.TrainerClientLink;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerClient.TrainerClientLinkService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.Role;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserService;

import java.util.*;

@Controller
@RequestMapping("/trainer/library")
public class TrainerLibraryController {

    private final AuthHelper authHelper;
    private final UserService userService;
    private final UserRepository userRepository;
    private final TrainerLibraryService trainerLibraryService;
    private final TrainerClientLinkService trainerClientLinkService;

    public TrainerLibraryController(AuthHelper authHelper,
                                   UserService userService,
                                   UserRepository userRepository,
                                   TrainerLibraryService trainerLibraryService,
                                   TrainerClientLinkService trainerClientLinkService) {
        this.authHelper = authHelper;
        this.userService = userService;
        this.userRepository = userRepository;
        this.trainerLibraryService = trainerLibraryService;
        this.trainerClientLinkService = trainerClientLinkService;
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

    private User currentTrainerOrThrow() {
        User user = currentUserOrThrow();
        if (user.getRole() != Role.TRAINER) {
            throw new AccessDeniedException("Not a trainer");
        }
        return user;
    }

    private Long currentTrainerIdOrThrow() {
        return currentTrainerOrThrow().getId();
    }

    private List<User> activeClientsForTrainer(Long trainerId) {
        List<TrainerClientLink> activeLinks = trainerClientLinkService.getActiveClientsForTrainer(trainerId);
        List<Long> clientIds = activeLinks.stream().map(TrainerClientLink::getClientUserId).distinct().toList();
        if (clientIds.isEmpty()) {
            return List.of();
        }
        return userRepository.findAllById(clientIds);
    }

    @GetMapping
    public ModelAndView overview() {
        Long trainerId = currentTrainerIdOrThrow();

        ModelAndView mav = new ModelAndView("trainer/library");
        mav.addObject("pageTitle", "Trainer Library");
        mav.addObject("exerciseCount", trainerLibraryService.listExercises(trainerId).size());
        mav.addObject("workoutCount", trainerLibraryService.listWorkouts(trainerId).size());
        mav.addObject("programmeCount", trainerLibraryService.listProgrammes(trainerId).size());
        return mav;
    }

    // -------------------------
    // Exercises
    // -------------------------

    @GetMapping("/exercises")
    public ModelAndView exercisesList() {
        Long trainerId = currentTrainerIdOrThrow();
        ModelAndView mav = new ModelAndView("trainer/exercises/list");
        mav.addObject("pageTitle", "Library · Exercises");
        mav.addObject("exercises", trainerLibraryService.listExercises(trainerId));
        return mav;
    }

    @GetMapping("/exercises/create")
    public ModelAndView exercisesCreate() {
        ModelAndView mav = new ModelAndView("trainer/exercises/create");
        mav.addObject("pageTitle", "Create Exercise");
        mav.addObject("form", new TrainerLibraryExerciseForm());
        return mav;
    }

    @PostMapping("/exercises/create")
    public ModelAndView exercisesCreateSubmit(@Valid @ModelAttribute("form") TrainerLibraryExerciseForm form,
                                              BindingResult bindingResult) {
        Long trainerId = currentTrainerIdOrThrow();
        if (bindingResult.hasErrors()) {
            ModelAndView mav = new ModelAndView("trainer/exercises/create");
            mav.addObject("pageTitle", "Create Exercise");
            return mav;
        }
        TrainerLibraryExercise created = trainerLibraryService.createExercise(trainerId, form);
        return new ModelAndView("redirect:/trainer/library/exercises/" + created.getId());
    }

    @GetMapping("/exercises/{id}")
    public ModelAndView exercisesView(@PathVariable Long id) {
        Long trainerId = currentTrainerIdOrThrow();
        TrainerLibraryExercise exercise;
        try {
            exercise = trainerLibraryService.getExerciseOwned(trainerId, id);
        } catch (AccessDeniedException ex) {
            return new ModelAndView("redirect:/access-denied");
        }

        ModelAndView mav = new ModelAndView("trainer/exercises/view");
        mav.addObject("pageTitle", "Exercise · " + exercise.getName());
        mav.addObject("exercise", exercise);
        mav.addObject("notes", trainerLibraryService.getExerciseNotes(id));

        mav.addObject("shareForm", defaultShareForm(TrainerLibraryTemplateType.EXERCISE, id, "/trainer/library/exercises/" + id));
        mav.addObject("activeClients", activeClientsForTrainer(trainerId));

        return mav;
    }

    @GetMapping("/exercises/{id}/edit")
    public ModelAndView exercisesEdit(@PathVariable Long id) {
        Long trainerId = currentTrainerIdOrThrow();
        TrainerLibraryExercise exercise;
        try {
            exercise = trainerLibraryService.getExerciseOwned(trainerId, id);
        } catch (AccessDeniedException ex) {
            return new ModelAndView("redirect:/access-denied");
        }

        TrainerLibraryExerciseForm form = new TrainerLibraryExerciseForm();
        form.setName(exercise.getName());
        form.setDescription(exercise.getDescription());
        form.setPrimaryMuscles(exercise.getPrimaryMuscles());
        form.setEquipment(exercise.getEquipment());
        form.setDifficulty(exercise.getDifficulty());
        form.setVideoUrl(exercise.getVideoUrl());
        form.setNotesText(String.join("\n", trainerLibraryService.getExerciseNotes(id).stream().map(TrainerLibraryExerciseNote::getNoteText).toList()));

        ModelAndView mav = new ModelAndView("trainer/exercises/edit");
        mav.addObject("pageTitle", "Edit Exercise");
        mav.addObject("exerciseId", id);
        mav.addObject("form", form);
        return mav;
    }

    @PostMapping("/exercises/{id}/edit")
    public ModelAndView exercisesEditSubmit(@PathVariable Long id,
                                            @Valid @ModelAttribute("form") TrainerLibraryExerciseForm form,
                                            BindingResult bindingResult) {
        Long trainerId = currentTrainerIdOrThrow();
        if (bindingResult.hasErrors()) {
            ModelAndView mav = new ModelAndView("trainer/exercises/edit");
            mav.addObject("pageTitle", "Edit Exercise");
            mav.addObject("exerciseId", id);
            return mav;
        }

        try {
            trainerLibraryService.updateExercise(trainerId, id, form);
        } catch (AccessDeniedException ex) {
            return new ModelAndView("redirect:/access-denied");
        }
        return new ModelAndView("redirect:/trainer/library/exercises/" + id);
    }

    @PostMapping("/exercises/{id}/delete")
    public ModelAndView exercisesDelete(@PathVariable Long id) {
        Long trainerId = currentTrainerIdOrThrow();
        try {
            trainerLibraryService.deleteExercise(trainerId, id);
        } catch (AccessDeniedException ex) {
            return new ModelAndView("redirect:/access-denied");
        }
        return new ModelAndView("redirect:/trainer/library/exercises");
    }

    // -------------------------
    // Workouts
    // -------------------------

    @GetMapping("/workouts")
    public ModelAndView workoutsList() {
        Long trainerId = currentTrainerIdOrThrow();
        ModelAndView mav = new ModelAndView("trainer/workouts/list");
        mav.addObject("pageTitle", "Library · Workouts");
        mav.addObject("workouts", trainerLibraryService.listWorkouts(trainerId));
        return mav;
    }

    @GetMapping("/workouts/create")
    public ModelAndView workoutsCreate() {
        ModelAndView mav = new ModelAndView("trainer/workouts/create");
        mav.addObject("pageTitle", "Create Workout");
        mav.addObject("form", new TrainerLibraryWorkoutTemplateForm());
        return mav;
    }

    @PostMapping("/workouts/create")
    public ModelAndView workoutsCreateSubmit(@Valid @ModelAttribute("form") TrainerLibraryWorkoutTemplateForm form,
                                             BindingResult bindingResult) {
        Long trainerId = currentTrainerIdOrThrow();
        if (bindingResult.hasErrors()) {
            ModelAndView mav = new ModelAndView("trainer/workouts/create");
            mav.addObject("pageTitle", "Create Workout");
            return mav;
        }
        TrainerLibraryWorkoutTemplate created = trainerLibraryService.createWorkout(trainerId, form);
        return new ModelAndView("redirect:/trainer/library/workouts/" + created.getId());
    }

    @GetMapping("/workouts/{id}")
    public ModelAndView workoutsView(@PathVariable Long id) {
        Long trainerId = currentTrainerIdOrThrow();
        TrainerLibraryWorkoutTemplate workout;
        try {
            workout = trainerLibraryService.getWorkoutOwned(trainerId, id);
        } catch (AccessDeniedException ex) {
            return new ModelAndView("redirect:/access-denied");
        }

        List<TrainerLibraryWorkoutItem> items = trainerLibraryService.getWorkoutItems(id);
        List<TrainerLibraryWorkoutNote> notes = trainerLibraryService.getWorkoutNotes(id);

        Set<Long> exerciseIds = new HashSet<>();
        for (TrainerLibraryWorkoutItem item : items) {
            exerciseIds.add(item.getExerciseId());
        }
        Map<Long, TrainerLibraryExercise> exercisesById = new HashMap<>();
        if (!exerciseIds.isEmpty()) {
            for (TrainerLibraryExercise ex : trainerLibraryService.listExercises(trainerId)) {
                if (exerciseIds.contains(ex.getId())) {
                    exercisesById.put(ex.getId(), ex);
                }
            }
        }

        ModelAndView mav = new ModelAndView("trainer/workouts/view");
        mav.addObject("pageTitle", "Workout · " + workout.getTitle());
        mav.addObject("workout", workout);
        mav.addObject("items", items);
        mav.addObject("notes", notes);
        mav.addObject("exercisesById", exercisesById);

        mav.addObject("itemForm", new TrainerLibraryWorkoutItemForm());
        mav.addObject("exercises", trainerLibraryService.listExercises(trainerId));

        mav.addObject("shareForm", defaultShareForm(TrainerLibraryTemplateType.WORKOUT, id, "/trainer/library/workouts/" + id));
        mav.addObject("activeClients", activeClientsForTrainer(trainerId));

        return mav;
    }

    @GetMapping("/workouts/{id}/edit")
    public ModelAndView workoutsEdit(@PathVariable Long id) {
        Long trainerId = currentTrainerIdOrThrow();
        TrainerLibraryWorkoutTemplate workout;
        try {
            workout = trainerLibraryService.getWorkoutOwned(trainerId, id);
        } catch (AccessDeniedException ex) {
            return new ModelAndView("redirect:/access-denied");
        }

        TrainerLibraryWorkoutTemplateForm form = new TrainerLibraryWorkoutTemplateForm();
        form.setTitle(workout.getTitle());
        form.setSummary(workout.getSummary());
        form.setNotesText(String.join("\n", trainerLibraryService.getWorkoutNotes(id).stream().map(TrainerLibraryWorkoutNote::getNoteText).toList()));

        ModelAndView mav = new ModelAndView("trainer/workouts/edit");
        mav.addObject("pageTitle", "Edit Workout");
        mav.addObject("workoutId", id);
        mav.addObject("form", form);
        return mav;
    }

    @PostMapping("/workouts/{id}/edit")
    public ModelAndView workoutsEditSubmit(@PathVariable Long id,
                                           @Valid @ModelAttribute("form") TrainerLibraryWorkoutTemplateForm form,
                                           BindingResult bindingResult) {
        Long trainerId = currentTrainerIdOrThrow();
        if (bindingResult.hasErrors()) {
            ModelAndView mav = new ModelAndView("trainer/workouts/edit");
            mav.addObject("pageTitle", "Edit Workout");
            mav.addObject("workoutId", id);
            return mav;
        }
        try {
            trainerLibraryService.updateWorkout(trainerId, id, form);
        } catch (AccessDeniedException ex) {
            return new ModelAndView("redirect:/access-denied");
        }
        return new ModelAndView("redirect:/trainer/library/workouts/" + id);
    }

    @PostMapping("/workouts/{id}/items")
    public ModelAndView workoutsAddItem(@PathVariable Long id,
                                        @Valid @ModelAttribute("itemForm") TrainerLibraryWorkoutItemForm itemForm,
                                        BindingResult bindingResult,
                                        RedirectAttributes redirectAttributes) {
        Long trainerId = currentTrainerIdOrThrow();
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please fix the workout item fields.");
            return new ModelAndView("redirect:/trainer/library/workouts/" + id);
        }
        try {
            trainerLibraryService.addWorkoutItem(trainerId, id, itemForm);
        } catch (AccessDeniedException ex) {
            return new ModelAndView("redirect:/access-denied");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid exercise.");
        }
        return new ModelAndView("redirect:/trainer/library/workouts/" + id);
    }

    @PostMapping("/workouts/{workoutId}/items/{itemId}/delete")
    public ModelAndView workoutsDeleteItem(@PathVariable Long workoutId,
                                           @PathVariable Long itemId) {
        Long trainerId = currentTrainerIdOrThrow();
        try {
            trainerLibraryService.deleteWorkoutItem(trainerId, workoutId, itemId);
        } catch (AccessDeniedException ex) {
            return new ModelAndView("redirect:/access-denied");
        }
        return new ModelAndView("redirect:/trainer/library/workouts/" + workoutId);
    }

    @PostMapping("/workouts/{id}/delete")
    public ModelAndView workoutsDelete(@PathVariable Long id) {
        Long trainerId = currentTrainerIdOrThrow();
        try {
            trainerLibraryService.deleteWorkout(trainerId, id);
        } catch (AccessDeniedException ex) {
            return new ModelAndView("redirect:/access-denied");
        }
        return new ModelAndView("redirect:/trainer/library/workouts");
    }

    // -------------------------
    // Programmes
    // -------------------------

    @GetMapping("/programmes")
    public ModelAndView programmesList() {
        Long trainerId = currentTrainerIdOrThrow();
        ModelAndView mav = new ModelAndView("trainer/programmes/list");
        mav.addObject("pageTitle", "Library · Programmes");
        mav.addObject("programmes", trainerLibraryService.listProgrammes(trainerId));
        return mav;
    }

    @GetMapping("/programmes/create")
    public ModelAndView programmesCreate() {
        ModelAndView mav = new ModelAndView("trainer/programmes/create");
        mav.addObject("pageTitle", "Create Programme");
        mav.addObject("form", new TrainerLibraryProgrammeTemplateForm());
        return mav;
    }

    @PostMapping("/programmes/create")
    public ModelAndView programmesCreateSubmit(@Valid @ModelAttribute("form") TrainerLibraryProgrammeTemplateForm form,
                                               BindingResult bindingResult) {
        Long trainerId = currentTrainerIdOrThrow();
        if (bindingResult.hasErrors()) {
            ModelAndView mav = new ModelAndView("trainer/programmes/create");
            mav.addObject("pageTitle", "Create Programme");
            return mav;
        }
        TrainerLibraryProgrammeTemplate created = trainerLibraryService.createProgramme(trainerId, form);
        return new ModelAndView("redirect:/trainer/library/programmes/" + created.getId());
    }

    @GetMapping("/programmes/{id}")
    public ModelAndView programmesView(@PathVariable Long id) {
        Long trainerId = currentTrainerIdOrThrow();
        TrainerLibraryProgrammeTemplate programme;
        try {
            programme = trainerLibraryService.getProgrammeOwned(trainerId, id);
        } catch (AccessDeniedException ex) {
            return new ModelAndView("redirect:/access-denied");
        }

        List<TrainerLibraryProgrammeDay> days = trainerLibraryService.getProgrammeDays(id);
        List<TrainerLibraryProgrammeNote> notes = trainerLibraryService.getProgrammeNotes(id);

        Set<Long> workoutIds = new HashSet<>();
        for (TrainerLibraryProgrammeDay d : days) {
            workoutIds.add(d.getWorkoutId());
        }
        Map<Long, TrainerLibraryWorkoutTemplate> workoutsById = new HashMap<>();
        if (!workoutIds.isEmpty()) {
            for (TrainerLibraryWorkoutTemplate w : trainerLibraryService.listWorkouts(trainerId)) {
                if (workoutIds.contains(w.getId())) {
                    workoutsById.put(w.getId(), w);
                }
            }
        }

        ModelAndView mav = new ModelAndView("trainer/programmes/view");
        mav.addObject("pageTitle", "Programme · " + programme.getTitle());
        mav.addObject("programme", programme);
        mav.addObject("days", days);
        mav.addObject("notes", notes);
        mav.addObject("workoutsById", workoutsById);

        mav.addObject("dayForm", new TrainerLibraryProgrammeDayForm());
        mav.addObject("workouts", trainerLibraryService.listWorkouts(trainerId));

        mav.addObject("shareForm", defaultShareForm(TrainerLibraryTemplateType.PROGRAMME, id, "/trainer/library/programmes/" + id));
        mav.addObject("activeClients", activeClientsForTrainer(trainerId));

        return mav;
    }

    @GetMapping("/programmes/{id}/edit")
    public ModelAndView programmesEdit(@PathVariable Long id) {
        Long trainerId = currentTrainerIdOrThrow();
        TrainerLibraryProgrammeTemplate programme;
        try {
            programme = trainerLibraryService.getProgrammeOwned(trainerId, id);
        } catch (AccessDeniedException ex) {
            return new ModelAndView("redirect:/access-denied");
        }

        TrainerLibraryProgrammeTemplateForm form = new TrainerLibraryProgrammeTemplateForm();
        form.setTitle(programme.getTitle());
        form.setWeeks(programme.getWeeks());
        form.setNotesText(String.join("\n", trainerLibraryService.getProgrammeNotes(id).stream().map(TrainerLibraryProgrammeNote::getNoteText).toList()));

        ModelAndView mav = new ModelAndView("trainer/programmes/edit");
        mav.addObject("pageTitle", "Edit Programme");
        mav.addObject("programmeId", id);
        mav.addObject("form", form);
        return mav;
    }

    @PostMapping("/programmes/{id}/edit")
    public ModelAndView programmesEditSubmit(@PathVariable Long id,
                                             @Valid @ModelAttribute("form") TrainerLibraryProgrammeTemplateForm form,
                                             BindingResult bindingResult) {
        Long trainerId = currentTrainerIdOrThrow();
        if (bindingResult.hasErrors()) {
            ModelAndView mav = new ModelAndView("trainer/programmes/edit");
            mav.addObject("pageTitle", "Edit Programme");
            mav.addObject("programmeId", id);
            return mav;
        }
        try {
            trainerLibraryService.updateProgramme(trainerId, id, form);
        } catch (AccessDeniedException ex) {
            return new ModelAndView("redirect:/access-denied");
        }
        return new ModelAndView("redirect:/trainer/library/programmes/" + id);
    }

    @PostMapping("/programmes/{id}/days")
    public ModelAndView programmesAddDay(@PathVariable Long id,
                                         @Valid @ModelAttribute("dayForm") TrainerLibraryProgrammeDayForm dayForm,
                                         BindingResult bindingResult,
                                         RedirectAttributes redirectAttributes) {
        Long trainerId = currentTrainerIdOrThrow();
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please fix the programme day fields.");
            return new ModelAndView("redirect:/trainer/library/programmes/" + id);
        }
        try {
            trainerLibraryService.addProgrammeDay(trainerId, id, dayForm);
        } catch (AccessDeniedException ex) {
            return new ModelAndView("redirect:/access-denied");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid workout.");
        }
        return new ModelAndView("redirect:/trainer/library/programmes/" + id);
    }

    @PostMapping("/programmes/{programmeId}/days/{dayId}/delete")
    public ModelAndView programmesDeleteDay(@PathVariable Long programmeId,
                                            @PathVariable Long dayId) {
        Long trainerId = currentTrainerIdOrThrow();
        try {
            trainerLibraryService.deleteProgrammeDay(trainerId, programmeId, dayId);
        } catch (AccessDeniedException ex) {
            return new ModelAndView("redirect:/access-denied");
        }
        return new ModelAndView("redirect:/trainer/library/programmes/" + programmeId);
    }

    @PostMapping("/programmes/{id}/delete")
    public ModelAndView programmesDelete(@PathVariable Long id) {
        Long trainerId = currentTrainerIdOrThrow();
        try {
            trainerLibraryService.deleteProgramme(trainerId, id);
        } catch (AccessDeniedException ex) {
            return new ModelAndView("redirect:/access-denied");
        }
        return new ModelAndView("redirect:/trainer/library/programmes");
    }

    // -------------------------
    // Share
    // -------------------------

    @PostMapping("/share")
    public ModelAndView share(@Valid @ModelAttribute("shareForm") TrainerLibraryShareForm form,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) {
        User trainer = currentTrainerOrThrow();
        Long trainerId = trainer.getId();

        String returnUrl = (form.getReturnUrl() != null && form.getReturnUrl().startsWith("/"))
                ? form.getReturnUrl()
                : "/trainer/library";

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please select a client.");
            return new ModelAndView("redirect:" + returnUrl);
        }

        if (!trainer.isTrainerVerified()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Trainer verification required to share programmes publicly.");
            return new ModelAndView("redirect:" + returnUrl);
        }

        try {
            trainerLibraryService.shareTemplate(trainerId, form);
            redirectAttributes.addFlashAttribute("successMessage", "Shared successfully.");
        } catch (AccessDeniedException ex) {
            return new ModelAndView("redirect:/access-denied");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not share.");
        }

        return new ModelAndView("redirect:" + returnUrl);
    }

    private TrainerLibraryShareForm defaultShareForm(TrainerLibraryTemplateType type, Long templateId, String returnUrl) {
        TrainerLibraryShareForm form = new TrainerLibraryShareForm();
        form.setTemplateType(type);
        form.setTemplateId(templateId);
        form.setReturnUrl(returnUrl);
        return form;
    }
}
