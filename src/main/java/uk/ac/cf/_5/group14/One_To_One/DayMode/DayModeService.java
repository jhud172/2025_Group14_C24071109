package uk.ac.cf._5.group14.One_To_One.DayMode;

import org.springframework.stereotype.Service;
import uk.ac.cf._5.group14.One_To_One.StrengthLog.Service.WorkoutScheduleService;
import uk.ac.cf._5.group14.One_To_One.StrengthLog.Service.WorkoutSessionService;
import uk.ac.cf._5.group14.One_To_One.StrengthLog.WorkoutSession;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.time.LocalDate;
import java.util.List;

@Service
public class DayModeService {

    private final WorkoutScheduleService workoutScheduleService;
    private final WorkoutSessionService workoutSessionService;

    public DayModeService(WorkoutScheduleService workoutScheduleService,
                          WorkoutSessionService workoutSessionService) {
        this.workoutScheduleService = workoutScheduleService;
        this.workoutSessionService = workoutSessionService;
    }

    public DayMode determine(User user, LocalDate date) {
        if (user == null || date == null) {
            return DayMode.REST_DAY;
        }

        int dayOfWeek = date.getDayOfWeek().getValue();
        boolean hasSchedule = !workoutScheduleService.findByUserAndDayOfWeek(user, dayOfWeek).isEmpty();

        List<WorkoutSession> sessions = workoutSessionService.findByUserAndDate(user, date);
        boolean hasCompleted = sessions != null && sessions.stream().anyMatch(WorkoutSession::isCompleted);

        return (hasSchedule || hasCompleted) ? DayMode.TRAINING_DAY : DayMode.REST_DAY;
    }
}
