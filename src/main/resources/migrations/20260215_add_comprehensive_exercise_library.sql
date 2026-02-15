-- ===============================================
-- Comprehensive Exercise Library for Workout Studio
-- ===============================================
-- Adds 25+ high-quality exercises with full metadata
-- including muscle groups, equipment, difficulty, and video links

-- Clean up existing demo exercises (optional - we'll use INSERT ... ON CONFLICT DO NOTHING instead)

-- CHEST EXERCISES
INSERT INTO exercises (name, category, description, difficulty, type, video_url)
VALUES 
('Barbell Bench Press', 'Chest', 'Classic compound chest exercise. Lie on a flat bench and press a barbell from chest level to full arm extension. Primary: Pectoralis Major, Secondary: Anterior Deltoids, Triceps. Sets: 3-4, Reps: 6-10. Equipment: Barbell, Bench.', 3, 'strength', 'https://www.youtube.com/watch?v=rT7DgCr-3pg')
ON CONFLICT DO NOTHING;

INSERT INTO exercises (name, category, description, difficulty, type, video_url)
VALUES 
('Dumbbell Chest Fly', 'Chest', 'Isolation exercise for chest development. Lie on flat bench with dumbbells extended above chest, lower weights in arc motion until stretch is felt, then return. Primary: Pectoralis Major. Sets: 3, Reps: 10-12. Equipment: Dumbbells, Bench.', 2, 'strength', 'https://www.youtube.com/watch?v=eozdVDA78K0')
ON CONFLICT DO NOTHING;

INSERT INTO exercises (name, category, description, difficulty, type, video_url)
VALUES 
('Incline Dumbbell Press', 'Chest', 'Upper chest focus. Set bench to 30-45 degree incline, press dumbbells from shoulder level to full extension. Primary: Upper Pectoralis, Secondary: Anterior Deltoids. Sets: 3-4, Reps: 8-12. Equipment: Dumbbells, Incline Bench.', 2, 'strength', 'https://www.youtube.com/watch?v=8iPEnn-ltC8')
ON CONFLICT DO NOTHING;

INSERT INTO exercises (name, category, description, difficulty, type, video_url)
VALUES 
('Cable Crossover', 'Chest', 'Constant tension chest isolation. Stand between cable towers, bring handles together in front of chest with slight bend in elbows. Primary: Pectoralis Major (inner chest). Sets: 3, Reps: 12-15. Equipment: Cable Machine.', 2, 'strength', 'https://www.youtube.com/watch?v=taI4XduLpTk')
ON CONFLICT DO NOTHING;

-- BACK EXERCISES
INSERT INTO exercises (name, category, description, difficulty, type, video_url)
VALUES 
('Deadlift', 'Back', 'King of compound movements. Hip-hinge pattern lifting barbell from floor to standing position. Primary: Erector Spinae, Lats, Traps, Secondary: Glutes, Hamstrings, Forearms. Sets: 3-5, Reps: 5-8. Equipment: Barbell. Variation: Conventional, Sumo, Romanian.', 4, 'strength', 'https://www.youtube.com/watch?v=op9kVnSso6Q')
ON CONFLICT DO NOTHING;

INSERT INTO exercises (name, category, description, difficulty, type, video_url)
VALUES 
('Pull-Up', 'Back', 'Bodyweight vertical pull. Hang from bar with overhand grip, pull body until chin over bar. Primary: Latissimus Dorsi, Secondary: Biceps, Rhomboids. Sets: 3-4, Reps: 6-12 or to failure. Equipment: Pull-up Bar. Variation: Wide grip, Close grip, Neutral grip.', 3, 'strength', 'https://www.youtube.com/watch?v=eGo4IYlbE5g')
ON CONFLICT DO NOTHING;

INSERT INTO exercises (name, category, description, difficulty, type, video_url)
VALUES 
('Bent-Over Barbell Row', 'Back', 'Horizontal pull for back thickness. Hinge at hips with barbell, pull to lower chest/upper abdomen. Primary: Lats, Rhomboids, Traps, Secondary: Biceps. Sets: 3-4, Reps: 8-12. Equipment: Barbell.', 3, 'strength', 'https://www.youtube.com/watch?v=9efgcAjQe7E')
ON CONFLICT DO NOTHING;

INSERT INTO exercises (name, category, description, difficulty, type, video_url)
VALUES 
('Seated Cable Row', 'Back', 'Controlled horizontal pull. Sit at cable station, pull handle to torso keeping back straight. Primary: Mid-back, Lats, Rhomboids. Sets: 3, Reps: 10-12. Equipment: Cable Machine, V-Bar or Wide Handle.', 2, 'strength', 'https://www.youtube.com/watch?v=GZbfZ033f74')
ON CONFLICT DO NOTHING;

INSERT INTO exercises (name, category, description, difficulty, type, video_url)
VALUES 
('Lat Pulldown', 'Back', 'Machine-based vertical pull. Sit at lat pulldown station, pull bar to upper chest. Primary: Latissimus Dorsi, Secondary: Biceps, Rear Delts. Sets: 3, Reps: 10-12. Equipment: Lat Pulldown Machine.', 2, 'strength', 'https://www.youtube.com/watch?v=CAwf7n6Luuc')
ON CONFLICT DO NOTHING;

-- SHOULDER EXERCISES
INSERT INTO exercises (name, category, description, difficulty, type, video_url)
VALUES 
('Overhead Press', 'Shoulders', 'Compound overhead movement. Press barbell from shoulder height to overhead lockout. Primary: Anterior/Lateral Deltoids, Secondary: Triceps, Upper Chest. Sets: 3-4, Reps: 6-10. Equipment: Barbell. Variation: Standing, Seated, Dumbbell.', 3, 'strength', 'https://www.youtube.com/watch?v=2yjwXTZQDDI')
ON CONFLICT DO NOTHING;

INSERT INTO exercises (name, category, description, difficulty, type, video_url)
VALUES 
('Lateral Raise', 'Shoulders', 'Side delt isolation. Stand with dumbbells at sides, raise arms laterally to shoulder height. Primary: Lateral Deltoids. Sets: 3-4, Reps: 12-15. Equipment: Dumbbells.', 2, 'strength', 'https://www.youtube.com/watch?v=3VcKaXpzqRo')
ON CONFLICT DO NOTHING;

INSERT INTO exercises (name, category, description, difficulty, type, video_url)
VALUES 
('Face Pull', 'Shoulders', 'Rear delt and upper back. Pull rope attachment to face level with elbows high. Primary: Rear Deltoids, Rhomboids, Traps. Sets: 3, Reps: 15-20. Equipment: Cable Machine, Rope Attachment.', 2, 'strength', 'https://www.youtube.com/watch?v=rep-qVOkqgk')
ON CONFLICT DO NOTHING;

-- LEG EXERCISES
INSERT INTO exercises (name, category, description, difficulty, type, video_url)
VALUES 
('Barbell Back Squat', 'Legs', 'King of leg exercises. Bar on upper back, descend until thighs parallel or below, drive through heels to stand. Primary: Quadriceps, Glutes, Secondary: Hamstrings, Core. Sets: 3-5, Reps: 6-10. Equipment: Barbell, Squat Rack.', 4, 'strength', 'https://www.youtube.com/watch?v=ultWZbUMPL8')
ON CONFLICT DO NOTHING;

INSERT INTO exercises (name, category, description, difficulty, type, video_url)
VALUES 
('Bulgarian Split Squat', 'Legs', 'Single-leg strength and balance. Rear foot elevated on bench, lower into lunge until front thigh parallel. Primary: Quadriceps, Glutes, Secondary: Hamstrings. Sets: 3 per leg, Reps: 10-12. Equipment: Dumbbells, Bench.', 3, 'strength', 'https://www.youtube.com/watch?v=2C-uNgKwPLE')
ON CONFLICT DO NOTHING;

INSERT INTO exercises (name, category, description, difficulty, type, video_url)
VALUES 
('Romanian Deadlift', 'Legs', 'Hamstring and glute builder. Hip hinge with slight knee bend, lower barbell to mid-shin maintaining flat back. Primary: Hamstrings, Glutes, Secondary: Lower Back. Sets: 3-4, Reps: 8-12. Equipment: Barbell.', 3, 'strength', 'https://www.youtube.com/watch?v=fEciS7OWXR8')
ON CONFLICT DO NOTHING;

INSERT INTO exercises (name, category, description, difficulty, type, video_url)
VALUES 
('Leg Press', 'Legs', 'Machine-based compound leg movement. Push platform away with feet shoulder-width, lower until knees at 90 degrees. Primary: Quadriceps, Glutes, Secondary: Hamstrings. Sets: 3-4, Reps: 10-15. Equipment: Leg Press Machine.', 2, 'strength', 'https://www.youtube.com/watch?v=IZxyjW7MPJQ')
ON CONFLICT DO NOTHING;

INSERT INTO exercises (name, category, description, difficulty, type, video_url)
VALUES 
('Walking Lunge', 'Legs', 'Dynamic leg exercise. Step forward into lunge, alternate legs while moving forward. Primary: Quadriceps, Glutes, Secondary: Hamstrings, Calves. Sets: 3, Reps: 10-12 per leg. Equipment: Dumbbells (optional).', 2, 'strength', 'https://www.youtube.com/watch?v=L8fvypPrzzs')
ON CONFLICT DO NOTHING;

-- ARM EXERCISES
INSERT INTO exercises (name, category, description, difficulty, type, video_url)
VALUES 
('Barbell Curl', 'Arms', 'Classic bicep builder. Stand with barbell, curl to shoulders keeping elbows stationary. Primary: Biceps Brachii. Sets: 3-4, Reps: 8-12. Equipment: Barbell (EZ-bar or straight).', 2, 'strength', 'https://www.youtube.com/watch?v=kwG2ipFRgfo')
ON CONFLICT DO NOTHING;

INSERT INTO exercises (name, category, description, difficulty, type, video_url)
VALUES 
('Tricep Dip', 'Arms', 'Compound tricep exercise. Support bodyweight on parallel bars, lower until upper arms parallel, push back up. Primary: Triceps, Secondary: Chest, Shoulders. Sets: 3-4, Reps: 8-12. Equipment: Dip Station or Parallel Bars.', 3, 'strength', 'https://www.youtube.com/watch?v=2z8JmcrW-As')
ON CONFLICT DO NOTHING;

INSERT INTO exercises (name, category, description, difficulty, type, video_url)
VALUES 
('Hammer Curl', 'Arms', 'Neutral grip curl for brachialis. Hold dumbbells with thumbs up, curl to shoulders. Primary: Brachialis, Biceps, Forearms. Sets: 3, Reps: 10-12. Equipment: Dumbbells.', 2, 'strength', 'https://www.youtube.com/watch?v=zC3nLlEvin4')
ON CONFLICT DO NOTHING;

INSERT INTO exercises (name, category, description, difficulty, type, video_url)
VALUES 
('Overhead Tricep Extension', 'Arms', 'Tricep isolation. Hold dumbbell overhead with both hands, lower behind head, extend back up. Primary: Triceps (long head). Sets: 3, Reps: 10-12. Equipment: Dumbbell.', 2, 'strength', 'https://www.youtube.com/watch?v=YbX7Wd8jQ-Q')
ON CONFLICT DO NOTHING;

-- CORE EXERCISES
INSERT INTO exercises (name, category, description, difficulty, type, video_url)
VALUES 
('Plank', 'Core', 'Isometric core stability. Hold push-up position on forearms maintaining straight body line. Primary: Rectus Abdominis, Transverse Abdominis. Sets: 3, Time: 30-60 seconds. Equipment: None (Bodyweight).', 1, 'strength', 'https://www.youtube.com/watch?v=ASdvN_XEl_c')
ON CONFLICT DO NOTHING;

INSERT INTO exercises (name, category, description, difficulty, type, video_url)
VALUES 
('Russian Twist', 'Core', 'Rotational core exercise. Sit with feet elevated, rotate torso side to side touching floor. Primary: Obliques, Rectus Abdominis. Sets: 3, Reps: 20-30 total. Equipment: Medicine Ball (optional).', 2, 'strength', 'https://www.youtube.com/watch?v=wkD8rjkodUI')
ON CONFLICT DO NOTHING;

INSERT INTO exercises (name, category, description, difficulty, type, video_url)
VALUES 
('Hanging Leg Raise', 'Core', 'Advanced ab exercise. Hang from bar, raise legs to horizontal (or higher). Primary: Lower Abs, Hip Flexors. Sets: 3, Reps: 10-15. Equipment: Pull-up Bar.', 3, 'strength', 'https://www.youtube.com/watch?v=Pr1ieGZ5atk')
ON CONFLICT DO NOTHING;

INSERT INTO exercises (name, category, description, difficulty, type, video_url)
VALUES 
('Cable Crunch', 'Core', 'Weighted ab crunch. Kneel at cable station, crunch down bringing elbows to knees. Primary: Rectus Abdominis. Sets: 3, Reps: 15-20. Equipment: Cable Machine, Rope Attachment.', 2, 'strength', 'https://www.youtube.com/watch?v=sIZUOsFjBkM')
ON CONFLICT DO NOTHING;

-- CARDIO/CONDITIONING
INSERT INTO exercises (name, category, description, difficulty, type, video_url)
VALUES 
('Burpee', 'Conditioning', 'Full-body conditioning movement. Drop to push-up, jump feet in, explosive jump up, repeat. Primary: Full Body. Sets: 3-4, Reps: 10-15 or time-based. Equipment: None (Bodyweight). Variation: Add push-up, add tuck jump.', 3, 'cardio', 'https://www.youtube.com/watch?v=TU8QYVW0gDU')
ON CONFLICT DO NOTHING;

INSERT INTO exercises (name, category, description, difficulty, type, video_url)
VALUES 
('Jump Rope', 'Cardio', 'Low-impact cardio and coordination. Continuous rope jumping at various speeds. Primary: Cardio, Calves, Shoulders. Sets: 3-5, Time: 1-3 minutes. Equipment: Jump Rope.', 2, 'cardio', 'https://www.youtube.com/watch?v=hCHAXfnVzuE')
ON CONFLICT DO NOTHING;

INSERT INTO exercises (name, category, description, difficulty, type, video_url)
VALUES 
('Battle Ropes', 'Conditioning', 'Explosive power endurance. Create waves with heavy ropes in alternating or simultaneous patterns. Primary: Shoulders, Core, Cardio. Sets: 3-4, Time: 20-30 seconds. Equipment: Battle Ropes.', 3, 'cardio', 'https://www.youtube.com/watch?v=YxtwA7XRK_g')
ON CONFLICT DO NOTHING;

INSERT INTO exercises (name, category, description, difficulty, type, video_url)
VALUES 
('Box Jump', 'Plyometric', 'Explosive lower body power. Jump onto elevated platform from standing position. Primary: Quadriceps, Glutes, Power Development. Sets: 3-4, Reps: 6-10. Equipment: Plyo Box. Variation: Various heights 20-30 inches.', 3, 'cardio', 'https://www.youtube.com/watch?v=NBY9-kTuHEk')
ON CONFLICT DO NOTHING;

-- FUNCTIONAL/OLYMPIC
INSERT INTO exercises (name, category, description, difficulty, type, video_url)
VALUES 
('Kettlebell Swing', 'Functional', 'Hip-hinge power movement. Swing kettlebell from between legs to chest height using hip drive. Primary: Glutes, Hamstrings, Core, Secondary: Shoulders. Sets: 3-4, Reps: 15-20. Equipment: Kettlebell.', 3, 'strength', 'https://www.youtube.com/watch?v=YSxHifyI6s8')
ON CONFLICT DO NOTHING;

INSERT INTO exercises (name, category, description, difficulty, type, video_url)
VALUES 
('Turkish Get-Up', 'Functional', 'Complex full-body movement. Rise from lying to standing while holding weight overhead. Primary: Full Body, Stability, Mobility. Sets: 2-3 per side, Reps: 3-5. Equipment: Kettlebell or Dumbbell.', 4, 'strength', 'https://www.youtube.com/watch?v=0bWRPC49-KI')
ON CONFLICT DO NOTHING;

-- Update existing basic exercises if they exist
UPDATE exercises SET 
    description = 'Bodyweight upper body push. Start in plank, lower chest to ground, push back up. Primary: Pectoralis Major, Triceps, Anterior Deltoids. Sets: 3-4, Reps: 10-20. Equipment: None (Bodyweight). Variation: Wide, Diamond, Decline, Incline.',
    difficulty = 2,
    type = 'strength',
    video_url = 'https://www.youtube.com/watch?v=IODxDxX7oi4'
WHERE name = 'Push-up' AND (description IS NULL OR description = 'Upper body push movement.');

UPDATE exercises SET 
    description = 'Fundamental bodyweight squat. Feet shoulder-width, descend until thighs parallel, drive through heels. Primary: Quadriceps, Glutes, Secondary: Hamstrings. Sets: 3-4, Reps: 15-20. Equipment: None (Bodyweight).',
    difficulty = 1,
    type = 'strength',
    video_url = 'https://www.youtube.com/watch?v=aclHkVaku9U'
WHERE name = 'Bodyweight Squat' AND (description IS NULL OR description = 'Lower body squat pattern.');

UPDATE exercises SET 
    description = 'Steady-state low-intensity cardio. Continuous walking at moderate pace. Primary: Cardio, General Health. Time: 20-60 minutes. Equipment: None.',
    difficulty = 1,
    type = 'cardio',
    video_url = NULL
WHERE name = 'Walk' AND (description IS NULL OR description = 'Easy steady movement.');

UPDATE exercises SET 
    description = 'Moderate-intensity cardio. Continuous running at comfortable pace. Primary: Cardio, Leg Endurance. Time: 20-45 minutes. Equipment: None.',
    difficulty = 2,
    type = 'cardio',
    video_url = 'https://www.youtube.com/watch?v=kVX1GmXW5_s'
WHERE name = 'Run' AND (description IS NULL OR description = 'Moderate cardio session.');

UPDATE exercises SET 
    description = 'Horizontal pull for upper back and arms. Hinge at hips, pull dumbbell to hip keeping elbow close. Primary: Lats, Rhomboids, Secondary: Biceps, Rear Delts. Sets: 3-4, Reps: 10-12 per arm. Equipment: Dumbbell, Bench (optional).',
    difficulty = 2,
    type = 'strength',
    video_url = 'https://www.youtube.com/watch?v=roCP6wCXPqo'
WHERE name = 'Dumbbell Row' AND (description IS NULL OR description = 'Upper body pull movement.');
