-- Workout Template System Migration
-- Adds workout_ui_templates, workout_player_sessions, workout_session_exercises,
-- workout_session_sets tables and extends user_settings with template preferences.

CREATE TABLE IF NOT EXISTS workout_ui_templates
(
    id          BIGSERIAL    PRIMARY KEY,
    user_id     BIGINT       NULL,
    name        VARCHAR(200) NOT NULL,
    layout_type VARCHAR(30)  NOT NULL DEFAULT 'FLOW',
    config_json TEXT         NULL,
    is_default  BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_workout_ui_templates_user
        FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX IF NOT EXISTS idx_workout_ui_templates_user
    ON workout_ui_templates (user_id);

CREATE TABLE IF NOT EXISTS workout_template_sessions
(
    id                           BIGSERIAL     PRIMARY KEY,
    user_id                      BIGINT        NOT NULL,
    workout_id                   BIGINT        NOT NULL,
    template_id                  BIGINT        NULL,
    template_name_snapshot       VARCHAR(200)  NULL,
    config_json_snapshot         TEXT          NULL,
    started_at                   TIMESTAMP     NULL,
    ended_at                     TIMESTAMP     NULL,
    mood_before                  INT           NULL,
    mood_after                   INT           NULL,
    confidence                   INT           NULL,
    total_volume                 DECIMAL(10,2) NULL,
    status                       VARCHAR(20)   NOT NULL DEFAULT 'IN_PROGRESS',
    allow_completed_without_log  BOOLEAN       NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_workout_template_sessions_user
        FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_workout_template_sessions_workout
        FOREIGN KEY (workout_id) REFERENCES workouts (id),
    CONSTRAINT fk_workout_template_sessions_template
        FOREIGN KEY (template_id) REFERENCES workout_ui_templates (id)
);

CREATE INDEX IF NOT EXISTS idx_workout_template_sessions_user
    ON workout_template_sessions (user_id, started_at);

CREATE TABLE IF NOT EXISTS workout_session_exercises
(
    id                 BIGSERIAL    PRIMARY KEY,
    session_id         BIGINT       NOT NULL,
    exercise_id        BIGINT       NULL,
    custom_exercise_id BIGINT       NULL,
    order_index        INT          NOT NULL DEFAULT 0,
    mode               VARCHAR(20)  NOT NULL DEFAULT 'NORMAL',
    group_key          VARCHAR(100) NULL,
    notes              VARCHAR(1000) NULL,
    CONSTRAINT fk_workout_session_exercises_session
        FOREIGN KEY (session_id) REFERENCES workout_template_sessions (id)
);

CREATE INDEX IF NOT EXISTS idx_workout_session_exercises_session
    ON workout_session_exercises (session_id, order_index);

CREATE TABLE IF NOT EXISTS workout_session_sets
(
    id                  BIGSERIAL    PRIMARY KEY,
    session_exercise_id BIGINT       NOT NULL,
    set_index           INT          NOT NULL DEFAULT 0,
    reps                INT          NULL,
    weight              DECIMAL(8,2) NULL,
    rpe                 DECIMAL(4,1) NULL,
    tempo               VARCHAR(20)  NULL,
    is_drop             BOOLEAN      NOT NULL DEFAULT FALSE,
    completed_at        TIMESTAMP    NULL,
    CONSTRAINT fk_workout_session_sets_exercise
        FOREIGN KEY (session_exercise_id) REFERENCES workout_session_exercises (id)
);

CREATE INDEX IF NOT EXISTS idx_workout_session_sets_exercise
    ON workout_session_sets (session_exercise_id, set_index);

-- Extend user_settings with workout template preferences
ALTER TABLE user_settings
    ADD COLUMN IF NOT EXISTS preferred_workout_template_id BIGINT NULL;

ALTER TABLE user_settings
    ADD COLUMN IF NOT EXISTS hide_ai_one_shot_warning BOOLEAN NOT NULL DEFAULT FALSE;
