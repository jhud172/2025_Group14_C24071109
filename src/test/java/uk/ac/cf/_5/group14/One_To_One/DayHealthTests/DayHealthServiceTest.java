package uk.ac.cf._5.group14.One_To_One.DayHealthTests;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import uk.ac.cf._5.group14.One_To_One.BehaviourMemoryData.BehaviourMemoryService;
import uk.ac.cf._5.group14.One_To_One.CalendarData.CalendarTask;
import uk.ac.cf._5.group14.One_To_One.CalendarData.CalendarTaskService;
import uk.ac.cf._5.group14.One_To_One.DayHealthData.DayHealthAiService;
import uk.ac.cf._5.group14.One_To_One.DayHealthData.DayHealthService;
import uk.ac.cf._5.group14.One_To_One.StrengthLog.Service.WorkoutScheduleService;
import uk.ac.cf._5.group14.One_To_One.StrengthLog.Service.WorkoutSessionService;
import uk.ac.cf._5.group14.One_To_One.StrengthLog.WorkoutSession;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class DayHealthServiceTest {

    @Test
    void shouldIncludePreviousAndUpcomingContextWhenCallingAi() {
        CalendarTaskService taskService = mock(CalendarTaskService.class);
        WorkoutScheduleService workoutScheduleService = mock(WorkoutScheduleService.class);
        WorkoutSessionService workoutSessionService = mock(WorkoutSessionService.class);
        DayHealthAiService aiService = mock(DayHealthAiService.class);
        BehaviourMemoryService behaviourMemoryService = mock(BehaviourMemoryService.class);

        DayHealthService service = new DayHealthService(taskService, workoutScheduleService, workoutSessionService, aiService, behaviourMemoryService);

        User user = new User();
        user.setId(5L);

        LocalDate date = LocalDate.of(2026, 1, 15);

        // tasks: upcoming heavy day
        CalendarTask t1 = new CalendarTask();
        CalendarTask t2 = new CalendarTask();
        CalendarTask t3 = new CalendarTask();
        CalendarTask t4 = new CalendarTask();
        CalendarTask t5 = new CalendarTask();
        CalendarTask t6 = new CalendarTask();

        LocalDate heavy = date.plusDays(2);

        when(taskService.getTasksByRange(eq(user), any(LocalDate.class), any(LocalDate.class)))
                .thenAnswer(inv -> {
                    LocalDate start = inv.getArgument(1);
                    LocalDate end = inv.getArgument(2);
                    if (start.equals(date) && end.equals(date)) {
                        return Map.of(date, List.of());
                    }
                    if (start.equals(heavy) && end.isAfter(heavy)) {
                        return Map.of(heavy, List.of(t1, t2, t3, t4, t5, t6));
                    }
                    return Map.of();
                });

        when(workoutScheduleService.findByUserAndDayOfWeek(eq(user), anyInt())).thenReturn(List.of());
        when(workoutSessionService.findByUserAndDate(eq(user), any(LocalDate.class))).thenReturn(List.of());

        when(behaviourMemoryService.maybeGetAiContext(eq(user)))
            .thenReturn(Optional.of("Behaviour memory (last 14 days, aggregates):\n- Completion habits: 1 GREEN\nUse these only as gentle context."));

        when(aiService.suggestDayHealth(eq(date), anyString())).thenReturn("AI analysis");

        var advice = service.getDayHealthAdvice(user, date);

        assertThat(advice).isNotNull();
        assertThat(advice.primaryMessage()).isEqualTo("AI analysis");
        assertThat(advice.suggestions()).hasSize(2);

        ArgumentCaptor<String> ctxCaptor = ArgumentCaptor.forClass(String.class);
        verify(aiService).suggestDayHealth(eq(date), ctxCaptor.capture());

        String ctx = ctxCaptor.getValue();
        assertThat(ctx).contains("Previous 7 days");
        assertThat(ctx).contains("Current day");
        assertThat(ctx).contains("Next 7 days planned load");
        assertThat(ctx).contains("Heaviest upcoming day");
        assertThat(ctx).contains("Behaviour memory");
    }

    @Test
    void shouldFallbackWhenAiReturnsNull() {
        CalendarTaskService taskService = mock(CalendarTaskService.class);
        WorkoutScheduleService workoutScheduleService = mock(WorkoutScheduleService.class);
        WorkoutSessionService workoutSessionService = mock(WorkoutSessionService.class);
        DayHealthAiService aiService = mock(DayHealthAiService.class);
        BehaviourMemoryService behaviourMemoryService = mock(BehaviourMemoryService.class);

        DayHealthService service = new DayHealthService(taskService, workoutScheduleService, workoutSessionService, aiService, behaviourMemoryService);

        User user = new User();
        user.setId(10L);

        LocalDate date = LocalDate.of(2026, 1, 15);

        when(taskService.getTasksByRange(eq(user), any(LocalDate.class), any(LocalDate.class))).thenReturn(Map.of());
        when(workoutScheduleService.findByUserAndDayOfWeek(eq(user), anyInt())).thenReturn(List.of());
        when(workoutSessionService.findByUserAndDate(eq(user), any(LocalDate.class))).thenReturn(List.of());
        when(aiService.suggestDayHealth(eq(date), anyString())).thenReturn(null);
        when(behaviourMemoryService.maybeGetAiContext(eq(user))).thenReturn(Optional.empty());

        var advice = service.getDayHealthAdvice(user, date);

        assertThat(advice).isNotNull();
        assertThat(advice.primaryMessage()).isNotBlank();
        assertThat(advice.suggestions()).hasSize(2);
    }

    @Test
    void shouldReturnWatchOutWhenTomorrowIsHeavy() {
        CalendarTaskService taskService = mock(CalendarTaskService.class);
        WorkoutScheduleService workoutScheduleService = mock(WorkoutScheduleService.class);
        WorkoutSessionService workoutSessionService = mock(WorkoutSessionService.class);
        DayHealthAiService aiService = mock(DayHealthAiService.class);
        BehaviourMemoryService behaviourMemoryService = mock(BehaviourMemoryService.class);

        DayHealthService service = new DayHealthService(taskService, workoutScheduleService, workoutSessionService, aiService, behaviourMemoryService);

        User user = new User();
        user.setId(10L);

        LocalDate date = LocalDate.of(2026, 1, 16);

        CalendarTask t1 = new CalendarTask();
        CalendarTask t2 = new CalendarTask();
        CalendarTask t3 = new CalendarTask();
        CalendarTask t4 = new CalendarTask();
        CalendarTask t5 = new CalendarTask();
        CalendarTask t6 = new CalendarTask();

        when(taskService.getTasksByRange(eq(user), any(LocalDate.class), any(LocalDate.class)))
            .thenAnswer(inv -> {
                LocalDate start = inv.getArgument(1);
                LocalDate end = inv.getArgument(2);

                if (start.equals(date) && end.equals(date)) {
                    return Map.of(date, List.of());
                }
                if (start.equals(date.plusDays(1)) && end.equals(date.plusDays(7))) {
                    return Map.of(date.plusDays(1), List.of(t1, t2, t3, t4, t5, t6));
                }
                return Map.of();
            });

        when(workoutScheduleService.findByUserAndDayOfWeek(eq(user), anyInt())).thenReturn(List.of());
        when(workoutSessionService.findByUserAndDate(eq(user), any(LocalDate.class))).thenReturn(List.of());
        when(aiService.suggestDayHealth(eq(date), anyString())).thenReturn(null);
        when(behaviourMemoryService.maybeGetAiContext(eq(user))).thenReturn(Optional.empty());

        var advice = service.getDayHealthAdvice(user, date);

        assertThat(advice).isNotNull();
        assertThat(advice.suggestions()).hasSize(2);
        assertThat(advice.watchOut()).isNotBlank();
        assertThat(advice.watchOut()).contains("Tomorrow");
    }

    @Test
    void shouldRotatePrimaryMessageAcrossDatesDeterministically() {
        CalendarTaskService taskService = mock(CalendarTaskService.class);
        WorkoutScheduleService workoutScheduleService = mock(WorkoutScheduleService.class);
        WorkoutSessionService workoutSessionService = mock(WorkoutSessionService.class);
        DayHealthAiService aiService = mock(DayHealthAiService.class);
        BehaviourMemoryService behaviourMemoryService = mock(BehaviourMemoryService.class);

        DayHealthService service = new DayHealthService(taskService, workoutScheduleService, workoutSessionService, aiService, behaviourMemoryService);

        User user = new User();
        user.setId(5L);

        // Force fallback (no AI) and a stable branch: prevAvg=0, todayPlanned=0, upcomingHeavy=false.
        when(aiService.suggestDayHealth(any(LocalDate.class), anyString())).thenReturn(null);
        when(behaviourMemoryService.maybeGetAiContext(eq(user))).thenReturn(Optional.empty());
        when(taskService.getTasksByRange(eq(user), any(LocalDate.class), any(LocalDate.class))).thenReturn(Map.of());
        when(workoutScheduleService.findByUserAndDayOfWeek(eq(user), anyInt())).thenReturn(List.of());
        when(workoutSessionService.findByUserAndDate(eq(user), any(LocalDate.class))).thenReturn(List.of());

        LocalDate d1 = LocalDate.of(2026, 1, 16);

        int idx1 = Math.floorMod(Objects.hash(user.getId(), d1), 3);
        List<String> pool = List.of(
            "The last week looks a bit heavy relative to completions. Today is a good day to simplify and rebuild momentum.",
            "Your recent completion pattern suggests load might be high. Keep today light and focus on consistency.",
            "Recent days look like theyâ€™ve been tough to finish. Aim for a smaller, winnable plan today."
        );

        var a1 = service.getDayHealthAdvice(user, d1);
        var a1Repeat = service.getDayHealthAdvice(user, d1);

        assertThat(a1.primaryMessage()).isEqualTo(pool.get(idx1));
        assertThat(a1Repeat.primaryMessage()).isEqualTo(a1.primaryMessage());

        LocalDate d2 = null;
        for (int i = 1; i <= 10; i++) {
            LocalDate candidate = d1.plusDays(i);
            int idx2 = Math.floorMod(Objects.hash(user.getId(), candidate), 3);
            if (idx2 != idx1) {
                d2 = candidate;
                break;
            }
        }
        assertThat(d2).as("should find a date with different rotation index").isNotNull();

        var a2 = service.getDayHealthAdvice(user, d2);
        assertThat(a2.primaryMessage()).isNotEqualTo(a1.primaryMessage());
    }
}
