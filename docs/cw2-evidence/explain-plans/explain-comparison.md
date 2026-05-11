# Explain Plan Comparison

## Query

```sql
EXPLAIN
SELECT id, user_id, date, title, time, completed
FROM calendar_tasks
WHERE user_id = (SELECT id FROM users WHERE username = 'demo')
  AND date = DATE '2026-01-20'
ORDER BY time;
```

## Before Index

See `before-explain-plan.txt`.

Observed H2 plan: H2 used the existing foreign-key index path for `calendar_tasks.user_id` and the username unique index for the subquery. It did not show the new composite index because it was temporarily removed for this run.

## After Index

See `after-explain-plan.txt`.

Observed H2 plan: H2 still selected the existing foreign-key index path for `calendar_tasks.user_id` and the username unique index for the subquery. The composite index was restored in both schema files, but H2 did not choose to display it in the plan for this small seeded dataset.

Index location after restoration is recorded in `after-index-location.txt` (with the baseline in `before-index-location.txt`).

## What To Compare

- Whether the plan uses a table scan or an index
- Whether `idx_calendar_tasks_user_date_time` appears after the index is restored
- Whether the plan filters by `user_id` and `date`
- Whether sorting by `time` is handled by the index or by a separate sort step
- Whether H2 still scans because the demo dataset is small
