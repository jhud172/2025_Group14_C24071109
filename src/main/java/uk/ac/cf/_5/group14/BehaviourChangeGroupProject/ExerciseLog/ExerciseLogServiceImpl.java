package uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseLog;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTask;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTaskRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleOccurrence;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleOccurrenceRepository;

import java.util.List;

@Service
public class ExerciseLogServiceImpl implements ExerciseLogService {
    @Autowired
    private ScheduleOccurrenceRepository occurrenceRepo;

    @Autowired
    private CalendarTaskRepository calendarTaskRepo;

    private final ExerciseLogRepository repo;

    public ExerciseLogServiceImpl(ExerciseLogRepository repo, ScheduleOccurrenceRepository occurrenceRepo, CalendarTaskRepository calendarTaskRepo) {
        this.repo = repo;
        this.occurrenceRepo = occurrenceRepo;
        this.calendarTaskRepo = calendarTaskRepo;
    }

    @Override
    public void saveLog(ExerciseLogForm form, User user) {
        ExerciseLog log = new ExerciseLog();
        log.setUser(user);
        log.setDate(form.getDate());
        log.setUser(user);
        log.setMoodBefore(form.getMoodBefore());
        log.setMoodAfter(form.getMoodAfter());
        log.setConfidence(form.getConfidence());
        log.setComments(form.getComments());
        log.setDurationMinutes(form.getDurationMinutes());
        ExerciseLog saved = repo.save(log);
        if (form.getOccurrenceId() != null) {
            ScheduleOccurrence occ = occurrenceRepo.findById(form.getOccurrenceId()).orElse(null);
            if (occ != null) {
                occ.setExerciseLog(saved);
                occurrenceRepo.save(occ);
            }
        }
        if (form.getCalendarTaskId() != null) {
            CalendarTask task = calendarTaskRepo.findById(form.getCalendarTaskId()).orElse(null);
            if (task != null) {
                task.setExerciseLog(saved);
                task.setCompleted(true);
                calendarTaskRepo.save(task);
            }
        }
    }

    @Override
    public List<ExerciseLog> getAllLogs() {
        return new java.util.ArrayList<>(repo.findAll());
    }

    @Override
    public ExerciseLog getLogById(Long id) {
        return repo.findById(id).orElse(null);
    }

    public List<ExerciseLog> getLogsForUser(User user) {
        return repo.findByUser(user);
    }

    public List<ExerciseLog> getLogsByUser(User user) {
        return repo.findByUserOrderByDateDesc(user);
    }


    public List<ExerciseLog> findTop5RecentExerciseLogs(User user) {
        return repo.findTop5RecentExerciseLogs(user);
    }
}
