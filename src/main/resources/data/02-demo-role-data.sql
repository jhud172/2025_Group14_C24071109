-- Consolidated demo role and history seed
-- Merged from:
--   03-demo-extended-data.sql
--   04-demo-role-expansion.sql
--   07-demo-role-readiness.sql
-- Purpose:
--   profile customisation, health/history, role expansion, and role readiness data.

-- ========================================================================
-- BEGIN 03-demo-extended-data.sql
-- ========================================================================

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
    quiet_hours_start = TIME '22:00:00', quiet_hours_end = TIME '07:00:00',
    profile_banner_theme = 'SUNSET',
    profile_ring_style = 'KING_CROWN',
    profile_card_back_style = 'SUNBURST',
    profile_text_color = '#15BC7C',
    profile_bio_text_color = '#0F59B3',
    profile_milestone_keys = 'LEVEL_5'
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

INSERT INTO gym_profiles (user_id, gym_name, gym_code, address, city, contact_name, contact_phone,
    created_at, updated_at)
SELECT (SELECT id FROM users WHERE username = 'gymadmin_demo'),
    'FitZone Gym Cardiff',
    '4827001938456202',
    '42 Capital Way',
    'Cardiff',
    'Gym Admin',
    '+44 29 2000 1234',
    CURRENT_TIMESTAMP - INTERVAL '180' DAY,
    CURRENT_TIMESTAMP - INTERVAL '14' DAY
WHERE EXISTS (SELECT 1 FROM users WHERE username = 'gymadmin_demo')
  AND NOT EXISTS (SELECT 1 FROM gym_profiles WHERE user_id = (SELECT id FROM users WHERE username = 'gymadmin_demo'));

UPDATE gym_profiles
SET gym_code = '4827001938456202',
    address = '42 Capital Way',
    city = 'Cardiff',
    contact_name = 'Gym Admin',
    contact_phone = '+44 29 2000 1234',
    updated_at = CURRENT_TIMESTAMP - INTERVAL '6' DAY
WHERE user_id = (SELECT id FROM users WHERE username = 'gymadmin_demo');

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

-- ========================================================================
-- BEGIN 04-demo-role-expansion.sql
-- ========================================================================

-- Expanded demo role dataset (idempotent)
-- Keeps `demo` as a lapsed / non-premium account while preserving stored premium profile styling.
-- Adds premium role-focused accounts:
--   demo_client
--   demo_trainer
--   demo_gym
--   demo_admin

-- =========================================================================
-- ACCOUNTS + ROLES
-- =========================================================================

INSERT INTO users (public_id, email, first_name, last_name, username, password, enabled, subscription_status, role)
SELECT '91d6f4d1-1fd6-4b74-a8fb-9d88f27a1101', 'demo_client@example.com', 'Avery', 'Client', 'demo_client', '$2a$10$2EZk8xjJekcabhOOKPsxtuHWvgrgWunYC2v57bCNiEk8c8HxHedH6', TRUE, TRUE, 'CLIENT'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'demo_client');

INSERT INTO users (public_id, email, first_name, last_name, username, password, enabled, subscription_status, role)
SELECT '1bdf8537-cd1a-42b8-b84d-68c1a07f9df2', 'demo_trainer@example.com', 'Jordan', 'Trainer', 'demo_trainer', '$2a$10$2EZk8xjJekcabhOOKPsxtuHWvgrgWunYC2v57bCNiEk8c8HxHedH6', TRUE, TRUE, 'TRAINER'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'demo_trainer');

INSERT INTO users (public_id, email, first_name, last_name, username, password, enabled, subscription_status, role)
SELECT '854dc415-8475-45d0-9c69-8c3c8523ff57', 'demo_gym@example.com', 'Harbour', 'Gym', 'demo_gym', '$2a$10$2EZk8xjJekcabhOOKPsxtuHWvgrgWunYC2v57bCNiEk8c8HxHedH6', TRUE, TRUE, 'GYM_ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'demo_gym');

INSERT INTO users (public_id, email, first_name, last_name, username, password, enabled, subscription_status, role)
SELECT '678e7c50-2e98-44bb-8b56-929b1b2defbb', 'demo_admin@example.com', 'Morgan', 'Admin', 'demo_admin', '$2a$10$2EZk8xjJekcabhOOKPsxtuHWvgrgWunYC2v57bCNiEk8c8HxHedH6', TRUE, TRUE, 'PLATFORM_ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'demo_admin');

INSERT INTO users_roles (username, role_id)
SELECT rs.username, r.role_id
FROM (
    SELECT 'demo_client' AS username, 'USER' AS role_name
    UNION ALL SELECT 'demo_client', 'CLIENT'
    UNION ALL SELECT 'demo_trainer', 'USER'
    UNION ALL SELECT 'demo_trainer', 'TRAINER'
    UNION ALL SELECT 'demo_gym', 'USER'
    UNION ALL SELECT 'demo_gym', 'GYM_ADMIN'
    UNION ALL SELECT 'demo_admin', 'USER'
    UNION ALL SELECT 'demo_admin', 'PLATFORM_ADMIN'
) rs
JOIN roles r ON r.name = rs.role_name
WHERE NOT EXISTS (
    SELECT 1
    FROM users_roles ur
    WHERE ur.username = rs.username
      AND ur.role_id = r.role_id
);

UPDATE users
SET subscription_status = CASE
        WHEN username IN ('demo_client', 'demo_trainer', 'demo_gym', 'demo_admin') THEN TRUE
        WHEN username = 'demo' THEN FALSE
        ELSE subscription_status
    END,
    trainer_verified = CASE WHEN username = 'demo_trainer' THEN TRUE ELSE trainer_verified END,
    email_verified = CASE WHEN username IN ('demo', 'demo_client', 'demo_trainer', 'demo_gym', 'demo_admin') THEN TRUE ELSE email_verified END,
    email_verified_at = CASE WHEN username IN ('demo', 'demo_client', 'demo_trainer', 'demo_gym', 'demo_admin') THEN CURRENT_TIMESTAMP ELSE email_verified_at END,
    phone_verified = CASE WHEN username IN ('demo', 'demo_client', 'demo_trainer', 'demo_gym', 'demo_admin') THEN TRUE ELSE phone_verified END,
    phone_verified_at = CASE WHEN username IN ('demo', 'demo_client', 'demo_trainer', 'demo_gym', 'demo_admin') THEN CURRENT_TIMESTAMP ELSE phone_verified_at END,
    phone_number = CASE
        WHEN username = 'demo_client' THEN '7911123456'
        WHEN username = 'demo_trainer' THEN '7922234567'
        WHEN username = 'demo_gym' THEN '7933345678'
        WHEN username = 'demo_admin' THEN '7944456789'
        ELSE phone_number
    END,
    date_of_birth = CASE
        WHEN username = 'demo_client' THEN DATE '1998-04-18'
        WHEN username = 'demo_trainer' THEN DATE '1992-09-08'
        WHEN username = 'demo_gym' THEN DATE '1990-02-21'
        WHEN username = 'demo_admin' THEN DATE '1988-11-04'
        ELSE date_of_birth
    END,
    bio = CASE
        WHEN username = 'demo_client' THEN 'Premium client demo account with a busy but consistent training rhythm, a live trainer link, and a fully styled profile setup.'
        WHEN username = 'demo_trainer' THEN 'Strength and conditioning coach demo account showing an active trainer workflow with premium profile styling and a live client book.'
        WHEN username = 'demo_gym' THEN 'Gym admin demo account with premium platform access, organised operations, and a full planning/test dataset.'
        WHEN username = 'demo_admin' THEN 'Platform admin demo account used for moderation, support, and development-state checks across the system.'
        ELSE bio
    END,
    profile_image_url = CASE
        WHEN username = 'demo_client' THEN '/img/chat/charlie-avatar.svg'
        WHEN username = 'demo_trainer' THEN '/img/Products/Short_Sleeve_Top/Short_Sleeve_Front.jpg'
        WHEN username = 'demo_gym' THEN '/img/Products/Long_Sleeve_Top/Long_Sleeve_Front.jpg'
        WHEN username = 'demo_admin' THEN '/img/brand/tab_logo.png'
        ELSE profile_image_url
    END
WHERE username IN ('demo', 'demo_client', 'demo_trainer', 'demo_gym', 'demo_admin');

-- =========================================================================
-- PREMIUM / NON-PREMIUM STATE
-- =========================================================================

INSERT INTO platform_subscriptions (user_id, plan, status, current_period_end, cancel_at_period_end)
SELECT u.id,
       CASE WHEN u.username = 'demo' THEN 'MONTHLY' ELSE 'MONTHLY' END,
       CASE WHEN u.username = 'demo' THEN 'CANCELLED' ELSE 'ACTIVE' END,
       CASE WHEN u.username = 'demo' THEN CURRENT_TIMESTAMP - INTERVAL '14' DAY ELSE CURRENT_TIMESTAMP + INTERVAL '90' DAY END,
       CASE WHEN u.username = 'demo' THEN TRUE ELSE FALSE END
FROM users u
WHERE u.username IN ('demo', 'demo_client', 'demo_trainer', 'demo_gym', 'demo_admin')
  AND NOT EXISTS (SELECT 1 FROM platform_subscriptions ps WHERE ps.user_id = u.id);

UPDATE platform_subscriptions
SET plan = 'MONTHLY',
    status = CASE
        WHEN user_id = (SELECT id FROM users WHERE username = 'demo') THEN 'CANCELLED'
        ELSE 'ACTIVE'
    END,
    current_period_end = CASE
        WHEN user_id = (SELECT id FROM users WHERE username = 'demo') THEN CURRENT_TIMESTAMP - INTERVAL '14' DAY
        ELSE CURRENT_TIMESTAMP + INTERVAL '90' DAY
    END,
    cancel_at_period_end = CASE
        WHEN user_id = (SELECT id FROM users WHERE username = 'demo') THEN TRUE
        ELSE FALSE
    END
WHERE user_id IN (
    SELECT id FROM users WHERE username IN ('demo', 'demo_client', 'demo_trainer', 'demo_gym', 'demo_admin')
);

-- =========================================================================
-- SETTINGS / PROFILE CUSTOMISER / POINTS
-- =========================================================================

INSERT INTO user_settings (user_id, language, theme, easy_mode, calendar_view_preference, default_sets,
    default_rep_min, default_rep_max, preferred_equipment_bodyweight, preferred_equipment_dumbbell,
    preferred_equipment_barbell, preferred_equipment_machine, preferred_equipment_bands, preferred_equipment_kettlebell,
    macro_target_calories, macro_target_protein, macro_target_carbs, macro_target_fat, quiet_hours_start, quiet_hours_end,
    monthly_workout_target, weekly_summary_metrics, profile_banner_theme, profile_ring_style, profile_card_back_style,
    profile_text_color, profile_bio_text_color, profile_milestone_keys, dashboard_immersion_enabled,
    weather_temperature_unit, weather_display_mode, time_display_format, quick_preferences_completed)
SELECT u.id, 'en', 'LIGHT', FALSE, 'WEEK', 4,
    6, 10, TRUE, TRUE,
    TRUE, FALSE, TRUE, TRUE,
    2450, 185, 235, 72, TIME '22:30:00', TIME '06:30:00',
    18, 'WORKOUTS_COMPLETED,MEALS_LOGGED,WORKOUT_STREAK', 'LAGOON', 'AURORA_PULSE', 'NEBULA',
    '#F8FAFC', '#D1FAE5', 'VERIFIED_EMAIL,VERIFIED_PHONE,PREMIUM_MEMBER,LEVEL_5', TRUE,
    'CELSIUS', 'GRAPH', 'TWELVE_HOUR', TRUE
FROM users u
WHERE u.username = 'demo_client'
  AND NOT EXISTS (SELECT 1 FROM user_settings us WHERE us.user_id = u.id);

INSERT INTO user_settings (user_id, language, theme, easy_mode, calendar_view_preference, default_sets,
    default_rep_min, default_rep_max, preferred_equipment_bodyweight, preferred_equipment_dumbbell,
    preferred_equipment_barbell, preferred_equipment_machine, preferred_equipment_bands, preferred_equipment_kettlebell,
    quiet_hours_start, quiet_hours_end, monthly_workout_target, weekly_summary_metrics,
    profile_banner_theme, profile_ring_style, profile_card_back_style, profile_text_color,
    profile_bio_text_color, profile_milestone_keys, dashboard_immersion_enabled,
    weather_temperature_unit, weather_display_mode, time_display_format, quick_preferences_completed)
SELECT u.id, 'en', 'LIGHT', FALSE, 'WEEK', 5,
    5, 8, TRUE, TRUE,
    TRUE, TRUE, TRUE, TRUE,
    TIME '21:30:00', TIME '05:45:00', 16, 'WORKOUTS_COMPLETED,TASKS_COMPLETED,WORKOUT_STREAK',
    'MIDNIGHT', 'COMET_TRAIL', 'CARBON', '#F8FAFC',
    '#CBD5E1', 'VERIFIED_EMAIL,PREMIUM_MEMBER,LEVEL_10', TRUE,
    'CELSIUS', 'VISUAL', 'TWELVE_HOUR', TRUE
FROM users u
WHERE u.username = 'demo_trainer'
  AND NOT EXISTS (SELECT 1 FROM user_settings us WHERE us.user_id = u.id);

INSERT INTO user_settings (user_id, language, theme, easy_mode, calendar_view_preference, default_sets,
    default_rep_min, default_rep_max, preferred_equipment_machine, quiet_hours_start, quiet_hours_end,
    monthly_workout_target, weekly_summary_metrics, profile_banner_theme, profile_ring_style, profile_card_back_style,
    profile_text_color, profile_bio_text_color, profile_milestone_keys, dashboard_immersion_enabled,
    weather_temperature_unit, weather_display_mode, time_display_format, quick_preferences_completed)
SELECT u.id, 'en', 'LIGHT', FALSE, 'MONTH', 3,
    8, 12, TRUE, TIME '22:00:00', TIME '06:00:00',
    10, 'TASKS_COMPLETED,WORKOUTS_COMPLETED,MEALS_LOGGED', 'SUNSET', 'KING_CROWN', 'CIRCUIT',
    '#FEF3C7', '#FDE68A', 'VERIFIED_EMAIL,PREMIUM_MEMBER,LEVEL_5', TRUE,
    'CELSIUS', 'VISUAL', 'TWELVE_HOUR', TRUE
FROM users u
WHERE u.username = 'demo_gym'
  AND NOT EXISTS (SELECT 1 FROM user_settings us WHERE us.user_id = u.id);

INSERT INTO user_settings (user_id, language, theme, easy_mode, calendar_view_preference, default_sets,
    default_rep_min, default_rep_max, quiet_hours_start, quiet_hours_end, monthly_workout_target,
    weekly_summary_metrics, profile_banner_theme, profile_ring_style, profile_card_back_style,
    profile_text_color, profile_bio_text_color, profile_milestone_keys, dashboard_immersion_enabled,
    weather_temperature_unit, weather_display_mode, time_display_format, quick_preferences_completed)
SELECT u.id, 'en', 'LIGHT', FALSE, 'MONTH', 3,
    8, 12, TIME '23:00:00', TIME '06:30:00', 8,
    'TASKS_COMPLETED,WORKOUTS_COMPLETED,WORKOUT_STREAK', 'OCEAN', 'CYBER_ARMS', 'MATRIX',
    '#E0F2FE', '#BFDBFE', 'VERIFIED_EMAIL,PREMIUM_MEMBER,LEVEL_10', TRUE,
    'CELSIUS', 'GRAPH', 'TWELVE_HOUR', TRUE
FROM users u
WHERE u.username = 'demo_admin'
  AND NOT EXISTS (SELECT 1 FROM user_settings us WHERE us.user_id = u.id);

UPDATE user_settings
SET profile_banner_theme = CASE
        WHEN user_id = (SELECT id FROM users WHERE username = 'demo_client') THEN 'LAGOON'
        WHEN user_id = (SELECT id FROM users WHERE username = 'demo_trainer') THEN 'MIDNIGHT'
        WHEN user_id = (SELECT id FROM users WHERE username = 'demo_gym') THEN 'SUNSET'
        WHEN user_id = (SELECT id FROM users WHERE username = 'demo_admin') THEN 'OCEAN'
        WHEN user_id = (SELECT id FROM users WHERE username = 'demo') THEN 'ROSE'
        ELSE profile_banner_theme
    END,
    profile_ring_style = CASE
        WHEN user_id = (SELECT id FROM users WHERE username = 'demo_client') THEN 'AURORA_PULSE'
        WHEN user_id = (SELECT id FROM users WHERE username = 'demo_trainer') THEN 'COMET_TRAIL'
        WHEN user_id = (SELECT id FROM users WHERE username = 'demo_gym') THEN 'KING_CROWN'
        WHEN user_id = (SELECT id FROM users WHERE username = 'demo_admin') THEN 'CYBER_ARMS'
        WHEN user_id = (SELECT id FROM users WHERE username = 'demo') THEN 'SOLAR_FLARE'
        ELSE profile_ring_style
    END,
    profile_card_back_style = CASE
        WHEN user_id = (SELECT id FROM users WHERE username = 'demo_client') THEN 'NEBULA'
        WHEN user_id = (SELECT id FROM users WHERE username = 'demo_trainer') THEN 'CARBON'
        WHEN user_id = (SELECT id FROM users WHERE username = 'demo_gym') THEN 'CIRCUIT'
        WHEN user_id = (SELECT id FROM users WHERE username = 'demo_admin') THEN 'MATRIX'
        WHEN user_id = (SELECT id FROM users WHERE username = 'demo') THEN 'NEBULA'
        ELSE profile_card_back_style
    END,
    profile_text_color = CASE
        WHEN user_id = (SELECT id FROM users WHERE username = 'demo_gym') THEN '#FEF3C7'
        WHEN user_id = (SELECT id FROM users WHERE username = 'demo_admin') THEN '#E0F2FE'
        ELSE '#F8FAFC'
    END,
    profile_bio_text_color = CASE
        WHEN user_id = (SELECT id FROM users WHERE username = 'demo_client') THEN '#D1FAE5'
        WHEN user_id = (SELECT id FROM users WHERE username = 'demo_trainer') THEN '#CBD5E1'
        WHEN user_id = (SELECT id FROM users WHERE username = 'demo_gym') THEN '#FDE68A'
        WHEN user_id = (SELECT id FROM users WHERE username = 'demo_admin') THEN '#BFDBFE'
        WHEN user_id = (SELECT id FROM users WHERE username = 'demo') THEN '#E2E8F0'
        ELSE profile_bio_text_color
    END
WHERE user_id IN (
    SELECT id FROM users WHERE username IN ('demo', 'demo_client', 'demo_trainer', 'demo_gym', 'demo_admin')
);

INSERT INTO user_points (user_id, points, level, last_updated)
SELECT u.id, ps.points, ps.level, CURRENT_DATE
FROM (
    SELECT 'demo_client' AS username, 1840 AS points, 10 AS level
    UNION ALL SELECT 'demo_trainer', 2260, 13
    UNION ALL SELECT 'demo_gym', 1290, 8
    UNION ALL SELECT 'demo_admin', 2680, 15
) ps
JOIN users u ON u.username = ps.username
WHERE NOT EXISTS (SELECT 1 FROM user_points up WHERE up.user_id = u.id);

UPDATE user_points
SET points = CASE
        WHEN user_id = (SELECT id FROM users WHERE username = 'demo_client') THEN 1840
        WHEN user_id = (SELECT id FROM users WHERE username = 'demo_trainer') THEN 2260
        WHEN user_id = (SELECT id FROM users WHERE username = 'demo_gym') THEN 1290
        WHEN user_id = (SELECT id FROM users WHERE username = 'demo_admin') THEN 2680
        ELSE points
    END,
    level = CASE
        WHEN user_id = (SELECT id FROM users WHERE username = 'demo_client') THEN 10
        WHEN user_id = (SELECT id FROM users WHERE username = 'demo_trainer') THEN 13
        WHEN user_id = (SELECT id FROM users WHERE username = 'demo_gym') THEN 8
        WHEN user_id = (SELECT id FROM users WHERE username = 'demo_admin') THEN 15
        ELSE level
    END,
    last_updated = CURRENT_DATE
WHERE user_id IN (
    SELECT id FROM users WHERE username IN ('demo_client', 'demo_trainer', 'demo_gym', 'demo_admin')
);

-- =========================================================================
-- ROLE PROFILES / LINKS
-- =========================================================================

INSERT INTO trainer_profiles (user_id, bio, trainer_code, specializations, location, primary_gym, price_per_session,
    instagram_url, youtube_url, show_instagram, show_tiktok, show_youtube, show_linkedin, show_website, created_at, updated_at)
SELECT (SELECT id FROM users WHERE username = 'demo_trainer'),
    'Premium trainer demo profile with structured coaching blocks, form review emphasis, and sustainable performance planning.',
    '240781903465',
    'Strength Coaching, Performance, Habit Building',
    'Cardiff, UK',
    'Harbour Strength Club',
    55,
    'https://instagram.com/demo_trainer_coach',
    'https://youtube.com/@demo_trainer_coach',
    TRUE, FALSE, TRUE, FALSE, FALSE,
    CURRENT_TIMESTAMP - INTERVAL '120' DAY,
    CURRENT_TIMESTAMP - INTERVAL '2' DAY
WHERE EXISTS (SELECT 1 FROM users WHERE username = 'demo_trainer')
  AND NOT EXISTS (SELECT 1 FROM trainer_profiles WHERE user_id = (SELECT id FROM users WHERE username = 'demo_trainer'));

UPDATE trainer_profiles
SET trainer_code = '240781903465',
    specializations = 'Strength Coaching, Performance, Habit Building',
    location = 'Cardiff, UK',
    primary_gym = 'Harbour Strength Club',
    price_per_session = 55,
    updated_at = CURRENT_TIMESTAMP - INTERVAL '2' DAY
WHERE user_id = (SELECT id FROM users WHERE username = 'demo_trainer');

INSERT INTO gym_profiles (user_id, gym_name, gym_code, address, city, contact_name, contact_phone, created_at, updated_at)
SELECT (SELECT id FROM users WHERE username = 'demo_gym'),
    'Harbour Strength Club',
    '4827001938456203',
    '18 Dock Street',
    'Cardiff',
    'Harbour Operations',
    '+44 29 5555 0101',
    CURRENT_TIMESTAMP - INTERVAL '180' DAY,
    CURRENT_TIMESTAMP - INTERVAL '4' DAY
WHERE EXISTS (SELECT 1 FROM users WHERE username = 'demo_gym')
  AND NOT EXISTS (SELECT 1 FROM gym_profiles WHERE user_id = (SELECT id FROM users WHERE username = 'demo_gym'));

UPDATE gym_profiles
SET gym_code = '4827001938456203',
    address = '18 Dock Street',
    city = 'Cardiff',
    contact_name = 'Harbour Operations',
    contact_phone = '+44 29 5555 0101',
    updated_at = CURRENT_TIMESTAMP - INTERVAL '4' DAY
WHERE user_id = (SELECT id FROM users WHERE username = 'demo_gym');

INSERT INTO trainer_client_links (client_id, trainer_id, status, requested_at, activated_at, created_at, updated_at,
    coaching_phase, coaching_phase_label, coaching_phase_started_at, coaching_phase_updated_at)
SELECT
    (SELECT id FROM users WHERE username = 'demo_client'),
    (SELECT id FROM users WHERE username = 'demo_trainer'),
    'ACTIVE',
    CURRENT_TIMESTAMP - INTERVAL '50' DAY,
    CURRENT_TIMESTAMP - INTERVAL '48' DAY,
    CURRENT_TIMESTAMP - INTERVAL '50' DAY,
    CURRENT_TIMESTAMP - INTERVAL '1' DAY,
    'BUILD',
    'Spring Strength Build',
    CURRENT_TIMESTAMP - INTERVAL '18' DAY,
    CURRENT_TIMESTAMP - INTERVAL '5' DAY
WHERE EXISTS (SELECT 1 FROM users WHERE username = 'demo_client')
  AND EXISTS (SELECT 1 FROM users WHERE username = 'demo_trainer')
  AND NOT EXISTS (
    SELECT 1 FROM trainer_client_links
    WHERE client_id = (SELECT id FROM users WHERE username = 'demo_client')
      AND trainer_id = (SELECT id FROM users WHERE username = 'demo_trainer')
  );

-- =========================================================================
-- EXERCISES / WORKOUTS / SCHEDULES
-- =========================================================================

INSERT INTO exercises (name, category, description, difficulty, type)
SELECT 'Rower Sprint', 'Cardio', 'Short rowing burst for power and conditioning.', 3, 'cardio'
WHERE NOT EXISTS (SELECT 1 FROM exercises WHERE name = 'Rower Sprint');

INSERT INTO exercises (name, category, description, difficulty, type)
SELECT 'Trap Bar Deadlift', 'Strength', 'Heavy hinge pattern for posterior-chain development.', 3, 'strength'
WHERE NOT EXISTS (SELECT 1 FROM exercises WHERE name = 'Trap Bar Deadlift');

INSERT INTO exercises (name, category, description, difficulty, type)
SELECT 'Bike Recovery Ride', 'Cardio', 'Low-intensity cardio recovery block.', 1, 'cardio'
WHERE NOT EXISTS (SELECT 1 FROM exercises WHERE name = 'Bike Recovery Ride');

INSERT INTO exercises (name, category, description, difficulty, type)
SELECT 'Sled Push', 'Conditioning', 'Power and work-capacity pushing pattern.', 3, 'strength'
WHERE NOT EXISTS (SELECT 1 FROM exercises WHERE name = 'Sled Push');

INSERT INTO workouts (user_id, name, notes)
SELECT u.id, ws.workout_name, ws.notes
FROM (
    SELECT 'demo_client' AS username, 'Client Premium Push' AS workout_name, 'Flagship premium client training block.' AS notes
    UNION ALL SELECT 'demo_trainer', 'Trainer Performance Lift', 'Coach personal performance and testing session.'
    UNION ALL SELECT 'demo_gym', 'Gym Floor Conditioning', 'Operations-day conditioning workout for the gym demo account.'
    UNION ALL SELECT 'demo_admin', 'Admin Mobility Reset', 'Short recovery session for the admin demo account.'
) ws
JOIN users u ON u.username = ws.username
WHERE NOT EXISTS (
    SELECT 1 FROM workouts w
    WHERE w.user_id = u.id
      AND w.name = ws.workout_name
);

INSERT INTO workouts_exercises (workout_id, exercise_id)
SELECT w.id, e.id
FROM (
    SELECT 'demo_client' AS username, 'Client Premium Push' AS workout_name, 'Trap Bar Deadlift' AS exercise_name
    UNION ALL SELECT 'demo_client', 'Client Premium Push', 'Rower Sprint'
    UNION ALL SELECT 'demo_trainer', 'Trainer Performance Lift', 'Sled Push'
    UNION ALL SELECT 'demo_trainer', 'Trainer Performance Lift', 'Trap Bar Deadlift'
    UNION ALL SELECT 'demo_gym', 'Gym Floor Conditioning', 'Bike Recovery Ride'
    UNION ALL SELECT 'demo_admin', 'Admin Mobility Reset', 'Walk'
) wes
JOIN users u ON u.username = wes.username
JOIN workouts w ON w.user_id = u.id AND w.name = wes.workout_name
JOIN exercises e ON e.name = wes.exercise_name
WHERE NOT EXISTS (
    SELECT 1 FROM workouts_exercises we
    WHERE we.workout_id = w.id
      AND we.exercise_id = e.id
);

INSERT INTO workout_schedule (user_id, day_of_week, workout_id, order_index)
SELECT u.id, wss.day_of_week, w.id, 0
FROM (
    SELECT 'demo_client' AS username, 2 AS day_of_week, 'Client Premium Push' AS workout_name
    UNION ALL SELECT 'demo_trainer', 3, 'Trainer Performance Lift'
    UNION ALL SELECT 'demo_gym', 4, 'Gym Floor Conditioning'
    UNION ALL SELECT 'demo_admin', 5, 'Admin Mobility Reset'
) wss
JOIN users u ON u.username = wss.username
JOIN workouts w ON w.user_id = u.id AND w.name = wss.workout_name
WHERE NOT EXISTS (
    SELECT 1 FROM workout_schedule ws
    WHERE ws.user_id = u.id
      AND ws.day_of_week = wss.day_of_week
      AND ws.workout_id = w.id
);

INSERT INTO schedules (user_id, name, description)
SELECT u.id, ss.schedule_name, ss.description
FROM (
    SELECT 'demo_client' AS username, 'Client Premium Plan' AS schedule_name, 'Twelve-week premium client structure with live forward planning.' AS description
    UNION ALL SELECT 'demo_trainer', 'Trainer Coaching Rhythm', 'Weekly coaching rhythm for check-ins, programming, and content.'
    UNION ALL SELECT 'demo_gym', 'Gym Operations Circuit', 'Weekly operations and facility planning cadence.'
    UNION ALL SELECT 'demo_admin', 'Admin Oversight Cycle', 'Development oversight cycle for support, moderation, and QA.'
) ss
JOIN users u ON u.username = ss.username
WHERE NOT EXISTS (
    SELECT 1 FROM schedules s
    WHERE s.user_id = u.id
      AND s.name = ss.schedule_name
);

INSERT INTO schedule_entries (schedule_id, exercise_id, day_of_week, order_number)
SELECT s.id, e.id, ses.day_of_week, 1
FROM (
    SELECT 'demo_client' AS username, 'Client Premium Plan' AS schedule_name, 'Trap Bar Deadlift' AS exercise_name, 2 AS day_of_week
    UNION ALL SELECT 'demo_trainer', 'Trainer Coaching Rhythm', 'Sled Push', 3
    UNION ALL SELECT 'demo_gym', 'Gym Operations Circuit', 'Bike Recovery Ride', 4
    UNION ALL SELECT 'demo_admin', 'Admin Oversight Cycle', 'Walk', 5
) ses
JOIN users u ON u.username = ses.username
JOIN schedules s ON s.user_id = u.id AND s.name = ses.schedule_name
JOIN exercises e ON e.name = ses.exercise_name
WHERE NOT EXISTS (
    SELECT 1 FROM schedule_entries se
    WHERE se.schedule_id = s.id
      AND se.day_of_week = ses.day_of_week
      AND se.order_number = 1
);

-- =========================================================================
-- PAST MONTH + NEXT 3 MONTHS OF ACTIVITY
-- =========================================================================

INSERT INTO calendar_tasks (user_id, date, title, time, notes, is_exercise, completed, exercise_name, requires_log)
SELECT u.id, ad.activity_date, ts.title, ts.task_time, ts.notes, FALSE, ad.completed_flag, NULL, FALSE
FROM (
    SELECT 'demo_client' AS username, 'Client premium training block' AS title, TIME '07:30:00' AS task_time, 'Strength session, nutrition prep, and recovery review for the premium client demo journey.' AS notes
    UNION ALL SELECT 'demo_trainer', 'Trainer coaching review', TIME '09:00:00', 'Review client progress, refine programme notes, and prep the next coaching touchpoint.'
    UNION ALL SELECT 'demo_gym', 'Gym operations reset', TIME '10:30:00', 'Walk the floor, review trainer capacity, and prep the coming week of member operations.'
    UNION ALL SELECT 'demo_admin', 'Platform oversight check', TIME '08:45:00', 'Review support queues, moderation states, and dev availability settings.'
) ts
JOIN users u ON u.username = ts.username
CROSS JOIN (
    SELECT CURRENT_DATE - INTERVAL '28' DAY AS activity_date, TRUE AS completed_flag
    UNION ALL SELECT CURRENT_DATE - INTERVAL '21' DAY, TRUE
    UNION ALL SELECT CURRENT_DATE - INTERVAL '14' DAY, TRUE
    UNION ALL SELECT CURRENT_DATE - INTERVAL '7' DAY, TRUE
    UNION ALL SELECT CURRENT_DATE, FALSE
    UNION ALL SELECT CURRENT_DATE + INTERVAL '7' DAY, FALSE
    UNION ALL SELECT CURRENT_DATE + INTERVAL '14' DAY, FALSE
    UNION ALL SELECT CURRENT_DATE + INTERVAL '21' DAY, FALSE
    UNION ALL SELECT CURRENT_DATE + INTERVAL '28' DAY, FALSE
    UNION ALL SELECT CURRENT_DATE + INTERVAL '35' DAY, FALSE
    UNION ALL SELECT CURRENT_DATE + INTERVAL '42' DAY, FALSE
    UNION ALL SELECT CURRENT_DATE + INTERVAL '49' DAY, FALSE
    UNION ALL SELECT CURRENT_DATE + INTERVAL '56' DAY, FALSE
    UNION ALL SELECT CURRENT_DATE + INTERVAL '63' DAY, FALSE
    UNION ALL SELECT CURRENT_DATE + INTERVAL '70' DAY, FALSE
    UNION ALL SELECT CURRENT_DATE + INTERVAL '77' DAY, FALSE
    UNION ALL SELECT CURRENT_DATE + INTERVAL '84' DAY, FALSE
) ad
WHERE NOT EXISTS (
    SELECT 1 FROM calendar_tasks ct
    WHERE ct.user_id = u.id
      AND ct.date = ad.activity_date
      AND ct.title = ts.title
);

INSERT INTO schedule_occurrences (user_id, exercise_id, schedule_id, date, schedule_name, completed)
SELECT u.id, e.id, s.id, ad.activity_date, os.schedule_name, ad.completed_flag
FROM (
    SELECT 'demo_client' AS username, 'Client Premium Plan' AS schedule_name, 'Trap Bar Deadlift' AS exercise_name
    UNION ALL SELECT 'demo_trainer', 'Trainer Coaching Rhythm', 'Sled Push'
    UNION ALL SELECT 'demo_gym', 'Gym Operations Circuit', 'Bike Recovery Ride'
    UNION ALL SELECT 'demo_admin', 'Admin Oversight Cycle', 'Walk'
) os
JOIN users u ON u.username = os.username
JOIN schedules s ON s.user_id = u.id AND s.name = os.schedule_name
JOIN exercises e ON e.name = os.exercise_name
CROSS JOIN (
    SELECT CURRENT_DATE - INTERVAL '28' DAY AS activity_date, TRUE AS completed_flag
    UNION ALL SELECT CURRENT_DATE - INTERVAL '21' DAY, TRUE
    UNION ALL SELECT CURRENT_DATE - INTERVAL '14' DAY, TRUE
    UNION ALL SELECT CURRENT_DATE - INTERVAL '7' DAY, TRUE
    UNION ALL SELECT CURRENT_DATE, FALSE
    UNION ALL SELECT CURRENT_DATE + INTERVAL '7' DAY, FALSE
    UNION ALL SELECT CURRENT_DATE + INTERVAL '14' DAY, FALSE
    UNION ALL SELECT CURRENT_DATE + INTERVAL '21' DAY, FALSE
    UNION ALL SELECT CURRENT_DATE + INTERVAL '28' DAY, FALSE
    UNION ALL SELECT CURRENT_DATE + INTERVAL '35' DAY, FALSE
    UNION ALL SELECT CURRENT_DATE + INTERVAL '42' DAY, FALSE
    UNION ALL SELECT CURRENT_DATE + INTERVAL '49' DAY, FALSE
    UNION ALL SELECT CURRENT_DATE + INTERVAL '56' DAY, FALSE
    UNION ALL SELECT CURRENT_DATE + INTERVAL '63' DAY, FALSE
    UNION ALL SELECT CURRENT_DATE + INTERVAL '70' DAY, FALSE
    UNION ALL SELECT CURRENT_DATE + INTERVAL '77' DAY, FALSE
    UNION ALL SELECT CURRENT_DATE + INTERVAL '84' DAY, FALSE
) ad
WHERE NOT EXISTS (
    SELECT 1 FROM schedule_occurrences so
    WHERE so.user_id = u.id
      AND so.date = ad.activity_date
      AND so.schedule_name = os.schedule_name
);

INSERT INTO workout_sessions (user_id, date, workout_id, name_snapshot, completed)
SELECT u.id, ps.session_date, w.id, w.name, TRUE
FROM (
    SELECT 'demo_client' AS username, 'Client Premium Push' AS workout_name
    UNION ALL SELECT 'demo_trainer', 'Trainer Performance Lift'
    UNION ALL SELECT 'demo_gym', 'Gym Floor Conditioning'
    UNION ALL SELECT 'demo_admin', 'Admin Mobility Reset'
) ss
JOIN users u ON u.username = ss.username
JOIN workouts w ON w.user_id = u.id AND w.name = ss.workout_name
CROSS JOIN (
    SELECT CURRENT_DATE - INTERVAL '28' DAY AS session_date
    UNION ALL SELECT CURRENT_DATE - INTERVAL '21' DAY
    UNION ALL SELECT CURRENT_DATE - INTERVAL '14' DAY
    UNION ALL SELECT CURRENT_DATE - INTERVAL '7' DAY
) ps
WHERE NOT EXISTS (
    SELECT 1 FROM workout_sessions ws
    WHERE ws.user_id = u.id
      AND ws.date = ps.session_date
      AND ws.workout_id = w.id
);

-- =========================================================================
-- GOALS
-- =========================================================================

INSERT INTO goals (owner_user_id, created_by_user_id, title, description, goal_type, target_metric_name,
    target_metric_value, target_metric_unit, start_date, target_date, status, priority, archived, created_at, updated_at)
SELECT u.id, u.id, gs.title, gs.description, 'HABIT', gs.target_metric_name,
       gs.target_metric_value, gs.target_metric_unit,
       CURRENT_DATE - INTERVAL '28' DAY, CURRENT_DATE + INTERVAL '84' DAY,
       'ACTIVE', 1, FALSE, CURRENT_TIMESTAMP - INTERVAL '28' DAY, CURRENT_TIMESTAMP - INTERVAL '1' DAY
FROM (
    SELECT 'demo_client' AS username, 'Hold premium consistency for 12 weeks' AS title, 'Keep training, planning, and recovery tasks visible across the full premium demo horizon.' AS description, 'Weekly consistency' AS target_metric_name, 4 AS target_metric_value, 'sessions/week' AS target_metric_unit
    UNION ALL SELECT 'demo_trainer', 'Maintain weekly coaching cadence', 'Keep programming, reviews, and client follow-up flowing each week.', 'Coaching blocks', 3, 'touchpoints/week'
    UNION ALL SELECT 'demo_gym', 'Keep member operations smooth', 'Keep the gym admin demo account looking active across staffing and floor planning.', 'Ops reviews', 2, 'reviews/week'
    UNION ALL SELECT 'demo_admin', 'Track platform dev readiness', 'Use the admin demo account to keep availability, moderation, and support states active.', 'Oversight passes', 2, 'passes/week'
) gs
JOIN users u ON u.username = gs.username
WHERE NOT EXISTS (
    SELECT 1 FROM goals g
    WHERE g.owner_user_id = u.id
      AND g.title = gs.title
);

-- ========================================================================
-- BEGIN 07-demo-role-readiness.sql
-- ========================================================================

-- Demo role readiness completion dataset
-- Ensures there are at least two current, usable accounts for each runtime role:
-- CLIENT, TRAINER, GYM_ADMIN, PLATFORM_ADMIN, SUPER_ADMIN.
-- Focuses on the legacy role accounts plus the new super-admin accounts.

-- =========================================================================
-- ACCOUNT COMPLETION
-- =========================================================================

UPDATE users
SET subscription_status = TRUE,
    email_verified = TRUE,
    email_verified_at = CURRENT_TIMESTAMP,
    phone_verified = TRUE,
    phone_verified_at = CURRENT_TIMESTAMP,
    trainer_verified = CASE WHEN username = 'trainer_demo' THEN TRUE ELSE trainer_verified END,
    phone_number = CASE
        WHEN username = 'trainer_demo' THEN '447700900101'
        WHEN username = 'gymadmin_demo' THEN '447700900102'
        WHEN username = 'admin_demo' THEN '447700900103'
        WHEN username = 'superadmin_demo' THEN '447700900104'
        WHEN username = 'superadmin_ops' THEN '447700900105'
        ELSE phone_number
    END,
    date_of_birth = CASE
        WHEN username = 'trainer_demo' THEN DATE '1991-03-14'
        WHEN username = 'gymadmin_demo' THEN DATE '1989-08-27'
        WHEN username = 'admin_demo' THEN DATE '1987-06-11'
        WHEN username = 'superadmin_demo' THEN DATE '1985-01-23'
        WHEN username = 'superadmin_ops' THEN DATE '1986-10-05'
        ELSE date_of_birth
    END,
    bio = CASE
        WHEN username = 'trainer_demo' THEN 'Verified strength coach demo account with active client oversight, current planning blocks, and a polished coaching profile.'
        WHEN username = 'gymadmin_demo' THEN 'Gym account demo with live operations data, trainer oversight tasks, and a current planning timeline.'
        WHEN username = 'admin_demo' THEN 'Platform admin demo account for moderation, support triage, and rollout checks across the current product state.'
        WHEN username = 'superadmin_demo' THEN 'Super-admin demo account for governance, approvals, and platform-wide readiness oversight.'
        WHEN username = 'superadmin_ops' THEN 'Operations-focused super-admin demo account for incident response, deployment checks, and escalation control.'
        ELSE bio
    END,
    profile_image_url = CASE
        WHEN username = 'trainer_demo' THEN '/img/chat/charlie-avatar.svg'
        WHEN username = 'gymadmin_demo' THEN '/img/brand/tab_logo.png'
        WHEN username = 'admin_demo' THEN '/img/logo.png'
        WHEN username = 'superadmin_demo' THEN '/img/brand/tab_logo.png'
        WHEN username = 'superadmin_ops' THEN '/img/logo.png'
        ELSE profile_image_url
    END
WHERE username IN ('trainer_demo', 'gymadmin_demo', 'admin_demo', 'superadmin_demo', 'superadmin_ops');

INSERT INTO platform_subscriptions (user_id, plan, status, current_period_end, cancel_at_period_end)
SELECT u.id, 'MONTHLY', 'ACTIVE', CURRENT_TIMESTAMP + INTERVAL '45' DAY, FALSE
FROM users u
WHERE u.username IN ('trainer_demo', 'gymadmin_demo', 'admin_demo', 'superadmin_demo', 'superadmin_ops')
  AND NOT EXISTS (
    SELECT 1 FROM platform_subscriptions ps WHERE ps.user_id = u.id
  );

UPDATE platform_subscriptions
SET plan = 'MONTHLY',
    status = 'ACTIVE',
    current_period_end = CURRENT_TIMESTAMP + INTERVAL '45' DAY,
    cancel_at_period_end = FALSE
WHERE user_id IN (
    SELECT id
    FROM users
    WHERE username IN ('trainer_demo', 'gymadmin_demo', 'admin_demo', 'superadmin_demo', 'superadmin_ops')
);

-- =========================================================================
-- SETTINGS + PROFILE CUSTOMISER
-- =========================================================================

INSERT INTO user_settings (user_id, language, theme, easy_mode, calendar_view_preference, default_sets,
    default_rep_min, default_rep_max, quiet_hours_start, quiet_hours_end, monthly_workout_target,
    weekly_summary_metrics, profile_banner_theme, profile_ring_style, profile_card_back_style,
    profile_text_color, profile_bio_text_color, profile_milestone_keys, dashboard_immersion_enabled,
    weather_temperature_unit, weather_display_mode, time_display_format, quick_preferences_completed)
SELECT u.id, 'en', 'LIGHT', FALSE, 'MONTH', 3,
    8, 12, TIME '22:30:00', TIME '06:30:00', 8,
    'TASKS_COMPLETED,WORKOUTS_COMPLETED,WORKOUT_STREAK', 'OCEAN', 'COMET_TRAIL', 'CARBON',
    '#F8FAFC', '#CBD5E1', 'VERIFIED_EMAIL,VERIFIED_PHONE,PREMIUM_MEMBER,LEVEL_10', TRUE,
    'CELSIUS', 'GRAPH', 'TWELVE_HOUR', TRUE
FROM users u
WHERE u.username = 'superadmin_demo'
  AND NOT EXISTS (SELECT 1 FROM user_settings us WHERE us.user_id = u.id);

INSERT INTO user_settings (user_id, language, theme, easy_mode, calendar_view_preference, default_sets,
    default_rep_min, default_rep_max, quiet_hours_start, quiet_hours_end, monthly_workout_target,
    weekly_summary_metrics, profile_banner_theme, profile_ring_style, profile_card_back_style,
    profile_text_color, profile_bio_text_color, profile_milestone_keys, dashboard_immersion_enabled,
    weather_temperature_unit, weather_display_mode, time_display_format, quick_preferences_completed)
SELECT u.id, 'en', 'LIGHT', FALSE, 'MONTH', 3,
    8, 12, TIME '22:00:00', TIME '06:00:00', 6,
    'TASKS_COMPLETED,WORKOUTS_COMPLETED,MEALS_LOGGED', 'SUNSET', 'KING_CROWN', 'MATRIX',
    '#FEF3C7', '#FDE68A', 'VERIFIED_EMAIL,VERIFIED_PHONE,PREMIUM_MEMBER,LEVEL_10', TRUE,
    'CELSIUS', 'VISUAL', 'TWELVE_HOUR', TRUE
FROM users u
WHERE u.username = 'superadmin_ops'
  AND NOT EXISTS (SELECT 1 FROM user_settings us WHERE us.user_id = u.id);

UPDATE user_settings
SET calendar_view_preference = CASE
        WHEN user_id = (SELECT id FROM users WHERE username = 'trainer_demo') THEN 'WEEK'
        ELSE 'MONTH'
    END,
    monthly_workout_target = CASE
        WHEN user_id = (SELECT id FROM users WHERE username = 'trainer_demo') THEN 14
        WHEN user_id = (SELECT id FROM users WHERE username = 'gymadmin_demo') THEN 8
        WHEN user_id = (SELECT id FROM users WHERE username = 'admin_demo') THEN 6
        WHEN user_id = (SELECT id FROM users WHERE username = 'superadmin_demo') THEN 8
        WHEN user_id = (SELECT id FROM users WHERE username = 'superadmin_ops') THEN 6
        ELSE monthly_workout_target
    END,
    weekly_summary_metrics = CASE
        WHEN user_id = (SELECT id FROM users WHERE username = 'trainer_demo') THEN 'WORKOUTS_COMPLETED,TASKS_COMPLETED,WORKOUT_STREAK'
        WHEN user_id = (SELECT id FROM users WHERE username = 'gymadmin_demo') THEN 'TASKS_COMPLETED,WORKOUTS_COMPLETED,MEALS_LOGGED'
        WHEN user_id = (SELECT id FROM users WHERE username = 'admin_demo') THEN 'TASKS_COMPLETED,WORKOUTS_COMPLETED,WORKOUT_STREAK'
        WHEN user_id = (SELECT id FROM users WHERE username = 'superadmin_demo') THEN 'TASKS_COMPLETED,WORKOUTS_COMPLETED,WORKOUT_STREAK'
        WHEN user_id = (SELECT id FROM users WHERE username = 'superadmin_ops') THEN 'TASKS_COMPLETED,WORKOUTS_COMPLETED,MEALS_LOGGED'
        ELSE weekly_summary_metrics
    END,
    profile_banner_theme = CASE
        WHEN user_id = (SELECT id FROM users WHERE username = 'trainer_demo') THEN 'MIDNIGHT'
        WHEN user_id = (SELECT id FROM users WHERE username = 'gymadmin_demo') THEN 'SUNSET'
        WHEN user_id = (SELECT id FROM users WHERE username = 'admin_demo') THEN 'OCEAN'
        WHEN user_id = (SELECT id FROM users WHERE username = 'superadmin_demo') THEN 'OCEAN'
        WHEN user_id = (SELECT id FROM users WHERE username = 'superadmin_ops') THEN 'SUNSET'
        ELSE profile_banner_theme
    END,
    profile_ring_style = CASE
        WHEN user_id = (SELECT id FROM users WHERE username = 'trainer_demo') THEN 'COMET_TRAIL'
        WHEN user_id = (SELECT id FROM users WHERE username = 'gymadmin_demo') THEN 'KING_CROWN'
        WHEN user_id = (SELECT id FROM users WHERE username = 'admin_demo') THEN 'CYBER_ARMS'
        WHEN user_id = (SELECT id FROM users WHERE username = 'superadmin_demo') THEN 'AURORA_PULSE'
        WHEN user_id = (SELECT id FROM users WHERE username = 'superadmin_ops') THEN 'KING_CROWN'
        ELSE profile_ring_style
    END,
    profile_card_back_style = CASE
        WHEN user_id = (SELECT id FROM users WHERE username = 'trainer_demo') THEN 'CARBON'
        WHEN user_id = (SELECT id FROM users WHERE username = 'gymadmin_demo') THEN 'CIRCUIT'
        WHEN user_id = (SELECT id FROM users WHERE username = 'admin_demo') THEN 'MATRIX'
        WHEN user_id = (SELECT id FROM users WHERE username = 'superadmin_demo') THEN 'NEBULA'
        WHEN user_id = (SELECT id FROM users WHERE username = 'superadmin_ops') THEN 'MATRIX'
        ELSE profile_card_back_style
    END,
    profile_text_color = '#F8FAFC',
    profile_bio_text_color = CASE
        WHEN user_id = (SELECT id FROM users WHERE username = 'gymadmin_demo') THEN '#FDE68A'
        WHEN user_id = (SELECT id FROM users WHERE username = 'admin_demo') THEN '#BFDBFE'
        WHEN user_id = (SELECT id FROM users WHERE username = 'superadmin_ops') THEN '#FDE68A'
        ELSE '#CBD5E1'
    END,
    profile_milestone_keys = 'VERIFIED_EMAIL,VERIFIED_PHONE,PREMIUM_MEMBER,LEVEL_10',
    dashboard_immersion_enabled = TRUE,
    weather_temperature_unit = 'CELSIUS',
    weather_display_mode = 'GRAPH',
    time_display_format = 'TWELVE_HOUR',
    quick_preferences_completed = TRUE
WHERE user_id IN (
    SELECT id
    FROM users
    WHERE username IN ('trainer_demo', 'gymadmin_demo', 'admin_demo', 'superadmin_demo', 'superadmin_ops')
);

-- =========================================================================
-- USER POINTS
-- =========================================================================

INSERT INTO user_points (user_id, points, level, last_updated)
SELECT u.id, p.points, p.level, CURRENT_DATE
FROM (
    SELECT 'trainer_demo' AS username, 2140 AS points, 12 AS level
    UNION ALL SELECT 'gymadmin_demo', 1380, 8
    UNION ALL SELECT 'admin_demo', 2410, 14
    UNION ALL SELECT 'superadmin_demo', 3025, 18
    UNION ALL SELECT 'superadmin_ops', 2890, 17
) p
JOIN users u ON u.username = p.username
WHERE NOT EXISTS (SELECT 1 FROM user_points up WHERE up.user_id = u.id);

UPDATE user_points
SET points = CASE
        WHEN user_id = (SELECT id FROM users WHERE username = 'trainer_demo') THEN 2140
        WHEN user_id = (SELECT id FROM users WHERE username = 'gymadmin_demo') THEN 1380
        WHEN user_id = (SELECT id FROM users WHERE username = 'admin_demo') THEN 2410
        WHEN user_id = (SELECT id FROM users WHERE username = 'superadmin_demo') THEN 3025
        WHEN user_id = (SELECT id FROM users WHERE username = 'superadmin_ops') THEN 2890
        ELSE points
    END,
    level = CASE
        WHEN user_id = (SELECT id FROM users WHERE username = 'trainer_demo') THEN 12
        WHEN user_id = (SELECT id FROM users WHERE username = 'gymadmin_demo') THEN 8
        WHEN user_id = (SELECT id FROM users WHERE username = 'admin_demo') THEN 14
        WHEN user_id = (SELECT id FROM users WHERE username = 'superadmin_demo') THEN 18
        WHEN user_id = (SELECT id FROM users WHERE username = 'superadmin_ops') THEN 17
        ELSE level
    END,
    last_updated = CURRENT_DATE
WHERE user_id IN (
    SELECT id
    FROM users
    WHERE username IN ('trainer_demo', 'gymadmin_demo', 'admin_demo', 'superadmin_demo', 'superadmin_ops')
);

-- =========================================================================
-- ROLE PROFILES
-- =========================================================================

INSERT INTO trainer_profiles (user_id, bio, trainer_code, specializations, location, primary_gym, price_per_session,
    instagram_url, linkedin_url, show_instagram, show_tiktok, show_youtube, show_linkedin, show_website, created_at, updated_at)
SELECT (SELECT id FROM users WHERE username = 'trainer_demo'),
    'Premium coaching demo profile focused on technique review, progressive loading, and practical accountability.',
    '120340056789',
    'Strength & Conditioning, Fat Loss, Lifestyle Coaching',
    'Cardiff, UK',
    'FitZone Gym Cardiff',
    52,
    'https://instagram.com/trainer_demo_fit',
    'https://linkedin.com/in/trainer-demo',
    TRUE, FALSE, FALSE, TRUE, FALSE,
    CURRENT_TIMESTAMP - INTERVAL '120' DAY,
    CURRENT_TIMESTAMP - INTERVAL '2' DAY
WHERE EXISTS (SELECT 1 FROM users WHERE username = 'trainer_demo')
  AND NOT EXISTS (SELECT 1 FROM trainer_profiles WHERE user_id = (SELECT id FROM users WHERE username = 'trainer_demo'));

UPDATE trainer_profiles
SET trainer_code = '120340056789',
    bio = 'Premium coaching demo profile focused on technique review, progressive loading, and practical accountability.',
    specializations = 'Strength & Conditioning, Fat Loss, Lifestyle Coaching',
    location = 'Cardiff, UK',
    primary_gym = 'FitZone Gym Cardiff',
    price_per_session = 52,
    instagram_url = 'https://instagram.com/trainer_demo_fit',
    linkedin_url = 'https://linkedin.com/in/trainer-demo',
    show_instagram = TRUE,
    show_linkedin = TRUE,
    updated_at = CURRENT_TIMESTAMP - INTERVAL '2' DAY
WHERE user_id = (SELECT id FROM users WHERE username = 'trainer_demo');

INSERT INTO gym_profiles (user_id, gym_name, gym_code, address, city, contact_name, contact_phone, created_at, updated_at)
SELECT (SELECT id FROM users WHERE username = 'gymadmin_demo'),
    'FitZone Gym Cardiff',
    '4827001938456202',
    '42 Capital Way',
    'Cardiff',
    'Gym Admin',
    '+44 29 2000 1234',
    CURRENT_TIMESTAMP - INTERVAL '210' DAY,
    CURRENT_TIMESTAMP - INTERVAL '3' DAY
WHERE EXISTS (SELECT 1 FROM users WHERE username = 'gymadmin_demo')
  AND NOT EXISTS (SELECT 1 FROM gym_profiles WHERE user_id = (SELECT id FROM users WHERE username = 'gymadmin_demo'));

UPDATE gym_profiles
SET gym_name = 'FitZone Gym Cardiff',
    gym_code = '4827001938456202',
    address = '42 Capital Way',
    city = 'Cardiff',
    contact_name = 'Gym Admin',
    contact_phone = '+44 29 2000 1234',
    updated_at = CURRENT_TIMESTAMP - INTERVAL '3' DAY
WHERE user_id = (SELECT id FROM users WHERE username = 'gymadmin_demo');

-- =========================================================================
-- WORKOUTS / SCHEDULES / TIMELINE
-- =========================================================================

INSERT INTO workouts (user_id, name, notes)
SELECT u.id, w.workout_name, w.notes
FROM (
    SELECT 'trainer_demo' AS username, 'Trainer Demo Coaching Session' AS workout_name, 'Technique review and strength coaching demo session.' AS notes
    UNION ALL SELECT 'gymadmin_demo', 'Gym Admin Floor Circuit', 'Low-friction floor walk-through and staff readiness block.'
    UNION ALL SELECT 'admin_demo', 'Platform Admin Reset', 'Short movement reset between moderation and support passes.'
    UNION ALL SELECT 'superadmin_demo', 'Super Admin Readiness Walkthrough', 'Executive oversight reset with short training exposure.'
    UNION ALL SELECT 'superadmin_ops', 'Super Admin Ops Reset', 'Short conditioning block to keep the operations demo account active.'
 ) w
JOIN users u ON u.username = w.username
WHERE NOT EXISTS (
    SELECT 1
    FROM workouts x
    WHERE x.user_id = u.id
      AND x.name = w.workout_name
);

INSERT INTO workouts_exercises (workout_id, exercise_id)
SELECT w.id, e.id
FROM (
    SELECT 'trainer_demo' AS username, 'Trainer Demo Coaching Session' AS workout_name, 'Trap Bar Deadlift' AS exercise_name
    UNION ALL SELECT 'trainer_demo', 'Trainer Demo Coaching Session', 'Rower Sprint'
    UNION ALL SELECT 'gymadmin_demo', 'Gym Admin Floor Circuit', 'Bike Recovery Ride'
    UNION ALL SELECT 'admin_demo', 'Platform Admin Reset', 'Walk'
    UNION ALL SELECT 'superadmin_demo', 'Super Admin Readiness Walkthrough', 'Run'
    UNION ALL SELECT 'superadmin_ops', 'Super Admin Ops Reset', 'Incline Push-up'
) x
JOIN users u ON u.username = x.username
JOIN workouts w ON w.user_id = u.id AND w.name = x.workout_name
JOIN exercises e ON e.name = x.exercise_name
WHERE NOT EXISTS (
    SELECT 1
    FROM workouts_exercises we
    WHERE we.workout_id = w.id
      AND we.exercise_id = e.id
);

INSERT INTO workout_schedule (user_id, day_of_week, workout_id, order_index)
SELECT u.id, x.day_of_week, w.id, 0
FROM (
    SELECT 'trainer_demo' AS username, 2 AS day_of_week, 'Trainer Demo Coaching Session' AS workout_name
    UNION ALL SELECT 'gymadmin_demo', 3, 'Gym Admin Floor Circuit'
    UNION ALL SELECT 'admin_demo', 4, 'Platform Admin Reset'
    UNION ALL SELECT 'superadmin_demo', 5, 'Super Admin Readiness Walkthrough'
    UNION ALL SELECT 'superadmin_ops', 1, 'Super Admin Ops Reset'
) x
JOIN users u ON u.username = x.username
JOIN workouts w ON w.user_id = u.id AND w.name = x.workout_name
WHERE NOT EXISTS (
    SELECT 1
    FROM workout_schedule ws
    WHERE ws.user_id = u.id
      AND ws.day_of_week = x.day_of_week
      AND ws.workout_id = w.id
);

INSERT INTO schedules (user_id, name, description)
SELECT u.id, s.schedule_name, s.description
FROM (
    SELECT 'trainer_demo' AS username, 'Trainer Demo Coaching Calendar' AS schedule_name, 'Current coaching cadence for check-ins, programming, and client reviews.' AS description
    UNION ALL SELECT 'gymadmin_demo', 'Gym Admin Operations Calendar', 'Current operations cadence for trainer capacity, floor checks, and member support.'
    UNION ALL SELECT 'admin_demo', 'Platform Admin Oversight Calendar', 'Current moderation and support cadence for platform administration.'
    UNION ALL SELECT 'superadmin_demo', 'Super Admin Governance Calendar', 'Current governance cadence for approvals, audit checks, and roadmap readiness.'
    UNION ALL SELECT 'superadmin_ops', 'Super Admin Incident Calendar', 'Current incident and escalation cadence for platform operations.'
) s
JOIN users u ON u.username = s.username
WHERE NOT EXISTS (
    SELECT 1
    FROM schedules sc
    WHERE sc.user_id = u.id
      AND sc.name = s.schedule_name
);

INSERT INTO schedule_entries (schedule_id, exercise_id, day_of_week, order_number)
SELECT s.id, e.id, x.day_of_week, 1
FROM (
    SELECT 'trainer_demo' AS username, 'Trainer Demo Coaching Calendar' AS schedule_name, 'Trap Bar Deadlift' AS exercise_name, 2 AS day_of_week
    UNION ALL SELECT 'gymadmin_demo', 'Gym Admin Operations Calendar', 'Bike Recovery Ride', 3
    UNION ALL SELECT 'admin_demo', 'Platform Admin Oversight Calendar', 'Walk', 4
    UNION ALL SELECT 'superadmin_demo', 'Super Admin Governance Calendar', 'Run', 5
    UNION ALL SELECT 'superadmin_ops', 'Super Admin Incident Calendar', 'Incline Push-up', 1
) x
JOIN users u ON u.username = x.username
JOIN schedules s ON s.user_id = u.id AND s.name = x.schedule_name
JOIN exercises e ON e.name = x.exercise_name
WHERE NOT EXISTS (
    SELECT 1
    FROM schedule_entries se
    WHERE se.schedule_id = s.id
      AND se.day_of_week = x.day_of_week
      AND se.order_number = 1
);

INSERT INTO schedule_applied (schedule_id, user_id, date_applied, shown_on_calendar, requires_logging, duration_weeks)
SELECT s.id, u.id, CAST((CURRENT_DATE - INTERVAL '35' DAY) AS DATE), TRUE, TRUE, 8
FROM users u
JOIN schedules s ON s.user_id = u.id
WHERE u.username IN ('trainer_demo', 'gymadmin_demo', 'admin_demo', 'superadmin_demo', 'superadmin_ops')
  AND s.name IN (
      'Trainer Demo Coaching Calendar',
      'Gym Admin Operations Calendar',
      'Platform Admin Oversight Calendar',
      'Super Admin Governance Calendar',
      'Super Admin Incident Calendar'
  )
  AND NOT EXISTS (
      SELECT 1
      FROM schedule_applied sa
      WHERE sa.schedule_id = s.id
        AND sa.user_id = u.id
  );

INSERT INTO schedule_occurrences (user_id, exercise_id, schedule_id, date, schedule_name, completed)
SELECT u.id, e.id, s.id, d.activity_date, s.name, d.completed_flag
FROM (
    SELECT CAST((CURRENT_DATE - INTERVAL '7' DAY) AS DATE) AS activity_date, TRUE AS completed_flag
    UNION ALL SELECT CAST(CURRENT_DATE AS DATE), FALSE
    UNION ALL SELECT CAST((CURRENT_DATE + INTERVAL '7' DAY) AS DATE), FALSE
) d
JOIN (
    SELECT 'trainer_demo' AS username, 'Trainer Demo Coaching Calendar' AS schedule_name, 'Trap Bar Deadlift' AS exercise_name
    UNION ALL SELECT 'gymadmin_demo', 'Gym Admin Operations Calendar', 'Bike Recovery Ride'
    UNION ALL SELECT 'admin_demo', 'Platform Admin Oversight Calendar', 'Walk'
    UNION ALL SELECT 'superadmin_demo', 'Super Admin Governance Calendar', 'Run'
    UNION ALL SELECT 'superadmin_ops', 'Super Admin Incident Calendar', 'Incline Push-up'
) x ON 1 = 1
JOIN users u ON u.username = x.username
JOIN schedules s ON s.user_id = u.id AND s.name = x.schedule_name
JOIN exercises e ON e.name = x.exercise_name
WHERE NOT EXISTS (
    SELECT 1
    FROM schedule_occurrences so
    WHERE so.user_id = u.id
      AND so.date = d.activity_date
      AND so.schedule_name = s.name
);

INSERT INTO calendar_tasks (user_id, date, title, time, notes, is_exercise, completed, exercise_name, requires_log)
SELECT u.id, d.activity_date, t.title, t.task_time, t.notes, FALSE, d.completed_flag, NULL, FALSE
FROM (
    SELECT CAST((CURRENT_DATE - INTERVAL '2' DAY) AS DATE) AS activity_date, TRUE AS completed_flag
    UNION ALL SELECT CAST(CURRENT_DATE AS DATE), FALSE
    UNION ALL SELECT CAST((CURRENT_DATE + INTERVAL '2' DAY) AS DATE), FALSE
) d
JOIN (
    SELECT 'trainer_demo' AS username, 'Coach programme review' AS title, TIME '08:30:00' AS task_time, 'Review client notes, refresh the next training block, and confirm current priorities.' AS notes
    UNION ALL SELECT 'gymadmin_demo', 'Trainer capacity review', TIME '09:15:00', 'Check trainer coverage, floor usage, and member support tasks.'
    UNION ALL SELECT 'admin_demo', 'Moderation and support sweep', TIME '08:45:00', 'Review flagged content, outstanding support items, and current escalation states.'
    UNION ALL SELECT 'superadmin_demo', 'Platform readiness review', TIME '08:00:00', 'Review governance actions, live priorities, and release readiness.'
    UNION ALL SELECT 'superadmin_ops', 'Incident and risk pass', TIME '07:45:00', 'Check operational alerts, blockers, and escalation ownership.'
 ) t ON 1 = 1
JOIN users u ON u.username = t.username
WHERE NOT EXISTS (
    SELECT 1
    FROM calendar_tasks ct
    WHERE ct.user_id = u.id
      AND ct.date = d.activity_date
      AND ct.title = t.title
);

INSERT INTO workout_sessions (user_id, date, workout_id, name_snapshot, completed)
SELECT u.id, CAST((CURRENT_DATE - INTERVAL '7' DAY) AS DATE), w.id, w.name, TRUE
FROM (
    SELECT 'trainer_demo' AS username, 'Trainer Demo Coaching Session' AS workout_name
    UNION ALL SELECT 'gymadmin_demo', 'Gym Admin Floor Circuit'
    UNION ALL SELECT 'admin_demo', 'Platform Admin Reset'
    UNION ALL SELECT 'superadmin_demo', 'Super Admin Readiness Walkthrough'
    UNION ALL SELECT 'superadmin_ops', 'Super Admin Ops Reset'
) x
JOIN users u ON u.username = x.username
JOIN workouts w ON w.user_id = u.id AND w.name = x.workout_name
WHERE NOT EXISTS (
    SELECT 1
    FROM workout_sessions ws
    WHERE ws.user_id = u.id
      AND ws.date = CAST((CURRENT_DATE - INTERVAL '7' DAY) AS DATE)
      AND ws.workout_id = w.id
);

INSERT INTO exercise_log (user_id, date, mood_before, mood_after, confidence, comments, duration_minutes)
SELECT u.id, CAST((CURRENT_DATE - INTERVAL '7' DAY) AS DATE), 3, 4, 4, l.comments, l.duration_minutes
FROM (
    SELECT 'trainer_demo' AS username, 'Coaching session felt sharp and current client notes were easy to act on.' AS comments, 48 AS duration_minutes
    UNION ALL SELECT 'gymadmin_demo', 'Operations walkthrough was clear and trainer coverage looked balanced.', 32
    UNION ALL SELECT 'admin_demo', 'Support and moderation pass was controlled and easy to prioritise.', 24
    UNION ALL SELECT 'superadmin_demo', 'Governance review was clean and current product risk was well scoped.', 26
    UNION ALL SELECT 'superadmin_ops', 'Ops review surfaced the right blockers early and stayed manageable.', 28
) l
JOIN users u ON u.username = l.username
WHERE NOT EXISTS (
    SELECT 1
    FROM exercise_log el
    WHERE el.user_id = u.id
      AND el.date = CAST((CURRENT_DATE - INTERVAL '7' DAY) AS DATE)
      AND el.comments = l.comments
);

-- =========================================================================
-- GOALS
-- =========================================================================

INSERT INTO goals (owner_user_id, created_by_user_id, title, description, goal_type, target_metric_name,
    target_metric_value, target_metric_unit, start_date, target_date, status, priority, archived, created_at, updated_at)
SELECT u.id, u.id, g.title, g.description, 'HABIT', g.metric_name,
    g.metric_value, g.metric_unit,
    CAST((CURRENT_DATE - INTERVAL '21' DAY) AS DATE),
    CAST((CURRENT_DATE + INTERVAL '70' DAY) AS DATE),
    'ACTIVE', 1, FALSE, CURRENT_TIMESTAMP - INTERVAL '21' DAY, CURRENT_TIMESTAMP - INTERVAL '1' DAY
FROM (
    SELECT 'trainer_demo' AS username, 'Keep coaching cadence visible weekly' AS title, 'Maintain a premium coaching rhythm with active reviews, planning, and client communication.' AS description, 'Coaching reviews' AS metric_name, 3 AS metric_value, 'reviews/week' AS metric_unit
    UNION ALL SELECT 'gymadmin_demo', 'Keep trainer operations stable', 'Maintain a clear weekly rhythm for trainer coverage, floor checks, and member support.', 'Ops reviews', 2, 'reviews/week'
    UNION ALL SELECT 'admin_demo', 'Keep moderation queue current', 'Run current moderation and support checks on a predictable weekly cadence.', 'Oversight passes', 2, 'passes/week'
    UNION ALL SELECT 'superadmin_demo', 'Audit platform readiness weekly', 'Keep governance, approvals, and roadmap readiness visible every week.', 'Readiness audits', 1, 'audit/week'
    UNION ALL SELECT 'superadmin_ops', 'Protect escalation coverage', 'Keep incidents, blockers, and operational escalations visible and controlled.', 'Escalation reviews', 2, 'reviews/week'
) g
JOIN users u ON u.username = g.username
WHERE NOT EXISTS (
    SELECT 1
    FROM goals x
    WHERE x.owner_user_id = u.id
      AND x.title = g.title
);

