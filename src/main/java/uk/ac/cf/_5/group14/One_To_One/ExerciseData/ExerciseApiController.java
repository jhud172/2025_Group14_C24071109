package uk.ac.cf._5.group14.One_To_One.ExerciseData;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uk.ac.cf._5.group14.One_To_One.Users.AuthHelper;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.util.List;

@RestController
@RequestMapping("/api/exercises")
@PreAuthorize("isAuthenticated()")
public class ExerciseApiController {

    private final ExerciseService exerciseService;

    public ExerciseApiController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Exercise>> getAllExercises() {
        return ResponseEntity.ok(exerciseService.getAllExercises());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Exercise> getExerciseById(@PathVariable Long id) {
        Exercise exercise = exerciseService.getExerciseById(id);
        if (exercise == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(exercise);
    }
}
