package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Accountability;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface WeeklySummaryRepository extends JpaRepository<WeeklySummary, Long> {

    Optional<WeeklySummary> findByUserIdAndWeekStart(Long userId, LocalDate weekStart);
}
