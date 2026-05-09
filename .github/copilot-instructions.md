# Copilot instructions for One To One

## Build and test commands

Most active work is in `Web_App`. Run these from `Web_App` unless the task is explicitly about the Android app.

```powershell
npm install
npm run build:css
npm run watch:css
.\gradlew.bat bootRun
.\gradlew.bat test
.\gradlew.bat test --tests uk.ac.cf._5.group14.One_To_One.SecurityTests.LoginIntegrationTest
.\gradlew.bat test --tests uk.ac.cf._5.group14.One_To_One.ProfileTests.ProfileRouteAccessTest
```

- `bootRun` defaults to the `local` profile unless `SPRING_PROFILES_ACTIVE` is set, and it switches to `render` automatically when Render provides `RENDER=true`.
- `test` uses `src\test\resources\application-test.properties` with an embedded H2 database and seeded SQL data.
- `.\gradlew.bat test` excludes `UserRepositoryTest` and `UserPreferenceFullContainerMockTests` in `build.gradle`; do not assume those ran unless you invoke them directly.
- There is no dedicated lint task in the current repository.

Android companion commands live under `Phone-App\application`:

```powershell
.\gradlew.bat assembleDebug
.\gradlew.bat installDebug
```

## High-level architecture

- The repository contains two applications: `Web_App` is the main Spring Boot platform and source of truth, while `Phone-App\application` is a native Android companion that calls the hosted web service and depends on `/api/mobile/**`.
- `Web_App` is a feature-oriented Spring Boot monolith using Java 21, Spring MVC, Spring Security, Spring Data JPA, Thymeleaf, and scheduled jobs. Packages under `src\main\java\uk\ac\cf\_5\group14\One_To_One\*` are organized by feature area instead of by technical layer.
- The frontend is server-rendered Thymeleaf. Templates are split by audience under `src\main\resources\templates` (`client-views`, `trainer-views`, `gym-views`, `admin-views`, `public-views`, `shared-views`, `system-views`) with shared fragments in `universal-fragments`. Static assets live under `src\main\resources\static`, and `npm run build:css` compiles `src\main\resources\static\css\tailwind.css` into `src\main\resources\static\css\app.css`.
- Runtime profile wiring is important. `local` uses H2 with `schema.sql` plus `data\*.sql` seed files on port `8081`; `render` uses PostgreSQL with `schema-render.sql` plus `render-data.sql`. `OneToOneApplication` normalizes Render-style `DATABASE_URL` values into JDBC settings at startup, and the repo-level `render.yaml` deploys the Dockerized `Web_App`.
- Authentication and authorization are centralized in `src\main\java\uk\ac\cf\_5\group14\One_To_One\Security\SecurityConfig.java`. `DEV_MODE` relaxes browsing access for public pages but still keeps dashboard, calendar, workouts, goals, profile, trainer, gym, and admin flows protected. Mobile auth endpoints under `/api/mobile/auth/**` are explicitly whitelisted.
- Tests mirror the feature layout under `src\test\java`. The suite mixes full `@SpringBootTest` integration tests with narrower `@WebMvcTest` slices that mock feature services.

## Key conventions

- Preserve the feature-first structure. When changing a feature, put backend code, templates, assets, and tests in the existing feature area instead of creating cross-cutting catch-all folders.
- Keep HTML and Thymeleaf files as markup-only surfaces. Do not add inline `<script>`, inline `<style>`, inline event handlers, or new `th:style` usage. Put behavior in `src\main\resources\static\js\**` and styling in `src\main\resources\static\css\**`. The current frontend audit in `Web_App\docs\audits\frontend-template-structure-audit-2026-03-29.md` is the source of truth for remaining violations.
- Reuse existing UI patterns before inventing new ones. `Web_App\.codex\frontend_rules.md` and `Web_App\.codex\ui_patterns.md` are the current pattern references for cards, buttons, modals, hover panels, sticky bars, and motion.
- Keep template routing audience-specific. Controllers typically return views under `client-views\...`, `trainer-views\...`, `gym-views\...`, `public-views\...`, or `system-views\...`; new templates should follow the same audience split.
- Prefer the seeded SQL data for local and test-path debugging. Authentication work should usually start with the demo accounts defined in `src\main\resources\data\00-auth-demo.sql`, such as `demo`, `trainer_demo`, and `gymadmin_demo` with password `Demo123!` in local/test environments.
- Do not assume `.env` is a full local configuration loader. `bootRun` only forwards a limited set of keys from `.env` (mail, `APP_BASE_URL`, SMS, and Twilio-related values); most runtime behavior still comes from Spring profile properties and real environment variables.
