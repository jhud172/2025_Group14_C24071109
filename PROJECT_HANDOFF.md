# One To One — project handoff and continuation point

**Last updated:** 13 July 2026  
**Repository:** `C:\Visual Studio\One-To-One\One-To-One`  
**Application:** Spring web app in `Web_App`  
**Current branch:** `main`  
**Current phase:** Phase 1 complete; Phase 2 is next  
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

> Read `PROJECT_HANDOFF.md`, `ONE_TO_ONE_UX_AUDIT.md` and `Web_App/AGENTS.md`. Preserve all existing work. Verify the CSS build and Gradle tests, start the application at localhost:8081, then begin Phase 2 surface consistency from the documented next step.

## Current verified status

- The full-site UX and visual audit is complete.
- Phase 0 emergency stability is complete.
- Phase 1 overlay and motion continuity is complete.
- Phase 1 performance and shell continuity is complete.
- Latest full Gradle result: **467 tests passed, 0 failed, 0 skipped**.
- Latest CSS version token: **`20260713p1b`**.
- Core CSS is **212,420 bytes / 207.4 KiB**, approximately 83% smaller than the previous monolithic core.
- Core CSS transfers at approximately **34 KiB with gzip**.
- Static CSS caching, gzip compression, `Last-Modified` and `304 Not Modified` revalidation were verified.
- Dynamic HTML remains gzip-compressed and `no-store`.
- Responsive browser checks passed at mobile, tablet and desktop sizes with no horizontal overflow in the verified routes.

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
- `Web_App/src/main/resources/application.properties`

### Regression coverage

- `Web_App/src/test/java/uk/ac/cf/_5/group14/One_To_One/SecurityTests/OverlayContinuityContractTest.java`
- `Web_App/src/test/java/uk/ac/cf/_5/group14/One_To_One/SecurityTests/ShellPerformanceContractTest.java`
- `Web_App/src/test/java/uk/ac/cf/_5/group14/One_To_One/SecurityTests/TemplateRouteContractTest.java`

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

Useful focused checks:

```powershell
node --check src/main/resources/static/js/core/platform-panel.js
node --check src/main/resources/static/js/dashboard/client-dashboard-page.js
.\gradlew.bat test --tests "*OverlayContinuityContractTest" --tests "*ShellPerformanceContractTest" --no-daemon
```

## Next implementation step

Begin **Phase 2 — surface consistency**.

Priority order:

1. Make authentication and onboarding role-aware, including field visibility, grouped errors and destination routing.
2. Standardize typography, contrast, action hierarchy and empty states across public and authenticated roles.
3. Make shared inbox, support and tool copy match the active role.
4. Shorten long public pages and standardize section transitions using the existing motion tokens.
5. Continue replacing broad dashboard/calendar transitions only after confirming which properties visibly change.

Phase 2 exit condition: every role uses the same visual, action and recovery language without reintroducing overlay collisions, hard-coded fixed offsets or broad transitions.

## Remaining release QA

These checks were not claimed as complete and remain scheduled for Phase 3:

- Physical touch devices and GPU performance.
- Screen-reader output and full keyboard order.
- Measured colour contrast.
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

