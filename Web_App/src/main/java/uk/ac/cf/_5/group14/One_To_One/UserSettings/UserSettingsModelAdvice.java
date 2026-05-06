package uk.ac.cf._5.group14.One_To_One.UserSettings;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.ac.cf._5.group14.One_To_One.Level.LevelProgress;
import uk.ac.cf._5.group14.One_To_One.Level.LevelService;
import uk.ac.cf._5.group14.One_To_One.Users.AuthHelper;
import uk.ac.cf._5.group14.One_To_One.Users.User;

@ControllerAdvice
public class UserSettingsModelAdvice {

    private final AuthHelper authHelper;
    private final UserSettingsService userSettingsService;
    private final ObjectProvider<LevelService> levelServiceProvider;

    public UserSettingsModelAdvice(AuthHelper authHelper,
                                   UserSettingsService userSettingsService,
                                   ObjectProvider<LevelService> levelServiceProvider) {
        this.authHelper = authHelper;
        this.userSettingsService = userSettingsService;
        this.levelServiceProvider = levelServiceProvider;
    }

    @ModelAttribute("userSettings")
    public UserSettings userSettings() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (!(attributes instanceof ServletRequestAttributes servletAttributes)) {
            return null;
        }

        if (UserSettingsRequestSupport.shouldSkip(servletAttributes.getRequest())) {
            return null;
        }

        UserSettings cached = UserSettingsRequestCache.getFromCurrentRequest();
        if (cached != null) {
            return cached;
        }

        User user = authHelper.getAuthenticatedUser();
        if (user == null) {
            return null;
        }
        UserSettings settings = userSettingsService.getOrCreate(user);
        UserSettingsRequestCache.setOnCurrentRequest(settings);
        return settings;
    }

    @ModelAttribute("navLevelProgress")
    public LevelProgress navLevelProgress() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (!(attributes instanceof ServletRequestAttributes servletAttributes)) {
            return null;
        }

        if (UserSettingsRequestSupport.shouldSkip(servletAttributes.getRequest())) {
            return null;
        }

        User user = authHelper.getAuthenticatedUser();
        if (user == null) {
            return null;
        }

        LevelService levelService = levelServiceProvider.getIfAvailable();
        if (levelService == null) {
            return null;
        }

        return levelService.getProgress(user);
    }
}
