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

Optional local overrides:

- copy [`.env.example`](./.env.example) to `.env` if you need to provide mail, SMS, AI, payment, or base-URL settings for local runs

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

## Useful Commands

Watch CSS:

```bash
npm run watch:css
```

Run tests:

```bash
./gradlew test
```

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
