package uk.ac.cf._5.group14.One_To_One.Security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.Instant;

@Service
@Slf4j
public class PrivilegedAuditService {

    private final JdbcTemplate jdbcTemplate;
    private final Clock clock;

    public PrivilegedAuditService(JdbcTemplate jdbcTemplate, Clock clock) {
        this.jdbcTemplate = jdbcTemplate;
        this.clock = clock;
    }

    public void record(
            String requestId,
            String actor,
            String authorities,
            String method,
            String path,
            int status,
            boolean succeeded,
            String sourceIp) {
        try {
            jdbcTemplate.update(
                    """
                    INSERT INTO privileged_audit_events
                        (occurred_at, request_id, actor, authorities, http_method,
                         request_path, response_status, succeeded, source_ip_hash)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """,
                    Timestamp.from(Instant.now(clock)),
                    bounded(requestId, 80),
                    bounded(actor, 100),
                    bounded(authorities, 500),
                    bounded(method, 10),
                    bounded(path, 500),
                    status,
                    succeeded,
                    SecurityFingerprint.sha256(sourceIp));
        } catch (RuntimeException ex) {
            log.error("Failed to retain privileged-action audit event {}", requestId, ex);
        }
    }

    public int purgeBefore(Instant cutoff) {
        return jdbcTemplate.update(
                "DELETE FROM privileged_audit_events WHERE occurred_at < ?",
                Timestamp.from(cutoff));
    }

    private static String bounded(String value, int maximumLength) {
        String normalized = value == null ? "" : value;
        return normalized.length() <= maximumLength
                ? normalized
                : normalized.substring(0, maximumLength);
    }
}
