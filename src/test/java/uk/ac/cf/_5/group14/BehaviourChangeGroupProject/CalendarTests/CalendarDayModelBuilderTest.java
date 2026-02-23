package uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarDayModel;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarDayModelBuilder;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTask;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CustomExerciseData.CustomExercise;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseData.Exercise;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleOccurrence;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CalendarDayModelBuilder.
 * Verifies that the builder correctly constructs CalendarDayModel instances
 * from task and occurrence data, with proper status computation.
 */
public class CalendarDayModelBuilderTest {

    private CalendarDayModelBuilder builder;
    private User testUser;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        builder = new CalendarDayModelBuilder();
        testUser = new User();
        testUser.setId(1L);
        today = LocalDate.of(2026, 2, 15); // Fixed date for consistent testing
    }

    @Test
    void buildMonthDays_WithEmptyData_ReturnsAllDaysWithNoItems() {
        int year = 2026;
        int month = 2; // February 2026
        Map<LocalDate, List<CalendarTask>> tasksByDate = new HashMap<>();
        Map<LocalDate, List<ScheduleOccurrence>> occurrencesByDate = new HashMap<>();

        List<CalendarDayModel> result = builder.buildMonthDays(year, month, today, tasksByDate, occurrencesByDate);

        assertEquals(28, result.size(), "February 2026 should have 28 days");
        
        for (int i = 0; i < result.size(); i++) {
            CalendarDayModel day = result.get(i);
            assertEquals(i + 1, day.getDayOfMonth());
            assertTrue(day.isEmpty());
            assertEquals(0, day.getTaskCount());
            assertEquals(0, day.getOccurrenceCount());
            assertEquals(0, day.getTotalItemCount());
        }
    }

    @Test
    void buildMonthDays_WithNullMaps_ReturnsAllDaysWithNoItems() {
        int year = 2026;
        int month = 2;

        List<CalendarDayModel> result = builder.buildMonthDays(year, month, today, null, null);

        assertEquals(28, result.size());
        for (CalendarDayModel day : result) {
            assertTrue(day.isEmpty());
            assertEquals(0, day.getTaskCount());
            assertEquals(0, day.getOccurrenceCount());
        }
    }

    @Test
    void buildMonthDays_WithTasksOnly_ReturnsCorrectCounts() {
        int year = 2026;
        int month = 2;
        LocalDate date = LocalDate.of(2026, 2, 10);
        
        Map<LocalDate, List<CalendarTask>> tasksByDate = new HashMap<>();
        tasksByDate.put(date, List.of(
            createTask(date, "Task 1", false),
            createTask(date, "Task 2", true)
        ));

        List<CalendarDayModel> result = builder.buildMonthDays(year, month, today, tasksByDate, null);

        assertEquals(28, result.size());
        
        // Check the day with tasks (10th day of month, index 9)
        CalendarDayModel dayWithTasks = result.get(9);
        assertEquals(10, dayWithTasks.getDayOfMonth());
        assertEquals(2, dayWithTasks.getTaskCount());
        assertEquals(0, dayWithTasks.getOccurrenceCount());
        assertEquals(2, dayWithTasks.getTotalItemCount());
        assertFalse(dayWithTasks.isEmpty());
        
        // Check a day without tasks (1st day of month, index 0)
        CalendarDayModel dayWithoutTasks = result.get(0);
        assertTrue(dayWithoutTasks.isEmpty());
    }

    @Test
    void buildMonthDays_WithOccurrencesOnly_ReturnsCorrectCounts() {
        int year = 2026;
        int month = 2;
        LocalDate date = LocalDate.of(2026, 2, 20);
        
        Map<LocalDate, List<ScheduleOccurrence>> occurrencesByDate = new HashMap<>();
        occurrencesByDate.put(date, List.of(
            createOccurrence(date, "Workout 1", false),
            createOccurrence(date, "Workout 2", true),
            createOccurrence(date, "Workout 3", false)
        ));

        List<CalendarDayModel> result = builder.buildMonthDays(year, month, today, null, occurrencesByDate);

        assertEquals(28, result.size());
        
        // Check the day with occurrences (20th day of month, index 19)
        CalendarDayModel dayWithOccurrences = result.get(19);
        assertEquals(20, dayWithOccurrences.getDayOfMonth());
        assertEquals(0, dayWithOccurrences.getTaskCount());
        assertEquals(3, dayWithOccurrences.getOccurrenceCount());
        assertEquals(3, dayWithOccurrences.getTotalItemCount());
        assertFalse(dayWithOccurrences.isEmpty());
    }

    @Test
    void buildMonthDays_WithBothTasksAndOccurrences_ReturnsCorrectCounts() {
        int year = 2026;
        int month = 2;
        LocalDate date = LocalDate.of(2026, 2, 15);
        
        Map<LocalDate, List<CalendarTask>> tasksByDate = new HashMap<>();
        tasksByDate.put(date, List.of(createTask(date, "Task", false)));
        
        Map<LocalDate, List<ScheduleOccurrence>> occurrencesByDate = new HashMap<>();
        occurrencesByDate.put(date, List.of(
            createOccurrence(date, "Workout 1", false),
            createOccurrence(date, "Workout 2", false)
        ));

        List<CalendarDayModel> result = builder.buildMonthDays(year, month, today, tasksByDate, occurrencesByDate);

        // Check the day with both (15th day of month, index 14)
        CalendarDayModel dayWithBoth = result.get(14);
        assertEquals(15, dayWithBoth.getDayOfMonth());
        assertEquals(1, dayWithBoth.getTaskCount());
        assertEquals(2, dayWithBoth.getOccurrenceCount());
        assertEquals(3, dayWithBoth.getTotalItemCount());
        assertFalse(dayWithBoth.isEmpty());
    }

    @Test
    void buildMonthDays_StatusComputation_TodayIsCorrect() {
        int year = 2026;
        int month = 2;
        LocalDate todayDate = LocalDate.of(2026, 2, 15);

        List<CalendarDayModel> result = builder.buildMonthDays(year, month, todayDate, null, null);

        // Check today (15th day of month, index 14)
        CalendarDayModel todayModel = result.get(14);
        assertTrue(todayModel.isToday());
        assertFalse(todayModel.isTomorrow());
        assertFalse(todayModel.isPast());
        assertEquals(CalendarDayModel.DayStatus.TODAY, todayModel.getStatus());
        assertEquals("today", todayModel.getStatusValue());
    }

    @Test
    void buildMonthDays_StatusComputation_TomorrowIsCorrect() {
        int year = 2026;
        int month = 2;
        LocalDate todayDate = LocalDate.of(2026, 2, 15);

        List<CalendarDayModel> result = builder.buildMonthDays(year, month, todayDate, null, null);

        // Check tomorrow (16th day of month, index 15)
        CalendarDayModel tomorrowModel = result.get(15);
        assertFalse(tomorrowModel.isToday());
        assertTrue(tomorrowModel.isTomorrow());
        assertFalse(tomorrowModel.isPast());
        assertEquals(CalendarDayModel.DayStatus.TOMORROW, tomorrowModel.getStatus());
        assertEquals("tomorrow", tomorrowModel.getStatusValue());
    }

    @Test
    void buildMonthDays_StatusComputation_PastWithNoItems() {
        int year = 2026;
        int month = 2;
        LocalDate todayDate = LocalDate.of(2026, 2, 15);

        List<CalendarDayModel> result = builder.buildMonthDays(year, month, todayDate, null, null);

        // Check past day with no items (10th day of month, index 9)
        CalendarDayModel pastModel = result.get(9);
        assertFalse(pastModel.isToday());
        assertFalse(pastModel.isTomorrow());
        assertTrue(pastModel.isPast());
        assertEquals(CalendarDayModel.DayStatus.PAST_ZERO, pastModel.getStatus());
        assertEquals("past-zero", pastModel.getStatusValue());
    }

    @Test
    void buildMonthDays_StatusComputation_PastWithItems() {
        int year = 2026;
        int month = 2;
        LocalDate todayDate = LocalDate.of(2026, 2, 15);
        LocalDate pastDate = LocalDate.of(2026, 2, 10);
        
        Map<LocalDate, List<CalendarTask>> tasksByDate = new HashMap<>();
        tasksByDate.put(pastDate, List.of(createTask(pastDate, "Past Task", false)));

        List<CalendarDayModel> result = builder.buildMonthDays(year, month, todayDate, tasksByDate, null);

        // Check past day with items (10th day of month, index 9)
        CalendarDayModel pastModel = result.get(9);
        assertTrue(pastModel.isPast());
        assertEquals(CalendarDayModel.DayStatus.PAST_INCOMPLETE, pastModel.getStatus());
        assertEquals("past-incomplete", pastModel.getStatusValue());
    }

    @Test
    void buildMonthDays_StatusComputation_FutureIsCorrect() {
        int year = 2026;
        int month = 2;
        LocalDate todayDate = LocalDate.of(2026, 2, 15);

        List<CalendarDayModel> result = builder.buildMonthDays(year, month, todayDate, null, null);

        // Check future day (20th day of month, index 19)
        CalendarDayModel futureModel = result.get(19);
        assertFalse(futureModel.isToday());
        assertFalse(futureModel.isTomorrow());
        assertFalse(futureModel.isPast());
        assertEquals(CalendarDayModel.DayStatus.FUTURE, futureModel.getStatus());
        assertEquals("future", futureModel.getStatusValue());
    }

    @Test
    void buildMonthDays_IsoDateFormat_IsCorrect() {
        int year = 2026;
        int month = 2;

        List<CalendarDayModel> result = builder.buildMonthDays(year, month, today, null, null);

        // Check first day
        assertEquals("2026-02-01", result.get(0).getIsoDate());
        
        // Check 15th day
        assertEquals("2026-02-15", result.get(14).getIsoDate());
        
        // Check last day
        assertEquals("2026-02-28", result.get(27).getIsoDate());
    }

    @Test
    void buildMonthDays_DifferentMonthLengths_CorrectDayCounts() {
        // January (31 days)
        List<CalendarDayModel> january = builder.buildMonthDays(2026, 1, today, null, null);
        assertEquals(31, january.size());
        
        // February non-leap year (28 days)
        List<CalendarDayModel> february = builder.buildMonthDays(2026, 2, today, null, null);
        assertEquals(28, february.size());
        
        // February leap year (29 days)
        List<CalendarDayModel> februaryLeap = builder.buildMonthDays(2024, 2, today, null, null);
        assertEquals(29, februaryLeap.size());
        
        // April (30 days)
        List<CalendarDayModel> april = builder.buildMonthDays(2026, 4, today, null, null);
        assertEquals(30, april.size());
    }

    @Test
    void buildWeekDays_WithEmptyData_ReturnsSevenDays() {
        LocalDate weekStart = LocalDate.of(2026, 2, 9); // Monday
        Map<LocalDate, List<CalendarTask>> tasksByDate = new HashMap<>();
        Map<LocalDate, List<ScheduleOccurrence>> occurrencesByDate = new HashMap<>();

        List<CalendarDayModel> result = builder.buildWeekDays(weekStart, today, tasksByDate, occurrencesByDate);

        assertEquals(7, result.size());
        
        for (int i = 0; i < result.size(); i++) {
            CalendarDayModel day = result.get(i);
            assertEquals(weekStart.plusDays(i), day.getLocalDate());
            assertTrue(day.isEmpty());
        }
    }

    @Test
    void buildWeekDays_WithNullMaps_ReturnsSevenDays() {
        LocalDate weekStart = LocalDate.of(2026, 2, 9);

        List<CalendarDayModel> result = builder.buildWeekDays(weekStart, today, null, null);

        assertEquals(7, result.size());
        for (CalendarDayModel day : result) {
            assertTrue(day.isEmpty());
        }
    }

    @Test
    void buildWeekDays_WithData_ReturnsCorrectCounts() {
        LocalDate weekStart = LocalDate.of(2026, 2, 9); // Monday
        LocalDate wednesday = LocalDate.of(2026, 2, 11);
        LocalDate friday = LocalDate.of(2026, 2, 13);
        
        Map<LocalDate, List<CalendarTask>> tasksByDate = new HashMap<>();
        tasksByDate.put(wednesday, List.of(createTask(wednesday, "Mid-week task", false)));
        
        Map<LocalDate, List<ScheduleOccurrence>> occurrencesByDate = new HashMap<>();
        occurrencesByDate.put(friday, List.of(
            createOccurrence(friday, "Friday workout 1", false),
            createOccurrence(friday, "Friday workout 2", false)
        ));

        List<CalendarDayModel> result = builder.buildWeekDays(weekStart, today, tasksByDate, occurrencesByDate);

        assertEquals(7, result.size());
        
        // Monday (index 0) - empty
        assertTrue(result.get(0).isEmpty());
        
        // Wednesday (index 2) - has 1 task
        assertEquals(1, result.get(2).getTaskCount());
        assertEquals(0, result.get(2).getOccurrenceCount());
        
        // Friday (index 4) - has 2 occurrences
        assertEquals(0, result.get(4).getTaskCount());
        assertEquals(2, result.get(4).getOccurrenceCount());
    }

    @Test
    void buildWeekDays_StatusComputation_IsCorrect() {
        LocalDate weekStart = LocalDate.of(2026, 2, 9); // Week Mon Feb 9 - Sun Feb 15
        LocalDate todayDate = LocalDate.of(2026, 2, 11); // Wednesday

        List<CalendarDayModel> result = builder.buildWeekDays(weekStart, todayDate, null, null);

        // Monday-Tuesday should be past
        assertTrue(result.get(0).isPast(), "Monday should be past");
        assertTrue(result.get(1).isPast(), "Tuesday should be past");
        
        // Wednesday should be today
        assertTrue(result.get(2).isToday(), "Wednesday should be today");
        
        // Thursday should be tomorrow
        assertTrue(result.get(3).isTomorrow(), "Thursday should be tomorrow");
        
        // Friday-Sunday should be future
        assertEquals(CalendarDayModel.DayStatus.FUTURE, result.get(4).getStatus(), "Friday should be future");
        assertEquals(CalendarDayModel.DayStatus.FUTURE, result.get(5).getStatus(), "Saturday should be future");
        assertEquals(CalendarDayModel.DayStatus.FUTURE, result.get(6).getStatus(), "Sunday should be future");
    }

    @Test
    void buildWeekDays_SpanningMonthBoundary_IsCorrect() {
        LocalDate weekStart = LocalDate.of(2026, 2, 23); // Last week of February, spanning into March

        List<CalendarDayModel> result = builder.buildWeekDays(weekStart, today, null, null);

        assertEquals(7, result.size());
        
        // Check dates
        assertEquals("2026-02-23", result.get(0).getIsoDate()); // Monday in February
        assertEquals("2026-02-24", result.get(1).getIsoDate());
        assertEquals("2026-02-25", result.get(2).getIsoDate());
        assertEquals("2026-02-26", result.get(3).getIsoDate());
        assertEquals("2026-02-27", result.get(4).getIsoDate());
        assertEquals("2026-02-28", result.get(5).getIsoDate()); // Last day of February
        assertEquals("2026-03-01", result.get(6).getIsoDate()); // Sunday in March
    }

    @Test
    void toIsoDateMap_WithValidList_ReturnsCorrectMap() {
        List<CalendarDayModel> days = builder.buildMonthDays(2026, 2, today, null, null);

        Map<String, CalendarDayModel> map = builder.toIsoDateMap(days);

        assertEquals(28, map.size());
        assertTrue(map.containsKey("2026-02-01"));
        assertTrue(map.containsKey("2026-02-15"));
        assertTrue(map.containsKey("2026-02-28"));
        
        CalendarDayModel day15 = map.get("2026-02-15");
        assertNotNull(day15);
        assertEquals(15, day15.getDayOfMonth());
    }

    @Test
    void toIsoDateMap_WithNullList_ReturnsEmptyMap() {
        Map<String, CalendarDayModel> map = builder.toIsoDateMap(null);

        assertNotNull(map);
        assertTrue(map.isEmpty());
    }

    @Test
    void toIsoDateMap_WithEmptyList_ReturnsEmptyMap() {
        Map<String, CalendarDayModel> map = builder.toIsoDateMap(new ArrayList<>());

        assertNotNull(map);
        assertTrue(map.isEmpty());
    }

    @Test
    void buildMonthDays_TasksListDefensiveCopy_ModifyingOriginalDoesNotAffectModel() {
        int year = 2026;
        int month = 2;
        LocalDate date = LocalDate.of(2026, 2, 15);
        
        List<CalendarTask> originalTasks = new ArrayList<>();
        originalTasks.add(createTask(date, "Task 1", false));
        
        Map<LocalDate, List<CalendarTask>> tasksByDate = new HashMap<>();
        tasksByDate.put(date, originalTasks);

        List<CalendarDayModel> result = builder.buildMonthDays(year, month, today, tasksByDate, null);

        // Modify the original list after building
        originalTasks.add(createTask(date, "Task 2", false));

        // The model should still have only 1 task
        CalendarDayModel day15 = result.get(14);
        assertEquals(1, day15.getTaskCount(), "Model should have defensive copy, not affected by original list modification");
    }

    // Helper methods to create test entities

    private CalendarTask createTask(LocalDate date, String title, boolean completed) {
        CalendarTask task = new CalendarTask();
        task.setId((long) (Math.random() * 10000));
        task.setUser(testUser);
        task.setDate(date);
        task.setTitle(title);
        task.setCompleted(completed);
        task.setExercise(false);
        task.setTime(LocalTime.of(9, 0));
        return task;
    }

    private ScheduleOccurrence createOccurrence(LocalDate date, String exerciseName, boolean completed) {
        ScheduleOccurrence occurrence = new ScheduleOccurrence();
        occurrence.setId((long) (Math.random() * 10000));
        occurrence.setUser(testUser);
        occurrence.setDate(date);
        occurrence.setScheduleName("Test Schedule");
        occurrence.setCompleted(completed);
        
        // Create exercise with name
        Exercise exercise = new Exercise();
        exercise.setId((long) (Math.random() * 10000));
        exercise.setName(exerciseName);
        occurrence.setExercise(exercise);
        
        return occurrence;
    }
}
