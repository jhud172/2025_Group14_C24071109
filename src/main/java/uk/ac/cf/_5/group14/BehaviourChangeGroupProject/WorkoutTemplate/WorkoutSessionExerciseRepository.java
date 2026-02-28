package uk.ac.cf._5.group14.BehaviourChangeGroupProject.WorkoutTemplate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkoutSessionExerciseRepository extends JpaRepository<WorkoutSessionExercise, Long> {

    List<WorkoutSessionExercise> findBySessionOrderByOrderIndexAsc(WorkoutSession session);
}
