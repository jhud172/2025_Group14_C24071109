package uk.ac.cf._5.group14.One_To_One.StrengthLog.Service;

import uk.ac.cf._5.group14.One_To_One.StrengthLog.WorkoutSchedule;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.util.List;

public interface WorkoutScheduleService {
    List<WorkoutSchedule> findByUserAndDayOfWeek(User user, int dayOfWeek);
}
