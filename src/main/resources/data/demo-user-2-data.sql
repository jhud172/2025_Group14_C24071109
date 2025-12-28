delete
from health_records;

delete
from exercise_log;

-- Demo Health Record Data

insert into health_records(user_id, baseline_date, systolic_blood_pressure, diastolic_blood_pressure, cholesterol,
                           weight_kg, height_cm, bmi, waist_height_ratio, waist_cm, activity_level)
values (2, '2025-07-04 10:00:00', 150, 100, 7.2, 105, 180, 32.41, 0.64, 115, 'Sedentary'),
       (2, '2025-08-05 10:00:00', 145, 92, 7.15, 103, 180, 31.79, 0.62, 112, 'Lightly Active'),
       (2, '2025-09-06 10:00:00', 135, 88, 6.90, 99, 180, 30.56, 0.60, 108, 'Moderate'),
       (2, '2025-10-03 10:00:00', 128, 84, 7.10, 95, 180, 29.32, 0.57, 102, 'Moderate'),
       (2, '2025-11-02 10:00:00', 122, 80, 6.85, 92, 180, 28.40, 0.54, 98, 'High'),
       (2, '2025-12-01 10:00:00', 118, 75, 6.50, 90, 180, 27.78, 0.51, 92, 'High');

-- Demo Exercise Log Data
insert into exercise_log(user_id, date, mood_before, mood_after, confidence, duration_minutes)
values (2, '2025-07-04', 2, 4, 1, 15),
       (2, '2025-08-04', 2, 3, 2, 20),
       (2, '2025-09-04', 2, 4, 3, 30),
       (2, '2025-10-04', 2, 1, 2, 45),
       (2, '2025-11-04', 3, 4, 3, 50),
       (2, '2025-12-04', 1, 4, 4, 60);

