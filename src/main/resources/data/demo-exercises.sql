INSERT INTO exercises (id, name, category, description, video_url, difficulty, type, image_url)
VALUES
    (1, 'Squat', 'Strength', 'Strengthen legs and core', 'https://www.youtube.com/embed/xqvCmoLULNY', 3, 'strength', NULL),
    (2, 'Hamstring Stretch', 'Flexibility', 'Stretches the hamstring muscles', 'https://www.youtube.com/embed/RxEd4pN7CDs', 1, 'stretch', NULL),
    (3, 'Ankle Mobility Lean', 'Mobility', 'Improves dorsiflexion', NULL, 1, 'mobility', NULL),
    (4, 'Wall Sit', 'Strength', 'Isometric leg strength hold', 'https://www.youtube.com/embed/cWTZ8Am1Ee0', 2, 'strength', NULL),
    (5, 'March in Place', 'Cardio', 'Low-impact warm-up cardio', 'https://www.youtube.com/embed/9wl_AiNhYP0', 1, 'cardio', NULL);

-- Tags and exercises relation

insert into exercises_tags(exercise_id, tag_id)
values ((select id from exercises where name = 'Squat'),
        (select id from tags where name = 'Free'));
insert into exercises_tags(exercise_id, tag_id)
values ((select id from exercises where name = 'Squat'),
        (select id from tags where name = 'Barbell'));
insert into exercises_tags(exercise_id, tag_id)
values ((select id from exercises where name = 'Squat'),
        (select id from tags where name = 'Home'));
insert into exercises_tags(exercise_id, tag_id)
values ((select id from exercises where name = 'Hamstring Stretch'),
    (select id from tags where name = 'Free'));
insert into exercises_tags(exercise_id, tag_id)
values ((select id from exercises where name = 'Hamstring Stretch'),
        (select id from tags where name = 'Home'));
insert into exercises_tags(exercise_id, tag_id)
values ((select id from exercises where name = 'Hamstring Stretch'),
        (select id from tags where name = 'Solo'));