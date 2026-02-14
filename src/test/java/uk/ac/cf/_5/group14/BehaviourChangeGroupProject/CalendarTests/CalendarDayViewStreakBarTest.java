package uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarTests;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.time.Clock;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTaskService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTaskWarningService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.DailyCompletionStatus;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.DailyStreakDaySummary;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.DailyStreakService;
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

@WebMvcTest(CalendarController.class)
@ActiveProfiles("test")
public class CalendarDayViewStreakBarTest {

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
    private WorkoutSessionService workoutSessionService;

    @MockitoBean
    private AuthHelper authHelper;

    @MockitoBean
    private UserSettingsService userSettingsService;

    @MockitoBean
    private DailyStreakService dailyStreakService;

    @MockitoBean
    private DayModeService dayModeService;@MockitoBean
    private GoalLinkService goalLinkService;

    @MockitoBean
    private PlatformSubscriptionService platformSubscriptionService;

    @Test
    public void dayViewShouldRenderDailyStreakBar() throws Exception {
        User sessionUser = new User();
        LocalDate date = LocalDate.of(2026, 1, 15);

        given(taskService.getTasks(eq(sessionUser), eq(date))).willReturn(Collections.emptyList());
        given(scheduleOccurrenceService.getOccurrencesForUserOnDate(eq(sessionUser), eq(date))).willReturn(Collections.emptyList());
        given(workoutScheduleService.findByUserAndDayOfWeek(eq(sessionUser), any(Integer.class))).willReturn(Collections.emptyList());
        given(taskTemplateService.listRecents(eq(sessionUser), eq(6))).willReturn(Collections.emptyList());
        given(taskTemplateService.listFavourites(eq(sessionUser))).willReturn(Collections.emptyList());
        given(taskTemplateService.listAll(eq(sessionUser))).willReturn(Collections.emptyList());

        given(dailyStreakService.calculateRange(eq(sessionUser), any(LocalDate.class), any(LocalDate.class), any(LocalDate.class)))
            .willReturn(
                List.of(
                    new DailyStreakDaySummary(
                        LocalDate.of(2026, 1, 13),
                        DailyCompletionStatus.RED,
                        0,
                        0,
                        2,
                        0,
                        0,
                        0
                    ),
                    new DailyStreakDaySummary(
                        LocalDate.of(2026, 1, 14),
                        DailyCompletionStatus.ORANGE,
                        50,
                        1,
                        2,
                        0,
                        0,
                        1
                    ),
                    new DailyStreakDaySummary(
                        LocalDate.of(2026, 1, 15),
                        DailyCompletionStatus.GREEN,
                        100,
                        2,
                        2,
                        1,
                        1,
                        0
                    )
                )
            );

        mvc.perform(get("/calendar/day/2026-01-15").sessionAttr("user", sessionUser))
                .andExpect(status().isOk())
                .andExpect(view().name("calendar/day"))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("data-testid=\"daily-streak-bar\"")))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("0/0 completed (0%)")))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("Remaining tasks: 0, Remaining workouts: 0")));

        mvc.perform(get("/calendar/day/2026-01-15").sessionAttr("user", sessionUser))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("data-testid=\"daily-streak-day\"")))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("bg-emerald-500")))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("3/3 completed (100%)")))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("Tasks left: 0")))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("Workouts left: 0")))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("Logs needed: 0")));
    }

    @TestConfiguration
    static class TestSecurityConfig {
        @Bean("testSecurityFilterChain")
        SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
            return http
                    .csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                    .build();
        }}
}
