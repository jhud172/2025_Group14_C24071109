package uk.ac.cf._5.group14.One_To_One.Chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DailyUsageRepository extends JpaRepository<DailyUsage, DailyUsageId> {

    Optional<DailyUsage> findByUserIdAndDate(Long userId, LocalDate date);
}
