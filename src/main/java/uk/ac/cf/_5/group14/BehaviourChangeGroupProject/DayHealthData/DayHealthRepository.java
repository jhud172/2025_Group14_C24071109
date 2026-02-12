package uk.ac.cf._5.group14.BehaviourChangeGroupProject.DayHealthData;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.util.Optional;

public interface DayHealthRepository extends JpaRepository<DayHealth, DayHealthKey> {
	Optional<DayHealth> findTopByUserOrderByDateDesc(User user);
}
