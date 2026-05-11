# IntelliJ Profiler Observations

## Run details

- Date/time:
- App command:
- Target route: `/calendar/day/2026-01-20`
- Load used:
- JMeter command used during profiling:
- Profiler mode used:

## Scenario profiled

- Route: `/calendar/day/2026-01-20`
- User load:
- Duration:
- Whether login succeeded:
- Whether calendar page succeeded:

## Methods/classes to check

- CalendarController.dayView
- CalendarTaskServiceImpl.getTasks
- CalendarTaskRepository.findByUserAndDateOrderByTime
- Spring Security/session handling
- Thymeleaf rendering
- H2/JPA/database access

## Observations

- Main hotspot:
- Secondary hotspot:
- Database access impact:
- Template rendering impact:
- Authentication/session impact:
- Memory/GC notes:
- CPU notes:

## Screenshot evidence

- Screenshot path:
- What the screenshot shows:

## Interpretation for report

- What the profiler suggests:
- How this relates to JMeter:
- How this relates to explain plans:
- Limitations:

## Real profiler observation

Profiler run date/time: 2026-05-11 13:55  
Target route: `/calendar/day/2026-01-20`  
Load used: 25-user JMeter run  
JMeter result file: `docs/cw2-evidence/jmeter/results/profiler-25-users.csv`  
Profiler screenshot: `docs/cw2-evidence/profiler/intellij-profiler-calendar-25-users.png`

### JMeter profiler-support run

The profiler was active while a 25-user JMeter load test requested the calendar day page. The run produced 375 samples in roughly 5 seconds, with an average response time of around 48-49 ms and throughput around 69-70 requests per second. A small number of `POST Login` requests returned HTTP 500 at the beginning of the run, but the main calendar page requests completed successfully during the live load.

### Profiler observation

The IntelliJ profiler timeline showed multiple `http-nio-8080-exec-*` request threads active during the JMeter run. This confirms that the application was handling concurrent HTTP requests while the profiler was recording.

The visible activity was spread across request-handling threads rather than showing one clear database-only bottleneck. This supports the JMeter and explain-plan results: the composite index on `calendar_tasks(user_id, date, time)` matches the query pattern, but the measured local response time was not clearly improved by the index.

### Interpretation for the report

The profiler evidence suggests that the calendar day request cost was influenced by general web request handling, Spring Security/session processing, Thymeleaf rendering, JVM warm-up, and local machine variability, not only by the database query. This helps explain why the AFTER JMeter timings were broadly similar to slightly slower despite the index being structurally appropriate.

## How to collect profiler evidence manually

1. Open the project in IntelliJ.
2. Start the Spring Boot app using IntelliJ's profiler.
3. Confirm the app is running on <http://localhost:8080>.
4. Run a JMeter 10-user or 25-user load against `/calendar/day/2026-01-20`.
5. Watch for hotspots in controller, service, repository, database/JPA, security, and Thymeleaf rendering.
6. Take one screenshot of the profiler result.
7. Save the screenshot under `docs/cw2-evidence/profiler/`.
8. Fill in the observations section using only what is actually visible.

Recommended profiling load: use the 25-user run if you want the clearest sustained hotspot view; use the 10-user run if you want a lighter load.
