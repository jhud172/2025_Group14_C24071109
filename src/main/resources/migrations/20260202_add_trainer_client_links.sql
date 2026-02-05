-- Trainer <-> Client relationship lifecycle
CREATE TABLE IF NOT EXISTS trainer_client_links
(
    id            BIGSERIAL PRIMARY KEY,
    client_id     BIGINT      NOT NULL,
    trainer_id    BIGINT      NOT NULL,
    status        VARCHAR(20) NOT NULL,
    requested_at  TIMESTAMP   NULL,
    activated_at  TIMESTAMP   NULL,
    paused_at     TIMESTAMP   NULL,
    ended_at      TIMESTAMP   NULL,
    created_at    TIMESTAMP   NOT NULL,
    updated_at    TIMESTAMP   NOT NULL,

    CONSTRAINT fk_tcl_client
        FOREIGN KEY (client_id) REFERENCES users (id),

    CONSTRAINT fk_tcl_trainer
        FOREIGN KEY (trainer_id) REFERENCES users (id)
);

CREATE INDEX IF NOT EXISTS idx_tcl_client_id
    ON trainer_client_links (client_id);

CREATE INDEX IF NOT EXISTS idx_tcl_trainer_id
    ON trainer_client_links (trainer_id);

CREATE INDEX IF NOT EXISTS idx_tcl_status
    ON trainer_client_links (status);
