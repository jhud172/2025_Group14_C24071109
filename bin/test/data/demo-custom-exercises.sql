DELETE FROM custom_exercises;

INSERT INTO custom_exercises (id, user_id, name, category, description, video_url, type, image_url)
VALUES
    (1, 1, 'Resistance Band Row', 'Strength', 'Pull with a band', NULL, 'strength', NULL),
    (2, 1, 'Chair Balance Practice', 'Balance', 'Light balance exercise', NULL, 'balance', NULL);