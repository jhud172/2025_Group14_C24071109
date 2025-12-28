package uk.ac.cf._5.group14.BehaviourChangeGroupProject.HomePage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseLog.ExerciseLog;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseLog.ExerciseLogService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.Schedule;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleApplied;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleAppliedRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.util.List;

@Controller
public class HomePageController {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private ScheduleAppliedRepository scheduleAppliedRepository;

    private final ExerciseLogService exerciseLogService;

    @Autowired
    private AuthHelper authHelper;

    public HomePageController(ExerciseLogService exerciseLogService) {
        this.exerciseLogService = exerciseLogService;
    }

    @GetMapping("/")
    public ModelAndView homePage() {
        ModelAndView modelAndView = new ModelAndView("index");

        User user = authHelper.getAuthenticatedUser();
//        List<Schedule> all = scheduleService.findByUser(user);
//        List<ScheduleApplied> active = scheduleAppliedRepository.findByUser(user);


        List<ExerciseLog> recentExerciseLogs = exerciseLogService.findTop5RecentExerciseLogs(user);
//        List<ScheduleApplied> activeSchedules = scheduleAppliedRepository.findByUser(user);

        modelAndView.addObject("recentExerciseLogs", recentExerciseLogs);
//        modelAndView.addObject("activeSchedules", activeSchedules);

        return modelAndView;
    }

}
