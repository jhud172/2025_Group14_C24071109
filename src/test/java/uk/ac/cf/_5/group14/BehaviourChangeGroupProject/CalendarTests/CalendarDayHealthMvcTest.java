package uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTaskService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTaskWarningService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.TaskAiGenerationService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.TaskTemplateService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.DayHealthData.DayHealthPersistenceService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.DayHealthData.DayHealthService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.CalendarController;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleOccurrenceService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.StrengthLog.Service.WorkoutScheduleService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.StrengthLog.Service.WorkoutSessionService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.UserSettings.UserSettingsService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CalendarController.class)
@AutoConfigureMockMvc(addFilters = false)
class CalendarDayHealthMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean private CalendarTaskService taskService;
    @MockitoBean private CalendarTaskWarningService taskWarningService;
    @MockitoBean private TaskTemplateService taskTemplateService;
    @MockitoBean private TaskAiGenerationService taskAiGenerationService;
    @MockitoBean private UserSettingsService userSettingsService;
    @MockitoBean private ScheduleService scheduleService;
    @MockitoBean private ScheduleOccurrenceService scheduleOccurrenceService;
    @MockitoBean private WorkoutScheduleService workoutScheduleService;
    @MockitoBean private WorkoutSessionService workoutSessionService;
    @MockitoBean private DayHealthPersistenceService dayHealthPersistenceService;

    // Required by UserSettingsModelAdvice
    @MockitoBean private AuthHelper authHelper;

    private void stubDayViewDependencies(LocalDate date) {
        when(taskService.getTasks(any(User.class), eq(date))).thenReturn(List.of());
        when(scheduleOccurrenceService.getOccurrencesForUserOnDate(any(User.class), eq(date))).thenReturn(List.of());
        when(workoutScheduleService.findByUserAndDayOfWeek(any(User.class), anyInt())).thenReturn(List.of());
        when(workoutSessionService.findByUserDateAndWorkout(any(User.class), eq(date), any())).thenReturn(java.util.Optional.empty());

        when(taskTemplateService.listRecents(any(User.class), anyInt())).thenReturn(List.of());
        when(taskTemplateService.listFavourites(any(User.class))).thenReturn(List.of());
        when(taskTemplateService.listAll(any(User.class))).thenReturn(List.of());

        when(userSettingsService.getOrCreate(any(User.class))).thenReturn(null);
        when(authHelper.getAuthenticatedUser()).thenReturn(null);
    }

    @Test
    void shouldRenderDayHealthWhenProvided() throws Exception {
        User sessionUser = new User();
        sessionUser.setId(1L);

        LocalDate date = LocalDate.of(2026, 1, 15);

        stubDayViewDependencies(date);

        when(dayHealthPersistenceService.getSavedAdvice(any(User.class), eq(date))).thenReturn(
            new DayHealthService.DayHealthAdvice(
                "Health analysis text",
                List.of("Suggestion A", "Suggestion B"),
                "Tomorrow looks heavy (6 planned items)."
            )
        );

        mockMvc.perform(get("/calendar/day/2026-01-15").sessionAttr("user", sessionUser))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Day Health")))
                .andExpect(content().string(containsString("data-testid=\"day-health-primary\"")))
                .andExpect(content().string(containsString("Health analysis text")))
                .andExpect(content().string(containsString("data-testid=\"day-health-suggestions\"")))
                .andExpect(content().string(containsString("Suggestion A")))
                .andExpect(content().string(containsString("Suggestion B")))
                .andExpect(content().string(containsString("data-testid=\"day-health-watchout\"")))
                .andExpect(content().string(containsString("Tomorrow looks heavy")));
    }

    @Test
    void shouldShowGenerateButtonWhenNoAdvice() throws Exception {
        User sessionUser = new User();
        sessionUser.setId(1L);

        LocalDate date = LocalDate.of(2026, 1, 15);
        stubDayViewDependencies(date);

        when(dayHealthPersistenceService.getSavedAdvice(any(User.class), eq(date))).thenReturn(null);

        mockMvc.perform(get("/calendar/day/2026-01-15").sessionAttr("user", sessionUser))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Day Health")))
            .andExpect(content().string(containsString("data-testid=\"day-health-generate\"")));
    }

    @Test
    void shouldRemoveGenerateButtonAfterGeneration() throws Exception {
        User sessionUser = new User();
        sessionUser.setId(1L);

        LocalDate date = LocalDate.of(2026, 1, 15);
        stubDayViewDependencies(date);

        DayHealthService.DayHealthAdvice advice = new DayHealthService.DayHealthAdvice(
            "Health analysis text",
            List.of("Suggestion A", "Suggestion B"),
            null
        );

        when(dayHealthPersistenceService.getSavedAdvice(any(User.class), eq(date)))
            .thenReturn(null)
            .thenReturn(advice);
        when(dayHealthPersistenceService.generateOnce(any(User.class), eq(date))).thenReturn(advice);

        // First load: no saved content, so show button.
        mockMvc.perform(get("/calendar/day/2026-01-15").sessionAttr("user", sessionUser))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("data-testid=\"day-health-generate\"")));

        // Generate: redirects back.
        mockMvc.perform(post("/calendar/day/2026-01-15/day-health/generate").sessionAttr("user", sessionUser))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/calendar/day/2026-01-15"));

        // Subsequent load: DB content should show, button should be absent.
        mockMvc.perform(get("/calendar/day/2026-01-15").sessionAttr("user", sessionUser))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("data-testid=\"day-health-primary\"")))
            .andExpect(content().string(containsString("Health analysis text")))
            .andExpect(content().string(not(containsString("data-testid=\"day-health-generate\""))));
    }

    @Test
    void postGenerateDayHealthRedirectsBackToDay() throws Exception {
        User sessionUser = new User();
        sessionUser.setId(1L);

        LocalDate date = LocalDate.of(2026, 1, 15);

        when(dayHealthPersistenceService.generateOnce(any(User.class), eq(date))).thenReturn(
            new DayHealthService.DayHealthAdvice(
                "Health analysis text",
                List.of("Suggestion A", "Suggestion B"),
                null
            )
        );

        mockMvc.perform(post("/calendar/day/2026-01-15/day-health/generate").sessionAttr("user", sessionUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/calendar/day/2026-01-15"));
    }
}
