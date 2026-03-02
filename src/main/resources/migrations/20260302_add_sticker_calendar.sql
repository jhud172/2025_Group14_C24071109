-- Sticker Calendar Migration
-- Adds sticker_pack and monthly_workout_target to user_settings

ALTER TABLE user_settings
    ADD COLUMN IF NOT EXISTS sticker_pack VARCHAR(20) NOT NULL DEFAULT 'STARS';

ALTER TABLE user_settings
    ADD COLUMN IF NOT EXISTS monthly_workout_target INT NOT NULL DEFAULT 12;
