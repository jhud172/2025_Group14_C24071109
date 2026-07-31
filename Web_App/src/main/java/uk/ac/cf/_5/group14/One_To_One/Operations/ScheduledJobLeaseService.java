package uk.ac.cf._5.group14.One_To_One.Operations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

@Service
public class ScheduledJobLeaseService {

    private final JdbcTemplate jdbcTemplate;
    private final Clock clock;
    private final String instanceId;

    public ScheduledJobLeaseService(
            JdbcTemplate jdbcTemplate,
            Clock clock,
            @Value("${RENDER_INSTANCE_ID:${HOSTNAME:local}}") String instanceId) {
        this.jdbcTemplate = jdbcTemplate;
        this.clock = clock;
        this.instanceId = instanceId == null || instanceId.isBlank() ? "local" : instanceId;
    }

    public boolean acquire(String jobName, Duration lockAtMostFor) {
        Instant now = Instant.now(clock);
        Instant lockedUntil = now.plus(lockAtMostFor);
        int updated = jdbcTemplate.update(
                """
                UPDATE scheduled_job_locks
                   SET locked_until = ?, locked_at = ?, locked_by = ?
                 WHERE job_name = ? AND locked_until <= ?
                """,
                Timestamp.from(lockedUntil),
                Timestamp.from(now),
                instanceId,
                jobName,
                Timestamp.from(now));
        if (updated > 0) {
            return true;
        }

        try {
            jdbcTemplate.update(
                    """
                    INSERT INTO scheduled_job_locks (job_name, locked_until, locked_at, locked_by)
                    VALUES (?, ?, ?, ?)
                    """,
                    jobName,
                    Timestamp.from(lockedUntil),
                    Timestamp.from(now),
                    instanceId);
            return true;
        } catch (DuplicateKeyException alreadyOwned) {
            return false;
        }
    }

    public void release(String jobName) {
        Instant now = Instant.now(clock);
        jdbcTemplate.update(
                """
                UPDATE scheduled_job_locks
                   SET locked_until = ?
                 WHERE job_name = ? AND locked_by = ?
                """,
                Timestamp.from(now),
                jobName,
                instanceId);
    }
}
