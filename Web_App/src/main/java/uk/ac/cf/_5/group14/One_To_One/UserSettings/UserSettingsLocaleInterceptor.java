package uk.ac.cf._5.group14.One_To_One.UserSettings;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.LocaleResolver;
import uk.ac.cf._5.group14.One_To_One.Config.SupportedLanguage;
import uk.ac.cf._5.group14.One_To_One.Users.AuthHelper;
import uk.ac.cf._5.group14.One_To_One.Users.User;

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
        if (UserSettingsRequestSupport.shouldSkip(request)) {
            return true;
        }

        User user = authHelper.getAuthenticatedUser();
        String requestedLanguage = requestedLanguage(request);
        UserSettings settings = null;

        if (user != null) {
            settings = UserSettingsRequestCache.get(request);
            if (settings == null) {
                settings = userSettingsService.getOrCreate(user);
                UserSettingsRequestCache.set(request, settings);
            }
        }

        if (requestedLanguage != null) {
            if (user != null && settings != null && !requestedLanguage.equals(normalizeLanguage(settings.getLanguage()))) {
                settings = userSettingsService.update(
                        user,
                        requestedLanguage,
                        settings.getTheme(),
                        settings.isEasyMode()
                );
                UserSettingsRequestCache.set(request, settings);
            }
            localeResolver.setLocale(request, response, SupportedLanguage.fromCode(requestedLanguage).locale());
            return true;
        }

        String language = settings != null
                ? normalizeLanguage(settings.getLanguage())
                : normalizeLanguage(localeResolver.resolveLocale(request).getLanguage());

        localeResolver.setLocale(request, response, SupportedLanguage.fromCode(language).locale());
        return true;
    }

    private static String requestedLanguage(HttpServletRequest request) {
        if (!request.getParameterMap().containsKey("lang")) {
            return null;
        }
        return normalizeLanguage(request.getParameter("lang"));
    }

    private static String normalizeLanguage(String language) {
        return SupportedLanguage.normalizeCode(language);
    }
}
