package uk.ac.cf._5.group14.One_To_One.Security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Locale;

@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final Duration WINDOW = Duration.ofMinutes(15);
    private static final Duration BLOCK_DURATION = Duration.ofMinutes(5);

    private final JdbcTemplate jdbcTemplate;
    private final Clock clock;

    public LoginAttemptService(JdbcTemplate jdbcTemplate, Clock clock) {
        this.jdbcTemplate = jdbcTemplate;
        this.clock = clock;
    }

    public boolean isBlocked(HttpServletRequest request) {
        String key = key(request);
        Instant now = Instant.now(clock);
        List<Timestamp> blockedUntil = jdbcTemplate.query(
                "SELECT blocked_until FROM login_attempts WHERE attempt_key_hash = ?",
                (resultSet, rowNumber) -> resultSet.getTimestamp("blocked_until"),
                key);
        if (blockedUntil.isEmpty() || blockedUntil.getFirst() == null) {
            return false;
        }
        if (now.isBefore(blockedUntil.getFirst().toInstant())) {
            return true;
        }
        jdbcTemplate.update(
                "DELETE FROM login_attempts WHERE attempt_key_hash = ? AND blocked_until <= ?",
                key,
                Timestamp.from(now));
        return false;
    }

    public void recordFailure(HttpServletRequest request) {
        String key = key(request);
        Instant now = Instant.now(clock);
        if (updateExistingFailure(key, now) > 0) {
            return;
        }
        try {
            jdbcTemplate.update(
                    """
                    INSERT INTO login_attempts
                        (attempt_key_hash, attempt_count, window_start, blocked_until, updated_at)
                    VALUES (?, 1, ?, NULL, ?)
                    """,
                    key,
                    Timestamp.from(now),
                    Timestamp.from(now));
        } catch (DuplicateKeyException concurrentInsert) {
            updateExistingFailure(key, now);
        }
    }

    public void recordSuccess(HttpServletRequest request) {
        jdbcTemplate.update("DELETE FROM login_attempts WHERE attempt_key_hash = ?", key(request));
    }

    private String key(HttpServletRequest request) {
        String username = request.getParameter("username");
        if (username == null) username = "";

        String ip = request.getRemoteAddr();
        if (ip == null) ip = "";

        return SecurityFingerprint.sha256(username.trim().toLowerCase(Locale.ROOT) + "|" + ip.trim());
    }

    public int purgeBefore(Instant cutoff) {
        return jdbcTemplate.update(
                "DELETE FROM login_attempts WHERE updated_at < ?",
                Timestamp.from(cutoff));
    }

    private int updateExistingFailure(String key, Instant now) {
        Instant windowCutoff = now.minus(WINDOW);
        Instant blockUntil = now.plus(BLOCK_DURATION);
        return jdbcTemplate.update(
                """
                UPDATE login_attempts
                   SET attempt_count = CASE
                           WHEN blocked_until IS NOT NULL AND blocked_until > ? THEN attempt_count
                           WHEN window_start IS NULL OR window_start < ? OR blocked_until IS NOT NULL THEN 1
                           ELSE attempt_count + 1
                       END,
                       window_start = CASE
                           WHEN blocked_until IS NOT NULL AND blocked_until > ? THEN window_start
                           WHEN window_start IS NULL OR window_start < ? OR blocked_until IS NOT NULL THEN ?
                           ELSE window_start
                       END,
                       blocked_until = CASE
                           WHEN blocked_until IS NOT NULL AND blocked_until > ? THEN blocked_until
                           WHEN window_start IS NULL OR window_start < ? OR blocked_until IS NOT NULL THEN NULL
                           WHEN attempt_count + 1 >= ? THEN ?
                           ELSE NULL
                       END,
                       updated_at = ?
                 WHERE attempt_key_hash = ?
                """,
                Timestamp.from(now),
                Timestamp.from(windowCutoff),
                Timestamp.from(now),
                Timestamp.from(windowCutoff),
                Timestamp.from(now),
                Timestamp.from(now),
                Timestamp.from(windowCutoff),
                MAX_ATTEMPTS,
                Timestamp.from(blockUntil),
                Timestamp.from(now),
                key);
    }
}
