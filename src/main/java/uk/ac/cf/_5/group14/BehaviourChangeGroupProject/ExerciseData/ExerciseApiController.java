package uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseData;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.util.List;

@RestController
@RequestMapping("/api/exercises")
public class ExerciseApiController {

    private final ExerciseService exerciseService;
    private final AuthHelper authHelper;

    public ExerciseApiController(ExerciseService exerciseService, AuthHelper authHelper) {
        this.exerciseService = exerciseService;
        this.authHelper = authHelper;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Exercise>> getAllExercises() {
        User user = authHelper.getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(exerciseService.getAllExercises());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Exercise> getExerciseById(@PathVariable Long id) {
        User user = authHelper.getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        Exercise exercise = exerciseService.getExerciseById(id);
        if (exercise == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(exercise);
    }
}
