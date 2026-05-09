package uk.ac.cf._5.group14.One_To_One.ExerciseData;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<ExerciseView>> getAllExercises() {
        return ResponseEntity.ok(exerciseService.getAllExercises().stream()
                .map(ExerciseView::from)
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExerciseView> getExerciseById(@PathVariable Long id) {
        Exercise exercise = exerciseService.getExerciseById(id);
        if (exercise == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ExerciseView.from(exercise));
    }
}
