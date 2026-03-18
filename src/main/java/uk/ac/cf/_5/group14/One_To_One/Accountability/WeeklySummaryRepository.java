package uk.ac.cf._5.group14.One_To_One.Accountability;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface WeeklySummaryRepository extends JpaRepository<WeeklySummary, Long> {

    Optional<WeeklySummary> findByUserIdAndWeekStart(Long userId, LocalDate weekStart);
}
