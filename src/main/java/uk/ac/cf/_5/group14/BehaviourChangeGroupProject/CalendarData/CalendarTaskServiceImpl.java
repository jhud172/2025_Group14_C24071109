package uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Transactional
@Service
public class CalendarTaskServiceImpl implements CalendarTaskService {

    @Autowired
    public CalendarTaskServiceImpl(CalendarTaskRepository repo, CalendarTaskWarningService warningService) {
        this.repo = repo;
        this.warningService = warningService;
    }

    private final CalendarTaskRepository repo;
    private final CalendarTaskWarningService warningService;

    @Override
    public CalendarTask getTaskById(Long id) {
        CalendarTask task = repo.findById(id).orElse(null);
        if (task != null) {
            warningService.applyWarningStates(List.of(task), Instant.now());
        }
        return task;
    }

    @Override
    public void updateTask(Long id, User user, String title, String time, String notes, boolean exercise) {
        updateTask(id, user, title, time, notes, exercise, null);
    }

    @Override
    public void updateTask(Long id, User user, String title, String time, String notes, boolean exercise, ActivityType activityType) {
        CalendarTask task = repo.findById(id).orElse(null);
        if (task == null || !task.getUser().getId().equals(user.getId())) return;
        task.setTitle(title);
        if (time == null || time.isBlank()) {
            task.setTime(null);
        } else {
            task.setTime(LocalTime.parse(time));
        }
        task.setNotes(notes);
        task.setExercise(exercise || activityType != null);
        task.setActivityType(activityType);
        repo.save(task);
    }

    @Override
    public void updateGracePeriodMinutes(Long id, User user, Integer gracePeriodMinutes) {
        CalendarTask task = repo.findById(id).orElse(null);
        if (task == null || task.getUser() == null || task.getUser().getId() == null) return;
        if (user == null || user.getId() == null || !task.getUser().getId().equals(user.getId())) return;

        if (gracePeriodMinutes == null || gracePeriodMinutes <= 0) {
            task.setGracePeriodMinutes(null);
        } else {
            task.setGracePeriodMinutes(gracePeriodMinutes);
        }
        repo.save(task);
    }

    @Override
    public void deleteTask(Long id, User user) {
        CalendarTask task = repo.findById(id).orElse(null);
        if (task == null || task.getUser() == null || task.getUser().getId() == null) return;
        if (user == null || user.getId() == null || !task.getUser().getId().equals(user.getId())) return;

        warningService.deleteWarningsReferencingTask(id);
        repo.delete(task);
    }

    public void toggleCompleted(Long taskId, User user) {
        CalendarTask task = repo.findById(taskId).orElse(null);
        if (task == null || !task.getUser().getId().equals(user.getId())) return;

        task.setCompleted(!task.getCompleted());
        repo.save(task);

        if (Boolean.TRUE.equals(task.getCompleted())) {
            warningService.onTaskCompleted(task, Instant.now());
        }
    }

    @Override
    public void createTask(User user, LocalDate date, LocalTime time, String title, String notes, boolean exercise, boolean completed) {
        createTask(user, date, time, title, notes, exercise, completed, null);
    }

    @Override
    public void createTask(User user, LocalDate date, LocalTime time, String title, String notes, boolean exercise, boolean completed, ActivityType activityType) {
        CalendarTask task = new CalendarTask();
        task.setUser(user);
        task.setDate(date);
        task.setTime(time);
        task.setTitle(title);
        task.setNotes(notes);
        task.setExercise(exercise || activityType != null);
        task.setActivityType(activityType);
        task.setCompleted(completed);

        repo.save(task);
    }

    @Override
    public List<CalendarTask> getTasks(User user, LocalDate date) {
        List<CalendarTask> tasks = repo.findByUserAndDateOrderByTime(user, date);
        warningService.applyWarningStates(tasks, Instant.now());
        return tasks;
    }

    @Override
    public Map<LocalDate, List<CalendarTask>> getTasksGroupedByDate(User user) {
        List<CalendarTask> allTasks = repo.findByUserOrderByDateAscTimeAsc(user);

        warningService.applyWarningStates(allTasks, Instant.now());

        Map<LocalDate, List<CalendarTask>> map = new HashMap<>();

        for (CalendarTask task : allTasks) {
            map.computeIfAbsent(task.getDate(), d -> new ArrayList<>()).add(task);
        }

        return map;
    }

    @Override
    public Map<LocalDate, List<CalendarTask>> getTasksByRange(User user, LocalDate start, LocalDate end) {
        Map<LocalDate, List<CalendarTask>> map = new HashMap<>();

        List<CalendarTask> tasks = repo.findByUserAndDateBetween(user, start, end);
        System.out.println("DEBUG: CalendarTaskServiceImpl.getTasksByRange - user: " + user.getId() + ", start: " + start + ", end: " + end + ", foundTasks: " + tasks.size());

        warningService.applyWarningStates(tasks, Instant.now());

        for (CalendarTask task : tasks) {
            map.computeIfAbsent(task.getDate(), d -> new ArrayList<>()).add(task);
        }

        System.out.println("DEBUG: CalendarTaskServiceImpl.getTasksByRange - final map size: " + map.size());
        return map;
    }
}
