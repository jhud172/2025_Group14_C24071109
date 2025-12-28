package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Workout;


import lombok.AllArgsConstructor;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseData.Exercise;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseData.ExerciseRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserService;


import java.util.ArrayList;
import java.util.List;


@Service
@AllArgsConstructor
public class WorkoutServiceImpl implements WorkoutService {

    private final UserService userService;
    private final ExerciseRepository exerciseRepository;
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

        List<Exercise> exercises = exerciseRepository.findAllById(dto.getExerciseIds());
        workout.setExercises(exercises);

        workout.setUserId(userId);
        workout.setName(dto.getName());
        workout.setNotes(dto.getWorkoutNotes());

        workoutRepository.save(workout);
    }

    public void deleteWorkout(Long workoutId) {
        workoutRepository.deleteById(workoutId);
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
}
