package uk.ac.cf._5.group14.BehaviourChangeGroupProject.WorkoutTemplateTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Workout.Workout;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Workout.WorkoutRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.WorkoutTemplate.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkoutPlayerSessionServiceTest {

    @Mock
    private WorkoutPlayerSessionRepository sessionRepository;

    @Mock
    private WorkoutTemplateService templateService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WorkoutRepository workoutRepository;

    @Mock
    private WorkoutSessionExerciseRepository exerciseRepository;

    @Mock
    private WorkoutSessionSetRepository setRepository;

    @InjectMocks
    private WorkoutPlayerSessionServiceImpl service;

    private User testUser;
    private Workout testWorkout;
    private WorkoutSession testSession;
    private WorkoutSessionExercise testExercise;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);

        testWorkout = new Workout();
        testWorkout.setId(10L);
        testWorkout.setName("Test Workout");

        testSession = new WorkoutSession();
        testSession.setId(100L);
        testSession.setUser(testUser);
        testSession.setWorkout(testWorkout);
        testSession.setStatus(SessionStatus.IN_PROGRESS);

        testExercise = new WorkoutSessionExercise();
        testExercise.setId(200L);
        testExercise.setSession(testSession);
        testExercise.setExerciseId(5L);
        testExercise.setOrderIndex(0);
        testExercise.setMode(ExerciseMode.NORMAL);
        testSession.getExercises().add(testExercise);
    }

    @Test
    void addExercise_addsNewExerciseToSession() {
        when(sessionRepository.findById(100L)).thenReturn(Optional.of(testSession));
        WorkoutSessionExercise saved = new WorkoutSessionExercise();
        saved.setId(201L);
        saved.setSession(testSession);
        saved.setExerciseId(7L);
        saved.setOrderIndex(1);
        saved.setMode(ExerciseMode.NORMAL);
        when(exerciseRepository.save(any())).thenReturn(saved);

        WorkoutSessionExercise result = service.addExercise(100L, 7L, null, "Test notes");

        assertNotNull(result);
        assertEquals(201L, result.getId());
        assertEquals(7L, result.getExerciseId());
        assertEquals(1, result.getOrderIndex());
        verify(exerciseRepository).save(any(WorkoutSessionExercise.class));
    }

    @Test
    void addExercise_withCustomExercise_setsCustomExerciseId() {
        when(sessionRepository.findById(100L)).thenReturn(Optional.of(testSession));
        WorkoutSessionExercise saved = new WorkoutSessionExercise();
        saved.setId(202L);
        saved.setCustomExerciseId(99L);
        saved.setOrderIndex(1);
        saved.setMode(ExerciseMode.NORMAL);
        when(exerciseRepository.save(any())).thenReturn(saved);

        WorkoutSessionExercise result = service.addExercise(100L, null, 99L, null);

        assertNotNull(result);
        assertEquals(99L, result.getCustomExerciseId());
    }

    @Test
    void addExercise_throwsWhenSessionNotFound() {
        when(sessionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                service.addExercise(999L, 5L, null, null));
    }

    @Test
    void updateExerciseMode_changesToSuperset() {
        when(exerciseRepository.findById(200L)).thenReturn(Optional.of(testExercise));
        when(exerciseRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        WorkoutSessionExercise result = service.updateExerciseMode(200L, ExerciseMode.SUPERSET, "group-A");

        assertEquals(ExerciseMode.SUPERSET, result.getMode());
        assertEquals("group-A", result.getGroupKey());
        verify(exerciseRepository).save(testExercise);
    }

    @Test
    void updateExerciseMode_changesToDropset() {
        when(exerciseRepository.findById(200L)).thenReturn(Optional.of(testExercise));
        when(exerciseRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        WorkoutSessionExercise result = service.updateExerciseMode(200L, ExerciseMode.DROPSET, null);

        assertEquals(ExerciseMode.DROPSET, result.getMode());
        assertNull(result.getGroupKey());
    }

    @Test
    void updateExerciseMode_throwsWhenExerciseNotFound() {
        when(exerciseRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                service.updateExerciseMode(999L, ExerciseMode.SUPERSET, null));
    }

    @Test
    void reorderExercises_updatesOrderIndices() {
        WorkoutSessionExercise ex2 = new WorkoutSessionExercise();
        ex2.setId(201L);
        ex2.setSession(testSession);
        ex2.setOrderIndex(1);
        testSession.getExercises().add(ex2);

        when(sessionRepository.findById(100L)).thenReturn(Optional.of(testSession));
        when(exerciseRepository.findBySessionOrderByOrderIndexAsc(testSession))
                .thenReturn(Arrays.asList(ex2, testExercise));

        List<WorkoutSessionExercise> result = service.reorderExercises(100L, Arrays.asList(201L, 200L));

        assertEquals(0, ex2.getOrderIndex());
        assertEquals(1, testExercise.getOrderIndex());
        verify(exerciseRepository, times(2)).save(any(WorkoutSessionExercise.class));
    }

    @Test
    void deleteSet_callsDeleteById() {
        service.deleteSet(500L);
        verify(setRepository).deleteById(500L);
    }

    @Test
    void completeSession_setsStatusAndEndTime() {
        when(sessionRepository.findById(100L)).thenReturn(Optional.of(testSession));
        when(sessionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        WorkoutSession result = service.completeSession(100L);

        assertEquals(SessionStatus.COMPLETED, result.getStatus());
        assertNotNull(result.getEndedAt());
    }
}
