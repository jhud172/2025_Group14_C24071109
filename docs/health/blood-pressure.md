# Blood Pressure Tracking

Track, visualise, and manage blood pressure readings within the app.

## Routes

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/health/blood-pressure` | Hub page – log a reading and view history |
| `POST` | `/health/blood-pressure` | Save a new reading |
| `GET` | `/health/blood-pressure/edit/{id}` | Edit form for an existing reading |
| `POST` | `/health/blood-pressure/edit/{id}` | Save edited reading |
| `POST` | `/health/blood-pressure/delete/{id}` | Delete a reading |
| `GET` | `/api/blood-pressure` | JSON list (query params: `from`, `to` ISO dates) |
| `POST` | `/api/blood-pressure` | JSON create (body: reading JSON) |

All routes require an authenticated session (`user` attribute in `HttpSession`). Unauthenticated requests redirect to `/login`.

## Data Model – `BloodPressureReading`

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| `id` | `BIGINT` | PK | Auto-generated |
| `user_id` | `BIGINT` | ✓ | FK → `users(id)` ON DELETE CASCADE |
| `reading_date` | `DATE` | ✓ | |
| `reading_time` | `TIME` | | Optional – omit for one-per-day readings |
| `systolic` | `INT` | ✓ | 60–250 mmHg |
| `diastolic` | `INT` | ✓ | 40–150 mmHg |
| `pulse` | `INT` | | 30–220 bpm |
| `arm` | `VARCHAR(10)` | | `LEFT` or `RIGHT` |
| `position` | `VARCHAR(10)` | | `SITTING`, `STANDING`, or `LYING` |
| `notes` | `VARCHAR(500)` | | Free text |
| `source` | `VARCHAR(10)` | ✓ | `MANUAL` or `IMPORTED` (default `MANUAL`) |
| `created_at` | `TIMESTAMP` | ✓ | Set on insert |
| `updated_at` | `TIMESTAMP` | ✓ | Set on insert and update |

## Category Classification (`BpCategory`)

| Category | Systolic | Diastolic | Badge |
|----------|----------|-----------|-------|
| Low | < 90 | or < 60 | Blue |
| Normal | 90–119 | and < 80 | Emerald |
| Elevated | 120–129 | and < 80 | Yellow |
| High Stage 1 | 130–139 | or 80–89 | Orange |
| High Stage 2 | 140–179 | or 90–119 | Red |
| Hypertensive Crisis | ≥ 180 | or ≥ 120 | Dark Red |

Classification follows American Heart Association guidelines.

## One-per-day Rule

When `readingTime` is `null`, only one reading is allowed per day. This is enforced in `BloodPressureService.save()`. To log multiple readings on the same day, set a specific `readingTime`.

## Database

- **schema.sql** – H2 (test) table definition using `BIGSERIAL PRIMARY KEY`.
- **migrations/20260302_add_blood_pressure.sql** – Postgres (production) migration.
- The migration list is configured in `application.properties` and `application-test.properties` under `spring.sql.init.schema-locations`.

## Navigation

A **Vitals** link (heart-monitor icon) is added to the CLIENT/USER navbar section, pointing to `/health/blood-pressure`.
