package uk.ac.cf._5.group14.One_To_One.WorkoutTemplate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.util.List;

@Repository("workoutTemplateSessionRepository")
public interface WorkoutPlayerSessionRepository extends JpaRepository<WorkoutSession, Long> {

    List<WorkoutSession> findByUserOrderByStartedAtDesc(User user);

    List<WorkoutSession> findByUserAndStatus(User user, SessionStatus status);
}
