# Calendar Day JMeter Run Guide

Target page: `/calendar/day/2026-01-20`

Base URL: `http://localhost:8080`

Demo login: `demo` / `Demo123!`

Historical note: JMeter was not available on the PATH during the initial Stage 4 setup. This was superseded in Stage 4B after local JMeter was configured.

## Start The App

From the project root:

```powershell
.\gradlew bootRun
```

Wait until the logs show Tomcat running on port `8080`, then check `http://localhost:8080/login`.

## BEFORE Run

BEFORE means the composite index is temporarily removed.

1. Save copies of:
   - `src/main/resources/schema.sql`
   - `src/test/resources/schema.sql`
2. Temporarily remove only:

```sql
CREATE INDEX IF NOT EXISTS idx_calendar_tasks_user_date_time
    ON calendar_tasks (user_id, date, time);
```

3. Start the app so H2 recreates the schema without the index.
4. Run JMeter for each load level.
5. Stop the app.

Planned output files:

- `docs/cw2-evidence/jmeter/results/before-1-users.csv`
- `docs/cw2-evidence/jmeter/results/before-5-users.csv`
- `docs/cw2-evidence/jmeter/results/before-10-users.csv`
- `docs/cw2-evidence/jmeter/results/before-25-users.csv`

Example command:

```powershell
jmeter -n -t docs/cw2-evidence/jmeter/calendar-day-load-test.jmx -JTHREADS=1 -JLOOPS=5 -l docs/cw2-evidence/jmeter/results/before-1-users.csv
```

Repeat with `-JTHREADS=5`, `-JTHREADS=10`, and `-JTHREADS=25`.

## AFTER Run

AFTER means the composite index is present.

1. Restore the index in both schema files:

```sql
CREATE INDEX IF NOT EXISTS idx_calendar_tasks_user_date_time
    ON calendar_tasks (user_id, date, time);
```

2. Confirm exactly one copy exists in each schema file.
3. Start the app so H2 recreates the schema with the index.
4. Run JMeter for each load level.
5. Stop the app.

Planned output files:

- `docs/cw2-evidence/jmeter/results/after-1-users.csv`
- `docs/cw2-evidence/jmeter/results/after-5-users.csv`
- `docs/cw2-evidence/jmeter/results/after-10-users.csv`
- `docs/cw2-evidence/jmeter/results/after-25-users.csv`

Example command:

```powershell
jmeter -n -t docs/cw2-evidence/jmeter/calendar-day-load-test.jmx -JTHREADS=1 -JLOOPS=5 -l docs/cw2-evidence/jmeter/results/after-1-users.csv
```

Repeat with `-JTHREADS=5`, `-JTHREADS=10`, and `-JTHREADS=25`.

## Metrics To Copy Into The Report

For each CSV/result:

- sample count
- average response time
- median response time
- 95th percentile
- throughput
- error percentage
- slowest request

Use only real values from JMeter. Do not estimate missing values.

## Notes

- Use an HTTP Cookie Manager so the login session is kept.
- The test plan extracts the Spring Security CSRF token from the login page.
- If the login request fails, check the CSRF extractor and the login response in JMeter's View Results Tree.
- H2 and a small local dataset may make before/after differences small.
