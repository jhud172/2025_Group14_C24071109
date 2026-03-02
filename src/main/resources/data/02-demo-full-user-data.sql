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
SELECT s.id, u.id, DATE '2026-02-10', TRUE, TRUE, 6
FROM schedules s
JOIN users u ON u.id = s.user_id
WHERE u.username = 'demo' AND s.name = 'Demo Performance Week'
  AND NOT EXISTS (SELECT 1 FROM schedule_applied sa WHERE sa.schedule_id = s.id AND sa.user_id = u.id);

INSERT INTO schedule_applied (schedule_id, user_id, date_applied, shown_on_calendar, requires_logging, duration_weeks)
SELECT s.id, u.id, DATE '2026-02-10', TRUE, FALSE, 4
FROM schedules s
JOIN users u ON u.id = s.user_id
WHERE u.username = 'demo2' AND s.name = 'Demo2 Foundation Week'
  AND NOT EXISTS (SELECT 1 FROM schedule_applied sa WHERE sa.schedule_id = s.id AND sa.user_id = u.id);

-- -------------------------
-- SCHEDULE OCCURRENCES (current/future week for demos)
-- -------------------------
INSERT INTO schedule_occurrences (user_id, exercise_id, schedule_id, date, schedule_name, completed)
SELECT u.id, e.id, s.id, DATE '2026-02-18', s.name, TRUE
FROM users u
JOIN schedules s ON s.user_id = u.id AND s.name = 'Demo Performance Week'
JOIN exercises e ON e.name = 'Walk'
WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM schedule_occurrences so WHERE so.user_id = u.id AND so.date = DATE '2026-02-18' AND so.schedule_name = s.name);

INSERT INTO schedule_occurrences (user_id, custom_exercise_id, schedule_id, date, schedule_name, completed)
SELECT u.id, ce.id, s.id, DATE '2026-02-20', s.name, FALSE
FROM users u
JOIN schedules s ON s.user_id = u.id AND s.name = 'Demo Performance Week'
JOIN custom_exercises ce ON ce.user_id = u.id AND ce.name = 'KB Halo'
WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM schedule_occurrences so WHERE so.user_id = u.id AND so.date = DATE '2026-02-20' AND so.schedule_name = s.name);

INSERT INTO schedule_occurrences (user_id, exercise_id, schedule_id, date, schedule_name, completed)
SELECT u.id, e.id, s.id, DATE '2026-02-19', s.name, FALSE
FROM users u
JOIN schedules s ON s.user_id = u.id AND s.name = 'Demo2 Foundation Week'
JOIN exercises e ON e.name = 'Run'
WHERE u.username = 'demo2'
  AND NOT EXISTS (SELECT 1 FROM schedule_occurrences so WHERE so.user_id = u.id AND so.date = DATE '2026-02-19' AND so.schedule_name = s.name);

INSERT INTO schedule_occurrences (user_id, custom_exercise_id, schedule_id, date, schedule_name, completed)
SELECT u.id, ce.id, s.id, DATE '2026-02-22', s.name, FALSE
FROM users u
JOIN schedules s ON s.user_id = u.id AND s.name = 'Demo2 Foundation Week'
JOIN custom_exercises ce ON ce.user_id = u.id AND ce.name = 'Mobility Flow 8'
WHERE u.username = 'demo2'
  AND NOT EXISTS (SELECT 1 FROM schedule_occurrences so WHERE so.user_id = u.id AND so.date = DATE '2026-02-22' AND so.schedule_name = s.name);

-- -------------------------
-- CALENDAR TASKS (normal + custom + exercise tasks)
-- -------------------------
INSERT INTO calendar_tasks (user_id, date, title, time, notes, is_exercise, completed, exercise_name, requires_log)
SELECT u.id, DATE '2026-02-18', 'Client check-in summary', TIME '08:30:00', 'Review sleep, stress, and recovery scores.', FALSE, FALSE, NULL, FALSE
FROM users u
WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM calendar_tasks ct WHERE ct.user_id = u.id AND ct.date = DATE '2026-02-18' AND ct.title = 'Client check-in summary');

INSERT INTO calendar_tasks (user_id, date, title, time, notes, is_exercise, completed, exercise_name, requires_log)
SELECT u.id, DATE '2026-02-19', 'Create custom progression note', TIME '12:15:00', 'Update progression targets for next block.', FALSE, FALSE, NULL, FALSE
FROM users u
WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM calendar_tasks ct WHERE ct.user_id = u.id AND ct.date = DATE '2026-02-19' AND ct.title = 'Create custom progression note');

INSERT INTO calendar_tasks (user_id, date, title, time, notes, is_exercise, completed, exercise_name, requires_log)
SELECT u.id, DATE '2026-02-20', 'Band Pull-Apart technique set', TIME '18:00:00', 'Log 3 controlled sets after warm-up.', TRUE, FALSE, 'Band Pull-Apart', TRUE
FROM users u
WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM calendar_tasks ct WHERE ct.user_id = u.id AND ct.date = DATE '2026-02-20' AND ct.title = 'Band Pull-Apart technique set');

INSERT INTO calendar_tasks (user_id, date, title, time, notes, is_exercise, completed, exercise_name, requires_log)
SELECT u.id, DATE '2026-02-18', 'Plan meals for two training days', TIME '09:00:00', 'Set up high-protein lunches and hydration reminders.', FALSE, FALSE, NULL, FALSE
FROM users u
WHERE u.username = 'demo2'
  AND NOT EXISTS (SELECT 1 FROM calendar_tasks ct WHERE ct.user_id = u.id AND ct.date = DATE '2026-02-18' AND ct.title = 'Plan meals for two training days');

INSERT INTO calendar_tasks (user_id, date, title, time, notes, is_exercise, completed, exercise_name, requires_log)
SELECT u.id, DATE '2026-02-21', 'Mobility Flow 8 evening reset', TIME '19:30:00', 'Complete full mobility sequence and note pain level.', TRUE, FALSE, 'Mobility Flow 8', TRUE
FROM users u
WHERE u.username = 'demo2'
  AND NOT EXISTS (SELECT 1 FROM calendar_tasks ct WHERE ct.user_id = u.id AND ct.date = DATE '2026-02-21' AND ct.title = 'Mobility Flow 8 evening reset');

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
SELECT u.id, DATE '2026-02-17', w.id, w.name, TRUE
FROM users u
JOIN workouts w ON w.user_id = u.id AND w.name = 'Demo Strength Builder'
WHERE u.username = 'demo'
  AND NOT EXISTS (
    SELECT 1 FROM workout_sessions ws
    WHERE ws.user_id = u.id AND ws.date = DATE '2026-02-17' AND ws.workout_id = w.id
  );

INSERT INTO workout_sessions (user_id, date, workout_id, name_snapshot, completed)
SELECT u.id, DATE '2026-02-16', w.id, w.name, TRUE
FROM users u
JOIN workouts w ON w.user_id = u.id AND w.name = 'Demo2 Starter Strength'
WHERE u.username = 'demo2'
  AND NOT EXISTS (
    SELECT 1 FROM workout_sessions ws
    WHERE ws.user_id = u.id AND ws.date = DATE '2026-02-16' AND ws.workout_id = w.id
  );

-- -------------------------
-- EXERCISE LOG SAMPLE
-- -------------------------
INSERT INTO exercise_log (user_id, date, mood_before, mood_after, confidence, comments, duration_minutes)
SELECT u.id, DATE '2026-02-17', 3, 4, 4, 'Good session, form improving and energy stable.', 42
FROM users u
WHERE u.username = 'demo'
  AND NOT EXISTS (
    SELECT 1 FROM exercise_log el
    WHERE el.user_id = u.id AND el.date = DATE '2026-02-17' AND el.comments = 'Good session, form improving and energy stable.'
  );

INSERT INTO exercise_log (user_id, date, mood_before, mood_after, confidence, comments, duration_minutes)
SELECT u.id, DATE '2026-02-16', 2, 4, 3, 'First full session complete, pacing felt manageable.', 35
FROM users u
WHERE u.username = 'demo2'
  AND NOT EXISTS (
    SELECT 1 FROM exercise_log el
    WHERE el.user_id = u.id AND el.date = DATE '2026-02-16' AND el.comments = 'First full session complete, pacing felt manageable.'
  );
