-- demo-schedule.sql

DELETE FROM schedule_occurrences;
DELETE FROM schedule_entries;
DELETE FROM schedules;

-- Two schedules, one per user
INSERT INTO schedules (id, user_id, name, description)
VALUES
    (1, 1, 'Morning Routine', 'Quick morning exercises'),
    (2, 2, 'Rehab Plan', 'Light band work');

-- Template entries for schedule 1 (user 1)
-- Monday = 1, Wednesday = 3, Friday = 5
INSERT INTO schedule_entries (id, schedule_id, exercise_id, day_of_week, order_number)
VALUES
    (1, 1, 1, 1, 1),   -- Mon: Exercise 1
    (2, 1, 2, 3, 1),   -- Wed: Exercise 2
    (3, 1, 3, 5, 1);   -- Fri: Exercise 3

-- Template entries for schedule 2 (user 2)
INSERT INTO schedule_entries (id, schedule_id, exercise_id, day_of_week, order_number)
VALUES
    (4, 2, 3, 2, 1),   -- Tue: Exercise 3
    (5, 2, 1, 4, 1);   -- Thu: Exercise 1

-- A few pre-generated occurrences to prove it works
INSERT INTO schedule_occurrences (id, user_id, exercise_id, date, schedule_name)
VALUES
    (1, 1, 1, '2025-01-06', 'Morning Routine'),
    (2, 1, 2, '2025-01-08', 'Morning Routine'),
    (3, 2, 3, '2025-01-07', 'Rehab Plan');
