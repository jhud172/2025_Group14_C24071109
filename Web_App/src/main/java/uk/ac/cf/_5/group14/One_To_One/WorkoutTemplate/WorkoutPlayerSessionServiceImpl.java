package uk.ac.cf._5.group14.One_To_One.WorkoutTemplate;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.ac.cf._5.group14.One_To_One.ExerciseData.Exercise;
import uk.ac.cf._5.group14.One_To_One.Users.User;
import uk.ac.cf._5.group14.One_To_One.Users.UserRepository;
import uk.ac.cf._5.group14.One_To_One.Workout.Workout;
import uk.ac.cf._5.group14.One_To_One.Workout.WorkoutRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WorkoutPlayerSessionServiceImpl implements WorkoutPlayerSessionService {

    private final WorkoutPlayerSessionRepository sessionRepository;
    private final WorkoutTemplateService templateService;
    private final UserRepository userRepository;
    private final WorkoutRepository workoutRepository;
    private final WorkoutSessionExerciseRepository exerciseRepository;
    private final WorkoutSessionSetRepository setRepository;

    @Override
    public WorkoutSession startSession(Long userId, Long workoutId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        Workout workout = workoutRepository.findById(workoutId)
                .orElseThrow(() -> new IllegalArgumentException("Workout not found: " + workoutId));

        WorkoutTemplate template = templateService.getDefaultTemplateForUser(userId);

        WorkoutSession session = new WorkoutSession();
        session.setUser(user);
        session.setWorkout(workout);
        session.setTemplateUsed(template);
        if (template != null && template.getId() != null) {
            session.setTemplateNameSnapshot(template.getName());
            session.setConfigJsonSnapshot(template.getConfigJson());
        }
        session.setStartedAt(Instant.now());
        session.setStatus(SessionStatus.IN_PROGRESS);

        int idx = 0;
        if (workout.getExercises() != null) {
            for (Exercise e : workout.getExercises()) {
                WorkoutSessionExercise ex = new WorkoutSessionExercise();
                ex.setSession(session);
                ex.setExerciseId(e.getId());
                ex.setOrderIndex(idx++);
                session.getExercises().add(ex);
            }
        }

        return sessionRepository.save(session);
    }

    @Override
    public WorkoutSession completeSession(Long sessionId) {
        WorkoutSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));
        session.setStatus(SessionStatus.COMPLETED);
        session.setEndedAt(Instant.now());
        BigDecimal volume = computeTotalVolume(sessionId);
        session.setTotalVolume(volume);
        return sessionRepository.save(session);
    }

    @Override
    public BigDecimal computeTotalVolume(Long sessionId) {
        WorkoutSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));
        BigDecimal total = BigDecimal.ZERO;
        List<WorkoutSessionExercise> exercises = session.getExercises();
        if (exercises != null) {
            for (WorkoutSessionExercise ex : exercises) {
                if (ex.getSets() != null) {
                    for (WorkoutSessionSet set : ex.getSets()) {
                        if (set.getWeight() != null && set.getReps() != null) {
                            BigDecimal setVolume = set.getWeight()
                                    .multiply(BigDecimal.valueOf(set.getReps()));
                            total = total.add(setVolume);
                        }
                    }
                }
            }
        }
        return total;
    }

    @Override
    public Optional<WorkoutSession> findById(Long sessionId) {
        return sessionRepository.findById(sessionId);
    }

    @Override
    public WorkoutSessionSet addSet(Long sessionExerciseId, Integer reps, BigDecimal weight, BigDecimal rpe) {
        WorkoutSessionExercise ex = exerciseRepository.findById(sessionExerciseId)
                .orElseThrow(() -> new IllegalArgumentException("Exercise not found: " + sessionExerciseId));
        int nextIndex = ex.getSets() != null ? ex.getSets().size() : 0;
        WorkoutSessionSet set = new WorkoutSessionSet();
        set.setSessionExercise(ex);
        set.setSetIndex(nextIndex);
        set.setReps(reps);
        set.setWeight(weight);
        set.setRpe(rpe);
        return setRepository.save(set);
    }

    @Override
    public WorkoutSessionSet updateSet(Long setId, Integer reps, BigDecimal weight, BigDecimal rpe) {
        WorkoutSessionSet set = setRepository.findById(setId)
                .orElseThrow(() -> new IllegalArgumentException("Set not found: " + setId));
        if (reps != null) set.setReps(reps);
        if (weight != null) set.setWeight(weight);
        if (rpe != null) set.setRpe(rpe);
        return setRepository.save(set);
    }

    @Override
    public WorkoutSessionSet completeSet(Long setId) {
        WorkoutSessionSet set = setRepository.findById(setId)
                .orElseThrow(() -> new IllegalArgumentException("Set not found: " + setId));
        set.setCompletedAt(Instant.now());
        return setRepository.save(set);
    }

    @Override
    public WorkoutSessionExercise addExercise(Long sessionId, Long exerciseId, Long customExerciseId, String notes) {
        WorkoutSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));
        int nextIndex = session.getExercises() != null ? session.getExercises().size() : 0;
        WorkoutSessionExercise ex = new WorkoutSessionExercise();
        ex.setSession(session);
        ex.setExerciseId(exerciseId);
        ex.setCustomExerciseId(customExerciseId);
        ex.setOrderIndex(nextIndex);
        ex.setMode(ExerciseMode.NORMAL);
        ex.setNotes(notes);
        return exerciseRepository.save(ex);
    }

    @Override
    public WorkoutSessionExercise updateExerciseMode(Long sessionExerciseId, ExerciseMode mode, String groupKey) {
        WorkoutSessionExercise ex = exerciseRepository.findById(sessionExerciseId)
                .orElseThrow(() -> new IllegalArgumentException("Exercise not found: " + sessionExerciseId));
        if (mode != null) ex.setMode(mode);
        ex.setGroupKey(groupKey);
        return exerciseRepository.save(ex);
    }

    @Override
    public List<WorkoutSessionExercise> reorderExercises(Long sessionId, List<Long> orderedExerciseIds) {
        WorkoutSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));
        List<WorkoutSessionExercise> exercises = session.getExercises();
        Map<Long, WorkoutSessionExercise> byId = new HashMap<>();
        for (WorkoutSessionExercise ex : exercises) {
            byId.put(ex.getId(), ex);
        }
        for (int i = 0; i < orderedExerciseIds.size(); i++) {
            WorkoutSessionExercise ex = byId.get(orderedExerciseIds.get(i));
            if (ex != null) {
                ex.setOrderIndex(i);
            }
        }
        exercises.forEach(exerciseRepository::save);
        return exerciseRepository.findBySessionOrderByOrderIndexAsc(session);
    }

    @Override
    public void deleteSet(Long setId) {
        setRepository.deleteById(setId);
    }
}
