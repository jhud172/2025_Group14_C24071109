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
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CalendarController.class)
@ActiveProfiles("test")
class CalendarDayHubHeaderMvcTest {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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
    private WorkoutScheduleService workoutScheduleService;

    @MockitoBean
    private WorkoutSessionService workoutSessionService;@MockitoBean
    private UserSettingsService userSettingsService;

    @MockitoBean
    private DayModeService dayModeService;@MockitoBean
    private GoalLinkService goalLinkService;

    @MockitoBean
    private PlatformSubscriptionService platformSubscriptionService;

    @Test
    void dayViewRendersHubHeaderAndTitleContainsDate() throws Exception {
        User sessionUser = new User();
        sessionUser.setId(1L);
        LocalDate date = LocalDate.of(2026, 1, 15);

        given(taskService.getTasks(eq(sessionUser), eq(date))).willReturn(Collections.emptyList());
        given(scheduleOccurrenceService.getOccurrencesForUserOnDate(eq(sessionUser), eq(date))).willReturn(Collections.emptyList());
        given(workoutScheduleService.findByUserAndDayOfWeek(eq(sessionUser), any(Integer.class))).willReturn(Collections.emptyList());

        given(taskTemplateService.listRecents(eq(sessionUser), eq(6))).willReturn(Collections.emptyList());
        given(taskTemplateService.listFavourites(eq(sessionUser))).willReturn(Collections.emptyList());
        given(taskTemplateService.listAll(eq(sessionUser))).willReturn(Collections.emptyList());

        mvc.perform(get("/calendar/day/2026-01-15").sessionAttr("user", sessionUser))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("data-testid=\"day-hub-header\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Day view: ")))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("2026-01-15")))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("ring-white/20")));
    }

    @Test
    void dayViewRendersTodayBadgeWhenDateIsToday() throws Exception {
        User sessionUser = new User();
        sessionUser.setId(1L);
        LocalDate today = LocalDate.now();

        given(taskService.getTasks(eq(sessionUser), eq(today))).willReturn(Collections.emptyList());
        given(scheduleOccurrenceService.getOccurrencesForUserOnDate(eq(sessionUser), eq(today))).willReturn(Collections.emptyList());
        given(workoutScheduleService.findByUserAndDayOfWeek(eq(sessionUser), any(Integer.class))).willReturn(Collections.emptyList());

        given(taskTemplateService.listRecents(eq(sessionUser), eq(6))).willReturn(Collections.emptyList());
        given(taskTemplateService.listFavourites(eq(sessionUser))).willReturn(Collections.emptyList());
        given(taskTemplateService.listAll(eq(sessionUser))).willReturn(Collections.emptyList());

        mvc.perform(get("/calendar/day/" + today.format(DATE_FORMAT)).sessionAttr("user", sessionUser))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("data-testid=\"today-badge\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Today")));
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
    }
}
