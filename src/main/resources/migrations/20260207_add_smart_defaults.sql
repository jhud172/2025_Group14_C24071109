ALTER TABLE user_settings
    ADD COLUMN IF NOT EXISTS default_sets INT NOT NULL DEFAULT 3;

ALTER TABLE user_settings
    ADD COLUMN IF NOT EXISTS default_rep_min INT NOT NULL DEFAULT 8;

ALTER TABLE user_settings
    ADD COLUMN IF NOT EXISTS default_rep_max INT NOT NULL DEFAULT 12;

ALTER TABLE user_settings
    ADD COLUMN IF NOT EXISTS preferred_equipment_bodyweight BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE user_settings
    ADD COLUMN IF NOT EXISTS preferred_equipment_dumbbell BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE user_settings
    ADD COLUMN IF NOT EXISTS preferred_equipment_barbell BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE user_settings
    ADD COLUMN IF NOT EXISTS preferred_equipment_machine BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE user_settings
    ADD COLUMN IF NOT EXISTS preferred_equipment_bands BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE user_settings
    ADD COLUMN IF NOT EXISTS preferred_equipment_kettlebell BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE user_settings
    ADD COLUMN IF NOT EXISTS macro_target_calories INT NULL;

ALTER TABLE user_settings
    ADD COLUMN IF NOT EXISTS macro_target_protein INT NULL;

ALTER TABLE user_settings
    ADD COLUMN IF NOT EXISTS macro_target_carbs INT NULL;

ALTER TABLE user_settings
    ADD COLUMN IF NOT EXISTS macro_target_fat INT NULL;
