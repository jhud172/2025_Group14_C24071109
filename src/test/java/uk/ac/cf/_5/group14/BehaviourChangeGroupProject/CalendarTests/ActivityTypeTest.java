package uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.ActivityType;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTask;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTaskRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTaskServiceImpl;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTaskWarningService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ActivityTypeTest {

    private CalendarTaskRepository repo;
    private CalendarTaskWarningService warningService;
    private CalendarTaskServiceImpl service;
    private User user;

    @BeforeEach
    void setUp() {
        repo = mock(CalendarTaskRepository.class);
        warningService = mock(CalendarTaskWarningService.class);
        service = new CalendarTaskServiceImpl(repo, warningService);

        user = new User();
        user.setId(1L);

        when(repo.save(any(CalendarTask.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void createTaskWithSwimActivityType_setsExerciseTrue() {
        service.createTask(user, LocalDate.of(2026, 3, 1), LocalTime.NOON,
                "Morning swim", "Pool session", false, false, ActivityType.SWIM);

        var captor = forClass(CalendarTask.class);
        verify(repo).save(captor.capture());
        CalendarTask saved = captor.getValue();

        assertEquals(ActivityType.SWIM, saved.getActivityType());
        assertTrue(saved.getExercise(), "exercise flag should be true when activityType is set");
        assertEquals("Morning swim", saved.getTitle());
    }

    @Test
    void createTaskWithRunActivityType_setsActivityTypeAndExercise() {
        service.createTask(user, LocalDate.of(2026, 3, 1), LocalTime.NOON,
                "Morning run", null, false, false, ActivityType.RUN);

        var captor = forClass(CalendarTask.class);
        verify(repo).save(captor.capture());
        CalendarTask saved = captor.getValue();

        assertEquals(ActivityType.RUN, saved.getActivityType());
        assertTrue(saved.getExercise());
    }

    @Test
    void createTaskWithNullActivityType_preservesExerciseFlag() {
        service.createTask(user, LocalDate.of(2026, 3, 1), LocalTime.NOON,
                "Grocery run", null, false, false, null);

        var captor = forClass(CalendarTask.class);
        verify(repo).save(captor.capture());
        CalendarTask saved = captor.getValue();

        assertEquals(null, saved.getActivityType());
    }

    @Test
    void activityTypeEnum_hasCorrectValues() {
        assertEquals("🏊", ActivityType.SWIM.getIcon());
        assertEquals("Swim", ActivityType.SWIM.getLabel());
        assertEquals("🏋️", ActivityType.GYM.getIcon());
        assertEquals("🏃", ActivityType.RUN.getIcon());
        assertEquals("🚴", ActivityType.BIKE.getIcon());
        assertEquals("⚡", ActivityType.CUSTOM.getIcon());
    }

    @Test
    void updateTaskWithBikeActivityType_setsExerciseTrue() {
        CalendarTask existing = new CalendarTask();
        existing.setUser(user);
        existing.setTitle("Old");
        existing.setExercise(false);

        when(repo.findById(42L)).thenReturn(Optional.of(existing));

        service.updateTask(42L, user, "Bike ride", null, "Cycling session", false, ActivityType.BIKE);

        var captor = forClass(CalendarTask.class);
        verify(repo).save(captor.capture());
        CalendarTask saved = captor.getValue();

        assertEquals(ActivityType.BIKE, saved.getActivityType());
        assertTrue(saved.getExercise());
        assertEquals("Bike ride", saved.getTitle());
    }
}
