package uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTask;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTaskService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTaskWarningService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.TaskAiGenerationService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.TaskTemplateService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ReflectionData.ReflectionResult;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ReflectionData.ReflectionService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CalendarController.class)
@AutoConfigureMockMvc(addFilters = false)
class CalendarDayReflectionPostMvcTest {

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
    @MockitoBean private ReflectionService reflectionService;

    // Required by UserSettingsModelAdvice
    @MockitoBean private AuthHelper authHelper;

    @Test
    void shouldStoreAiResultInFlashAttributes() throws Exception {
        User sessionUser = new User();
        sessionUser.setId(1L);

        LocalDate date = LocalDate.of(2026, 1, 15);

        CalendarTask completed = new CalendarTask();
        completed.setTitle("Task");
        completed.setCompleted(true);

        when(taskService.getTasks(any(User.class), eq(date))).thenReturn(List.of(completed));
        when(workoutScheduleService.findByUserAndDayOfWeek(any(User.class), anyInt())).thenReturn(List.of());

        when(reflectionService.generate(any(User.class), eq(date), any(), anyList(), anyInt(), anyInt(), anyString(), any()))
                .thenReturn(new ReflectionResult("Summary", "Suggestions"));

        mockMvc.perform(
                        post("/calendar/day/2026-01-15/reflection")
                                .sessionAttr("user", sessionUser)
                                .param("reflection", "It was good")
                                .param("notes", "Keep it casual")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/calendar/day/2026-01-15"))
                .andExpect(flash().attribute("reflectionPerformanceSummary", "Summary"))
                .andExpect(flash().attribute("reflectionImprovementSuggestions", "Suggestions"));
    }
}
