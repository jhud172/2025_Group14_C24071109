package uk.ac.cf._5.group14.One_To_One.Security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.mock.web.MockHttpServletRequest;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class LoginAttemptServiceTest {

    private JdbcTemplate jdbcTemplate;
    private Clock clock;

    @BeforeEach
    void setUp() {
        jdbcTemplate = new JdbcTemplate(new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .generateUniqueName(true)
                .build());
        jdbcTemplate.execute("""
                CREATE TABLE login_attempts (
                    attempt_key_hash CHAR(64) PRIMARY KEY,
                    attempt_count INT NOT NULL,
                    window_start TIMESTAMP WITH TIME ZONE,
                    blocked_until TIMESTAMP WITH TIME ZONE,
                    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
                )
                """);
        clock = Clock.fixed(Instant.parse("2026-07-29T10:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void throttlePersistsAcrossServiceInstancesWithoutStoringRawIdentity() {
        LoginAttemptService firstInstance = new LoginAttemptService(jdbcTemplate, clock);
        MockHttpServletRequest request = loginRequest("person@example.test", "192.0.2.10");

        for (int attempt = 0; attempt < 5; attempt++) {
            firstInstance.recordFailure(request);
        }

        LoginAttemptService restartedInstance = new LoginAttemptService(jdbcTemplate, clock);
        assertThat(restartedInstance.isBlocked(request)).isTrue();
        assertThat(jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM login_attempts WHERE attempt_key_hash LIKE '%person%'",
                Integer.class)).isZero();
    }

    @Test
    void successfulAuthenticationClearsOnlyTheMatchingThrottleKey() {
        LoginAttemptService service = new LoginAttemptService(jdbcTemplate, clock);
        MockHttpServletRequest first = loginRequest("first@example.test", "192.0.2.20");
        MockHttpServletRequest second = loginRequest("second@example.test", "192.0.2.20");

        service.recordFailure(first);
        service.recordFailure(second);
        service.recordSuccess(first);

        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM login_attempts", Integer.class))
                .isEqualTo(1);
    }

    private static MockHttpServletRequest loginRequest(String username, String address) {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/login");
        request.setParameter("username", username);
        request.setRemoteAddr(address);
        return request;
    }
}
