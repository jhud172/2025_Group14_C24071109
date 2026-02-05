package uk.ac.cf._5.group14.BehaviourChangeGroupProject.StrengthLog.Service;

import uk.ac.cf._5.group14.BehaviourChangeGroupProject.StrengthLog.WorkoutSchedule;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.util.List;

public interface WorkoutScheduleService {
    List<WorkoutSchedule> findByUserAndDayOfWeek(User user, int dayOfWeek);
}
