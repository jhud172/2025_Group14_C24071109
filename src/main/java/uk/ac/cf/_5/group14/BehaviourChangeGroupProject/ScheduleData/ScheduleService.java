package uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData;

import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.util.List;

public interface ScheduleService {

    Schedule save(Schedule schedule);

    Schedule findById(Long id);

    List<Schedule> findByUser(User user);
}
