CREATE TABLE IF NOT EXISTS quick_action_definitions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type VARCHAR(20) NOT NULL,
    name VARCHAR(120) NOT NULL,
    action_key VARCHAR(60),
    prompt VARCHAR(2000),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    sort_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_quick_action_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_quick_action_user
    ON quick_action_definitions (user_id, sort_order);
