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
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTaskWarning;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTaskWarningService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTaskWarningTriggerType;
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
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.UserSettings.CalendarTaskLayoutPreference;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.UserSettings.CalendarTaskOrderingPreference;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.UserSettings.UserSettings;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.UserSettings.UserSettingsService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import java.time.Clock;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.mock;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CalendarController.class)
@ActiveProfiles("test")
class CalendarDayTaskDrawerRenderTest {

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
    private DayModeService dayModeService;@MockitoBean
    private GoalLinkService goalLinkService;

    @MockitoBean
    private PlatformSubscriptionService platformSubscriptionService;

    @Test
    void dayViewRendersTaskDrawerAndHiddenContent() throws Exception {
        User sessionUser = new User();
        sessionUser.setId(1L);
        LocalDate date = LocalDate.of(2026, 1, 15);

        CalendarTask task = new CalendarTask();
        task.setId(7L);
        task.setUser(sessionUser);
        task.setTitle("My task");
        task.setDate(date);
        task.setTime(LocalTime.of(9, 0));
        task.setNotes("Some notes");
        task.setExercise(false);
        task.setCompleted(false);
        task.setRequiresLog(true);
        task.setExerciseLog(null);

        CalendarTaskWarning warning = new CalendarTaskWarning();
        warning.setId(99L);
        warning.setTask(task);
        warning.setTriggerType(CalendarTaskWarningTriggerType.TIME);
        warning.setTriggerTime(LocalTime.of(8, 30));
        warning.setCreatedAt(Instant.parse("2026-01-01T00:00:00Z"));

        UserSettings settings = new UserSettings();
        settings.setCalendarTaskOrdering(CalendarTaskOrderingPreference.CHRONOLOGICAL);
        settings.setCalendarTaskLayout(CalendarTaskLayoutPreference.COMBINED_LIST);

        given(authHelper.getAuthenticatedUser()).willReturn(sessionUser);
        given(userSettingsService.getOrCreate(eq(sessionUser))).willReturn(settings);

        given(taskService.getTasks(eq(sessionUser), eq(date))).willReturn(List.of(task));
        given(taskWarningService.listWarningsForTasks(any())).willReturn(Map.of(7L, List.of(warning)));

        given(scheduleOccurrenceService.getOccurrencesForUserOnDate(eq(sessionUser), eq(date))).willReturn(Collections.emptyList());
        given(workoutScheduleService.findByUserAndDayOfWeek(eq(sessionUser), any(Integer.class))).willReturn(Collections.emptyList());
        given(taskTemplateService.listRecents(eq(sessionUser), eq(6))).willReturn(Collections.emptyList());
        given(taskTemplateService.listFavourites(eq(sessionUser))).willReturn(Collections.emptyList());
        given(taskTemplateService.listAll(eq(sessionUser))).willReturn(Collections.emptyList());

        mvc.perform(get("/calendar/day/2026-01-15").sessionAttr("user", sessionUser))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data-testid=\"task-drawer\"")))
                .andExpect(content().string(containsString("id=\"task-drawer-content-7\"")))
                .andExpect(content().string(containsString("data-testid=\"task-drawer-title\"")))
                .andExpect(content().string(containsString("Some notes")))
                .andExpect(content().string(containsString("Time warning at")))
                .andExpect(content().string(containsString("Log needed")))
                .andExpect(content().string(containsString("/calendar/day/2026-01-15/toggle-complete")))
                .andExpect(content().string(containsString("/calendar/task/7/edit-inline")))
                .andExpect(content().string(containsString("/calendar/task/7/delete")));
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
