package uk.ac.cf._5.group14.One_To_One.UserSettingsTests;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import uk.ac.cf._5.group14.One_To_One.Level.LevelService;
import uk.ac.cf._5.group14.One_To_One.UserSettings.UserSettings;
import uk.ac.cf._5.group14.One_To_One.UserSettings.UserSettingsModelAdvice;
import uk.ac.cf._5.group14.One_To_One.UserSettings.UserSettingsService;
import uk.ac.cf._5.group14.One_To_One.Users.AuthHelper;
import uk.ac.cf._5.group14.One_To_One.Users.User;

/**
 * Unit tests for the selectedMilestones @ModelAttribute method added to
 * UserSettingsModelAdvice. Verifies parsing, empty-key handling, and
 * unauthenticated/no-request-context paths.
 */
@ExtendWith(MockitoExtension.class)
class UserSettingsModelAdviceTest {

    @Mock private AuthHelper authHelper;
    @Mock private UserSettingsService userSettingsService;
    @Mock private ObjectProvider<LevelService> levelServiceProvider;

    private UserSettingsModelAdvice advice;

    @BeforeEach
    void setUp() {
        advice = new UserSettingsModelAdvice(authHelper, userSettingsService, levelServiceProvider);
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    private void bindRequest(String path) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(path);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    private User makeUser(long id) {
        User user = new User();
        user.setId(id);
        user.setUsername("user" + id);
        return user;
    }

    // ── No request context ───────────────────────────────────────────────────

    @Test
    void selectedMilestonesReturnsEmptyWhenNoRequestContext() {
        List<String> result = advice.selectedMilestones();
        assertThat(result).isEmpty();
    }

    // ── Skipped request paths ────────────────────────────────────────────────

    @Test
    void selectedMilestonesReturnsEmptyForApiPath() {
        bindRequest("/api/some/endpoint");
        List<String> result = advice.selectedMilestones();
        assertThat(result).isEmpty();
    }

    @Test
    void selectedMilestonesReturnsEmptyForStaticJsPath() {
        bindRequest("/js/core/app.js");
        List<String> result = advice.selectedMilestones();
        assertThat(result).isEmpty();
    }

    // ── Unauthenticated user ─────────────────────────────────────────────────

    @Test
    void selectedMilestonesReturnsEmptyWhenUserNotAuthenticated() {
        bindRequest("/profile");
        when(authHelper.getAuthenticatedUser()).thenReturn(null);
        List<String> result = advice.selectedMilestones();
        assertThat(result).isEmpty();
    }

    // ── Null / blank keys ────────────────────────────────────────────────────

    @Test
    void selectedMilestonesReturnsEmptyWhenKeysIsNull() {
        bindRequest("/profile");
        User user = makeUser(1L);
        UserSettings settings = new UserSettings();
        settings.setProfileMilestoneKeys(null);
        when(authHelper.getAuthenticatedUser()).thenReturn(user);
        when(userSettingsService.getOrCreate(user)).thenReturn(settings);

        assertThat(advice.selectedMilestones()).isEmpty();
    }

    @Test
    void selectedMilestonesReturnsEmptyWhenKeysIsBlank() {
        bindRequest("/profile");
        User user = makeUser(2L);
        UserSettings settings = new UserSettings();
        settings.setProfileMilestoneKeys("   ");
        when(authHelper.getAuthenticatedUser()).thenReturn(user);
        when(userSettingsService.getOrCreate(user)).thenReturn(settings);

        assertThat(advice.selectedMilestones()).isEmpty();
    }

    // ── Parsing ──────────────────────────────────────────────────────────────

    @Test
    void selectedMilestonesParsesSingleKey() {
        bindRequest("/dashboard");
        User user = makeUser(3L);
        UserSettings settings = new UserSettings();
        settings.setProfileMilestoneKeys("streak");
        when(authHelper.getAuthenticatedUser()).thenReturn(user);
        when(userSettingsService.getOrCreate(user)).thenReturn(settings);

        assertThat(advice.selectedMilestones()).containsExactly("streak");
    }

    @Test
    void selectedMilestonesParsesSeveralKeysInOrder() {
        bindRequest("/dashboard");
        User user = makeUser(4L);
        UserSettings settings = new UserSettings();
        settings.setProfileMilestoneKeys("streak,weight,milestone3");
        when(authHelper.getAuthenticatedUser()).thenReturn(user);
        when(userSettingsService.getOrCreate(user)).thenReturn(settings);

        assertThat(advice.selectedMilestones()).containsExactly("streak", "weight", "milestone3");
    }

    @Test
    void selectedMilestonesTrimsWhitespaceAroundKeys() {
        bindRequest("/dashboard");
        User user = makeUser(5L);
        UserSettings settings = new UserSettings();
        settings.setProfileMilestoneKeys("  streak , weight  ,  milestone3  ");
        when(authHelper.getAuthenticatedUser()).thenReturn(user);
        when(userSettingsService.getOrCreate(user)).thenReturn(settings);

        assertThat(advice.selectedMilestones()).containsExactly("streak", "weight", "milestone3");
    }

    @Test
    void selectedMilestonesFiltersOutBlankTokensFromSparseString() {
        bindRequest("/dashboard");
        User user = makeUser(6L);
        UserSettings settings = new UserSettings();
        settings.setProfileMilestoneKeys("streak,,weight,  ,milestone3");
        when(authHelper.getAuthenticatedUser()).thenReturn(user);
        when(userSettingsService.getOrCreate(user)).thenReturn(settings);

        assertThat(advice.selectedMilestones()).containsExactly("streak", "weight", "milestone3");
    }
}
