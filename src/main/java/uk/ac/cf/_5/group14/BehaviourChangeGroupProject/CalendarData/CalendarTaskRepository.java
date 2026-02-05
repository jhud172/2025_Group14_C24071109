package uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CalendarTaskRepository extends CrudRepository<CalendarTask, Long> {

    List<CalendarTask> findByUserAndDateOrderByTime(User user, LocalDate date);

    long countByUserAndDate(User user, LocalDate date);

    boolean existsByUserAndDateAndRequiresLogTrueAndCompletedFalse(User user, LocalDate date);

    boolean existsByUserAndDateAndRequiresLogTrueAndExerciseLogIsNull(User user, LocalDate date);

    List<CalendarTask> findByUserOrderByDateAscTimeAsc(User user);

    CalendarTask findByIdAndUser(Long id, User user);

    List<CalendarTask> findByUserAndDateBetween(User user, LocalDate start, LocalDate end);

    Optional<CalendarTask> findFirstByUserAndDateAndCompletedFalseOrderByTimeAsc(User user, LocalDate date);

    long countByUserAndDateBeforeAndCompletedFalse(User user, LocalDate date);

    long countByUserAndDateAndCompletedTrue(User user, LocalDate date);

    @Query("""
        SELECT t FROM CalendarTask t
        JOIN FETCH t.user
        WHERE t.date = :date
        AND t.time IS NOT NULL
        AND t.time >= :fromTime
        AND t.time <= :toTime
        AND t.completed = false
    """)
    List<CalendarTask> findUpcomingTasks(LocalDate date, LocalTime fromTime, LocalTime toTime);
}
