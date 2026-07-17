# One To One

One To One is a Spring Boot coaching platform for trainer-client collaboration, planning, tracking, messaging, profile customization, payments, and role-based dashboards.

## Read This First

The main documentation now lives in [`docs/README.md`](./docs/README.md).

Core docs:

- stable platform overview: [`docs/System-Overview.md`](./docs/System-Overview.md)
- dev-mode-active overview: [`docs/System-Overview-Dev-Mode.md`](./docs/System-Overview-Dev-Mode.md)
- startup and deployment guide: [`docs/README.md`](./docs/README.md)

## Quick Start

Prerequisites:

- Java 21
- Node.js and npm

Use the pinned Node version before installing frontend tooling:

```bash
nvm use
```

Optional local overrides:

- copy [`.env.example`](./.env.example) to [`../.env`](../.env) if you need to provide mail, SMS, AI, payment, or base-URL settings for local runs
- `bootRun` loads the repository-root `.env` first, then falls back to `Web_App/.env` for older local setups
- database keys in `.env` are ignored when the active profile is `local` or `test`, so production PostgreSQL settings cannot replace the embedded H2 datasource

Install frontend dependencies:

```bash
npm install
```

Build CSS:

```bash
npm run build:css
```

Run the app:

```bash
./gradlew bootRun
```

Default local behavior:

- Spring profile: `local`
- URL: `http://localhost:8081`
- database: H2 in memory

The application also ignores an inherited `DATABASE_URL` for explicit `local` and `test` profiles. Render and other non-embedded profiles retain PostgreSQL URL normalisation.

## Useful Commands

Watch CSS:

```bash
npm run watch:css
```

Run tests:

```bash
./gradlew test
```

Run the automated browser simulation across public, client, trainer, gym, admin and super-admin profiles:

```bash
npm run qa:simulate
```

The simulation starts the local app when needed, exercises feature workflows, checks every configured page against page/element/design/text/accessibility criteria, and writes Markdown, JSON, JUnit and screenshot evidence under `output/playwright/site-simulation`. See [`docs/qa/SITE_SIMULATION_STANDARDS.md`](./docs/qa/SITE_SIMULATION_STANDARDS.md).

Check dependency drift:

```bash
./gradlew dependencyUpdates
npm outdated
```

## Key Paths

- Java source: [`src/main/java/uk/ac/cf/_5/group14/One_To_One`](./src/main/java/uk/ac/cf/_5/group14/One_To_One)
- templates: [`src/main/resources/templates`](./src/main/resources/templates)
- static assets: [`src/main/resources/static`](./src/main/resources/static)
- application config: [`src/main/resources`](./src/main/resources)
- deployment container: [`Dockerfile`](./Dockerfile)
- repo tooling and MCP workers: [`tools`](./tools)
- generated local artifacts and logs: [`output`](./output)

## Deployment

The repository is set up to run the application on Render, with the public hostname expected to be served through Cloudflare. See [`docs/README.md`](./docs/README.md) for the environment-variable list and deployment notes.
