package uk.ac.cf._5.group14.One_To_One.ProfileTests;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import uk.ac.cf._5.group14.One_To_One.DataExport.DataExportRequestService;
import uk.ac.cf._5.group14.One_To_One.DayMode.DayModeService;
import uk.ac.cf._5.group14.One_To_One.ExerciseLog.ExerciseLogService;
import uk.ac.cf._5.group14.One_To_One.HealthConditions.HealthConditionType;
import uk.ac.cf._5.group14.One_To_One.HealthConditions.UserHealthConditionService;
import uk.ac.cf._5.group14.One_To_One.Level.LevelProgress;
import uk.ac.cf._5.group14.One_To_One.Level.LevelService;
import uk.ac.cf._5.group14.One_To_One.MerchOrders.MerchOrderService;
import uk.ac.cf._5.group14.One_To_One.PaymentCards.SavedPaymentMethodService;
import uk.ac.cf._5.group14.One_To_One.PlatformBilling.PlatformSubscriptionService;
import uk.ac.cf._5.group14.One_To_One.Profile.FileStorageService;
import uk.ac.cf._5.group14.One_To_One.Profile.ProfileController;
import uk.ac.cf._5.group14.One_To_One.UserSettings.UserSettings;
import uk.ac.cf._5.group14.One_To_One.UserSettings.UserSettingsService;
import uk.ac.cf._5.group14.One_To_One.Users.AuthHelper;
import uk.ac.cf._5.group14.One_To_One.Users.User;
import uk.ac.cf._5.group14.One_To_One.Users.UserRepository;
import uk.ac.cf._5.group14.One_To_One.Users.UserService;
import uk.ac.cf._5.group14.One_To_One.Config.DevModeProperties;

/**
 * Verifies that the profile route returns HTTP 200 when a valid session user
 * is present (simulating dev-mode access with a test user), and redirects to
 * login when no session user is found (preventing 500 NPE).
 */
@WebMvcTest(ProfileController.class)
@ActiveProfiles("test")
class ProfileRouteAccessTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean private UserService userService;
    @MockitoBean private ExerciseLogService exerciseLogService;
    @MockitoBean private PlatformSubscriptionService platformSubscriptionService;
    @MockitoBean private UserSettingsService userSettingsService;
    @MockitoBean private UserHealthConditionService conditionService;
    @MockitoBean private DataExportRequestService dataExportRequestService;
    @MockitoBean private LevelService levelService;
    @MockitoBean private FileStorageService fileStorageService;
    @MockitoBean private UserRepository userRepository;
    @MockitoBean private AuthHelper authHelper;
    @MockitoBean private DayModeService dayModeService;
    @MockitoBean private SavedPaymentMethodService savedPaymentMethodService;
    @MockitoBean private MerchOrderService merchOrderService;

    @Test
    void profileRouteReturns200WhenSessionUserPresent() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("devtest");
        user.setEmail("devtest@example.com");
        user.setFirstName("Dev");
        user.setLastName("Test");

        UserSettings settings = new UserSettings();
        settings.setUser(user);
        settings.setUserId(user.getId());

        LevelProgress levelProgress = new LevelProgress();
        levelProgress.setUser(user);
        levelProgress.setPoints(0);
        levelProgress.setLevel(1);

        given(authHelper.getAuthenticatedUser()).willReturn(user);
        given(platformSubscriptionService.findByUserId(eq(user.getId()))).willReturn(java.util.Optional.empty());
        given(userSettingsService.getOrCreate(eq(user))).willReturn(settings);
        given(levelService.getProgress(eq(user))).willReturn(levelProgress);
        given(conditionService.getConditionsByType(eq(user), eq(HealthConditionType.PERMANENT)))
                .willReturn(Collections.emptyList());
        given(conditionService.getConditionsByType(eq(user), eq(HealthConditionType.TIMED)))
                .willReturn(Collections.emptyList());
        given(exerciseLogService.getLogsByUser(eq(user))).willReturn(Collections.emptyList());
        given(dataExportRequestService.getRecentRequests(eq(user))).willReturn(Collections.emptyList());
        given(savedPaymentMethodService.getCardsForUser(eq(user.getId()))).willReturn(Collections.emptyList());
        given(merchOrderService.getOrdersForUser(eq(user.getId()))).willReturn(Collections.emptyList());

        mvc.perform(get("/profile").sessionAttr("user", user))
                .andExpect(status().isOk())
                .andExpect(view().name("client-views/profile/profile"));
    }

    @Test
    void profileRouteRedirectsToLoginWhenNoSessionUser() throws Exception {
        // When getAuthenticatedUser returns null (no session), expect redirect instead of 500
        given(authHelper.getAuthenticatedUser()).willReturn(null);

        mvc.perform(get("/profile"))
                .andExpect(status().is3xxRedirection());
    }

    @TestConfiguration
    static class TestSecurityConfig {
        @Bean("testProfileSecurityFilterChain")
        SecurityFilterChain testProfileSecurityFilterChain(HttpSecurity http) throws Exception {
            return http
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                    .build();
        }

        @Bean
        public AuthHelper authHelper() {
            return mock(AuthHelper.class);
        }

        @Bean
        public PlatformSubscriptionService platformSubscriptionService() {
            return mock(PlatformSubscriptionService.class);
        }

        @Bean
        public Clock systemClock() {
            return Clock.fixed(Instant.parse("2026-03-15T12:00:00Z"), ZoneId.of("UTC"));
        }
    
        @Bean
        public DevModeProperties devModeProperties() {
            return new DevModeProperties();
        }
}
}
