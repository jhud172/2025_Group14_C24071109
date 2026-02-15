package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Workouts;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CustomExerciseData.CustomExercise;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CustomExerciseData.CustomExerciseService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseData.Exercise;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseData.ExerciseService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Goals.GoalLinkService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Goals.GoalLinkSource;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Goals.GoalService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Goals.GoalStatus;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.UserSettings.UserSettings;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.UserSettings.UserSettingsService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/workouts")
public class WorkoutBuilderController {

    private final WorkoutBuilderService workoutBuilderService;
    private final ExerciseService exerciseService;
    private final CustomExerciseService customExerciseService;
    private final AuthHelper authHelper;
    private final GoalService goalService;
    private final GoalLinkService goalLinkService;
    private final WorkoutPerformanceService workoutPerformanceService;
    private final UserSettingsService userSettingsService;

    public WorkoutBuilderController(WorkoutBuilderService workoutBuilderService,
                                    ExerciseService exerciseService,
                                    CustomExerciseService customExerciseService,
                                    AuthHelper authHelper,
                                    GoalService goalService,
                                    GoalLinkService goalLinkService,
                                    WorkoutPerformanceService workoutPerformanceService,
                                    UserSettingsService userSettingsService) {
        this.workoutBuilderService = workoutBuilderService;
        this.exerciseService = exerciseService;
        this.customExerciseService = customExerciseService;
        this.authHelper = authHelper;
        this.goalService = goalService;
        this.goalLinkService = goalLinkService;
        this.workoutPerformanceService = workoutPerformanceService;
        this.userSettingsService = userSettingsService;
    }

    @GetMapping
    public String index(Model model) {
        User user = authHelper.getAuthenticatedUser();
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("templates", workoutBuilderService.listTemplates(user));
        
        // Add exercises and custom exercises for the builder mode
        model.addAttribute("exercises", exerciseService.getAllExercises());
        model.addAttribute("customExercises", customExerciseService.getCustomExercisesByUser(user.getId()));
        
        return "workouts/index";
    }

    @PostMapping
    public String create(@RequestParam String name,
                         @RequestParam(required = false) String description) {
        User user = authHelper.getAuthenticatedUser();
        if (user == null) {
            return "redirect:/login";
        }
        WorkoutTemplateForm form = new WorkoutTemplateForm();
        form.setName(name);
        form.setDescription(description);
        WorkoutTemplate created = workoutBuilderService.createTemplate(user, form);
        return "redirect:/workouts/" + created.getId() + "/edit";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        User user = authHelper.getAuthenticatedUser();
        if (user == null) {
            return "redirect:/login";
        }
        WorkoutTemplate template = workoutBuilderService.getTemplate(user, id);
        WorkoutTemplateForm form = toForm(template);
        model.addAttribute("template", template);
        model.addAttribute("form", form);
        hydrateExerciseOptions(model, user);
        applySmartDefaults(model, user);
        return "workouts/edit";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, @ModelAttribute("form") WorkoutTemplateForm form, Model model) {
        User user = authHelper.getAuthenticatedUser();
        if (user == null) {
            return "redirect:/login";
        }
        Map<Long, String> exerciseNames = exerciseNameMap();
        Map<Long, String> customExerciseNames = customExerciseNameMap(user);
        resolveExercises(form, exerciseNames, customExerciseNames);

        workoutBuilderService.updateTemplate(user, id, form);
        return "redirect:/workouts/" + id + "/edit";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        User user = authHelper.getAuthenticatedUser();
        if (user == null) {
            return "redirect:/login";
        }
        workoutBuilderService.deleteTemplate(user, id);
        return "redirect:/workouts";
    }

    @GetMapping("/{id}/start")
    public String start(@PathVariable Long id, Model model) {
        User user = authHelper.getAuthenticatedUser();
        if (user == null) {
            return "redirect:/login";
        }
        WorkoutSession session = workoutBuilderService.startSession(user, id);
        model.addAttribute("session", session);
        model.addAttribute("exerciseViews", buildPlayerViews(session));
        model.addAttribute("goalOptions", goalService.listGoalsForViewer(user, null, GoalStatus.ACTIVE, null, false));
        model.addAttribute("selectedGoal", goalLinkService.findGoalForWorkoutSession(user, session.getId()));
        return "workouts/start";
    }

    @GetMapping("/session/{sessionId}")
    public String viewSession(@PathVariable Long sessionId, Model model) {
        User user = authHelper.getAuthenticatedUser();
        if (user == null) {
            return "redirect:/login";
        }
        WorkoutSession session = workoutBuilderService.getSession(user, sessionId);
        model.addAttribute("session", session);
        model.addAttribute("exerciseViews", buildPlayerViews(session));
        model.addAttribute("goalOptions", goalService.listGoalsForViewer(user, null, GoalStatus.ACTIVE, null, false));
        model.addAttribute("selectedGoal", goalLinkService.findGoalForWorkoutSession(user, session.getId()));
        return "workouts/start";
    }

    @PostMapping("/session/{sessionId}/goal")
    public String updateSessionGoal(@PathVariable Long sessionId,
                                    @RequestParam(required = false) Long goalId) {
        User user = authHelper.getAuthenticatedUser();
        if (user == null) {
            return "redirect:/login";
        }
        workoutBuilderService.getSession(user, sessionId);
        goalLinkService.replaceWorkoutSessionLink(user, goalId, sessionId, GoalLinkSource.SELF);
        return "redirect:/workouts/session/" + sessionId;
    }

    @PostMapping("/session/{sessionId}/sets/{setId}")
    @ResponseBody
    public ResponseEntity<?> updateSet(@PathVariable Long sessionId,
                                       @PathVariable Long setId,
                                       @RequestBody WorkoutSetUpdateRequest request) {
        User user = authHelper.getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        workoutBuilderService.updateSet(user, sessionId, setId, request);
        WorkoutSession session = workoutBuilderService.getSession(user, sessionId);
        return ResponseEntity.ok(Map.of(
                "setId", setId,
                "totalVolume", session.getTotalVolume(),
                "completed", session.isCompleted()
        ));
    }

    private void hydrateExerciseOptions(Model model, User user) {
        List<Exercise> exercises = exerciseService.getAllExercises();
        List<CustomExercise> customExercises = customExerciseService.getCustomExercisesByUser(user.getId());
        model.addAttribute("exercises", exercises);
        model.addAttribute("customExercises", customExercises);
    }

    private void applySmartDefaults(Model model, User user) {
        UserSettings settings = userSettingsService.getOrCreate(user);
        int defaultSets = settings != null ? settings.getDefaultSets() : 3;
        int repMin = settings != null ? settings.getDefaultRepMin() : 8;
        int repMax = settings != null ? settings.getDefaultRepMax() : 12;
        model.addAttribute("defaultSets", defaultSets);
        model.addAttribute("defaultRepMin", repMin);
        model.addAttribute("defaultRepMax", repMax);
    }

    private WorkoutTemplateForm toForm(WorkoutTemplate template) {
        WorkoutTemplateForm form = new WorkoutTemplateForm();
        form.setName(template.getName());
        form.setDescription(template.getDescription());

        List<WorkoutExercise> ordered = new ArrayList<>(template.getExercises());
        ordered.sort(Comparator.comparingInt(WorkoutExercise::getOrderIndex));
        for (WorkoutExercise exercise : ordered) {
            WorkoutExerciseForm row = new WorkoutExerciseForm();
            row.setExerciseName(exercise.getExerciseName());
            row.setExerciseId(exercise.getExerciseId());
            row.setCustomExerciseId(exercise.getCustomExerciseId());
            row.setSets(exercise.getSets());
            row.setReps(exercise.getReps());
            row.setRestSeconds(exercise.getRestSeconds());
            row.setNotes(exercise.getNotes());
            if (exercise.getExerciseId() != null) {
                row.setExerciseRef("e:" + exercise.getExerciseId());
            } else if (exercise.getCustomExerciseId() != null) {
                row.setExerciseRef("c:" + exercise.getCustomExerciseId());
            }
            form.getExercises().add(row);
        }
        return form;
    }

    private void resolveExercises(WorkoutTemplateForm form,
                                  Map<Long, String> exerciseNames,
                                  Map<Long, String> customExerciseNames) {
        if (form.getExercises() == null) {
            return;
        }
        for (WorkoutExerciseForm row : form.getExercises()) {
            ParsedExerciseRef parsed = ParsedExerciseRef.parse(row.getExerciseRef());
            if (parsed == null) {
                row.setExerciseId(null);
                row.setCustomExerciseId(null);
                continue;
            }

            if (parsed.type == ExerciseType.EXERCISE) {
                row.setExerciseId(parsed.id);
                row.setCustomExerciseId(null);
                String name = exerciseNames.get(parsed.id);
                if (name != null) {
                    row.setExerciseName(name);
                }
            }
            if (parsed.type == ExerciseType.CUSTOM) {
                row.setCustomExerciseId(parsed.id);
                row.setExerciseId(null);
                String name = customExerciseNames.get(parsed.id);
                if (name != null) {
                    row.setExerciseName(name);
                }
            }
        }
    }

    private Map<Long, String> exerciseNameMap() {
        Map<Long, String> map = new HashMap<>();
        for (Exercise exercise : exerciseService.getAllExercises()) {
            map.put(exercise.getId(), exercise.getName());
        }
        return map;
    }

    private Map<Long, String> customExerciseNameMap(User user) {
        Map<Long, String> map = new HashMap<>();
        for (CustomExercise exercise : customExerciseService.getCustomExercisesByUser(user.getId())) {
            map.put(exercise.getId(), exercise.getName());
        }
        return map;
    }

    private List<WorkoutPlayerExerciseView> buildPlayerViews(WorkoutSession session) {
        Map<Integer, WorkoutPlayerExerciseView> grouped = new HashMap<>();
        for (WorkoutSetLog setLog : session.getSetLogs()) {
            WorkoutPlayerExerciseView view = grouped.computeIfAbsent(setLog.getExerciseOrder(), key -> {
                WorkoutPlayerExerciseView v = new WorkoutPlayerExerciseView();
                v.setOrderIndex(key);
                v.setName(setLog.getExerciseName());
                return v;
            });
            view.getSets().add(setLog);
        }
        List<WorkoutPlayerExerciseView> list = new ArrayList<>(grouped.values());
        list.sort(Comparator.comparingInt(WorkoutPlayerExerciseView::getOrderIndex));
        for (WorkoutPlayerExerciseView view : list) {
            view.getSets().sort(Comparator.comparingInt(WorkoutSetLog::getSetNumber));
            WorkoutPerformanceHint hint = workoutPerformanceService.buildHint(
                    session.getUser(),
                    view.getName(),
                    session.getStartedAt()
            );
            if (hint != null) {
                view.setLastSummary(hint.lastSummary());
                view.setBestSummary(hint.bestSummary());
            }
        }
        return list;
    }

    enum ExerciseType {
        EXERCISE,
        CUSTOM
    }

    static class ParsedExerciseRef {
        private final ExerciseType type;
        private final Long id;

        private ParsedExerciseRef(ExerciseType type, Long id) {
            this.type = type;
            this.id = id;
        }

        static ParsedExerciseRef parse(String ref) {
            if (ref == null || ref.isBlank() || !ref.contains(":")) {
                return null;
            }
            String[] parts = ref.split(":", 2);
            if (parts.length != 2) {
                return null;
            }
            try {
                long id = Long.parseLong(parts[1]);
                if ("e".equalsIgnoreCase(parts[0])) {
                    return new ParsedExerciseRef(ExerciseType.EXERCISE, id);
                }
                if ("c".equalsIgnoreCase(parts[0])) {
                    return new ParsedExerciseRef(ExerciseType.CUSTOM, id);
                }
            } catch (NumberFormatException ignored) {
                return null;
            }
            return null;
        }
    }
}
