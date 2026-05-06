package uk.ac.cf._5.group14.One_To_One.Health.BloodPressure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BloodPressureReadingRepository extends JpaRepository<BloodPressureReading, Long> {

    List<BloodPressureReading> findByUserOrderByReadingDateDescReadingTimeDesc(User user);

    List<BloodPressureReading> findByUserAndReadingDateBetweenOrderByReadingDateDescReadingTimeDesc(
            User user, LocalDate from, LocalDate to);

    List<BloodPressureReading> findTop14ByUserOrderByReadingDateDescReadingTimeDesc(User user);

    Optional<BloodPressureReading> findByUserAndReadingDateAndReadingTimeIsNull(User user, LocalDate date);

    @Query("SELECT r FROM BloodPressureReading r WHERE r.user = :user AND r.readingDate BETWEEN :from AND :to ORDER BY r.readingDate ASC, r.readingTime ASC")
    List<BloodPressureReading> findForRange(@Param("user") User user, @Param("from") LocalDate from, @Param("to") LocalDate to);
}
