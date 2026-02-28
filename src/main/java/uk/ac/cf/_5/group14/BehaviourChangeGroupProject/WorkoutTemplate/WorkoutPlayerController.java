package uk.ac.cf._5.group14.BehaviourChangeGroupProject.WorkoutTemplate;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CustomExerciseData.CustomExercise;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CustomExerciseData.CustomExerciseRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseData.Exercise;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseData.ExerciseRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class WorkoutPlayerController {

    private final WorkoutPlayerSessionService workoutSessionService;
    private final ExerciseRepository exerciseRepository;
    private final CustomExerciseRepository customExerciseRepository;
    private final AuthHelper authHelper;

    // ─── Start a new session and redirect to the session page ───────────────

    @PostMapping("/workout-sessions/start/{workoutId}")
    public String startSession(@PathVariable Long workoutId) {
        User user = authHelper.getAuthenticatedUser();
        if (user == null) return "redirect:/login";
        WorkoutSession session = workoutSessionService.startSession(user.getId(), workoutId);
        return "redirect:/workout-sessions/" + session.getId();
    }

    // ─── Session page (HTML view) ────────────────────────────────────────────

    @GetMapping("/workout-sessions/{id}")
    public String sessionPage(@PathVariable Long id, Model model) {
        User user = authHelper.getAuthenticatedUser();
        if (user == null) return "redirect:/login";

        Optional<WorkoutSession> sessionOpt = workoutSessionService.findById(id);
        if (sessionOpt.isEmpty()) return "redirect:/workouts";

        WorkoutSession session = sessionOpt.get();
        if (!session.getUser().getId().equals(user.getId())) return "redirect:/workouts";

        WorkoutSessionViewModel vm = toViewModel(session);
        model.addAttribute("session", vm);
        model.addAttribute("sessionId", id);

        String layoutType = vm.getTemplateLayoutType() != null ? vm.getTemplateLayoutType() : "FLOW";
        model.addAttribute("layoutType", layoutType);

        return "workout-sessions/session";
    }

    // ─── Session data (JSON) ─────────────────────────────────────────────────

    @GetMapping("/workout-sessions/{id}/data")
    @ResponseBody
    public ResponseEntity<?> getSessionData(@PathVariable Long id) {
        User user = authHelper.getAuthenticatedUser();
        if (user == null) return ResponseEntity.status(401).build();
        Optional<WorkoutSession> sessionOpt = workoutSessionService.findById(id);
        if (sessionOpt.isEmpty()) return ResponseEntity.notFound().build();
        WorkoutSession session = sessionOpt.get();
        if (!session.getUser().getId().equals(user.getId())) return ResponseEntity.status(403).build();
        return ResponseEntity.ok(toViewModel(session));
    }

    // ─── Complete session ────────────────────────────────────────────────────

    @PostMapping("/workout-sessions/{id}/complete")
    @ResponseBody
    public ResponseEntity<?> completeSession(@PathVariable Long id) {
        User user = authHelper.getAuthenticatedUser();
        if (user == null) return ResponseEntity.status(401).build();
        Optional<WorkoutSession> sessionOpt = workoutSessionService.findById(id);
        if (sessionOpt.isEmpty()) return ResponseEntity.notFound().build();
        if (!sessionOpt.get().getUser().getId().equals(user.getId())) return ResponseEntity.status(403).build();
        WorkoutSession completed = workoutSessionService.completeSession(id);
        return ResponseEntity.ok(toViewModel(completed));
    }

    // ─── Add set ─────────────────────────────────────────────────────────────

    @PostMapping("/workout-sessions/{sessionId}/exercises/{exerciseId}/sets")
    @ResponseBody
    public ResponseEntity<?> addSet(
            @PathVariable Long sessionId,
            @PathVariable Long exerciseId,
            @RequestParam(required = false) Integer reps,
            @RequestParam(required = false) BigDecimal weight,
            @RequestParam(required = false) BigDecimal rpe) {
        User user = authHelper.getAuthenticatedUser();
        if (user == null) return ResponseEntity.status(401).build();
        Optional<WorkoutSession> sessionOpt = workoutSessionService.findById(sessionId);
        if (sessionOpt.isEmpty()) return ResponseEntity.notFound().build();
        if (!sessionOpt.get().getUser().getId().equals(user.getId())) return ResponseEntity.status(403).build();
        WorkoutSessionSet set = workoutSessionService.addSet(exerciseId, reps, weight, rpe);
        return ResponseEntity.ok(toSetView(set));
    }

    // ─── Update set ──────────────────────────────────────────────────────────

    @RequestMapping(value = "/workout-session-sets/{setId}", method = {RequestMethod.PATCH, RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<?> updateSet(
            @PathVariable Long setId,
            @RequestParam(required = false) Integer reps,
            @RequestParam(required = false) BigDecimal weight,
            @RequestParam(required = false) BigDecimal rpe) {
        User user = authHelper.getAuthenticatedUser();
        if (user == null) return ResponseEntity.status(401).build();
        WorkoutSessionSet set = workoutSessionService.updateSet(setId, reps, weight, rpe);
        return ResponseEntity.ok(toSetView(set));
    }

    // ─── Complete set ─────────────────────────────────────────────────────────

    @PostMapping("/workout-session-sets/{setId}/complete")
    @ResponseBody
    public ResponseEntity<?> completeSet(@PathVariable Long setId) {
        User user = authHelper.getAuthenticatedUser();
        if (user == null) return ResponseEntity.status(401).build();
        WorkoutSessionSet set = workoutSessionService.completeSet(setId);
        return ResponseEntity.ok(toSetView(set));
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private WorkoutSessionViewModel toViewModel(WorkoutSession session) {
        WorkoutSessionViewModel vm = new WorkoutSessionViewModel();
        vm.setSessionId(session.getId());
        vm.setWorkoutName(session.getWorkout() != null ? session.getWorkout().getName() : null);
        vm.setStatus(session.getStatus());
        vm.setStartedAt(session.getStartedAt());
        vm.setEndedAt(session.getEndedAt());
        vm.setMoodBefore(session.getMoodBefore());
        vm.setMoodAfter(session.getMoodAfter());
        vm.setConfidence(session.getConfidence());
        vm.setTotalVolume(session.getTotalVolume());
        vm.setAllowCompletedWithoutLog(session.isAllowCompletedWithoutLog());

        WorkoutTemplate template = session.getTemplateUsed();
        if (template != null) {
            vm.setTemplateName(template.getName());
            vm.setTemplateLayoutType(template.getLayoutType() != null ? template.getLayoutType().name() : null);
            vm.setTemplateConfigJson(template.getConfigJson());
        } else {
            vm.setTemplateName(session.getTemplateNameSnapshot());
            vm.setTemplateConfigJson(session.getConfigJsonSnapshot());
        }

        List<WorkoutSessionViewModel.ExerciseView> exerciseViews = session.getExercises().stream()
                .map(this::toExerciseView)
                .collect(Collectors.toList());
        vm.setExercises(exerciseViews);

        return vm;
    }

    private WorkoutSessionViewModel.ExerciseView toExerciseView(WorkoutSessionExercise ex) {
        WorkoutSessionViewModel.ExerciseView view = new WorkoutSessionViewModel.ExerciseView();
        view.setId(ex.getId());
        view.setExerciseId(ex.getExerciseId());
        view.setCustomExerciseId(ex.getCustomExerciseId());
        view.setOrderIndex(ex.getOrderIndex());
        view.setMode(ex.getMode() != null ? ex.getMode().name() : null);
        view.setGroupKey(ex.getGroupKey());
        view.setNotes(ex.getNotes());

        // Resolve exercise name
        if (ex.getExerciseId() != null) {
            exerciseRepository.findById(ex.getExerciseId()).ifPresent(e -> {
                view.setExerciseName(e.getName());
                view.setExerciseCategory(e.getCategory());
            });
        } else if (ex.getCustomExerciseId() != null) {
            customExerciseRepository.findById(ex.getCustomExerciseId()).ifPresent(e -> {
                view.setExerciseName(e.getName());
                view.setExerciseCategory(e.getCategory());
            });
        }
        if (view.getExerciseName() == null) {
            view.setExerciseName("Exercise " + (ex.getOrderIndex() + 1));
        }

        List<WorkoutSessionViewModel.SetView> setViews = ex.getSets().stream()
                .map(this::toSetView)
                .collect(Collectors.toList());
        view.setSets(setViews);

        return view;
    }

    private WorkoutSessionViewModel.SetView toSetView(WorkoutSessionSet set) {
        WorkoutSessionViewModel.SetView view = new WorkoutSessionViewModel.SetView();
        view.setId(set.getId());
        view.setSetIndex(set.getSetIndex());
        view.setReps(set.getReps());
        view.setWeight(set.getWeight());
        view.setRpe(set.getRpe());
        view.setTempo(set.getTempo());
        view.setDrop(set.isDrop());
        view.setCompletedAt(set.getCompletedAt());
        return view;
    }
}
