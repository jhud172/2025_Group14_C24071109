delete
from workouts_exercises;
delete
from workouts;
delete from health_records;
delete
from selected_preferences;
delete
from preference_tag;
delete
from physical_condition_tag;
delete
from exercises_tags;
delete
from users_roles;
delete
from user_preferences;
delete
from roles;
delete
from preferences;
delete
from tags;
delete
from physical_conditions;
delete
from users;

-- Physical condition data
insert into physical_conditions(name)
values ('Arthritis');
insert into physical_conditions(name)
values ('Knee pain');
insert into physical_conditions(name)
values ('Back pain');
insert into physical_conditions(name)
values ('Chronic pain');
insert into physical_conditions(name)
values ('Diabetes');
insert into physical_conditions(name)
values ('Asthma');

-- Test user
INSERT INTO users (email,first_name, last_name, username, password, enabled, subscription_status)
VALUES ('Jane@gmail.com','Jane', 'Doe', 'user', '$2a$12$yOTznIO3eJBYmFckkJM.Xuu2qy59Rg4CQUhIrGpHzseNGj5KI5MSO', true, false);
INSERT INTO users (email,first_name, last_name, username, password, enabled, subscription_status)
VALUES ('John@gmail.com','John', 'Doe', 'user2', '$2a$12$yOTznIO3eJBYmFckkJM.Xuu2qy59Rg4CQUhIrGpHzseNGj5KI5MSO', true, false);

-- Roles
insert into roles(role_id, name)
values (1, 'USER');

-- Links user to a role
insert into users_roles (username, role_id)
values ('user', 1);
insert into users_roles (username, role_id)
values ('user2', 1);

-- Preference data
insert into preferences(category, description)
values ('Finance', 'Free');
insert into preferences(category, description)
values ('Finance', 'Paid');
insert into preferences(category, description)
values ('Location', 'Home');
insert into preferences(category, description)
values ('Location', 'Outside');
insert into preferences(category, description)
values ('Location', 'Gym');
insert into preferences(category, description)
values ('Social', 'Solo');
insert into preferences(category, description)
values ('Social', 'Group');
insert into preferences(category, description)
values ('Equipment', 'Dumbbells');
insert into preferences(category, description)
values ('Equipment', 'Weights');
insert into preferences(category, description)
values ('Equipment', 'Kettlebells');
insert into preferences(category, description)
values ('Equipment', 'Barbell');

-- Tags
insert into tags(name, category)
values ('High Intensity', 'Intensity');
insert into tags(name, category)
values ('Barbell', 'Equipment');
insert into tags(name, category)
values ('Free', 'Finance');
insert into tags(name, category)
values ('Paid', 'Finance');
insert into tags(name, category)
values ('Gym', 'Location');
insert into tags(name, category)
values ('Home', 'Location');
insert into tags(name, category)
values ('Outside', 'Location');
insert into tags(name, category)
values ('Solo', 'Social');
insert into tags(name, category)
values ('Group', 'Social');
insert into tags(name, category)
values ('Dumbbells', 'Equipment');
insert into tags(name, category)
values ('Weights', 'Equipment');
insert into tags(name, category)
values ('Kettlebells', 'Equipment');
insert into tags(name, category)
values ('High impact', 'Impact');


-- Tags and preference relation
insert into preference_tag(preference_id, tag_id)
values ((select id from preferences where description = 'Free'),
        (select id from tags where name = 'Free'));
insert into preference_tag(preference_id, tag_id)
values ((select id from preferences where description = 'Paid'),
        (select id from tags where name = 'Paid'));
insert into preference_tag(preference_id, tag_id)
values ((select id from preferences where description = 'Home'),
        (select id from tags where name = 'Home'));
insert into preference_tag(preference_id, tag_id)
values ((select id from preferences where description = 'Outside'),
        (select id from tags where name = 'Outside'));
insert into preference_tag(preference_id, tag_id)
values ((select id from preferences where description = 'Gym'),
        (select id from tags where name = 'Gym'));
insert into preference_tag(preference_id, tag_id)
values ((select id from preferences where description = 'Solo'),
        (select id from tags where name = 'Solo'));
insert into preference_tag(preference_id, tag_id)
values ((select id from preferences where description = 'Barbell'),
        (select id from tags where name = 'Barbell'));

-- Tags and physical condition relation
insert into physical_condition_tag(physical_condition_id, tag_id)
values ((select id from physical_conditions where name = 'Asthma'),
        (select id from tags where name = 'High Intensity'));
insert into physical_condition_tag(physical_condition_id, tag_id)
values ((select id from physical_conditions where name = 'Arthritis'),
        (select id from tags where name = 'High impact'));


-- Set up user preferences
insert into user_preferences(id, user_id)
values (4, 1);

-- Demo Preference Data
insert into user_preferences(user_id)
values (2);
insert into selected_preferences(user_preference_id, preference_id)
values ((select id from user_preferences where user_id = 2),
        (select id from preferences where description = 'Gym')),
       ((select id from user_preferences where user_id = 2),
        (select id from preferences where description = 'Solo')),
       ((select id from user_preferences where user_id = 2),
        (select id from preferences where description = 'Weights')),
       ((select id from user_preferences where user_id = 2),
        (select id from preferences where description = 'Free'));

