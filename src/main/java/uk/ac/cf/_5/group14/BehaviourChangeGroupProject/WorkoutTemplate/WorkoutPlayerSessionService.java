package uk.ac.cf._5.group14.BehaviourChangeGroupProject.WorkoutTemplate;

import java.math.BigDecimal;
import java.util.Optional;

public interface WorkoutPlayerSessionService {

    WorkoutSession startSession(Long userId, Long workoutId);

    WorkoutSession completeSession(Long sessionId);

    BigDecimal computeTotalVolume(Long sessionId);

    Optional<WorkoutSession> findById(Long sessionId);

    WorkoutSessionSet addSet(Long exerciseId, Integer reps, BigDecimal weight, BigDecimal rpe);

    WorkoutSessionSet updateSet(Long setId, Integer reps, BigDecimal weight, BigDecimal rpe);

    WorkoutSessionSet completeSet(Long setId);
}
