package uk.ac.cf._5.group14.One_To_One.CalendarTests;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import uk.ac.cf._5.group14.One_To_One.CalendarData.CalendarDayModelBuilder;
import uk.ac.cf._5.group14.One_To_One.CalendarData.CalendarTask;
import uk.ac.cf._5.group14.One_To_One.CalendarData.CalendarTaskService;
import uk.ac.cf._5.group14.One_To_One.CalendarData.CalendarTaskWarningService;
import uk.ac.cf._5.group14.One_To_One.CalendarData.DailyStreakService;
import uk.ac.cf._5.group14.One_To_One.CalendarData.TaskAiGenerationService;
import uk.ac.cf._5.group14.One_To_One.CalendarData.TaskTemplateService;
import uk.ac.cf._5.group14.One_To_One.DayHealthData.DayHealthPersistenceService;
import uk.ac.cf._5.group14.One_To_One.DayMode.DayModeService;
import uk.ac.cf._5.group14.One_To_One.ExerciseData.Exercise;
import uk.ac.cf._5.group14.One_To_One.Goals.GoalLinkService;
import uk.ac.cf._5.group14.One_To_One.PlatformBilling.PlatformSubscriptionService;
import uk.ac.cf._5.group14.One_To_One.ReflectionData.ReflectionService;
import uk.ac.cf._5.group14.One_To_One.ScheduleData.CalendarController;
import uk.ac.cf._5.group14.One_To_One.ScheduleData.Schedule;
import uk.ac.cf._5.group14.One_To_One.ScheduleData.ScheduleAppliedRepository;
import uk.ac.cf._5.group14.One_To_One.ScheduleData.ScheduleOccurrence;
import uk.ac.cf._5.group14.One_To_One.ScheduleData.ScheduleOccurrenceService;
import uk.ac.cf._5.group14.One_To_One.ScheduleData.ScheduleService;
import uk.ac.cf._5.group14.One_To_One.ScheduleData.ScheduleType;
import uk.ac.cf._5.group14.One_To_One.StrengthLog.Service.WorkoutScheduleService;
import uk.ac.cf._5.group14.One_To_One.StrengthLog.Service.WorkoutSessionService;
import uk.ac.cf._5.group14.One_To_One.UserSettings.CalendarTaskLayoutPreference;
import uk.ac.cf._5.group14.One_To_One.UserSettings.CalendarTaskOrderingPreference;
import uk.ac.cf._5.group14.One_To_One.UserSettings.UserSettings;
import uk.ac.cf._5.group14.One_To_One.UserSettings.UserSettingsService;
import uk.ac.cf._5.group14.One_To_One.Users.AuthHelper;
import uk.ac.cf._5.group14.One_To_One.Users.User;
import uk.ac.cf._5.group14.One_To_One.Notifications.NotificationSseRegistry;
import uk.ac.cf._5.group14.One_To_One.CalendarData.DayOptimisationRepository;
import uk.ac.cf._5.group14.One_To_One.Config.DevModeProperties;

/**
 * Tests to verify that the calendar month and week views correctly display tasks and schedules.
 * These tests ensure that when the calendar loads with inputted data, the result shows the
 * tasks and schedules on the specific page (month or week view).
 */
@WebMvcTest(CalendarController.class)
@ActiveProfiles("test")
class CalendarMonthWeekViewDataDisplayTest {

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
    private ScheduleAppliedRepository scheduleAppliedRepository;

    @MockitoBean
    private WorkoutScheduleService workoutScheduleService;

    @MockitoBean
    private WorkoutSessionService workoutSessionService;

    @MockitoBean
    private AuthHelper authHelper;

    @MockitoBean
    private UserSettingsService userSettingsService;

    @MockitoBean
    private DayModeService dayModeService;

    @MockitoBean
    private GoalLinkService goalLinkService;

    @MockitoBean
    private PlatformSubscriptionService platformSubscriptionService;

    @MockitoBean
    private ReflectionService reflectionService;

    @MockitoBean
    private DailyStreakService dailyStreakService;

    @MockitoBean
    private DayHealthPersistenceService dayHealthPersistenceService;

    @Autowired
    private CalendarDayModelBuilder calendarDayModelBuilder;

    @MockitoBean
    private NotificationSseRegistry sseRegistry;

    @MockitoBean
    private DayOptimisationRepository dayOptimisationRepository;

    /**
     * Test that the month view correctly displays tasks and schedules for February 2026.
     * This test verifies that the calendar loads with the data and the result shows
     * the tasks and schedules on that specific month page.
     */
    @Test
    void monthViewDisplaysTasksAndSchedulesCorrectly() throws Exception {
        // Arrange: Create user and test data
        User testUser = new User();
        testUser.setId(100L);
        testUser.setUsername("testuser");

        // Create mock user settings
        UserSettings settings = new UserSettings();
        settings.setCalendarTaskOrdering(CalendarTaskOrderingPreference.CHRONOLOGICAL);
        settings.setCalendarTaskLayout(CalendarTaskLayoutPreference.COMBINED_LIST);

        // Create test tasks for various dates in February 2026
        List<CalendarTask> februaryTasks = createFebruaryTasks(testUser);

        // Create test schedule occurrences for February 2026
        Map<LocalDate, List<ScheduleOccurrence>> februaryOccurrences = createFebruaryScheduleOccurrences(testUser);

        // Create test schedules
        List<Schedule> userSchedules = createTestSchedules(testUser);

        // Mock service responses
        given(userSettingsService.getOrCreate(eq(testUser))).willReturn(settings);
        given(platformSubscriptionService.isPremium(eq(testUser.getId()), any(Clock.class))).willReturn(true);
        given(scheduleService.findByUser(eq(testUser))).willReturn(userSchedules);
        given(scheduleAppliedRepository.findByUserAndSchedule(any(), any())).willReturn(Collections.emptyList());

        // Mock the date range query for February 2026 (month start to end)
        LocalDate monthStart = LocalDate.of(2026, 2, 1);
        LocalDate monthEnd = LocalDate.of(2026, 2, 28);
        
        // Group tasks by date for the service mock
        Map<LocalDate, List<CalendarTask>> tasksByDate = new HashMap<>();
        for (CalendarTask task : februaryTasks) {
            tasksByDate.computeIfAbsent(task.getDate(), k -> new ArrayList<>()).add(task);
        }
        
        given(taskService.getTasksByRange(eq(testUser), eq(monthStart), eq(monthEnd)))
                .willReturn(tasksByDate);
        given(scheduleOccurrenceService.getOccurrencesForUserInMonth(eq(testUser), eq(2026), eq(2)))
                .willReturn(februaryOccurrences);

        // Act & Assert: Request the month view for February 2026
        mvc.perform(get("/calendar")
                .param("view", "month")
                        .param("month", "2")
                        .param("year", "2026")
                        .sessionAttr("user", testUser))
                .andExpect(status().isOk())
                // Verify the model contains the correct data
                .andExpect(model().attribute("month", 2))
                .andExpect(model().attribute("year", 2026))
                .andExpect(model().attributeExists("tasksByDate"))
                .andExpect(model().attributeExists("tasksByDateIso"))
                .andExpect(model().attributeExists("occurrences"))
                .andExpect(model().attributeExists("occurrencesByDateIso"))
                .andExpect(model().attributeExists("schedules"))
                // Verify the model data structure is correct
                .andExpect(result -> {
                    @SuppressWarnings("unchecked")
                    Map<LocalDate, List<CalendarTask>> modelTasks =
                            (Map<LocalDate, List<CalendarTask>>) result.getModelAndView().getModel().get("tasksByDate");
                    @SuppressWarnings("unchecked")
                    Map<LocalDate, List<ScheduleOccurrence>> modelOccurrences =
                            (Map<LocalDate, List<ScheduleOccurrence>>) result.getModelAndView().getModel().get("occurrences");
                    
                    // Assert we have tasks on the expected dates
                    assert modelTasks != null;
                    assert modelTasks.get(LocalDate.of(2026, 2, 3)) != null;
                    assert modelTasks.get(LocalDate.of(2026, 2, 3)).size() == 1;
                    assert "Team Meeting".equals(modelTasks.get(LocalDate.of(2026, 2, 3)).get(0).getTitle());
                    
                    assert modelTasks.get(LocalDate.of(2026, 2, 10)) != null;
                    assert "Client Presentation".equals(modelTasks.get(LocalDate.of(2026, 2, 10)).get(0).getTitle());
                    
                    // Assert we have schedule occurrences on the expected dates
                    assert modelOccurrences != null;
                    assert modelOccurrences.get(LocalDate.of(2026, 2, 4)) != null;
                    assert "Chest & Triceps".equals(modelOccurrences.get(LocalDate.of(2026, 2, 4)).get(0).getScheduleName());
                });
    }

    /**
     * Test that the week view correctly displays tasks and schedules for week 7 of 2026
     * (February 9-15, 2026). This test verifies that the calendar loads with the data
     * and the result shows the tasks and schedules on that specific week page.
     */
    @Test
    void weekViewDisplaysTasksAndSchedulesCorrectly() throws Exception {
        // Arrange: Create user and test data
        User testUser = new User();
        testUser.setId(200L);
        testUser.setUsername("weekuser");

        // Create mock user settings
        UserSettings settings = new UserSettings();
        settings.setCalendarTaskOrdering(CalendarTaskOrderingPreference.CHRONOLOGICAL);
        settings.setCalendarTaskLayout(CalendarTaskLayoutPreference.COMBINED_LIST);

        // Week 7 of 2026 (ISO week) is February 9-15, 2026 (Monday to Sunday)
        LocalDate weekStart = LocalDate.of(2026, 2, 9);  // Monday
        LocalDate weekEnd = LocalDate.of(2026, 2, 15);    // Sunday

        // Create test tasks for the week
        List<CalendarTask> weekTasks = createWeekTasks(testUser, weekStart);

        // Create test schedule occurrences for the week
        Map<LocalDate, List<ScheduleOccurrence>> weekOccurrences = createWeekScheduleOccurrences(testUser, weekStart);

        // Create test schedules
        List<Schedule> userSchedules = createTestSchedules(testUser);

        // Mock service responses
        given(userSettingsService.getOrCreate(eq(testUser))).willReturn(settings);
        given(platformSubscriptionService.isPremium(eq(testUser.getId()), any(Clock.class))).willReturn(true);
        given(scheduleService.findByUser(eq(testUser))).willReturn(userSchedules);
        given(scheduleAppliedRepository.findByUserAndSchedule(any(), any())).willReturn(Collections.emptyList());

        // Group tasks by date
        Map<LocalDate, List<CalendarTask>> tasksByDate = new HashMap<>();
        for (CalendarTask task : weekTasks) {
            tasksByDate.computeIfAbsent(task.getDate(), k -> new ArrayList<>()).add(task);
        }

        given(taskService.getTasksByRange(eq(testUser), eq(weekStart), eq(weekEnd)))
                .willReturn(tasksByDate);
        given(scheduleOccurrenceService.getOccurrencesByRange(eq(testUser), eq(weekStart), eq(weekEnd)))
                .willReturn(weekOccurrences);

        // Act & Assert: Request the week view for week 7 of 2026
        mvc.perform(get("/calendar/week-fragment")
                        .param("week", "7")
                        .param("weekYear", "2026")
                        .sessionAttr("user", testUser))
                .andExpect(status().isOk())
                // Verify the model contains the correct data
                .andExpect(model().attribute("week",7))
                .andExpect(model().attribute("weekYear", 2026))
                .andExpect(model().attributeExists("weekDays"))
                .andExpect(model().attributeExists("tasksByDate"))
                .andExpect(model().attributeExists("tasksByDateIso"))
                .andExpect(model().attributeExists("occurrences"))
                .andExpect(model().attributeExists("occurrencesByDateIso"))
                .andExpect(model().attributeExists("schedules"))
                // Verify the model data structure is correct
                .andExpect(result -> {
                    @SuppressWarnings("unchecked")
                    Map<LocalDate, List<CalendarTask>> modelTasks =
                            (Map<LocalDate, List<CalendarTask>>) result.getModelAndView().getModel().get("tasksByDate");
                    @SuppressWarnings("unchecked")
                    Map<LocalDate, List<ScheduleOccurrence>> modelOccurrences =
                            (Map<LocalDate, List<ScheduleOccurrence>>) result.getModelAndView().getModel().get("occurrences");
                    
                    // Assert we have tasks on the expected dates
                    assert modelTasks != null;
                    LocalDate monday = LocalDate.of(2026, 2, 9);
                    LocalDate tuesday = monday.plusDays(1);
                    LocalDate wednesday = monday.plusDays(2);
                    LocalDate friday = monday.plusDays(4);
                    
                    assert modelTasks.get(monday) != null;
                    assert "Monday Standup".equals(modelTasks.get(monday).get(0).getTitle());
                    
                    assert modelTasks.get(tuesday) != null;
                    assert "Tuesday Planning".equals(modelTasks.get(tuesday).get(0).getTitle());
                    
                    assert modelTasks.get(wednesday) != null;
                    assert "Wednesday Review".equals(modelTasks.get(wednesday).get(0).getTitle());
                    
                    // Assert we have schedule occurrences on the expected dates
                    assert modelOccurrences != null;
                    assert modelOccurrences.get(monday) != null;
                    assert "Leg Day".equals(modelOccurrences.get(monday).get(0).getScheduleName());
                    
                    assert modelOccurrences.get(wednesday) != null;
                    assert "Chest Day".equals(modelOccurrences.get(wednesday).get(0).getScheduleName());
                    
                    assert modelOccurrences.get(friday) != null;
                    assert "Back Day".equals(modelOccurrences.get(friday).get(0).getScheduleName());
                });
    }

    /**
     * Test that the month view renders HTML with correct data-type attributes.
     * Verifies that tasks use data-type="task" and occurrences use data-type="occurrence".
     */
    @Test
    void monthViewRendersCorrectDataTypeAttributes() throws Exception {
        User testUser = new User();
        testUser.setId(400L);
        testUser.setUsername("attributeuser");

        UserSettings settings = new UserSettings();
        settings.setCalendarTaskOrdering(CalendarTaskOrderingPreference.CHRONOLOGICAL);
        settings.setCalendarTaskLayout(CalendarTaskLayoutPreference.COMBINED_LIST);

        LocalDate testDate = LocalDate.of(2026, 3, 10);
        
        // Create task and occurrence for the same date
        CalendarTask task = new CalendarTask();
        task.setId(100L);
        task.setUser(testUser);
        task.setDate(testDate);
        task.setTime(LocalTime.of(10, 0));
        task.setTitle("Test Task");
        task.setCompleted(false);
        task.setExercise(false);
        
        Exercise exercise = new Exercise();
        exercise.setId(100L);
        exercise.setName("Test Exercise");
        
        ScheduleOccurrence occurrence = new ScheduleOccurrence();
        occurrence.setId(100L);
        occurrence.setUser(testUser);
        occurrence.setDate(testDate);
        occurrence.setExercise(exercise);
        occurrence.setScheduleName("Test Schedule");
        occurrence.setCompleted(false);

        Map<LocalDate, List<CalendarTask>> tasksByDate = new HashMap<>();
        tasksByDate.put(testDate, List.of(task));
        
        Map<LocalDate, List<ScheduleOccurrence>> occurrencesByDate = new HashMap<>();
        occurrencesByDate.put(testDate, List.of(occurrence));

        given(userSettingsService.getOrCreate(eq(testUser))).willReturn(settings);
        given(platformSubscriptionService.isPremium(eq(testUser.getId()), any(Clock.class))).willReturn(true);
        given(scheduleService.findByUser(eq(testUser))).willReturn(Collections.emptyList());
        given(taskService.getTasksByRange(any(), any(), any())).willReturn(tasksByDate);
        given(scheduleOccurrenceService.getOccurrencesForUserInMonth(any(), anyInt(), anyInt()))
                .willReturn(occurrencesByDate);

        mvc.perform(get("/calendar")
                .param("view", "month")
                        .param("month", "3")
                        .param("year", "2026")
                        .sessionAttr("user", testUser))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String htmlContent = result.getResponse().getContentAsString();
                    
                    // Verify tasks have data-type="task"
                    assert htmlContent.contains("data-type=\"task\"") || htmlContent.contains("data-type='task'") 
                        : "HTML should contain task items with data-type='task'";
                    
                    // Verify occurrences have data-type="occurrence"
                    assert htmlContent.contains("data-type=\"occurrence\"") || htmlContent.contains("data-type='occurrence'")
                        : "HTML should contain occurrence items with data-type='occurrence'";
                    
                    // Verify NO items have the old incorrect data-type="workout"
                    assert !htmlContent.contains("data-type=\"workout\"") && !htmlContent.contains("data-type='workout'")
                        : "HTML should NOT contain any items with data-type='workout' (deprecated)";
                });
    }

    /**
     * Test that the month view renders HTML with correct data-date attributes in ISO format.
     */
    @Test
    void monthViewRendersCorrectDataDateAttributes() throws Exception {
        User testUser = new User();
        testUser.setId(500L);
        testUser.setUsername("dateuser");

        UserSettings settings = new UserSettings();
        settings.setCalendarTaskOrdering(CalendarTaskOrderingPreference.CHRONOLOGICAL);
        settings.setCalendarTaskLayout(CalendarTaskLayoutPreference.COMBINED_LIST);

        LocalDate testDate = LocalDate.of(2026, 3, 15);
        String expectedIsoDate = "2026-03-15";
        
        CalendarTask task = new CalendarTask();
        task.setId(200L);
        task.setUser(testUser);
        task.setDate(testDate);
        task.setTime(LocalTime.of(14, 30));
        task.setTitle("Date Test Task");
        task.setCompleted(false);
        task.setExercise(false);

        Map<LocalDate, List<CalendarTask>> tasksByDate = new HashMap<>();
        tasksByDate.put(testDate, List.of(task));

        given(userSettingsService.getOrCreate(eq(testUser))).willReturn(settings);
        given(platformSubscriptionService.isPremium(eq(testUser.getId()), any(Clock.class))).willReturn(false);
        given(scheduleService.findByUser(eq(testUser))).willReturn(Collections.emptyList());
        given(taskService.getTasksByRange(any(), any(), any())).willReturn(tasksByDate);
        given(scheduleOccurrenceService.getOccurrencesForUserInMonth(any(), anyInt(), anyInt()))
                .willReturn(Collections.emptyMap());

        mvc.perform(get("/calendar")
                .param("view", "month")
                        .param("month", "3")
                        .param("year", "2026")
                        .sessionAttr("user", testUser))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String htmlContent = result.getResponse().getContentAsString();
                    
                    // Verify data-date attributes use ISO format (yyyy-MM-dd)
                    assert htmlContent.contains("data-date=\"" + expectedIsoDate + "\"") ||
                           htmlContent.contains("data-date='" + expectedIsoDate + "'")
                        : "HTML should contain data-date with ISO format: " + expectedIsoDate;
                });
    }

    /**
     * Test that the week view renders HTML with correct data-type attributes.
     */
    @Test
    void weekViewRendersCorrectDataTypeAttributes() throws Exception {
        User testUser = new User();
        testUser.setId(600L);
        testUser.setUsername("weekattruser");

        UserSettings settings = new UserSettings();
        settings.setCalendarTaskOrdering(CalendarTaskOrderingPreference.CHRONOLOGICAL);
        settings.setCalendarTaskLayout(CalendarTaskLayoutPreference.SEPARATED_BY_CATEGORY);

        LocalDate weekStart = LocalDate.of(2026, 3, 2); // Monday
        LocalDate wednesday = weekStart.plusDays(2);
        
        CalendarTask task = new CalendarTask();
        task.setId(300L);
        task.setUser(testUser);
        task.setDate(wednesday);
        task.setTime(LocalTime.of(11, 0));
        task.setTitle("Week Task");
        task.setCompleted(false);
        task.setExercise(false);
        
        Exercise exercise = new Exercise();
        exercise.setId(300L);
        exercise.setName("Week Exercise");
        
        ScheduleOccurrence occurrence = new ScheduleOccurrence();
        occurrence.setId(300L);
        occurrence.setUser(testUser);
        occurrence.setDate(wednesday);
        occurrence.setExercise(exercise);
        occurrence.setScheduleName("Week Schedule");
        occurrence.setCompleted(false);

        Map<LocalDate, List<CalendarTask>> tasksByDate = new HashMap<>();
        tasksByDate.put(wednesday, List.of(task));
        
        Map<LocalDate, List<ScheduleOccurrence>> occurrencesByDate = new HashMap<>();
        occurrencesByDate.put(wednesday, List.of(occurrence));

        given(userSettingsService.getOrCreate(eq(testUser))).willReturn(settings);
        given(platformSubscriptionService.isPremium(eq(testUser.getId()), any(Clock.class))).willReturn(true);
        given(scheduleService.findByUser(eq(testUser))).willReturn(Collections.emptyList());
        given(taskService.getTasksByRange(any(), any(), any())).willReturn(tasksByDate);
        given(scheduleOccurrenceService.getOccurrencesByRange(any(), any(), any())).willReturn(occurrencesByDate);

        mvc.perform(get("/calendar/week-fragment")
                        .param("week", "10")
                        .param("weekYear", "2026")
                        .sessionAttr("user", testUser))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String htmlContent = result.getResponse().getContentAsString();
                    
                    // Verify data-type attributes are correct
                    assert htmlContent.contains("data-type=\"task\"") || htmlContent.contains("data-type='task'")
                        : "Week view should contain tasks with data-type='task'";
                    assert htmlContent.contains("data-type=\"occurrence\"") || htmlContent.contains("data-type='occurrence'")
                        : "Week view should contain occurrences with data-type='occurrence'";
                    assert !htmlContent.contains("data-type=\"workout\"") && !htmlContent.contains("data-type='workout'")
                        : "Week view should NOT contain data-type='workout'";
                });
    }

    /**
     * Test that the month view correctly handles empty data (no tasks or schedules).
     */
    @Test
    void monthViewHandlesEmptyDataCorrectly() throws Exception {
        User testUser = new User();
        testUser.setId(300L);
        testUser.setUsername("emptyuser");

        UserSettings settings = new UserSettings();
        settings.setCalendarTaskOrdering(CalendarTaskOrderingPreference.CHRONOLOGICAL);
        settings.setCalendarTaskLayout(CalendarTaskLayoutPreference.COMBINED_LIST);

        given(userSettingsService.getOrCreate(eq(testUser))).willReturn(settings);
        given(platformSubscriptionService.isPremium(eq(testUser.getId()), any(Clock.class))).willReturn(false);
        given(scheduleService.findByUser(eq(testUser))).willReturn(Collections.emptyList());
        given(taskService.getTasksByRange(any(), any(), any())).willReturn(Collections.emptyMap());
        given(scheduleOccurrenceService.getOccurrencesForUserInMonth(any(), anyInt(), anyInt()))
                .willReturn(Collections.emptyMap());

        mvc.perform(get("/calendar")
                .param("view", "month")
                        .param("month", "3")
                        .param("year", "2026")
                        .sessionAttr("user", testUser))
                .andExpect(status().isOk())
                .andExpect(model().attribute("month", 3))
                .andExpect(model().attribute("year", 2026));
    }

    // Helper methods to create test data

    /**
     * Creates sample tasks for February 2026
     */
    private List<CalendarTask> createFebruaryTasks(User user) {
        List<CalendarTask> tasks = new ArrayList<>();

        // Task 1: Team Meeting on Feb 3rd
        CalendarTask task1 = new CalendarTask();
        task1.setId(1L);
        task1.setUser(user);
        task1.setDate(LocalDate.of(2026, 2, 3));
        task1.setTime(LocalTime.of(10, 0));
        task1.setTitle("Team Meeting");
        task1.setNotes("Discuss project milestones");
        task1.setExercise(false);
        task1.setCompleted(false);
        tasks.add(task1);

        // Task 2: Morning Workout on Feb 5th
        CalendarTask task2 = new CalendarTask();
        task2.setId(2L);
        task2.setUser(user);
        task2.setDate(LocalDate.of(2026, 2, 5));
        task2.setTime(LocalTime.of(7, 30));
        task2.setTitle("Morning Workout");
        task2.setNotes("30 min cardio");
        task2.setExercise(true);
        task2.setCompleted(false);
        tasks.add(task2);

        // Task 3: Client Presentation on Feb 10th
        CalendarTask task3 = new CalendarTask();
        task3.setId(3L);
        task3.setUser(user);
        task3.setDate(LocalDate.of(2026, 2, 10));
        task3.setTime(LocalTime.of(14, 0));
        task3.setTitle("Client Presentation");
        task3.setNotes("Q1 results presentation");
        task3.setExercise(false);
        task3.setCompleted(false);
        tasks.add(task3);

        // Task 4: Grocery Shopping on Feb 15th
        CalendarTask task4 = new CalendarTask();
        task4.setId(4L);
        task4.setUser(user);
        task4.setDate(LocalDate.of(2026, 2, 15));
        task4.setTime(LocalTime.of(18, 0));
        task4.setTitle("Grocery Shopping");
        task4.setNotes("Weekly groceries");
        task4.setExercise(false);
        task4.setCompleted(false);
        tasks.add(task4);

        // Task 5: Code Review on Feb 20th
        CalendarTask task5 = new CalendarTask();
        task5.setId(5L);
        task5.setUser(user);
        task5.setDate(LocalDate.of(2026, 2, 20));
        task5.setTime(LocalTime.of(15, 30));
        task5.setTitle("Code Review");
        task5.setNotes("Review pull requests");
        task5.setExercise(false);
        task5.setCompleted(false);
        tasks.add(task5);

        return tasks;
    }

    /**
     * Creates sample schedule occurrences for February 2026
     */
    private Map<LocalDate, List<ScheduleOccurrence>> createFebruaryScheduleOccurrences(User user) {
        Map<LocalDate, List<ScheduleOccurrence>> occurrences = new HashMap<>();

        // Schedule 1: Chest & Triceps on Feb 4th
        Exercise exercise1 = new Exercise();
        exercise1.setId(101L);
        exercise1.setName("Bench Press");
        
        ScheduleOccurrence occ1 = new ScheduleOccurrence();
        occ1.setId(1L);
        occ1.setUser(user);
        occ1.setExercise(exercise1);
        occ1.setScheduleName("Chest & Triceps");
        occ1.setDate(LocalDate.of(2026, 2, 4));
        occ1.setCompleted(false);
        occurrences.computeIfAbsent(occ1.getDate(), k -> new ArrayList<>()).add(occ1);

        // Schedule 2: Cardio Session on Feb 7th
        Exercise exercise2 = new Exercise();
        exercise2.setId(102L);
        exercise2.setName("Running");
        
        ScheduleOccurrence occ2 = new ScheduleOccurrence();
        occ2.setId(2L);
        occ2.setUser(user);
        occ2.setExercise(exercise2);
        occ2.setScheduleName("Cardio Session");
        occ2.setDate(LocalDate.of(2026, 2, 7));
        occ2.setCompleted(false);
        occurrences.computeIfAbsent(occ2.getDate(), k -> new ArrayList<>()).add(occ2);

        // Schedule 3: Back & Biceps on Feb 11th
        Exercise exercise3 = new Exercise();
        exercise3.setId(103L);
        exercise3.setName("Pull-ups");
        
        ScheduleOccurrence occ3 = new ScheduleOccurrence();
        occ3.setId(3L);
        occ3.setUser(user);
        occ3.setExercise(exercise3);
        occ3.setScheduleName("Back & Biceps");
        occ3.setDate(LocalDate.of(2026, 2, 11));
        occ3.setCompleted(false);
        occurrences.computeIfAbsent(occ3.getDate(), k -> new ArrayList<>()).add(occ3);

        return occurrences;
    }

    /**
     * Creates sample tasks for a specific week
     */
    private List<CalendarTask> createWeekTasks(User user, LocalDate weekStart) {
        List<CalendarTask> tasks = new ArrayList<>();

        // Monday task
        CalendarTask mondayTask = new CalendarTask();
        mondayTask.setId(10L);
        mondayTask.setUser(user);
        mondayTask.setDate(weekStart);  // Monday
        mondayTask.setTime(LocalTime.of(9, 0));
        mondayTask.setTitle("Monday Standup");
        mondayTask.setNotes("Daily standup meeting");
        mondayTask.setExercise(false);
        mondayTask.setCompleted(false);
        tasks.add(mondayTask);

        // Tuesday task
        CalendarTask tuesdayTask = new CalendarTask();
        tuesdayTask.setId(11L);
        tuesdayTask.setUser(user);
        tuesdayTask.setDate(weekStart.plusDays(1));  // Tuesday
        tuesdayTask.setTime(LocalTime.of(10, 0));
        tuesdayTask.setTitle("Tuesday Planning");
        tuesdayTask.setNotes("Sprint planning session");
        tuesdayTask.setExercise(false);
        tuesdayTask.setCompleted(false);
        tasks.add(tuesdayTask);

        // Wednesday task
        CalendarTask wednesdayTask = new CalendarTask();
        wednesdayTask.setId(12L);
        wednesdayTask.setUser(user);
        wednesdayTask.setDate(weekStart.plusDays(2));  // Wednesday
        wednesdayTask.setTime(LocalTime.of(14, 0));
        wednesdayTask.setTitle("Wednesday Review");
        wednesdayTask.setNotes("Mid-week review");
        wednesdayTask.setExercise(false);
        wednesdayTask.setCompleted(false);
        tasks.add(wednesdayTask);

        // Thursday task
        CalendarTask thursdayTask = new CalendarTask();
        thursdayTask.setId(13L);
        thursdayTask.setUser(user);
        thursdayTask.setDate(weekStart.plusDays(3));  // Thursday
        thursdayTask.setTime(LocalTime.of(15, 0));
        thursdayTask.setTitle("Thursday Demo");
        thursdayTask.setNotes("Demo new features");
        thursdayTask.setExercise(false);
        thursdayTask.setCompleted(false);
        tasks.add(thursdayTask);

        // Friday task
        CalendarTask fridayTask = new CalendarTask();
        fridayTask.setId(14L);
        fridayTask.setUser(user);
        fridayTask.setDate(weekStart.plusDays(4));  // Friday
        fridayTask.setTime(LocalTime.of(16, 0));
        fridayTask.setTitle("Friday Retrospective");
        fridayTask.setNotes("Week retrospective");
        fridayTask.setExercise(false);
        fridayTask.setCompleted(false);
        tasks.add(fridayTask);

        return tasks;
    }

    /**
     * Creates sample schedule occurrences for a specific week
     */
    private Map<LocalDate, List<ScheduleOccurrence>> createWeekScheduleOccurrences(User user, LocalDate weekStart) {
        Map<LocalDate, List<ScheduleOccurrence>> occurrences = new HashMap<>();

        // Monday workout
        Exercise squat = new Exercise();
        squat.setId(201L);
        squat.setName("Squat");
        
        ScheduleOccurrence mondayOcc = new ScheduleOccurrence();
        mondayOcc.setId(20L);
        mondayOcc.setUser(user);
        mondayOcc.setExercise(squat);
        mondayOcc.setScheduleName("Leg Day");
        mondayOcc.setDate(weekStart);
        mondayOcc.setCompleted(false);
        occurrences.computeIfAbsent(mondayOcc.getDate(), k -> new ArrayList<>()).add(mondayOcc);

        // Wednesday workout
        Exercise bench = new Exercise();
        bench.setId(202L);
        bench.setName("Bench Press");
        
        ScheduleOccurrence wednesdayOcc = new ScheduleOccurrence();
        wednesdayOcc.setId(21L);
        wednesdayOcc.setUser(user);
        wednesdayOcc.setExercise(bench);
        wednesdayOcc.setScheduleName("Chest Day");
        wednesdayOcc.setDate(weekStart.plusDays(2));
        wednesdayOcc.setCompleted(false);
        occurrences.computeIfAbsent(wednesdayOcc.getDate(), k -> new ArrayList<>()).add(wednesdayOcc);

        // Friday workout
        Exercise deadlift = new Exercise();
        deadlift.setId(203L);
        deadlift.setName("Deadlift");
        
        ScheduleOccurrence fridayOcc = new ScheduleOccurrence();
        fridayOcc.setId(22L);
        fridayOcc.setUser(user);
        fridayOcc.setExercise(deadlift);
        fridayOcc.setScheduleName("Back Day");
        fridayOcc.setDate(weekStart.plusDays(4));
        fridayOcc.setCompleted(false);
        occurrences.computeIfAbsent(fridayOcc.getDate(), k -> new ArrayList<>()).add(fridayOcc);

        return occurrences;
    }

    /**
     * Creates sample schedules for the user
     */
    private List<Schedule> createTestSchedules(User user) {
        List<Schedule> schedules = new ArrayList<>();

        Schedule schedule1 = new Schedule();
        schedule1.setId(1L);
        schedule1.setUser(user);
        schedule1.setName("Weekly Workout Plan");
        schedule1.setDescription("5-day workout split");
        schedule1.setScheduleType(ScheduleType.WEEKLY);
        schedules.add(schedule1);

        Schedule schedule2 = new Schedule();
        schedule2.setId(2L);
        schedule2.setUser(user);
        schedule2.setName("Cardio Routine");
        schedule2.setDescription("Cardio every other day");
        schedule2.setScheduleType(ScheduleType.WEEKLY);
        schedules.add(schedule2);

        return schedules;
    }

    /**
     * Test: Verify that month view generates correct grid size with placeholders.
     * The grid should always have 35 or 42 cells (5 or 6 weeks).
     * Leading and trailing placeholders ensure proper alignment.
     */
    @Test
    void monthViewGeneratesCorrectGridSizeWithPlaceholders() throws Exception {
        // Arrange
        User testUser = new User();
        testUser.setId(200L);
        testUser.setUsername("griduser");

        UserSettings settings = new UserSettings();
        settings.setCalendarTaskOrdering(CalendarTaskOrderingPreference.CHRONOLOGICAL);
        settings.setCalendarTaskLayout(CalendarTaskLayoutPreference.COMBINED_LIST);

        given(userSettingsService.getOrCreate(eq(testUser))).willReturn(settings);
        given(platformSubscriptionService.isPremium(eq(testUser.getId()), any(Clock.class))).willReturn(false);
        given(scheduleService.findByUser(eq(testUser))).willReturn(Collections.emptyList());
        given(scheduleAppliedRepository.findByUserAndSchedule(any(), any())).willReturn(Collections.emptyList());
        given(taskService.getTasksByRange(any(), any(), any())).willReturn(Collections.emptyMap());
        given(scheduleOccurrenceService.getOccurrencesForUserInMonth(any(), anyInt(), anyInt()))
                .willReturn(Collections.emptyMap());

        // Act: Request March 2026 (starts on Sunday, so 6 leading placeholders for Monday-first calendar)
        MvcResult result = mvc.perform(get("/calendar")
                .param("view", "month")
                        .param("month", "3")
                        .param("year", "2026")
                        .sessionAttr("user", testUser))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("calendarCells"))
                .andReturn();

        // Assert: Verify grid size
        @SuppressWarnings("unchecked")
        List<uk.ac.cf._5.group14.One_To_One.CalendarData.CalendarCellModel> cells =
                (List<uk.ac.cf._5.group14.One_To_One.CalendarData.CalendarCellModel>)
                        result.getModelAndView().getModel().get("calendarCells");

        assertNotNull(cells, "calendarCells should not be null");
        assertTrue(cells.size() == 35 || cells.size() == 42,
                "Grid must have exactly 35 or 42 cells, got " + cells.size());

        // March 2026 has 31 days and starts on Sunday (day 7)
        // For Monday-first calendar, we need 6 leading placeholders
        // Total: 6 + 31 = 37 cells, rounded to 42 (6 weeks)
        assertEquals(42, cells.size(), "March 2026 should generate 42-cell grid");

        // Count placeholders
        long placeholderCount = cells.stream().filter(c -> c.isPlaceholder()).count();
        long realDayCount = cells.stream().filter(c -> !c.isPlaceholder()).count();

        assertEquals(31, realDayCount, "Should have exactly 31 real day cells for March");
        assertEquals(11, placeholderCount, "Should have 11 placeholder cells (6 leading + 5 trailing)");
    }

    /**
     * Test: Verify that placeholder cells do NOT emit data-date attributes in rendered HTML.
     * Only real day cells should have data-date, data-type, and other data attributes.
     */
    @Test
    void monthViewPlaceholderCellsDoNotEmitDataAttributes() throws Exception {
        // Arrange
        User testUser = new User();
        testUser.setId(201L);
        testUser.setUsername("placeholderuser");

        UserSettings settings = new UserSettings();
        settings.setCalendarTaskOrdering(CalendarTaskOrderingPreference.CHRONOLOGICAL);
        settings.setCalendarTaskLayout(CalendarTaskLayoutPreference.COMBINED_LIST);

        given(userSettingsService.getOrCreate(eq(testUser))).willReturn(settings);
        given(platformSubscriptionService.isPremium(eq(testUser.getId()), any(Clock.class))).willReturn(false);
        given(scheduleService.findByUser(eq(testUser))).willReturn(Collections.emptyList());
        given(scheduleAppliedRepository.findByUserAndSchedule(any(), any())).willReturn(Collections.emptyList());
        given(taskService.getTasksByRange(any(), any(), any())).willReturn(Collections.emptyMap());
        given(scheduleOccurrenceService.getOccurrencesForUserInMonth(any(), anyInt(), anyInt()))
                .willReturn(Collections.emptyMap());

        // Act: Request February 2026
        MvcResult result = mvc.perform(get("/calendar")
                .param("view", "month")
                        .param("month", "2")
                        .param("year", "2026")
                        .sessionAttr("user", testUser))
                .andExpect(status().isOk())
                .andReturn();

        String htmlContent = result.getResponse().getContentAsString();

        assertTrue(htmlContent.contains("calendar-month-grid"),
                "Rendered response must include the month grid rather than a partial template");
        assertTrue(htmlContent.contains("/js/calendar/month.js"),
                "Rendered response must reach the page script block");
        assertFalse(htmlContent.contains("CalendarTaskLayoutPreference"),
                "Server-only enum types must not leak into the rendered response");

        // Assert: Verify placeholder cells exist
        assertTrue(htmlContent.contains("calendar-day-card--placeholder"),
                "HTML should contain placeholder cells with class 'calendar-day-card--placeholder'");

        // Count data-date occurrences - should match real days only (28 for February 2026)
        int dataDateCount = countOccurrences(htmlContent, "data-date=");
        assertEquals(28, dataDateCount,
                "Should have exactly 28 data-date attributes (one per real day in February 2026)");

        // Count placeholder divs
        int placeholderCount = countOccurrences(htmlContent, "calendar-day-card--placeholder");
        assertEquals(7, placeholderCount,
                "Should have exactly 7 placeholder cells for February 2026 (6 leading + 1 trailing)");
    }

    /**
     * Test: Verify February 2026 grid structure (common edge case: 28 days, starts on Sunday).
     * February 2026: 28 days, starts on Sunday (day 7 in ISO week)
     * Monday-first calendar needs 6 leading placeholders + 28 days + 1 trailing = 35 cells
     */
    @Test
    void monthViewFebruary2026GridStructure() throws Exception {
        // Arrange
        User testUser = new User();
        testUser.setId(202L);
        testUser.setUsername("febuser");

        UserSettings settings = new UserSettings();
        settings.setCalendarTaskOrdering(CalendarTaskOrderingPreference.CHRONOLOGICAL);
        settings.setCalendarTaskLayout(CalendarTaskLayoutPreference.COMBINED_LIST);

        given(userSettingsService.getOrCreate(eq(testUser))).willReturn(settings);
        given(platformSubscriptionService.isPremium(eq(testUser.getId()), any(Clock.class))).willReturn(false);
        given(scheduleService.findByUser(eq(testUser))).willReturn(Collections.emptyList());
        given(scheduleAppliedRepository.findByUserAndSchedule(any(), any())).willReturn(Collections.emptyList());
        given(taskService.getTasksByRange(any(), any(), any())).willReturn(Collections.emptyMap());
        given(scheduleOccurrenceService.getOccurrencesForUserInMonth(any(), anyInt(), anyInt()))
                .willReturn(Collections.emptyMap());

        // Act
        MvcResult result = mvc.perform(get("/calendar")
                .param("view", "month")
                        .param("month", "2")
                        .param("year", "2026")
                        .sessionAttr("user", testUser))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        @SuppressWarnings("unchecked")
        List<uk.ac.cf._5.group14.One_To_One.CalendarData.CalendarCellModel> cells =
                (List<uk.ac.cf._5.group14.One_To_One.CalendarData.CalendarCellModel>)
                        result.getModelAndView().getModel().get("calendarCells");

        assertNotNull(cells);
        assertEquals(35, cells.size(), "February 2026 should have 35 cells (5 weeks)");

        // Verify first 6 are placeholders  
        for (int i = 0; i < 6; i++) {
            assertTrue(cells.get(i).isPlaceholder(),
                    "Cell " + i + " should be a placeholder (Monday-Saturday before Sunday Feb 1)");
        }

        // Verify cells 6-33 are real days (28 days)
        for (int i = 6; i < 34; i++) {
            assertFalse(cells.get(i).isPlaceholder(),
                    "Cell " + i + " should be a real day");
        }

        // Verify last cell is placeholder
        assertTrue(cells.get(34).isPlaceholder(), "Last cell should be a placeholder");
    }

    // Helper method to count occurrences of a substring
    private int countOccurrences(String str, String substring) {
        int count = 0;
        int index = 0;
        while ((index = str.indexOf(substring, index)) != -1) {
            count++;
            index += substring.length();
        }
        return count;
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
        AuthHelper authHelper() {
            return mock(AuthHelper.class);
        }

        @Bean
        PlatformSubscriptionService platformSubscriptionService() {
            return mock(PlatformSubscriptionService.class);
        }

        @Bean
        Clock systemClock() {
            return Clock.system(ZoneId.systemDefault());
        }

        @Bean
        CalendarDayModelBuilder calendarDayModelBuilder() {
            return new CalendarDayModelBuilder();
        }
    
        @Bean
        public DevModeProperties devModeProperties() {
            return new DevModeProperties();
        }
}
}
