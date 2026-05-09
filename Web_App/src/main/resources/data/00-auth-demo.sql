-- Dev/demo auth seed (idempotent)
-- Demo credentials:
--   username: demo
--   password: Demo123!
--   username: demo2
--   password: Demo123!
--   username: trainer_demo
--   password: Demo123!
--   username: gymadmin_demo
--   password: Demo123!
--   username: admin_demo
--   password: Demo123!
--   username: superadmin_demo
--   password: Demo123!
--   username: superadmin_ops
--   password: Demo123!

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

INSERT INTO roles (name)
SELECT 'SUPER_ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'SUPER_ADMIN');

INSERT INTO users (public_id, email, first_name, last_name, username, password, enabled, subscription_status, role)
SELECT '3a7b6f1b-2bd7-4e5d-a70e-1b4a7a9d93a2', 'demo@example.com', 'Demo', 'User', 'demo', '$2a$10$2EZk8xjJekcabhOOKPsxtuHWvgrgWunYC2v57bCNiEk8c8HxHedH6', true, true, 'CLIENT'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'demo');

INSERT INTO users (public_id, email, first_name, last_name, username, password, enabled, subscription_status, role)
SELECT 'f4a7d8f7-3e83-4cf0-9f67-ec2f3d56a4f1', 'demo2@example.com', 'Demo', 'Two', 'demo2', '$2a$10$2EZk8xjJekcabhOOKPsxtuHWvgrgWunYC2v57bCNiEk8c8HxHedH6', true, false, 'CLIENT'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'demo2');

INSERT INTO users (public_id, email, first_name, last_name, username, password, enabled, subscription_status, role)
SELECT 'a7f5c9e0-2b8f-4f7b-ae59-5b6d0f7f1a63', 'trainer_demo@example.com', 'Trainer', 'Demo', 'trainer_demo', '$2a$10$2EZk8xjJekcabhOOKPsxtuHWvgrgWunYC2v57bCNiEk8c8HxHedH6', true, true, 'TRAINER'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'trainer_demo');

INSERT INTO users (public_id, email, first_name, last_name, username, password, enabled, subscription_status, role)
SELECT '0f2e9fd2-1e38-4d55-9b4c-2a77c8122e0b', 'gymadmin_demo@example.com', 'Gym', 'Admin', 'gymadmin_demo', '$2a$10$2EZk8xjJekcabhOOKPsxtuHWvgrgWunYC2v57bCNiEk8c8HxHedH6', true, true, 'GYM_ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'gymadmin_demo');

INSERT INTO users (public_id, email, first_name, last_name, username, password, enabled, subscription_status, role)
SELECT 'b1c2d3e4-f5a6-7b8c-9d0e-1f2a3b4c5d6e', 'admin_demo@example.com', 'Admin', 'Demo', 'admin_demo', '$2a$10$2EZk8xjJekcabhOOKPsxtuHWvgrgWunYC2v57bCNiEk8c8HxHedH6', true, true, 'PLATFORM_ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin_demo');

INSERT INTO users (public_id, email, first_name, last_name, username, password, enabled, subscription_status, role)
SELECT 'cb3d373e-8f3b-4957-a2bf-53ef4c0fe538', 'superadmin_demo@example.com', 'Super', 'Admin', 'superadmin_demo', '$2a$10$2EZk8xjJekcabhOOKPsxtuHWvgrgWunYC2v57bCNiEk8c8HxHedH6', true, true, 'SUPER_ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'superadmin_demo');

INSERT INTO users (public_id, email, first_name, last_name, username, password, enabled, subscription_status, role)
SELECT '5e25816e-7999-4684-8dd0-cf4c945fd4cf', 'superadmin_ops@example.com', 'Operations', 'Lead', 'superadmin_ops', '$2a$10$2EZk8xjJekcabhOOKPsxtuHWvgrgWunYC2v57bCNiEk8c8HxHedH6', true, true, 'SUPER_ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'superadmin_ops');

INSERT INTO users_roles (username, role_id)
SELECT 'demo', (SELECT role_id FROM roles WHERE name = 'USER')
WHERE NOT EXISTS (
    SELECT 1
    FROM users_roles ur
    WHERE ur.username = 'demo'
      AND ur.role_id = (SELECT role_id FROM roles WHERE name = 'USER')
);

INSERT INTO users_roles (username, role_id)
SELECT 'demo', (SELECT role_id FROM roles WHERE name = 'CLIENT')
WHERE NOT EXISTS (
    SELECT 1
    FROM users_roles ur
    WHERE ur.username = 'demo'
      AND ur.role_id = (SELECT role_id FROM roles WHERE name = 'CLIENT')
);

    INSERT INTO users_roles (username, role_id)
    SELECT 'demo2', (SELECT role_id FROM roles WHERE name = 'USER')
    WHERE NOT EXISTS (
        SELECT 1
        FROM users_roles ur
        WHERE ur.username = 'demo2'
      AND ur.role_id = (SELECT role_id FROM roles WHERE name = 'USER')
    );

    INSERT INTO users_roles (username, role_id)
    SELECT 'demo2', (SELECT role_id FROM roles WHERE name = 'CLIENT')
    WHERE NOT EXISTS (
        SELECT 1
        FROM users_roles ur
        WHERE ur.username = 'demo2'
      AND ur.role_id = (SELECT role_id FROM roles WHERE name = 'CLIENT')
    );

INSERT INTO users_roles (username, role_id)
SELECT 'trainer_demo', (SELECT role_id FROM roles WHERE name = 'USER')
WHERE NOT EXISTS (
    SELECT 1
    FROM users_roles ur
    WHERE ur.username = 'trainer_demo'
      AND ur.role_id = (SELECT role_id FROM roles WHERE name = 'USER')
);

INSERT INTO users_roles (username, role_id)
SELECT 'trainer_demo', (SELECT role_id FROM roles WHERE name = 'TRAINER')
WHERE NOT EXISTS (
    SELECT 1
    FROM users_roles ur
    WHERE ur.username = 'trainer_demo'
      AND ur.role_id = (SELECT role_id FROM roles WHERE name = 'TRAINER')
);

INSERT INTO users_roles (username, role_id)
SELECT 'gymadmin_demo', (SELECT role_id FROM roles WHERE name = 'USER')
WHERE NOT EXISTS (
    SELECT 1
    FROM users_roles ur
    WHERE ur.username = 'gymadmin_demo'
      AND ur.role_id = (SELECT role_id FROM roles WHERE name = 'USER')
);

INSERT INTO users_roles (username, role_id)
SELECT 'gymadmin_demo', (SELECT role_id FROM roles WHERE name = 'GYM_ADMIN')
WHERE NOT EXISTS (
    SELECT 1
    FROM users_roles ur
    WHERE ur.username = 'gymadmin_demo'
      AND ur.role_id = (SELECT role_id FROM roles WHERE name = 'GYM_ADMIN')
);

INSERT INTO users_roles (username, role_id)
SELECT 'admin_demo', (SELECT role_id FROM roles WHERE name = 'USER')
WHERE NOT EXISTS (
    SELECT 1
    FROM users_roles ur
    WHERE ur.username = 'admin_demo'
      AND ur.role_id = (SELECT role_id FROM roles WHERE name = 'USER')
);

INSERT INTO users_roles (username, role_id)
SELECT 'admin_demo', (SELECT role_id FROM roles WHERE name = 'PLATFORM_ADMIN')
WHERE NOT EXISTS (
    SELECT 1
    FROM users_roles ur
    WHERE ur.username = 'admin_demo'
      AND ur.role_id = (SELECT role_id FROM roles WHERE name = 'PLATFORM_ADMIN')
);

INSERT INTO users_roles (username, role_id)
SELECT 'superadmin_demo', (SELECT role_id FROM roles WHERE name = 'USER')
WHERE NOT EXISTS (
    SELECT 1
    FROM users_roles ur
    WHERE ur.username = 'superadmin_demo'
      AND ur.role_id = (SELECT role_id FROM roles WHERE name = 'USER')
);

INSERT INTO users_roles (username, role_id)
SELECT 'superadmin_demo', (SELECT role_id FROM roles WHERE name = 'SUPER_ADMIN')
WHERE NOT EXISTS (
    SELECT 1
    FROM users_roles ur
    WHERE ur.username = 'superadmin_demo'
      AND ur.role_id = (SELECT role_id FROM roles WHERE name = 'SUPER_ADMIN')
);

INSERT INTO users_roles (username, role_id)
SELECT 'superadmin_ops', (SELECT role_id FROM roles WHERE name = 'USER')
WHERE NOT EXISTS (
    SELECT 1
    FROM users_roles ur
    WHERE ur.username = 'superadmin_ops'
      AND ur.role_id = (SELECT role_id FROM roles WHERE name = 'USER')
);

INSERT INTO users_roles (username, role_id)
SELECT 'superadmin_ops', (SELECT role_id FROM roles WHERE name = 'SUPER_ADMIN')
WHERE NOT EXISTS (
    SELECT 1
    FROM users_roles ur
    WHERE ur.username = 'superadmin_ops'
      AND ur.role_id = (SELECT role_id FROM roles WHERE name = 'SUPER_ADMIN')
);

-- Keep demo premium and demo2 non-premium in both user and subscription tables
UPDATE users
SET subscription_status = true
WHERE username = 'demo';

UPDATE users
SET email = 'jhudson172@icloud.com',
    phone_number = '7858256917',
    date_of_birth = DATE '2006-05-26',
    email_verified = TRUE,
    email_verified_at = CURRENT_TIMESTAMP,
    phone_verified = TRUE,
    phone_verified_at = CURRENT_TIMESTAMP,
    bio = 'Focused on consistency, form, and building long-term strength one session at a time.',
    profile_image_url = '/img/logo.png'
WHERE username = 'demo';

UPDATE users
SET subscription_status = false
WHERE username = 'demo2';

UPDATE users
SET email_verified = TRUE,
    email_verified_at = CURRENT_TIMESTAMP,
    phone_number = '7858256918',
    phone_verified = TRUE,
    phone_verified_at = CURRENT_TIMESTAMP
WHERE username = 'demo2';

INSERT INTO platform_subscriptions (user_id, plan, status, current_period_end, cancel_at_period_end)
SELECT u.id, 'MONTHLY', 'ACTIVE', CURRENT_TIMESTAMP + INTERVAL '30' DAY, FALSE
FROM users u
WHERE u.username = 'demo'
  AND NOT EXISTS (
    SELECT 1 FROM platform_subscriptions ps WHERE ps.user_id = u.id
  );

UPDATE platform_subscriptions ps
SET plan = 'MONTHLY',
    status = 'ACTIVE',
  current_period_end = CURRENT_TIMESTAMP + INTERVAL '30' DAY,
    cancel_at_period_end = FALSE
WHERE ps.user_id = (SELECT id FROM users WHERE username = 'demo');

INSERT INTO platform_subscriptions (user_id, plan, status, current_period_end, cancel_at_period_end)
SELECT u.id, 'MONTHLY', 'CANCELLED', CURRENT_TIMESTAMP - INTERVAL '1' DAY, TRUE
FROM users u
WHERE u.username = 'demo2'
  AND NOT EXISTS (
    SELECT 1 FROM platform_subscriptions ps WHERE ps.user_id = u.id
  );

UPDATE platform_subscriptions ps
SET plan = 'MONTHLY',
    status = 'CANCELLED',
  current_period_end = CURRENT_TIMESTAMP - INTERVAL '1' DAY,
    cancel_at_period_end = TRUE
WHERE ps.user_id = (SELECT id FROM users WHERE username = 'demo2');

INSERT INTO platform_subscriptions (user_id, plan, status, current_period_end, cancel_at_period_end)
SELECT u.id, 'MONTHLY', 'ACTIVE', CURRENT_TIMESTAMP + INTERVAL '30' DAY, FALSE
FROM users u
WHERE u.username = 'admin_demo'
  AND NOT EXISTS (
    SELECT 1 FROM platform_subscriptions ps WHERE ps.user_id = u.id
  );

UPDATE platform_subscriptions ps
SET plan = 'MONTHLY',
    status = 'ACTIVE',
    current_period_end = CURRENT_TIMESTAMP + INTERVAL '30' DAY,
    cancel_at_period_end = FALSE
WHERE ps.user_id = (SELECT id FROM users WHERE username = 'admin_demo');

INSERT INTO platform_subscriptions (user_id, plan, status, current_period_end, cancel_at_period_end)
SELECT u.id, 'MONTHLY', 'ACTIVE', CURRENT_TIMESTAMP + INTERVAL '30' DAY, FALSE
FROM users u
WHERE u.username IN ('superadmin_demo', 'superadmin_ops')
  AND NOT EXISTS (
    SELECT 1 FROM platform_subscriptions ps WHERE ps.user_id = u.id
  );

UPDATE platform_subscriptions ps
SET plan = 'MONTHLY',
    status = 'ACTIVE',
    current_period_end = CURRENT_TIMESTAMP + INTERVAL '30' DAY,
    cancel_at_period_end = FALSE
WHERE ps.user_id IN (
    SELECT id FROM users WHERE username IN ('superadmin_demo', 'superadmin_ops')
);
