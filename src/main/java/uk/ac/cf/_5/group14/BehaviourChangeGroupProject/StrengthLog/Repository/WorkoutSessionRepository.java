package uk.ac.cf._5.group14.BehaviourChangeGroupProject.StrengthLog.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.StrengthLog.WorkoutSession;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Workout.Workout;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, Long> {
    Optional<WorkoutSession> findByUserAndDateAndWorkout(User user, LocalDate date, Workout workout);
    List<WorkoutSession> findByUserAndDate(User user, LocalDate date);

    List<WorkoutSession> findTop3ByUserOrderByDateDesc(User user);

    List<WorkoutSession> findTop20ByUserOrderByDateDesc(User user);

    List<WorkoutSession> findByUserAndDateBetweenOrderByDateDesc(User user, LocalDate from, LocalDate to);
}
