package uk.ac.cf._5.group14.One_To_One.Workout;


import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import uk.ac.cf._5.group14.One_To_One.CustomExerciseData.CustomExercise;
import uk.ac.cf._5.group14.One_To_One.CustomExerciseData.CustomExerciseRepository;
import uk.ac.cf._5.group14.One_To_One.ExerciseData.Exercise;
import uk.ac.cf._5.group14.One_To_One.ExerciseData.ExerciseRepository;
import uk.ac.cf._5.group14.One_To_One.Users.AuthHelper;
import uk.ac.cf._5.group14.One_To_One.Users.UserService;


@Service
@AllArgsConstructor
public class WorkoutServiceImpl implements WorkoutService {

    private final UserService userService;
    private final ExerciseRepository exerciseRepository;
    private final CustomExerciseRepository customExerciseRepository;
    private final WorkoutRepository workoutRepository;
    private final AuthHelper authHelper;

    public void saveWorkout(SaveWorkoutDTO dto){
        Long userId = authHelper.getAuthenticatedUser().getId();
        Workout workout;

        if (dto.getId() != null) {
            workout = workoutRepository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("Workout not found"));
        } else {
            workout = new Workout();
        }

        List<Exercise> exercises = dto.getExerciseIds() == null
            ? List.of()
            : toList(exerciseRepository.findAllById(dto.getExerciseIds()));
        workout.setExercises(exercises);

        List<CustomExercise> customExercises = dto.getCustomExerciseIds() == null
            ? List.of()
            : toList(customExerciseRepository.findAllById(dto.getCustomExerciseIds()));
        workout.setCustomExercises(customExercises);

        workout.setUserId(userId);
        workout.setName(dto.getName());
        workout.setNotes(dto.getWorkoutNotes());

        workoutRepository.save(workout);
    }

    public void deleteWorkout(Long workoutId) {
        Long userId = authHelper.getAuthenticatedUser().getId();
        workoutRepository.findByIdAndUserId(workoutId, userId)
                .ifPresent(workoutRepository::delete);
    }

    public List<Workout> getWorkouts() {
        Long userId = authHelper.getAuthenticatedUser().getId();

        return workoutRepository.findByUserId(userId);
    }

    public Workout getWorkoutToEdit(Long workoutId) {
        Long userId = authHelper.getAuthenticatedUser().getId();

        return workoutRepository.findByIdAndUserId(workoutId, userId)
                .orElseThrow(() -> new RuntimeException("Workout not found or access denied"));
    }

    private <T> List<T> toList(Iterable<T> items) {
        List<T> results = new ArrayList<>();
        for (T item : items) {
            results.add(item);
        }
        return results;
    }
}
