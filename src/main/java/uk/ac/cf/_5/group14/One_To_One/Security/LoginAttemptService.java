package uk.ac.cf._5.group14.One_To_One.Security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final Duration WINDOW = Duration.ofMinutes(15);
    private static final Duration BLOCK_DURATION = Duration.ofMinutes(5);

    private static final class Attempt {
        int count;
        Instant windowStart;
        Instant blockedUntil;
    }

    private final Map<String, Attempt> attempts = new ConcurrentHashMap<>();

    public boolean isBlocked(HttpServletRequest request) {
        String key = key(request);
        Attempt attempt = attempts.get(key);
        if (attempt == null || attempt.blockedUntil == null) return false;

        Instant now = Instant.now();
        if (now.isBefore(attempt.blockedUntil)) return true;

        // Unblock once time has passed.
        attempt.blockedUntil = null;
        attempt.count = 0;
        attempt.windowStart = null;
        return false;
    }

    public void recordFailure(HttpServletRequest request) {
        String key = key(request);
        Instant now = Instant.now();

        attempts.compute(key, (k, attempt) -> {
            if (attempt == null) attempt = new Attempt();

            if (attempt.blockedUntil != null && now.isBefore(attempt.blockedUntil)) {
                return attempt;
            }

            if (attempt.windowStart == null || now.isAfter(attempt.windowStart.plus(WINDOW))) {
                attempt.windowStart = now;
                attempt.count = 0;
                attempt.blockedUntil = null;
            }

            attempt.count++;
            if (attempt.count >= MAX_ATTEMPTS) {
                attempt.blockedUntil = now.plus(BLOCK_DURATION);
            }

            return attempt;
        });
    }

    public void recordSuccess(HttpServletRequest request) {
        attempts.remove(key(request));
    }

    private String key(HttpServletRequest request) {
        String username = request.getParameter("username");
        if (username == null) username = "";

        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) {
            ip = request.getRemoteAddr();
        } else {
            // In case of multiple IPs, take the left-most.
            int comma = ip.indexOf(',');
            if (comma > -1) ip = ip.substring(0, comma);
            ip = ip.trim();
        }

        return username.trim().toLowerCase() + "|" + ip;
    }
}
