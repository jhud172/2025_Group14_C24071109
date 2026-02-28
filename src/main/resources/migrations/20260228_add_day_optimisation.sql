-- Day Optimisation Migration
-- Adds day_optimisations table for per-user, per-date AI optimise one-shot enforcement.

CREATE TABLE IF NOT EXISTS day_optimisations
(
    user_id   BIGINT      NOT NULL,
    date      DATE        NOT NULL,
    day_theme VARCHAR(30) NOT NULL DEFAULT 'CLEAN',
    created_at TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, date),
    CONSTRAINT fk_day_optimisations_user
        FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX IF NOT EXISTS idx_day_optimisations_user_date
    ON day_optimisations (user_id, date);
