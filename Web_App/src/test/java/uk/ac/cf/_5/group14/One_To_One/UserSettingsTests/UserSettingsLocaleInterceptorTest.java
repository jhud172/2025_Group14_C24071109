package uk.ac.cf._5.group14.One_To_One.UserSettingsTests;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import uk.ac.cf._5.group14.One_To_One.UserSettings.ThemePreference;
import uk.ac.cf._5.group14.One_To_One.UserSettings.UserSettings;
import uk.ac.cf._5.group14.One_To_One.UserSettings.UserSettingsLocaleInterceptor;
import uk.ac.cf._5.group14.One_To_One.UserSettings.UserSettingsService;
import uk.ac.cf._5.group14.One_To_One.Users.AuthHelper;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserSettingsLocaleInterceptorTest {

    @Test
    void guestLanguageSelectionPersistsInTheSession() {
        AuthHelper authHelper = mock(AuthHelper.class);
        UserSettingsService settingsService = mock(UserSettingsService.class);
        SessionLocaleResolver resolver = new SessionLocaleResolver();
        resolver.setDefaultLocale(Locale.ENGLISH);
        UserSettingsLocaleInterceptor interceptor =
                new UserSettingsLocaleInterceptor(authHelper, settingsService, resolver);
        MockHttpSession session = new MockHttpSession();

        MockHttpServletRequest selection = new MockHttpServletRequest();
        selection.setSession(session);
        selection.setRequestURI("/");
        selection.setParameter("lang", "cy");
        interceptor.preHandle(selection, new MockHttpServletResponse(), new Object());

        MockHttpServletRequest nextRequest = new MockHttpServletRequest();
        nextRequest.setSession(session);
        nextRequest.setRequestURI("/");
        interceptor.preHandle(nextRequest, new MockHttpServletResponse(), new Object());

        assertThat(resolver.resolveLocale(nextRequest).getLanguage()).isEqualTo("cy");
    }

    @Test
    void unsupportedGuestLanguageFallsBackToEnglish() {
        SessionLocaleResolver resolver = new SessionLocaleResolver();
        resolver.setDefaultLocale(Locale.ENGLISH);
        UserSettingsLocaleInterceptor interceptor = new UserSettingsLocaleInterceptor(
                mock(AuthHelper.class),
                mock(UserSettingsService.class),
                resolver
        );
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/");
        request.setParameter("lang", "unknown");

        interceptor.preHandle(request, new MockHttpServletResponse(), new Object());

        assertThat(resolver.resolveLocale(request).getLanguage()).isEqualTo("en");
    }

    @Test
    void authenticatedLanguageSelectionUpdatesTheSavedPreference() {
        AuthHelper authHelper = mock(AuthHelper.class);
        UserSettingsService settingsService = mock(UserSettingsService.class);
        User user = mock(User.class);
        UserSettings settings = new UserSettings();
        settings.setLanguage("en");
        settings.setTheme(ThemePreference.SYSTEM);
        settings.setEasyMode(false);
        when(authHelper.getAuthenticatedUser()).thenReturn(user);
        when(settingsService.getOrCreate(user)).thenReturn(settings);
        when(settingsService.update(user, "cy", ThemePreference.SYSTEM, false)).thenReturn(settings);
        SessionLocaleResolver resolver = new SessionLocaleResolver();
        resolver.setDefaultLocale(Locale.ENGLISH);
        UserSettingsLocaleInterceptor interceptor =
                new UserSettingsLocaleInterceptor(authHelper, settingsService, resolver);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/");
        request.setParameter("lang", "cy");

        interceptor.preHandle(request, new MockHttpServletResponse(), new Object());

        verify(settingsService).update(user, "cy", ThemePreference.SYSTEM, false);
        assertThat(resolver.resolveLocale(request).getLanguage()).isEqualTo("cy");
    }
}
