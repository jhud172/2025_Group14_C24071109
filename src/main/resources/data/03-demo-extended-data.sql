-- Extended demo dataset: preferences, health records, goals, streaks, nutrition,
-- daily completions, vault notes, notes, user_points, trainer/gym profiles,
-- trainer-client links, exercise sessions/set logs, and completed past history.
-- All users: demo, demo2, trainer_demo, gymadmin_demo, admin_demo
-- Idempotent (INSERT ... WHERE NOT EXISTS / UPSERT style).

-- =========================================================================
-- USER SETTINGS (preferences, equipment, macro targets, calendar prefs)
-- =========================================================================

-- demo (premium, CLIENT) - active trainer, strength focus
INSERT INTO user_settings (
    user_id, language, theme, easy_mode, color_blind_mode,
    disability_hearing, disability_mobility, disability_vision,
    share_recovery_signals, share_nutrition_signals, share_sleep_signals,
    share_fatigue_signals, share_weight_trend,
    calendar_task_ordering, calendar_task_layout, calendar_workout_ordering,
    calendar_view_preference,
    default_sets, default_rep_min, default_rep_max,
    preferred_equipment_bodyweight, preferred_equipment_dumbbell,
    preferred_equipment_barbell, preferred_equipment_machine,
    preferred_equipment_bands, preferred_equipment_kettlebell,
    macro_target_calories, macro_target_protein, macro_target_carbs, macro_target_fat,
    quiet_hours_start, quiet_hours_end
)
SELECT
    (SELECT id FROM users WHERE username = 'demo'),
    'en', 'DARK', FALSE, FALSE,
    FALSE, FALSE, FALSE,
    TRUE, TRUE, TRUE, TRUE, TRUE,
    'CHRONOLOGICAL', 'COMBINED_LIST', 'SCHEDULE_ORDER',
    'WEEK',
    4, 6, 10,
    TRUE, TRUE, TRUE, FALSE, TRUE, TRUE,
    2400, 180, 220, 70,
    TIME '22:00:00', TIME '07:00:00'
WHERE EXISTS (SELECT 1 FROM users WHERE username = 'demo')
  AND NOT EXISTS (SELECT 1 FROM user_settings WHERE user_id = (SELECT id FROM users WHERE username = 'demo'));

UPDATE user_settings
SET theme = 'DARK',
    share_recovery_signals = TRUE, share_nutrition_signals = TRUE,
    share_sleep_signals = TRUE, share_fatigue_signals = TRUE,
    share_weight_trend = TRUE,
    calendar_view_preference = 'WEEK',
    default_sets = 4, default_rep_min = 6, default_rep_max = 10,
    preferred_equipment_bodyweight = TRUE, preferred_equipment_dumbbell = TRUE,
    preferred_equipment_barbell = TRUE, preferred_equipment_bands = TRUE,
    preferred_equipment_kettlebell = TRUE,
    macro_target_calories = 2400, macro_target_protein = 180,
    macro_target_carbs = 220, macro_target_fat = 70,
    quiet_hours_start = TIME '22:00:00', quiet_hours_end = TIME '07:00:00'
WHERE user_id = (SELECT id FROM users WHERE username = 'demo');

-- demo2 (non-premium, CLIENT) - beginner, bodyweight focus
INSERT INTO user_settings (
    user_id, language, theme, easy_mode, color_blind_mode,
    disability_hearing, disability_mobility, disability_vision,
    share_recovery_signals, share_nutrition_signals, share_sleep_signals,
    share_fatigue_signals, share_weight_trend,
    calendar_task_ordering, calendar_task_layout, calendar_workout_ordering,
    calendar_view_preference,
    default_sets, default_rep_min, default_rep_max,
    preferred_equipment_bodyweight, preferred_equipment_dumbbell,
    preferred_equipment_barbell, preferred_equipment_machine,
    preferred_equipment_bands, preferred_equipment_kettlebell,
    macro_target_calories, macro_target_protein, macro_target_carbs, macro_target_fat,
    quiet_hours_start, quiet_hours_end
)
SELECT
    (SELECT id FROM users WHERE username = 'demo2'),
    'en', 'LIGHT', TRUE, FALSE,
    FALSE, FALSE, FALSE,
    FALSE, FALSE, FALSE, FALSE, FALSE,
    'CHRONOLOGICAL', 'COMBINED_LIST', 'SCHEDULE_ORDER',
    'MONTH',
    3, 8, 12,
    TRUE, FALSE, FALSE, FALSE, FALSE, FALSE,
    1800, 120, 200, 55,
    TIME '23:00:00', TIME '07:30:00'
WHERE EXISTS (SELECT 1 FROM users WHERE username = 'demo2')
  AND NOT EXISTS (SELECT 1 FROM user_settings WHERE user_id = (SELECT id FROM users WHERE username = 'demo2'));

UPDATE user_settings
SET theme = 'LIGHT', easy_mode = TRUE,
    calendar_view_preference = 'MONTH',
    default_sets = 3, default_rep_min = 8, default_rep_max = 12,
    preferred_equipment_bodyweight = TRUE,
    macro_target_calories = 1800, macro_target_protein = 120,
    macro_target_carbs = 200, macro_target_fat = 55,
    quiet_hours_start = TIME '23:00:00', quiet_hours_end = TIME '07:30:00'
WHERE user_id = (SELECT id FROM users WHERE username = 'demo2');

-- trainer_demo
INSERT INTO user_settings (
    user_id, language, theme, easy_mode, color_blind_mode,
    disability_hearing, disability_mobility, disability_vision,
    share_recovery_signals, share_nutrition_signals, share_sleep_signals,
    share_fatigue_signals, share_weight_trend,
    calendar_task_ordering, calendar_task_layout, calendar_workout_ordering,
    calendar_view_preference,
    default_sets, default_rep_min, default_rep_max,
    preferred_equipment_bodyweight, preferred_equipment_dumbbell,
    preferred_equipment_barbell, preferred_equipment_machine,
    preferred_equipment_bands, preferred_equipment_kettlebell,
    macro_target_calories, macro_target_protein, macro_target_carbs, macro_target_fat,
    quiet_hours_start, quiet_hours_end
)
SELECT
    (SELECT id FROM users WHERE username = 'trainer_demo'),
    'en', 'DARK', FALSE, FALSE,
    FALSE, FALSE, FALSE,
    FALSE, FALSE, FALSE, FALSE, FALSE,
    'CHRONOLOGICAL', 'COMBINED_LIST', 'SCHEDULE_ORDER',
    'WEEK',
    4, 5, 8,
    TRUE, TRUE, TRUE, TRUE, TRUE, TRUE,
    NULL, NULL, NULL, NULL,
    TIME '21:30:00', TIME '06:00:00'
WHERE EXISTS (SELECT 1 FROM users WHERE username = 'trainer_demo')
  AND NOT EXISTS (SELECT 1 FROM user_settings WHERE user_id = (SELECT id FROM users WHERE username = 'trainer_demo'));

-- gymadmin_demo
INSERT INTO user_settings (
    user_id, language, theme, easy_mode, color_blind_mode,
    disability_hearing, disability_mobility, disability_vision,
    share_recovery_signals, share_nutrition_signals, share_sleep_signals,
    share_fatigue_signals, share_weight_trend,
    calendar_task_ordering, calendar_task_layout, calendar_workout_ordering,
    calendar_view_preference,
    default_sets, default_rep_min, default_rep_max,
    preferred_equipment_bodyweight, preferred_equipment_dumbbell,
    preferred_equipment_barbell, preferred_equipment_machine,
    preferred_equipment_bands, preferred_equipment_kettlebell,
    macro_target_calories, macro_target_protein, macro_target_carbs, macro_target_fat,
    quiet_hours_start, quiet_hours_end
)
SELECT
    (SELECT id FROM users WHERE username = 'gymadmin_demo'),
    'en', 'SYSTEM', FALSE, FALSE,
    FALSE, FALSE, FALSE,
    FALSE, FALSE, FALSE, FALSE, FALSE,
    'CHRONOLOGICAL', 'COMBINED_LIST', 'SCHEDULE_ORDER',
    'MONTH',
    3, 8, 12,
    FALSE, FALSE, FALSE, TRUE, FALSE, FALSE,
    NULL, NULL, NULL, NULL,
    NULL, NULL
WHERE EXISTS (SELECT 1 FROM users WHERE username = 'gymadmin_demo')
  AND NOT EXISTS (SELECT 1 FROM user_settings WHERE user_id = (SELECT id FROM users WHERE username = 'gymadmin_demo'));

-- admin_demo
INSERT INTO user_settings (
    user_id, language, theme, easy_mode, color_blind_mode,
    disability_hearing, disability_mobility, disability_vision,
    share_recovery_signals, share_nutrition_signals, share_sleep_signals,
    share_fatigue_signals, share_weight_trend,
    calendar_task_ordering, calendar_task_layout, calendar_workout_ordering,
    calendar_view_preference,
    default_sets, default_rep_min, default_rep_max,
    preferred_equipment_bodyweight, preferred_equipment_dumbbell,
    preferred_equipment_barbell, preferred_equipment_machine,
    preferred_equipment_bands, preferred_equipment_kettlebell,
    macro_target_calories, macro_target_protein, macro_target_carbs, macro_target_fat,
    quiet_hours_start, quiet_hours_end
)
SELECT
    (SELECT id FROM users WHERE username = 'admin_demo'),
    'en', 'DARK', FALSE, FALSE,
    FALSE, FALSE, FALSE,
    FALSE, FALSE, FALSE, FALSE, FALSE,
    'CHRONOLOGICAL', 'COMBINED_LIST', 'SCHEDULE_ORDER',
    'MONTH',
    3, 8, 12,
    TRUE, FALSE, FALSE, FALSE, FALSE, FALSE,
    NULL, NULL, NULL, NULL,
    NULL, NULL
WHERE EXISTS (SELECT 1 FROM users WHERE username = 'admin_demo')
  AND NOT EXISTS (SELECT 1 FROM user_settings WHERE user_id = (SELECT id FROM users WHERE username = 'admin_demo'));

-- =========================================================================
-- USER POINTS / LEVELS
-- =========================================================================

INSERT INTO user_points (user_id, points, level, last_updated)
SELECT (SELECT id FROM users WHERE username = 'demo'), 1240, 8, CURRENT_DATE
WHERE EXISTS (SELECT 1 FROM users WHERE username = 'demo')
  AND NOT EXISTS (SELECT 1 FROM user_points WHERE user_id = (SELECT id FROM users WHERE username = 'demo'));

UPDATE user_points SET points = 1240, level = 8, last_updated = CURRENT_DATE
WHERE user_id = (SELECT id FROM users WHERE username = 'demo');

INSERT INTO user_points (user_id, points, level, last_updated)
SELECT (SELECT id FROM users WHERE username = 'demo2'), 310, 3, CURRENT_DATE
WHERE EXISTS (SELECT 1 FROM users WHERE username = 'demo2')
  AND NOT EXISTS (SELECT 1 FROM user_points WHERE user_id = (SELECT id FROM users WHERE username = 'demo2'));

UPDATE user_points SET points = 310, level = 3, last_updated = CURRENT_DATE
WHERE user_id = (SELECT id FROM users WHERE username = 'demo2');

INSERT INTO user_points (user_id, points, level, last_updated)
SELECT (SELECT id FROM users WHERE username = 'trainer_demo'), 850, 6, CURRENT_DATE
WHERE EXISTS (SELECT 1 FROM users WHERE username = 'trainer_demo')
  AND NOT EXISTS (SELECT 1 FROM user_points WHERE user_id = (SELECT id FROM users WHERE username = 'trainer_demo'));

INSERT INTO user_points (user_id, points, level, last_updated)
SELECT (SELECT id FROM users WHERE username = 'gymadmin_demo'), 420, 4, CURRENT_DATE
WHERE EXISTS (SELECT 1 FROM users WHERE username = 'gymadmin_demo')
  AND NOT EXISTS (SELECT 1 FROM user_points WHERE user_id = (SELECT id FROM users WHERE username = 'gymadmin_demo'));

INSERT INTO user_points (user_id, points, level, last_updated)
SELECT (SELECT id FROM users WHERE username = 'admin_demo'), 2000, 12, CURRENT_DATE
WHERE EXISTS (SELECT 1 FROM users WHERE username = 'admin_demo')
  AND NOT EXISTS (SELECT 1 FROM user_points WHERE user_id = (SELECT id FROM users WHERE username = 'admin_demo'));

-- =========================================================================
-- HEALTH RECORDS
-- =========================================================================

INSERT INTO health_records (user_id, baseline_date, weight_kg, height_cm, waist_cm,
    systolic_blood_pressure, diastolic_blood_pressure, cholesterol, bmi,
    waist_height_ratio, activity_level)
SELECT (SELECT id FROM users WHERE username = 'demo'),
    CURRENT_TIMESTAMP - INTERVAL '60' DAY,
    82, 178, 88,
    122, 78, 4.8,
    ROUND(CAST(82.0 / (1.78 * 1.78) AS NUMERIC), 2),
    ROUND(CAST(88.0 / 178.0 AS NUMERIC), 2),
    'MODERATELY_ACTIVE'
WHERE EXISTS (SELECT 1 FROM users WHERE username = 'demo')
  AND NOT EXISTS (SELECT 1 FROM health_records WHERE user_id = (SELECT id FROM users WHERE username = 'demo'));

INSERT INTO health_records (user_id, baseline_date, weight_kg, height_cm, waist_cm,
    systolic_blood_pressure, diastolic_blood_pressure, cholesterol, bmi,
    waist_height_ratio, activity_level)
SELECT (SELECT id FROM users WHERE username = 'demo2'),
    CURRENT_TIMESTAMP - INTERVAL '30' DAY,
    72, 165, 82,
    118, 74, 5.1,
    ROUND(CAST(72.0 / (1.65 * 1.65) AS NUMERIC), 2),
    ROUND(CAST(82.0 / 165.0 AS NUMERIC), 2),
    'LIGHTLY_ACTIVE'
WHERE EXISTS (SELECT 1 FROM users WHERE username = 'demo2')
  AND NOT EXISTS (SELECT 1 FROM health_records WHERE user_id = (SELECT id FROM users WHERE username = 'demo2'));

-- =========================================================================
-- TRAINER PROFILE
-- =========================================================================

INSERT INTO trainer_profiles (user_id, bio, specializations, location, primary_gym,
    price_per_session, instagram_url, tiktok_url, youtube_url,
    show_instagram, show_tiktok, show_youtube, show_linkedin, show_website,
    created_at, updated_at)
SELECT (SELECT id FROM users WHERE username = 'trainer_demo'),
    'Certified personal trainer with 8+ years experience in strength and conditioning. I specialise in helping everyday people build sustainable fitness habits that last.',
    'Strength & Conditioning, Weight Loss, Habit Coaching',
    'Cardiff, UK',
    'FitZone Gym Cardiff',
    45,
    'https://instagram.com/trainer_demo_fit',
    NULL,
    NULL,
    TRUE, FALSE, FALSE, FALSE, FALSE,
    CURRENT_TIMESTAMP - INTERVAL '90' DAY,
    CURRENT_TIMESTAMP - INTERVAL '7' DAY
WHERE EXISTS (SELECT 1 FROM users WHERE username = 'trainer_demo')
  AND NOT EXISTS (SELECT 1 FROM trainer_profiles WHERE user_id = (SELECT id FROM users WHERE username = 'trainer_demo'));

-- =========================================================================
-- GYM PROFILE
-- =========================================================================

INSERT INTO gym_profiles (user_id, gym_name, address, city, contact_name, contact_phone,
    created_at, updated_at)
SELECT (SELECT id FROM users WHERE username = 'gymadmin_demo'),
    'FitZone Gym Cardiff',
    '42 Capital Way',
    'Cardiff',
    'Gym Admin',
    '+44 29 2000 1234',
    CURRENT_TIMESTAMP - INTERVAL '180' DAY,
    CURRENT_TIMESTAMP - INTERVAL '14' DAY
WHERE EXISTS (SELECT 1 FROM users WHERE username = 'gymadmin_demo')
  AND NOT EXISTS (SELECT 1 FROM gym_profiles WHERE user_id = (SELECT id FROM users WHERE username = 'gymadmin_demo'));

-- =========================================================================
-- TRAINER-CLIENT LINK (demo is client of trainer_demo)
-- =========================================================================

INSERT INTO trainer_client_links (client_id, trainer_id, status,
    requested_at, activated_at, created_at, updated_at,
    coaching_phase, coaching_phase_label, coaching_phase_started_at, coaching_phase_updated_at)
SELECT
    (SELECT id FROM users WHERE username = 'demo'),
    (SELECT id FROM users WHERE username = 'trainer_demo'),
    'ACTIVE',
    CURRENT_TIMESTAMP - INTERVAL '45' DAY,
    CURRENT_TIMESTAMP - INTERVAL '44' DAY,
    CURRENT_TIMESTAMP - INTERVAL '45' DAY,
    CURRENT_TIMESTAMP - INTERVAL '7' DAY,
    'BUILD',
    'Strength Build Phase',
    CURRENT_TIMESTAMP - INTERVAL '20' DAY,
    CURRENT_TIMESTAMP - INTERVAL '20' DAY
WHERE EXISTS (SELECT 1 FROM users WHERE username = 'demo')
  AND EXISTS (SELECT 1 FROM users WHERE username = 'trainer_demo')
  AND NOT EXISTS (
    SELECT 1 FROM trainer_client_links
    WHERE client_id = (SELECT id FROM users WHERE username = 'demo')
      AND trainer_id = (SELECT id FROM users WHERE username = 'trainer_demo')
  );

-- =========================================================================
-- GOALS (demo user: ACTIVE, COMPLETED, PAUSED)
-- =========================================================================

INSERT INTO goals (owner_user_id, created_by_user_id, title, description, goal_type,
    target_metric_name, target_metric_value, target_metric_unit,
    start_date, target_date, status, priority, archived, created_at, updated_at)
SELECT u.id, u.id,
    'Complete 3 workouts per week for 8 weeks',
    'Build a consistent workout habit by hitting three sessions each week without missing more than one week.',
    'HABIT',
    'Weekly workouts', 3, 'sessions/week',
    CURRENT_DATE - INTERVAL '30' DAY,
    CURRENT_DATE + INTERVAL '26' DAY,
    'ACTIVE', 1, FALSE,
    CURRENT_TIMESTAMP - INTERVAL '30' DAY,
    CURRENT_TIMESTAMP - INTERVAL '3' DAY
FROM users u
WHERE u.username = 'demo'
  AND NOT EXISTS (
    SELECT 1 FROM goals g WHERE g.owner_user_id = u.id
      AND g.title = 'Complete 3 workouts per week for 8 weeks'
  );

INSERT INTO goals (owner_user_id, created_by_user_id, title, description, goal_type,
    target_metric_name, target_metric_value, target_metric_unit,
    start_date, target_date, status, priority, archived, created_at, updated_at)
SELECT u.id, u.id,
    'Increase squat to 100kg',
    'Progress my back squat from current estimated 75kg to 100kg over the next 12 weeks through progressive overload.',
    'STRENGTH',
    'Back squat 1RM', 100, 'kg',
    CURRENT_DATE - INTERVAL '60' DAY,
    CURRENT_DATE + INTERVAL '24' DAY,
    'ACTIVE', 2, FALSE,
    CURRENT_TIMESTAMP - INTERVAL '60' DAY,
    CURRENT_TIMESTAMP - INTERVAL '5' DAY
FROM users u
WHERE u.username = 'demo'
  AND NOT EXISTS (
    SELECT 1 FROM goals g WHERE g.owner_user_id = u.id
      AND g.title = 'Increase squat to 100kg'
  );

INSERT INTO goals (owner_user_id, created_by_user_id, title, description, goal_type,
    target_metric_name, target_metric_value, target_metric_unit,
    start_date, target_date, status, priority, archived, created_at, updated_at)
SELECT u.id, u.id,
    'Lose 5kg body weight',
    'Reduce body weight by 5kg over 16 weeks using a moderate calorie deficit combined with strength training.',
    'FAT_LOSS',
    'Body weight', 77, 'kg',
    CURRENT_DATE - INTERVAL '90' DAY,
    CURRENT_DATE - INTERVAL '2' DAY,
    'COMPLETED', 1, FALSE,
    CURRENT_TIMESTAMP - INTERVAL '90' DAY,
    CURRENT_TIMESTAMP - INTERVAL '2' DAY
FROM users u
WHERE u.username = 'demo'
  AND NOT EXISTS (
    SELECT 1 FROM goals g WHERE g.owner_user_id = u.id
      AND g.title = 'Lose 5kg body weight'
  );

INSERT INTO goals (owner_user_id, created_by_user_id, title, description, goal_type,
    target_metric_name, target_metric_value, target_metric_unit,
    start_date, target_date, status, priority, archived, created_at, updated_at)
SELECT u.id, u.id,
    'Run 5km without stopping',
    'Build endurance to complete a 5km run without stopping. Starting from 2km base.',
    'ENDURANCE',
    '5km run completion', 1, 'achievement',
    CURRENT_DATE - INTERVAL '50' DAY,
    CURRENT_DATE + INTERVAL '14' DAY,
    'PAUSED', 3, FALSE,
    CURRENT_TIMESTAMP - INTERVAL '50' DAY,
    CURRENT_TIMESTAMP - INTERVAL '15' DAY
FROM users u
WHERE u.username = 'demo'
  AND NOT EXISTS (
    SELECT 1 FROM goals g WHERE g.owner_user_id = u.id
      AND g.title = 'Run 5km without stopping'
  );

-- demo2 goals
INSERT INTO goals (owner_user_id, created_by_user_id, title, description, goal_type,
    target_metric_name, target_metric_value, target_metric_unit,
    start_date, target_date, status, priority, archived, created_at, updated_at)
SELECT u.id, u.id,
    'Exercise at least twice a week',
    'Establish a basic workout habit by completing at least 2 sessions every week.',
    'HABIT',
    'Weekly workouts', 2, 'sessions/week',
    CURRENT_DATE - INTERVAL '20' DAY,
    CURRENT_DATE + INTERVAL '40' DAY,
    'ACTIVE', 1, FALSE,
    CURRENT_TIMESTAMP - INTERVAL '20' DAY,
    CURRENT_TIMESTAMP - INTERVAL '2' DAY
FROM users u
WHERE u.username = 'demo2'
  AND NOT EXISTS (
    SELECT 1 FROM goals g WHERE g.owner_user_id = u.id
      AND g.title = 'Exercise at least twice a week'
  );

INSERT INTO goals (owner_user_id, created_by_user_id, title, description, goal_type,
    target_metric_name, target_metric_value, target_metric_unit,
    start_date, target_date, status, priority, archived, created_at, updated_at)
SELECT u.id, u.id,
    'Improve overall mobility',
    'Dedicate time each week to mobility work to reduce stiffness and improve movement quality.',
    'REHAB',
    'Mobility sessions', 2, 'sessions/week',
    CURRENT_DATE - INTERVAL '15' DAY,
    CURRENT_DATE + INTERVAL '45' DAY,
    'ACTIVE', 2, FALSE,
    CURRENT_TIMESTAMP - INTERVAL '15' DAY,
    CURRENT_TIMESTAMP - INTERVAL '1' DAY
FROM users u
WHERE u.username = 'demo2'
  AND NOT EXISTS (
    SELECT 1 FROM goals g WHERE g.owner_user_id = u.id
      AND g.title = 'Improve overall mobility'
  );

-- =========================================================================
-- GOAL CHECK-INS (weekly reflections for demo user's active goals)
-- =========================================================================

INSERT INTO goal_check_ins (goal_id, created_by_user_id, created_by_role,
    week_start_date, reflection, confidence_rating, trainer_comment, created_at)
SELECT g.id, u.id, 'CLIENT',
    CURRENT_DATE - INTERVAL '14' DAY,
    'Managed all three sessions this week. Energy was good on Monday but lower on Friday. Keeping the habit going.',
    4, NULL,
    CURRENT_TIMESTAMP - INTERVAL '7' DAY
FROM goals g
JOIN users u ON u.id = g.owner_user_id
WHERE u.username = 'demo' AND g.title = 'Complete 3 workouts per week for 8 weeks'
  AND NOT EXISTS (
    SELECT 1 FROM goal_check_ins ci
    WHERE ci.goal_id = g.id AND ci.week_start_date = CURRENT_DATE - INTERVAL '14' DAY
  );

INSERT INTO goal_check_ins (goal_id, created_by_user_id, created_by_role,
    week_start_date, reflection, confidence_rating, trainer_comment, created_at)
SELECT g.id, u.id, 'CLIENT',
    CURRENT_DATE - INTERVAL '7' DAY,
    'Hit all three sessions again. Squat feeling stronger - added 2.5kg. Feeling confident heading into next week.',
    5, 'Great consistency! Keep the progressive overload going. Consider adding a deload in 2 weeks.',
    CURRENT_TIMESTAMP - INTERVAL '2' DAY
FROM goals g
JOIN users u ON u.id = g.owner_user_id
WHERE u.username = 'demo' AND g.title = 'Complete 3 workouts per week for 8 weeks'
  AND NOT EXISTS (
    SELECT 1 FROM goal_check_ins ci
    WHERE ci.goal_id = g.id AND ci.week_start_date = CURRENT_DATE - INTERVAL '7' DAY
  );

INSERT INTO goal_check_ins (goal_id, created_by_user_id, created_by_role,
    week_start_date, reflection, confidence_rating, trainer_comment, created_at)
SELECT g.id, u.id, 'CLIENT',
    CURRENT_DATE - INTERVAL '21' DAY,
    'Squat at 82.5kg now - up from 80kg. Technique felt solid. Hip mobility still limiting depth slightly.',
    4, NULL,
    CURRENT_TIMESTAMP - INTERVAL '14' DAY
FROM goals g
JOIN users u ON u.id = g.owner_user_id
WHERE u.username = 'demo' AND g.title = 'Increase squat to 100kg'
  AND NOT EXISTS (
    SELECT 1 FROM goal_check_ins ci
    WHERE ci.goal_id = g.id AND ci.week_start_date = CURRENT_DATE - INTERVAL '21' DAY
  );

-- =========================================================================
-- USER STREAKS (TASK and WORKOUT for demo, demo2)
-- =========================================================================

INSERT INTO user_streaks (user_id, streak_type, current_count, longest_count, last_completed_date, updated_at)
SELECT (SELECT id FROM users WHERE username = 'demo'), 'WORKOUT', 12, 18, CURRENT_DATE - INTERVAL '1' DAY, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM users WHERE username = 'demo')
  AND NOT EXISTS (
    SELECT 1 FROM user_streaks WHERE user_id = (SELECT id FROM users WHERE username = 'demo') AND streak_type = 'WORKOUT'
  );

UPDATE user_streaks SET current_count = 12, longest_count = 18, last_completed_date = CURRENT_DATE - INTERVAL '1' DAY, updated_at = CURRENT_TIMESTAMP
WHERE user_id = (SELECT id FROM users WHERE username = 'demo') AND streak_type = 'WORKOUT';

INSERT INTO user_streaks (user_id, streak_type, current_count, longest_count, last_completed_date, updated_at)
SELECT (SELECT id FROM users WHERE username = 'demo'), 'TASK', 9, 21, CURRENT_DATE - INTERVAL '1' DAY, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM users WHERE username = 'demo')
  AND NOT EXISTS (
    SELECT 1 FROM user_streaks WHERE user_id = (SELECT id FROM users WHERE username = 'demo') AND streak_type = 'TASK'
  );

UPDATE user_streaks SET current_count = 9, longest_count = 21, last_completed_date = CURRENT_DATE - INTERVAL '1' DAY, updated_at = CURRENT_TIMESTAMP
WHERE user_id = (SELECT id FROM users WHERE username = 'demo') AND streak_type = 'TASK';

INSERT INTO user_streaks (user_id, streak_type, current_count, longest_count, last_completed_date, updated_at)
SELECT (SELECT id FROM users WHERE username = 'demo2'), 'WORKOUT', 3, 5, CURRENT_DATE - INTERVAL '2' DAY, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM users WHERE username = 'demo2')
  AND NOT EXISTS (
    SELECT 1 FROM user_streaks WHERE user_id = (SELECT id FROM users WHERE username = 'demo2') AND streak_type = 'WORKOUT'
  );

UPDATE user_streaks SET current_count = 3, longest_count = 5, last_completed_date = CURRENT_DATE - INTERVAL '2' DAY, updated_at = CURRENT_TIMESTAMP
WHERE user_id = (SELECT id FROM users WHERE username = 'demo2') AND streak_type = 'WORKOUT';

INSERT INTO user_streaks (user_id, streak_type, current_count, longest_count, last_completed_date, updated_at)
SELECT (SELECT id FROM users WHERE username = 'demo2'), 'TASK', 4, 7, CURRENT_DATE - INTERVAL '1' DAY, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM users WHERE username = 'demo2')
  AND NOT EXISTS (
    SELECT 1 FROM user_streaks WHERE user_id = (SELECT id FROM users WHERE username = 'demo2') AND streak_type = 'TASK'
  );

UPDATE user_streaks SET current_count = 4, longest_count = 7, last_completed_date = CURRENT_DATE - INTERVAL '1' DAY, updated_at = CURRENT_TIMESTAMP
WHERE user_id = (SELECT id FROM users WHERE username = 'demo2') AND streak_type = 'TASK';

-- =========================================================================
-- WEEKLY SUMMARIES (past 3 weeks for demo, 2 weeks for demo2)
-- =========================================================================

INSERT INTO weekly_summaries (user_id, week_start, summary_json, goals_json, missed_items_json, streaks_json, created_at)
SELECT u.id,
    CURRENT_DATE - INTERVAL '21' DAY,
    '{"tasksCompleted":5,"workoutsCompleted":3,"scheduledOccurrencesCompleted":4,"overallScore":80}',
    '{"active":2,"completedThisWeek":0,"newCheckIns":1}',
    '{"tasks":["Hydrate 2L target"],"workouts":[]}',
    '{"WORKOUT":{"current":9,"longest":18},"TASK":{"current":6,"longest":21}}',
    CURRENT_TIMESTAMP - INTERVAL '14' DAY
FROM users u
WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM weekly_summaries ws WHERE ws.user_id = u.id AND ws.week_start = CURRENT_DATE - INTERVAL '21' DAY);

INSERT INTO weekly_summaries (user_id, week_start, summary_json, goals_json, missed_items_json, streaks_json, created_at)
SELECT u.id,
    CURRENT_DATE - INTERVAL '14' DAY,
    '{"tasksCompleted":6,"workoutsCompleted":3,"scheduledOccurrencesCompleted":5,"overallScore":88}',
    '{"active":2,"completedThisWeek":0,"newCheckIns":2}',
    '{"tasks":[],"workouts":[]}',
    '{"WORKOUT":{"current":12,"longest":18},"TASK":{"current":9,"longest":21}}',
    CURRENT_TIMESTAMP - INTERVAL '7' DAY
FROM users u
WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM weekly_summaries ws WHERE ws.user_id = u.id AND ws.week_start = CURRENT_DATE - INTERVAL '14' DAY);

INSERT INTO weekly_summaries (user_id, week_start, summary_json, goals_json, missed_items_json, streaks_json, created_at)
SELECT u.id,
    CURRENT_DATE - INTERVAL '7' DAY,
    '{"tasksCompleted":4,"workoutsCompleted":2,"scheduledOccurrencesCompleted":3,"overallScore":72}',
    '{"active":2,"completedThisWeek":0,"newCheckIns":1}',
    '{"tasks":["Inbox zero (15 min)","Tidy workspace"],"workouts":["Demo Cardio Blast"]}',
    '{"WORKOUT":{"current":9,"longest":18},"TASK":{"current":9,"longest":21}}',
    CURRENT_TIMESTAMP - INTERVAL '1' DAY
FROM users u
WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM weekly_summaries ws WHERE ws.user_id = u.id AND ws.week_start = CURRENT_DATE - INTERVAL '7' DAY);

INSERT INTO weekly_summaries (user_id, week_start, summary_json, goals_json, missed_items_json, streaks_json, created_at)
SELECT u.id,
    CURRENT_DATE - INTERVAL '14' DAY,
    '{"tasksCompleted":3,"workoutsCompleted":2,"scheduledOccurrencesCompleted":2,"overallScore":62}',
    '{"active":1,"completedThisWeek":0,"newCheckIns":0}',
    '{"tasks":["Plan meals for two training days"],"workouts":[]}',
    '{"WORKOUT":{"current":3,"longest":5},"TASK":{"current":4,"longest":7}}',
    CURRENT_TIMESTAMP - INTERVAL '7' DAY
FROM users u
WHERE u.username = 'demo2'
  AND NOT EXISTS (SELECT 1 FROM weekly_summaries ws WHERE ws.user_id = u.id AND ws.week_start = CURRENT_DATE - INTERVAL '14' DAY);

INSERT INTO weekly_summaries (user_id, week_start, summary_json, goals_json, missed_items_json, streaks_json, created_at)
SELECT u.id,
    CURRENT_DATE - INTERVAL '7' DAY,
    '{"tasksCompleted":2,"workoutsCompleted":2,"scheduledOccurrencesCompleted":1,"overallScore":55}',
    '{"active":2,"completedThisWeek":0,"newCheckIns":0}',
    '{"tasks":["Mobility Flow 8 evening reset"],"workouts":[]}',
    '{"WORKOUT":{"current":3,"longest":5},"TASK":{"current":4,"longest":7}}',
    CURRENT_TIMESTAMP - INTERVAL '2' DAY
FROM users u
WHERE u.username = 'demo2'
  AND NOT EXISTS (SELECT 1 FROM weekly_summaries ws WHERE ws.user_id = u.id AND ws.week_start = CURRENT_DATE - INTERVAL '7' DAY);

-- =========================================================================
-- DAY HEALTH (past 5 days + today for demo and demo2)
-- =========================================================================

INSERT INTO day_health (user_id, date, primary_message, suggestion_a, suggestion_b, watch_out, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '4' DAY,
    'Your recovery is on track. Yesterday''s session completed well and your stress signals look manageable.',
    'Keep your warm-up thorough — 8 minutes minimum.',
    'Prioritise a protein-rich meal within 90 minutes of training.',
    NULL, CURRENT_TIMESTAMP
FROM users u WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM day_health WHERE user_id = u.id AND date = CURRENT_DATE - INTERVAL '4' DAY);

INSERT INTO day_health (user_id, date, primary_message, suggestion_a, suggestion_b, watch_out, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '3' DAY,
    'Moderate fatigue noted. This is a planned rest day — use it well.',
    'A 20-minute walk will help with active recovery.',
    'Hydrate consistently and aim for 8 hours sleep tonight.',
    'Avoid heavy loading today. Your body needs the break.', CURRENT_TIMESTAMP
FROM users u WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM day_health WHERE user_id = u.id AND date = CURRENT_DATE - INTERVAL '3' DAY);

INSERT INTO day_health (user_id, date, primary_message, suggestion_a, suggestion_b, watch_out, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '2' DAY,
    'Recovery looking strong. Today is a good day to push intensity.',
    'Focus on your key compound lifts first.',
    'Log your session mood afterwards to track patterns.',
    NULL, CURRENT_TIMESTAMP
FROM users u WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM day_health WHERE user_id = u.id AND date = CURRENT_DATE - INTERVAL '2' DAY);

INSERT INTO day_health (user_id, date, primary_message, suggestion_a, suggestion_b, watch_out, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '1' DAY,
    'Solid effort yesterday. Your consistency this week is above your 4-week average.',
    'Include a mobility cooldown after today''s session.',
    'Track your meals today — you''re close to your protein target.',
    NULL, CURRENT_TIMESTAMP
FROM users u WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM day_health WHERE user_id = u.id AND date = CURRENT_DATE - INTERVAL '1' DAY);

INSERT INTO day_health (user_id, date, primary_message, suggestion_a, suggestion_b, watch_out, updated_at)
SELECT u.id, CURRENT_DATE,
    'Great momentum this week! You''ve completed more than 80% of your planned activities.',
    'Today''s workout is scheduled — stick to your warm-up protocol.',
    'End the day with a brief reflection to lock in the habit.',
    NULL, CURRENT_TIMESTAMP
FROM users u WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM day_health WHERE user_id = u.id AND date = CURRENT_DATE);

-- demo2 day health
INSERT INTO day_health (user_id, date, primary_message, suggestion_a, suggestion_b, watch_out, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '2' DAY,
    'Good work completing your session. Building momentum from week 1.',
    'Add 5 minutes of mobility after your next workout.',
    'Try to get to bed 30 minutes earlier this week.',
    NULL, CURRENT_TIMESTAMP
FROM users u WHERE u.username = 'demo2'
  AND NOT EXISTS (SELECT 1 FROM day_health WHERE user_id = u.id AND date = CURRENT_DATE - INTERVAL '2' DAY);

INSERT INTO day_health (user_id, date, primary_message, suggestion_a, suggestion_b, watch_out, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '1' DAY,
    'You''re building a solid base. Consistency is the key at this stage.',
    'Focus on form over speed in every exercise.',
    'Drink water before, during, and after your sessions.',
    'Don''t push through sharp pain — modify if needed.', CURRENT_TIMESTAMP
FROM users u WHERE u.username = 'demo2'
  AND NOT EXISTS (SELECT 1 FROM day_health WHERE user_id = u.id AND date = CURRENT_DATE - INTERVAL '1' DAY);

INSERT INTO day_health (user_id, date, primary_message, suggestion_a, suggestion_b, watch_out, updated_at)
SELECT u.id, CURRENT_DATE,
    'Two sessions completed this week — you''re hitting your goal! Keep it up.',
    'Plan tomorrow''s nutrition in advance.',
    'Take 5 minutes to reflect on what''s working.',
    NULL, CURRENT_TIMESTAMP
FROM users u WHERE u.username = 'demo2'
  AND NOT EXISTS (SELECT 1 FROM day_health WHERE user_id = u.id AND date = CURRENT_DATE);

-- =========================================================================
-- DAILY NUTRITION LOGS (14 days history for demo, 10 days for demo2)
-- =========================================================================

INSERT INTO daily_nutrition_logs (user_id, log_date, calories, protein_grams, carbs_grams, fat_grams, fibre_grams, water_ml, notes, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '13' DAY, 2250, 165, 210, 68, 28, 2200, 'Pre-training day - higher carbs.', CURRENT_TIMESTAMP
FROM users u WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM daily_nutrition_logs WHERE user_id = u.id AND log_date = CURRENT_DATE - INTERVAL '13' DAY);

INSERT INTO daily_nutrition_logs (user_id, log_date, calories, protein_grams, carbs_grams, fat_grams, fibre_grams, water_ml, notes, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '12' DAY, 2380, 182, 228, 72, 30, 2500, 'Training day. Hit macros well.', CURRENT_TIMESTAMP
FROM users u WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM daily_nutrition_logs WHERE user_id = u.id AND log_date = CURRENT_DATE - INTERVAL '12' DAY);

INSERT INTO daily_nutrition_logs (user_id, log_date, calories, protein_grams, carbs_grams, fat_grams, fibre_grams, water_ml, notes, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '11' DAY, 2100, 155, 195, 65, 22, 1900, 'Rest day, lower appetite.', CURRENT_TIMESTAMP
FROM users u WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM daily_nutrition_logs WHERE user_id = u.id AND log_date = CURRENT_DATE - INTERVAL '11' DAY);

INSERT INTO daily_nutrition_logs (user_id, log_date, calories, protein_grams, carbs_grams, fat_grams, fibre_grams, water_ml, notes, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '10' DAY, 2420, 185, 232, 74, 32, 2600, 'Full training day, good energy.', CURRENT_TIMESTAMP
FROM users u WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM daily_nutrition_logs WHERE user_id = u.id AND log_date = CURRENT_DATE - INTERVAL '10' DAY);

INSERT INTO daily_nutrition_logs (user_id, log_date, calories, protein_grams, carbs_grams, fat_grams, fibre_grams, water_ml, notes, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '9' DAY, 2050, 148, 188, 62, 20, 2000, NULL, CURRENT_TIMESTAMP
FROM users u WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM daily_nutrition_logs WHERE user_id = u.id AND log_date = CURRENT_DATE - INTERVAL '9' DAY);

INSERT INTO daily_nutrition_logs (user_id, log_date, calories, protein_grams, carbs_grams, fat_grams, fibre_grams, water_ml, notes, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '8' DAY, 2310, 170, 215, 70, 25, 2300, 'Weekend training day.', CURRENT_TIMESTAMP
FROM users u WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM daily_nutrition_logs WHERE user_id = u.id AND log_date = CURRENT_DATE - INTERVAL '8' DAY);

INSERT INTO daily_nutrition_logs (user_id, log_date, calories, protein_grams, carbs_grams, fat_grams, fibre_grams, water_ml, notes, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '7' DAY, 2180, 160, 200, 67, 26, 2100, 'Active recovery day.', CURRENT_TIMESTAMP
FROM users u WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM daily_nutrition_logs WHERE user_id = u.id AND log_date = CURRENT_DATE - INTERVAL '7' DAY);

INSERT INTO daily_nutrition_logs (user_id, log_date, calories, protein_grams, carbs_grams, fat_grams, fibre_grams, water_ml, notes, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '6' DAY, 2390, 183, 225, 73, 31, 2400, 'Heavy squat session - carbs loaded.', CURRENT_TIMESTAMP
FROM users u WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM daily_nutrition_logs WHERE user_id = u.id AND log_date = CURRENT_DATE - INTERVAL '6' DAY);

INSERT INTO daily_nutrition_logs (user_id, log_date, calories, protein_grams, carbs_grams, fat_grams, fibre_grams, water_ml, notes, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '5' DAY, 2120, 158, 196, 64, 23, 2000, NULL, CURRENT_TIMESTAMP
FROM users u WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM daily_nutrition_logs WHERE user_id = u.id AND log_date = CURRENT_DATE - INTERVAL '5' DAY);

INSERT INTO daily_nutrition_logs (user_id, log_date, calories, protein_grams, carbs_grams, fat_grams, fibre_grams, water_ml, notes, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '4' DAY, 2450, 188, 235, 76, 33, 2700, 'Conditioning day + meal prep.', CURRENT_TIMESTAMP
FROM users u WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM daily_nutrition_logs WHERE user_id = u.id AND log_date = CURRENT_DATE - INTERVAL '4' DAY);

INSERT INTO daily_nutrition_logs (user_id, log_date, calories, protein_grams, carbs_grams, fat_grams, fibre_grams, water_ml, notes, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '3' DAY, 2090, 153, 190, 63, 21, 1800, 'Rest day, lower intake.', CURRENT_TIMESTAMP
FROM users u WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM daily_nutrition_logs WHERE user_id = u.id AND log_date = CURRENT_DATE - INTERVAL '3' DAY);

INSERT INTO daily_nutrition_logs (user_id, log_date, calories, protein_grams, carbs_grams, fat_grams, fibre_grams, water_ml, notes, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '2' DAY, 2360, 180, 220, 71, 29, 2400, 'Good session and good eating.', CURRENT_TIMESTAMP
FROM users u WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM daily_nutrition_logs WHERE user_id = u.id AND log_date = CURRENT_DATE - INTERVAL '2' DAY);

INSERT INTO daily_nutrition_logs (user_id, log_date, calories, protein_grams, carbs_grams, fat_grams, fibre_grams, water_ml, notes, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '1' DAY, 2280, 175, 210, 69, 27, 2300, NULL, CURRENT_TIMESTAMP
FROM users u WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM daily_nutrition_logs WHERE user_id = u.id AND log_date = CURRENT_DATE - INTERVAL '1' DAY);

INSERT INTO daily_nutrition_logs (user_id, log_date, calories, protein_grams, carbs_grams, fat_grams, fibre_grams, water_ml, notes, updated_at)
SELECT u.id, CURRENT_DATE, 1650, 125, 158, 52, 18, 1800, 'Still logging for today.', CURRENT_TIMESTAMP
FROM users u WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM daily_nutrition_logs WHERE user_id = u.id AND log_date = CURRENT_DATE);

-- demo2 nutrition (10 days)
INSERT INTO daily_nutrition_logs (user_id, log_date, calories, protein_grams, carbs_grams, fat_grams, fibre_grams, water_ml, notes, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '9' DAY, 1720, 115, 190, 52, 20, 1600, 'First week of tracking.', CURRENT_TIMESTAMP
FROM users u WHERE u.username = 'demo2'
  AND NOT EXISTS (SELECT 1 FROM daily_nutrition_logs WHERE user_id = u.id AND log_date = CURRENT_DATE - INTERVAL '9' DAY);

INSERT INTO daily_nutrition_logs (user_id, log_date, calories, protein_grams, carbs_grams, fat_grams, fibre_grams, water_ml, notes, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '8' DAY, 1840, 122, 210, 57, 22, 1800, 'Training day.', CURRENT_TIMESTAMP
FROM users u WHERE u.username = 'demo2'
  AND NOT EXISTS (SELECT 1 FROM daily_nutrition_logs WHERE user_id = u.id AND log_date = CURRENT_DATE - INTERVAL '8' DAY);

INSERT INTO daily_nutrition_logs (user_id, log_date, calories, protein_grams, carbs_grams, fat_grams, fibre_grams, water_ml, notes, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '7' DAY, 1680, 108, 185, 50, 18, 1500, NULL, CURRENT_TIMESTAMP
FROM users u WHERE u.username = 'demo2'
  AND NOT EXISTS (SELECT 1 FROM daily_nutrition_logs WHERE user_id = u.id AND log_date = CURRENT_DATE - INTERVAL '7' DAY);

INSERT INTO daily_nutrition_logs (user_id, log_date, calories, protein_grams, carbs_grams, fat_grams, fibre_grams, water_ml, notes, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '6' DAY, 1900, 128, 218, 59, 24, 2000, 'Felt good today.', CURRENT_TIMESTAMP
FROM users u WHERE u.username = 'demo2'
  AND NOT EXISTS (SELECT 1 FROM daily_nutrition_logs WHERE user_id = u.id AND log_date = CURRENT_DATE - INTERVAL '6' DAY);

INSERT INTO daily_nutrition_logs (user_id, log_date, calories, protein_grams, carbs_grams, fat_grams, fibre_grams, water_ml, notes, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '5' DAY, 1640, 105, 180, 48, 17, 1400, NULL, CURRENT_TIMESTAMP
FROM users u WHERE u.username = 'demo2'
  AND NOT EXISTS (SELECT 1 FROM daily_nutrition_logs WHERE user_id = u.id AND log_date = CURRENT_DATE - INTERVAL '5' DAY);

INSERT INTO daily_nutrition_logs (user_id, log_date, calories, protein_grams, carbs_grams, fat_grams, fibre_grams, water_ml, notes, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '4' DAY, 1780, 120, 198, 55, 21, 1700, 'Started hitting closer to target.', CURRENT_TIMESTAMP
FROM users u WHERE u.username = 'demo2'
  AND NOT EXISTS (SELECT 1 FROM daily_nutrition_logs WHERE user_id = u.id AND log_date = CURRENT_DATE - INTERVAL '4' DAY);

INSERT INTO daily_nutrition_logs (user_id, log_date, calories, protein_grams, carbs_grams, fat_grams, fibre_grams, water_ml, notes, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '3' DAY, 1820, 123, 205, 56, 22, 1900, NULL, CURRENT_TIMESTAMP
FROM users u WHERE u.username = 'demo2'
  AND NOT EXISTS (SELECT 1 FROM daily_nutrition_logs WHERE user_id = u.id AND log_date = CURRENT_DATE - INTERVAL '3' DAY);

INSERT INTO daily_nutrition_logs (user_id, log_date, calories, protein_grams, carbs_grams, fat_grams, fibre_grams, water_ml, notes, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '2' DAY, 1760, 118, 196, 54, 20, 1800, NULL, CURRENT_TIMESTAMP
FROM users u WHERE u.username = 'demo2'
  AND NOT EXISTS (SELECT 1 FROM daily_nutrition_logs WHERE user_id = u.id AND log_date = CURRENT_DATE - INTERVAL '2' DAY);

INSERT INTO daily_nutrition_logs (user_id, log_date, calories, protein_grams, carbs_grams, fat_grams, fibre_grams, water_ml, notes, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '1' DAY, 1850, 126, 208, 58, 23, 2000, 'Best day yet for nutrition.', CURRENT_TIMESTAMP
FROM users u WHERE u.username = 'demo2'
  AND NOT EXISTS (SELECT 1 FROM daily_nutrition_logs WHERE user_id = u.id AND log_date = CURRENT_DATE - INTERVAL '1' DAY);

INSERT INTO daily_nutrition_logs (user_id, log_date, calories, protein_grams, carbs_grams, fat_grams, fibre_grams, water_ml, notes, updated_at)
SELECT u.id, CURRENT_DATE, 1200, 88, 140, 38, 14, 1200, 'Still logging today.', CURRENT_TIMESTAMP
FROM users u WHERE u.username = 'demo2'
  AND NOT EXISTS (SELECT 1 FROM daily_nutrition_logs WHERE user_id = u.id AND log_date = CURRENT_DATE);

-- =========================================================================
-- DAILY FOCUS (past 7 days for demo, 5 days for demo2)
-- =========================================================================

INSERT INTO daily_focus (user_id, date, daily_focus, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '6' DAY, 'Nail the squat session and eat enough protein.', CURRENT_TIMESTAMP
FROM users u WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM daily_focus WHERE user_id = u.id AND date = CURRENT_DATE - INTERVAL '6' DAY);

INSERT INTO daily_focus (user_id, date, daily_focus, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '5' DAY, 'Active recovery and hydration.', CURRENT_TIMESTAMP
FROM users u WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM daily_focus WHERE user_id = u.id AND date = CURRENT_DATE - INTERVAL '5' DAY);

INSERT INTO daily_focus (user_id, date, daily_focus, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '4' DAY, 'Conditioning work — keep heart rate up for 25+ minutes.', CURRENT_TIMESTAMP
FROM users u WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM daily_focus WHERE user_id = u.id AND date = CURRENT_DATE - INTERVAL '4' DAY);

INSERT INTO daily_focus (user_id, date, daily_focus, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '3' DAY, 'Rest day. Stretch and sleep.', CURRENT_TIMESTAMP
FROM users u WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM daily_focus WHERE user_id = u.id AND date = CURRENT_DATE - INTERVAL '3' DAY);

INSERT INTO daily_focus (user_id, date, daily_focus, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '2' DAY, 'Push intensity on bench and row. Log everything.', CURRENT_TIMESTAMP
FROM users u WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM daily_focus WHERE user_id = u.id AND date = CURRENT_DATE - INTERVAL '2' DAY);

INSERT INTO daily_focus (user_id, date, daily_focus, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '1' DAY, 'Lower body strength. Don''t skip the warm-up.', CURRENT_TIMESTAMP
FROM users u WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM daily_focus WHERE user_id = u.id AND date = CURRENT_DATE - INTERVAL '1' DAY);

INSERT INTO daily_focus (user_id, date, daily_focus, updated_at)
SELECT u.id, CURRENT_DATE, 'Stay consistent. Every rep counts.', CURRENT_TIMESTAMP
FROM users u WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM daily_focus WHERE user_id = u.id AND date = CURRENT_DATE);

-- demo2 daily focus
INSERT INTO daily_focus (user_id, date, daily_focus, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '4' DAY, 'Complete the full workout without skipping sets.', CURRENT_TIMESTAMP
FROM users u WHERE u.username = 'demo2'
  AND NOT EXISTS (SELECT 1 FROM daily_focus WHERE user_id = u.id AND date = CURRENT_DATE - INTERVAL '4' DAY);

INSERT INTO daily_focus (user_id, date, daily_focus, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '3' DAY, 'Rest and recovery. Drink enough water.', CURRENT_TIMESTAMP
FROM users u WHERE u.username = 'demo2'
  AND NOT EXISTS (SELECT 1 FROM daily_focus WHERE user_id = u.id AND date = CURRENT_DATE - INTERVAL '3' DAY);

INSERT INTO daily_focus (user_id, date, daily_focus, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '2' DAY, 'Mobility session tonight.', CURRENT_TIMESTAMP
FROM users u WHERE u.username = 'demo2'
  AND NOT EXISTS (SELECT 1 FROM daily_focus WHERE user_id = u.id AND date = CURRENT_DATE - INTERVAL '2' DAY);

INSERT INTO daily_focus (user_id, date, daily_focus, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '1' DAY, 'Morning workout. Get it done early.', CURRENT_TIMESTAMP
FROM users u WHERE u.username = 'demo2'
  AND NOT EXISTS (SELECT 1 FROM daily_focus WHERE user_id = u.id AND date = CURRENT_DATE - INTERVAL '1' DAY);

INSERT INTO daily_focus (user_id, date, daily_focus, updated_at)
SELECT u.id, CURRENT_DATE, 'Keep the streak going. Two sessions this week already.', CURRENT_TIMESTAMP
FROM users u WHERE u.username = 'demo2'
  AND NOT EXISTS (SELECT 1 FROM daily_focus WHERE user_id = u.id AND date = CURRENT_DATE);

-- =========================================================================
-- ADAPTIVE FEEDBACK (past 5 days for demo, 3 days for demo2)
-- =========================================================================

INSERT INTO adaptive_feedback (user_id, date, feedback_text, tone, feedback_hash, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '4' DAY,
    'You''ve been consistently hitting your sessions this week. Your squat performance is trending up — keep the progressive overload going but don''t skip your warm-up sets. Nutrition looks on point.',
    'MOTIVATIONAL',
    'a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6e7f8a9b0c1d2e3f4a5b6c7d8e9f0a1b2',
    CURRENT_TIMESTAMP
FROM users u WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM adaptive_feedback WHERE user_id = u.id AND date = CURRENT_DATE - INTERVAL '4' DAY);

INSERT INTO adaptive_feedback (user_id, date, feedback_text, tone, feedback_hash, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '3' DAY,
    'Rest day well taken. Your body is recovering from a strong training block. Consider adding 10 minutes of mobility work to maintain flexibility gains.',
    'CALM',
    'b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6e7f8a9b0c1d2e3f4a5b6c7d8e9f0a1b2c3',
    CURRENT_TIMESTAMP
FROM users u WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM adaptive_feedback WHERE user_id = u.id AND date = CURRENT_DATE - INTERVAL '3' DAY);

INSERT INTO adaptive_feedback (user_id, date, feedback_text, tone, feedback_hash, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '2' DAY,
    'Strong week overall! You''ve completed 4 of 5 planned sessions. Missing one workout won''t derail progress — focus on finishing the week strong tomorrow.',
    'MOTIVATIONAL',
    'c3d4e5f6a7b8c9d0e1f2a3b4c5d6e7f8a9b0c1d2e3f4a5b6c7d8e9f0a1b2c3d4',
    CURRENT_TIMESTAMP
FROM users u WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM adaptive_feedback WHERE user_id = u.id AND date = CURRENT_DATE - INTERVAL '2' DAY);

INSERT INTO adaptive_feedback (user_id, date, feedback_text, tone, feedback_hash, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '1' DAY,
    'Excellent consistency this week! Your 9-day task streak shows real commitment. Keep protecting your recovery — sleep is your biggest performance lever right now.',
    'MOTIVATIONAL',
    'd4e5f6a7b8c9d0e1f2a3b4c5d6e7f8a9b0c1d2e3f4a5b6c7d8e9f0a1b2c3d4e5',
    CURRENT_TIMESTAMP
FROM users u WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM adaptive_feedback WHERE user_id = u.id AND date = CURRENT_DATE - INTERVAL '1' DAY);

INSERT INTO adaptive_feedback (user_id, date, feedback_text, tone, feedback_hash, updated_at)
SELECT u.id, CURRENT_DATE,
    'You''re in a great rhythm. All key metrics are pointing in the right direction. Today is a workout day — approach it with focus and the week will close on a high.',
    'MOTIVATIONAL',
    'e5f6a7b8c9d0e1f2a3b4c5d6e7f8a9b0c1d2e3f4a5b6c7d8e9f0a1b2c3d4e5f6',
    CURRENT_TIMESTAMP
FROM users u WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM adaptive_feedback WHERE user_id = u.id AND date = CURRENT_DATE);

-- demo2 feedback
INSERT INTO adaptive_feedback (user_id, date, feedback_text, tone, feedback_hash, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '2' DAY,
    'You''re three weeks in and the habit is starting to form. Two sessions per week is achievable — you''ve already proved it. Focus on showing up rather than perfect form at this stage.',
    'CALM',
    'f6a7b8c9d0e1f2a3b4c5d6e7f8a9b0c1d2e3f4a5b6c7d8e9f0a1b2c3d4e5f6a7',
    CURRENT_TIMESTAMP
FROM users u WHERE u.username = 'demo2'
  AND NOT EXISTS (SELECT 1 FROM adaptive_feedback WHERE user_id = u.id AND date = CURRENT_DATE - INTERVAL '2' DAY);

INSERT INTO adaptive_feedback (user_id, date, feedback_text, tone, feedback_hash, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '1' DAY,
    'Your nutrition is improving week on week. Protein intake is creeping up — this will support your recovery and make sessions feel more manageable.',
    'CALM',
    'a7b8c9d0e1f2a3b4c5d6e7f8a9b0c1d2e3f4a5b6c7d8e9f0a1b2c3d4e5f6a7b8',
    CURRENT_TIMESTAMP
FROM users u WHERE u.username = 'demo2'
  AND NOT EXISTS (SELECT 1 FROM adaptive_feedback WHERE user_id = u.id AND date = CURRENT_DATE - INTERVAL '1' DAY);

INSERT INTO adaptive_feedback (user_id, date, feedback_text, tone, feedback_hash, updated_at)
SELECT u.id, CURRENT_DATE,
    'You''ve already hit your weekly workout goal — today marks your third session! Every small win matters. Keep going.',
    'MOTIVATIONAL',
    'b8c9d0e1f2a3b4c5d6e7f8a9b0c1d2e3f4a5b6c7d8e9f0a1b2c3d4e5f6a7b8c9',
    CURRENT_TIMESTAMP
FROM users u WHERE u.username = 'demo2'
  AND NOT EXISTS (SELECT 1 FROM adaptive_feedback WHERE user_id = u.id AND date = CURRENT_DATE);

-- =========================================================================
-- DAILY COMPLETION (past 14 days of completion records)
-- =========================================================================

INSERT INTO daily_completion (user_id, date, completion_status, completion_percentage, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '13' DAY, 'GREEN', 100, CURRENT_TIMESTAMP FROM users u WHERE u.username = 'demo' AND NOT EXISTS (SELECT 1 FROM daily_completion WHERE user_id = u.id AND date = CURRENT_DATE - INTERVAL '13' DAY);

INSERT INTO daily_completion (user_id, date, completion_status, completion_percentage, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '12' DAY, 'GREEN', 100, CURRENT_TIMESTAMP FROM users u WHERE u.username = 'demo' AND NOT EXISTS (SELECT 1 FROM daily_completion WHERE user_id = u.id AND date = CURRENT_DATE - INTERVAL '12' DAY);

INSERT INTO daily_completion (user_id, date, completion_status, completion_percentage, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '11' DAY, 'ORANGE', 60, CURRENT_TIMESTAMP FROM users u WHERE u.username = 'demo' AND NOT EXISTS (SELECT 1 FROM daily_completion WHERE user_id = u.id AND date = CURRENT_DATE - INTERVAL '11' DAY);

INSERT INTO daily_completion (user_id, date, completion_status, completion_percentage, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '10' DAY, 'GREEN', 100, CURRENT_TIMESTAMP FROM users u WHERE u.username = 'demo' AND NOT EXISTS (SELECT 1 FROM daily_completion WHERE user_id = u.id AND date = CURRENT_DATE - INTERVAL '10' DAY);

INSERT INTO daily_completion (user_id, date, completion_status, completion_percentage, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '9' DAY, 'GREEN', 80, CURRENT_TIMESTAMP FROM users u WHERE u.username = 'demo' AND NOT EXISTS (SELECT 1 FROM daily_completion WHERE user_id = u.id AND date = CURRENT_DATE - INTERVAL '9' DAY);

INSERT INTO daily_completion (user_id, date, completion_status, completion_percentage, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '8' DAY, 'ORANGE', 50, CURRENT_TIMESTAMP FROM users u WHERE u.username = 'demo' AND NOT EXISTS (SELECT 1 FROM daily_completion WHERE user_id = u.id AND date = CURRENT_DATE - INTERVAL '8' DAY);

INSERT INTO daily_completion (user_id, date, completion_status, completion_percentage, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '7' DAY, 'GREEN', 100, CURRENT_TIMESTAMP FROM users u WHERE u.username = 'demo' AND NOT EXISTS (SELECT 1 FROM daily_completion WHERE user_id = u.id AND date = CURRENT_DATE - INTERVAL '7' DAY);

INSERT INTO daily_completion (user_id, date, completion_status, completion_percentage, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '6' DAY, 'GREEN', 100, CURRENT_TIMESTAMP FROM users u WHERE u.username = 'demo' AND NOT EXISTS (SELECT 1 FROM daily_completion WHERE user_id = u.id AND date = CURRENT_DATE - INTERVAL '6' DAY);

INSERT INTO daily_completion (user_id, date, completion_status, completion_percentage, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '5' DAY, 'ORANGE', 67, CURRENT_TIMESTAMP FROM users u WHERE u.username = 'demo' AND NOT EXISTS (SELECT 1 FROM daily_completion WHERE user_id = u.id AND date = CURRENT_DATE - INTERVAL '5' DAY);

INSERT INTO daily_completion (user_id, date, completion_status, completion_percentage, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '4' DAY, 'GREEN', 100, CURRENT_TIMESTAMP FROM users u WHERE u.username = 'demo' AND NOT EXISTS (SELECT 1 FROM daily_completion WHERE user_id = u.id AND date = CURRENT_DATE - INTERVAL '4' DAY);

INSERT INTO daily_completion (user_id, date, completion_status, completion_percentage, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '3' DAY, 'ORANGE', 75, CURRENT_TIMESTAMP FROM users u WHERE u.username = 'demo' AND NOT EXISTS (SELECT 1 FROM daily_completion WHERE user_id = u.id AND date = CURRENT_DATE - INTERVAL '3' DAY);

INSERT INTO daily_completion (user_id, date, completion_status, completion_percentage, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '2' DAY, 'GREEN', 100, CURRENT_TIMESTAMP FROM users u WHERE u.username = 'demo' AND NOT EXISTS (SELECT 1 FROM daily_completion WHERE user_id = u.id AND date = CURRENT_DATE - INTERVAL '2' DAY);

INSERT INTO daily_completion (user_id, date, completion_status, completion_percentage, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '1' DAY, 'GREEN', 100, CURRENT_TIMESTAMP FROM users u WHERE u.username = 'demo' AND NOT EXISTS (SELECT 1 FROM daily_completion WHERE user_id = u.id AND date = CURRENT_DATE - INTERVAL '1' DAY);

INSERT INTO daily_completion (user_id, date, completion_status, completion_percentage, updated_at)
SELECT u.id, CURRENT_DATE, 'ORANGE', 40, CURRENT_TIMESTAMP FROM users u WHERE u.username = 'demo' AND NOT EXISTS (SELECT 1 FROM daily_completion WHERE user_id = u.id AND date = CURRENT_DATE);

-- demo2 completion
INSERT INTO daily_completion (user_id, date, completion_status, completion_percentage, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '9' DAY, 'ORANGE', 50, CURRENT_TIMESTAMP FROM users u WHERE u.username = 'demo2' AND NOT EXISTS (SELECT 1 FROM daily_completion WHERE user_id = u.id AND date = CURRENT_DATE - INTERVAL '9' DAY);

INSERT INTO daily_completion (user_id, date, completion_status, completion_percentage, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '8' DAY, 'GREEN', 100, CURRENT_TIMESTAMP FROM users u WHERE u.username = 'demo2' AND NOT EXISTS (SELECT 1 FROM daily_completion WHERE user_id = u.id AND date = CURRENT_DATE - INTERVAL '8' DAY);

INSERT INTO daily_completion (user_id, date, completion_status, completion_percentage, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '7' DAY, 'GREY', 0, CURRENT_TIMESTAMP FROM users u WHERE u.username = 'demo2' AND NOT EXISTS (SELECT 1 FROM daily_completion WHERE user_id = u.id AND date = CURRENT_DATE - INTERVAL '7' DAY);

INSERT INTO daily_completion (user_id, date, completion_status, completion_percentage, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '6' DAY, 'GREEN', 100, CURRENT_TIMESTAMP FROM users u WHERE u.username = 'demo2' AND NOT EXISTS (SELECT 1 FROM daily_completion WHERE user_id = u.id AND date = CURRENT_DATE - INTERVAL '6' DAY);

INSERT INTO daily_completion (user_id, date, completion_status, completion_percentage, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '5' DAY, 'ORANGE', 33, CURRENT_TIMESTAMP FROM users u WHERE u.username = 'demo2' AND NOT EXISTS (SELECT 1 FROM daily_completion WHERE user_id = u.id AND date = CURRENT_DATE - INTERVAL '5' DAY);

INSERT INTO daily_completion (user_id, date, completion_status, completion_percentage, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '4' DAY, 'GREEN', 100, CURRENT_TIMESTAMP FROM users u WHERE u.username = 'demo2' AND NOT EXISTS (SELECT 1 FROM daily_completion WHERE user_id = u.id AND date = CURRENT_DATE - INTERVAL '4' DAY);

INSERT INTO daily_completion (user_id, date, completion_status, completion_percentage, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '3' DAY, 'ORANGE', 67, CURRENT_TIMESTAMP FROM users u WHERE u.username = 'demo2' AND NOT EXISTS (SELECT 1 FROM daily_completion WHERE user_id = u.id AND date = CURRENT_DATE - INTERVAL '3' DAY);

INSERT INTO daily_completion (user_id, date, completion_status, completion_percentage, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '2' DAY, 'GREEN', 100, CURRENT_TIMESTAMP FROM users u WHERE u.username = 'demo2' AND NOT EXISTS (SELECT 1 FROM daily_completion WHERE user_id = u.id AND date = CURRENT_DATE - INTERVAL '2' DAY);

INSERT INTO daily_completion (user_id, date, completion_status, completion_percentage, updated_at)
SELECT u.id, CURRENT_DATE - INTERVAL '1' DAY, 'GREEN', 100, CURRENT_TIMESTAMP FROM users u WHERE u.username = 'demo2' AND NOT EXISTS (SELECT 1 FROM daily_completion WHERE user_id = u.id AND date = CURRENT_DATE - INTERVAL '1' DAY);

INSERT INTO daily_completion (user_id, date, completion_status, completion_percentage, updated_at)
SELECT u.id, CURRENT_DATE, 'ORANGE', 50, CURRENT_TIMESTAMP FROM users u WHERE u.username = 'demo2' AND NOT EXISTS (SELECT 1 FROM daily_completion WHERE user_id = u.id AND date = CURRENT_DATE);

-- =========================================================================
-- COMPLETED CALENDAR TASKS (past 4 weeks of completed tasks for demo + demo2)
-- =========================================================================

-- demo completed tasks (past weeks)
INSERT INTO calendar_tasks (user_id, date, title, time, notes, is_exercise, completed, requires_log)
SELECT u.id, CURRENT_DATE - INTERVAL '13' DAY, 'Morning stretch routine', TIME '07:30:00', '10 minutes to start the day.', FALSE, TRUE, FALSE
FROM users u WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM calendar_tasks ct WHERE ct.user_id = u.id AND ct.date = CURRENT_DATE - INTERVAL '13' DAY AND ct.title = 'Morning stretch routine');

INSERT INTO calendar_tasks (user_id, date, title, time, notes, is_exercise, completed, requires_log)
SELECT u.id, CURRENT_DATE - INTERVAL '12' DAY, 'Review training programme', TIME '18:30:00', 'Check progress towards goals.', FALSE, TRUE, FALSE
FROM users u WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM calendar_tasks ct WHERE ct.user_id = u.id AND ct.date = CURRENT_DATE - INTERVAL '12' DAY AND ct.title = 'Review training programme');

INSERT INTO calendar_tasks (user_id, date, title, time, notes, is_exercise, completed, requires_log)
SELECT u.id, CURRENT_DATE - INTERVAL '11' DAY, 'Prepare nutrition log', TIME '08:00:00', 'Set up macros for the day ahead.', FALSE, TRUE, FALSE
FROM users u WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM calendar_tasks ct WHERE ct.user_id = u.id AND ct.date = CURRENT_DATE - INTERVAL '11' DAY AND ct.title = 'Prepare nutrition log');

INSERT INTO calendar_tasks (user_id, date, title, time, notes, is_exercise, completed, requires_log)
SELECT u.id, CURRENT_DATE - INTERVAL '10' DAY, 'Schedule deload week', TIME '19:00:00', 'Plan reduced load for next week.', FALSE, FALSE, FALSE
FROM users u WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM calendar_tasks ct WHERE ct.user_id = u.id AND ct.date = CURRENT_DATE - INTERVAL '10' DAY AND ct.title = 'Schedule deload week');

INSERT INTO calendar_tasks (user_id, date, title, time, notes, is_exercise, completed, requires_log)
SELECT u.id, CURRENT_DATE - INTERVAL '9' DAY, 'Walk (30 min)', TIME '12:00:00', 'Midday walk for active recovery.', TRUE, TRUE, FALSE
FROM users u WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM calendar_tasks ct WHERE ct.user_id = u.id AND ct.date = CURRENT_DATE - INTERVAL '9' DAY AND ct.title = 'Walk (30 min)');

INSERT INTO calendar_tasks (user_id, date, title, time, notes, is_exercise, completed, requires_log)
SELECT u.id, CURRENT_DATE - INTERVAL '8' DAY, 'Sleep 8 hours goal', TIME '22:00:00', 'No screens after 21:30.', FALSE, TRUE, FALSE
FROM users u WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM calendar_tasks ct WHERE ct.user_id = u.id AND ct.date = CURRENT_DATE - INTERVAL '8' DAY AND ct.title = 'Sleep 8 hours goal');

INSERT INTO calendar_tasks (user_id, date, title, time, notes, is_exercise, completed, requires_log)
SELECT u.id, CURRENT_DATE - INTERVAL '7' DAY, 'Weekly goal check-in', TIME '17:00:00', 'Reflect on the week and adjust plan.', FALSE, TRUE, FALSE
FROM users u WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM calendar_tasks ct WHERE ct.user_id = u.id AND ct.date = CURRENT_DATE - INTERVAL '7' DAY AND ct.title = 'Weekly goal check-in');

INSERT INTO calendar_tasks (user_id, date, title, time, notes, is_exercise, completed, requires_log)
SELECT u.id, CURRENT_DATE - INTERVAL '6' DAY, 'Mobility warm-up (10 min)', TIME '06:45:00', 'Hip and shoulder mobility.', TRUE, TRUE, FALSE
FROM users u WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM calendar_tasks ct WHERE ct.user_id = u.id AND ct.date = CURRENT_DATE - INTERVAL '6' DAY AND ct.title = 'Mobility warm-up (10 min)');

INSERT INTO calendar_tasks (user_id, date, title, time, notes, is_exercise, completed, requires_log)
SELECT u.id, CURRENT_DATE - INTERVAL '5' DAY, 'Post-workout protein shake', TIME '19:15:00', 'Log and consume immediately post-session.', FALSE, TRUE, FALSE
FROM users u WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM calendar_tasks ct WHERE ct.user_id = u.id AND ct.date = CURRENT_DATE - INTERVAL '5' DAY AND ct.title = 'Post-workout protein shake');

-- demo2 completed past tasks
INSERT INTO calendar_tasks (user_id, date, title, time, notes, is_exercise, completed, requires_log)
SELECT u.id, CURRENT_DATE - INTERVAL '9' DAY, 'First workout done', TIME '18:00:00', 'Completed the starter strength session.', FALSE, TRUE, FALSE
FROM users u WHERE u.username = 'demo2'
  AND NOT EXISTS (SELECT 1 FROM calendar_tasks ct WHERE ct.user_id = u.id AND ct.date = CURRENT_DATE - INTERVAL '9' DAY AND ct.title = 'First workout done');

INSERT INTO calendar_tasks (user_id, date, title, time, notes, is_exercise, completed, requires_log)
SELECT u.id, CURRENT_DATE - INTERVAL '8' DAY, 'Set up nutrition tracker', TIME '09:30:00', 'Started logging meals.', FALSE, TRUE, FALSE
FROM users u WHERE u.username = 'demo2'
  AND NOT EXISTS (SELECT 1 FROM calendar_tasks ct WHERE ct.user_id = u.id AND ct.date = CURRENT_DATE - INTERVAL '8' DAY AND ct.title = 'Set up nutrition tracker');

INSERT INTO calendar_tasks (user_id, date, title, time, notes, is_exercise, completed, requires_log)
SELECT u.id, CURRENT_DATE - INTERVAL '6' DAY, 'Second session this week', TIME '17:00:00', 'Hit weekly workout goal.', FALSE, TRUE, FALSE
FROM users u WHERE u.username = 'demo2'
  AND NOT EXISTS (SELECT 1 FROM calendar_tasks ct WHERE ct.user_id = u.id AND ct.date = CURRENT_DATE - INTERVAL '6' DAY AND ct.title = 'Second session this week');

INSERT INTO calendar_tasks (user_id, date, title, time, notes, is_exercise, completed, requires_log)
SELECT u.id, CURRENT_DATE - INTERVAL '4' DAY, 'Mobility session', TIME '19:30:00', 'Full 8-minute mobility flow completed.', TRUE, TRUE, FALSE
FROM users u WHERE u.username = 'demo2'
  AND NOT EXISTS (SELECT 1 FROM calendar_tasks ct WHERE ct.user_id = u.id AND ct.date = CURRENT_DATE - INTERVAL '4' DAY AND ct.title = 'Mobility session');

INSERT INTO calendar_tasks (user_id, date, title, time, notes, is_exercise, completed, requires_log)
SELECT u.id, CURRENT_DATE - INTERVAL '2' DAY, 'Drink 2L water', TIME '08:00:00', 'Track water intake throughout the day.', FALSE, TRUE, FALSE
FROM users u WHERE u.username = 'demo2'
  AND NOT EXISTS (SELECT 1 FROM calendar_tasks ct WHERE ct.user_id = u.id AND ct.date = CURRENT_DATE - INTERVAL '2' DAY AND ct.title = 'Drink 2L water');

-- =========================================================================
-- COMPLETED WORKOUT SESSIONS (past 4 weeks, more rich history)
-- =========================================================================

INSERT INTO workout_sessions (user_id, date, workout_id, name_snapshot, completed)
SELECT u.id, CURRENT_DATE - INTERVAL '14' DAY, w.id, w.name, TRUE
FROM users u
JOIN workouts w ON w.user_id = u.id AND w.name = 'Demo Full Body'
WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM workout_sessions ws WHERE ws.user_id = u.id AND ws.date = CURRENT_DATE - INTERVAL '14' DAY AND ws.workout_id = w.id);

INSERT INTO workout_sessions (user_id, date, workout_id, name_snapshot, completed)
SELECT u.id, CURRENT_DATE - INTERVAL '12' DAY, w.id, w.name, TRUE
FROM users u
JOIN workouts w ON w.user_id = u.id AND w.name = 'Demo Cardio Blast'
WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM workout_sessions ws WHERE ws.user_id = u.id AND ws.date = CURRENT_DATE - INTERVAL '12' DAY AND ws.workout_id = w.id);

INSERT INTO workout_sessions (user_id, date, workout_id, name_snapshot, completed)
SELECT u.id, CURRENT_DATE - INTERVAL '10' DAY, w.id, w.name, TRUE
FROM users u
JOIN workouts w ON w.user_id = u.id AND w.name = 'Demo Strength Builder'
WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM workout_sessions ws WHERE ws.user_id = u.id AND ws.date = CURRENT_DATE - INTERVAL '10' DAY AND ws.workout_id = w.id);

INSERT INTO workout_sessions (user_id, date, workout_id, name_snapshot, completed)
SELECT u.id, CURRENT_DATE - INTERVAL '9' DAY, w.id, w.name, TRUE
FROM users u
JOIN workouts w ON w.user_id = u.id AND w.name = 'Demo Full Body'
WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM workout_sessions ws WHERE ws.user_id = u.id AND ws.date = CURRENT_DATE - INTERVAL '9' DAY AND ws.workout_id = w.id);

INSERT INTO workout_sessions (user_id, date, workout_id, name_snapshot, completed)
SELECT u.id, CURRENT_DATE - INTERVAL '7' DAY, w.id, w.name, TRUE
FROM users u
JOIN workouts w ON w.user_id = u.id AND w.name = 'Demo Conditioning Mix'
WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM workout_sessions ws WHERE ws.user_id = u.id AND ws.date = CURRENT_DATE - INTERVAL '7' DAY AND ws.workout_id = w.id);

INSERT INTO workout_sessions (user_id, date, workout_id, name_snapshot, completed)
SELECT u.id, CURRENT_DATE - INTERVAL '6' DAY, w.id, w.name, TRUE
FROM users u
JOIN workouts w ON w.user_id = u.id AND w.name = 'Demo Full Body'
WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM workout_sessions ws WHERE ws.user_id = u.id AND ws.date = CURRENT_DATE - INTERVAL '6' DAY AND ws.workout_id = w.id);

INSERT INTO workout_sessions (user_id, date, workout_id, name_snapshot, completed)
SELECT u.id, CURRENT_DATE - INTERVAL '4' DAY, w.id, w.name, TRUE
FROM users u
JOIN workouts w ON w.user_id = u.id AND w.name = 'Demo Strength Builder'
WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM workout_sessions ws WHERE ws.user_id = u.id AND ws.date = CURRENT_DATE - INTERVAL '4' DAY AND ws.workout_id = w.id);

INSERT INTO workout_sessions (user_id, date, workout_id, name_snapshot, completed)
SELECT u.id, CURRENT_DATE - INTERVAL '2' DAY, w.id, w.name, TRUE
FROM users u
JOIN workouts w ON w.user_id = u.id AND w.name = 'Demo Full Body'
WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM workout_sessions ws WHERE ws.user_id = u.id AND ws.date = CURRENT_DATE - INTERVAL '2' DAY AND ws.workout_id = w.id);

-- demo2 completed sessions
INSERT INTO workout_sessions (user_id, date, workout_id, name_snapshot, completed)
SELECT u.id, CURRENT_DATE - INTERVAL '9' DAY, w.id, w.name, TRUE
FROM users u
JOIN workouts w ON w.user_id = u.id AND w.name = 'Demo2 Starter Strength'
WHERE u.username = 'demo2'
  AND NOT EXISTS (SELECT 1 FROM workout_sessions ws WHERE ws.user_id = u.id AND ws.date = CURRENT_DATE - INTERVAL '9' DAY AND ws.workout_id = w.id);

INSERT INTO workout_sessions (user_id, date, workout_id, name_snapshot, completed)
SELECT u.id, CURRENT_DATE - INTERVAL '7' DAY, w.id, w.name, TRUE
FROM users u
JOIN workouts w ON w.user_id = u.id AND w.name = 'Demo2 Cardio Core'
WHERE u.username = 'demo2'
  AND NOT EXISTS (SELECT 1 FROM workout_sessions ws WHERE ws.user_id = u.id AND ws.date = CURRENT_DATE - INTERVAL '7' DAY AND ws.workout_id = w.id);

INSERT INTO workout_sessions (user_id, date, workout_id, name_snapshot, completed)
SELECT u.id, CURRENT_DATE - INTERVAL '4' DAY, w.id, w.name, TRUE
FROM users u
JOIN workouts w ON w.user_id = u.id AND w.name = 'Demo2 Starter Strength'
WHERE u.username = 'demo2'
  AND NOT EXISTS (SELECT 1 FROM workout_sessions ws WHERE ws.user_id = u.id AND ws.date = CURRENT_DATE - INTERVAL '4' DAY AND ws.workout_id = w.id);

INSERT INTO workout_sessions (user_id, date, workout_id, name_snapshot, completed)
SELECT u.id, CURRENT_DATE - INTERVAL '2' DAY, w.id, w.name, TRUE
FROM users u
JOIN workouts w ON w.user_id = u.id AND w.name = 'Demo2 Cardio Core'
WHERE u.username = 'demo2'
  AND NOT EXISTS (SELECT 1 FROM workout_sessions ws WHERE ws.user_id = u.id AND ws.date = CURRENT_DATE - INTERVAL '2' DAY AND ws.workout_id = w.id);

-- =========================================================================
-- EXERCISE LOG (detailed history for demo and demo2)
-- =========================================================================

INSERT INTO exercise_log (user_id, date, mood_before, mood_after, confidence, comments, duration_minutes)
SELECT u.id, CURRENT_DATE - INTERVAL '12' DAY, 3, 5, 4, 'Cardio session felt great. Best run in a while.', 38
FROM users u WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM exercise_log el WHERE el.user_id = u.id AND el.date = CURRENT_DATE - INTERVAL '12' DAY);

INSERT INTO exercise_log (user_id, date, mood_before, mood_after, confidence, comments, duration_minutes)
SELECT u.id, CURRENT_DATE - INTERVAL '10' DAY, 4, 5, 5, 'Strength builder - new personal best on Romanian Deadlift.', 52
FROM users u WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM exercise_log el WHERE el.user_id = u.id AND el.date = CURRENT_DATE - INTERVAL '10' DAY);

INSERT INTO exercise_log (user_id, date, mood_before, mood_after, confidence, comments, duration_minutes)
SELECT u.id, CURRENT_DATE - INTERVAL '9' DAY, 2, 4, 3, 'Full body - started tired but finished strong.', 45
FROM users u WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM exercise_log el WHERE el.user_id = u.id AND el.date = CURRENT_DATE - INTERVAL '9' DAY);

INSERT INTO exercise_log (user_id, date, mood_before, mood_after, confidence, comments, duration_minutes)
SELECT u.id, CURRENT_DATE - INTERVAL '7' DAY, 4, 5, 4, 'Conditioning mix - great interval work.', 40
FROM users u WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM exercise_log el WHERE el.user_id = u.id AND el.date = CURRENT_DATE - INTERVAL '7' DAY);

INSERT INTO exercise_log (user_id, date, mood_before, mood_after, confidence, comments, duration_minutes)
SELECT u.id, CURRENT_DATE - INTERVAL '6' DAY, 3, 4, 4, 'Full body strength - steady progress on all lifts.', 48
FROM users u WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM exercise_log el WHERE el.user_id = u.id AND el.date = CURRENT_DATE - INTERVAL '6' DAY);

INSERT INTO exercise_log (user_id, date, mood_before, mood_after, confidence, comments, duration_minutes)
SELECT u.id, CURRENT_DATE - INTERVAL '4' DAY, 5, 5, 5, 'Strength builder - everything clicked today. Best session in weeks.', 55
FROM users u WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM exercise_log el WHERE el.user_id = u.id AND el.date = CURRENT_DATE - INTERVAL '4' DAY);

INSERT INTO exercise_log (user_id, date, mood_before, mood_after, confidence, comments, duration_minutes)
SELECT u.id, CURRENT_DATE - INTERVAL '2' DAY, 3, 5, 4, 'Full body - solid effort. Maintained all weights from last session.', 46
FROM users u WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM exercise_log el WHERE el.user_id = u.id AND el.date = CURRENT_DATE - INTERVAL '2' DAY);

-- demo2 exercise logs
INSERT INTO exercise_log (user_id, date, mood_before, mood_after, confidence, comments, duration_minutes)
SELECT u.id, CURRENT_DATE - INTERVAL '9' DAY, 2, 3, 2, 'First solo session. Harder than expected but completed it.', 32
FROM users u WHERE u.username = 'demo2'
  AND NOT EXISTS (SELECT 1 FROM exercise_log el WHERE el.user_id = u.id AND el.date = CURRENT_DATE - INTERVAL '9' DAY);

INSERT INTO exercise_log (user_id, date, mood_before, mood_after, confidence, comments, duration_minutes)
SELECT u.id, CURRENT_DATE - INTERVAL '7' DAY, 3, 4, 3, 'Cardio core - kept pace better than last time.', 28
FROM users u WHERE u.username = 'demo2'
  AND NOT EXISTS (SELECT 1 FROM exercise_log el WHERE el.user_id = u.id AND el.date = CURRENT_DATE - INTERVAL '7' DAY);

INSERT INTO exercise_log (user_id, date, mood_before, mood_after, confidence, comments, duration_minutes)
SELECT u.id, CURRENT_DATE - INTERVAL '4' DAY, 3, 4, 3, 'Starter strength - form improving on squats.', 35
FROM users u WHERE u.username = 'demo2'
  AND NOT EXISTS (SELECT 1 FROM exercise_log el WHERE el.user_id = u.id AND el.date = CURRENT_DATE - INTERVAL '4' DAY);

INSERT INTO exercise_log (user_id, date, mood_before, mood_after, confidence, comments, duration_minutes)
SELECT u.id, CURRENT_DATE - INTERVAL '2' DAY, 4, 5, 4, 'Cardio core - really pushed the run intervals. Best yet.', 30
FROM users u WHERE u.username = 'demo2'
  AND NOT EXISTS (SELECT 1 FROM exercise_log el WHERE el.user_id = u.id AND el.date = CURRENT_DATE - INTERVAL '2' DAY);

-- =========================================================================
-- COMPLETED SCHEDULE OCCURRENCES (past 2 weeks for demo and demo2)
-- =========================================================================

INSERT INTO schedule_occurrences (user_id, exercise_id, schedule_id, date, schedule_name, completed)
SELECT u.id, e.id, s.id, CURRENT_DATE - INTERVAL '14' DAY, s.name, TRUE
FROM users u
JOIN schedules s ON s.user_id = u.id AND s.name = 'Demo Daily Movement'
JOIN exercises e ON e.name = 'Walk'
WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM schedule_occurrences so WHERE so.user_id = u.id AND so.date = CURRENT_DATE - INTERVAL '14' DAY AND so.schedule_name = s.name);

INSERT INTO schedule_occurrences (user_id, exercise_id, schedule_id, date, schedule_name, completed)
SELECT u.id, e.id, s.id, CURRENT_DATE - INTERVAL '12' DAY, s.name, TRUE
FROM users u
JOIN schedules s ON s.user_id = u.id AND s.name = 'Demo Daily Movement'
JOIN exercises e ON e.name = 'Run'
WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM schedule_occurrences so WHERE so.user_id = u.id AND so.date = CURRENT_DATE - INTERVAL '12' DAY AND so.schedule_name = s.name);

INSERT INTO schedule_occurrences (user_id, exercise_id, schedule_id, date, schedule_name, completed)
SELECT u.id, e.id, s.id, CURRENT_DATE - INTERVAL '10' DAY, s.name, TRUE
FROM users u
JOIN schedules s ON s.user_id = u.id AND s.name = 'Demo Daily Movement'
JOIN exercises e ON e.name = 'Plank'
WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM schedule_occurrences so WHERE so.user_id = u.id AND so.date = CURRENT_DATE - INTERVAL '10' DAY AND so.schedule_name = s.name);

INSERT INTO schedule_occurrences (user_id, exercise_id, schedule_id, date, schedule_name, completed)
SELECT u.id, e.id, s.id, CURRENT_DATE - INTERVAL '9' DAY, s.name, TRUE
FROM users u
JOIN schedules s ON s.user_id = u.id AND s.name = 'Demo Daily Movement'
JOIN exercises e ON e.name = 'Walk'
WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM schedule_occurrences so WHERE so.user_id = u.id AND so.date = CURRENT_DATE - INTERVAL '9' DAY AND so.schedule_name = s.name);

INSERT INTO schedule_occurrences (user_id, exercise_id, schedule_id, date, schedule_name, completed)
SELECT u.id, e.id, s.id, CURRENT_DATE - INTERVAL '8' DAY, s.name, TRUE
FROM users u
JOIN schedules s ON s.user_id = u.id AND s.name = 'Demo Performance Week'
JOIN exercises e ON e.name = 'Walk'
WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM schedule_occurrences so WHERE so.user_id = u.id AND so.date = CURRENT_DATE - INTERVAL '8' DAY AND so.schedule_name = s.name);

INSERT INTO schedule_occurrences (user_id, exercise_id, schedule_id, date, schedule_name, completed)
SELECT u.id, e.id, s.id, CURRENT_DATE - INTERVAL '6' DAY, s.name, TRUE
FROM users u
JOIN schedules s ON s.user_id = u.id AND s.name = 'Demo Daily Movement'
JOIN exercises e ON e.name = 'Run'
WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM schedule_occurrences so WHERE so.user_id = u.id AND so.date = CURRENT_DATE - INTERVAL '6' DAY AND so.schedule_name = s.name);

INSERT INTO schedule_occurrences (user_id, exercise_id, schedule_id, date, schedule_name, completed)
SELECT u.id, e.id, s.id, CURRENT_DATE - INTERVAL '4' DAY, s.name, TRUE
FROM users u
JOIN schedules s ON s.user_id = u.id AND s.name = 'Demo Performance Week'
JOIN exercises e ON e.name = 'Walk'
WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM schedule_occurrences so WHERE so.user_id = u.id AND so.date = CURRENT_DATE - INTERVAL '4' DAY AND so.schedule_name = s.name);

INSERT INTO schedule_occurrences (user_id, exercise_id, schedule_id, date, schedule_name, completed)
SELECT u.id, e.id, s.id, CURRENT_DATE - INTERVAL '2' DAY, s.name, TRUE
FROM users u
JOIN schedules s ON s.user_id = u.id AND s.name = 'Demo Daily Movement'
JOIN exercises e ON e.name = 'Plank'
WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM schedule_occurrences so WHERE so.user_id = u.id AND so.date = CURRENT_DATE - INTERVAL '2' DAY AND so.schedule_name = s.name);

-- demo2 completed occurrences
INSERT INTO schedule_occurrences (user_id, exercise_id, schedule_id, date, schedule_name, completed)
SELECT u.id, e.id, s.id, CURRENT_DATE - INTERVAL '8' DAY, s.name, TRUE
FROM users u
JOIN schedules s ON s.user_id = u.id AND s.name = 'Demo2 Foundation Week'
JOIN exercises e ON e.name = 'Run'
WHERE u.username = 'demo2'
  AND NOT EXISTS (SELECT 1 FROM schedule_occurrences so WHERE so.user_id = u.id AND so.date = CURRENT_DATE - INTERVAL '8' DAY AND so.schedule_name = s.name);

INSERT INTO schedule_occurrences (user_id, exercise_id, schedule_id, date, schedule_name, completed)
SELECT u.id, e.id, s.id, CURRENT_DATE - INTERVAL '4' DAY, s.name, TRUE
FROM users u
JOIN schedules s ON s.user_id = u.id AND s.name = 'Demo2 Foundation Week'
JOIN exercises e ON e.name = 'Run'
WHERE u.username = 'demo2'
  AND NOT EXISTS (SELECT 1 FROM schedule_occurrences so WHERE so.user_id = u.id AND so.date = CURRENT_DATE - INTERVAL '4' DAY AND so.schedule_name = s.name);

-- =========================================================================
-- VAULT NOTES (training journal for demo and demo2)
-- =========================================================================

INSERT INTO vault_notes (user_id, note_type, title, content, linked_date, pinned, tags, mood, created_at, updated_at)
SELECT u.id, 'TRAINING',
    'Week 4 Strength Review',
    'Romanian Deadlift hit 90kg today — form was solid throughout. Noticing real strength gains over this block. The progressive overload is working. Benchmark: squat still at 85kg but improving each session.',
    CURRENT_DATE - INTERVAL '10' DAY,
    TRUE,
    'strength,progress,squat,deadlift',
    'GREAT',
    CURRENT_TIMESTAMP - INTERVAL '10' DAY,
    CURRENT_TIMESTAMP - INTERVAL '10' DAY
FROM users u WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM vault_notes vn WHERE vn.user_id = u.id AND vn.title = 'Week 4 Strength Review');

INSERT INTO vault_notes (user_id, note_type, title, content, linked_date, pinned, tags, mood, created_at, updated_at)
SELECT u.id, 'NUTRITION',
    'Macro Tracking Observations',
    'Running at about 2350 kcal most days. Protein hitting 170-185g consistently. Carbs a little lower on rest days (~190g). Energy levels are good on training days. Noticing better recovery when I eat more carbs pre-workout.',
    CURRENT_DATE - INTERVAL '7' DAY,
    FALSE,
    'nutrition,macros,protein,energy',
    'GOOD',
    CURRENT_TIMESTAMP - INTERVAL '7' DAY,
    CURRENT_TIMESTAMP - INTERVAL '7' DAY
FROM users u WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM vault_notes vn WHERE vn.user_id = u.id AND vn.title = 'Macro Tracking Observations');

INSERT INTO vault_notes (user_id, note_type, title, content, linked_date, pinned, tags, mood, created_at, updated_at)
SELECT u.id, 'GOAL',
    'Goal Progress - February',
    'Squat: 85kg (target 100kg) - on track for Q2. Consistency: 12-day workout streak going strong. Fat loss goal completed last month - down from 82kg to 77kg. Next focus: squat and endurance improvement.',
    CURRENT_DATE - INTERVAL '5' DAY,
    TRUE,
    'goals,squat,progress,milestone',
    'GREAT',
    CURRENT_TIMESTAMP - INTERVAL '5' DAY,
    CURRENT_TIMESTAMP - INTERVAL '5' DAY
FROM users u WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM vault_notes vn WHERE vn.user_id = u.id AND vn.title = 'Goal Progress - February');

INSERT INTO vault_notes (user_id, note_type, title, content, linked_date, pinned, tags, mood, created_at, updated_at)
SELECT u.id, 'REFLECTION',
    'What''s working this training block',
    'Training 3x per week is sustainable. The key changes: consistent warm-up, progressive overload on main lifts, and logging everything. Trainer check-ins every 2 weeks are helping keep me accountable. Sleep has improved since I started the 22:00 quiet hours.',
    CURRENT_DATE - INTERVAL '3' DAY,
    FALSE,
    'reflection,habit,consistency,sleep',
    'GOOD',
    CURRENT_TIMESTAMP - INTERVAL '3' DAY,
    CURRENT_TIMESTAMP - INTERVAL '3' DAY
FROM users u WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM vault_notes vn WHERE vn.user_id = u.id AND vn.title = 'What''s working this training block');

INSERT INTO vault_notes (user_id, note_type, title, content, linked_date, pinned, tags, mood, created_at, updated_at)
SELECT u.id, 'CHECKIN',
    'Weekly Check-in Notes - Trainer Session',
    'Trainer feedback: keep the squat frequency up. Work on hip mobility before each session. Diet looks solid. Suggested adding a deload every 6-8 weeks. Next check-in in 2 weeks.',
    CURRENT_DATE - INTERVAL '2' DAY,
    FALSE,
    'trainer,checkin,feedback,squat',
    'GOOD',
    CURRENT_TIMESTAMP - INTERVAL '2' DAY,
    CURRENT_TIMESTAMP - INTERVAL '2' DAY
FROM users u WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM vault_notes vn WHERE vn.user_id = u.id AND vn.title = 'Weekly Check-in Notes - Trainer Session');

-- demo2 vault notes
INSERT INTO vault_notes (user_id, note_type, title, content, linked_date, pinned, tags, mood, created_at, updated_at)
SELECT u.id, 'REFLECTION',
    'Starting out - first 3 weeks',
    'Three weeks in and I''m actually enjoying it. The bodyweight sessions are manageable. Mobility is the hardest part. Setting a goal to do 2 sessions per week is helping me stay committed without feeling overwhelmed.',
    CURRENT_DATE - INTERVAL '6' DAY,
    TRUE,
    'beginner,habit,reflection,mobility',
    'GOOD',
    CURRENT_TIMESTAMP - INTERVAL '6' DAY,
    CURRENT_TIMESTAMP - INTERVAL '6' DAY
FROM users u WHERE u.username = 'demo2'
  AND NOT EXISTS (SELECT 1 FROM vault_notes vn WHERE vn.user_id = u.id AND vn.title = 'Starting out - first 3 weeks');

INSERT INTO vault_notes (user_id, note_type, title, content, linked_date, pinned, tags, mood, created_at, updated_at)
SELECT u.id, 'TRAINING',
    'Session Notes - Cardio Core',
    'Run intervals: 6x90 seconds with 60-second rest. Managed all 6! Last week could only do 4. Form on the core exercises is improving. Need to work on breathing during planks.',
    CURRENT_DATE - INTERVAL '2' DAY,
    FALSE,
    'cardio,intervals,run,progress',
    'GREAT',
    CURRENT_TIMESTAMP - INTERVAL '2' DAY,
    CURRENT_TIMESTAMP - INTERVAL '2' DAY
FROM users u WHERE u.username = 'demo2'
  AND NOT EXISTS (SELECT 1 FROM vault_notes vn WHERE vn.user_id = u.id AND vn.title = 'Session Notes - Cardio Core');

-- =========================================================================
-- NOTES & NOTE FOLDERS
-- =========================================================================

INSERT INTO note_folders (user_id, name, colour, created_at, updated_at)
SELECT u.id, 'Training Plans', 'emerald', CURRENT_TIMESTAMP - INTERVAL '40' DAY, CURRENT_TIMESTAMP - INTERVAL '5' DAY
FROM users u WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM note_folders nf WHERE nf.user_id = u.id AND nf.name = 'Training Plans');

INSERT INTO note_folders (user_id, name, colour, created_at, updated_at)
SELECT u.id, 'Nutrition', 'amber', CURRENT_TIMESTAMP - INTERVAL '35' DAY, CURRENT_TIMESTAMP - INTERVAL '3' DAY
FROM users u WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM note_folders nf WHERE nf.user_id = u.id AND nf.name = 'Nutrition');

INSERT INTO note_folders (user_id, name, colour, created_at, updated_at)
SELECT u.id, 'General', 'slate', CURRENT_TIMESTAMP - INTERVAL '30' DAY, CURRENT_TIMESTAMP - INTERVAL '1' DAY
FROM users u WHERE u.username = 'demo2'
  AND NOT EXISTS (SELECT 1 FROM note_folders nf WHERE nf.user_id = u.id AND nf.name = 'General');

INSERT INTO notes (user_id, folder_id, title, content, colour, pinned, is_public, created_at, updated_at)
SELECT u.id, nf.id,
    'Current Training Block Overview',
    'Block: Strength Focus (Weeks 1-8)\n\nMain lifts:\n- Squat: 85kg -> target 100kg\n- Deadlift: 90kg\n- Bench: 72.5kg\n\nSchedule: Mon/Wed/Fri strength + Tue conditioning\n\nNext deload: week 6',
    'emerald', TRUE, FALSE,
    CURRENT_TIMESTAMP - INTERVAL '14' DAY,
    CURRENT_TIMESTAMP - INTERVAL '3' DAY
FROM users u
JOIN note_folders nf ON nf.user_id = u.id AND nf.name = 'Training Plans'
WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM notes n WHERE n.user_id = u.id AND n.title = 'Current Training Block Overview');

INSERT INTO notes (user_id, folder_id, title, content, colour, pinned, is_public, created_at, updated_at)
SELECT u.id, nf.id,
    'Daily Macro Targets',
    'Training days:\n- Calories: 2400\n- Protein: 180g\n- Carbs: 220g\n- Fat: 70g\n\nRest days:\n- Calories: 2100\n- Protein: 170g\n- Carbs: 190g\n- Fat: 65g\n\nPre-workout: oats + banana\nPost-workout: shake + chicken',
    'amber', FALSE, FALSE,
    CURRENT_TIMESTAMP - INTERVAL '20' DAY,
    CURRENT_TIMESTAMP - INTERVAL '7' DAY
FROM users u
JOIN note_folders nf ON nf.user_id = u.id AND nf.name = 'Nutrition'
WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM notes n WHERE n.user_id = u.id AND n.title = 'Daily Macro Targets');

INSERT INTO notes (user_id, folder_id, title, content, colour, pinned, is_public, created_at, updated_at)
SELECT u.id, nf.id,
    'My workout routine',
    'Week 1 plan:\n- Mon: Starter Strength (squats, push-ups, rows)\n- Wed: Rest/mobility\n- Fri: Cardio Core (run intervals + planks)\n\nRules I''m following:\n1. Always warm up first\n2. 3 sets of each\n3. Log how I feel after each session',
    'slate', TRUE, FALSE,
    CURRENT_TIMESTAMP - INTERVAL '10' DAY,
    CURRENT_TIMESTAMP - INTERVAL '2' DAY
FROM users u
JOIN note_folders nf ON nf.user_id = u.id AND nf.name = 'General'
WHERE u.username = 'demo2'
  AND NOT EXISTS (SELECT 1 FROM notes n WHERE n.user_id = u.id AND n.title = 'My workout routine');

-- =========================================================================
-- NOTIFICATIONS (sample for all users)
-- =========================================================================

INSERT INTO notifications (user_id, type, title, message, cta_url, created_at, read_at, dismissed_at)
SELECT u.id, 'INFO', 'Welcome to your training dashboard!',
    'Your profile is set up and ready. Start by checking your calendar for today''s planned activities.',
    '/calendar', CURRENT_TIMESTAMP - INTERVAL '40' DAY,
    CURRENT_TIMESTAMP - INTERVAL '39' DAY, NULL
FROM users u WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM notifications n WHERE n.user_id = u.id AND n.title = 'Welcome to your training dashboard!');

INSERT INTO notifications (user_id, type, title, message, cta_url, created_at, read_at, dismissed_at)
SELECT u.id, 'INFO', 'New goal check-in available',
    'Your trainer has left feedback on your squat goal check-in. Review it in Goals.',
    '/goals', CURRENT_TIMESTAMP - INTERVAL '2' DAY,
    NULL, NULL
FROM users u WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM notifications n WHERE n.user_id = u.id AND n.title = 'New goal check-in available');

INSERT INTO notifications (user_id, type, title, message, cta_url, created_at, read_at, dismissed_at)
SELECT u.id, 'INFO', '12-day workout streak! Keep going!',
    'You''ve completed workouts 12 days in a row. Your longest streak is 18 — can you beat it?',
    '/calendar', CURRENT_TIMESTAMP - INTERVAL '1' DAY,
    NULL, NULL
FROM users u WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM notifications n WHERE n.user_id = u.id AND n.title = '12-day workout streak! Keep going!');

INSERT INTO notifications (user_id, type, title, message, cta_url, created_at, read_at, dismissed_at)
SELECT u.id, 'INFO', 'Welcome! Let''s get started.',
    'Your account is ready. Your first workout is scheduled — check your calendar to see what''s planned.',
    '/calendar', CURRENT_TIMESTAMP - INTERVAL '20' DAY,
    CURRENT_TIMESTAMP - INTERVAL '19' DAY, NULL
FROM users u WHERE u.username = 'demo2'
  AND NOT EXISTS (SELECT 1 FROM notifications n WHERE n.user_id = u.id AND n.title = 'Welcome! Let''s get started.');

INSERT INTO notifications (user_id, type, title, message, cta_url, created_at, read_at, dismissed_at)
SELECT u.id, 'INFO', 'Weekly goal hit — 2 sessions complete!',
    'You reached your weekly workout goal this week. Check your progress in Goals.',
    '/goals', CURRENT_TIMESTAMP - INTERVAL '3' DAY,
    NULL, NULL
FROM users u WHERE u.username = 'demo2'
  AND NOT EXISTS (SELECT 1 FROM notifications n WHERE n.user_id = u.id AND n.title = 'Weekly goal hit — 2 sessions complete!');

INSERT INTO notifications (user_id, type, title, message, cta_url, created_at, read_at, dismissed_at)
SELECT u.id, 'INFO', 'New client request',
    'Demo User has requested to be linked as your client. Review and accept in your client management area.',
    '/trainer/clients', CURRENT_TIMESTAMP - INTERVAL '45' DAY,
    CURRENT_TIMESTAMP - INTERVAL '44' DAY, NULL
FROM users u WHERE u.username = 'trainer_demo'
  AND NOT EXISTS (SELECT 1 FROM notifications n WHERE n.user_id = u.id AND n.title = 'New client request');

INSERT INTO notifications (user_id, type, title, message, cta_url, created_at, read_at, dismissed_at)
SELECT u.id, 'INFO', 'Client milestone — 12 day streak',
    'Your client Demo User has hit a 12-day workout streak. Great coaching results!',
    '/trainer/clients', CURRENT_TIMESTAMP - INTERVAL '1' DAY,
    NULL, NULL
FROM users u WHERE u.username = 'trainer_demo'
  AND NOT EXISTS (SELECT 1 FROM notifications n WHERE n.user_id = u.id AND n.title = 'Client milestone — 12 day streak');

INSERT INTO notifications (user_id, type, title, message, cta_url, created_at, read_at, dismissed_at)
SELECT u.id, 'INFO', 'Platform update available',
    'New features have been released. Check the admin dashboard for details.',
    '/admin', CURRENT_TIMESTAMP - INTERVAL '5' DAY,
    NULL, NULL
FROM users u WHERE u.username = 'admin_demo'
  AND NOT EXISTS (SELECT 1 FROM notifications n WHERE n.user_id = u.id AND n.title = 'Platform update available');

INSERT INTO notifications (user_id, type, title, message, cta_url, created_at, read_at, dismissed_at)
SELECT u.id, 'INFO', 'Gym profile set up complete',
    'Your gym profile for FitZone Gym Cardiff is live. Trainers can now associate with your gym.',
    '/gym/profile', CURRENT_TIMESTAMP - INTERVAL '90' DAY,
    CURRENT_TIMESTAMP - INTERVAL '89' DAY, NULL
FROM users u WHERE u.username = 'gymadmin_demo'
  AND NOT EXISTS (SELECT 1 FROM notifications n WHERE n.user_id = u.id AND n.title = 'Gym profile set up complete');
