package uk.ac.cf._5.group14.BehaviourChangeGroupProject.FocusData;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.LocalDate;
import java.util.List;

public interface DailyFocusRepository extends JpaRepository<DailyFocus, DailyFocusKey> {

	List<DailyFocus> findByUserAndDateBetween(User user, LocalDate start, LocalDate end);
}
