package uk.ac.cf._5.group14.One_To_One.PaymentsTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.cf._5.group14.One_To_One.Payments.StripeWebhookEventStore;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class StripeWebhookEventStoreIntegrationTest {

    @Autowired
    private StripeWebhookEventStore eventStore;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void processedEventIdPersistsOnceAndCanBeDetected() {
        String eventId = "evt_contract_duplicate_1";

        assertThat(eventStore.hasProcessed(eventId)).isFalse();

        eventStore.recordProcessed(eventId, "invoice.payment_succeeded");
        eventStore.recordProcessed(eventId, "invoice.payment_succeeded");

        assertThat(eventStore.hasProcessed(eventId)).isTrue();
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM stripe_webhook_events WHERE event_id = ?",
                Integer.class,
                eventId);
        assertThat(count).isEqualTo(1);
    }
}
