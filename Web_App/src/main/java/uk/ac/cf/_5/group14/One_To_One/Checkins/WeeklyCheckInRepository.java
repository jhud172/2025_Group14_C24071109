package uk.ac.cf._5.group14.One_To_One.Checkins;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WeeklyCheckInRepository extends JpaRepository<WeeklyCheckIn, Long> {

    List<WeeklyCheckIn> findByTrainerIdOrderBySubmittedAtDesc(Long trainerId);

    List<WeeklyCheckIn> findByClientIdOrderBySubmittedAtDesc(Long clientId);

    Optional<WeeklyCheckIn> findByTrainerIdAndClientIdAndWeekStartDate(Long trainerId, Long clientId, LocalDate weekStartDate);
}
