package uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData;

import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public interface CalendarTaskService {

    void createTask(User user, LocalDate date, LocalTime time, String title, String notes, boolean exercise, boolean completed);

    List<CalendarTask> getTasks(User user, LocalDate date);

    Map<LocalDate, List<CalendarTask>> getTasksGroupedByDate(User user);

    DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    void toggleCompleted(Long taskId, User user);

    CalendarTask getTaskById(Long id);

    void updateTask(Long id, User user, String title, String time, String notes, boolean exercise);

    Map<LocalDate, List<CalendarTask>> getTasksByRange(User user, LocalDate start, LocalDate end);

}
