package uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Transactional
@Service
public class CalendarTaskServiceImpl implements CalendarTaskService {

    @Autowired
    public CalendarTaskServiceImpl(CalendarTaskRepository repo) {
        this.repo = repo;
    }

    private final CalendarTaskRepository repo;

    @Override
    public CalendarTask getTaskById(Long id) {
        return repo.findById(id).orElse(null);
    }

    @Override
    public void updateTask(Long id, User user, String title, String time, String notes, boolean exercise) {
        CalendarTask task = repo.findById(id).orElse(null);
        if (task == null || !task.getUser().getId().equals(user.getId())) return;
        task.setTitle(title);
        if (time == null || time.isBlank()) {
            task.setTime(null);
        } else {
            task.setTime(LocalTime.parse(time));
        }
        task.setNotes(notes);
        task.setExercise(exercise);
        repo.save(task);
    }

    public void toggleCompleted(Long taskId, User user) {
        CalendarTask task = repo.findById(taskId).orElse(null);
        if (task == null || !task.getUser().getId().equals(user.getId())) return;

        task.setCompleted(!task.getCompleted());
        repo.save(task);
    }

    @Override
    public void createTask(User user, LocalDate date, LocalTime time, String title, String notes, boolean exercise, boolean completed) {
        CalendarTask task = new CalendarTask();
        task.setUser(user);
        task.setDate(date);
        task.setTime(time);
        task.setTitle(title);
        task.setNotes(notes);
        task.setExercise(exercise);
        task.setCompleted(completed);

        repo.save(task);
    }

    @Override
    public List<CalendarTask> getTasks(User user, LocalDate date) {
        return repo.findByUserAndDateOrderByTime(user, date);
    }

    @Override
    public Map<LocalDate, List<CalendarTask>> getTasksGroupedByDate(User user) {
        List<CalendarTask> allTasks = repo.findByUserOrderByDateAscTimeAsc(user);

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

        for (CalendarTask task : tasks) {
            map.computeIfAbsent(task.getDate(), d -> new ArrayList<>()).add(task);
        }

        return map;
    }
}
