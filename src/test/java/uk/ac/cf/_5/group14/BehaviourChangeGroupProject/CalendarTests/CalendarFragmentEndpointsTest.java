package uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarTests;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTask;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTaskService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTaskWarningService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.TaskAiGenerationService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.TaskTemplateService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.DayMode.DayModeService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Goals.GoalLinkService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.PlatformBilling.PlatformSubscriptionService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.CalendarController;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleOccurrenceService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.StrengthLog.Service.WorkoutScheduleService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.StrengthLog.Service.WorkoutSessionService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.UserSettings.UserSettings;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.UserSettings.UserSettingsService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

@WebMvcTest(CalendarController.class)
@ActiveProfiles("test")
class CalendarFragmentEndpointsTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private CalendarTaskService taskService;

    @MockitoBean
    private CalendarTaskWarningService taskWarningService;

    @MockitoBean
    private TaskAiGenerationService taskAiGenerationService;

    @MockitoBean
    private TaskTemplateService taskTemplateService;

    @MockitoBean
    private UserSettingsService userSettingsService;

    @MockitoBean
    private PlatformSubscriptionService platformSubscriptionService;

    @MockitoBean
    private ScheduleService scheduleService;

    @MockitoBean
    private ScheduleOccurrenceService scheduleOccurrenceService;

    @MockitoBean
    private WorkoutScheduleService workoutScheduleService;

    @MockitoBean
    private WorkoutSessionService workoutSessionService;

    @MockitoBean
    private GoalLinkService goalLinkService;

    @MockitoBean
    private DayModeService dayModeService;

    @MockitoBean
    private Clock clock;

    @Test
    void weekFragmentRendersWeekPane() throws Exception {
        User sessionUser = new User();
        sessionUser.setId(1L);

        UserSettings settings = new UserSettings();
        settings.setUser(sessionUser);
        settings.setUserId(sessionUser.getId());

        given(userSettingsService.getOrCreate(eq(sessionUser))).willReturn(settings);
        given(platformSubscriptionService.isPremium(eq(sessionUser.getId()), any(Clock.class))).willReturn(false);
        CalendarTask weekTask = new CalendarTask();
        weekTask.setId(102L);
        weekTask.setTitle("Weekly visible task");
        given(taskService.getTasks(any(User.class), any(LocalDate.class))).willAnswer(invocation -> {
            LocalDate date = invocation.getArgument(1);
            if (LocalDate.of(2026, 1, 12).equals(date)) {
                return List.of(weekTask);
            }
            return Collections.emptyList();
        });
        given(scheduleOccurrenceService.getOccurrencesByRange(eq(sessionUser), any(), any())).willReturn(Collections.emptyMap());
        given(scheduleService.findByUser(eq(sessionUser))).willReturn(Collections.emptyList());

        mvc.perform(get("/calendar/week-fragment")
                        .param("week", "3")
                        .param("weekYear", "2026")
                        .sessionAttr("user", sessionUser))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("data-week-pane")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Weekly visible task")));
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

        @Bean
        public Clock systemClock() {
            return Clock.systemDefaultZone();
        }

        @Bean
        public AuthHelper authHelper() {
            return mock(AuthHelper.class);
        }

        @Bean
        public PlatformSubscriptionService platformSubscriptionService() {
            return mock(PlatformSubscriptionService.class);
        }
    }
}
