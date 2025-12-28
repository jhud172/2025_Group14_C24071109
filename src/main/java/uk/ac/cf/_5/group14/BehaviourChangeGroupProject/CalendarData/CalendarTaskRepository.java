package uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CalendarTaskRepository extends CrudRepository<CalendarTask, Long> {

    List<CalendarTask> findByUserAndDateOrderByTime(User user, LocalDate date);

    List<CalendarTask> findByUserOrderByDateAscTimeAsc(User user);

    CalendarTask findByIdAndUser(Long id, User user);

    List<CalendarTask> findByUserAndDateBetween(User user, LocalDate start, LocalDate end);
}
