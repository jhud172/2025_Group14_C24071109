CREATE TABLE IF NOT EXISTS spring_session
(
    primary_id            CHAR(36)     NOT NULL PRIMARY KEY,
    session_id            CHAR(36)     NOT NULL,
    creation_time         BIGINT       NOT NULL,
    last_access_time      BIGINT       NOT NULL,
    max_inactive_interval INT          NOT NULL,
    expiry_time           BIGINT       NOT NULL,
    principal_name        VARCHAR(100)
);

CREATE UNIQUE INDEX IF NOT EXISTS spring_session_ix1
    ON spring_session (session_id);
CREATE INDEX IF NOT EXISTS spring_session_ix2
    ON spring_session (expiry_time);
CREATE INDEX IF NOT EXISTS spring_session_ix3
    ON spring_session (principal_name);

CREATE TABLE IF NOT EXISTS spring_session_attributes
(
    session_primary_id CHAR(36)     NOT NULL,
    attribute_name     VARCHAR(200) NOT NULL,
    attribute_bytes    BYTEA        NOT NULL,
    PRIMARY KEY (session_primary_id, attribute_name),
    CONSTRAINT spring_session_attributes_fk
        FOREIGN KEY (session_primary_id) REFERENCES spring_session (primary_id)
            ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS login_attempts
(
    attempt_key_hash CHAR(64)    PRIMARY KEY,
    attempt_count    INT         NOT NULL,
    window_start     TIMESTAMPTZ NULL,
    blocked_until    TIMESTAMPTZ NULL,
    updated_at       TIMESTAMPTZ NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_login_attempts_updated_at
    ON login_attempts (updated_at);

CREATE TABLE IF NOT EXISTS scheduled_job_locks
(
    job_name     VARCHAR(120) PRIMARY KEY,
    locked_until TIMESTAMPTZ NOT NULL,
    locked_at    TIMESTAMPTZ NOT NULL,
    locked_by    VARCHAR(120) NOT NULL
);

CREATE TABLE IF NOT EXISTS privileged_audit_events
(
    id             BIGSERIAL    PRIMARY KEY,
    occurred_at    TIMESTAMPTZ  NOT NULL,
    request_id     VARCHAR(80)   NOT NULL,
    actor          VARCHAR(100)  NOT NULL,
    authorities    VARCHAR(500)  NOT NULL,
    http_method    VARCHAR(10)   NOT NULL,
    request_path   VARCHAR(500)  NOT NULL,
    response_status INT          NOT NULL,
    succeeded      BOOLEAN       NOT NULL,
    source_ip_hash CHAR(64)      NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_privileged_audit_occurred_at
    ON privileged_audit_events (occurred_at);
CREATE INDEX IF NOT EXISTS idx_privileged_audit_actor
    ON privileged_audit_events (actor, occurred_at);
