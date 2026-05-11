package uk.ac.cf._5.group14.BehaviourChangeGroupProject.UserSettings;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.LocaleResolver;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.util.Locale;

public class UserSettingsLocaleInterceptor implements HandlerInterceptor {

    private final AuthHelper authHelper;
    private final UserSettingsService userSettingsService;
    private final LocaleResolver localeResolver;

    public UserSettingsLocaleInterceptor(AuthHelper authHelper,
                                         UserSettingsService userSettingsService,
                                         LocaleResolver localeResolver) {
        this.authHelper = authHelper;
        this.userSettingsService = userSettingsService;
        this.localeResolver = localeResolver;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        User user = authHelper.getAuthenticatedUser();
        String language = "en";
        if (user != null) {
            UserSettings settings = userSettingsService.getOrCreate(user);
            if (settings != null && settings.getLanguage() != null && !settings.getLanguage().isBlank()) {
                language = settings.getLanguage();
            }
        }
        localeResolver.setLocale(request, response, Locale.forLanguageTag(language));
        return true;
    }
}
