package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Chat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.stereotype.Service;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTask;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTaskRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Level.LevelProgressRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Notes.Note;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Notes.NoteRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleOccurrence;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleOccurrenceRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.StrengthLog.Repository.WorkoutSessionRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.StrengthLog.WorkoutSession;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChatContextService {

    private static final Logger log = LoggerFactory.getLogger(ChatContextService.class);

    private final ScheduleOccurrenceRepository scheduleOccurrenceRepository;
    private final CalendarTaskRepository calendarTaskRepository;
    private final WorkoutSessionRepository workoutSessionRepository;
    private final NoteRepository noteRepository;
    private final LevelProgressRepository levelProgressRepository;

    public ChatContextService(
            ScheduleOccurrenceRepository scheduleOccurrenceRepository,
            CalendarTaskRepository calendarTaskRepository,
            WorkoutSessionRepository workoutSessionRepository,
            NoteRepository noteRepository,
            LevelProgressRepository levelProgressRepository
    ) {
        this.scheduleOccurrenceRepository = scheduleOccurrenceRepository;
        this.calendarTaskRepository = calendarTaskRepository;
        this.workoutSessionRepository = workoutSessionRepository;
        this.noteRepository = noteRepository;
        this.levelProgressRepository = levelProgressRepository;
    }

    public ChatContext build(User user, LocalDate requestedDate) {
        LocalDate today = LocalDate.now();
        LocalDate req = requestedDate;

        List<String> todaysTasks = summarizeTasks(calendarTaskRepository.findByUserAndDateOrderByTime(user, today));
        List<String> todaysScheduled = summarizeOccurrences(scheduleOccurrenceRepository.findByUserAndDate(user, today));

        List<String> reqItems = List.of();
        if (req != null && !req.equals(today)) {
            List<String> items = new ArrayList<>();
            items.addAll(summarizeTasks(calendarTaskRepository.findByUserAndDateOrderByTime(user, req)));
            items.addAll(summarizeOccurrences(scheduleOccurrenceRepository.findByUserAndDate(user, req)));
            reqItems = items;
        }

        List<ChatContext.RecentWorkout> recentWorkouts = List.of();
        try {
            List<WorkoutSession> recentSessions = workoutSessionRepository.findTop3ByUserOrderByDateDesc(user);
            recentWorkouts = summarizeWorkouts(recentSessions);
        } catch (InvalidDataAccessResourceUsageException e) {
            log.warn("Workout session context unavailable (likely missing table). Continuing without workout context.", e);
            recentWorkouts = List.of();
        } catch (DataAccessException e) {
            log.warn("Workout session context unavailable due to data access error. Continuing without workout context.", e);
            recentWorkouts = List.of();
        } catch (RuntimeException e) {
            log.warn("Workout session context unavailable due to unexpected error. Continuing without workout context.", e);
            recentWorkouts = List.of();
        }

        List<Note> notes = noteRepository.findByUserOrderByUpdatedAtDesc(user);
        List<String> recentNotes = notes.stream()
                .limit(3)
                .map(n -> n.getTitle() + " (updated " + n.getUpdatedAt().toLocalDate() + ")")
                .toList();

        var lpOpt = levelProgressRepository.findByUser(user);
        Integer points = lpOpt.map(lp -> lp.getPoints()).orElse(null);
        Integer level = lpOpt.map(lp -> lp.getLevel()).orElse(null);

        return new ChatContext(today, req, todaysTasks, todaysScheduled, reqItems, recentWorkouts, recentNotes, points, level);
    }

    private List<String> summarizeTasks(List<CalendarTask> tasks) {
        return tasks.stream()
                .limit(10)
                .map(t -> {
                    String time = t.getTime() != null ? t.getTime().toString() : "";
                    String prefix = time.isBlank() ? "" : (time + " - ");
                    String done = Boolean.TRUE.equals(t.getCompleted()) ? " (done)" : "";
                    return prefix + t.getTitle() + done;
                })
                .toList();
    }

    private List<String> summarizeOccurrences(List<ScheduleOccurrence> occs) {
        return occs.stream()
                .limit(10)
                .map(o -> {
                    String exercise = o.getExercise() != null ? o.getExercise().getName() : null;
                    if (exercise == null && o.getCustomExercise() != null) {
                        exercise = o.getCustomExercise().getName();
                    }
                    if (exercise == null) exercise = "(exercise)";
                    String done = o.isCompleted() ? " (done)" : "";
                    String schedule = o.getScheduleName() != null ? o.getScheduleName() : "Schedule";
                    return schedule + ": " + exercise + done;
                })
                .toList();
    }

    private List<ChatContext.RecentWorkout> summarizeWorkouts(List<WorkoutSession> sessions) {
        return sessions.stream()
                .limit(3)
                .map(s -> {
                    int totalSets = 0;
                    if (s.getExerciseSessions() != null) {
                        for (var es : s.getExerciseSessions()) {
                            if (es.getSetLogs() != null) {
                                totalSets += es.getSetLogs().size();
                            }
                        }
                    }
                    String name = s.getNameSnapshot() != null && !s.getNameSnapshot().isBlank()
                            ? s.getNameSnapshot()
                            : (s.getWorkout() != null ? s.getWorkout().getName() : "Workout");
                    return new ChatContext.RecentWorkout(s.getDate(), name, s.isCompleted(), totalSets);
                })
                .toList();
    }
}
