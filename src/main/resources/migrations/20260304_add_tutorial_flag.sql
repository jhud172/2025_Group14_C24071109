-- Add has_seen_tutorial flag to users table for first-login tutorial system
ALTER TABLE users ADD COLUMN IF NOT EXISTS has_seen_tutorial BOOLEAN NOT NULL DEFAULT FALSE;
