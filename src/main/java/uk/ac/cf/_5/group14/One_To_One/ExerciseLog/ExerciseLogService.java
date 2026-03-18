package uk.ac.cf._5.group14.One_To_One.ExerciseLog;

import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.util.List;

public interface ExerciseLogService {
    void saveLog(ExerciseLogForm form, User user);
    void updateLog(Long id, ExerciseLogForm form, User user);
    List<ExerciseLog> getAllLogs();
    ExerciseLog getLogById(Long id);
    ExerciseLog getLogByIdForUser(Long id, User user);
    List<ExerciseLog> findTop5RecentExerciseLogs(User user);
    List<ExerciseLog> getLogsForUser(User user);
    List<ExerciseLog> getLogsByUser(User user);
}
