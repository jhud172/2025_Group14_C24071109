package uk.ac.cf._5.group14.One_To_One.CalendarData;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.time.LocalDate;
import java.util.List;

public interface DailyCompletionRepository extends JpaRepository<DailyCompletion, DailyCompletionKey> {

	List<DailyCompletion> findByUserAndDateBetween(User user, LocalDate start, LocalDate end);
}
