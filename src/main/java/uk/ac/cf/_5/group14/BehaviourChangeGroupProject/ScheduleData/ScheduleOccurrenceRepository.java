package uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduleOccurrenceRepository extends JpaRepository<ScheduleOccurrence, Long> {

    long countByUserAndDate(User user, LocalDate date);

    boolean existsByUserAndDateAndCompletedFalse(User user, LocalDate date);

    boolean existsByUserAndDateAndExerciseLogIsNull(User user, LocalDate date);

    boolean existsByUserAndDateAndTrainerTemplateEntryId(User user, LocalDate date, Long trainerTemplateEntryId);

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

    @Query("SELECT o.date FROM ScheduleOccurrence o WHERE o.user = :user AND o.date BETWEEN :from AND :to AND o.completed = true")
    List<LocalDate> findCompletedDatesByUserAndDateBetween(User user, LocalDate from, LocalDate to);

    void deleteBySchedule(Schedule schedule);

    void deleteByScheduleIdAndUserId(Long scheduleId, Long userId);

}