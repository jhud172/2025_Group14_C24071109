package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Goals;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findByOwnerUserOrderByUpdatedAtDesc(User ownerUser);
    List<Goal> findByOwnerUserAndArchivedOrderByUpdatedAtDesc(User ownerUser, boolean archived);
}
