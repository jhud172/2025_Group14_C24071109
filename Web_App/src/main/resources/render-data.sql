-- Render-safe demo seed (idempotent)
-- Shared demo password for all seeded accounts: Demo123!
-- Seeded usernames:
--   demo_client
--   demo_trainer
--   demo_gym
--   demo_admin

-- =========================================================================
-- ROLES
-- =========================================================================

INSERT INTO roles (name)
SELECT 'USER'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'USER');

INSERT INTO roles (name)
SELECT 'CLIENT'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'CLIENT');

INSERT INTO roles (name)
SELECT 'TRAINER'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'TRAINER');

INSERT INTO roles (name)
SELECT 'GYM_ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'GYM_ADMIN');

INSERT INTO roles (name)
SELECT 'PLATFORM_ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'PLATFORM_ADMIN');

-- =========================================================================
-- ACCOUNTS
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
SET email_verified = TRUE,
    email_verified_at = CURRENT_TIMESTAMP,
    phone_verified = TRUE,
    phone_verified_at = CURRENT_TIMESTAMP,
    trainer_verified = CASE WHEN username = 'demo_trainer' THEN TRUE ELSE trainer_verified END,
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
        WHEN username = 'demo_client' THEN 'Premium client demo account with a live trainer link and a ready-to-use premium profile.'
        WHEN username = 'demo_trainer' THEN 'Trainer demo account with an active code, premium access, and linked client data.'
        WHEN username = 'demo_gym' THEN 'Gym admin demo account with premium platform access and an active gym profile.'
        WHEN username = 'demo_admin' THEN 'Platform admin demo account for moderation, support, and platform controls.'
        ELSE bio
    END,
    profile_image_url = CASE
        WHEN username = 'demo_client' THEN '/img/chat/charlie-avatar.svg'
        WHEN username = 'demo_trainer' THEN '/img/Products/Short_Sleeve_Top/Short_Sleeve_Front.jpg'
        WHEN username = 'demo_gym' THEN '/img/Products/Long_Sleeve_Top/Long_Sleeve_Front.jpg'
        WHEN username = 'demo_admin' THEN '/img/brand/tab_logo.png'
        ELSE profile_image_url
    END
WHERE username IN ('demo_client', 'demo_trainer', 'demo_gym', 'demo_admin');

-- =========================================================================
-- PREMIUM STATUS
-- =========================================================================

INSERT INTO platform_subscriptions (user_id, plan, status, current_period_end, cancel_at_period_end)
SELECT u.id, 'MONTHLY', 'ACTIVE', CURRENT_TIMESTAMP + INTERVAL '90' DAY, FALSE
FROM users u
WHERE u.username IN ('demo_client', 'demo_trainer', 'demo_gym', 'demo_admin')
  AND NOT EXISTS (SELECT 1 FROM platform_subscriptions ps WHERE ps.user_id = u.id);

UPDATE platform_subscriptions
SET plan = 'MONTHLY',
    status = 'ACTIVE',
    current_period_end = CURRENT_TIMESTAMP + INTERVAL '90' DAY,
    cancel_at_period_end = FALSE
WHERE user_id IN (
    SELECT id FROM users WHERE username IN ('demo_client', 'demo_trainer', 'demo_gym', 'demo_admin')
);

-- =========================================================================
-- SETTINGS / POINTS
-- =========================================================================

INSERT INTO user_settings (
    user_id, language, theme, easy_mode, calendar_view_preference, default_sets,
    default_rep_min, default_rep_max, monthly_workout_target, dashboard_immersion_enabled,
    weather_temperature_unit, weather_display_mode, time_display_format, quick_preferences_completed
)
SELECT u.id, 'en', 'LIGHT', FALSE, 'WEEK', 4,
       6, 10, 12, TRUE,
       'CELSIUS', 'VISUAL', 'TWELVE_HOUR', TRUE
FROM users u
WHERE u.username IN ('demo_client', 'demo_trainer', 'demo_gym', 'demo_admin')
  AND NOT EXISTS (SELECT 1 FROM user_settings us WHERE us.user_id = u.id);

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
-- PROFILES / LINKS
-- =========================================================================

INSERT INTO trainer_profiles (
    user_id, bio, trainer_code, specializations, location, primary_gym, price_per_session,
    instagram_url, youtube_url, show_instagram, show_tiktok, show_youtube, show_linkedin, show_website, created_at, updated_at
)
SELECT (SELECT id FROM users WHERE username = 'demo_trainer'),
    'Premium trainer demo profile with structured coaching blocks, form review emphasis, and sustainable performance planning.',
    'DMTR20260001',
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
SET trainer_code = 'DMTR20260001',
    specializations = 'Strength Coaching, Performance, Habit Building',
    location = 'Cardiff, UK',
    primary_gym = 'Harbour Strength Club',
    price_per_session = 55,
    updated_at = CURRENT_TIMESTAMP - INTERVAL '2' DAY
WHERE user_id = (SELECT id FROM users WHERE username = 'demo_trainer');

INSERT INTO gym_profiles (user_id, gym_name, gym_code, address, city, contact_name, contact_phone, created_at, updated_at)
SELECT (SELECT id FROM users WHERE username = 'demo_gym'),
    'Harbour Strength Club',
    '4827001938456201',
    '18 Dock Street',
    'Cardiff',
    'Harbour Operations',
    '+44 29 5555 0101',
    CURRENT_TIMESTAMP - INTERVAL '180' DAY,
    CURRENT_TIMESTAMP - INTERVAL '4' DAY
WHERE EXISTS (SELECT 1 FROM users WHERE username = 'demo_gym')
  AND NOT EXISTS (SELECT 1 FROM gym_profiles WHERE user_id = (SELECT id FROM users WHERE username = 'demo_gym'));

UPDATE gym_profiles
SET gym_code = '4827001938456201',
    address = '18 Dock Street',
    city = 'Cardiff',
    contact_name = 'Harbour Operations',
    contact_phone = '+44 29 5555 0101',
    updated_at = CURRENT_TIMESTAMP - INTERVAL '4' DAY
WHERE user_id = (SELECT id FROM users WHERE username = 'demo_gym');

-- Gym-admin routes resolve their operational scope from users.gym_id.
UPDATE users
SET gym_id = (SELECT id FROM gym_profiles WHERE user_id = users.id)
WHERE username = 'demo_gym'
  AND EXISTS (SELECT 1 FROM gym_profiles WHERE user_id = users.id);

INSERT INTO trainer_client_links (
    client_id, trainer_id, status, requested_at, activated_at, created_at, updated_at,
    coaching_phase, coaching_phase_label, coaching_phase_started_at, coaching_phase_updated_at
)
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
