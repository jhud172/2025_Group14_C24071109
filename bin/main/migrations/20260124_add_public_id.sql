-- Migration: add immutable public_id to users and backfill existing rows (PostgreSQL)

CREATE EXTENSION IF NOT EXISTS pgcrypto;

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS public_id VARCHAR(36);

UPDATE users
SET public_id = COALESCE(public_id, gen_random_uuid()::text);

ALTER TABLE users
    ALTER COLUMN public_id SET NOT NULL;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'uq_users_public_id'
    ) THEN
        ALTER TABLE users
            ADD CONSTRAINT uq_users_public_id UNIQUE (public_id);
    END IF;
END $$;
