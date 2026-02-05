ALTER TABLE users
    ADD COLUMN IF NOT EXISTS email_verified_at TIMESTAMP NULL;

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS phone_verified_at TIMESTAMP NULL;

ALTER TABLE phone_verification_codes
    ALTER COLUMN code TYPE VARCHAR(120);
