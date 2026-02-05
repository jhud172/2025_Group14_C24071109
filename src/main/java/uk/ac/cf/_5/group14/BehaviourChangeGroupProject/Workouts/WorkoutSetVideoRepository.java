package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Workouts;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkoutSetVideoRepository extends JpaRepository<WorkoutSetVideo, Long> {

    Optional<WorkoutSetVideo> findTopBySetLogOrderByCreatedAtDesc(WorkoutSetLog setLog);

    List<WorkoutSetVideo> findByStatusOrderByCreatedAtAsc(VideoProcessingStatus status);
}
