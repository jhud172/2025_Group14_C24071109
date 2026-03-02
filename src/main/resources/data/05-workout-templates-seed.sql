-- Seed built-in (global) workout UI templates (idempotent)

INSERT INTO workout_ui_templates (user_id, name, layout_type, config_json, is_default, created_at, updated_at)
SELECT NULL, 'Flow', 'FLOW',
       '{"layout":"flow","theme":"default","transition":"slide","density":"comfortable","progress":true,"restTimer":true,"components":["progress","timer","exerciseCard","setEntry","restTimer","summary"]}',
       TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM workout_ui_templates WHERE user_id IS NULL AND layout_type = 'FLOW');

INSERT INTO workout_ui_templates (user_id, name, layout_type, config_json, is_default, created_at, updated_at)
SELECT NULL, 'Professional', 'PROFESSIONAL',
       '{"layout":"professional","theme":"light-clean","transition":"none","density":"compact","progress":true,"restTimer":true,"components":["timer","exerciseCard","setEntry","summary"]}',
       FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM workout_ui_templates WHERE user_id IS NULL AND layout_type = 'PROFESSIONAL');

INSERT INTO workout_ui_templates (user_id, name, layout_type, config_json, is_default, created_at, updated_at)
SELECT NULL, 'Plain', 'PLAIN',
       '{"layout":"plain","theme":"light-clean","transition":"none","density":"compact","progress":false,"restTimer":false,"components":["exerciseCard","setEntry"]}',
       FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM workout_ui_templates WHERE user_id IS NULL AND layout_type = 'PLAIN');

INSERT INTO workout_ui_templates (user_id, name, layout_type, config_json, is_default, created_at, updated_at)
SELECT NULL, 'Futuristic', 'FUTURISTIC_FLOW',
       '{"layout":"futuristic_flow","theme":"futuristic","transition":"slide","density":"comfortable","progress":true,"restTimer":true,"components":["progress","timer","exerciseCard","setEntry","restTimer","summary"]}',
       FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM workout_ui_templates WHERE user_id IS NULL AND layout_type = 'FUTURISTIC_FLOW');
