package uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DayOptimisationRepository extends JpaRepository<DayOptimisation, DayOptimisationKey> {

    Optional<DayOptimisation> findByUserIdAndDate(Long userId, LocalDate date);
}
