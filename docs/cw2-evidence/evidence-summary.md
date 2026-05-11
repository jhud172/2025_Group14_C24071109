# CM6222 CW2 Evidence Summary

## Target

- Page: `/calendar/day/2026-01-20`
- Demo login: `demo`
- Implemented improvement: `idx_calendar_tasks_user_date_time` on `calendar_tasks (user_id, date, time)`

## Evidence Collected

- Before screenshots: captured in `screenshots/`
- After screenshots: captured in `screenshots/`
- Before explain plan: `explain-plans/before-explain-plan.txt`
- After explain plan: `explain-plans/after-explain-plan.txt`
- JMeter plan: `jmeter/calendar-day-test-plan.md`
- Build verification: `build-verification.txt`

## Playwright Check Results

- BEFORE login page loaded: true
- BEFORE login succeeded: true
- BEFORE calendar day loaded: true
- BEFORE exact seeded task text found: false
- AFTER login page loaded: true
- AFTER login succeeded: true
- AFTER calendar day loaded: true
- AFTER exact seeded task text found: false

The screenshots should be used as the visual evidence. The exact seeded task text check was kept as recorded by the script and was not edited after the run.

## Build Verification

Command run:

```powershell
.\gradlew clean build
```

Result: failed during tests.

Recorded result: `236 tests completed, 53 failed`.

This matches the previously known failing test count, so it appears unrelated to the CW2 evidence files and index restoration.

## Notes For Report

- Use the before/after screenshots to prove the same page was tested.
- Use the explain plans to discuss whether the query planner used the composite index.
- In the H2 explain output, the planner still selected the existing `calendar_tasks.user_id` foreign-key index path on this small dataset. This is still useful evidence because it shows the optimiser choice rather than assumed behaviour.
- Use JMeter and profiler results only after collecting real measurements.

## Stage 4 JMeter Status

Historical note: this section records the environment state before local JMeter was configured. It is superseded by the Stage 4B section below.

- JMeter installed on PATH: no
- JMeter command-line run completed: no
- JMeter `.jmx` created: `jmeter/calendar-day-load-test.jmx`
- Manual run guide created: `jmeter/calendar-day-jmeter-run-guide.md`
- Blank results template created: `jmeter/results-summary-template.md`
- Timing CSV files created: no
- Before/after timing values collected: no
- Index restored after Stage 4: yes

Stage 4 prepared the load test files, but no timing results were collected at that point because the `jmeter` command was not available in that environment.

## Stage 4B JMeter Evidence

- JMeter version: 5.6.3
- BEFORE tests ran: yes
- AFTER tests ran: yes
- Result CSV files:
  - `jmeter/results/before-1-users.csv`
  - `jmeter/results/before-5-users.csv`
  - `jmeter/results/before-10-users.csv`
  - `jmeter/results/before-25-users.csv`
  - `jmeter/results/after-1-users.csv`
  - `jmeter/results/after-5-users.csv`
  - `jmeter/results/after-10-users.csv`
  - `jmeter/results/after-25-users.csv`
- Summary: the AFTER results were slightly slower overall on this local H2 run, with differences that are small enough to be affected by warm-up and local variability.
- Limitations: small seeded dataset, H2 optimiser choice, JVM warm-up, and local authentication/session/template overhead all make the timing delta noisy.
- Index restored: yes, the composite `idx_calendar_tasks_user_date_time` statement is present again in both schema files.
- Final comparison uses the successful reruns captured after the app was fully live.

## Stage 5 - IntelliJ profiler evidence

A real IntelliJ profiler screenshot was captured while running a 25-user JMeter load test against `/calendar/day/2026-01-20`.

Profiler screenshot:

`docs/cw2-evidence/profiler/intellij-profiler-calendar-25-users.png`

The timeline shows multiple `http-nio-8080-exec-*` request threads active during the test, confirming concurrent request handling during profiling. The profiler evidence supports the conclusion that the request cost was not dominated by the new calendar task index alone.

The supporting JMeter profiler run produced 375 samples in roughly 5 seconds, with an average response time of around 48-49 ms and throughput around 69-70 requests per second. A small number of `POST Login` requests returned HTTP 500 at the beginning of the run, so this evidence should be interpreted as profiling support rather than the main before/after timing comparison.
