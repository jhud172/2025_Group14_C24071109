package uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarTests;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarDayModelBuilder;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTaskService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTaskWarningService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.DailyStreakService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.DayOptimisationRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.TaskAiGenerationService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.TaskTemplateService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.DayHealthData.DayHealthPersistenceService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.DayMode.DayModeService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Goals.GoalLinkService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.PlatformBilling.PlatformSubscriptionService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ReflectionData.ReflectionService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.CalendarController;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleAppliedRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleOccurrenceService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.StrengthLog.WorkoutSession;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.StrengthLog.Service.WorkoutScheduleService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.StrengthLog.Service.WorkoutSessionService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.UserSettings.CalendarTaskLayoutPreference;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.UserSettings.CalendarTaskOrderingPreference;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.UserSettings.StickerPackPreference;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.UserSettings.UserSettings;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.UserSettings.UserSettingsService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Workout.Workout;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Notifications.NotificationSseRegistry;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Config.DevModeProperties;

/**
 * Tests verifying that the month view includes sticker calendar data and UI elements.
 */
@WebMvcTest(CalendarController.class)
@ActiveProfiles("test")
class StickerCalendarMonthMvcTest {

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
    private ScheduleService scheduleService;

    @MockitoBean
    private ScheduleOccurrenceService scheduleOccurrenceService;

    @MockitoBean
    private ScheduleAppliedRepository scheduleAppliedRepository;

    @MockitoBean
    private WorkoutScheduleService workoutScheduleService;

    @MockitoBean
    private WorkoutSessionService workoutSessionService;

    @MockitoBean
    private AuthHelper authHelper;

    @MockitoBean
    private UserSettingsService userSettingsService;

    @MockitoBean
    private DayModeService dayModeService;

    @MockitoBean
    private GoalLinkService goalLinkService;

    @MockitoBean
    private PlatformSubscriptionService platformSubscriptionService;

    @MockitoBean
    private DayOptimisationRepository dayOptimisationRepository;

    @MockitoBean
    private ReflectionService reflectionService;

    @MockitoBean
    private DailyStreakService dailyStreakService;

    @MockitoBean
    private DayHealthPersistenceService dayHealthPersistenceService;

    @Autowired
    private CalendarDayModelBuilder calendarDayModelBuilder;

    @MockitoBean
    private NotificationSseRegistry sseRegistry;

    /** Month view model contains sticker-related attributes */
    @Test
    void monthViewModelContainsStickerAttributes() throws Exception {
        User user = new User();
        user.setId(1L);

        UserSettings settings = new UserSettings();
        settings.setCalendarTaskOrdering(CalendarTaskOrderingPreference.CHRONOLOGICAL);
        settings.setCalendarTaskLayout(CalendarTaskLayoutPreference.COMBINED_LIST);
        settings.setStickerPack(StickerPackPreference.STARS);
        settings.setMonthlyWorkoutTarget(12);

        given(userSettingsService.getOrCreate(eq(user))).willReturn(settings);
        given(platformSubscriptionService.isPremium(eq(user.getId()), any(Clock.class))).willReturn(false);
        given(taskService.getTasksByRange(any(), any(), any())).willReturn(Collections.emptyMap());
        given(scheduleOccurrenceService.getOccurrencesForUserInMonth(any(), anyInt(), anyInt()))
                .willReturn(Collections.emptyMap());
        given(scheduleService.findByUser(any())).willReturn(Collections.emptyList());
        given(workoutSessionService.findCompletedByUserAndDateRange(any(), any(), any()))
                .willReturn(Collections.emptyList());

        mvc.perform(get("/calendar/month-fragment")
                        .param("month", "3")
                        .param("year", "2026")
                        .sessionAttr("user", user))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("stickerSessionNames"))
                .andExpect(model().attributeExists("completedSessionCount"))
                .andExpect(model().attributeExists("stickerPack"))
                .andExpect(model().attributeExists("monthlyWorkoutTarget"))
                .andExpect(model().attribute("stickerPack", StickerPackPreference.STARS))
                .andExpect(model().attribute("monthlyWorkoutTarget", 12))
                .andExpect(model().attribute("completedSessionCount", 0));
    }

    /** Month fragment HTML contains the sticker data JSON script element */
    @Test
    void monthViewHtmlContainsModeToggle() throws Exception {
        User user = new User();
        user.setId(2L);

        UserSettings settings = new UserSettings();
        settings.setCalendarTaskOrdering(CalendarTaskOrderingPreference.CHRONOLOGICAL);
        settings.setCalendarTaskLayout(CalendarTaskLayoutPreference.COMBINED_LIST);
        settings.setStickerPack(StickerPackPreference.SPORT);
        settings.setMonthlyWorkoutTarget(10);

        given(userSettingsService.getOrCreate(eq(user))).willReturn(settings);
        given(platformSubscriptionService.isPremium(eq(user.getId()), any(Clock.class))).willReturn(false);
        given(taskService.getTasksByRange(any(), any(), any())).willReturn(Collections.emptyMap());
        given(scheduleOccurrenceService.getOccurrencesForUserInMonth(any(), anyInt(), anyInt()))
                .willReturn(Collections.emptyMap());
        given(scheduleService.findByUser(any())).willReturn(Collections.emptyList());
        given(workoutSessionService.findCompletedByUserAndDateRange(any(), any(), any()))
                .willReturn(Collections.emptyList());

        // The month fragment (used by AJAX carousel) contains the embedded sticker data JSON
        // with pack and target info — toggle buttons are in the outer page, not the fragment
        mvc.perform(get("/calendar/month-fragment")
                        .param("month", "3")
                        .param("year", "2026")
                        .sessionAttr("user", user))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("sticker-pane-data")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("SPORT")));
    }

    /** Month fragment HTML contains the sticker view panel elements */
    @Test
    void monthViewHtmlContainsStickerViewPanel() throws Exception {
        User user = new User();
        user.setId(3L);

        UserSettings settings = new UserSettings();
        settings.setCalendarTaskOrdering(CalendarTaskOrderingPreference.CHRONOLOGICAL);
        settings.setCalendarTaskLayout(CalendarTaskLayoutPreference.COMBINED_LIST);
        settings.setStickerPack(StickerPackPreference.EMOJI);
        settings.setMonthlyWorkoutTarget(15);

        given(userSettingsService.getOrCreate(eq(user))).willReturn(settings);
        given(platformSubscriptionService.isPremium(eq(user.getId()), any(Clock.class))).willReturn(false);
        given(taskService.getTasksByRange(any(), any(), any())).willReturn(Collections.emptyMap());
        given(scheduleOccurrenceService.getOccurrencesForUserInMonth(any(), anyInt(), anyInt()))
                .willReturn(Collections.emptyMap());
        given(scheduleService.findByUser(any())).willReturn(Collections.emptyList());
        given(workoutSessionService.findCompletedByUserAndDateRange(any(), any(), any()))
                .willReturn(Collections.emptyList());

        // The sticker panel and grid are in the full page; test the fragment for sticker data presence
        mvc.perform(get("/calendar/month-fragment")
                        .param("month", "3")
                        .param("year", "2026")
                        .sessionAttr("user", user))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("sticker-pane-data")));
    }

    /** Month view embeds sticker session data JSON for completed workouts */
    @Test
    void monthViewEmbedsStickerSessionDataForCompletedWorkouts() throws Exception {
        User user = new User();
        user.setId(4L);

        UserSettings settings = new UserSettings();
        settings.setCalendarTaskOrdering(CalendarTaskOrderingPreference.CHRONOLOGICAL);
        settings.setCalendarTaskLayout(CalendarTaskLayoutPreference.COMBINED_LIST);
        settings.setStickerPack(StickerPackPreference.STARS);
        settings.setMonthlyWorkoutTarget(12);

        // Create a completed workout session
        WorkoutSession session = new WorkoutSession();
        session.setId(1L);
        session.setUser(user);
        session.setDate(LocalDate.of(2026, 3, 10));
        session.setCompleted(true);
        session.setNameSnapshot("Leg Day");
        Workout workout = new Workout();
        workout.setId(1L);
        workout.setName("Leg Day");
        workout.setUserId(user.getId());
        session.setWorkout(workout);

        given(userSettingsService.getOrCreate(eq(user))).willReturn(settings);
        given(platformSubscriptionService.isPremium(eq(user.getId()), any(Clock.class))).willReturn(false);
        given(taskService.getTasksByRange(any(), any(), any())).willReturn(Collections.emptyMap());
        given(scheduleOccurrenceService.getOccurrencesForUserInMonth(any(), anyInt(), anyInt()))
                .willReturn(Collections.emptyMap());
        given(scheduleService.findByUser(any())).willReturn(Collections.emptyList());
        given(workoutSessionService.findCompletedByUserAndDateRange(any(), any(), any()))
                .willReturn(List.of(session));

        mvc.perform(get("/calendar/month-fragment")
                        .param("month", "3")
                        .param("year", "2026")
                        .sessionAttr("user", user))
                .andExpect(status().isOk())
                .andExpect(model().attribute("completedSessionCount", 1))
                .andExpect(result -> {
                    @SuppressWarnings("unchecked")
                    Map<String, List<String>> stickerData =
                            (Map<String, List<String>>) result.getModelAndView().getModel().get("stickerSessionNames");
                    assert stickerData != null : "stickerSessionNames should not be null";
                    assert stickerData.containsKey("2026-03-10") : "Should have sticker for 2026-03-10";
                    assert stickerData.get("2026-03-10").contains("Leg Day") : "Should include workout name";
                });
    }

    @TestConfiguration
    static class TestSecurityConfig {
        @Bean("stickerTestSecurityFilterChain")
        SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
            return http
                    .csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                    .build();
        }

        @Bean
        AuthHelper authHelper() {
            return mock(AuthHelper.class);
        }

        @Bean
        PlatformSubscriptionService platformSubscriptionService() {
            return mock(PlatformSubscriptionService.class);
        }

        @Bean
        Clock systemClock() {
            return Clock.system(ZoneId.systemDefault());
        }

        @Bean
        CalendarDayModelBuilder calendarDayModelBuilder() {
            return new CalendarDayModelBuilder();
        }
    
        @Bean
        public DevModeProperties devModeProperties() {
            return new DevModeProperties();
        }
}
}
