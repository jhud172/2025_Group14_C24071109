DELETE FROM calendar_tasks;

-- =========================================
-- DEMO TASKS FOR USER 1
-- =========================================

INSERT INTO calendar_tasks (id, user_id, date, time, title, notes, is_exercise, completed)
VALUES
    (1, 1, '2025-11-06', '14:00:00', 'Eat Lunch', 'Chicken wrap + protein shake', FALSE, TRUE),
    (2, 1, '2025-11-06', '18:30:00', 'Evening Walk', '30 minutes around the park', TRUE, FALSE),
    (3, 1, '2025-11-07', NULL, 'Clean Desk Area', 'Sort cables + wipe surfaces', FALSE, FALSE),
    (4, 1, '2025-11-08', '09:00:00', 'Study Session', 'Focus on Java Spring Boot', FALSE, FALSE),
    (5, 1, '2025-11-08', '20:00:00', 'Call Family', NULL, FALSE, FALSE),
    (11, 1, '2025-11-06', '16:00:00', 'TEST 1', 'blehhh', FALSE, FALSE),
    (12, 1, '2025-11-06', '18:00:00', 'TEST 2', 'blahhh', FALSE, FALSE);

-- =========================================
-- DEMO TASKS FOR USER 2
-- =========================================

INSERT INTO calendar_tasks (id, user_id, date, time, title, notes, is_exercise, completed)
VALUES
    (6, 2, '2025-11-06', '13:00:00', 'Physio Stretching', 'Lower back routine', TRUE, FALSE),
    (7, 2, '2025-11-07', NULL, 'Buy Groceries', 'Eggs, rice, peanut butter', FALSE, FALSE),
    (8, 2, '2025-11-08', '16:45:00', 'Meal Prep', '3x chicken + rice portions', FALSE, FALSE),
    (9, 2, '2025-11-09', '10:30:00', 'Medium Walk', '30 minutes, physio-approved', TRUE, FALSE),
    (10, 2, '2025-11-09', '19:00:00', 'Watch Documentary', 'Any fitness science one', FALSE, FALSE);
