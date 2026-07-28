CREATE TABLE IF NOT EXISTS stripe_webhook_events
(
    event_id      VARCHAR(255) PRIMARY KEY,
    event_type    VARCHAR(120) NOT NULL,
    processed_at  TIMESTAMPTZ NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_stripe_webhook_events_processed_at
    ON stripe_webhook_events (processed_at);
