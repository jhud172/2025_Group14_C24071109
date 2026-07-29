CREATE TABLE IF NOT EXISTS card_encryption_key_checks
(
    id               SMALLINT    PRIMARY KEY,
    encrypted_marker TEXT        NOT NULL,
    created_at       TIMESTAMPTZ NOT NULL,
    verified_at      TIMESTAMPTZ NOT NULL,
    CONSTRAINT card_encryption_key_check_singleton CHECK (id = 1)
);
