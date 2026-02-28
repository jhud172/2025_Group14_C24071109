package uk.ac.cf._5.group14.BehaviourChangeGroupProject.WorkoutTemplate;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class WorkoutPlayerController {

    private final WorkoutPlayerSessionService workoutSessionService;
    private final AuthHelper authHelper;

    @GetMapping("/workouts/{id}/start")
    @ResponseBody
    public ResponseEntity<?> startSession(@PathVariable Long id) {
        User user = authHelper.getAuthenticatedUser();
        WorkoutSession session = workoutSessionService.startSession(user.getId(), id);
        return ResponseEntity.ok(toViewModel(session));
    }

    @GetMapping("/workout-sessions/{id}")
    @ResponseBody
    public ResponseEntity<?> getSession(@PathVariable Long id) {
        User user = authHelper.getAuthenticatedUser();
        Optional<WorkoutSession> sessionOpt = workoutSessionService.findById(id);
        if (sessionOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        WorkoutSession session = sessionOpt.get();
        if (!session.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(toViewModel(session));
    }

    @PostMapping("/workout-sessions/{id}/complete")
    @ResponseBody
    public ResponseEntity<?> completeSession(@PathVariable Long id) {
        User user = authHelper.getAuthenticatedUser();
        Optional<WorkoutSession> sessionOpt = workoutSessionService.findById(id);
        if (sessionOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (!sessionOpt.get().getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }
        WorkoutSession completed = workoutSessionService.completeSession(id);
        return ResponseEntity.ok(toViewModel(completed));
    }

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
