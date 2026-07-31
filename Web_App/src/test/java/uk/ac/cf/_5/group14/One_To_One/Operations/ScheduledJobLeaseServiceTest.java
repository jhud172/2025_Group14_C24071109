package uk.ac.cf._5.group14.One_To_One.Operations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class ScheduledJobLeaseServiceTest {

    private JdbcTemplate jdbcTemplate;
    private Clock clock;

    @BeforeEach
    void setUp() {
        jdbcTemplate = new JdbcTemplate(new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .generateUniqueName(true)
                .build());
        jdbcTemplate.execute("""
                CREATE TABLE scheduled_job_locks (
                    job_name VARCHAR(120) PRIMARY KEY,
                    locked_until TIMESTAMP WITH TIME ZONE NOT NULL,
                    locked_at TIMESTAMP WITH TIME ZONE NOT NULL,
                    locked_by VARCHAR(120) NOT NULL
                )
                """);
        clock = Clock.fixed(Instant.parse("2026-07-29T10:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void onlyOneInstanceOwnsAJobUntilItReleasesTheLease() {
        ScheduledJobLeaseService first = new ScheduledJobLeaseService(jdbcTemplate, clock, "instance-a");
        ScheduledJobLeaseService second = new ScheduledJobLeaseService(jdbcTemplate, clock, "instance-b");

        assertThat(first.acquire("notification-scan", Duration.ofMinutes(10))).isTrue();
        assertThat(second.acquire("notification-scan", Duration.ofMinutes(10))).isFalse();

        first.release("notification-scan");

        assertThat(second.acquire("notification-scan", Duration.ofMinutes(10))).isTrue();
    }
}
