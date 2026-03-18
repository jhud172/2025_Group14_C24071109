package uk.ac.cf._5.group14.One_To_One.CalendarTests;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.containsString;

import uk.ac.cf._5.group14.One_To_One.CalendarData.CalendarDayModelBuilder;
import uk.ac.cf._5.group14.One_To_One.CalendarData.CalendarTaskService;
import uk.ac.cf._5.group14.One_To_One.CalendarData.CalendarTaskWarningService;
import uk.ac.cf._5.group14.One_To_One.CalendarData.DailyStreakService;
import uk.ac.cf._5.group14.One_To_One.CalendarData.DayOptimisationRepository;
import uk.ac.cf._5.group14.One_To_One.CalendarData.TaskAiGenerationService;
import uk.ac.cf._5.group14.One_To_One.CalendarData.TaskTemplateService;
import uk.ac.cf._5.group14.One_To_One.DayHealthData.DayHealthPersistenceService;
import uk.ac.cf._5.group14.One_To_One.DayMode.DayModeService;
import uk.ac.cf._5.group14.One_To_One.Goals.GoalLinkService;
import uk.ac.cf._5.group14.One_To_One.PlatformBilling.PlatformSubscriptionService;
import uk.ac.cf._5.group14.One_To_One.ReflectionData.ReflectionService;
import uk.ac.cf._5.group14.One_To_One.ScheduleData.CalendarController;
import uk.ac.cf._5.group14.One_To_One.ScheduleData.ScheduleAppliedRepository;
import uk.ac.cf._5.group14.One_To_One.ScheduleData.ScheduleOccurrenceService;
import uk.ac.cf._5.group14.One_To_One.ScheduleData.ScheduleService;
import uk.ac.cf._5.group14.One_To_One.StrengthLog.Service.WorkoutScheduleService;
import uk.ac.cf._5.group14.One_To_One.StrengthLog.Service.WorkoutSessionService;
import uk.ac.cf._5.group14.One_To_One.UserSettings.CalendarTaskLayoutPreference;
import uk.ac.cf._5.group14.One_To_One.UserSettings.UserSettings;
import uk.ac.cf._5.group14.One_To_One.UserSettings.UserSettingsService;
import uk.ac.cf._5.group14.One_To_One.Users.AuthHelper;
import uk.ac.cf._5.group14.One_To_One.Users.User;
import uk.ac.cf._5.group14.One_To_One.Notifications.NotificationSseRegistry;
import uk.ac.cf._5.group14.One_To_One.Config.DevModeProperties;

/**
 * Tests that calendar routes (month, week, day) return HTTP 200 with required
 * model attributes, that jump controls render with the correct IDs, and that
 * each view shows the Day-view navigation link.
 */
@WebMvcTest(CalendarController.class)
@ActiveProfiles("test")
class CalendarNavigationAndJumpControlsTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean private CalendarTaskService taskService;
    @MockitoBean private CalendarTaskWarningService taskWarningService;
    @MockitoBean private TaskAiGenerationService taskAiGenerationService;
    @MockitoBean private TaskTemplateService taskTemplateService;
    @MockitoBean private UserSettingsService userSettingsService;
    @MockitoBean private PlatformSubscriptionService platformSubscriptionService;
    @MockitoBean private ScheduleService scheduleService;
    @MockitoBean private ScheduleAppliedRepository scheduleAppliedRepository;
    @MockitoBean private ScheduleOccurrenceService scheduleOccurrenceService;
    @MockitoBean private WorkoutScheduleService workoutScheduleService;
    @MockitoBean private WorkoutSessionService workoutSessionService;
    @MockitoBean private GoalLinkService goalLinkService;
    @MockitoBean private DayModeService dayModeService;
    @MockitoBean private ReflectionService reflectionService;
    @MockitoBean private DailyStreakService dailyStreakService;
    @MockitoBean private DayHealthPersistenceService dayHealthPersistenceService;
    @MockitoBean private DayOptimisationRepository dayOptimisationRepository;

    @MockitoBean
    private NotificationSseRegistry sseRegistry;

    private User testUser() {
        User u = new User();
        u.setId(1L);
        u.setUsername("testuser");
        return u;
    }

    private UserSettings defaultSettings(User user) {
        UserSettings s = new UserSettings();
        s.setUser(user);
        s.setUserId(user.getId());
        s.setCalendarTaskLayout(CalendarTaskLayoutPreference.COMBINED_LIST);
        return s;
    }

    // â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€ Month view â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @Test
    void monthViewReturns200WithRequiredModelAttributes() throws Exception {
        User user = testUser();
        given(userSettingsService.getOrCreate(eq(user))).willReturn(defaultSettings(user));
        given(platformSubscriptionService.isPremium(eq(user.getId()), any(Clock.class))).willReturn(false);
        given(scheduleService.findByUser(eq(user))).willReturn(Collections.emptyList());
        given(scheduleAppliedRepository.findByUserAndSchedule(any(), any())).willReturn(Collections.emptyList());
        given(taskService.getTasksByRange(eq(user), any(LocalDate.class), any(LocalDate.class)))
                .willReturn(Collections.emptyMap());
        given(scheduleOccurrenceService.getOccurrencesForUserInMonth(eq(user), anyInt(), anyInt()))
                .willReturn(Collections.emptyMap());

        mvc.perform(get("/calendar")
                        .param("view", "month")
                        .param("month", "3")
                        .param("year", "2026")
                        .sessionAttr("user", user))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("month", "year", "today", "calendarCells", "schedules"));
    }

    @Test
    void monthViewRendersJumpControlsWithCorrectIds() throws Exception {
        User user = testUser();
        given(userSettingsService.getOrCreate(eq(user))).willReturn(defaultSettings(user));
        given(platformSubscriptionService.isPremium(eq(user.getId()), any(Clock.class))).willReturn(false);
        given(scheduleService.findByUser(eq(user))).willReturn(Collections.emptyList());
        given(scheduleAppliedRepository.findByUserAndSchedule(any(), any())).willReturn(Collections.emptyList());
        given(taskService.getTasksByRange(eq(user), any(LocalDate.class), any(LocalDate.class)))
                .willReturn(Collections.emptyMap());
        given(scheduleOccurrenceService.getOccurrencesForUserInMonth(eq(user), anyInt(), anyInt()))
                .willReturn(Collections.emptyMap());

        mvc.perform(get("/calendar")
                        .param("view", "month")
                        .param("month", "3")
                        .param("year", "2026")
                        .sessionAttr("user", user))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("id=\"month-jump-date\"")))
                .andExpect(content().string(containsString("id=\"month-jump-date-go\"")))
                .andExpect(content().string(containsString("id=\"month-jump-task\"")))
                .andExpect(content().string(containsString("id=\"month-jump-task-go\"")))
                .andExpect(content().string(containsString("id=\"month-jump-next-workout\"")));
    }

    @Test
    void monthViewRendersDayViewLink() throws Exception {
        User user = testUser();
        given(userSettingsService.getOrCreate(eq(user))).willReturn(defaultSettings(user));
        given(platformSubscriptionService.isPremium(eq(user.getId()), any(Clock.class))).willReturn(false);
        given(scheduleService.findByUser(eq(user))).willReturn(Collections.emptyList());
        given(scheduleAppliedRepository.findByUserAndSchedule(any(), any())).willReturn(Collections.emptyList());
        given(taskService.getTasksByRange(eq(user), any(LocalDate.class), any(LocalDate.class)))
                .willReturn(Collections.emptyMap());
        given(scheduleOccurrenceService.getOccurrencesForUserInMonth(eq(user), anyInt(), anyInt()))
                .willReturn(Collections.emptyMap());

        mvc.perform(get("/calendar")
                        .param("view", "month")
                        .param("month", "3")
                        .param("year", "2026")
                        .sessionAttr("user", user))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Day view")));
    }

    // â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€ Week view â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @Test
    void weekViewReturns200WithRequiredModelAttributes() throws Exception {
        User user = testUser();
        given(userSettingsService.getOrCreate(eq(user))).willReturn(defaultSettings(user));
        given(platformSubscriptionService.isPremium(eq(user.getId()), any(Clock.class))).willReturn(false);
        given(scheduleService.findByUser(eq(user))).willReturn(Collections.emptyList());
        given(scheduleAppliedRepository.findByUserAndSchedule(any(), any())).willReturn(Collections.emptyList());
        given(taskService.getTasksByRange(eq(user), any(LocalDate.class), any(LocalDate.class)))
                .willReturn(Collections.emptyMap());
        given(scheduleOccurrenceService.getOccurrencesByRange(eq(user), any(LocalDate.class), any(LocalDate.class)))
                .willReturn(Collections.emptyMap());

        mvc.perform(get("/calendar")
                        .param("view", "week")
                        .param("week", "10")
                        .param("weekYear", "2026")
                        .sessionAttr("user", user))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("week", "weekYear", "weekStart", "weekEnd", "today", "schedules"));
    }

    @Test
    void weekViewRendersDayViewLink() throws Exception {
        User user = testUser();
        given(userSettingsService.getOrCreate(eq(user))).willReturn(defaultSettings(user));
        given(platformSubscriptionService.isPremium(eq(user.getId()), any(Clock.class))).willReturn(false);
        given(scheduleService.findByUser(eq(user))).willReturn(Collections.emptyList());
        given(scheduleAppliedRepository.findByUserAndSchedule(any(), any())).willReturn(Collections.emptyList());
        given(taskService.getTasksByRange(eq(user), any(LocalDate.class), any(LocalDate.class)))
                .willReturn(Collections.emptyMap());
        given(scheduleOccurrenceService.getOccurrencesByRange(eq(user), any(LocalDate.class), any(LocalDate.class)))
                .willReturn(Collections.emptyMap());

        mvc.perform(get("/calendar")
                        .param("view", "week")
                        .param("week", "10")
                        .param("weekYear", "2026")
                        .sessionAttr("user", user))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Day view")));
    }

    // â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€ Day view â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @Test
    void dayViewReturns200WithRequiredModelAttributes() throws Exception {
        User user = testUser();
        given(taskService.getTasks(eq(user), any(LocalDate.class))).willReturn(Collections.emptyList());
        given(scheduleOccurrenceService.getOccurrencesForUserOnDate(eq(user), any(LocalDate.class)))
                .willReturn(Collections.emptyList());
        given(workoutScheduleService.findByUserAndDayOfWeek(eq(user), any(Integer.class)))
                .willReturn(Collections.emptyList());
        given(taskTemplateService.listRecents(eq(user), anyInt())).willReturn(Collections.emptyList());
        given(taskTemplateService.listFavourites(eq(user))).willReturn(Collections.emptyList());
        given(taskTemplateService.listAll(eq(user))).willReturn(Collections.emptyList());

        mvc.perform(get("/calendar/day/2026-03-15").sessionAttr("user", user))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("date", "prevDate", "nextDate", "tasks", "occurrences"));
    }

    @Test
    void dayViewRendersAllThreeViewToggleLinks() throws Exception {
        User user = testUser();
        given(taskService.getTasks(eq(user), any(LocalDate.class))).willReturn(Collections.emptyList());
        given(scheduleOccurrenceService.getOccurrencesForUserOnDate(eq(user), any(LocalDate.class)))
                .willReturn(Collections.emptyList());
        given(workoutScheduleService.findByUserAndDayOfWeek(eq(user), any(Integer.class)))
                .willReturn(Collections.emptyList());
        given(taskTemplateService.listRecents(eq(user), anyInt())).willReturn(Collections.emptyList());
        given(taskTemplateService.listFavourites(eq(user))).willReturn(Collections.emptyList());
        given(taskTemplateService.listAll(eq(user))).willReturn(Collections.emptyList());

        mvc.perform(get("/calendar/day/2026-03-15").sessionAttr("user", user))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("calendar-view-toggle")))
                .andExpect(content().string(containsString("/calendar?view=month")))
                .andExpect(content().string(containsString("/calendar?view=week")))
                .andExpect(content().string(containsString("Day")));
    }

    @Test
    void dayViewRendersCanonicalShortcutsButtonOnce() throws Exception {
        User user = testUser();
        given(taskService.getTasks(eq(user), any(LocalDate.class))).willReturn(Collections.emptyList());
        given(scheduleOccurrenceService.getOccurrencesForUserOnDate(eq(user), any(LocalDate.class)))
                .willReturn(Collections.emptyList());
        given(workoutScheduleService.findByUserAndDayOfWeek(eq(user), any(Integer.class)))
                .willReturn(Collections.emptyList());
        given(taskTemplateService.listRecents(eq(user), anyInt())).willReturn(Collections.emptyList());
        given(taskTemplateService.listFavourites(eq(user))).willReturn(Collections.emptyList());
        given(taskTemplateService.listAll(eq(user))).willReturn(Collections.emptyList());

        String html = mvc.perform(get("/calendar/day/2026-03-15").sessionAttr("user", user))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // The shortcuts button must appear exactly once in the rendered HTML
        int count = 0;
        int idx = 0;
        while ((idx = html.indexOf("id=\"day-shortcuts-btn\"", idx)) != -1) {
            count++;
            idx++;
        }
        Assertions.assertEquals(1, count, "Expected exactly one day-shortcuts-btn in rendered HTML");
    }

    // â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€ Test configuration â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @TestConfiguration
    static class TestSecurityConfig {
        @Bean("testNavSecurityFilterChain")
        SecurityFilterChain testNavSecurityFilterChain(HttpSecurity http) throws Exception {
            return http
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                    .build();
        }

        @Bean
        public Clock systemClock() {
            return Clock.system(ZoneId.systemDefault());
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
        public CalendarDayModelBuilder calendarDayModelBuilder() {
            return new CalendarDayModelBuilder();
        }
    
        @Bean
        public DevModeProperties devModeProperties() {
            return new DevModeProperties();
        }
}
}
