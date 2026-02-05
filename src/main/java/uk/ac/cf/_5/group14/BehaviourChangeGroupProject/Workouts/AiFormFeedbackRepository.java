package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Workouts;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AiFormFeedbackRepository extends JpaRepository<AiFormFeedback, Long> {

    Optional<AiFormFeedback> findByVideo(WorkoutSetVideo video);
}
