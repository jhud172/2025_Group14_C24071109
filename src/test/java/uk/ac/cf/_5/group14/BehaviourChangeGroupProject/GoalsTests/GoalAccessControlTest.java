package uk.ac.cf._5.group14.BehaviourChangeGroupProject.GoalsTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTask;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTaskRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Goals.*;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleOccurrence;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleOccurrenceRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerClient.TrainerClientLink;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerClient.TrainerClientLinkRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerClient.TrainerClientLinkStatus;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.Role;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ConditionsPreferences.UserPreference.UserPreferenceRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseData.Exercise;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseData.ExerciseRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Workout.WorkoutRepository;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class GoalAccessControlTest {

    @Autowired
    private GoalService goalService;

    @Autowired
    private GoalLinkService goalLinkService;

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private GoalLinkRepository goalLinkRepository;

    @Autowired
    private CalendarTaskRepository calendarTaskRepository;

    @Autowired
    private ScheduleOccurrenceRepository scheduleOccurrenceRepository;

    @Autowired
    private TrainerClientLinkRepository trainerClientLinkRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private UserPreferenceRepository userPreferenceRepository;

    @Autowired
    private WorkoutRepository workoutRepository;

    private User clientA;
    private User clientB;
    private User trainer;

    @BeforeEach
    void setup() {
        goalLinkRepository.deleteAll();
        goalRepository.deleteAll();
        scheduleOccurrenceRepository.deleteAll();
        calendarTaskRepository.deleteAll();
        trainerClientLinkRepository.deleteAll();
        workoutRepository.deleteAll();
        userPreferenceRepository.deleteAll();
        exerciseRepository.deleteAll();
        userRepository.deleteAll();

        String suffix = UUID.randomUUID().toString().replace("-", "");

        clientA = new User("clientA+" + suffix + "@example.com", "Client", "A", "goal_client_a_" + suffix, "password123");
        clientA.setRole(Role.CLIENT);
        clientA = userRepository.save(clientA);

        clientB = new User("clientB+" + suffix + "@example.com", "Client", "B", "goal_client_b_" + suffix, "password123");
        clientB.setRole(Role.CLIENT);
        clientB = userRepository.save(clientB);

        trainer = new User("trainer+" + suffix + "@example.com", "Trainer", "One", "goal_trainer_" + suffix, "password123");
        trainer.setRole(Role.TRAINER);
        trainer = userRepository.save(trainer);
    }

    @Test
    void clientCannotAccessOtherClientsGoal() {
        GoalForm form = new GoalForm();
        form.setTitle("Lose 5kg");
        form.setGoalType(GoalType.FAT_LOSS);
        form.setStatus(GoalStatus.ACTIVE);
        Goal goal = goalService.createGoal(clientA, null, form);

        assertThatThrownBy(() -> goalService.getGoalForViewer(clientB, goal.getId()))
            .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void trainerAccessRequiresActiveLink() {
        GoalForm form = new GoalForm();
        form.setTitle("Mobility routine");
        form.setGoalType(GoalType.REHAB);
        form.setStatus(GoalStatus.ACTIVE);
        Goal goal = goalService.createGoal(clientA, null, form);

        assertThatThrownBy(() -> goalService.getGoalForViewer(trainer, goal.getId()))
            .isInstanceOf(AccessDeniedException.class);

        trainerClientLinkRepository.save(new TrainerClientLink(clientA.getId(), trainer.getId(), TrainerClientLinkStatus.ACTIVE));

        Goal fetched = goalService.getGoalForViewer(trainer, goal.getId());
        assertThat(fetched.getId()).isEqualTo(goal.getId());
    }

    @Test
    void goalLinkRejectsOtherUsersItems() {
        GoalForm form = new GoalForm();
        form.setTitle("Consistency");
        form.setGoalType(GoalType.HABIT);
        form.setStatus(GoalStatus.ACTIVE);
        Goal goal = goalService.createGoal(clientA, null, form);

        CalendarTask task = new CalendarTask();
        task.setUser(clientB);
        task.setDate(LocalDate.now());
        task.setTitle("Other task");
        task.setExercise(false);
        task.setCompleted(false);
        task = calendarTaskRepository.save(task);

        Exercise exercise = new Exercise();
        exercise.setName("Push Up");
        exercise.setCategory("Bodyweight");
        exercise.setDifficulty(1);
        exercise.setType("Strength");
        exercise = exerciseRepository.save(exercise);

        ScheduleOccurrence occurrence = new ScheduleOccurrence();
        occurrence.setUser(clientB);
        occurrence.setExercise(exercise);
        occurrence.setScheduleName("Morning" );
        occurrence.setDate(LocalDate.now());
        occurrence.setCompleted(false);
        occurrence = scheduleOccurrenceRepository.save(occurrence);

        Long taskId = task.getId();
        Long occurrenceId = occurrence.getId();

        assertThatThrownBy(() -> goalLinkService.linkCalendarTask(clientA, goal.getId(), taskId, GoalLinkSource.SELF))
            .isInstanceOf(AccessDeniedException.class);

        assertThatThrownBy(() -> goalLinkService.linkScheduleOccurrence(clientA, goal.getId(), occurrenceId, GoalLinkSource.SELF))
            .isInstanceOf(AccessDeniedException.class);
    }
}
