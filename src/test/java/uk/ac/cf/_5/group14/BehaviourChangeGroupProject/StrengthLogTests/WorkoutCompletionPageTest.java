package uk.ac.cf._5.group14.BehaviourChangeGroupProject.StrengthLogTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.StrengthLog.Repository.ExerciseSessionRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.StrengthLog.Repository.SetLogRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.StrengthLog.Repository.WorkoutSessionRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.StrengthLog.StrengthLogController;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.StrengthLog.WorkoutSession;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.UserSettings.UserSettings;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.UserSettings.UserSettingsService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Workout.Workout;

import java.time.LocalDate;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StrengthLogController.class)
@ActiveProfiles("test")
class WorkoutCompletionPageTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private WorkoutSessionRepository workoutSessionRepository;

    @MockitoBean
    private ExerciseSessionRepository exerciseSessionRepository;

    @MockitoBean
    private SetLogRepository setLogRepository;

    @MockitoBean
    private UserSettingsService userSettingsService;

    @MockitoBean
    private AuthHelper authHelper;

    @Test
    void completionPageRendersAndHonoursDayQueryParam() throws Exception {
        User sessionUser = new User();
        sessionUser.setId(1L);

        given(authHelper.getAuthenticatedUser()).willReturn(sessionUser);
        given(userSettingsService.getOrCreate(sessionUser)).willReturn(new UserSettings());

        Workout workout = new Workout();
        workout.setId(99L);
        workout.setName("Leg day");
        workout.setNotes("Notes");

        WorkoutSession ws = new WorkoutSession();
        ws.setId(55L);
        ws.setUser(sessionUser);
        ws.setDate(LocalDate.of(2026, 1, 10));
        ws.setNameSnapshot("Leg day");
        ws.setWorkout(workout);

        given(workoutSessionRepository.findById(55L)).willReturn(Optional.of(ws));

        mvc.perform(get("/workout-session/55/completion").param("day", "2026-01-15").sessionAttr("user", sessionUser))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Leg day")))
                .andExpect(content().string(containsString("2026-01-15")))
                .andExpect(content().string(containsString("/calendar/day/2026-01-15")))
                .andExpect(content().string(containsString("/workout-session/55")));
    }

    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
            return http
                    .csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                    .build();
        }
    }
}
