package uk.ac.cf._5.group14.BehaviourChangeGroupProject.ChatTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTask;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTaskRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.DailyStreakService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Chat.ChatContextBuilder;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Chat.ChatSummaryDto;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleOccurrenceRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatContextBuilderTest {

    @Mock
    private CalendarTaskRepository calendarTaskRepository;

    @Mock
    private ScheduleOccurrenceRepository scheduleOccurrenceRepository;

    @Mock
    private DailyStreakService dailyStreakService;

    private Clock clock;

    private ChatContextBuilder chatContextBuilder;

    private User user;

    @BeforeEach
    void setUp() {
        // Fix clock to 09:00 UTC (morning)
        clock = Clock.fixed(Instant.parse("2025-06-10T09:00:00Z"), ZoneOffset.UTC);
        chatContextBuilder = new ChatContextBuilder(
                calendarTaskRepository,
                scheduleOccurrenceRepository,
                dailyStreakService,
                clock
        );
        user = new User();
        user.setId(1L);
        user.setFirstName("Alice");
    }

    @Test
    void buildSummary_returnsGoodMorningGreetingInMorning() {
        when(calendarTaskRepository.findByUserAndDateOrderByTime(eq(user), any())).thenReturn(List.of());
        when(scheduleOccurrenceRepository.findByUserAndDate(eq(user), any())).thenReturn(List.of());
        when(scheduleOccurrenceRepository.findActiveByUser(user)).thenReturn(List.of());
        when(dailyStreakService.calculateRange(eq(user), any(), any(), any())).thenReturn(List.of());

        ChatSummaryDto summary = chatContextBuilder.buildSummary(user);

        assertTrue(summary.greeting().startsWith("Good morning"), "Expected morning greeting but got: " + summary.greeting());
        assertTrue(summary.greeting().contains("Alice"), "Greeting should include first name");
    }

    @Test
    void buildSummary_countsCompletedTasksCorrectly() {
        CalendarTask done = new CalendarTask();
        done.setCompleted(true);
        CalendarTask notDone = new CalendarTask();
        notDone.setCompleted(false);

        when(calendarTaskRepository.findByUserAndDateOrderByTime(eq(user), any()))
                .thenReturn(List.of(done, notDone, notDone));
        when(scheduleOccurrenceRepository.findByUserAndDate(eq(user), any())).thenReturn(List.of());
        when(scheduleOccurrenceRepository.findActiveByUser(user)).thenReturn(List.of());
        when(dailyStreakService.calculateRange(eq(user), any(), any(), any())).thenReturn(List.of());

        ChatSummaryDto summary = chatContextBuilder.buildSummary(user);

        assertEquals(1, summary.tasksDone());
        assertEquals(3, summary.tasksTotal());
    }

    @Test
    void buildSummary_completionPctIsCorrectForMixedItems() {
        CalendarTask done = new CalendarTask();
        done.setCompleted(true);
        CalendarTask notDone = new CalendarTask();
        notDone.setCompleted(false);

        when(calendarTaskRepository.findByUserAndDateOrderByTime(eq(user), any()))
                .thenReturn(List.of(done, notDone));
        when(scheduleOccurrenceRepository.findByUserAndDate(eq(user), any())).thenReturn(List.of());
        when(scheduleOccurrenceRepository.findActiveByUser(user)).thenReturn(List.of());
        when(dailyStreakService.calculateRange(eq(user), any(), any(), any())).thenReturn(List.of());

        ChatSummaryDto summary = chatContextBuilder.buildSummary(user);

        assertEquals(50, summary.completionPct());
    }

    @Test
    void buildSummary_noTasksOrWorkouts_zeroCompletionPct() {
        when(calendarTaskRepository.findByUserAndDateOrderByTime(eq(user), any())).thenReturn(List.of());
        when(scheduleOccurrenceRepository.findByUserAndDate(eq(user), any())).thenReturn(List.of());
        when(scheduleOccurrenceRepository.findActiveByUser(user)).thenReturn(List.of());
        when(dailyStreakService.calculateRange(eq(user), any(), any(), any())).thenReturn(List.of());

        ChatSummaryDto summary = chatContextBuilder.buildSummary(user);

        assertEquals(0, summary.completionPct());
        assertEquals(0, summary.streakDays());
        assertNull(summary.nextWorkoutName());
    }

    @Test
    void computeTimeTheme_returnsCorrectThemeForEachHour() {
        // Morning: 09:00
        assertEquals("morning", chatContextBuilder.computeTimeTheme(
                java.time.LocalTime.of(9, 0)));
        // Midday: 13:00
        assertEquals("midday", chatContextBuilder.computeTimeTheme(
                java.time.LocalTime.of(13, 0)));
        // Evening: 18:00
        assertEquals("evening", chatContextBuilder.computeTimeTheme(
                java.time.LocalTime.of(18, 0)));
        // Night: 23:00
        assertEquals("night", chatContextBuilder.computeTimeTheme(
                java.time.LocalTime.of(23, 0)));
    }
}
