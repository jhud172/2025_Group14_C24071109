package uk.ac.cf._5.group14.One_To_One.GoalsTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.ac.cf._5.group14.One_To_One.CalendarData.CalendarTask;
import uk.ac.cf._5.group14.One_To_One.CalendarData.CalendarTaskRepository;
import uk.ac.cf._5.group14.One_To_One.ExerciseData.Exercise;
import uk.ac.cf._5.group14.One_To_One.ExerciseData.ExerciseRepository;
import uk.ac.cf._5.group14.One_To_One.Goals.*;
import uk.ac.cf._5.group14.One_To_One.ScheduleData.ScheduleOccurrence;
import uk.ac.cf._5.group14.One_To_One.ScheduleData.ScheduleOccurrenceRepository;
import uk.ac.cf._5.group14.One_To_One.Users.Role;
import uk.ac.cf._5.group14.One_To_One.Users.User;
import uk.ac.cf._5.group14.One_To_One.Users.UserRepository;
import uk.ac.cf._5.group14.One_To_One.ConditionsPreferences.UserPreference.UserPreferenceRepository;
import uk.ac.cf._5.group14.One_To_One.Workout.WorkoutRepository;
import uk.ac.cf._5.group14.One_To_One.Workouts.WorkoutSession;
import uk.ac.cf._5.group14.One_To_One.Workouts.WorkoutSessionRepository;
import uk.ac.cf._5.group14.One_To_One.Workouts.WorkoutTemplate;
import uk.ac.cf._5.group14.One_To_One.Workouts.WorkoutTemplateRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class GoalAdherenceServiceTest {

    @Autowired
    private GoalService goalService;

    @Autowired
    private GoalLinkService goalLinkService;

    @Autowired
    private GoalAdherenceService goalAdherenceService;

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private GoalLinkRepository goalLinkRepository;

    @Autowired
    private CalendarTaskRepository calendarTaskRepository;

    @Autowired
    private ScheduleOccurrenceRepository scheduleOccurrenceRepository;

    @Autowired
    private WorkoutTemplateRepository workoutTemplateRepository;

    @Autowired
    private WorkoutSessionRepository workoutSessionRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserPreferenceRepository userPreferenceRepository;

    @Autowired
    private WorkoutRepository workoutRepository;

    private User client;

    @BeforeEach
    void setup() {
        String suffix = UUID.randomUUID().toString().replace("-", "");
        client = new User("client+" + suffix + "@example.com", "Client", "One", "goal_adherence_" + suffix, "password123");
        client.setRole(Role.CLIENT);
        client = userRepository.save(client);
    }

    @Test
    void adherenceCountsLinkedItemsInWeek() {
        GoalForm form = new GoalForm();
        form.setTitle("Weekly consistency");
        form.setGoalType(GoalType.HABIT);
        form.setStatus(GoalStatus.ACTIVE);
        Goal goal = goalService.createGoal(client, null, form);

        LocalDate weekStart = GoalAdherenceService.normalizeWeekStart(LocalDate.now());
        LocalDate inWeek = weekStart.plusDays(2);

        CalendarTask taskA = new CalendarTask();
        taskA.setUser(client);
        taskA.setDate(inWeek);
        taskA.setTitle("Task A");
        taskA.setExercise(false);
        taskA.setCompleted(true);
        taskA = calendarTaskRepository.save(taskA);

        CalendarTask taskB = new CalendarTask();
        taskB.setUser(client);
        taskB.setDate(inWeek.plusDays(1));
        taskB.setTitle("Task B");
        taskB.setExercise(false);
        taskB.setCompleted(false);
        taskB = calendarTaskRepository.save(taskB);

        Exercise exercise = new Exercise();
        exercise.setName("Row");
        exercise.setCategory("Strength");
        exercise.setDifficulty(2);
        exercise.setType("Strength");
        exercise = exerciseRepository.save(exercise);

        ScheduleOccurrence occurrence = new ScheduleOccurrence();
        occurrence.setUser(client);
        occurrence.setExercise(exercise);
        occurrence.setScheduleName("Daily" );
        occurrence.setDate(inWeek);
        occurrence.setCompleted(true);
        occurrence = scheduleOccurrenceRepository.save(occurrence);

        WorkoutTemplate template = new WorkoutTemplate();
        template.setOwnerUser(client);
        template.setOwnerRole(Role.CLIENT);
        template.setName("Template A");
        workoutTemplateRepository.save(template);

        WorkoutSession session = new WorkoutSession();
        session.setUser(client);
        session.setTemplate(template);
        session.setNameSnapshot("Session A");
        session.setStartedAt(LocalDateTime.of(inWeek, java.time.LocalTime.NOON));
        session.setCompleted(true);
        session = workoutSessionRepository.save(session);

        goalLinkService.linkCalendarTask(client, goal.getId(), taskA.getId(), GoalLinkSource.SELF);
        goalLinkService.linkCalendarTask(client, goal.getId(), taskB.getId(), GoalLinkSource.SELF);
        goalLinkService.linkScheduleOccurrence(client, goal.getId(), occurrence.getId(), GoalLinkSource.SELF);
        goalLinkService.linkWorkoutSession(client, goal.getId(), session.getId(), GoalLinkSource.SELF);

        GoalAdherenceWeek result = goalAdherenceService.calculateWeek(goal.getId(), weekStart);

        assertThat(result.getPlannedCount()).isEqualTo(4);
        assertThat(result.getCompletedCount()).isEqualTo(3);
        assertThat(result.getPercent()).isEqualTo(75);
    }
}
