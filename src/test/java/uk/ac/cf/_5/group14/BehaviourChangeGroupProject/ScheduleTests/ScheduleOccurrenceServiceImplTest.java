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
public class ScheduleOccurrenceServiceImplTest {

    @Mock private ScheduleEntryService scheduleEntryService;
    @Mock private ScheduleEntryRepository scheduleEntryRepository;
    @Mock private ScheduleOccurrenceRepository scheduleOccurrenceRepository;

    @InjectMocks
    private ScheduleOccurrenceServiceImpl scheduleOccurrenceService;

    @BeforeEach
    void injectFields() {
        ReflectionTestUtils.setField(scheduleOccurrenceService, "scheduleEntryService", scheduleEntryService);
        ReflectionTestUtils.setField(scheduleOccurrenceService, "scheduleEntryRepository", scheduleEntryRepository);
        ReflectionTestUtils.setField(scheduleOccurrenceService, "scheduleOccurrenceRepository", scheduleOccurrenceRepository);
    }

    @Test
    void generateOccurrences_ShouldReturnEarly_WhenArgumentsNull() {
        scheduleOccurrenceService.generateOccurrencesForSchedule(null, new User(), LocalDate.now(), LocalDate.now(), 1);
        verify(scheduleOccurrenceRepository, never()).save(any());
        scheduleOccurrenceService.generateOccurrencesForSchedule(new Schedule(), null, LocalDate.now(), LocalDate.now(), 1);
        verify(scheduleOccurrenceRepository, never()).save(any());
        scheduleOccurrenceService.generateOccurrencesForSchedule(new Schedule(), new User(), null, LocalDate.now(), 1);
        verify(scheduleOccurrenceRepository, never()).save(any());
        scheduleOccurrenceService.generateOccurrencesForSchedule(new Schedule(), new User(), LocalDate.now(), null, 1);
        verify(scheduleOccurrenceRepository, never()).save(any());
    }

    @Test
    void generateOccurrences_ShouldReturnEarly_WhenNoEntries() {
        Schedule schedule = new Schedule();
        schedule.setId(1L);
        User user = new User();
        LocalDate start = LocalDate.of(2023, 9, 25);
        LocalDate end = LocalDate.of(2023, 9, 27);
        when(scheduleEntryService.getEntries(schedule.getId())).thenReturn(Collections.emptyList());
        scheduleOccurrenceService.generateOccurrencesForSchedule(schedule, user, start, end, 1);
        verify(scheduleOccurrenceRepository, never()).save(any());
    }

    @Test
    void generateOccurrences_ShouldCreateOccurrencesForMatchingDays() {
        Schedule schedule = new Schedule();
        schedule.setId(1L);
        schedule.setName("Test");
        User user = new User();
        user.setId(1L);
        ScheduleEntry mondayEntry = new ScheduleEntry();
        mondayEntry.setDayOfWeek(1);
        Exercise exercise = new Exercise();
        mondayEntry.setExercise(exercise);
        ScheduleEntry tuesdayEntry = new ScheduleEntry();
        tuesdayEntry.setDayOfWeek(2);
        CustomExercise customExercise = new CustomExercise();
        tuesdayEntry.setCustomExercise(customExercise);
        when(scheduleEntryService.getEntries(schedule.getId())).thenReturn(Arrays.asList(mondayEntry, tuesdayEntry));
        LocalDate start = LocalDate.of(2023, 9, 25);
        LocalDate end = LocalDate.of(2023, 9, 26);
        scheduleOccurrenceService.generateOccurrencesForSchedule(schedule, user, start, end, 1);
        ArgumentCaptor<ScheduleOccurrence> captor = ArgumentCaptor.forClass(ScheduleOccurrence.class);
        verify(scheduleOccurrenceRepository, times(2)).save(captor.capture());
        List<ScheduleOccurrence> saved = captor.getAllValues();
        ScheduleOccurrence occ0 = saved.get(0);
        assertEquals(user, occ0.getUser());
        assertEquals(schedule, occ0.getSchedule());
        assertEquals("Test", occ0.getScheduleName());
        assertEquals(start, occ0.getDate());
        assertEquals(exercise, occ0.getExercise());
        assertNull(occ0.getCustomExercise());
        ScheduleOccurrence occ1 = saved.get(1);
        assertEquals(user, occ1.getUser());
        assertEquals(schedule, occ1.getSchedule());
        assertEquals("Test", occ1.getScheduleName());
        assertEquals(end, occ1.getDate());
        assertNull(occ1.getExercise());
        assertEquals(customExercise, occ1.getCustomExercise());
    }

    @Test
    void getOccurrencesForUserOnDate_ShouldReturnList() {
        User user = new User();
        user.setId(1L);
        LocalDate date = LocalDate.now();
        List<ScheduleOccurrence> list = Arrays.asList(new ScheduleOccurrence(), new ScheduleOccurrence());
        when(scheduleOccurrenceRepository.findByUserAndDate(user, date)).thenReturn(list);
        List<ScheduleOccurrence> result = scheduleOccurrenceService.getOccurrencesForUserOnDate(user, date);
        assertEquals(list, result);
    }

    @Test
    void getOccurrencesForUserInMonth_ShouldGroupByDate() {
        User user = new User();
        LocalDate date1 = LocalDate.of(2023, 9, 25);
        LocalDate date2 = LocalDate.of(2023, 9, 26);
        ScheduleOccurrence occ1 = new ScheduleOccurrence();
        occ1.setDate(date1);
        ScheduleOccurrence occ2 = new ScheduleOccurrence();
        occ2.setDate(date1);
        ScheduleOccurrence occ3 = new ScheduleOccurrence();
        occ3.setDate(date2);
        when(scheduleOccurrenceRepository.findByUserAndDateBetween(eq(user), any(), any())).thenReturn(Arrays.asList(occ1, occ2, occ3));
        Map<LocalDate, List<ScheduleOccurrence>> result = scheduleOccurrenceService.getOccurrencesForUserInMonth(user, 2023, 9);
        assertEquals(2, result.size());
        assertEquals(2, result.get(date1).size());
        assertEquals(1, result.get(date2).size());
    }

    @Test
    void getActiveSchedulesForUser_ShouldReturnList() {
        User user = new User();
        List<ScheduleOccurrence> list = Arrays.asList(new ScheduleOccurrence());
        when(scheduleOccurrenceRepository.findActiveByUser(user)).thenReturn(list);
        List<ScheduleOccurrence> result = scheduleOccurrenceService.getActiveSchedulesForUser(user);
        assertEquals(list, result);
    }

    @Test
    void getOccurrencesByRange_ShouldGroupByDate() {
        User user = new User();
        LocalDate date1 = LocalDate.of(2023, 9, 25);
        LocalDate date2 = LocalDate.of(2023, 9, 26);
        ScheduleOccurrence occ1 = new ScheduleOccurrence();
        occ1.setDate(date1);
        ScheduleOccurrence occ2 = new ScheduleOccurrence();
        occ2.setDate(date2);
        when(scheduleOccurrenceRepository.findByUserAndDateBetween(eq(user), any(), any())).thenReturn(Arrays.asList(occ1, occ2));
        Map<LocalDate, List<ScheduleOccurrence>> result = scheduleOccurrenceService.getOccurrencesByRange(user, date1, date2);
        assertEquals(2, result.size());
        assertEquals(1, result.get(date1).size());
        assertEquals(1, result.get(date2).size());
    }

    @Test
    void generateOccurrences_ShouldReturnEarly_WhenEndBeforeStart() {
        Schedule schedule = new Schedule();
        schedule.setId(1L);
        User user = new User();
        LocalDate start = LocalDate.of(2023, 9, 26);
        LocalDate end = LocalDate.of(2023, 9, 25);
        scheduleOccurrenceService.generateOccurrencesForSchedule(schedule, user, start, end, 1);
        verify(scheduleOccurrenceRepository, never()).save(any());
    }

    @Test
    void generateOccurrences_ShouldHandleEntryWithBothExerciseTypes() {
        Schedule schedule = new Schedule();
        schedule.setId(1L);
        schedule.setName("Both");
        User user = new User();
        LocalDate date = LocalDate.of(2023, 9, 25); // Monday
        ScheduleEntry entry = new ScheduleEntry();
        entry.setDayOfWeek(1);
        entry.setExercise(new Exercise());
        entry.setCustomExercise(new CustomExercise());
        when(scheduleEntryService.getEntries(schedule.getId())).thenReturn(List.of(entry));
        scheduleOccurrenceService.generateOccurrencesForSchedule(schedule, user, date, date, 1);
        ArgumentCaptor<ScheduleOccurrence> captor = ArgumentCaptor.forClass(ScheduleOccurrence.class);
        verify(scheduleOccurrenceRepository).save(captor.capture());
        ScheduleOccurrence saved = captor.getValue();
        assertNotNull(saved.getExercise());
        assertNotNull(saved.getCustomExercise());
    }

    @Test
    void generateOccurrences_ShouldSkipEntryWithNoExerciseTypes() {
        Schedule schedule = new Schedule();
        schedule.setId(1L);
        schedule.setName("Skip");
        User user = new User();
        LocalDate date = LocalDate.of(2023, 9, 25);
        ScheduleEntry entry = new ScheduleEntry();
        entry.setDayOfWeek(1);
        when(scheduleEntryService.getEntries(schedule.getId())).thenReturn(List.of(entry));
        scheduleOccurrenceService.generateOccurrencesForSchedule(schedule, user, date, date, 1);
        verify(scheduleOccurrenceRepository, never()).save(any());
    }

    @Test
    void generateOccurrences_ShouldSkipWhenNoMatchingDays() {
        Schedule schedule = new Schedule();
        schedule.setId(1L);
        User user = new User();
        LocalDate start = LocalDate.of(2023, 9, 25); // Monday
        LocalDate end = LocalDate.of(2023, 9, 26);   // Tuesday
        ScheduleEntry entry = new ScheduleEntry();
        entry.setDayOfWeek(5);
        when(scheduleEntryService.getEntries(schedule.getId())).thenReturn(List.of(entry));
        scheduleOccurrenceService.generateOccurrencesForSchedule(schedule, user, start, end, 1);
        verify(scheduleOccurrenceRepository, never()).save(any());
    }
}