package uk.ac.cf._5.group14.One_To_One.TrainerAssignments;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssignedScheduleRepository extends JpaRepository<AssignedSchedule, Long> {
    List<AssignedSchedule> findByClientUserIdOrderByAssignedAtDesc(Long clientUserId);
    List<AssignedSchedule> findByTrainerUserIdAndClientUserIdOrderByAssignedAtDesc(Long trainerUserId, Long clientUserId);
    Optional<AssignedSchedule> findByIdAndClientUserId(Long id, Long clientUserId);
}
