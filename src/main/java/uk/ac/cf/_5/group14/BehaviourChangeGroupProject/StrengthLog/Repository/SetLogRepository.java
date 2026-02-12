package uk.ac.cf._5.group14.BehaviourChangeGroupProject.StrengthLog.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.StrengthLog.SetLog;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.StrengthLog.ExerciseSession;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.LocalDate;
import java.util.List;

public interface SetLogRepository extends JpaRepository<SetLog, Long> {
    List<SetLog> findByExerciseSessionOrderBySetNumberAsc(ExerciseSession session);

    @Query("""
        SELECT ws.date AS date, COUNT(sl) AS total
        FROM SetLog sl
        JOIN sl.exerciseSession es
        JOIN es.workoutSession ws
        WHERE ws.user = :user
        AND ws.date BETWEEN :start AND :end
        AND sl.completed = true
        GROUP BY ws.date
    """)
    List<SetLogDailyTotal> countCompletedSetsByUserAndDateRange(
            @Param("user") User user,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    interface SetLogDailyTotal {
        LocalDate getDate();
        long getTotal();
    }
}
