-- Blood Pressure Reading Migration
CREATE TABLE IF NOT EXISTS blood_pressure_readings
(
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT       NOT NULL,
    reading_date DATE         NOT NULL,
    reading_time TIME         NULL,
    systolic     INT          NOT NULL,
    diastolic    INT          NOT NULL,
    pulse        INT          NULL,
    arm          VARCHAR(10)  NULL,
    position     VARCHAR(10)  NULL,
    notes        VARCHAR(500) NULL,
    source       VARCHAR(10)  NOT NULL DEFAULT 'MANUAL',
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_bp_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_bp_user_date ON blood_pressure_readings (user_id, reading_date DESC);
