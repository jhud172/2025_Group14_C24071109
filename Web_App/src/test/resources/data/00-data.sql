-- NOTE: Spring's `classpath:data/*.sql` resource loading order is not guaranteed.
-- This file contains ALL test seed data so that SQL init is deterministic.

-- Clear dependent tables first (FK-safe order)
delete from schedule_occurrences;
delete from schedule_entries;
delete from schedules;

delete from calendar_tasks;

delete from workouts_exercises;
delete from favourites;

delete from workouts;
delete from custom_exercises;

delete from preference_tag;
delete from physical_condition_tag;
delete from exercises_tags;
delete from physical_conditions;
delete from users_roles;
delete from user_preferences;
delete from users;
delete from roles;
delete from preferences;
delete from tags;

-- Physical condition data
insert into physical_conditions(name)
values ('Arthritis');
insert into physical_conditions(name)
values ('Knee pain');
insert into physical_conditions(name)
values ('Back pain');
insert into physical_conditions(name)
values ('Chronic pain');
insert into physical_conditions(name)
values ('Diabetes');
insert into physical_conditions(name)
values ('Asthma');

-- Test user
INSERT INTO users (public_id, email, email_verified, email_verified_at, first_name, last_name, username, password, enabled, subscription_status, role)
VALUES ('c4562d75-0c7f-4f3d-8c35-0e4a5f0a6c2b', 'Jane@gmail.com', true, CURRENT_TIMESTAMP, 'Jane', 'Doe', 'user', '$2a$12$yOTznIO3eJBYmFckkJM.Xuu2qy59Rg4CQUhIrGpHzseNGj5KI5MSO', true, false, 'CLIENT');
INSERT INTO users (public_id, email, email_verified, email_verified_at, first_name, last_name, username, password, enabled, subscription_status, role)
VALUES ('a6cc7a41-9b0c-4b9a-9b19-9cdd2b1c3f58', 'John@gmail.com', true, CURRENT_TIMESTAMP, 'John', 'Doe', 'user2', '$2a$12$yOTznIO3eJBYmFckkJM.Xuu2qy59Rg4CQUhIrGpHzseNGj5KI5MSO', true, false, 'CLIENT');

-- Demo credentials (used by login integration test):
--   username: demo
--   password: Demo123!
INSERT INTO users (public_id, email, email_verified, email_verified_at, first_name, last_name, username, password, enabled, subscription_status, role)
VALUES ('f0d3b90c-65b6-4d78-a7ae-09a1639fd8a2', 'demo@example.com', true, CURRENT_TIMESTAMP, 'Demo', 'User', 'demo', '$2a$10$2EZk8xjJekcabhOOKPsxtuHWvgrgWunYC2v57bCNiEk8c8HxHedH6', true, false, 'CLIENT');

INSERT INTO users (public_id, email, email_verified, email_verified_at, first_name, last_name, username, password, enabled, subscription_status, role, trainer_verified)
VALUES ('76bce96e-54a4-47ee-a7fd-9ad2dfcfeb62', 'trainer_demo@example.com', true, CURRENT_TIMESTAMP, 'Trainer', 'Demo', 'trainer_demo', '$2a$10$2EZk8xjJekcabhOOKPsxtuHWvgrgWunYC2v57bCNiEk8c8HxHedH6', true, true, 'TRAINER', true);

INSERT INTO users (public_id, email, email_verified, email_verified_at, first_name, last_name, username, password, enabled, subscription_status, role)
VALUES ('1c24ee40-044f-4bf6-9b9e-2a8d34f31330', 'gymadmin_demo@example.com', true, CURRENT_TIMESTAMP, 'Gym', 'Admin', 'gymadmin_demo', '$2a$10$2EZk8xjJekcabhOOKPsxtuHWvgrgWunYC2v57bCNiEk8c8HxHedH6', true, true, 'GYM_ADMIN');

INSERT INTO users (public_id, email, email_verified, email_verified_at, first_name, last_name, username, password, enabled, subscription_status, role)
VALUES ('da7b6a5f-d203-4a3d-8b50-134d1843a466', 'admin_demo@example.com', true, CURRENT_TIMESTAMP, 'Platform', 'Admin', 'admin_demo', '$2a$10$2EZk8xjJekcabhOOKPsxtuHWvgrgWunYC2v57bCNiEk8c8HxHedH6', true, true, 'PLATFORM_ADMIN');

INSERT INTO users (public_id, email, email_verified, email_verified_at, first_name, last_name, username, password, enabled, subscription_status, role)
VALUES ('cb3d373e-8f3b-4957-a2bf-53ef4c0fe538', 'superadmin_demo@example.com', true, CURRENT_TIMESTAMP, 'Super', 'Admin', 'superadmin_demo', '$2a$10$2EZk8xjJekcabhOOKPsxtuHWvgrgWunYC2v57bCNiEk8c8HxHedH6', true, true, 'SUPER_ADMIN');

-- Roles
insert into roles(role_id, name)
values (1, 'USER');
insert into roles(role_id, name)
values (2, 'CLIENT');
insert into roles(role_id, name)
values (3, 'TRAINER');
insert into roles(role_id, name)
values (4, 'GYM_ADMIN');
insert into roles(role_id, name)
values (5, 'PLATFORM_ADMIN');
insert into roles(role_id, name)
values (6, 'SUPER_ADMIN');

-- Links user to a role
insert into users_roles (username, role_id)
values ('user', 1);
insert into users_roles (username, role_id)
values ('user', 2);
insert into users_roles (username, role_id)
values ('user2', 1);
insert into users_roles (username, role_id)
values ('user2', 2);
insert into users_roles (username, role_id)
values ('demo', 1);
insert into users_roles (username, role_id)
values ('demo', 2);
insert into users_roles (username, role_id)
values ('trainer_demo', 1);
insert into users_roles (username, role_id)
values ('trainer_demo', 3);
insert into users_roles (username, role_id)
values ('gymadmin_demo', 1);
insert into users_roles (username, role_id)
values ('gymadmin_demo', 4);
insert into users_roles (username, role_id)
values ('admin_demo', 1);
insert into users_roles (username, role_id)
values ('admin_demo', 5);
insert into users_roles (username, role_id)
values ('superadmin_demo', 1);
insert into users_roles (username, role_id)
values ('superadmin_demo', 6);

INSERT INTO trainer_profiles (user_id, trainer_code, created_at, updated_at)
VALUES ((SELECT id FROM users WHERE username = 'trainer_demo'), '120340056789', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO gym_profiles (user_id, gym_name, gym_code, created_at, updated_at)
VALUES ((SELECT id FROM users WHERE username = 'gymadmin_demo'), 'Demo Gym', '4827001938456202', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Preference data
insert into preferences(category, description)
values ('Finance', 'Free');
insert into preferences(category, description)
values ('Finance', 'Paid');
insert into preferences(category, description)
values ('Location', 'Home');
insert into preferences(category, description)
values ('Location', 'Outside');
insert into preferences(category, description)
values ('Location', 'Gym');
insert into preferences(category, description)
values ('Social', 'Solo');
insert into preferences(category, description)
values ('Social', 'Group');
insert into preferences(category, description)
values ('Equipment', 'Dumbbells');
insert into preferences(category, description)
values ('Equipment', 'Weights');
insert into preferences(category, description)
values ('Equipment', 'Kettlebells');
insert into preferences(category, description)
values ('Equipment', 'Barbell');

-- Tags
insert into tags(name, category)
values ('High Intensity', 'Intensity');
insert into tags(name, category)
values ('Barbell', 'Equipment');
insert into tags(name, category)
values ('Free', 'Finance');
insert into tags(name, category)
values ('Paid', 'Finance');
insert into tags(name, category)
values ('Gym', 'Location');
insert into tags(name, category)
values ('Home', 'Location');
insert into tags(name, category)
values ('Outside', 'Location');
insert into tags(name, category)
values ('Solo', 'Social');
insert into tags(name, category)
values ('Group', 'Social');
insert into tags(name, category)
values ('Dumbbells', 'Equipment');
insert into tags(name, category)
values ('Weights', 'Equipment');
insert into tags(name, category)
values ('Kettlebells', 'Equipment');
insert into tags(name, category)
values ('High impact', 'Impact');


-- Tags and preference relation
insert into preference_tag(preference_id, tag_id)
values ((select id from preferences where description = 'Free'),
        (select id from tags where name = 'Free'));
insert into preference_tag(preference_id, tag_id)
values ((select id from preferences where description = 'Paid'),
        (select id from tags where name = 'Paid'));
insert into preference_tag(preference_id, tag_id)
values ((select id from preferences where description = 'Home'),
        (select id from tags where name = 'Home'));
insert into preference_tag(preference_id, tag_id)
values ((select id from preferences where description = 'Outside'),
        (select id from tags where name = 'Outside'));
insert into preference_tag(preference_id, tag_id)
values ((select id from preferences where description = 'Gym'),
        (select id from tags where name = 'Gym'));
insert into preference_tag(preference_id, tag_id)
values ((select id from preferences where description = 'Solo'),
        (select id from tags where name = 'Solo'));
insert into preference_tag(preference_id, tag_id)
values ((select id from preferences where description = 'Barbell'),
        (select id from tags where name = 'Barbell'));

-- Tags and physical condition relation
insert into physical_condition_tag(physical_condition_id, tag_id)
values ((select id from physical_conditions where name = 'Asthma'),
        (select id from tags where name = 'High Intensity'));
insert into physical_condition_tag(physical_condition_id, tag_id)
values ((select id from physical_conditions where name = 'Arthritis'),
        (select id from tags where name = 'High impact'));


-- Set up user preferences
insert into user_preferences(id, user_id)
values (4, 1);

-- =========================================
-- EXERCISES (kept small for tests)
-- =========================================

INSERT INTO exercises (id, name, category, description, video_url, difficulty, type, image_url)
VALUES
        (1, 'Squat', 'Strength', 'Strengthen legs and core', 'https://www.youtube.com/embed/xqvCmoLULNY', 3, 'strength', NULL),
        (2, 'Hamstring Stretch', 'Flexibility', 'Stretches the hamstring muscles', 'https://www.youtube.com/embed/RxEd4pN7CDs', 1, 'stretch', NULL),
        (3, 'Ankle Mobility Lean', 'Mobility', 'Improves dorsiflexion', NULL, 1, 'mobility', NULL),
        (4, 'Wall Sit', 'Strength', 'Isometric leg strength hold', 'https://www.youtube.com/embed/cWTZ8Am1Ee0', 2, 'strength', NULL),
        (5, 'March in Place', 'Cardio', 'Low-impact warm-up cardio', 'https://www.youtube.com/embed/9wl_AiNhYP0', 1, 'cardio', NULL);

-- Tags and exercises relation
insert into exercises_tags(exercise_id, tag_id)
values ((select id from exercises where name = 'Squat'),
                (select id from tags where name = 'Free'));
insert into exercises_tags(exercise_id, tag_id)
values ((select id from exercises where name = 'Squat'),
                (select id from tags where name = 'Barbell'));
insert into exercises_tags(exercise_id, tag_id)
values ((select id from exercises where name = 'Squat'),
                (select id from tags where name = 'Home'));
insert into exercises_tags(exercise_id, tag_id)
values ((select id from exercises where name = 'Hamstring Stretch'),
                (select id from tags where name = 'Free'));
insert into exercises_tags(exercise_id, tag_id)
values ((select id from exercises where name = 'Hamstring Stretch'),
                (select id from tags where name = 'Home'));
insert into exercises_tags(exercise_id, tag_id)
values ((select id from exercises where name = 'Hamstring Stretch'),
                (select id from tags where name = 'Solo'));

-- =========================================
-- CUSTOM EXERCISES
-- =========================================

INSERT INTO custom_exercises (id, user_id, name, category, description, video_url, type, image_url)
VALUES
        (1, 1, 'Resistance Band Row', 'Strength', 'Pull with a band', NULL, 'strength', NULL),
        (2, 1, 'Chair Balance Practice', 'Balance', 'Light balance exercise', NULL, 'balance', NULL),
        (3, 2, 'Band Pull Apart', 'Strength', 'Rear delt + upper back', NULL, 'strength', NULL);

-- =========================================
-- WORKOUTS
-- =========================================

-- USER 1 WORKOUTS
INSERT INTO workouts (id, user_id, name, notes) VALUES
        (1, 1, 'Push Day A', 'Chest + shoulders + triceps'),
        (2, 1, 'Pull Day A', 'Back + biceps'),
        (3, 1, 'Legs Day A', 'Quads + hamstrings + glutes'),
        (4, 1, 'Push Day B', 'Variation set — incline focus'),
        (5, 1, 'Pull Day B', 'More rowing patterns'),
        (6, 1, 'Legs Day B', 'Glute and hamstring dominant'),
        (7, 1, 'Upper Body Strength', 'Heavy compound day'),
        (8, 1, 'Lower Body Strength', 'Heavy squats & deadlifts'),
        (9, 1, 'Chest & Arms', 'Isolation pump session'),
        (10, 1, 'Back & Shoulders', 'Upper posterior chain'),
        (11, 1, 'HIIT Circuit', '20-min high-intensity circuit'),
        (12, 1, 'Core & Stability', 'Abs + balance work');

-- USER 2 WORKOUTS
INSERT INTO workouts (id, user_id, name, notes) VALUES
        (13, 2, 'Mobility Routine A', 'Daily morning mobility'),
        (14, 2, 'Mobility Routine B', 'Evening lower-body mobility'),
        (15, 2, 'Light Full Body A', 'Daily physio-approved conditioning'),
        (16, 2, 'Light Full Body B', 'Slightly harder progression'),
        (17, 2, 'Rehab – Lower Back', 'Strengthening hip + core'),
        (18, 2, 'Rehab – Knee', 'Patellar tendon & quad stability'),
        (19, 2, 'Cardio & Stretch', 'Low-intensity recovery day'),
        (20, 2, 'Beginner Strength', 'Learning basic compound lifts');

-- Keep workout→exercise links within the seeded exercise IDs (1..5)
INSERT INTO workouts_exercises (workout_id, exercise_id) VALUES
        (1, 1), (1, 4), (1, 5),
        (2, 1), (2, 3), (2, 5),
        (3, 1), (3, 2), (3, 4),
        (4, 1), (4, 4), (4, 5),
        (5, 1), (5, 3), (5, 4),
        (6, 1), (6, 2), (6, 5),
        (7, 1), (7, 4), (7, 5),
        (8, 1), (8, 2), (8, 4),
        (9, 1), (9, 4), (9, 5),
        (10, 1), (10, 3), (10, 4),
        (11, 1), (11, 4), (11, 5),
        (12, 2), (12, 3), (12, 5),
        (13, 2), (13, 3), (13, 5),
        (14, 2), (14, 3), (14, 4),
        (15, 1), (15, 4), (15, 5),
        (16, 1), (16, 3), (16, 5),
        (17, 1), (17, 2), (17, 3),
        (18, 1), (18, 2), (18, 4),
        (19, 2), (19, 3), (19, 5),
        (20, 1), (20, 4), (20, 5);

-- =========================================
-- FAVOURITES
-- =========================================

INSERT INTO favourites (id, user_id, exercise_id, custom_exercise_id)
VALUES
        (1, 1, 1, NULL),
        (2, 1, 3, NULL),
        (3, 2, NULL, 3);

-- =========================================
-- SCHEDULE (minimal demo)
-- =========================================

-- Two schedules, one per user
INSERT INTO schedules (id, user_id, name, description)
VALUES
        (1, 1, 'Morning Routine', 'Quick morning exercises'),
        (2, 2, 'Rehab Plan', 'Light band work');

-- Template entries for schedule 1 (user 1)
-- Monday = 1, Wednesday = 3, Friday = 5
INSERT INTO schedule_entries (id, schedule_id, exercise_id, day_of_week, order_number)
VALUES
        (1, 1, 1, 1, 1),
        (2, 1, 2, 3, 1),
        (3, 1, 3, 5, 1);

-- Template entries for schedule 2 (user 2)
INSERT INTO schedule_entries (id, schedule_id, exercise_id, day_of_week, order_number)
VALUES
        (4, 2, 3, 2, 1),
        (5, 2, 1, 4, 1);

-- A few pre-generated occurrences to prove it works
INSERT INTO schedule_occurrences (id, user_id, exercise_id, date, schedule_name)
VALUES
        (1, 1, 1, '2025-01-06', 'Morning Routine'),
        (2, 1, 2, '2025-01-08', 'Morning Routine'),
        (3, 2, 3, '2025-01-07', 'Rehab Plan');

-- =========================================
-- CALENDAR TASKS
-- =========================================

INSERT INTO calendar_tasks (id, user_id, date, time, title, notes, is_exercise, completed)
VALUES
        (1, 1, '2025-11-06', '14:00:00', 'Eat Lunch', 'Chicken wrap + protein shake', FALSE, TRUE),
        (2, 1, '2025-11-06', '18:30:00', 'Evening Walk', '30 minutes around the park', TRUE, FALSE),
        (3, 1, '2025-11-07', NULL, 'Clean Desk Area', 'Sort cables + wipe surfaces', FALSE, FALSE),
        (4, 1, '2025-11-08', '09:00:00', 'Study Session', 'Focus on Java Spring Boot', FALSE, FALSE),
        (5, 1, '2025-11-08', '20:00:00', 'Call Family', NULL, FALSE, FALSE),
        (11, 1, '2025-11-06', '16:00:00', 'TEST 1', 'blehhh', FALSE, FALSE),
        (12, 1, '2025-11-06', '18:00:00', 'TEST 2', 'blahhh', FALSE, FALSE),
        (6, 2, '2025-11-06', '13:00:00', 'Physio Stretching', 'Lower back routine', TRUE, FALSE),
        (7, 2, '2025-11-07', NULL, 'Buy Groceries', 'Eggs, rice, peanut butter', FALSE, FALSE),
        (8, 2, '2025-11-08', '16:45:00', 'Meal Prep', '3x chicken + rice portions', FALSE, FALSE),
        (9, 2, '2025-11-09', '10:30:00', 'Medium Walk', '30 minutes, physio-approved', TRUE, FALSE),
        (10, 2, '2025-11-09', '19:00:00', 'Watch Documentary', 'Any fitness science one', FALSE, FALSE);

-- Explicit fixture IDs do not advance H2 identity counters automatically.
-- Keep generated IDs above the seeded rows so repository/service tests can insert safely.
ALTER TABLE roles ALTER COLUMN role_id RESTART WITH 7;
ALTER TABLE user_preferences ALTER COLUMN id RESTART WITH 5;
ALTER TABLE exercises ALTER COLUMN id RESTART WITH 6;
ALTER TABLE custom_exercises ALTER COLUMN id RESTART WITH 4;
ALTER TABLE workouts ALTER COLUMN id RESTART WITH 21;
ALTER TABLE favourites ALTER COLUMN id RESTART WITH 4;
ALTER TABLE schedules ALTER COLUMN id RESTART WITH 3;
ALTER TABLE schedule_entries ALTER COLUMN id RESTART WITH 6;
ALTER TABLE schedule_occurrences ALTER COLUMN id RESTART WITH 4;
ALTER TABLE calendar_tasks ALTER COLUMN id RESTART WITH 13;
