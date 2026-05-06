package uk.ac.cf._5.group14.One_To_One.ExerciseLog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uk.ac.cf._5.group14.One_To_One.Profile.AverageConfidenceTrackerDto;
import uk.ac.cf._5.group14.One_To_One.Profile.AverageMoodTrackerDto;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface ExerciseLogRepository extends JpaRepository<ExerciseLog, Long> {

    @Query("select avg(el.moodAfter) - avg(el.moodBefore) as moodDifference, " +
            "max(el.date) as dateRated from ExerciseLog el " +
            "where el.user = :user " +
            "and el.date >= :dateBefore " +
            "group by month(el.date), year(el.date) " +
            "order by el.date asc ")
    List<AverageMoodTrackerDto> getAverageMoodDifference(@Param("user") User user, @Param("dateBefore") LocalDate dateBefore);

    @Query("select avg(el.confidence) as confidence, " +
            "max(el.date) as dateRated from ExerciseLog el " +
            "where el.user = :user " +
            "and el.date >= :dateBefore " +
            "group by month(el.date), year(el.date) " +
            "order by el.date asc ")
    List<AverageConfidenceTrackerDto> getAverageConfidence(@Param("user") User user, @Param("dateBefore") LocalDate dateBefore);

    List<ExerciseLog> findByUser(User user);
    Optional<ExerciseLog> findByIdAndUser(Long id, User user);

    List<ExerciseLog> findByUserOrderByDateDesc(User user);

    @Query("select el from ExerciseLog el where el.user = :user " +
            "order by el.date desc limit 3")
    List<ExerciseLog> findTop5RecentExerciseLogs(@Param("user") User user);

}
