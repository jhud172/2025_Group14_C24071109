drop table if exists selected_preferences;
drop table if exists user_preference_conditions;
drop table if exists user_preferences;
DROP TABLE IF EXISTS record_condition;
DROP TABLE IF EXISTS health_records;
drop table if exists users_roles;
drop table if exists roles;
drop table if exists physical_condition_tag;
DROP TABLE IF EXISTS physical_conditions;
DROP TABLE IF EXISTS exercises_tags;
drop table if exists preference_tag;
drop table if exists preferences;
DROP TABLE IF EXISTS tags;
DROP TABLE IF EXISTS schedule_occurrences;
DROP TABLE IF EXISTS schedule_entries;
drop table if exists schedule_applied;
DROP TABLE IF EXISTS schedules;
DROP TABLE IF EXISTS exercise_log;
DROP TABLE IF EXISTS calendar_tasks;
DROP TABLE IF EXISTS workouts_exercises;
drop table if exists favourites;
DROP TABLE IF EXISTS exercises;
DROP TABLE IF EXISTS workouts;
drop table if exists custom_exercises;
drop table if exists users;


CREATE TABLE if NOT EXISTS users
(
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT not null,
    email               VARCHAR(100)                      not null,
    first_name          VARCHAR(100)                      not null,
    last_name           VARCHAR(100)                      not null,
    username            VARCHAR(100)                      not null,
    password            VARCHAR(500)                      not null,
    enabled             boolean                           not null,
    subscription_status boolean                           not null
) engine = InnoDB;


create table if not exists roles
(
    role_id int(11)     not null auto_increment primary key,
    name    VARCHAR(45) not null
) engine = InnoDB;

CREATE TABLE if not exists users_roles
(
    id       bigint auto_increment primary key,
    username VARCHAR(100) NOT NULL,
    role_id  int(11)      NOT NULL
) engine = InnoDB;


create table if not exists physical_conditions
(
    id   bigint primary key auto_increment not null,
    name VARCHAR(180)
) engine = InnoDB;

create table if not exists health_records
(
    id                       bigint primary key auto_increment not null,
    user_id                  bigint                            not null,
    baseline_date            datetime                          not null,
    systolic_blood_pressure  bigint,
    diastolic_blood_pressure bigint,
    cholesterol              decimal(4, 2),
    weight_kg                bigint                            not null,
    height_cm                bigint                            not null,
    bmi                      decimal(5, 2),
    waist_height_ratio       double,
    waist_cm                 bigint                            not null,
    activity_level           varchar(180),
    foreign key (user_id) references users (id)

) engine = InnoDB;

create table if not exists record_condition
(
    health_record_id      bigint not null,
    physical_condition_id bigint not null,
    primary key (health_record_id, physical_condition_id),
    foreign key (health_record_id) references health_records (id),
    foreign key (physical_condition_id) references physical_conditions (id)
) engine = InnoDB;

CREATE TABLE IF NOT EXISTS exercise_log
(
    id               BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id          BIGINT NOT NULL,
    date             DATE,
    mood_before      INT,
    mood_after       INT,
    confidence       INT,
    comments         VARCHAR(300),
    duration_minutes INT,
    occurrence_id    BIGINT NULL,
    calendar_task_id BIGINT NULL,

    -- Ensures that if a user is deleted, all their health records are also deleted.
    FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE

    -- FOREIGN KEY (occurrence_id) REFERENCES schedule_occurrences (id) ON DELETE SET NULL;

    -- FOREIGN KEY (calendar_task_id) REFERENCES calendar_tasks (id) ON DELETE SET NULL;

) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS exercises
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(200) NOT NULL,
    category    VARCHAR(100),
    description TEXT,
    video_url   VARCHAR(500),
    difficulty  INT,
    type        VARCHAR(50),
    image_url   VARCHAR(500)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS custom_exercises
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id     BIGINT       NOT NULL,
    name        VARCHAR(200) NOT NULL,
    category    VARCHAR(100),
    description TEXT,
    video_url   VARCHAR(500),
    type        VARCHAR(50),
    image_url   VARCHAR(500),

    FOREIGN KEY (user_id) REFERENCES users (id)
        ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS favourites
(
    id                 BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id            BIGINT NOT NULL,
    exercise_id        BIGINT NULL,
    custom_exercise_id BIGINT NULL,

    -- Ensures at least 1 exercise is set
    CHECK (
        (exercise_id IS NOT NULL) OR
        (custom_exercise_id IS NOT NULL)
        ),

    FOREIGN KEY (user_id) REFERENCES users (id)
        ON DELETE CASCADE,
    FOREIGN KEY (exercise_id) REFERENCES exercises (id)
        ON DELETE CASCADE,
    FOREIGN KEY (custom_exercise_id) REFERENCES custom_exercises (id)
        ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS tags
(
    id       BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name     VARCHAR(200) NOT NULL UNIQUE,
    category VARCHAR(200) NOT NULL
);

CREATE TABLE IF NOT EXISTS exercises_tags
(
    id          BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    exercise_id BIGINT NOT NULL,
    tag_id      BIGINT NOT NULL,

    FOREIGN KEY (exercise_id) REFERENCES exercises (id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags (id) ON DELETE CASCADE,

    UNIQUE KEY unique_tagging (exercise_id, tag_id)
);

CREATE VIEW if not exists user_authorities as
select u.username as username, CONCAT('ROLE_', r.name) as authority
from users u
         inner join users_roles ur on u.username = ur.username
         inner join roles r on ur.role_id = r.role_id;


create table if not exists preferences
(
    id          bigint primary key auto_increment not null,
    category    varchar(180),
    description text
) engine = InnoDB;

create table if not exists user_preferences
(
    id      bigint primary key auto_increment not null,
    user_id bigint                            not null,
    foreign key (user_id) references users (id)
) engine = InnoDB;

create table if not exists user_preference_conditions
(
    user_preference_id    bigint not null,
    physical_condition_id bigint not null,
    primary key (user_preference_id, physical_condition_id),
    foreign key (user_preference_id) references user_preferences (id),
    foreign key (physical_condition_id) references physical_conditions (id)
) engine = InnoDB;

create table if not exists selected_preferences
(
    user_preference_id bigint not null,
    preference_id      bigint not null,
    primary key (user_preference_id, preference_id),
    foreign key (user_preference_id) references user_preferences (id),
    foreign key (preference_id) references preferences (id)
) engine = InnoDB;

create table if not exists preference_tag
(
    preference_id bigint not null,
    tag_id        bigint not null,
    primary key (preference_id, tag_id),
    foreign key (preference_id) references preferences (id),
    foreign key (tag_id) references tags (id)
) engine = InnoDB;

create table if not exists physical_condition_tag
(
    physical_condition_id bigint not null,
    tag_id                bigint not null,
    primary key (physical_condition_id, tag_id),
    foreign key (physical_condition_id) references physical_conditions (id),
    foreign key (tag_id) references tags (id)
) engine = InnoDB;


CREATE TABLE IF NOT EXISTS schedules
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id     BIGINT       NOT NULL,
    name        VARCHAR(200) NOT NULL,
    description VARCHAR(500),

    FOREIGN KEY (user_id) REFERENCES users (id)
        ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS schedule_entries
(
    id                 BIGINT PRIMARY KEY AUTO_INCREMENT,
    schedule_id        BIGINT NOT NULL,
    exercise_id        BIGINT NULL,
    custom_exercise_id BIGINT NULL,

    day_of_week        INT    NOT NULL,
    order_number       INT    NOT NULL,

    FOREIGN KEY (schedule_id) REFERENCES schedules (id)
        ON DELETE CASCADE,

    FOREIGN KEY (exercise_id) REFERENCES exercises (id)
        ON DELETE CASCADE,

    FOREIGN KEY (custom_exercise_id) REFERENCES custom_exercises (id)
        ON DELETE SET NULL

) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS schedule_occurrences
(
    id                 BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id            BIGINT       NOT NULL,
    exercise_id        BIGINT       NULL,
    custom_exercise_id BIGINT       NULL,
    schedule_id        BIGINT,
    date               DATE         NOT NULL,
    schedule_name      VARCHAR(200) NOT NULL,
    exercise_log_id    BIGINT       NULL,
    completed          BOOLEAN DEFAULT FALSE,

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

) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS calendar_tasks
(
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
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

    FOREIGN KEY (user_id) REFERENCES users (id)
        ON DELETE CASCADE
);

create table if not exists workouts
(
    id      bigint primary key auto_increment,
    user_id bigint not null,
    name    varchar(200),
    notes   text,

    foreign key (user_id) references users (id)
) engine = InnoDB;

create table if not exists workouts_exercises
(
    workout_id  bigint not null,
    exercise_id bigint not null,

    primary key (workout_id, exercise_id),
    foreign key (workout_id) references workouts (id),
    foreign key (exercise_id) references exercises (id)
) engine = InnoDB;

create table schedule_applied
(
    id           bigint primary key auto_increment,
    schedule_id  bigint not null,
    user_id      bigint not null,
    date_applied date   not null,

    foreign key (schedule_id) references schedules (id),
    foreign key (user_id) references users (id)
);

-- Just to speed up queries
CREATE INDEX IF NOT EXISTS idx_fav_user
    ON favourites (user_id);
CREATE INDEX IF NOT EXISTS idx_custom_user
    ON custom_exercises (user_id);
CREATE INDEX IF NOT EXISTS idx_users_username
    ON users (username);
CREATE INDEX idx_occ_user_date
    ON schedule_occurrences (user_id, date);
CREATE INDEX idx_entry_schedule
    ON schedule_entries (schedule_id);
CREATE INDEX idx_schedule_user
    ON schedules (user_id);

-- Just allows automatic cleanup with CASCADE
ALTER TABLE users_roles
    ADD CONSTRAINT fk_users_roles_username
        FOREIGN KEY (username) REFERENCES users (username)
            ON DELETE CASCADE;

