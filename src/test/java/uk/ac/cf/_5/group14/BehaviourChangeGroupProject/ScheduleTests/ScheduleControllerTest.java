package uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CustomExerciseData.CustomExercise;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseData.Exercise;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.*;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScheduleControllerTest {

    @Mock private ScheduleService scheduleService;
    @Mock private ScheduleEntryService scheduleEntryService;
    @Mock private ScheduleAppliedRepository scheduleAppliedRepository;
    @Mock private ScheduleOccurrenceService scheduleOccurrenceService;
    @Mock private ScheduleEntryRepository scheduleEntryRepository;
    @Mock private ScheduleOccurrenceRepository scheduleOccurrenceRepository;
    @Mock private ScheduleRepository scheduleRepository;
    @Mock private uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseData.ExerciseRepository exerciseRepository;
    @Mock private uk.ac.cf._5.group14.BehaviourChangeGroupProject.Workout.WorkoutRepository workoutRepository;

    @InjectMocks
    private ScheduleController scheduleController;

    @BeforeEach
    void injectFields() {
        ReflectionTestUtils.setField(scheduleController, "scheduleService", scheduleService);
        ReflectionTestUtils.setField(scheduleController, "scheduleEntryService", scheduleEntryService);
        ReflectionTestUtils.setField(scheduleController, "scheduleAppliedRepository", scheduleAppliedRepository);
        ReflectionTestUtils.setField(scheduleController, "scheduleOccurrenceService", scheduleOccurrenceService);
        ReflectionTestUtils.setField(scheduleController, "scheduleEntryRepository", scheduleEntryRepository);
        ReflectionTestUtils.setField(scheduleController, "scheduleOccurrenceRepository", scheduleOccurrenceRepository);
        ReflectionTestUtils.setField(scheduleController, "scheduleRepository", scheduleRepository);
        ReflectionTestUtils.setField(scheduleController, "exerciseRepository", exerciseRepository);
        ReflectionTestUtils.setField(scheduleController, "workoutRepository", workoutRepository);
    }

    @Test
    void applyScheduleToCalendar_ShouldCreateOccurrencesForEachWeekAndEntry() {
        Long id = 1L;
        User user = new User();
        user.setId(1L);
        Schedule schedule = new Schedule();
        schedule.setId(id);
        schedule.setName("Test");
        when(scheduleService.findById(id)).thenReturn(schedule);
        ScheduleEntry e1 = new ScheduleEntry();
        e1.setDayOfWeek(1);
        e1.setExercise(new Exercise());
        ScheduleEntry e2 = new ScheduleEntry();
        e2.setDayOfWeek(2);
        e2.setCustomExercise(new CustomExercise());
        when(scheduleEntryService.getEntriesBySchedule(schedule)).thenReturn(Arrays.asList(e1, e2));
        LocalDate startDate = LocalDate.of(2023, 9, 25);
        int weeks = 2;
        String result = scheduleController.applyScheduleToCalendar(id, startDate, weeks, user);
        assertEquals("redirect:/calendar", result);
        verify(scheduleAppliedRepository).save(any());
        verify(scheduleOccurrenceRepository, times(4)).save(any(ScheduleOccurrence.class));
    }

    @Test
    void applyScheduleToCalendar_ShouldReturnRedirect_WhenScheduleNotFound() {
        Long id = 999L;
        User user = new User();
        when(scheduleService.findById(id)).thenReturn(null);
        String result = scheduleController.applyScheduleToCalendar(id, LocalDate.now(), 1, user);
        assertEquals("redirect:/calendar", result);
        verify(scheduleAppliedRepository, times(1)).save(any());
        verify(scheduleOccurrenceRepository, never()).save(any());
    }

    @Test
    void applyScheduleToCalendar_ShouldDoNothing_WhenNoEntries() {
        Long id = 1L;
        User user = new User();
        Schedule schedule = new Schedule();
        schedule.setId(id);
        when(scheduleService.findById(id)).thenReturn(schedule);
        when(scheduleEntryService.getEntriesBySchedule(schedule)).thenReturn(Collections.emptyList());
        String result = scheduleController.applyScheduleToCalendar(id, LocalDate.now(), 3, user);
        assertEquals("redirect:/calendar", result);
        verify(scheduleAppliedRepository, times(1)).save(any());
        verify(scheduleOccurrenceRepository, never()).save(any());
    }

    @Test
    void applyScheduleToCalendar_ShouldNotCreateOccurrences_WhenWeeksIsZero() {
        Long id = 1L;
        User user = new User();
        Schedule schedule = new Schedule();
        schedule.setId(id);
        when(scheduleService.findById(id)).thenReturn(schedule);
        ScheduleEntry e1 = new ScheduleEntry();
        e1.setDayOfWeek(1);
        when(scheduleEntryService.getEntriesBySchedule(schedule)).thenReturn(Collections.singletonList(e1));
        String result = scheduleController.applyScheduleToCalendar(id, LocalDate.now(), 0, user);
        assertEquals("redirect:/calendar", result);
        verify(scheduleAppliedRepository).save(any());
        verify(scheduleOccurrenceRepository, never()).save(any());
    }

}
