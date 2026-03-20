# One To One

One To One is a Spring Boot coaching platform for trainer-client collaboration, planning, tracking, messaging, profile customisation, and role-based dashboards.

This repository uses:
- Java 21
- Spring Boot 3.5
- Thymeleaf
- Spring Security
- JPA with H2/PostgreSQL
- Tailwind/PostCSS for CSS generation

## Repo Guide

This README is a lightweight entry point. The main project documentation already lives in [`docs/`](./docs).

- Stable product and platform overview: [`docs/System-Overview.md`](./docs/System-Overview.md)
- Current development-state snapshot: [`docs/System-Overview-Dev-Mode.md`](./docs/System-Overview-Dev-Mode.md)
- Codex workflow and automation guidance: [`docs/Codex_Automation_Setup.md`](./docs/Codex_Automation_Setup.md)
- Temporary notes / scratch documentation: [`docs/Temp_Text.md`](./docs/Temp_Text.md)

## Quick Start

### Prerequisites

- Java 21
- Node.js and npm

### Run Locally

Install frontend tooling if needed:

```bash
npm install
```

Start the Spring Boot app:

```bash
./gradlew bootRun
```

By default, `bootRun` uses the `local` Spring profile when no profile is set. The local profile uses an in-memory H2 database and defaults to port `8081`.

### Frontend CSS

Build CSS once:

```bash
npm run build:css
```

Watch CSS during development:

```bash
npm run watch:css
```

### Tests

Run the test suite with:

```bash
./gradlew test
```

## Key Paths

- Application entry point: [`src/main/java/uk/ac/cf/_5/group14/One_To_One/OneToOneApplication.java`](./src/main/java/uk/ac/cf/_5/group14/One_To_One/OneToOneApplication.java)
- Server-rendered templates: [`src/main/resources/templates`](./src/main/resources/templates)
- Static assets: [`src/main/resources/static`](./src/main/resources/static)
- Database schema and seed data: [`src/main/resources/schema.sql`](./src/main/resources/schema.sql) and [`src/main/resources/data`](./src/main/resources/data)
- Docker build: [`Dockerfile`](./Dockerfile)

## Notes

- Production-style configuration defaults to port `8080`; local development defaults to `8081`.
- The repository contains a root `.env` file used by `bootRun` for selected mail, SMS, and base URL settings.
- For fuller product and implementation details, prefer the files in [`docs/`](./docs) instead of expanding this README into a second source of truth.
