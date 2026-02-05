package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Workouts;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkoutSetLogRepository extends JpaRepository<WorkoutSetLog, Long> {
    Optional<WorkoutSetLog> findByIdAndSession(Long id, WorkoutSession session);
}
