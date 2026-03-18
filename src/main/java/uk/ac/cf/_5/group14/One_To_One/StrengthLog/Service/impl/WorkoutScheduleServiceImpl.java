package uk.ac.cf._5.group14.One_To_One.StrengthLog.Service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.cf._5.group14.One_To_One.StrengthLog.Repository.WorkoutScheduleRepository;
import uk.ac.cf._5.group14.One_To_One.StrengthLog.WorkoutSchedule;
import uk.ac.cf._5.group14.One_To_One.StrengthLog.Service.WorkoutScheduleService;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.util.List;

@Service
public class WorkoutScheduleServiceImpl implements WorkoutScheduleService {

    @Autowired
    private WorkoutScheduleRepository repository;

    @Override
    public List<WorkoutSchedule> findByUserAndDayOfWeek(User user, int dayOfWeek) {
        return repository.findByUserAndDayOfWeekOrderByOrderIndexAsc(user, dayOfWeek);
    }
}
