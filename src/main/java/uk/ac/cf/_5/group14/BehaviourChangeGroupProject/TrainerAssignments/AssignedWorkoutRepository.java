package uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerAssignments;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssignedWorkoutRepository extends JpaRepository<AssignedWorkout, Long> {
    List<AssignedWorkout> findByClientUserIdOrderByAssignedAtDesc(Long clientUserId);
    List<AssignedWorkout> findByTrainerUserIdAndClientUserIdOrderByAssignedAtDesc(Long trainerUserId, Long clientUserId);
    Optional<AssignedWorkout> findByIdAndClientUserId(Long id, Long clientUserId);
}
