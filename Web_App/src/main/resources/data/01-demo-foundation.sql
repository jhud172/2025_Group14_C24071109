-- Consolidated demo foundation seed
-- Merged from:
--   01-demo-calendar.sql
--   02-demo-full-user-data.sql
-- Purpose:
--   shared exercises, base client workouts, schedules, and current relative timeline data.

-- ========================================================================
-- BEGIN 01-demo-calendar.sql
-- ========================================================================

-- Demo calendar seed for user 'demo'
-- Range: CURRENT_DATE - 7 .. CURRENT_DATE + 7 (inclusive)
-- Guarantees: each day has at least one item (either a calendar task or a schedule occurrence)
-- Also seeds exercises, workouts, and some workout_sessions.

-- -------------------------
-- EXERCISES (shared catalog)
-- -------------------------
INSERT INTO exercises (name, category, description, difficulty, type)
SELECT 'Walk', 'Cardio', 'Easy steady movement.', 1, 'cardio'
WHERE NOT EXISTS (SELECT 1 FROM exercises WHERE name = 'Walk');

INSERT INTO exercises (name, category, description, difficulty, type)
SELECT 'Run', 'Cardio', 'Moderate cardio session.', 2, 'cardio'
WHERE NOT EXISTS (SELECT 1 FROM exercises WHERE name = 'Run');

INSERT INTO exercises (name, category, description, difficulty, type)
SELECT 'Push-up', 'Strength', 'Upper body push movement.', 2, 'strength'
WHERE NOT EXISTS (SELECT 1 FROM exercises WHERE name = 'Push-up');

INSERT INTO exercises (name, category, description, difficulty, type)
SELECT 'Bodyweight Squat', 'Strength', 'Lower body squat pattern.', 2, 'strength'
WHERE NOT EXISTS (SELECT 1 FROM exercises WHERE name = 'Bodyweight Squat');

INSERT INTO exercises (name, category, description, difficulty, type)
SELECT 'Plank', 'Core', 'Core stability hold.', 1, 'strength'
WHERE NOT EXISTS (SELECT 1 FROM exercises WHERE name = 'Plank');

INSERT INTO exercises (name, category, description, difficulty, type)
SELECT 'Burpee', 'Conditioning', 'Full-body conditioning movement.', 3, 'cardio'
WHERE NOT EXISTS (SELECT 1 FROM exercises WHERE name = 'Burpee');

INSERT INTO exercises (name, category, description, difficulty, type)
SELECT 'Dumbbell Row', 'Strength', 'Upper body pull movement.', 2, 'strength'
WHERE NOT EXISTS (SELECT 1 FROM exercises WHERE name = 'Dumbbell Row');

-- -------------------------
-- WORKOUTS (Strength Log)
-- -------------------------
INSERT INTO workouts (user_id, name, notes)
SELECT (SELECT id FROM users WHERE username = 'demo'), 'Demo Full Body', 'A simple full-body session for the demo user.'
WHERE EXISTS (SELECT 1 FROM users WHERE username = 'demo')
  AND NOT EXISTS (
    SELECT 1 FROM workouts
    WHERE user_id = (SELECT id FROM users WHERE username = 'demo')
      AND name = 'Demo Full Body'
  );

INSERT INTO workouts (user_id, name, notes)
SELECT (SELECT id FROM users WHERE username = 'demo'), 'Demo Cardio Blast', 'A short cardio-focused session for the demo user.'
WHERE EXISTS (SELECT 1 FROM users WHERE username = 'demo')
  AND NOT EXISTS (
    SELECT 1 FROM workouts
    WHERE user_id = (SELECT id FROM users WHERE username = 'demo')
      AND name = 'Demo Cardio Blast'
  );

-- -------------------------
-- WORKOUT SCHEDULE (so Day View can show “Scheduled Workouts”)
-- Uses ISO day-of-week 1=Mon .. 7=Sun
-- -------------------------
INSERT INTO workout_schedule (user_id, day_of_week, workout_id, order_index)
SELECT
  (SELECT id FROM users WHERE username = 'demo'),
  1,
  (SELECT id FROM workouts WHERE user_id = (SELECT id FROM users WHERE username = 'demo') AND name = 'Demo Full Body'),
  0
WHERE EXISTS (SELECT 1 FROM users WHERE username = 'demo')
  AND NOT EXISTS (
    SELECT 1 FROM workout_schedule
    WHERE user_id = (SELECT id FROM users WHERE username = 'demo')
      AND day_of_week = 1
      AND workout_id = (SELECT id FROM workouts WHERE user_id = (SELECT id FROM users WHERE username = 'demo') AND name = 'Demo Full Body')
  );

INSERT INTO workout_schedule (user_id, day_of_week, workout_id, order_index)
SELECT
  (SELECT id FROM users WHERE username = 'demo'),
  3,
  (SELECT id FROM workouts WHERE user_id = (SELECT id FROM users WHERE username = 'demo') AND name = 'Demo Full Body'),
  0
WHERE EXISTS (SELECT 1 FROM users WHERE username = 'demo')
  AND NOT EXISTS (
    SELECT 1 FROM workout_schedule
    WHERE user_id = (SELECT id FROM users WHERE username = 'demo')
      AND day_of_week = 3
      AND workout_id = (SELECT id FROM workouts WHERE user_id = (SELECT id FROM users WHERE username = 'demo') AND name = 'Demo Full Body')
  );

INSERT INTO workout_schedule (user_id, day_of_week, workout_id, order_index)
SELECT
  (SELECT id FROM users WHERE username = 'demo'),
  5,
  (SELECT id FROM workouts WHERE user_id = (SELECT id FROM users WHERE username = 'demo') AND name = 'Demo Cardio Blast'),
  0
WHERE EXISTS (SELECT 1 FROM users WHERE username = 'demo')
  AND NOT EXISTS (
    SELECT 1 FROM workout_schedule
    WHERE user_id = (SELECT id FROM users WHERE username = 'demo')
      AND day_of_week = 5
      AND workout_id = (SELECT id FROM workouts WHERE user_id = (SELECT id FROM users WHERE username = 'demo') AND name = 'Demo Cardio Blast')
  );

-- Link workouts to exercises
INSERT INTO workouts_exercises (workout_id, exercise_id)
SELECT
  (SELECT id FROM workouts WHERE user_id = (SELECT id FROM users WHERE username = 'demo') AND name = 'Demo Full Body'),
  (SELECT id FROM exercises WHERE name = 'Bodyweight Squat')
WHERE NOT EXISTS (
  SELECT 1 FROM workouts_exercises
  WHERE workout_id = (SELECT id FROM workouts WHERE user_id = (SELECT id FROM users WHERE username = 'demo') AND name = 'Demo Full Body')
    AND exercise_id = (SELECT id FROM exercises WHERE name = 'Bodyweight Squat')
);

INSERT INTO workouts_exercises (workout_id, exercise_id)
SELECT
  (SELECT id FROM workouts WHERE user_id = (SELECT id FROM users WHERE username = 'demo') AND name = 'Demo Full Body'),
  (SELECT id FROM exercises WHERE name = 'Push-up')
WHERE NOT EXISTS (
  SELECT 1 FROM workouts_exercises
  WHERE workout_id = (SELECT id FROM workouts WHERE user_id = (SELECT id FROM users WHERE username = 'demo') AND name = 'Demo Full Body')
    AND exercise_id = (SELECT id FROM exercises WHERE name = 'Push-up')
);

INSERT INTO workouts_exercises (workout_id, exercise_id)
SELECT
  (SELECT id FROM workouts WHERE user_id = (SELECT id FROM users WHERE username = 'demo') AND name = 'Demo Full Body'),
  (SELECT id FROM exercises WHERE name = 'Plank')
WHERE NOT EXISTS (
  SELECT 1 FROM workouts_exercises
  WHERE workout_id = (SELECT id FROM workouts WHERE user_id = (SELECT id FROM users WHERE username = 'demo') AND name = 'Demo Full Body')
    AND exercise_id = (SELECT id FROM exercises WHERE name = 'Plank')
);

INSERT INTO workouts_exercises (workout_id, exercise_id)
SELECT
  (SELECT id FROM workouts WHERE user_id = (SELECT id FROM users WHERE username = 'demo') AND name = 'Demo Cardio Blast'),
  (SELECT id FROM exercises WHERE name = 'Run')
WHERE NOT EXISTS (
  SELECT 1 FROM workouts_exercises
  WHERE workout_id = (SELECT id FROM workouts WHERE user_id = (SELECT id FROM users WHERE username = 'demo') AND name = 'Demo Cardio Blast')
    AND exercise_id = (SELECT id FROM exercises WHERE name = 'Run')
);

INSERT INTO workouts_exercises (workout_id, exercise_id)
SELECT
  (SELECT id FROM workouts WHERE user_id = (SELECT id FROM users WHERE username = 'demo') AND name = 'Demo Cardio Blast'),
  (SELECT id FROM exercises WHERE name = 'Burpee')
WHERE NOT EXISTS (
  SELECT 1 FROM workouts_exercises
  WHERE workout_id = (SELECT id FROM workouts WHERE user_id = (SELECT id FROM users WHERE username = 'demo') AND name = 'Demo Cardio Blast')
    AND exercise_id = (SELECT id FROM exercises WHERE name = 'Burpee')
);

-- -------------------------
-- SCHEDULES + ENTRIES
-- -------------------------
INSERT INTO schedules (user_id, name, description)
SELECT (SELECT id FROM users WHERE username = 'demo'), 'Demo Daily Movement', 'Light movement / recovery schedule for demo week.'
WHERE EXISTS (SELECT 1 FROM users WHERE username = 'demo')
  AND NOT EXISTS (
    SELECT 1 FROM schedules
    WHERE user_id = (SELECT id FROM users WHERE username = 'demo')
      AND name = 'Demo Daily Movement'
  );

-- A few representative schedule entries (Mon/Wed/Fri)
INSERT INTO schedule_entries (schedule_id, exercise_id, day_of_week, order_number)
SELECT
  (SELECT id FROM schedules WHERE user_id = (SELECT id FROM users WHERE username = 'demo') AND name = 'Demo Daily Movement'),
  (SELECT id FROM exercises WHERE name = 'Walk'),
  1,
  1
WHERE NOT EXISTS (
  SELECT 1 FROM schedule_entries
  WHERE schedule_id = (SELECT id FROM schedules WHERE user_id = (SELECT id FROM users WHERE username = 'demo') AND name = 'Demo Daily Movement')
    AND day_of_week = 1 AND order_number = 1
);

INSERT INTO schedule_entries (schedule_id, exercise_id, day_of_week, order_number)
SELECT
  (SELECT id FROM schedules WHERE user_id = (SELECT id FROM users WHERE username = 'demo') AND name = 'Demo Daily Movement'),
  (SELECT id FROM exercises WHERE name = 'Plank'),
  3,
  1
WHERE NOT EXISTS (
  SELECT 1 FROM schedule_entries
  WHERE schedule_id = (SELECT id FROM schedules WHERE user_id = (SELECT id FROM users WHERE username = 'demo') AND name = 'Demo Daily Movement')
    AND day_of_week = 3 AND order_number = 1
);

INSERT INTO schedule_entries (schedule_id, exercise_id, day_of_week, order_number)
SELECT
  (SELECT id FROM schedules WHERE user_id = (SELECT id FROM users WHERE username = 'demo') AND name = 'Demo Daily Movement'),
  (SELECT id FROM exercises WHERE name = 'Run'),
  5,
  1
WHERE NOT EXISTS (
  SELECT 1 FROM schedule_entries
  WHERE schedule_id = (SELECT id FROM schedules WHERE user_id = (SELECT id FROM users WHERE username = 'demo') AND name = 'Demo Daily Movement')
    AND day_of_week = 5 AND order_number = 1
);

-- -------------------------
-- SCHEDULE OCCURRENCES (odd days) - ensure every odd day has schedule data
-- -------------------------
-- CURRENT_DATE - 7
INSERT INTO schedule_occurrences (user_id, exercise_id, schedule_id, date, schedule_name, completed)
SELECT
  (SELECT id FROM users WHERE username = 'demo'),
  (SELECT id FROM exercises WHERE name = 'Walk'),
  (SELECT id FROM schedules WHERE user_id = (SELECT id FROM users WHERE username = 'demo') AND name = 'Demo Daily Movement'),
  CURRENT_DATE - INTERVAL '7' DAY,
  'Demo Daily Movement',
  FALSE
WHERE NOT EXISTS (
  SELECT 1 FROM schedule_occurrences
  WHERE user_id = (SELECT id FROM users WHERE username = 'demo')
    AND date = CURRENT_DATE - INTERVAL '7' DAY
    AND schedule_name = 'Demo Daily Movement'
);

-- CURRENT_DATE - 5
INSERT INTO schedule_occurrences (user_id, exercise_id, schedule_id, date, schedule_name, completed)
SELECT
  (SELECT id FROM users WHERE username = 'demo'),
  (SELECT id FROM exercises WHERE name = 'Plank'),
  (SELECT id FROM schedules WHERE user_id = (SELECT id FROM users WHERE username = 'demo') AND name = 'Demo Daily Movement'),
  CURRENT_DATE - INTERVAL '5' DAY,
  'Demo Daily Movement',
  FALSE
WHERE NOT EXISTS (
  SELECT 1 FROM schedule_occurrences
  WHERE user_id = (SELECT id FROM users WHERE username = 'demo')
    AND date = CURRENT_DATE - INTERVAL '5' DAY
    AND schedule_name = 'Demo Daily Movement'
);

-- CURRENT_DATE - 3
INSERT INTO schedule_occurrences (user_id, exercise_id, schedule_id, date, schedule_name, completed)
SELECT
  (SELECT id FROM users WHERE username = 'demo'),
  (SELECT id FROM exercises WHERE name = 'Walk'),
  (SELECT id FROM schedules WHERE user_id = (SELECT id FROM users WHERE username = 'demo') AND name = 'Demo Daily Movement'),
  CURRENT_DATE - INTERVAL '3' DAY,
  'Demo Daily Movement',
  FALSE
WHERE NOT EXISTS (
  SELECT 1 FROM schedule_occurrences
  WHERE user_id = (SELECT id FROM users WHERE username = 'demo')
    AND date = CURRENT_DATE - INTERVAL '3' DAY
    AND schedule_name = 'Demo Daily Movement'
);

-- CURRENT_DATE - 1
INSERT INTO schedule_occurrences (user_id, exercise_id, schedule_id, date, schedule_name, completed)
SELECT
  (SELECT id FROM users WHERE username = 'demo'),
  (SELECT id FROM exercises WHERE name = 'Run'),
  (SELECT id FROM schedules WHERE user_id = (SELECT id FROM users WHERE username = 'demo') AND name = 'Demo Daily Movement'),
  CURRENT_DATE - INTERVAL '1' DAY,
  'Demo Daily Movement',
  FALSE
WHERE NOT EXISTS (
  SELECT 1 FROM schedule_occurrences
  WHERE user_id = (SELECT id FROM users WHERE username = 'demo')
    AND date = CURRENT_DATE - INTERVAL '1' DAY
    AND schedule_name = 'Demo Daily Movement'
);

-- CURRENT_DATE + 1
INSERT INTO schedule_occurrences (user_id, exercise_id, schedule_id, date, schedule_name, completed)
SELECT
  (SELECT id FROM users WHERE username = 'demo'),
  (SELECT id FROM exercises WHERE name = 'Walk'),
  (SELECT id FROM schedules WHERE user_id = (SELECT id FROM users WHERE username = 'demo') AND name = 'Demo Daily Movement'),
  CURRENT_DATE + INTERVAL '1' DAY,
  'Demo Daily Movement',
  FALSE
WHERE NOT EXISTS (
  SELECT 1 FROM schedule_occurrences
  WHERE user_id = (SELECT id FROM users WHERE username = 'demo')
    AND date = CURRENT_DATE + INTERVAL '1' DAY
    AND schedule_name = 'Demo Daily Movement'
);

-- CURRENT_DATE + 3
INSERT INTO schedule_occurrences (user_id, exercise_id, schedule_id, date, schedule_name, completed)
SELECT
  (SELECT id FROM users WHERE username = 'demo'),
  (SELECT id FROM exercises WHERE name = 'Plank'),
  (SELECT id FROM schedules WHERE user_id = (SELECT id FROM users WHERE username = 'demo') AND name = 'Demo Daily Movement'),
  CURRENT_DATE + INTERVAL '3' DAY,
  'Demo Daily Movement',
  FALSE
WHERE NOT EXISTS (
  SELECT 1 FROM schedule_occurrences
  WHERE user_id = (SELECT id FROM users WHERE username = 'demo')
    AND date = CURRENT_DATE + INTERVAL '3' DAY
    AND schedule_name = 'Demo Daily Movement'
);

-- CURRENT_DATE + 5
INSERT INTO schedule_occurrences (user_id, exercise_id, schedule_id, date, schedule_name, completed)
SELECT
  (SELECT id FROM users WHERE username = 'demo'),
  (SELECT id FROM exercises WHERE name = 'Walk'),
  (SELECT id FROM schedules WHERE user_id = (SELECT id FROM users WHERE username = 'demo') AND name = 'Demo Daily Movement'),
  CURRENT_DATE + INTERVAL '5' DAY,
  'Demo Daily Movement',
  FALSE
WHERE NOT EXISTS (
  SELECT 1 FROM schedule_occurrences
  WHERE user_id = (SELECT id FROM users WHERE username = 'demo')
    AND date = CURRENT_DATE + INTERVAL '5' DAY
    AND schedule_name = 'Demo Daily Movement'
);

-- CURRENT_DATE + 7
INSERT INTO schedule_occurrences (user_id, exercise_id, schedule_id, date, schedule_name, completed)
SELECT
  (SELECT id FROM users WHERE username = 'demo'),
  (SELECT id FROM exercises WHERE name = 'Run'),
  (SELECT id FROM schedules WHERE user_id = (SELECT id FROM users WHERE username = 'demo') AND name = 'Demo Daily Movement'),
  CURRENT_DATE + INTERVAL '7' DAY,
  'Demo Daily Movement',
  FALSE
WHERE NOT EXISTS (
  SELECT 1 FROM schedule_occurrences
  WHERE user_id = (SELECT id FROM users WHERE username = 'demo')
    AND date = CURRENT_DATE + INTERVAL '7' DAY
    AND schedule_name = 'Demo Daily Movement'
);

-- -------------------------
-- TASKS (even days) - ensure every even day has task data
-- -------------------------
-- CURRENT_DATE - 6
INSERT INTO calendar_tasks (user_id, date, title, time, notes, is_exercise, completed, requires_log)
SELECT (SELECT id FROM users WHERE username = 'demo'), CURRENT_DATE - INTERVAL '6' DAY, 'Plan the day (10 min)', TIME '09:00:00', 'Pick your top 1–2 priorities.', FALSE, FALSE, FALSE
WHERE NOT EXISTS (
  SELECT 1 FROM calendar_tasks
  WHERE user_id = (SELECT id FROM users WHERE username = 'demo')
    AND date = CURRENT_DATE - INTERVAL '6' DAY
    AND title = 'Plan the day (10 min)'
);

-- CURRENT_DATE - 4
INSERT INTO calendar_tasks (user_id, date, title, time, notes, is_exercise, completed, requires_log)
SELECT (SELECT id FROM users WHERE username = 'demo'), CURRENT_DATE - INTERVAL '4' DAY, 'Hydrate (2L target)', TIME '10:00:00', 'Keep a bottle nearby.', FALSE, FALSE, FALSE
WHERE NOT EXISTS (
  SELECT 1 FROM calendar_tasks
  WHERE user_id = (SELECT id FROM users WHERE username = 'demo')
    AND date = CURRENT_DATE - INTERVAL '4' DAY
    AND title = 'Hydrate (2L target)'
);

-- CURRENT_DATE - 2
INSERT INTO calendar_tasks (user_id, date, title, time, notes, is_exercise, completed, requires_log)
SELECT (SELECT id FROM users WHERE username = 'demo'), CURRENT_DATE - INTERVAL '2' DAY, 'Tidy workspace', TIME '11:30:00', 'Small reset = big focus.', FALSE, FALSE, FALSE
WHERE NOT EXISTS (
  SELECT 1 FROM calendar_tasks
  WHERE user_id = (SELECT id FROM users WHERE username = 'demo')
    AND date = CURRENT_DATE - INTERVAL '2' DAY
    AND title = 'Tidy workspace'
);

-- CURRENT_DATE
INSERT INTO calendar_tasks (user_id, date, title, time, notes, is_exercise, completed, requires_log)
SELECT (SELECT id FROM users WHERE username = 'demo'), CURRENT_DATE, 'Review week progress', TIME '17:00:00', 'Quick reflection: what worked?', FALSE, FALSE, FALSE
WHERE NOT EXISTS (
  SELECT 1 FROM calendar_tasks
  WHERE user_id = (SELECT id FROM users WHERE username = 'demo')
    AND date = CURRENT_DATE
    AND title = 'Review week progress'
);

-- CURRENT_DATE + 2
INSERT INTO calendar_tasks (user_id, date, title, time, notes, is_exercise, completed, requires_log)
SELECT (SELECT id FROM users WHERE username = 'demo'), CURRENT_DATE + INTERVAL '2' DAY, 'Meal prep basics', TIME '13:00:00', 'Prep something easy for tomorrow.', FALSE, FALSE, FALSE
WHERE NOT EXISTS (
  SELECT 1 FROM calendar_tasks
  WHERE user_id = (SELECT id FROM users WHERE username = 'demo')
    AND date = CURRENT_DATE + INTERVAL '2' DAY
    AND title = 'Meal prep basics'
);

-- CURRENT_DATE + 4
INSERT INTO calendar_tasks (user_id, date, title, time, notes, is_exercise, completed, requires_log)
SELECT (SELECT id FROM users WHERE username = 'demo'), CURRENT_DATE + INTERVAL '4' DAY, 'Inbox zero (15 min)', TIME '09:30:00', 'Batch messages, then stop.', FALSE, FALSE, FALSE
WHERE NOT EXISTS (
  SELECT 1 FROM calendar_tasks
  WHERE user_id = (SELECT id FROM users WHERE username = 'demo')
    AND date = CURRENT_DATE + INTERVAL '4' DAY
    AND title = 'Inbox zero (15 min)'
);

-- CURRENT_DATE + 6
INSERT INTO calendar_tasks (user_id, date, title, time, notes, is_exercise, completed, requires_log)
SELECT (SELECT id FROM users WHERE username = 'demo'), CURRENT_DATE + INTERVAL '6' DAY, 'Early night', TIME '21:30:00', 'Protect recovery for tomorrow.', FALSE, FALSE, FALSE
WHERE NOT EXISTS (
  SELECT 1 FROM calendar_tasks
  WHERE user_id = (SELECT id FROM users WHERE username = 'demo')
    AND date = CURRENT_DATE + INTERVAL '6' DAY
    AND title = 'Early night'
);

-- -------------------------
-- WORKOUT SESSIONS (sprinkled across range)
-- -------------------------
INSERT INTO workout_sessions (user_id, date, workout_id, name_snapshot, completed)
SELECT
  (SELECT id FROM users WHERE username = 'demo'),
  CURRENT_DATE - INTERVAL '4' DAY,
  (SELECT id FROM workouts WHERE user_id = (SELECT id FROM users WHERE username = 'demo') AND name = 'Demo Full Body'),
  'Demo Full Body',
  FALSE
WHERE NOT EXISTS (
  SELECT 1 FROM workout_sessions
  WHERE user_id = (SELECT id FROM users WHERE username = 'demo')
    AND date = CURRENT_DATE - INTERVAL '4' DAY
    AND workout_id = (SELECT id FROM workouts WHERE user_id = (SELECT id FROM users WHERE username = 'demo') AND name = 'Demo Full Body')
);

INSERT INTO workout_sessions (user_id, date, workout_id, name_snapshot, completed)
SELECT
  (SELECT id FROM users WHERE username = 'demo'),
  CURRENT_DATE,
  (SELECT id FROM workouts WHERE user_id = (SELECT id FROM users WHERE username = 'demo') AND name = 'Demo Cardio Blast'),
  'Demo Cardio Blast',
  FALSE
WHERE NOT EXISTS (
  SELECT 1 FROM workout_sessions
  WHERE user_id = (SELECT id FROM users WHERE username = 'demo')
    AND date = CURRENT_DATE
    AND workout_id = (SELECT id FROM workouts WHERE user_id = (SELECT id FROM users WHERE username = 'demo') AND name = 'Demo Cardio Blast')
);

INSERT INTO workout_sessions (user_id, date, workout_id, name_snapshot, completed)
SELECT
  (SELECT id FROM users WHERE username = 'demo'),
  CURRENT_DATE + INTERVAL '5' DAY,
  (SELECT id FROM workouts WHERE user_id = (SELECT id FROM users WHERE username = 'demo') AND name = 'Demo Full Body'),
  'Demo Full Body',
  FALSE
WHERE NOT EXISTS (
  SELECT 1 FROM workout_sessions
  WHERE user_id = (SELECT id FROM users WHERE username = 'demo')
    AND date = CURRENT_DATE + INTERVAL '5' DAY
    AND workout_id = (SELECT id FROM workouts WHERE user_id = (SELECT id FROM users WHERE username = 'demo') AND name = 'Demo Full Body')
);

-- ========================================================================
-- BEGIN 02-demo-full-user-data.sql
-- ========================================================================

-- Extended demo dataset for full user walkthroughs (idempotent)
-- Covers: exercises, custom exercises, workouts, workout links, schedules, schedule entries,
-- schedule occurrences, applied schedules, calendar tasks (including exercise tasks), and workout sessions.

-- -------------------------
-- EXTRA EXERCISES (shared catalog)
-- -------------------------
INSERT INTO exercises (name, category, description, difficulty, type)
SELECT 'Romanian Deadlift', 'Strength', 'Hip hinge movement focusing posterior chain.', 3, 'strength'
WHERE NOT EXISTS (SELECT 1 FROM exercises WHERE name = 'Romanian Deadlift');

INSERT INTO exercises (name, category, description, difficulty, type)
SELECT 'Mountain Climbers', 'Conditioning', 'Fast core and cardio movement.', 2, 'cardio'
WHERE NOT EXISTS (SELECT 1 FROM exercises WHERE name = 'Mountain Climbers');

INSERT INTO exercises (name, category, description, difficulty, type)
SELECT 'Incline Push-up', 'Strength', 'Push-up variation for volume and form.', 1, 'strength'
WHERE NOT EXISTS (SELECT 1 FROM exercises WHERE name = 'Incline Push-up');

INSERT INTO exercises (name, category, description, difficulty, type)
SELECT 'Glute Bridge', 'Strength', 'Hip extension movement for glute activation.', 1, 'strength'
WHERE NOT EXISTS (SELECT 1 FROM exercises WHERE name = 'Glute Bridge');

-- -------------------------
-- CUSTOM EXERCISES (user-specific)
-- -------------------------
INSERT INTO custom_exercises (user_id, name, category, description, how_to, color_tag, type)
SELECT u.id, 'Band Pull-Apart', 'Strength', 'Shoulder-friendly upper back activation.', 'Hold the band at chest height and pull apart slowly.', '#f59e0b', 'strength'
FROM users u
WHERE u.username = 'demo'
  AND NOT EXISTS (
    SELECT 1 FROM custom_exercises ce
    WHERE ce.user_id = u.id AND ce.name = 'Band Pull-Apart'
  );

INSERT INTO custom_exercises (user_id, name, category, description, how_to, color_tag, type)
SELECT u.id, 'KB Halo', 'Mobility', 'Shoulder and upper-back mobility pattern.', 'Circle kettlebell around head while keeping torso braced.', '#10b981', 'mobility'
FROM users u
WHERE u.username = 'demo'
  AND NOT EXISTS (
    SELECT 1 FROM custom_exercises ce
    WHERE ce.user_id = u.id AND ce.name = 'KB Halo'
  );

INSERT INTO custom_exercises (user_id, name, category, description, how_to, color_tag, type)
SELECT u.id, 'Stair March Intervals', 'Cardio', 'Low-impact stair interval conditioning.', 'March up and down stairs in controlled intervals.', '#3b82f6', 'cardio'
FROM users u
WHERE u.username = 'demo2'
  AND NOT EXISTS (
    SELECT 1 FROM custom_exercises ce
    WHERE ce.user_id = u.id AND ce.name = 'Stair March Intervals'
  );

INSERT INTO custom_exercises (user_id, name, category, description, how_to, color_tag, type)
SELECT u.id, 'Mobility Flow 8', 'Mobility', 'Eight-minute full-body mobility flow.', 'Cycle through thoracic openers, hip openers, and ankle rocks.', '#8b5cf6', 'mobility'
FROM users u
WHERE u.username = 'demo2'
  AND NOT EXISTS (
    SELECT 1 FROM custom_exercises ce
    WHERE ce.user_id = u.id AND ce.name = 'Mobility Flow 8'
  );

-- -------------------------
-- WORKOUTS
-- -------------------------
INSERT INTO workouts (user_id, name, notes)
SELECT u.id, 'Demo Strength Builder', 'Primary strength progression workout.'
FROM users u
WHERE u.username = 'demo'
  AND NOT EXISTS (
    SELECT 1 FROM workouts w WHERE w.user_id = u.id AND w.name = 'Demo Strength Builder'
  );

INSERT INTO workouts (user_id, name, notes)
SELECT u.id, 'Demo Conditioning Mix', 'Conditioning and mobility blend.'
FROM users u
WHERE u.username = 'demo'
  AND NOT EXISTS (
    SELECT 1 FROM workouts w WHERE w.user_id = u.id AND w.name = 'Demo Conditioning Mix'
  );

INSERT INTO workouts (user_id, name, notes)
SELECT u.id, 'Demo2 Starter Strength', 'Starter full-body workout for non-premium demo.'
FROM users u
WHERE u.username = 'demo2'
  AND NOT EXISTS (
    SELECT 1 FROM workouts w WHERE w.user_id = u.id AND w.name = 'Demo2 Starter Strength'
  );

INSERT INTO workouts (user_id, name, notes)
SELECT u.id, 'Demo2 Cardio Core', 'Cardio + core session for daily consistency.'
FROM users u
WHERE u.username = 'demo2'
  AND NOT EXISTS (
    SELECT 1 FROM workouts w WHERE w.user_id = u.id AND w.name = 'Demo2 Cardio Core'
  );

-- -------------------------
-- WORKOUT -> EXERCISE LINKS
-- -------------------------
INSERT INTO workouts_exercises (workout_id, exercise_id)
SELECT w.id, e.id
FROM workouts w
JOIN users u ON u.id = w.user_id
JOIN exercises e ON e.name = 'Romanian Deadlift'
WHERE u.username = 'demo' AND w.name = 'Demo Strength Builder'
  AND NOT EXISTS (SELECT 1 FROM workouts_exercises we WHERE we.workout_id = w.id AND we.exercise_id = e.id);

INSERT INTO workouts_exercises (workout_id, exercise_id)
SELECT w.id, e.id
FROM workouts w
JOIN users u ON u.id = w.user_id
JOIN exercises e ON e.name = 'Incline Push-up'
WHERE u.username = 'demo' AND w.name = 'Demo Strength Builder'
  AND NOT EXISTS (SELECT 1 FROM workouts_exercises we WHERE we.workout_id = w.id AND we.exercise_id = e.id);

INSERT INTO workouts_exercises (workout_id, exercise_id)
SELECT w.id, e.id
FROM workouts w
JOIN users u ON u.id = w.user_id
JOIN exercises e ON e.name = 'Mountain Climbers'
WHERE u.username = 'demo' AND w.name = 'Demo Conditioning Mix'
  AND NOT EXISTS (SELECT 1 FROM workouts_exercises we WHERE we.workout_id = w.id AND we.exercise_id = e.id);

INSERT INTO workouts_exercises (workout_id, exercise_id)
SELECT w.id, e.id
FROM workouts w
JOIN users u ON u.id = w.user_id
JOIN exercises e ON e.name = 'Glute Bridge'
WHERE u.username = 'demo2' AND w.name = 'Demo2 Starter Strength'
  AND NOT EXISTS (SELECT 1 FROM workouts_exercises we WHERE we.workout_id = w.id AND we.exercise_id = e.id);

INSERT INTO workouts_exercises (workout_id, exercise_id)
SELECT w.id, e.id
FROM workouts w
JOIN users u ON u.id = w.user_id
JOIN exercises e ON e.name = 'Run'
WHERE u.username = 'demo2' AND w.name = 'Demo2 Cardio Core'
  AND NOT EXISTS (SELECT 1 FROM workouts_exercises we WHERE we.workout_id = w.id AND we.exercise_id = e.id);

INSERT INTO workouts_custom_exercises (workout_id, custom_exercise_id)
SELECT w.id, ce.id
FROM workouts w
JOIN users u ON u.id = w.user_id
JOIN custom_exercises ce ON ce.user_id = u.id AND ce.name = 'Band Pull-Apart'
WHERE u.username = 'demo' AND w.name = 'Demo Strength Builder'
  AND NOT EXISTS (SELECT 1 FROM workouts_custom_exercises wce WHERE wce.workout_id = w.id AND wce.custom_exercise_id = ce.id);

INSERT INTO workouts_custom_exercises (workout_id, custom_exercise_id)
SELECT w.id, ce.id
FROM workouts w
JOIN users u ON u.id = w.user_id
JOIN custom_exercises ce ON ce.user_id = u.id AND ce.name = 'Mobility Flow 8'
WHERE u.username = 'demo2' AND w.name = 'Demo2 Cardio Core'
  AND NOT EXISTS (SELECT 1 FROM workouts_custom_exercises wce WHERE wce.workout_id = w.id AND wce.custom_exercise_id = ce.id);

-- -------------------------
-- WORKOUT SCHEDULE (weekly placement)
-- -------------------------
INSERT INTO workout_schedule (user_id, day_of_week, workout_id, order_index)
SELECT u.id, 2, w.id, 0
FROM users u
JOIN workouts w ON w.user_id = u.id AND w.name = 'Demo Strength Builder'
WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM workout_schedule ws WHERE ws.user_id = u.id AND ws.day_of_week = 2 AND ws.workout_id = w.id);

INSERT INTO workout_schedule (user_id, day_of_week, workout_id, order_index)
SELECT u.id, 5, w.id, 0
FROM users u
JOIN workouts w ON w.user_id = u.id AND w.name = 'Demo Conditioning Mix'
WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM workout_schedule ws WHERE ws.user_id = u.id AND ws.day_of_week = 5 AND ws.workout_id = w.id);

INSERT INTO workout_schedule (user_id, day_of_week, workout_id, order_index)
SELECT u.id, 1, w.id, 0
FROM users u
JOIN workouts w ON w.user_id = u.id AND w.name = 'Demo2 Starter Strength'
WHERE u.username = 'demo2'
  AND NOT EXISTS (SELECT 1 FROM workout_schedule ws WHERE ws.user_id = u.id AND ws.day_of_week = 1 AND ws.workout_id = w.id);

INSERT INTO workout_schedule (user_id, day_of_week, workout_id, order_index)
SELECT u.id, 4, w.id, 0
FROM users u
JOIN workouts w ON w.user_id = u.id AND w.name = 'Demo2 Cardio Core'
WHERE u.username = 'demo2'
  AND NOT EXISTS (SELECT 1 FROM workout_schedule ws WHERE ws.user_id = u.id AND ws.day_of_week = 4 AND ws.workout_id = w.id);

-- -------------------------
-- SCHEDULES + ENTRIES
-- -------------------------
INSERT INTO schedules (user_id, name, description)
SELECT u.id, 'Demo Performance Week', 'Balanced week with recovery and performance blocks.'
FROM users u
WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM schedules s WHERE s.user_id = u.id AND s.name = 'Demo Performance Week');

INSERT INTO schedules (user_id, name, description)
SELECT u.id, 'Demo2 Foundation Week', 'Starter weekly structure for routine building.'
FROM users u
WHERE u.username = 'demo2'
  AND NOT EXISTS (SELECT 1 FROM schedules s WHERE s.user_id = u.id AND s.name = 'Demo2 Foundation Week');

INSERT INTO schedule_entries (schedule_id, exercise_id, day_of_week, order_number)
SELECT s.id, e.id, 1, 1
FROM schedules s
JOIN users u ON u.id = s.user_id
JOIN exercises e ON e.name = 'Walk'
WHERE u.username = 'demo' AND s.name = 'Demo Performance Week'
  AND NOT EXISTS (SELECT 1 FROM schedule_entries se WHERE se.schedule_id = s.id AND se.day_of_week = 1 AND se.order_number = 1);

INSERT INTO schedule_entries (schedule_id, custom_exercise_id, day_of_week, order_number)
SELECT s.id, ce.id, 3, 1
FROM schedules s
JOIN users u ON u.id = s.user_id
JOIN custom_exercises ce ON ce.user_id = u.id AND ce.name = 'KB Halo'
WHERE u.username = 'demo' AND s.name = 'Demo Performance Week'
  AND NOT EXISTS (SELECT 1 FROM schedule_entries se WHERE se.schedule_id = s.id AND se.day_of_week = 3 AND se.order_number = 1);

INSERT INTO schedule_entries (schedule_id, exercise_id, day_of_week, order_number)
SELECT s.id, e.id, 5, 1
FROM schedules s
JOIN users u ON u.id = s.user_id
JOIN exercises e ON e.name = 'Run'
WHERE u.username = 'demo2' AND s.name = 'Demo2 Foundation Week'
  AND NOT EXISTS (SELECT 1 FROM schedule_entries se WHERE se.schedule_id = s.id AND se.day_of_week = 5 AND se.order_number = 1);

INSERT INTO schedule_entries (schedule_id, custom_exercise_id, day_of_week, order_number)
SELECT s.id, ce.id, 6, 1
FROM schedules s
JOIN users u ON u.id = s.user_id
JOIN custom_exercises ce ON ce.user_id = u.id AND ce.name = 'Mobility Flow 8'
WHERE u.username = 'demo2' AND s.name = 'Demo2 Foundation Week'
  AND NOT EXISTS (SELECT 1 FROM schedule_entries se WHERE se.schedule_id = s.id AND se.day_of_week = 6 AND se.order_number = 1);

-- -------------------------
-- APPLY SCHEDULES
-- -------------------------
INSERT INTO schedule_applied (schedule_id, user_id, date_applied, shown_on_calendar, requires_logging, duration_weeks)
SELECT s.id, u.id, CAST((CURRENT_DATE - INTERVAL '56' DAY) AS DATE), TRUE, TRUE, 6
FROM schedules s
JOIN users u ON u.id = s.user_id
WHERE u.username = 'demo' AND s.name = 'Demo Performance Week'
  AND NOT EXISTS (SELECT 1 FROM schedule_applied sa WHERE sa.schedule_id = s.id AND sa.user_id = u.id);

INSERT INTO schedule_applied (schedule_id, user_id, date_applied, shown_on_calendar, requires_logging, duration_weeks)
SELECT s.id, u.id, CAST((CURRENT_DATE - INTERVAL '42' DAY) AS DATE), TRUE, FALSE, 4
FROM schedules s
JOIN users u ON u.id = s.user_id
WHERE u.username = 'demo2' AND s.name = 'Demo2 Foundation Week'
  AND NOT EXISTS (SELECT 1 FROM schedule_applied sa WHERE sa.schedule_id = s.id AND sa.user_id = u.id);

-- -------------------------
-- SCHEDULE OCCURRENCES (current/future week for demos)
-- -------------------------
INSERT INTO schedule_occurrences (user_id, exercise_id, schedule_id, date, schedule_name, completed)
SELECT u.id, e.id, s.id, CAST((CURRENT_DATE - INTERVAL '2' DAY) AS DATE), s.name, TRUE
FROM users u
JOIN schedules s ON s.user_id = u.id AND s.name = 'Demo Performance Week'
JOIN exercises e ON e.name = 'Walk'
WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM schedule_occurrences so WHERE so.user_id = u.id AND so.date = CAST((CURRENT_DATE - INTERVAL '2' DAY) AS DATE) AND so.schedule_name = s.name);

INSERT INTO schedule_occurrences (user_id, custom_exercise_id, schedule_id, date, schedule_name, completed)
SELECT u.id, ce.id, s.id, CAST(CURRENT_DATE AS DATE), s.name, FALSE
FROM users u
JOIN schedules s ON s.user_id = u.id AND s.name = 'Demo Performance Week'
JOIN custom_exercises ce ON ce.user_id = u.id AND ce.name = 'KB Halo'
WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM schedule_occurrences so WHERE so.user_id = u.id AND so.date = CAST(CURRENT_DATE AS DATE) AND so.schedule_name = s.name);

INSERT INTO schedule_occurrences (user_id, exercise_id, schedule_id, date, schedule_name, completed)
SELECT u.id, e.id, s.id, CAST((CURRENT_DATE + INTERVAL '1' DAY) AS DATE), s.name, FALSE
FROM users u
JOIN schedules s ON s.user_id = u.id AND s.name = 'Demo2 Foundation Week'
JOIN exercises e ON e.name = 'Run'
WHERE u.username = 'demo2'
  AND NOT EXISTS (SELECT 1 FROM schedule_occurrences so WHERE so.user_id = u.id AND so.date = CAST((CURRENT_DATE + INTERVAL '1' DAY) AS DATE) AND so.schedule_name = s.name);

INSERT INTO schedule_occurrences (user_id, custom_exercise_id, schedule_id, date, schedule_name, completed)
SELECT u.id, ce.id, s.id, CAST((CURRENT_DATE + INTERVAL '3' DAY) AS DATE), s.name, FALSE
FROM users u
JOIN schedules s ON s.user_id = u.id AND s.name = 'Demo2 Foundation Week'
JOIN custom_exercises ce ON ce.user_id = u.id AND ce.name = 'Mobility Flow 8'
WHERE u.username = 'demo2'
  AND NOT EXISTS (SELECT 1 FROM schedule_occurrences so WHERE so.user_id = u.id AND so.date = CAST((CURRENT_DATE + INTERVAL '3' DAY) AS DATE) AND so.schedule_name = s.name);

-- -------------------------
-- CALENDAR TASKS (normal + custom + exercise tasks)
-- -------------------------
INSERT INTO calendar_tasks (user_id, date, title, time, notes, is_exercise, completed, exercise_name, requires_log)
SELECT u.id, CAST((CURRENT_DATE - INTERVAL '2' DAY) AS DATE), 'Client check-in summary', TIME '08:30:00', 'Review sleep, stress, and recovery scores.', FALSE, FALSE, NULL, FALSE
FROM users u
WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM calendar_tasks ct WHERE ct.user_id = u.id AND ct.date = CAST((CURRENT_DATE - INTERVAL '2' DAY) AS DATE) AND ct.title = 'Client check-in summary');

INSERT INTO calendar_tasks (user_id, date, title, time, notes, is_exercise, completed, exercise_name, requires_log)
SELECT u.id, CAST((CURRENT_DATE - INTERVAL '1' DAY) AS DATE), 'Create custom progression note', TIME '12:15:00', 'Update progression targets for next block.', FALSE, FALSE, NULL, FALSE
FROM users u
WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM calendar_tasks ct WHERE ct.user_id = u.id AND ct.date = CAST((CURRENT_DATE - INTERVAL '1' DAY) AS DATE) AND ct.title = 'Create custom progression note');

INSERT INTO calendar_tasks (user_id, date, title, time, notes, is_exercise, completed, exercise_name, requires_log)
SELECT u.id, CAST((CURRENT_DATE + INTERVAL '1' DAY) AS DATE), 'Band Pull-Apart technique set', TIME '18:00:00', 'Log 3 controlled sets after warm-up.', TRUE, FALSE, 'Band Pull-Apart', TRUE
FROM users u
WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM calendar_tasks ct WHERE ct.user_id = u.id AND ct.date = CAST((CURRENT_DATE + INTERVAL '1' DAY) AS DATE) AND ct.title = 'Band Pull-Apart technique set');

INSERT INTO calendar_tasks (user_id, date, title, time, notes, is_exercise, completed, exercise_name, requires_log)
SELECT u.id, CAST((CURRENT_DATE - INTERVAL '1' DAY) AS DATE), 'Plan meals for two training days', TIME '09:00:00', 'Set up high-protein lunches and hydration reminders.', FALSE, FALSE, NULL, FALSE
FROM users u
WHERE u.username = 'demo2'
  AND NOT EXISTS (SELECT 1 FROM calendar_tasks ct WHERE ct.user_id = u.id AND ct.date = CAST((CURRENT_DATE - INTERVAL '1' DAY) AS DATE) AND ct.title = 'Plan meals for two training days');

INSERT INTO calendar_tasks (user_id, date, title, time, notes, is_exercise, completed, exercise_name, requires_log)
SELECT u.id, CAST((CURRENT_DATE + INTERVAL '2' DAY) AS DATE), 'Mobility Flow 8 evening reset', TIME '19:30:00', 'Complete full mobility sequence and note pain level.', TRUE, FALSE, 'Mobility Flow 8', TRUE
FROM users u
WHERE u.username = 'demo2'
  AND NOT EXISTS (SELECT 1 FROM calendar_tasks ct WHERE ct.user_id = u.id AND ct.date = CAST((CURRENT_DATE + INTERVAL '2' DAY) AS DATE) AND ct.title = 'Mobility Flow 8 evening reset');

-- -------------------------
-- DYNAMIC CURRENT-WEEK DATA (demo user)
-- Ensures visible calendar content regardless of current month/week.
-- -------------------------

INSERT INTO schedules (user_id, name, description)
SELECT u.id, 'Demo Live Week Plan', 'Auto-seeded current-week demo schedule visibility.'
FROM users u
WHERE u.username = 'demo'
  AND NOT EXISTS (
    SELECT 1 FROM schedules s WHERE s.user_id = u.id AND s.name = 'Demo Live Week Plan'
  );

INSERT INTO schedule_entries (schedule_id, exercise_id, day_of_week, order_number)
SELECT s.id, e.id, 1, 1
FROM schedules s
JOIN users u ON u.id = s.user_id
JOIN exercises e ON e.name = 'Walk'
WHERE u.username = 'demo' AND s.name = 'Demo Live Week Plan'
  AND NOT EXISTS (SELECT 1 FROM schedule_entries se WHERE se.schedule_id = s.id AND se.day_of_week = 1 AND se.order_number = 1);

INSERT INTO schedule_entries (schedule_id, exercise_id, day_of_week, order_number)
SELECT s.id, e.id, 3, 1
FROM schedules s
JOIN users u ON u.id = s.user_id
JOIN exercises e ON e.name = 'Push-up'
WHERE u.username = 'demo' AND s.name = 'Demo Live Week Plan'
  AND NOT EXISTS (SELECT 1 FROM schedule_entries se WHERE se.schedule_id = s.id AND se.day_of_week = 3 AND se.order_number = 1);

INSERT INTO schedule_entries (schedule_id, exercise_id, day_of_week, order_number)
SELECT s.id, e.id, 5, 1
FROM schedules s
JOIN users u ON u.id = s.user_id
JOIN exercises e ON e.name = 'Run'
WHERE u.username = 'demo' AND s.name = 'Demo Live Week Plan'
  AND NOT EXISTS (SELECT 1 FROM schedule_entries se WHERE se.schedule_id = s.id AND se.day_of_week = 5 AND se.order_number = 1);

INSERT INTO schedule_applied (schedule_id, user_id, date_applied, shown_on_calendar, requires_logging, duration_weeks)
SELECT s.id, u.id, CAST(CURRENT_DATE AS DATE), TRUE, TRUE, 12
FROM schedules s
JOIN users u ON u.id = s.user_id
WHERE u.username = 'demo' AND s.name = 'Demo Live Week Plan'
  AND NOT EXISTS (SELECT 1 FROM schedule_applied sa WHERE sa.schedule_id = s.id AND sa.user_id = u.id);

INSERT INTO schedule_occurrences (user_id, exercise_id, schedule_id, date, schedule_name, completed)
SELECT u.id, e.id, s.id, CAST(CURRENT_DATE AS DATE), s.name, FALSE
FROM users u
JOIN schedules s ON s.user_id = u.id AND s.name = 'Demo Live Week Plan'
JOIN exercises e ON e.name = 'Walk'
WHERE u.username = 'demo'
  AND NOT EXISTS (
    SELECT 1 FROM schedule_occurrences so
    WHERE so.user_id = u.id
      AND so.date = CAST(CURRENT_DATE AS DATE)
      AND so.schedule_name = s.name
  );

INSERT INTO schedule_occurrences (user_id, exercise_id, schedule_id, date, schedule_name, completed)
SELECT u.id, e.id, s.id, CAST((CURRENT_DATE + INTERVAL '2' DAY) AS DATE), s.name, FALSE
FROM users u
JOIN schedules s ON s.user_id = u.id AND s.name = 'Demo Live Week Plan'
JOIN exercises e ON e.name = 'Push-up'
WHERE u.username = 'demo'
  AND NOT EXISTS (
    SELECT 1 FROM schedule_occurrences so
    WHERE so.user_id = u.id
      AND so.date = CAST((CURRENT_DATE + INTERVAL '2' DAY) AS DATE)
      AND so.schedule_name = s.name
  );

INSERT INTO schedule_occurrences (user_id, exercise_id, schedule_id, date, schedule_name, completed)
SELECT u.id, e.id, s.id, CAST((CURRENT_DATE + INTERVAL '4' DAY) AS DATE), s.name, FALSE
FROM users u
JOIN schedules s ON s.user_id = u.id AND s.name = 'Demo Live Week Plan'
JOIN exercises e ON e.name = 'Run'
WHERE u.username = 'demo'
  AND NOT EXISTS (
    SELECT 1 FROM schedule_occurrences so
    WHERE so.user_id = u.id
      AND so.date = CAST((CURRENT_DATE + INTERVAL '4' DAY) AS DATE)
      AND so.schedule_name = s.name
  );

INSERT INTO calendar_tasks (user_id, date, title, time, notes, is_exercise, completed, exercise_name, requires_log)
SELECT u.id, CAST(CURRENT_DATE AS DATE), 'Daily priority check-in', TIME '08:15:00', 'Identify top task and training focus for today.', FALSE, FALSE, NULL, FALSE
FROM users u
WHERE u.username = 'demo'
  AND NOT EXISTS (
    SELECT 1 FROM calendar_tasks ct
    WHERE ct.user_id = u.id
      AND ct.date = CAST(CURRENT_DATE AS DATE)
      AND ct.title = 'Daily priority check-in'
  );

INSERT INTO calendar_tasks (user_id, date, title, time, notes, is_exercise, completed, exercise_name, requires_log)
SELECT u.id, CAST((CURRENT_DATE + INTERVAL '1' DAY) AS DATE), 'Prep tomorrow''s session', TIME '18:00:00', 'Review plan and prep kit for tomorrow.', FALSE, FALSE, NULL, FALSE
FROM users u
WHERE u.username = 'demo'
  AND NOT EXISTS (
    SELECT 1 FROM calendar_tasks ct
    WHERE ct.user_id = u.id
      AND ct.date = CAST((CURRENT_DATE + INTERVAL '1' DAY) AS DATE)
      AND ct.title = 'Prep tomorrow''s session'
  );

INSERT INTO calendar_tasks (user_id, date, title, time, notes, is_exercise, completed, exercise_name, requires_log)
SELECT u.id, CAST((CURRENT_DATE + INTERVAL '3' DAY) AS DATE), 'Band Pull-Apart activation', TIME '17:45:00', 'Complete 3 sets and log form quality.', TRUE, FALSE, 'Band Pull-Apart', TRUE
FROM users u
WHERE u.username = 'demo'
  AND NOT EXISTS (
    SELECT 1 FROM calendar_tasks ct
    WHERE ct.user_id = u.id
      AND ct.date = CAST((CURRENT_DATE + INTERVAL '3' DAY) AS DATE)
      AND ct.title = 'Band Pull-Apart activation'
  );

-- -------------------------
-- WORKOUT SESSIONS (history data for demos)
-- -------------------------
INSERT INTO workout_sessions (user_id, date, workout_id, name_snapshot, completed)
SELECT u.id, CAST((CURRENT_DATE - INTERVAL '6' DAY) AS DATE), w.id, w.name, TRUE
FROM users u
JOIN workouts w ON w.user_id = u.id AND w.name = 'Demo Strength Builder'
WHERE u.username = 'demo'
  AND NOT EXISTS (
    SELECT 1 FROM workout_sessions ws
    WHERE ws.user_id = u.id AND ws.date = CAST((CURRENT_DATE - INTERVAL '6' DAY) AS DATE) AND ws.workout_id = w.id
  );

INSERT INTO workout_sessions (user_id, date, workout_id, name_snapshot, completed)
SELECT u.id, CAST((CURRENT_DATE - INTERVAL '7' DAY) AS DATE), w.id, w.name, TRUE
FROM users u
JOIN workouts w ON w.user_id = u.id AND w.name = 'Demo2 Starter Strength'
WHERE u.username = 'demo2'
  AND NOT EXISTS (
    SELECT 1 FROM workout_sessions ws
    WHERE ws.user_id = u.id AND ws.date = CAST((CURRENT_DATE - INTERVAL '7' DAY) AS DATE) AND ws.workout_id = w.id
  );

-- -------------------------
-- EXERCISE LOG SAMPLE
-- -------------------------
INSERT INTO exercise_log (user_id, date, mood_before, mood_after, confidence, comments, duration_minutes)
SELECT u.id, CAST((CURRENT_DATE - INTERVAL '6' DAY) AS DATE), 3, 4, 4, 'Good session, form improving and energy stable.', 42
FROM users u
WHERE u.username = 'demo'
  AND NOT EXISTS (
    SELECT 1 FROM exercise_log el
    WHERE el.user_id = u.id AND el.date = CAST((CURRENT_DATE - INTERVAL '6' DAY) AS DATE) AND el.comments = 'Good session, form improving and energy stable.'
  );

INSERT INTO exercise_log (user_id, date, mood_before, mood_after, confidence, comments, duration_minutes)
SELECT u.id, CAST((CURRENT_DATE - INTERVAL '7' DAY) AS DATE), 2, 4, 3, 'First full session complete, pacing felt manageable.', 35
FROM users u
WHERE u.username = 'demo2'
  AND NOT EXISTS (
    SELECT 1 FROM exercise_log el
    WHERE el.user_id = u.id AND el.date = CAST((CURRENT_DATE - INTERVAL '7' DAY) AS DATE) AND el.comments = 'First full session complete, pacing felt manageable.'
  );

