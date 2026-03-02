-- Add activity_type column to calendar_tasks for multi-sport activity tracking
ALTER TABLE calendar_tasks
    ADD COLUMN IF NOT EXISTS activity_type VARCHAR(20) NULL;

-- Add activity_type column to task_templates for template persistence
ALTER TABLE task_templates
    ADD COLUMN IF NOT EXISTS activity_type VARCHAR(20) NULL;
