package uk.ac.cf._5.group14.BehaviourChangeGroupProject.WorkoutTemplate;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.util.List;
import java.util.Optional;

public interface WorkoutUiTemplateRepository extends JpaRepository<WorkoutTemplate, Long> {

    List<WorkoutTemplate> findByUserIsNullOrderByName();

    List<WorkoutTemplate> findByUserOrderByUpdatedAtDesc(User user);

    Optional<WorkoutTemplate> findFirstByUserIsNullAndIsDefaultTrue();

    Optional<WorkoutTemplate> findFirstByUserAndIsDefaultTrue(User user);

    Optional<WorkoutTemplate> findByIdAndUser(Long id, User user);
}
