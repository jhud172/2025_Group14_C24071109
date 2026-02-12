CREATE TABLE IF NOT EXISTS daily_nutrition_logs
(
    id            BIGSERIAL PRIMARY KEY,
    user_id       BIGINT      NOT NULL,
    log_date      DATE        NOT NULL,
    calories      INTEGER     NOT NULL,
    protein_grams INTEGER     NOT NULL,
    carbs_grams   INTEGER     NOT NULL,
    fat_grams     INTEGER     NOT NULL,
    fibre_grams   INTEGER     NULL,
    water_ml      INTEGER     NULL,
    notes         VARCHAR(1000),
    updated_at    TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_daily_nutrition_user
        FOREIGN KEY (user_id) REFERENCES users (id)
            ON DELETE CASCADE,

    CONSTRAINT uq_daily_nutrition_user_date
        UNIQUE (user_id, log_date)
);

CREATE INDEX IF NOT EXISTS idx_daily_nutrition_user
    ON daily_nutrition_logs (user_id);

CREATE INDEX IF NOT EXISTS idx_daily_nutrition_user_date
    ON daily_nutrition_logs (user_id, log_date);
