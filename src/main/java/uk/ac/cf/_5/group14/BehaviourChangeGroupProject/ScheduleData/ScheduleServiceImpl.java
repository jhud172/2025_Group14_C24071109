package uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.util.ArrayList;
import java.util.List;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Override
    public Schedule save(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    @Override
    public Schedule findById(Long id) {
        return scheduleRepository.findById(id).orElse(null);
    }

    @Override
    public List<Schedule> findByUser(User user) {
        List<Schedule> list = new ArrayList<>();
        scheduleRepository.findByUser(user).forEach(list::add);
        return list;
    }
}
