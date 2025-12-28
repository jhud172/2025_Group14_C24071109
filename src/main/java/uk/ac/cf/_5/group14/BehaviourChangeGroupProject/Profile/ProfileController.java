package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Profile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ConditionsPreferences.Preference.Preference;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ConditionsPreferences.UserPreference.UserPreference;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ConditionsPreferences.UserPreference.UserPreferenceService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseLog.ExerciseLog;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseLog.ExerciseLogService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.HealthDataInput.HealthRecord;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.HealthDataInput.HealthRecordService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserService;

import java.util.List;
import java.util.Map;

@Controller
public class ProfileController {

    private final ProfileService profileService;
    private final UserService userService;
    private final HealthRecordService healthRecordService;
    private final UserPreferenceService userPreferenceService;
    private final ExerciseLogService exerciseLogService;

    @Autowired
    private AuthHelper authHelper;


    public ProfileController(ProfileService profileService, UserService userService, HealthRecordService healthRecordService, UserPreferenceService userPreferenceService, ExerciseLogService exerciseLogService) {
        this.profileService = profileService;
        this.userService = userService;
        this.healthRecordService = healthRecordService;
        this.userPreferenceService = userPreferenceService;
        this.exerciseLogService = exerciseLogService;
    }

    @GetMapping("/profile")
    public ModelAndView getProfile() {
        ModelAndView modelAndView = new ModelAndView("/profile/profile");
        User user = authHelper.getAuthenticatedUser();
        ChartsDataDto charts = profileService.getChartData(user);
        HealthRecord healthRecord = healthRecordService.getMostRecentHealthRecord(user);
        Map<String, List<Preference>> userPreferenceList = userPreferenceService.getUserPreferencesByCategory(user);
        List<ExerciseLog> recentExerciseLogs = exerciseLogService.findTop5RecentExerciseLogs(user);

        modelAndView.addObject("charts", charts);
        modelAndView.addObject("healthRecord", healthRecord);
        modelAndView.addObject("userPreferenceList", userPreferenceList);
        modelAndView.addObject("recentExerciseLogs", recentExerciseLogs);


        return modelAndView;
    }
}
