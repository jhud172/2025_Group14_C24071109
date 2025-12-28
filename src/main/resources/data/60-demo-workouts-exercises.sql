-- Helper pattern:
-- Inserts pairs only if workout exists AND exercise exists.
-- Avoids duplicates with ON CONFLICT DO NOTHING.

-- 1. Push A
INSERT INTO workouts_exercises (workout_id, exercise_id)
SELECT 1, v.exercise_id
FROM (VALUES (1),(2),(3),(4),(5),(6)) AS v(exercise_id)
WHERE EXISTS (SELECT 1 FROM workouts  w WHERE w.id = 1)
  AND EXISTS (SELECT 1 FROM exercises e WHERE e.id = v.exercise_id)
ON CONFLICT DO NOTHING;

-- 2. Pull A
INSERT INTO workouts_exercises (workout_id, exercise_id)
SELECT 2, v.exercise_id
FROM (VALUES (10),(11),(24),(25),(12),(13)) AS v(exercise_id)
WHERE EXISTS (SELECT 1 FROM workouts  w WHERE w.id = 2)
  AND EXISTS (SELECT 1 FROM exercises e WHERE e.id = v.exercise_id)
ON CONFLICT DO NOTHING;

-- 3. Legs A
INSERT INTO workouts_exercises (workout_id, exercise_id)
SELECT 3, v.exercise_id
FROM (VALUES (7),(8),(9),(16),(17),(19)) AS v(exercise_id)
WHERE EXISTS (SELECT 1 FROM workouts  w WHERE w.id = 3)
  AND EXISTS (SELECT 1 FROM exercises e WHERE e.id = v.exercise_id)
ON CONFLICT DO NOTHING;

-- 4. Push B
INSERT INTO workouts_exercises (workout_id, exercise_id)
SELECT 4, v.exercise_id
FROM (VALUES (2),(14),(30),(4),(6),(5)) AS v(exercise_id)
WHERE EXISTS (SELECT 1 FROM workouts  w WHERE w.id = 4)
  AND EXISTS (SELECT 1 FROM exercises e WHERE e.id = v.exercise_id)
ON CONFLICT DO NOTHING;

-- 5. Pull B
INSERT INTO workouts_exercises (workout_id, exercise_id)
SELECT 5, v.exercise_id
FROM (VALUES (28),(11),(27),(24),(25),(12)) AS v(exercise_id)
WHERE EXISTS (SELECT 1 FROM workouts  w WHERE w.id = 5)
  AND EXISTS (SELECT 1 FROM exercises e WHERE e.id = v.exercise_id)
ON CONFLICT DO NOTHING;

-- 6. Legs B
INSERT INTO workouts_exercises (workout_id, exercise_id)
SELECT 6, v.exercise_id
FROM (VALUES (18),(9),(7),(17),(19),(16)) AS v(exercise_id)
WHERE EXISTS (SELECT 1 FROM workouts  w WHERE w.id = 6)
  AND EXISTS (SELECT 1 FROM exercises e WHERE e.id = v.exercise_id)
ON CONFLICT DO NOTHING;

-- 7. Upper Strength
INSERT INTO workouts_exercises (workout_id, exercise_id)
SELECT 7, v.exercise_id
FROM (VALUES (1),(3),(10),(11),(12),(5)) AS v(exercise_id)
WHERE EXISTS (SELECT 1 FROM workouts  w WHERE w.id = 7)
  AND EXISTS (SELECT 1 FROM exercises e WHERE e.id = v.exercise_id)
ON CONFLICT DO NOTHING;

-- 8. Lower Strength
INSERT INTO workouts_exercises (workout_id, exercise_id)
SELECT 8, v.exercise_id
FROM (VALUES (7),(26),(27),(9),(19),(16)) AS v(exercise_id)
WHERE EXISTS (SELECT 1 FROM workouts  w WHERE w.id = 8)
  AND EXISTS (SELECT 1 FROM exercises e WHERE e.id = v.exercise_id)
ON CONFLICT DO NOTHING;

-- 9. Chest & Arms
INSERT INTO workouts_exercises (workout_id, exercise_id)
SELECT 9, v.exercise_id
FROM (VALUES (1),(14),(15),(12),(13),(29)) AS v(exercise_id)
WHERE EXISTS (SELECT 1 FROM workouts  w WHERE w.id = 9)
  AND EXISTS (SELECT 1 FROM exercises e WHERE e.id = v.exercise_id)
ON CONFLICT DO NOTHING;

-- 10. Back & Shoulders
INSERT INTO workouts_exercises (workout_id, exercise_id)
SELECT 10, v.exercise_id
FROM (VALUES (10),(11),(25),(3),(4),(28)) AS v(exercise_id)
WHERE EXISTS (SELECT 1 FROM workouts  w WHERE w.id = 10)
  AND EXISTS (SELECT 1 FROM exercises e WHERE e.id = v.exercise_id)
ON CONFLICT DO NOTHING;

-- 11. HIIT Circuit
INSERT INTO workouts_exercises (workout_id, exercise_id)
SELECT 11, v.exercise_id
FROM (VALUES (7),(22),(23),(20),(28),(29)) AS v(exercise_id)
WHERE EXISTS (SELECT 1 FROM workouts  w WHERE w.id = 11)
  AND EXISTS (SELECT 1 FROM exercises e WHERE e.id = v.exercise_id)
ON CONFLICT DO NOTHING;

-- 12. Core & Stability
INSERT INTO workouts_exercises (workout_id, exercise_id)
SELECT 12, v.exercise_id
FROM (VALUES (20),(21),(22),(23),(25),(18)) AS v(exercise_id)
WHERE EXISTS (SELECT 1 FROM workouts  w WHERE w.id = 12)
  AND EXISTS (SELECT 1 FROM exercises e WHERE e.id = v.exercise_id)
ON CONFLICT DO NOTHING;

-- 13. Mobility A
INSERT INTO workouts_exercises (workout_id, exercise_id)
SELECT 13, v.exercise_id
FROM (VALUES (21),(22),(23),(12),(4)) AS v(exercise_id)
WHERE EXISTS (SELECT 1 FROM workouts  w WHERE w.id = 13)
  AND EXISTS (SELECT 1 FROM exercises e WHERE e.id = v.exercise_id)
ON CONFLICT DO NOTHING;

-- 14. Mobility B
INSERT INTO workouts_exercises (workout_id, exercise_id)
SELECT 14, v.exercise_id
FROM (VALUES (16),(17),(21),(20),(8)) AS v(exercise_id)
WHERE EXISTS (SELECT 1 FROM workouts  w WHERE w.id = 14)
  AND EXISTS (SELECT 1 FROM exercises e WHERE e.id = v.exercise_id)
ON CONFLICT DO NOTHING;

-- 15. Light Full Body A
INSERT INTO workouts_exercises (workout_id, exercise_id)
SELECT 15, v.exercise_id
FROM (VALUES (13),(12),(18),(7),(10)) AS v(exercise_id)
WHERE EXISTS (SELECT 1 FROM workouts  w WHERE w.id = 15)
  AND EXISTS (SELECT 1 FROM exercises e WHERE e.id = v.exercise_id)
ON CONFLICT DO NOTHING;

-- 16. Light Full Body B
INSERT INTO workouts_exercises (workout_id, exercise_id)
SELECT 16, v.exercise_id
FROM (VALUES (12),(13),(18),(9),(11)) AS v(exercise_id)
WHERE EXISTS (SELECT 1 FROM workouts  w WHERE w.id = 16)
  AND EXISTS (SELECT 1 FROM exercises e WHERE e.id = v.exercise_id)
ON CONFLICT DO NOTHING;

-- 17. Rehab – Lower Back
INSERT INTO workouts_exercises (workout_id, exercise_id)
SELECT 17, v.exercise_id
FROM (VALUES (20),(21),(18),(9),(19)) AS v(exercise_id)
WHERE EXISTS (SELECT 1 FROM workouts  w WHERE w.id = 17)
  AND EXISTS (SELECT 1 FROM exercises e WHERE e.id = v.exercise_id)
ON CONFLICT DO NOTHING;

-- 18. Rehab – Knee
INSERT INTO workouts_exercises (workout_id, exercise_id)
SELECT 18, v.exercise_id
FROM (VALUES (7),(16),(19),(20),(21)) AS v(exercise_id)
WHERE EXISTS (SELECT 1 FROM workouts  w WHERE w.id = 18)
  AND EXISTS (SELECT 1 FROM exercises e WHERE e.id = v.exercise_id)
ON CONFLICT DO NOTHING;

-- 19. Cardio & Stretch
INSERT INTO workouts_exercises (workout_id, exercise_id)
SELECT 19, v.exercise_id
FROM (VALUES (20),(21),(22),(23),(16)) AS v(exercise_id)
WHERE EXISTS (SELECT 1 FROM workouts  w WHERE w.id = 19)
  AND EXISTS (SELECT 1 FROM exercises e WHERE e.id = v.exercise_id)
ON CONFLICT DO NOTHING;

-- 20. Beginner Strength
INSERT INTO workouts_exercises (workout_id, exercise_id)
SELECT 20, v.exercise_id
FROM (VALUES (7),(1),(10),(12),(19)) AS v(exercise_id)
WHERE EXISTS (SELECT 1 FROM workouts  w WHERE w.id = 20)
  AND EXISTS (SELECT 1 FROM exercises e WHERE e.id = v.exercise_id)
ON CONFLICT DO NOTHING;
