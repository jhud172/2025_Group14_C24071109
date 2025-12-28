-- =========================
-- DROP (order matters)
-- =========================
DROP VIEW IF EXISTS user_authorities;

DROP TABLE IF EXISTS selected_preferences;
DROP TABLE IF EXISTS user_preference_conditions;
DROP TABLE IF EXISTS user_preferences;
DROP TABLE IF EXISTS record_condition;
DROP TABLE IF EXISTS health_records;
DROP TABLE IF EXISTS users_roles;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS physical_condition_tag;
DROP TABLE IF EXISTS physical_conditions;
DROP TABLE IF EXISTS exercises_tags;
DROP TABLE IF EXISTS preference_tag;
DROP TABLE IF EXISTS preferences;
DROP TABLE IF EXISTS tags;
DROP TABLE IF EXISTS schedule_occurrences;
DROP TABLE IF EXISTS schedule_entries;
DROP TABLE IF EXISTS schedule_applied;
DROP TABLE IF EXISTS schedules;
DROP TABLE IF EXISTS exercise_log;
DROP TABLE IF EXISTS calendar_tasks;
DROP TABLE IF EXISTS workouts_exercises;
DROP TABLE IF EXISTS favourites;
DROP TABLE IF EXISTS exercises;
DROP TABLE IF EXISTS workouts;
DROP TABLE IF EXISTS custom_exercises;
DROP TABLE IF EXISTS users;

-- =========================
-- USERS
-- =========================
CREATE TABLE IF NOT EXISTS users
(
    id                  BIGSERIAL PRIMARY KEY,
    email               VARCHAR(100) NOT NULL,
    first_name          VARCHAR(100) NOT NULL,
    last_name           VARCHAR(100) NOT NULL,
    username            VARCHAR(100) NOT NULL,
    password            VARCHAR(500) NOT NULL,
    enabled             BOOLEAN      NOT NULL,
    subscription_status BOOLEAN      NOT NULL,

    -- Needed because users_roles.username references users.username
    CONSTRAINT uq_users_username UNIQUE (username)
);

-- =========================
-- ROLES + USERS_ROLES
-- =========================
CREATE TABLE IF NOT EXISTS roles
(
    role_id SERIAL PRIMARY KEY,
    name    VARCHAR(45) NOT NULL
);

CREATE TABLE IF NOT EXISTS users_roles
(
    id       BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    role_id  INT          NOT NULL,

    CONSTRAINT fk_users_roles_role
        FOREIGN KEY (role_id) REFERENCES roles (role_id),

    CONSTRAINT fk_users_roles_username
        FOREIGN KEY (username) REFERENCES users (username)
            ON DELETE CASCADE
);

-- =========================
-- PHYSICAL CONDITIONS + HEALTH RECORDS
-- =========================
CREATE TABLE IF NOT EXISTS physical_conditions
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(180)
);

CREATE TABLE IF NOT EXISTS health_records
(
    id                       BIGSERIAL PRIMARY KEY,
    user_id                  BIGINT       NOT NULL,
    baseline_date            TIMESTAMP    NOT NULL,
    systolic_blood_pressure  BIGINT,
    diastolic_blood_pressure BIGINT,
    cholesterol              DECIMAL(4, 2),
    weight_kg                BIGINT       NOT NULL,
    height_cm                BIGINT       NOT NULL,
    bmi                      DECIMAL(5, 2),
    waist_height_ratio       DOUBLE PRECISION,
    waist_cm                 BIGINT       NOT NULL,
    activity_level           VARCHAR(180),

    CONSTRAINT fk_health_records_user
        FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS record_condition
(
    health_record_id      BIGINT NOT NULL,
    physical_condition_id BIGINT NOT NULL,

    PRIMARY KEY (health_record_id, physical_condition_id),

    CONSTRAINT fk_record_condition_record
        FOREIGN KEY (health_record_id) REFERENCES health_records (id),

    CONSTRAINT fk_record_condition_condition
        FOREIGN KEY (physical_condition_id) REFERENCES physical_conditions (id)
);

-- =========================
-- EXERCISE LOG
-- =========================
CREATE TABLE IF NOT EXISTS exercise_log
(
    id               BIGSERIAL PRIMARY KEY,
    user_id          BIGINT NOT NULL,
    date             DATE,
    mood_before      INT,
    mood_after       INT,
    confidence       INT,
    comments         VARCHAR(300),
    duration_minutes INT,
    occurrence_id    BIGINT NULL,
    calendar_task_id BIGINT NULL,

    CONSTRAINT fk_exercise_log_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE

    -- You had these commented out; keeping them commented to match behaviour
    -- ,CONSTRAINT fk_exercise_log_occurrence
    --     FOREIGN KEY (occurrence_id) REFERENCES schedule_occurrences (id) ON DELETE SET NULL
    -- ,CONSTRAINT fk_exercise_log_calendar_task
    --     FOREIGN KEY (calendar_task_id) REFERENCES calendar_tasks (id) ON DELETE SET NULL
);

-- =========================
-- EXERCISES + CUSTOM_EXERCISES
-- =========================
CREATE TABLE IF NOT EXISTS exercises
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(200) NOT NULL,
    category    VARCHAR(100),
    description TEXT,
    video_url   VARCHAR(500),
    difficulty  INT,
    type        VARCHAR(50),
    image_url   VARCHAR(500)
);

CREATE TABLE IF NOT EXISTS custom_exercises
(
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT       NOT NULL,
    name        VARCHAR(200) NOT NULL,
    category    VARCHAR(100),
    description TEXT,
    video_url   VARCHAR(500),
    type        VARCHAR(50),
    image_url   VARCHAR(500),

    CONSTRAINT fk_custom_exercises_user
        FOREIGN KEY (user_id) REFERENCES users (id)
            ON DELETE CASCADE
);

-- =========================
-- FAVOURITES
-- =========================
CREATE TABLE IF NOT EXISTS favourites
(
    id                 BIGSERIAL PRIMARY KEY,
    user_id            BIGINT NOT NULL,
    exercise_id        BIGINT NULL,
    custom_exercise_id BIGINT NULL,

    CONSTRAINT chk_favourites_at_least_one_exercise
        CHECK ((exercise_id IS NOT NULL) OR (custom_exercise_id IS NOT NULL)),

    CONSTRAINT fk_favourites_user
        FOREIGN KEY (user_id) REFERENCES users (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_favourites_exercise
        FOREIGN KEY (exercise_id) REFERENCES exercises (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_favourites_custom_exercise
        FOREIGN KEY (custom_exercise_id) REFERENCES custom_exercises (id)
            ON DELETE CASCADE
);

-- =========================
-- TAGS + EXERCISES_TAGS
-- =========================
CREATE TABLE IF NOT EXISTS tags
(
    id       BIGSERIAL PRIMARY KEY,
    name     VARCHAR(200) NOT NULL UNIQUE,
    category VARCHAR(200) NOT NULL
);

CREATE TABLE IF NOT EXISTS exercises_tags
(
    id          BIGSERIAL PRIMARY KEY,
    exercise_id BIGINT NOT NULL,
    tag_id      BIGINT NOT NULL,

    CONSTRAINT fk_exercises_tags_exercise
        FOREIGN KEY (exercise_id) REFERENCES exercises (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_exercises_tags_tag
        FOREIGN KEY (tag_id) REFERENCES tags (id)
            ON DELETE CASCADE,

    CONSTRAINT unique_tagging UNIQUE (exercise_id, tag_id)
);

-- =========================
-- VIEW: USER_AUTHORITIES
-- =========================
CREATE VIEW user_authorities AS
SELECT
    u.username AS username,
    ('ROLE_' || r.name) AS authority
FROM users u
         JOIN users_roles ur ON u.username = ur.username
         JOIN roles r ON ur.role_id = r.role_id;

-- =========================
-- PREFERENCES
-- =========================
CREATE TABLE IF NOT EXISTS preferences
(
    id          BIGSERIAL PRIMARY KEY,
    category    VARCHAR(180),
    description TEXT
);

CREATE TABLE IF NOT EXISTS user_preferences
(
    id      BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,

    CONSTRAINT fk_user_preferences_user
        FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS user_preference_conditions
(
    user_preference_id    BIGINT NOT NULL,
    physical_condition_id BIGINT NOT NULL,

    PRIMARY KEY (user_preference_id, physical_condition_id),

    CONSTRAINT fk_user_pref_conditions_pref
        FOREIGN KEY (user_preference_id) REFERENCES user_preferences (id),

    CONSTRAINT fk_user_pref_conditions_condition
        FOREIGN KEY (physical_condition_id) REFERENCES physical_conditions (id)
);

CREATE TABLE IF NOT EXISTS selected_preferences
(
    user_preference_id BIGINT NOT NULL,
    preference_id      BIGINT NOT NULL,

    PRIMARY KEY (user_preference_id, preference_id),

    CONSTRAINT fk_selected_preferences_user_pref
        FOREIGN KEY (user_preference_id) REFERENCES user_preferences (id),

    CONSTRAINT fk_selected_preferences_preference
        FOREIGN KEY (preference_id) REFERENCES preferences (id)
);

CREATE TABLE IF NOT EXISTS preference_tag
(
    preference_id BIGINT NOT NULL,
    tag_id        BIGINT NOT NULL,

    PRIMARY KEY (preference_id, tag_id),

    CONSTRAINT fk_preference_tag_preference
        FOREIGN KEY (preference_id) REFERENCES preferences (id),

    CONSTRAINT fk_preference_tag_tag
        FOREIGN KEY (tag_id) REFERENCES tags (id)
);

CREATE TABLE IF NOT EXISTS physical_condition_tag
(
    physical_condition_id BIGINT NOT NULL,
    tag_id                BIGINT NOT NULL,

    PRIMARY KEY (physical_condition_id, tag_id),

    CONSTRAINT fk_physical_condition_tag_condition
        FOREIGN KEY (physical_condition_id) REFERENCES physical_conditions (id),

    CONSTRAINT fk_physical_condition_tag_tag
        FOREIGN KEY (tag_id) REFERENCES tags (id)
);

-- =========================
-- SCHEDULES
-- =========================
CREATE TABLE IF NOT EXISTS schedules
(
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT       NOT NULL,
    name        VARCHAR(200) NOT NULL,
    description VARCHAR(500),

    CONSTRAINT fk_schedules_user
        FOREIGN KEY (user_id) REFERENCES users (id)
            ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS schedule_entries
(
    id                 BIGSERIAL PRIMARY KEY,
    schedule_id        BIGINT NOT NULL,
    exercise_id        BIGINT NULL,
    custom_exercise_id BIGINT NULL,

    day_of_week        INT    NOT NULL,
    order_number       INT    NOT NULL,

    CONSTRAINT fk_schedule_entries_schedule
        FOREIGN KEY (schedule_id) REFERENCES schedules (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_schedule_entries_exercise
        FOREIGN KEY (exercise_id) REFERENCES exercises (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_schedule_entries_custom_exercise
        FOREIGN KEY (custom_exercise_id) REFERENCES custom_exercises (id)
            ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS schedule_occurrences
(
    id                 BIGSERIAL PRIMARY KEY,
    user_id            BIGINT       NOT NULL,
    exercise_id        BIGINT       NULL,
    custom_exercise_id BIGINT       NULL,
    schedule_id        BIGINT,
    date               DATE         NOT NULL,
    schedule_name      VARCHAR(200) NOT NULL,
    exercise_log_id    BIGINT       NULL,
    completed          BOOLEAN      DEFAULT FALSE,

    CONSTRAINT fk_schedule_user
        FOREIGN KEY (user_id) REFERENCES users (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_schedule_exercise
        FOREIGN KEY (exercise_id) REFERENCES exercises (id)
            ON DELETE SET NULL,

    CONSTRAINT fk_schedule_custom_exercise
        FOREIGN KEY (custom_exercise_id) REFERENCES custom_exercises (id)
            ON DELETE SET NULL,

    CONSTRAINT fk_schedule_schedule
        FOREIGN KEY (schedule_id) REFERENCES schedules (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_schedule_occurrence_log
        FOREIGN KEY (exercise_log_id) REFERENCES exercise_log (id)
            ON DELETE SET NULL
);

-- =========================
-- CALENDAR TASKS
-- =========================
CREATE TABLE IF NOT EXISTS calendar_tasks
(
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT       NOT NULL,
    date            DATE         NOT NULL,
    title           VARCHAR(200) NOT NULL,
    time            TIME         NULL,
    notes           TEXT         NULL,
    is_exercise     BOOLEAN      NOT NULL DEFAULT FALSE,
    completed       BOOLEAN      NOT NULL DEFAULT FALSE,
    exercise_log_id BIGINT       NULL,
    exercise_name   VARCHAR(200) NULL,
    requires_log    BOOLEAN      NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_calendar_tasks_user
        FOREIGN KEY (user_id) REFERENCES users (id)
            ON DELETE CASCADE
);

-- =========================
-- WORKOUTS
-- =========================
CREATE TABLE IF NOT EXISTS workouts
(
    id      BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name    VARCHAR(200),
    notes   TEXT,

    CONSTRAINT fk_workouts_user
        FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS workouts_exercises
(
    workout_id  BIGINT NOT NULL,
    exercise_id BIGINT NOT NULL,

    PRIMARY KEY (workout_id, exercise_id),

    CONSTRAINT fk_workouts_exercises_workout
        FOREIGN KEY (workout_id) REFERENCES workouts (id),

    CONSTRAINT fk_workouts_exercises_exercise
        FOREIGN KEY (exercise_id) REFERENCES exercises (id)
);

-- =========================
-- SCHEDULE APPLIED
-- =========================
CREATE TABLE IF NOT EXISTS schedule_applied
(
    id           BIGSERIAL PRIMARY KEY,
    schedule_id  BIGINT NOT NULL,
    user_id      BIGINT NOT NULL,
    date_applied DATE   NOT NULL,

    CONSTRAINT fk_schedule_applied_schedule
        FOREIGN KEY (schedule_id) REFERENCES schedules (id),

    CONSTRAINT fk_schedule_applied_user
        FOREIGN KEY (user_id) REFERENCES users (id)
);

-- =========================
-- INDEXES
-- =========================
CREATE INDEX IF NOT EXISTS idx_fav_user
    ON favourites (user_id);

CREATE INDEX IF NOT EXISTS idx_custom_user
    ON custom_exercises (user_id);

CREATE INDEX IF NOT EXISTS idx_users_username
    ON users (username);

CREATE INDEX IF NOT EXISTS idx_occ_user_date
    ON schedule_occurrences (user_id, date);

CREATE INDEX IF NOT EXISTS idx_entry_schedule
    ON schedule_entries (schedule_id);

CREATE INDEX IF NOT EXISTS idx_schedule_user
    ON schedules (user_id);
