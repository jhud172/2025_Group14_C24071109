# One To One — project handoff and continuation point

**Last updated:** 15 July 2026
**Repository:** `G:\No OneDrive Work\My Website\Crystal-Powers-OneToOne\One To One\One-To-One`
**Application:** Spring web app in `Web_App`  
**Current branch:** `main`  
**Current phase:** Phase 2 in progress; role-aware authentication/onboarding, four-role dashboards and shared role surfaces complete
**Local preview:** `http://localhost:8081`

## Purpose

This file is the durable reference point for continuing the One To One project on another device or in a new Codex session. Read this file first, then read `ONE_TO_ONE_UX_AUDIT.md` for the full evidence, defect inventory and phased plan.

## Critical transfer warning

The current working tree contains modified and untracked implementation files. They are **not committed at the time of this handoff**. A Markdown file alone will not transfer those changes to another device.

Before changing devices, preserve the whole working tree by doing one of the following:

1. Review, commit and push all intended project changes to the remote repository.
2. Copy the complete repository folder, including untracked files, to the new device.

Do not clone a fresh copy on the new device and assume this work will be present until the changes have been committed and pushed.

## Resume prompt for a new Codex session

Use this prompt after opening the repository on the new device:

> Read `PROJECT_HANDOFF.md`, `ONE_TO_ONE_UX_AUDIT.md` and `Web_App/AGENTS.md`. Preserve all existing work. Verify the CSS build and Gradle tests, start the application at localhost:8081, then continue Phase 2 surface consistency from the documented next step.

## Current verified status

- The full-site UX and visual audit is complete.
- Phase 0 emergency stability is complete.
- Phase 1 overlay and motion continuity is complete.
- Phase 1 performance and shell continuity is complete.
- The post-Phase 1 website stabilisation pass is complete: all eight failing web tests are repaired and local startup is isolated from production database values in `.env`.
- Phase 2's role-aware authentication and onboarding workstream is complete.
- Phase 2's shared dashboard consistency workstream is complete for the client, trainer, gym-admin and platform-admin dashboards.
- Phase 2's role-aware shared inbox, support and shared-tool workstream is complete for all four roles.
- Latest full Gradle result: **483 tests passed, 0 failed, 0 skipped**.
- Latest local runtime proof: **HTTP 200** at `http://localhost:8081`, active profile `local`, datasource `jdbc:h2:mem:localdb`.
- Latest CSS/JS version token: **`20260715p8`**.
- Core CSS is **212,420 bytes / 207.4 KiB**, approximately 83% smaller than the previous monolithic core.
- Core CSS transfers at approximately **34 KiB with gzip**.
- Static CSS caching, gzip compression, `Last-Modified` and `304 Not Modified` revalidation were verified.
- Dynamic HTML remains gzip-compressed and `no-store`.
- Responsive browser checks passed at mobile, tablet and desktop sizes with no horizontal overflow in the verified routes.
- Phase 2 browser checks passed for client, trainer and gym login states, including the 390 × 844 gym form with no horizontal overflow or Charlie collision.
- Shared-surface browser checks passed at **1280 × 900** and **390 × 844** for the client coaching inbox, trainer workout studio, gym support form and platform-admin support inbox; the role-specific platform action set was checked in each session.

## User experience direction to preserve

The product should feel like a fluent river: continuous, calm and responsive, with no visible jitters, gaps, clipped content or competing layers.

Keep these already implemented homepage and Charlie requirements intact:

- Closing Charlie returns it to a visible animated launcher; the assistant indicator must not disappear.
- Charlie supports up to five image attachments.
- Attachments remain visible in a scrollable area and can be removed.
- The message input displays its 1,600-character limit and over-limit state.
- The homepage Login typography matches the rest of the navigation.
- Motion must use named tokens and respect reduced-motion preferences.
- Only one global overlay surface owns the interaction layer at a time.

## Completed implementation

### Phase 0 — emergency stability

- Repaired client calendar month/week rendering.
- Restored trainer price formatting.
- Linked seeded and approved gym-admin users to gym profiles so operational routes work.
- Corrected gym Support routing and dashboard action presentation.
- Repaired client dashboard layout and fixed-panel clearance.
- Made authenticated mobile navigation scroll-safe, focus-managed and collision-free.
- Standardized Charlie naming and closed-panel accessibility behavior.

### Phase 1 — continuity

- Added a shared overlay coordinator for navigation, Charlie, Quick Actions and platform settings.
- Kept nested Charlie lightbox ownership separate while preserving parent focus and inert behavior.
- Added named motion duration/easing tokens and a shared layer scale.
- Replaced all broad transitions in the highest-risk global overlay surfaces with explicit properties.
- Added regression tests for overlay ownership, layer values and transition rules.

### Phase 1 — performance and shell continuity

- Split the monolithic CSS into a compact shared core and eight conditional feature bundles.
- Added production minification with cssnano.
- Added server-side route-to-style mapping through `UiStyleBundleAdvice`.
- Added shared measured variables for platform-panel height, local mobile-dock height, safe-area spacing and floating controls.
- Updated page padding, Charlie and Quick Actions to consume the same reservation values.
- Enabled response compression and one-day public caching for static resources.
- Added a cache-safe CSS/JS rollout version.
- Added performance and shell contract tests.

### Post-Phase 1 — website test and local-startup stabilisation

- Corrected the test fixture's trainer and gym access codes to match the documented login contracts.
- Restarted H2 identity counters after fixed-ID fixture rows so repository and service tests can create new records without primary-key collisions.
- Prevented `bootRun` from forwarding production database keys from `.env` when the effective profile is `local` or `test`.
- Made application startup ignore inherited `DATABASE_URL` values for the embedded `local` and `test` profiles while preserving Render/PostgreSQL URL normalisation.
- Verified the focused 13-test regression set, the complete 467-test suite and a live local H2 startup.

### Phase 2 — role-aware authentication and onboarding

- Made the login page server-render the selected client, trainer or gym state before JavaScript runs; inactive controls are now `hidden`, `inert` and excluded from assistive navigation.
- Added grouped trainer/gym code semantics, role-specific hints and sign-up paths, preserved identifiers after failure, and focused/announced the invalid credential group.
- Enforced the selected login role against the authenticated account and kept mismatch errors deliberately neutral.
- Routed returning client, trainer, gym-admin and platform-admin users to their correct dashboards while preserving valid saved requests.
- Added admin-specific onboarding and destination handling, replaced tutorial emoji with one SVG icon family, stabilised its content transition and added reduced-motion behaviour.
- Removed the global Charlie launcher from focused authentication/sign-up layouts so it cannot cover credential controls on mobile.
- Added cache-safe delivery for the changed authentication/tutorial CSS and JavaScript with version `20260715p2a`.

### Phase 2 — four-role dashboard consistency

- Added one shared dashboard contract for typography, surfaces, metrics, focus states, primary/secondary actions, fields, tables and useful empty states.
- Applied the contract to the client, trainer, gym-admin and platform-admin dashboards while retaining each role's workflow and responsive layout.
- Measured the shared text tokens against their intended surfaces. The lowest normal-text ratio is **4.76:1** in the light palette and **6.92:1** in the dark palette; the main title/body/accent tokens range up to **17.85:1**.
- Normalised dashboard controls to at least 44 px in rendered layouts. Client chip controls use a 45 px CSS minimum to remain at or above 44 px inside their existing transformed panels.
- Replaced passive zero-data messages with role-appropriate explanations and valid recovery actions, including restored client milestone recovery.
- Added automated dashboard contracts for measured contrast, shared template adoption, action sizing, reduced motion and useful empty-state copy.
- Rebuilt the production CSS and browser-checked all four dashboards at **1280 × 900** and **390 × 844** with one main landmark and no horizontal overflow.

### Phase 2 — role-aware shared surfaces

- Added one server-side role-surface context for client, trainer, gym-admin, platform-admin/super-admin and signed-out support states.
- Made inbox headings, notification guidance, thread prompts, dashboard links and empty-state recovery actions reflect the active role.
- Added a dedicated `/support` request page with role-specific headings and submit actions; gym navigation now leads to gym support while platform admins retain the separate moderation inbox.
- Reworked the platform action tray so each role receives only valid destinations, with role-scoped saved customisation state.
- Clarified the intent of workouts and goals for coaching roles versus personal use by operational roles, and made the closed custom-exercise dialog inert, Escape-closeable and focus-restoring.
- Applied the completed dashboard hierarchy to the admin support inbox and added a useful zero-request recovery state.
- Added `RoleAwareSharedSurfaceContractTest`, rebuilt production CSS and completed the 483-test Gradle suite.
- Browser-checked the representative shared journey for all four roles at **1280 × 900** and **390 × 844**.

## Important implementation files

### Project evidence and handoff

- `PROJECT_HANDOFF.md` — this continuation point.
- `ONE_TO_ONE_UX_AUDIT.md` — complete audit, priorities and phase plan.
- `Web_App/AGENTS.md` — repository-specific working instructions.

### CSS pipeline and delivery

- `Web_App/package.json`
- `Web_App/package-lock.json`
- `Web_App/postcss.config.js`
- `Web_App/tailwind.config.js`
- `Web_App/src/main/resources/static/css/components/core/index.css`
- `Web_App/src/main/resources/static/css/components/core/interaction-tokens.css`
- `Web_App/src/main/resources/static/css/components/core/shell-layout.css`
- `Web_App/src/main/resources/static/css/components/core/role-surfaces.css`
- `Web_App/src/main/resources/static/css/components/dashboard/dashboard-consistency.css`
- `Web_App/src/main/resources/static/css/entries/`
- `Web_App/src/main/resources/static/css/bundles/`
- `Web_App/src/main/resources/static/css/app.css`

### Shell ownership and fixed surfaces

- `Web_App/src/main/resources/static/js/core/overlay-manager.js`
- `Web_App/src/main/resources/static/js/core/navbar-page.js`
- `Web_App/src/main/resources/static/js/core/platform-panel.js`
- `Web_App/src/main/resources/static/js/core/quick-actions.js`
- `Web_App/src/main/resources/static/js/chat/chat.js`
- `Web_App/src/main/resources/static/js/dashboard/client-dashboard-page.js`
- `Web_App/src/main/resources/templates/base.html`

### Route-aware bundles and server delivery

- `Web_App/src/main/java/uk/ac/cf/_5/group14/One_To_One/Config/UiStyleBundleAdvice.java`
- `Web_App/src/main/java/uk/ac/cf/_5/group14/One_To_One/Config/RoleSurfaceContext.java`
- `Web_App/src/main/java/uk/ac/cf/_5/group14/One_To_One/Config/RoleSurfaceModelAdvice.java`
- `Web_App/src/main/resources/application.properties`

### Regression coverage

- `Web_App/src/test/java/uk/ac/cf/_5/group14/One_To_One/SecurityTests/OverlayContinuityContractTest.java`
- `Web_App/src/test/java/uk/ac/cf/_5/group14/One_To_One/SecurityTests/ShellPerformanceContractTest.java`
- `Web_App/src/test/java/uk/ac/cf/_5/group14/One_To_One/SecurityTests/TemplateRouteContractTest.java`
- `Web_App/src/test/java/uk/ac/cf/_5/group14/One_To_One/SecurityTests/LoginIntegrationTest.java`
- `Web_App/src/test/java/uk/ac/cf/_5/group14/One_To_One/SecurityTests/TutorialRoleFlowTest.java`
- `Web_App/src/test/java/uk/ac/cf/_5/group14/One_To_One/SecurityTests/DashboardConsistencyContractTest.java`
- `Web_App/src/test/java/uk/ac/cf/_5/group14/One_To_One/SecurityTests/RoleAwareSharedSurfaceContractTest.java`
- `Web_App/src/test/resources/data/00-data.sql`

### Local startup isolation

- `Web_App/build.gradle`
- `Web_App/src/main/java/uk/ac/cf/_5/group14/One_To_One/OneToOneApplication.java`
- `Web_App/src/main/resources/application-local.properties`

## Environment and commands

Expected tool versions from the repository configuration:

- Java 21
- Node.js 22.22.x
- npm 11.11.x

From the repository root in PowerShell:

```powershell
Set-Location .\Web_App
npm ci
npm run build:css
.\gradlew.bat test --no-daemon
.\gradlew.bat bootRun
```

Then open:

```text
http://localhost:8081
```

The local profile always uses in-memory H2. Production database variables in the root `.env` are intentionally ignored during a local or test run.

Useful focused checks:

```powershell
node --check src/main/resources/static/js/core/platform-panel.js
node --check src/main/resources/static/js/dashboard/client-dashboard-page.js
.\gradlew.bat test --tests "*OverlayContinuityContractTest" --tests "*ShellPerformanceContractTest" --no-daemon
```

## Next implementation step

Continue **Phase 2 — surface consistency**.

The immediate next workstream is to shorten long public pages and standardise their section transitions using the existing motion tokens and role-neutral public hierarchy.

Priority order:

1. **Complete:** make authentication and onboarding role-aware, including field visibility, grouped errors and destination routing.
2. **Complete for the four role dashboards:** standardise typography, measured contrast, action hierarchy and useful empty states.
3. **Complete:** make shared inbox, support and tool copy/actions/empty states match the active role.
4. **Next:** shorten long public pages and standardise section transitions using the existing motion tokens.
5. Continue replacing broad dashboard/calendar transitions only after confirming which properties visibly change.

Phase 2 exit condition: every role uses the same visual, action and recovery language without reintroducing overlay collisions, hard-coded fixed offsets or broad transitions.

## Remaining release QA

These checks were not claimed as complete and remain scheduled for Phase 3:

- Physical touch devices and GPU performance.
- Screen-reader output and full keyboard order.
- Whole-site measured colour contrast outside the completed dashboard token set.
- 200% and 400% zoom.
- Throttled CPU and network testing.
- Reduced-motion verification across every remaining feature surface.
- Responsive regression at 390, 768, 1024, 1280, 1366, 1440, 1536 and 1920 px.

## Rules for future updates to this file

After each implementation phase:

1. Update the date, current phase and next step at the top.
2. Record the exact full test count and failures/skips.
3. Record any CSS/JS version-token change.
4. Add the main files introduced or changed.
5. Record browser sizes and routes verified.
6. Clearly distinguish completed work from release QA still required.
7. Preserve previous phase summaries so the document remains a useful project history.

