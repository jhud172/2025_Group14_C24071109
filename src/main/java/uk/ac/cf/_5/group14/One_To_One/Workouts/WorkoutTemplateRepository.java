package uk.ac.cf._5.group14.One_To_One.Workouts;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.util.List;
import java.util.Optional;

public interface WorkoutTemplateRepository extends JpaRepository<WorkoutTemplate, Long> {
    List<WorkoutTemplate> findByOwnerUserOrderByUpdatedAtDesc(User ownerUser);
    Optional<WorkoutTemplate> findByIdAndOwnerUser(Long id, User ownerUser);
}
