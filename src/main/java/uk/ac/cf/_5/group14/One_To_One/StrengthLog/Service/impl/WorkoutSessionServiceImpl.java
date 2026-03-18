package uk.ac.cf._5.group14.One_To_One.StrengthLog.Service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.cf._5.group14.One_To_One.ExerciseData.Exercise;
import uk.ac.cf._5.group14.One_To_One.StrengthLog.ExerciseSession;
import uk.ac.cf._5.group14.One_To_One.StrengthLog.WorkoutSession;
import uk.ac.cf._5.group14.One_To_One.StrengthLog.Repository.WorkoutSessionRepository;
import uk.ac.cf._5.group14.One_To_One.StrengthLog.Service.WorkoutSessionService;
import uk.ac.cf._5.group14.One_To_One.Users.User;
import uk.ac.cf._5.group14.One_To_One.Workout.Workout;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class WorkoutSessionServiceImpl implements WorkoutSessionService {

    @Autowired
    private WorkoutSessionRepository repository;

    @Override
    public Optional<WorkoutSession> findByUserDateAndWorkout(User user, LocalDate date, Workout workout) {
        return repository.findByUserAndDateAndWorkout(user, date, workout);
    }

    @Override
    public WorkoutSession createIfMissing(User user, LocalDate date, Workout workout) {
        return repository.findByUserAndDateAndWorkout(user, date, workout)
                .orElseGet(() -> {
                    WorkoutSession s = new WorkoutSession();
                    s.setUser(user);
                    s.setDate(date);
                    s.setWorkout(workout);
                    s.setNameSnapshot(workout.getName());
                    s.setCreatedAt(LocalDateTime.now());
                    // build exercise sessions from workout.exercises
                    int idx = 0;
                    if (workout.getExercises() != null) {
                        for (Exercise e : workout.getExercises()) {
                            ExerciseSession es = new ExerciseSession();
                            es.setWorkoutSession(s);
                            es.setExercise(e);
                            es.setOrderIndex(idx++);
                            s.getExerciseSessions().add(es);
                        }
                    }
                    return repository.save(s);
                });
    }

    @Override
    public List<WorkoutSession> findByUserAndDate(User user, LocalDate date) {
        return repository.findByUserAndDate(user, date);
    }

    @Override
    public List<WorkoutSession> findCompletedByUserAndDateRange(User user, LocalDate from, LocalDate to) {
        return repository.findByUserAndDateBetweenAndCompletedTrue(user, from, to);
    }
}
