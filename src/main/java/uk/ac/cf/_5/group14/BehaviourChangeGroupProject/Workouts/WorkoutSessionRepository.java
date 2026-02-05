package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Workouts;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.util.List;
import java.util.Optional;

public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, Long> {
    List<WorkoutSession> findByUserOrderByStartedAtDesc(User user);
    Optional<WorkoutSession> findByIdAndUser(Long id, User user);
}
