package uk.ac.cf._5.group14.BehaviourChangeGroupProject.UserSettings;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

@ControllerAdvice
public class UserSettingsModelAdvice {

    private final AuthHelper authHelper;
    private final UserSettingsService userSettingsService;

    public UserSettingsModelAdvice(AuthHelper authHelper, UserSettingsService userSettingsService) {
        this.authHelper = authHelper;
        this.userSettingsService = userSettingsService;
    }

    @ModelAttribute("userSettings")
    public UserSettings userSettings() {
        User user = authHelper.getAuthenticatedUser();
        if (user == null) {
            return null;
        }
        return userSettingsService.getOrCreate(user);
    }
}
