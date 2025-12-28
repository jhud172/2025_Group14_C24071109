package uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduleOccurrenceRepository extends JpaRepository<ScheduleOccurrence, Long> {

    @Query("""
        SELECT o FROM ScheduleOccurrence o
        LEFT JOIN FETCH o.exercise
        LEFT JOIN FETCH o.customExercise
        JOIN FETCH o.user
        WHERE o.user = :user
        AND o.date = :date
    """)
    List<ScheduleOccurrence> findByUserAndDate(User user, LocalDate date);

    @Query("""
        SELECT o FROM ScheduleOccurrence o
        LEFT JOIN FETCH o.exercise
        LEFT JOIN FETCH o.customExercise
        JOIN FETCH o.user
        WHERE o.user = :user
        AND o.date BETWEEN :from AND :to
        ORDER BY o.date
    """)
    List<ScheduleOccurrence> findByUserAndDateBetween(User user, LocalDate from, LocalDate to);

    @Query("SELECT o FROM ScheduleOccurrence o WHERE o.user = :user AND o.date >= CURRENT_DATE")
    List<ScheduleOccurrence> findActiveByUser(User user);

    void deleteBySchedule(Schedule schedule);

    void deleteByScheduleIdAndUserId(Long scheduleId, Long userId);

}