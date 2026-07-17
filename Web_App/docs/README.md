# One To One Documentation Guide

## Start Here

This folder is the documentation entry point for the repository.

Use these files first:

- [System-Overview.md](./System-Overview.md) for the normal platform baseline
- [System-Overview-Dev-Mode.md](./System-Overview-Dev-Mode.md) for development-mode-active behavior
- [audits/frontend-template-structure-audit-2026-03-29.md](./audits/frontend-template-structure-audit-2026-03-29.md) for the current frontend template audit

## Local Startup

### Prerequisites

- Java 21
- Node.js and npm

### First Run

1. Install frontend dependencies:

   ```bash
   npm install
   ```

2. Build CSS once:

   ```bash
   npm run build:css
   ```

3. Start the application:

   ```bash
   ./gradlew bootRun
   ```

4. Open the local app:

   - application URL: `http://localhost:8081`
   - H2 console: `http://localhost:8081/h2-console`

By default, `bootRun` uses the `local` Spring profile if no profile is set.

For `local` and `test`, database variables from the root `.env` are not forwarded and an inherited `DATABASE_URL` is ignored by application startup. This keeps local runs on H2 even when the same `.env` contains production PostgreSQL settings.

## Local Runtime Behavior

The `local` profile currently uses:

- in-memory H2 database
- `schema.sql`
- `data/*.sql`
- default port `8081`

Demo accounts are seeded from [`src/main/resources/data/00-auth-demo.sql`](../src/main/resources/data/00-auth-demo.sql).

Example local login:

- username: `demo`
- password: `Demo123!`

## Frontend Build Loop

Build CSS once:

```bash
npm run build:css
```

Watch CSS during UI work:

```bash
npm run watch:css
```

## Tests

Run the full test suite:

```bash
./gradlew test
```

Latest verified result (14 July 2026): **467 tests passed, 0 failed, 0 skipped**.

Useful targeted checks:

```bash
./gradlew test --tests uk.ac.cf._5.group14.One_To_One.SecurityTests.LoginIntegrationTest
./gradlew test --tests uk.ac.cf._5.group14.One_To_One.ProfileTests.ProfileRouteAccessTest
```

## Profiles And Deployment

### Local

- profile: `local`
- port: `8081`
- database: H2 in memory
- SQL init: enabled

### Render

- profile: `render`
- port: `8080` unless Render overrides `PORT`
- database: PostgreSQL
- SQL init: `schema-render.sql` plus `render-data.sql`
- Docker image entry point: [`Dockerfile`](../Dockerfile)
- repo-level Blueprint: [`../../render.yaml`](../../render.yaml)

The Dockerfile already sets:

- `SPRING_PROFILES_ACTIVE=render`

If Render starts the app with `./gradlew bootRun`, the Gradle task now detects Render's default `RENDER=true` environment variable and uses the `render` Spring profile instead of the local H2 profile.

The application also normalizes PostgreSQL-style `DATABASE_URL` values at startup in [`src/main/java/uk/ac/cf/_5/group14/One_To_One/OneToOneApplication.java`](../src/main/java/uk/ac/cf/_5/group14/One_To_One/OneToOneApplication.java).

## Render And Cloudflare Deployment Notes

The repository is set up to run the application itself on Render and to serve the public hostname through Cloudflare.

### Render Responsibilities

Render should host the application container and provide:

- the running web service
- `PORT`
- PostgreSQL connection details
- any production secrets

Because the repository now has the web application inside `Web_App`, Render must build from that folder. Use one of these setups:

- Recommended Docker setup: use the repo root [`render.yaml`](../../render.yaml), or set Dockerfile Path to `./Web_App/Dockerfile` and Docker Build Context to `./Web_App`.
- Existing Dashboard service using commands: set Root Directory to `Web_App`, Build Command to `chmod +x ./gradlew && npm ci && npm run build:css && ./gradlew build -x test`, and Start Command to `chmod +x ./gradlew && ./gradlew bootRun --no-daemon`.

For the command-based setup, keep `SPRING_PROFILES_ACTIVE=render` in Render environment variables. The app also falls back to the render profile automatically when Render provides `RENDER=true`.

Minimum environment variables for a working Render deployment:

- `DATABASE_URL`
- `DATABASE_USER`
- `DATABASE_PASSWORD`
- `APP_BASE_URL`

Commonly needed production variables by feature:

- `OPENAI_API_KEY`
- `APP_AI_ENABLED`
- `APP_AI_MODEL`
- `STRIPE_SECRET_KEY`
- `STRIPE_WEBHOOK_SECRET`
- `SPRING_MAIL_HOST`
- `SPRING_MAIL_PORT`
- `SPRING_MAIL_USERNAME`
- `SPRING_MAIL_PASSWORD`
- `APP_EMAIL_FROM`
- `APP_SMS_PROVIDER`
- `TWILIO_ACCOUNT_SID`
- `TWILIO_AUTH_TOKEN`
- `TWILIO_FROM_NUMBER`
- `TWILIO_MESSAGING_SERVICE_SID`
- `APP_STORAGE_PROFILE_DIR`
- `APP_STORAGE_MERCH_DIR`
- `APP_STORAGE_WORKOUT_VIDEO_DIR`
- `DEV_MODE` should normally stay `false`

### Cloudflare Responsibilities

Cloudflare is not configured by code in this repository. There is no `wrangler.toml`, Worker, or Pages config checked in here.

Cloudflare is expected to sit in front of the Render app as the public DNS/proxy layer:

- point the production hostname at the Render service
- keep the public URL stable
- make sure `APP_BASE_URL` matches the Cloudflare-served public domain
- keep SSL and proxy settings aligned with the deployed hostname

If the public site lives at `https://crystal-production.com`, `APP_BASE_URL` should match that public URL rather than the internal Render hostname.

## `.env` Notes

`bootRun` reads application overrides from the root `.env` file before launching the app, falling back to `Web_App/.env` for older setups. When the effective profile is `local` or `test`, it deliberately skips PostgreSQL/database keys such as `DATABASE_URL`, `DATABASE_USER`, `DATABASE_PASSWORD` and the `PG*` connection variables. Mail, base-URL, SMS/Twilio, AI and payment overrides remain available locally.

For Render or another non-embedded profile, database variables are still forwarded and PostgreSQL-style URLs are normalised at application startup.

`DEV_MODE` is also read separately by `DevModeProperties` from the environment or `.env`.

Do not commit real secrets.

## Documentation Map

### Core Docs

- [System-Overview.md](./System-Overview.md)
- [System-Overview-Dev-Mode.md](./System-Overview-Dev-Mode.md)

### Setup And Working Notes

- [Codex_Automation_Setup.md](./Codex_Automation_Setup.md)
- [dependency-migration-plan-2026-04-11.md](./dependency-migration-plan-2026-04-11.md)

### Audits

- [audits/frontend-template-structure-audit-2026-03-29.md](./audits/frontend-template-structure-audit-2026-03-29.md)
- [project-improvement-audit-2026-03-21.md](./project-improvement-audit-2026-03-21.md)

## Maintenance Rule

Keep this file focused on:

- how to start the project
- how profiles and deployment are wired
- which docs should be read next

Keep feature detail in the overview files, and keep point-in-time findings in the audit files.
