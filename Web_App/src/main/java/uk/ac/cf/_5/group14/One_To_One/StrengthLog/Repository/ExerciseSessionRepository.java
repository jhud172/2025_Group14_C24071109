package uk.ac.cf._5.group14.One_To_One.StrengthLog.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.cf._5.group14.One_To_One.StrengthLog.ExerciseSession;
import uk.ac.cf._5.group14.One_To_One.StrengthLog.WorkoutSession;

import java.util.List;
import java.util.Optional;

public interface ExerciseSessionRepository extends JpaRepository<ExerciseSession, Long> {
    List<ExerciseSession> findByWorkoutSessionOrderByOrderIndexAsc(WorkoutSession workoutSession);
    Optional<ExerciseSession> findByIdAndWorkoutSessionId(Long id, Long workoutSessionId);
}
