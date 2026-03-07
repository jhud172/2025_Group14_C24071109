package uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarTests;

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
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.UserSettings.UserSettingsService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Notifications.NotificationSseRegistry;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleAppliedRepository;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.mock;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.DayOptimisationRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarDayModelBuilder;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Config.DevModeProperties;

@WebMvcTest(CalendarController.class)
@ActiveProfiles("test")
class CalendarDayReflectionGatingMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthHelper authHelper;

    @MockitoBean private CalendarTaskService taskService;
    @MockitoBean private CalendarTaskWarningService taskWarningService;
    @MockitoBean private TaskTemplateService taskTemplateService;
    @MockitoBean private TaskAiGenerationService taskAiGenerationService;
    @MockitoBean private UserSettingsService userSettingsService;
    @MockitoBean private ScheduleService scheduleService;
    @MockitoBean private ScheduleOccurrenceService scheduleOccurrenceService;
    @MockitoBean private WorkoutScheduleService workoutScheduleService;
    @MockitoBean private WorkoutSessionService workoutSessionService;
    @MockitoBean private DayModeService dayModeService;@MockitoBean
    private GoalLinkService goalLinkService;

    @MockitoBean
    private PlatformSubscriptionService platformSubscriptionService;

    @MockitoBean
    private ScheduleAppliedRepository scheduleAppliedRepository;

    @MockitoBean
    private NotificationSseRegistry sseRegistry;

    @MockitoBean
    private DayOptimisationRepository dayOptimisationRepository;

    @MockitoBean
    private CalendarDayModelBuilder calendarDayModelBuilder;

    // Required by UserSettingsModelAdvice

    @Test
    void shouldShowReflectionOnlyWhenGreen() throws Exception {
        User sessionUser = new User();
        sessionUser.setId(1L);

        LocalDate date = LocalDate.of(2026, 1, 15);

        CalendarTask completed = new CalendarTask();
        completed.setTitle("Task");
        completed.setCompleted(true);

        when(taskService.getTasks(any(User.class), eq(date))).thenReturn(List.of(completed));
        when(scheduleOccurrenceService.getOccurrencesForUserOnDate(any(User.class), eq(date))).thenReturn(List.of());
        when(workoutScheduleService.findByUserAndDayOfWeek(any(User.class), anyInt())).thenReturn(List.of());

        when(taskTemplateService.listRecents(any(User.class), anyInt())).thenReturn(List.of());
        when(taskTemplateService.listFavourites(any(User.class))).thenReturn(List.of());
        when(taskTemplateService.listAll(any(User.class))).thenReturn(List.of());

        when(userSettingsService.getOrCreate(any(User.class))).thenReturn(null);
        when(authHelper.getAuthenticatedUser()).thenReturn(null);

        mockMvc.perform(get("/calendar/day/2026-01-15").sessionAttr("user", sessionUser))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("How was today?")));
    }

    @Test
    void shouldHideReflectionWhenNotGreen() throws Exception {
        User sessionUser = new User();
        sessionUser.setId(1L);

        LocalDate date = LocalDate.of(2026, 1, 15);

        CalendarTask incomplete = new CalendarTask();
        incomplete.setTitle("Task");
        incomplete.setCompleted(false);

        when(taskService.getTasks(any(User.class), eq(date))).thenReturn(List.of(incomplete));
        when(scheduleOccurrenceService.getOccurrencesForUserOnDate(any(User.class), eq(date))).thenReturn(List.of());
        when(workoutScheduleService.findByUserAndDayOfWeek(any(User.class), anyInt())).thenReturn(List.of());

        when(taskTemplateService.listRecents(any(User.class), anyInt())).thenReturn(List.of());
        when(taskTemplateService.listFavourites(any(User.class))).thenReturn(List.of());
        when(taskTemplateService.listAll(any(User.class))).thenReturn(List.of());

        when(userSettingsService.getOrCreate(any(User.class))).thenReturn(null);
        when(authHelper.getAuthenticatedUser()).thenReturn(null);

        mockMvc.perform(get("/calendar/day/2026-01-15").sessionAttr("user", sessionUser))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("How was today?"))));
    }

    @TestConfiguration
    static class TestSecurityConfig {
        @Bean("testSecurityFilterChain")
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
    
        @Bean
        public DevModeProperties devModeProperties() {
            return new DevModeProperties();
        }
}
}
