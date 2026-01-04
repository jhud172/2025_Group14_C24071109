DELETE FROM favourites;

INSERT INTO favourites (id, user_id, exercise_id, custom_exercise_id)
VALUES
    (1, 1, 1, NULL),
    (2, 1, 3, NULL),
    (3, 2, NULL, 1);