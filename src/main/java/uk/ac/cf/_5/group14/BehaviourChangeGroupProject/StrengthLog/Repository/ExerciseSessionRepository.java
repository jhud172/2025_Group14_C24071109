package uk.ac.cf._5.group14.BehaviourChangeGroupProject.StrengthLog.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.StrengthLog.ExerciseSession;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.StrengthLog.WorkoutSession;

import java.util.List;

public interface ExerciseSessionRepository extends JpaRepository<ExerciseSession, Long> {
    List<ExerciseSession> findByWorkoutSessionOrderByOrderIndexAsc(WorkoutSession workoutSession);
}
