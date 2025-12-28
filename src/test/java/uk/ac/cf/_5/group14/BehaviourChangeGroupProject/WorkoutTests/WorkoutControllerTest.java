package uk.ac.cf._5.group14.BehaviourChangeGroupProject.WorkoutTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseData.Exercise;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseData.ExerciseService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Workout.Workout;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Workout.WorkoutController;

import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Workout.WorkoutService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(WorkoutController.class)
public class WorkoutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean private ExerciseService exerciseService;
    @MockitoBean private WorkoutService workoutService;
    @MockitoBean private UserService userService;
    @MockitoBean
    private AuthHelper authHelper;

    @Test
    @WithMockUser(username = "testUser")
    public void workoutControllerReturnsWorkoutPage() throws Exception {
        User testUser = new User();
        testUser.setId(1L);

        List<Exercise> testExercises = new ArrayList<>();
        Exercise testExercise = new Exercise();
        testExercise.setName("testExercise");
        testExercises.add(testExercise);

        List<Workout> testWorkouts = new ArrayList<>();
        Workout testWorkout = new Workout();
        testWorkout.setName("testWorkout");
        testWorkouts.add(testWorkout);

        given(authHelper.getAuthenticatedUser()).willReturn(testUser);
        given(exerciseService.suggestExercises(testUser)).willReturn(testExercises);
        given(workoutService.getWorkouts()).willReturn(testWorkouts);

        mockMvc.perform(get("/workout"))
                .andExpect(status().isOk())
                .andExpect(view().name("schedule/workout"))
                .andExpect(model().attributeExists("suggestedExercises"))
                .andExpect(model().attributeExists("usersWorkouts"));
    }


}
