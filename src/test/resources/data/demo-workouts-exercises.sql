-- Disabled: consolidated into 00-data.sql to avoid relying on non-deterministic
-- execution ordering from `classpath:data/*.sql`.

-- No-op statement so Spring SQL init does not treat this script as empty.
SELECT 1;
