# PostgreSQL migrations

`V1__baseline_schema.sql` is the first Flyway baseline for a fresh PostgreSQL
database. It intentionally contains schema only and creates no user, role,
subscription or demonstration records.

For every later database change:

1. add a new `V{number}__short_description.sql` file;
2. never edit a migration that has run in a shared environment;
3. make the forward migration safe for the current application version;
4. add focused repository or integration coverage;
5. take and verify a staging backup before applying a destructive change.

Flyway Community migrations are forward-only. Repair a failed release with a
new forward migration. Restore a backup into a separate empty database when a
data rollback is required; do not attempt an in-place downgrade.
