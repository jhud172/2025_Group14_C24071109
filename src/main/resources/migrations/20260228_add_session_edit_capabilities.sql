-- Session-edit capabilities: superset/dropset mode and group key on exercise_sessions
ALTER TABLE exercise_sessions ADD COLUMN IF NOT EXISTS mode      VARCHAR(20)  NOT NULL DEFAULT 'NORMAL';
ALTER TABLE exercise_sessions ADD COLUMN IF NOT EXISTS group_key VARCHAR(100) NULL;
