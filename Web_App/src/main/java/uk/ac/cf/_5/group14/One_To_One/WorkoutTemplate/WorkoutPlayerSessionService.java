package uk.ac.cf._5.group14.One_To_One.WorkoutTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface WorkoutPlayerSessionService {

    WorkoutSession startSession(Long userId, Long workoutId);

    WorkoutSession completeSession(Long sessionId);

    BigDecimal computeTotalVolume(Long sessionId);

    Optional<WorkoutSession> findById(Long sessionId);

    WorkoutSessionSet addSet(Long exerciseId, Integer reps, BigDecimal weight, BigDecimal rpe);

    WorkoutSessionSet updateSet(Long setId, Integer reps, BigDecimal weight, BigDecimal rpe);

    WorkoutSessionSet completeSet(Long setId);

    WorkoutSessionExercise addExercise(Long sessionId, Long exerciseId, Long customExerciseId, String notes);

    WorkoutSessionExercise updateExerciseMode(Long sessionExerciseId, ExerciseMode mode, String groupKey);

    List<WorkoutSessionExercise> reorderExercises(Long sessionId, List<Long> orderedExerciseIds);

    void deleteSet(Long setId);
}
