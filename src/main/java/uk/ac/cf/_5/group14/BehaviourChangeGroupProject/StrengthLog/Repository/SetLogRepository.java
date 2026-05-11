package uk.ac.cf._5.group14.BehaviourChangeGroupProject.StrengthLog.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.StrengthLog.SetLog;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.StrengthLog.ExerciseSession;

import java.util.List;

public interface SetLogRepository extends JpaRepository<SetLog, Long> {
    List<SetLog> findByExerciseSessionOrderBySetNumberAsc(ExerciseSession session);
}
