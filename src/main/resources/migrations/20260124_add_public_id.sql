-- Migration: add immutable public_id to users and backfill existing rows (PostgreSQL)

-- pgcrypto extension for PostgreSQL - skipped for H2 compatibility
-- CREATE EXTENSION IF NOT EXISTS pgcrypto;

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS public_id VARCHAR(36);

-- For PostgreSQL (using gen_random_uuid):
-- UPDATE users SET public_id = COALESCE(public_id, gen_random_uuid()::text);
-- For H2 (using RANDOM_UUID):
UPDATE users
SET public_id = COALESCE(public_id, RANDOM_UUID());

ALTER TABLE users
    ALTER COLUMN public_id SET NOT NULL;

-- Note: H2 doesn't support DO blocks, skipping constraint check
-- For PostgreSQL, use:
-- DO $$
-- BEGIN
--     IF NOT EXISTS (
--         SELECT 1
--         FROM pg_constraint
--         WHERE conname = 'uq_users_public_id'
--     ) THEN
--         ALTER TABLE users
--             ADD CONSTRAINT uq_users_public_id UNIQUE (public_id);
--     END IF;
-- END $$;

-- For H2, simple approach:
ALTER TABLE users
    ADD CONSTRAINT IF NOT EXISTS uq_users_public_id UNIQUE (public_id);
