-- Add trainer_code column to trainer_profiles for use as login/identification code
ALTER TABLE trainer_profiles
    ADD COLUMN IF NOT EXISTS trainer_code VARCHAR(12) NULL UNIQUE;
