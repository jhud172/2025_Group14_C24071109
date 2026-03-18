package uk.ac.cf._5.group14.One_To_One.StrengthLog.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.cf._5.group14.One_To_One.StrengthLog.WorkoutSchedule;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.util.List;

public interface WorkoutScheduleRepository extends JpaRepository<WorkoutSchedule, Long> {
    List<WorkoutSchedule> findByUserAndDayOfWeekOrderByOrderIndexAsc(User user, int dayOfWeek);
}
