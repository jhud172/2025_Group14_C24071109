package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Level;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.util.List;
import java.util.Optional;

/**
 * Repository for persisting and retrieving user points / levels.
 */
public interface LevelProgressRepository extends JpaRepository<LevelProgress, Long> {
    Optional<LevelProgress> findByUser(User user);
    List<LevelProgress> findAllByOrderByPointsDesc();
}
