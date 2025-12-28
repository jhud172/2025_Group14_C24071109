package uk.ac.cf._5.group14.BehaviourChangeGroupProject.WorkoutTests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseData.ExerciseRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Workout.WorkoutRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Workout.WorkoutServiceImpl;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class WorkoutDeletionTest {

    @Mock
    private UserService userService;

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private WorkoutRepository workoutRepository;

    @InjectMocks
    private WorkoutServiceImpl workoutService;

    @Test
    void deleteWorkout_shouldCallRepositoryDelete() {
        Long workoutIdToDelete = 123L;

        // Execute
        workoutService.deleteWorkout(workoutIdToDelete);

        // Verify
        verify(workoutRepository, times(1)).deleteById(workoutIdToDelete);
    }
}
