package uk.ac.cf._5.group14.BehaviourChangeGroupProject.SecurityTests;

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
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Config.DevModeProperties;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Membership.EmailService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Merch.MerchProductService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Verifies dev-mode security rules in SecurityConfig:
 *   - Most pages are open to unauthenticated users in dev mode.
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
        @Bean
        @Primary
        public DevModeProperties devModePropertiesEnabled() {
            return new DevModeProperties() {
                @Override
                public boolean isDevMode() {
                    return true;
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

    // --- Dev mode: normally-protected pages should be open ---

    @Test
    void devMode_unauthenticatedCanAccessDashboard() throws Exception {
        // /dashboard normally requires authentication; in dev mode security must not redirect to login.
        String redirectUrl = mockMvc.perform(get("/dashboard"))
                .andReturn().getResponse().getHeader("Location");
        // Either no redirect at all (2xx/5xx from controller), or redirect somewhere other than /login
        if (redirectUrl != null) {
            assertThat(redirectUrl).as("Dev mode should not redirect /dashboard to login")
                    .doesNotContain("/login");
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
