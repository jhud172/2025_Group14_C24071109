package uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.util.List;

public interface ScheduleAppliedRepository extends JpaRepository<ScheduleApplied, Long> {

    List<ScheduleApplied> findByUser(User user);

    List<ScheduleApplied> findByUserAndSchedule(User user, Schedule schedule);

    void deleteBySchedule(Schedule schedule);

}
