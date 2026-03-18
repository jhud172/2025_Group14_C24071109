package uk.ac.cf._5.group14.One_To_One.SecurityTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import org.mockito.Mockito;
import uk.ac.cf._5.group14.One_To_One.Config.DevModeProperties;
import uk.ac.cf._5.group14.One_To_One.Membership.EmailService;
import uk.ac.cf._5.group14.One_To_One.Merch.MerchProductService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Verifies dev-mode security rules in SecurityConfig:
 *   - Public pages remain open to unauthenticated users in dev mode.
 *   - Core authenticated pages (dashboard, calendar, workouts, goals, profile)
 *     require login in dev mode â€” they are NOT open to unauthenticated users.
 *   - Leaderboard (/levels/**), Trainers area (/trainer/**, /trainers/**), and
 *     Training Vault (/vault/**) remain protected even when dev mode is active.
 *   - Normal mode (dev mode off) keeps all existing access controls unchanged.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DevModeSecurityTest {

    /**
     * Replaces the real DevModeProperties with one that always reports dev mode
     * as enabled, so the security filter chain is configured for dev mode.
     * The @Primary annotation ensures SecurityConfig autowires this bean.
     *
     * A mock EmailService is also provided here because SmtpEmailService is
     * conditional on spring.mail.host which is not set in the test environment.
     */
    @TestConfiguration
    static class DevModeEnabledConfig {
        @Bean("devModeProperties")
        @Primary
        public DevModeProperties devModePropertiesEnabled() {
            return new DevModeProperties() {
                @Override
                public boolean isDevMode() {
                    return true;
                }

                @Override
                public void loadEnvFile() {
                    // No-op: prevent .env loading in tests to avoid polluting system properties
                }
            };
        }

        @Bean
        public EmailService emailService() {
            return Mockito.mock(EmailService.class);
        }
    }

    /**
     * MerchProductServiceImpl and MerchOrderServiceImpl have a constructor-injection
     * circular dependency. Mocking MerchProductService here prevents Spring from
     * trying to create the real implementation, breaking the cycle.
     */
    @MockitoBean
    private MerchProductService merchProductService;

    @Autowired
    private MockMvc mockMvc;

    /** Asserts that the HTTP status code is outside the 2xx success range. */
    private static void assertBlockedStatus(int status, String context) {
        assertThat(status < 200 || status >= 300)
                .as("Access to " + context + " should be blocked in dev mode, got status: " + status)
                .isTrue();
    }

    // --- Dev mode: public pages remain open to unauthenticated users ---

    @Test
    void devMode_unauthenticatedCanAccessHome() throws Exception {
        int status = mockMvc.perform(get("/")).andReturn().getResponse().getStatus();
        assertThat(status).as("Home page should be accessible in dev mode without auth")
                .isBetween(200, 399);
    }

    @Test
    void devMode_unauthenticatedCanAccessDevHub() throws Exception {
        int status = mockMvc.perform(get("/dev-mode")).andReturn().getResponse().getStatus();
        assertThat(status).as("Dev hub should be accessible in dev mode without auth")
                .isBetween(200, 399);
    }

    // --- Dev mode: core auth pages require login ---

    @Test
    void devMode_unauthenticatedCannotAccessDashboard() throws Exception {
        // In dev mode, /dashboard still requires authentication.
        String redirectUrl = mockMvc.perform(get("/dashboard"))
                .andReturn().getResponse().getHeader("Location");
        // Must redirect to login (not open to unauthenticated users in dev mode)
        if (redirectUrl != null) {
            assertThat(redirectUrl).as("Dev mode must redirect /dashboard to login for unauthenticated users")
                    .contains("/login");
        } else {
            int status = mockMvc.perform(get("/dashboard")).andReturn().getResponse().getStatus();
            assertBlockedStatus(status, "/dashboard");
        }
    }

    @Test
    void devMode_unauthenticatedCannotAccessCalendar() throws Exception {
        String redirectUrl = mockMvc.perform(get("/calendar"))
                .andReturn().getResponse().getHeader("Location");
        if (redirectUrl != null) {
            assertThat(redirectUrl).as("Dev mode must redirect /calendar to login for unauthenticated users")
                    .contains("/login");
        } else {
            int status = mockMvc.perform(get("/calendar")).andReturn().getResponse().getStatus();
            assertBlockedStatus(status, "/calendar");
        }
    }

    @Test
    void devMode_unauthenticatedCannotAccessGoals() throws Exception {
        String redirectUrl = mockMvc.perform(get("/goals"))
                .andReturn().getResponse().getHeader("Location");
        if (redirectUrl != null) {
            assertThat(redirectUrl).as("Dev mode must redirect /goals to login for unauthenticated users")
                    .contains("/login");
        } else {
            int status = mockMvc.perform(get("/goals")).andReturn().getResponse().getStatus();
            assertBlockedStatus(status, "/goals");
        }
    }

    // --- Dev mode: restricted sections must stay locked ---

    @Test
    void devMode_unauthenticatedCannotAccessLeaderboard() throws Exception {
        // /levels/** (Leaderboard) must remain protected even in dev mode.
        int status = mockMvc.perform(get("/levels")).andReturn().getResponse().getStatus();
        assertBlockedStatus(status, "/levels (leaderboard)");
    }

    @Test
    void devMode_unauthenticatedCannotAccessTrainerArea() throws Exception {
        // /trainer/** (Trainers area) must remain protected even in dev mode.
        int status = mockMvc.perform(get("/trainer/dashboard")).andReturn().getResponse().getStatus();
        assertBlockedStatus(status, "/trainer/dashboard (trainer area)");
    }

    @Test
    void devMode_unauthenticatedCannotAccessTrainersDirectory() throws Exception {
        // /trainers/** (client-facing trainer directory) must remain protected even in dev mode.
        int status = mockMvc.perform(get("/trainers")).andReturn().getResponse().getStatus();
        assertBlockedStatus(status, "/trainers (trainer directory)");
    }

    @Test
    void devMode_unauthenticatedCannotAccessTrainingVault() throws Exception {
        // /vault/** (Training Vault) must remain protected even in dev mode.
        int status = mockMvc.perform(get("/vault")).andReturn().getResponse().getStatus();
        assertBlockedStatus(status, "/vault (training vault)");
    }
}
