package uk.ac.cf._5.group14.One_To_One.Payments;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.Instant;

@Service
public class StripeWebhookEventStore {

    private final JdbcTemplate jdbcTemplate;
    private final Clock clock;

    public StripeWebhookEventStore(JdbcTemplate jdbcTemplate, Clock clock) {
        this.jdbcTemplate = jdbcTemplate;
        this.clock = clock;
    }

    public boolean hasProcessed(String eventId) {
        if (eventId == null || eventId.isBlank()) {
            return false;
        }
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM stripe_webhook_events WHERE event_id = ?",
                Integer.class,
                eventId.trim());
        return count != null && count > 0;
    }

    public void recordProcessed(String eventId, String eventType) {
        if (eventId == null || eventId.isBlank()) {
            return;
        }
        try {
            jdbcTemplate.update(
                    """
                    INSERT INTO stripe_webhook_events (event_id, event_type, processed_at)
                    VALUES (?, ?, ?)
                    """,
                    eventId.trim(),
                    eventType == null ? "" : eventType.trim(),
                    Timestamp.from(Instant.now(clock)));
        } catch (DuplicateKeyException ignored) {
            // A concurrent delivery already recorded this immutable Stripe event id.
        }
    }
}
