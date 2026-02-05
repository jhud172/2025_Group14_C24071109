-- Demo calendar seed for user 'demo'
-- Range: 2026-01-09 .. 2026-01-23 (inclusive)
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
-- 2026-01-09
INSERT INTO schedule_occurrences (user_id, exercise_id, schedule_id, date, schedule_name, completed)
SELECT
  (SELECT id FROM users WHERE username = 'demo'),
  (SELECT id FROM exercises WHERE name = 'Walk'),
  (SELECT id FROM schedules WHERE user_id = (SELECT id FROM users WHERE username = 'demo') AND name = 'Demo Daily Movement'),
  DATE '2026-01-09',
  'Demo Daily Movement',
  FALSE
WHERE NOT EXISTS (
  SELECT 1 FROM schedule_occurrences
  WHERE user_id = (SELECT id FROM users WHERE username = 'demo')
    AND date = DATE '2026-01-09'
    AND schedule_name = 'Demo Daily Movement'
);

-- 2026-01-11
INSERT INTO schedule_occurrences (user_id, exercise_id, schedule_id, date, schedule_name, completed)
SELECT
  (SELECT id FROM users WHERE username = 'demo'),
  (SELECT id FROM exercises WHERE name = 'Plank'),
  (SELECT id FROM schedules WHERE user_id = (SELECT id FROM users WHERE username = 'demo') AND name = 'Demo Daily Movement'),
  DATE '2026-01-11',
  'Demo Daily Movement',
  FALSE
WHERE NOT EXISTS (
  SELECT 1 FROM schedule_occurrences
  WHERE user_id = (SELECT id FROM users WHERE username = 'demo')
    AND date = DATE '2026-01-11'
    AND schedule_name = 'Demo Daily Movement'
);

-- 2026-01-13
INSERT INTO schedule_occurrences (user_id, exercise_id, schedule_id, date, schedule_name, completed)
SELECT
  (SELECT id FROM users WHERE username = 'demo'),
  (SELECT id FROM exercises WHERE name = 'Walk'),
  (SELECT id FROM schedules WHERE user_id = (SELECT id FROM users WHERE username = 'demo') AND name = 'Demo Daily Movement'),
  DATE '2026-01-13',
  'Demo Daily Movement',
  FALSE
WHERE NOT EXISTS (
  SELECT 1 FROM schedule_occurrences
  WHERE user_id = (SELECT id FROM users WHERE username = 'demo')
    AND date = DATE '2026-01-13'
    AND schedule_name = 'Demo Daily Movement'
);

-- 2026-01-15
INSERT INTO schedule_occurrences (user_id, exercise_id, schedule_id, date, schedule_name, completed)
SELECT
  (SELECT id FROM users WHERE username = 'demo'),
  (SELECT id FROM exercises WHERE name = 'Run'),
  (SELECT id FROM schedules WHERE user_id = (SELECT id FROM users WHERE username = 'demo') AND name = 'Demo Daily Movement'),
  DATE '2026-01-15',
  'Demo Daily Movement',
  FALSE
WHERE NOT EXISTS (
  SELECT 1 FROM schedule_occurrences
  WHERE user_id = (SELECT id FROM users WHERE username = 'demo')
    AND date = DATE '2026-01-15'
    AND schedule_name = 'Demo Daily Movement'
);

-- 2026-01-17
INSERT INTO schedule_occurrences (user_id, exercise_id, schedule_id, date, schedule_name, completed)
SELECT
  (SELECT id FROM users WHERE username = 'demo'),
  (SELECT id FROM exercises WHERE name = 'Walk'),
  (SELECT id FROM schedules WHERE user_id = (SELECT id FROM users WHERE username = 'demo') AND name = 'Demo Daily Movement'),
  DATE '2026-01-17',
  'Demo Daily Movement',
  FALSE
WHERE NOT EXISTS (
  SELECT 1 FROM schedule_occurrences
  WHERE user_id = (SELECT id FROM users WHERE username = 'demo')
    AND date = DATE '2026-01-17'
    AND schedule_name = 'Demo Daily Movement'
);

-- 2026-01-19
INSERT INTO schedule_occurrences (user_id, exercise_id, schedule_id, date, schedule_name, completed)
SELECT
  (SELECT id FROM users WHERE username = 'demo'),
  (SELECT id FROM exercises WHERE name = 'Plank'),
  (SELECT id FROM schedules WHERE user_id = (SELECT id FROM users WHERE username = 'demo') AND name = 'Demo Daily Movement'),
  DATE '2026-01-19',
  'Demo Daily Movement',
  FALSE
WHERE NOT EXISTS (
  SELECT 1 FROM schedule_occurrences
  WHERE user_id = (SELECT id FROM users WHERE username = 'demo')
    AND date = DATE '2026-01-19'
    AND schedule_name = 'Demo Daily Movement'
);

-- 2026-01-21
INSERT INTO schedule_occurrences (user_id, exercise_id, schedule_id, date, schedule_name, completed)
SELECT
  (SELECT id FROM users WHERE username = 'demo'),
  (SELECT id FROM exercises WHERE name = 'Walk'),
  (SELECT id FROM schedules WHERE user_id = (SELECT id FROM users WHERE username = 'demo') AND name = 'Demo Daily Movement'),
  DATE '2026-01-21',
  'Demo Daily Movement',
  FALSE
WHERE NOT EXISTS (
  SELECT 1 FROM schedule_occurrences
  WHERE user_id = (SELECT id FROM users WHERE username = 'demo')
    AND date = DATE '2026-01-21'
    AND schedule_name = 'Demo Daily Movement'
);

-- 2026-01-23
INSERT INTO schedule_occurrences (user_id, exercise_id, schedule_id, date, schedule_name, completed)
SELECT
  (SELECT id FROM users WHERE username = 'demo'),
  (SELECT id FROM exercises WHERE name = 'Run'),
  (SELECT id FROM schedules WHERE user_id = (SELECT id FROM users WHERE username = 'demo') AND name = 'Demo Daily Movement'),
  DATE '2026-01-23',
  'Demo Daily Movement',
  FALSE
WHERE NOT EXISTS (
  SELECT 1 FROM schedule_occurrences
  WHERE user_id = (SELECT id FROM users WHERE username = 'demo')
    AND date = DATE '2026-01-23'
    AND schedule_name = 'Demo Daily Movement'
);

-- -------------------------
-- TASKS (even days) - ensure every even day has task data
-- -------------------------
-- 2026-01-10
INSERT INTO calendar_tasks (user_id, date, title, time, notes, is_exercise, completed, requires_log)
SELECT (SELECT id FROM users WHERE username = 'demo'), DATE '2026-01-10', 'Plan the day (10 min)', TIME '09:00:00', 'Pick your top 1–2 priorities.', FALSE, FALSE, FALSE
WHERE NOT EXISTS (
  SELECT 1 FROM calendar_tasks
  WHERE user_id = (SELECT id FROM users WHERE username = 'demo')
    AND date = DATE '2026-01-10'
    AND title = 'Plan the day (10 min)'
);

-- 2026-01-12
INSERT INTO calendar_tasks (user_id, date, title, time, notes, is_exercise, completed, requires_log)
SELECT (SELECT id FROM users WHERE username = 'demo'), DATE '2026-01-12', 'Hydrate (2L target)', TIME '10:00:00', 'Keep a bottle nearby.', FALSE, FALSE, FALSE
WHERE NOT EXISTS (
  SELECT 1 FROM calendar_tasks
  WHERE user_id = (SELECT id FROM users WHERE username = 'demo')
    AND date = DATE '2026-01-12'
    AND title = 'Hydrate (2L target)'
);

-- 2026-01-14
INSERT INTO calendar_tasks (user_id, date, title, time, notes, is_exercise, completed, requires_log)
SELECT (SELECT id FROM users WHERE username = 'demo'), DATE '2026-01-14', 'Tidy workspace', TIME '11:30:00', 'Small reset = big focus.', FALSE, FALSE, FALSE
WHERE NOT EXISTS (
  SELECT 1 FROM calendar_tasks
  WHERE user_id = (SELECT id FROM users WHERE username = 'demo')
    AND date = DATE '2026-01-14'
    AND title = 'Tidy workspace'
);

-- 2026-01-16
INSERT INTO calendar_tasks (user_id, date, title, time, notes, is_exercise, completed, requires_log)
SELECT (SELECT id FROM users WHERE username = 'demo'), DATE '2026-01-16', 'Review week progress', TIME '17:00:00', 'Quick reflection: what worked?', FALSE, FALSE, FALSE
WHERE NOT EXISTS (
  SELECT 1 FROM calendar_tasks
  WHERE user_id = (SELECT id FROM users WHERE username = 'demo')
    AND date = DATE '2026-01-16'
    AND title = 'Review week progress'
);

-- 2026-01-18
INSERT INTO calendar_tasks (user_id, date, title, time, notes, is_exercise, completed, requires_log)
SELECT (SELECT id FROM users WHERE username = 'demo'), DATE '2026-01-18', 'Meal prep basics', TIME '13:00:00', 'Prep something easy for tomorrow.', FALSE, FALSE, FALSE
WHERE NOT EXISTS (
  SELECT 1 FROM calendar_tasks
  WHERE user_id = (SELECT id FROM users WHERE username = 'demo')
    AND date = DATE '2026-01-18'
    AND title = 'Meal prep basics'
);

-- 2026-01-20
INSERT INTO calendar_tasks (user_id, date, title, time, notes, is_exercise, completed, requires_log)
SELECT (SELECT id FROM users WHERE username = 'demo'), DATE '2026-01-20', 'Inbox zero (15 min)', TIME '09:30:00', 'Batch messages, then stop.', FALSE, FALSE, FALSE
WHERE NOT EXISTS (
  SELECT 1 FROM calendar_tasks
  WHERE user_id = (SELECT id FROM users WHERE username = 'demo')
    AND date = DATE '2026-01-20'
    AND title = 'Inbox zero (15 min)'
);

-- 2026-01-22
INSERT INTO calendar_tasks (user_id, date, title, time, notes, is_exercise, completed, requires_log)
SELECT (SELECT id FROM users WHERE username = 'demo'), DATE '2026-01-22', 'Early night', TIME '21:30:00', 'Protect recovery for tomorrow.', FALSE, FALSE, FALSE
WHERE NOT EXISTS (
  SELECT 1 FROM calendar_tasks
  WHERE user_id = (SELECT id FROM users WHERE username = 'demo')
    AND date = DATE '2026-01-22'
    AND title = 'Early night'
);

-- -------------------------
-- WORKOUT SESSIONS (sprinkled across range)
-- -------------------------
INSERT INTO workout_sessions (user_id, date, workout_id, name_snapshot, completed)
SELECT
  (SELECT id FROM users WHERE username = 'demo'),
  DATE '2026-01-12',
  (SELECT id FROM workouts WHERE user_id = (SELECT id FROM users WHERE username = 'demo') AND name = 'Demo Full Body'),
  'Demo Full Body',
  FALSE
WHERE NOT EXISTS (
  SELECT 1 FROM workout_sessions
  WHERE user_id = (SELECT id FROM users WHERE username = 'demo')
    AND date = DATE '2026-01-12'
    AND workout_id = (SELECT id FROM workouts WHERE user_id = (SELECT id FROM users WHERE username = 'demo') AND name = 'Demo Full Body')
);

INSERT INTO workout_sessions (user_id, date, workout_id, name_snapshot, completed)
SELECT
  (SELECT id FROM users WHERE username = 'demo'),
  DATE '2026-01-16',
  (SELECT id FROM workouts WHERE user_id = (SELECT id FROM users WHERE username = 'demo') AND name = 'Demo Cardio Blast'),
  'Demo Cardio Blast',
  FALSE
WHERE NOT EXISTS (
  SELECT 1 FROM workout_sessions
  WHERE user_id = (SELECT id FROM users WHERE username = 'demo')
    AND date = DATE '2026-01-16'
    AND workout_id = (SELECT id FROM workouts WHERE user_id = (SELECT id FROM users WHERE username = 'demo') AND name = 'Demo Cardio Blast')
);

INSERT INTO workout_sessions (user_id, date, workout_id, name_snapshot, completed)
SELECT
  (SELECT id FROM users WHERE username = 'demo'),
  DATE '2026-01-21',
  (SELECT id FROM workouts WHERE user_id = (SELECT id FROM users WHERE username = 'demo') AND name = 'Demo Full Body'),
  'Demo Full Body',
  FALSE
WHERE NOT EXISTS (
  SELECT 1 FROM workout_sessions
  WHERE user_id = (SELECT id FROM users WHERE username = 'demo')
    AND date = DATE '2026-01-21'
    AND workout_id = (SELECT id FROM workouts WHERE user_id = (SELECT id FROM users WHERE username = 'demo') AND name = 'Demo Full Body')
);
