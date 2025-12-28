package uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseData;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean; // <--- NEW IMPORT
import org.springframework.test.web.servlet.MockMvc;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserService;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExerciseController.class)
@AutoConfigureMockMvc(addFilters = false)
class ExerciseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExerciseService exerciseService;

    @MockitoBean private UserService userService;
    @MockitoBean private javax.sql.DataSource dataSource;
    @MockitoBean private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    @Test
    void getExerciseToReturnViewAndModel() throws Exception {

        Exercise mockExercise = new Exercise();
        mockExercise.setId(1L);
        mockExercise.setName("Plank");

        given(exerciseService.getExerciseById(1L)).willReturn(mockExercise);

        mockMvc.perform(get("/exercise/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("/exercise-log/ExerciseTutorial"))
                .andExpect(model().attributeExists("exercise"))
                .andExpect(model().attribute("exercise", mockExercise));
    }
}