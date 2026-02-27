-- Seed data for preferences and physical_conditions tables (idempotent)

-- =========================
-- PHYSICAL CONDITIONS
-- =========================

INSERT INTO physical_conditions (name)
SELECT 'Asthma' WHERE NOT EXISTS (SELECT 1 FROM physical_conditions WHERE name = 'Asthma');

INSERT INTO physical_conditions (name)
SELECT 'High Blood Pressure (Hypertension)' WHERE NOT EXISTS (SELECT 1 FROM physical_conditions WHERE name = 'High Blood Pressure (Hypertension)');

INSERT INTO physical_conditions (name)
SELECT 'Diabetes Type 1' WHERE NOT EXISTS (SELECT 1 FROM physical_conditions WHERE name = 'Diabetes Type 1');

INSERT INTO physical_conditions (name)
SELECT 'Diabetes Type 2' WHERE NOT EXISTS (SELECT 1 FROM physical_conditions WHERE name = 'Diabetes Type 2');

INSERT INTO physical_conditions (name)
SELECT 'Lower Back Pain' WHERE NOT EXISTS (SELECT 1 FROM physical_conditions WHERE name = 'Lower Back Pain');

INSERT INTO physical_conditions (name)
SELECT 'Knee Pain / Osteoarthritis' WHERE NOT EXISTS (SELECT 1 FROM physical_conditions WHERE name = 'Knee Pain / Osteoarthritis');

INSERT INTO physical_conditions (name)
SELECT 'Shoulder Injury' WHERE NOT EXISTS (SELECT 1 FROM physical_conditions WHERE name = 'Shoulder Injury');

INSERT INTO physical_conditions (name)
SELECT 'Osteoporosis' WHERE NOT EXISTS (SELECT 1 FROM physical_conditions WHERE name = 'Osteoporosis');

INSERT INTO physical_conditions (name)
SELECT 'Heart Condition' WHERE NOT EXISTS (SELECT 1 FROM physical_conditions WHERE name = 'Heart Condition');

INSERT INTO physical_conditions (name)
SELECT 'Anxiety / Stress' WHERE NOT EXISTS (SELECT 1 FROM physical_conditions WHERE name = 'Anxiety / Stress');

INSERT INTO physical_conditions (name)
SELECT 'Chronic Fatigue' WHERE NOT EXISTS (SELECT 1 FROM physical_conditions WHERE name = 'Chronic Fatigue');

INSERT INTO physical_conditions (name)
SELECT 'Arthritis' WHERE NOT EXISTS (SELECT 1 FROM physical_conditions WHERE name = 'Arthritis');

INSERT INTO physical_conditions (name)
SELECT 'Obesity / High BMI' WHERE NOT EXISTS (SELECT 1 FROM physical_conditions WHERE name = 'Obesity / High BMI');

INSERT INTO physical_conditions (name)
SELECT 'Plantar Fasciitis' WHERE NOT EXISTS (SELECT 1 FROM physical_conditions WHERE name = 'Plantar Fasciitis');

INSERT INTO physical_conditions (name)
SELECT 'Herniated Disc' WHERE NOT EXISTS (SELECT 1 FROM physical_conditions WHERE name = 'Herniated Disc');

-- =========================
-- TRAINING PREFERENCES
-- =========================

-- Category: Goal
INSERT INTO preferences (category, description)
SELECT 'Goal', 'Weight Loss'
WHERE NOT EXISTS (SELECT 1 FROM preferences WHERE category = 'Goal' AND description = 'Weight Loss');

INSERT INTO preferences (category, description)
SELECT 'Goal', 'Muscle Gain / Hypertrophy'
WHERE NOT EXISTS (SELECT 1 FROM preferences WHERE category = 'Goal' AND description = 'Muscle Gain / Hypertrophy');

INSERT INTO preferences (category, description)
SELECT 'Goal', 'Improve Endurance'
WHERE NOT EXISTS (SELECT 1 FROM preferences WHERE category = 'Goal' AND description = 'Improve Endurance');

INSERT INTO preferences (category, description)
SELECT 'Goal', 'Increase Strength'
WHERE NOT EXISTS (SELECT 1 FROM preferences WHERE category = 'Goal' AND description = 'Increase Strength');

INSERT INTO preferences (category, description)
SELECT 'Goal', 'Improve Flexibility & Mobility'
WHERE NOT EXISTS (SELECT 1 FROM preferences WHERE category = 'Goal' AND description = 'Improve Flexibility & Mobility');

INSERT INTO preferences (category, description)
SELECT 'Goal', 'General Health & Fitness'
WHERE NOT EXISTS (SELECT 1 FROM preferences WHERE category = 'Goal' AND description = 'General Health & Fitness');

INSERT INTO preferences (category, description)
SELECT 'Goal', 'Sports Performance'
WHERE NOT EXISTS (SELECT 1 FROM preferences WHERE category = 'Goal' AND description = 'Sports Performance');

-- Category: Experience Level
INSERT INTO preferences (category, description)
SELECT 'Experience Level', 'Beginner (New to exercise)'
WHERE NOT EXISTS (SELECT 1 FROM preferences WHERE category = 'Experience Level' AND description = 'Beginner (New to exercise)');

INSERT INTO preferences (category, description)
SELECT 'Experience Level', 'Intermediate (Exercising regularly)'
WHERE NOT EXISTS (SELECT 1 FROM preferences WHERE category = 'Experience Level' AND description = 'Intermediate (Exercising regularly)');

INSERT INTO preferences (category, description)
SELECT 'Experience Level', 'Advanced (Experienced athlete)'
WHERE NOT EXISTS (SELECT 1 FROM preferences WHERE category = 'Experience Level' AND description = 'Advanced (Experienced athlete)');

-- Category: Workout Style
INSERT INTO preferences (category, description)
SELECT 'Workout Style', 'Home Workouts'
WHERE NOT EXISTS (SELECT 1 FROM preferences WHERE category = 'Workout Style' AND description = 'Home Workouts');

INSERT INTO preferences (category, description)
SELECT 'Workout Style', 'Gym Workouts'
WHERE NOT EXISTS (SELECT 1 FROM preferences WHERE category = 'Workout Style' AND description = 'Gym Workouts');

INSERT INTO preferences (category, description)
SELECT 'Workout Style', 'Outdoor Activities'
WHERE NOT EXISTS (SELECT 1 FROM preferences WHERE category = 'Workout Style' AND description = 'Outdoor Activities');

INSERT INTO preferences (category, description)
SELECT 'Workout Style', 'Group / Class Training'
WHERE NOT EXISTS (SELECT 1 FROM preferences WHERE category = 'Workout Style' AND description = 'Group / Class Training');

INSERT INTO preferences (category, description)
SELECT 'Workout Style', 'Solo Training'
WHERE NOT EXISTS (SELECT 1 FROM preferences WHERE category = 'Workout Style' AND description = 'Solo Training');

INSERT INTO preferences (category, description)
SELECT 'Workout Style', 'HIIT / High Intensity'
WHERE NOT EXISTS (SELECT 1 FROM preferences WHERE category = 'Workout Style' AND description = 'HIIT / High Intensity');

INSERT INTO preferences (category, description)
SELECT 'Workout Style', 'Low Impact'
WHERE NOT EXISTS (SELECT 1 FROM preferences WHERE category = 'Workout Style' AND description = 'Low Impact');

-- Category: Workout Frequency
INSERT INTO preferences (category, description)
SELECT 'Workout Frequency', '1–2 times per week'
WHERE NOT EXISTS (SELECT 1 FROM preferences WHERE category = 'Workout Frequency' AND description = '1–2 times per week');

INSERT INTO preferences (category, description)
SELECT 'Workout Frequency', '3–4 times per week'
WHERE NOT EXISTS (SELECT 1 FROM preferences WHERE category = 'Workout Frequency' AND description = '3–4 times per week');

INSERT INTO preferences (category, description)
SELECT 'Workout Frequency', '5+ times per week'
WHERE NOT EXISTS (SELECT 1 FROM preferences WHERE category = 'Workout Frequency' AND description = '5+ times per week');

INSERT INTO preferences (category, description)
SELECT 'Workout Frequency', 'Daily'
WHERE NOT EXISTS (SELECT 1 FROM preferences WHERE category = 'Workout Frequency' AND description = 'Daily');

-- Category: Diet / Nutrition
INSERT INTO preferences (category, description)
SELECT 'Diet / Nutrition', 'High Protein'
WHERE NOT EXISTS (SELECT 1 FROM preferences WHERE category = 'Diet / Nutrition' AND description = 'High Protein');

INSERT INTO preferences (category, description)
SELECT 'Diet / Nutrition', 'Low Carb'
WHERE NOT EXISTS (SELECT 1 FROM preferences WHERE category = 'Diet / Nutrition' AND description = 'Low Carb');

INSERT INTO preferences (category, description)
SELECT 'Diet / Nutrition', 'Vegetarian'
WHERE NOT EXISTS (SELECT 1 FROM preferences WHERE category = 'Diet / Nutrition' AND description = 'Vegetarian');

INSERT INTO preferences (category, description)
SELECT 'Diet / Nutrition', 'Vegan'
WHERE NOT EXISTS (SELECT 1 FROM preferences WHERE category = 'Diet / Nutrition' AND description = 'Vegan');

INSERT INTO preferences (category, description)
SELECT 'Diet / Nutrition', 'Calorie Surplus (Bulking)'
WHERE NOT EXISTS (SELECT 1 FROM preferences WHERE category = 'Diet / Nutrition' AND description = 'Calorie Surplus (Bulking)');

INSERT INTO preferences (category, description)
SELECT 'Diet / Nutrition', 'Calorie Deficit (Cutting)'
WHERE NOT EXISTS (SELECT 1 FROM preferences WHERE category = 'Diet / Nutrition' AND description = 'Calorie Deficit (Cutting)');

-- Category: Recovery
INSERT INTO preferences (category, description)
SELECT 'Recovery', 'Prioritise Recovery Days'
WHERE NOT EXISTS (SELECT 1 FROM preferences WHERE category = 'Recovery' AND description = 'Prioritise Recovery Days');

INSERT INTO preferences (category, description)
SELECT 'Recovery', 'Active Recovery (light movement)'
WHERE NOT EXISTS (SELECT 1 FROM preferences WHERE category = 'Recovery' AND description = 'Active Recovery (light movement)');

INSERT INTO preferences (category, description)
SELECT 'Recovery', 'Regular Stretching / Yoga'
WHERE NOT EXISTS (SELECT 1 FROM preferences WHERE category = 'Recovery' AND description = 'Regular Stretching / Yoga');

INSERT INTO preferences (category, description)
SELECT 'Recovery', 'Sleep Optimisation'
WHERE NOT EXISTS (SELECT 1 FROM preferences WHERE category = 'Recovery' AND description = 'Sleep Optimisation');
