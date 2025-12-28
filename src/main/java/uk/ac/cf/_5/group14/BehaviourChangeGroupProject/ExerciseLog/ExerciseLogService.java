package uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseLog;

import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.util.List;

public interface ExerciseLogService {
    void saveLog(ExerciseLogForm form, User user);
    List<ExerciseLog> getAllLogs();
    ExerciseLog getLogById(Long id);
    List<ExerciseLog> findTop5RecentExerciseLogs(User user);
    List<ExerciseLog> getLogsForUser(User user);
    List<ExerciseLog> getLogsByUser(User user);
}
