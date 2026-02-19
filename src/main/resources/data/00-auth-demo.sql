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

-- Keep demo premium and demo2 non-premium in both user and subscription tables
UPDATE users
SET subscription_status = true
WHERE username = 'demo';

UPDATE users
SET subscription_status = false
WHERE username = 'demo2';

INSERT INTO platform_subscriptions (user_id, plan, status, current_period_end, cancel_at_period_end)
SELECT u.id, 'MONTHLY', 'ACTIVE', CURRENT_TIMESTAMP + INTERVAL '30 days', FALSE
FROM users u
WHERE u.username = 'demo'
  AND NOT EXISTS (
    SELECT 1 FROM platform_subscriptions ps WHERE ps.user_id = u.id
  );

UPDATE platform_subscriptions ps
SET plan = 'MONTHLY',
    status = 'ACTIVE',
  current_period_end = CURRENT_TIMESTAMP + INTERVAL '30 days',
    cancel_at_period_end = FALSE
WHERE ps.user_id = (SELECT id FROM users WHERE username = 'demo');

INSERT INTO platform_subscriptions (user_id, plan, status, current_period_end, cancel_at_period_end)
SELECT u.id, 'MONTHLY', 'CANCELLED', CURRENT_TIMESTAMP - INTERVAL '1 day', TRUE
FROM users u
WHERE u.username = 'demo2'
  AND NOT EXISTS (
    SELECT 1 FROM platform_subscriptions ps WHERE ps.user_id = u.id
  );

UPDATE platform_subscriptions ps
SET plan = 'MONTHLY',
    status = 'CANCELLED',
  current_period_end = CURRENT_TIMESTAMP - INTERVAL '1 day',
    cancel_at_period_end = TRUE
WHERE ps.user_id = (SELECT id FROM users WHERE username = 'demo2');
