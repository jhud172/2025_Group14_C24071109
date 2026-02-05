package uk.ac.cf._5.group14.BehaviourChangeGroupProject.StrengthLog.Service;

import uk.ac.cf._5.group14.BehaviourChangeGroupProject.StrengthLog.WorkoutSession;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Workout.Workout;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WorkoutSessionService {
    Optional<WorkoutSession> findByUserDateAndWorkout(User user, LocalDate date, Workout workout);
    WorkoutSession createIfMissing(User user, LocalDate date, Workout workout);
    List<WorkoutSession> findByUserAndDate(User user, LocalDate date);
}
