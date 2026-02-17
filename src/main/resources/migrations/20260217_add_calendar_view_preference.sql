ALTER TABLE user_settings
    ADD COLUMN IF NOT EXISTS calendar_view_preference VARCHAR(10) NOT NULL DEFAULT 'MONTH';
