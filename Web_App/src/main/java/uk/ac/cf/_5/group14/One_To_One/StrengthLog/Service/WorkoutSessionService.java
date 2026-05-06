package uk.ac.cf._5.group14.One_To_One.StrengthLog.Service;

import uk.ac.cf._5.group14.One_To_One.StrengthLog.WorkoutSession;
import uk.ac.cf._5.group14.One_To_One.Users.User;
import uk.ac.cf._5.group14.One_To_One.Workout.Workout;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WorkoutSessionService {
    Optional<WorkoutSession> findByUserDateAndWorkout(User user, LocalDate date, Workout workout);
    WorkoutSession createIfMissing(User user, LocalDate date, Workout workout);
    List<WorkoutSession> findByUserAndDate(User user, LocalDate date);
    List<WorkoutSession> findCompletedByUserAndDateRange(User user, LocalDate from, LocalDate to);
}
