# One To One — project handoff and continuation point

**Last updated:** 28 July 2026
**Repository:** `G:\No OneDrive Work\My Website\Crystal-Powers-OneToOne\One To One\One-To-One`
**Application:** Spring web app in `Web_App`  
**Current branch:** `main`  
**Current phase:** Phase 4 local P0 hardening complete; explicit cost approval is required before isolated staging provisioning
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

> Read `PROJECT_HANDOFF.md`, `PHASE4_PRODUCTION_READINESS.md`, `ONE_TO_ONE_UX_AUDIT.md` and `Web_App/AGENTS.md`. Preserve all existing work. Continue Phase 4 from the NO-GO blockers: create a strictly isolated staging service and fresh non-production PostgreSQL database, remove the production demo-account seed, implement durable uploads, and configure only sandbox/test provider credentials. Do not make a charge, refund, production-data change or production webhook change without my explicit approval. Reproduce failures before editing and rerun the full Gradle and release-gate suites after confirmed repairs.

## Current verified status

- The full-site UX and visual audit is complete.
- Phase 0 emergency stability is complete.
- Phase 1 overlay and motion continuity is complete.
- Phase 1 performance and shell continuity is complete.
- The post-Phase 1 website stabilisation pass is complete: all eight failing web tests are repaired and local startup is isolated from production database values in `.env`.
- Phase 2's role-aware authentication and onboarding workstream is complete.
- Phase 2's shared dashboard consistency workstream is complete for the client, trainer, gym-admin and platform-admin dashboards.
- Phase 2's role-aware shared inbox, support and shared-tool workstream is complete for all four roles.
- Phase 2's public-page workstream is complete: Home, About, Pricing and FAQ now use shorter four-stage journeys and one shared section-transition contract.
- Phase 3 now covers the public routes, login/sign-up presentation and the client, trainer, gym-admin and platform-admin dashboard baselines for keyboard/focus behaviour, reduced motion and 200% reflow.
- Phase 3 now also covers calendar, inbox, workouts, goals, support and platform-admin operations for keyboard/focus behaviour, overlay trapping/return, accessible names, live feedback, reduced motion, 200% reflow and an initial 400% CSS-viewport equivalent.
- Phase 3 browser release QA now also covers Chromium's accessibility tree, native 200%/400% zoom on representative login/client journeys, 400%-equivalent cross-role reflow, Windows forced colours, keyboard-only validation recovery and 44 × 44 px targets on critical controls.
- James has confirmed that the Phase 3 human release gate passed, closing the remaining audible screen-reader and real-touch-device checks.
- The final automated release gate covers public, login, client, trainer, gym and admin journeys: **88 responsive cases passed**, **22 Axe cases passed with zero serious/critical violations**, six cold-cache Slow 4G/4× CPU journeys passed, and six Lighthouse journeys passed.
- Final Lighthouse scores are: public **97/100/100/100**, login **98/100/100/100**, client **86/100/100**, trainer **92/100/100**, gym **92/100/100** and admin **94/100/100** for performance/accessibility/best practices, with SEO included for public/login.
- Latest full Gradle result: **508 tests passed, 0 failed, 0 skipped** across 127 suites.
- Latest local runtime proof: **HTTP 200** at `http://localhost:8081`, active profile `local`, datasource `jdbc:h2:mem:localdb`.
- Latest CSS/JS version token: **`20260727p23`**.
- Core CSS is **212,420 bytes / 207.4 KiB**, approximately 83% smaller than the previous monolithic core.
- Core CSS transfers at approximately **34 KiB with gzip**.
- Static CSS caching, gzip compression, `Last-Modified` and `304 Not Modified` revalidation were verified.
- Dynamic HTML remains gzip-compressed and `no-store`.
- Responsive browser checks passed at mobile, tablet and desktop sizes with no horizontal overflow in the verified routes.
- Phase 2 browser checks passed for client, trainer and gym login states, including the 390 × 844 gym form with no horizontal overflow or Charlie collision.
- Shared-surface browser checks passed at **1280 × 900** and **390 × 844** for the client coaching inbox, trainer workout studio, gym support form and platform-admin support inbox; the role-specific platform action set was checked in each session.
- Public Home, About, Pricing and FAQ checks passed at **1280 × 900** and **390 × 844** with no horizontal overflow. A **640 px CSS viewport** verified the 200% desktop reflow equivalent.
- Rapid-scroll and keyboard-focus fallbacks now reveal public sections that could otherwise remain visually hidden when an observer threshold was skipped.
- The client dashboard now applies the same fallback so rapid scrolling or direct keyboard focus cannot leave later cards visually hidden.
- Login role tabs now move selection and focus together with Arrow Left/Right and Home/End; the dashboard tour keeps a visible focus ring when its focus trap wraps.
- Authentication and all four role dashboards passed at **1280 × 900**, **390 × 844** and a **640 px CSS viewport** 200% reflow equivalent with no horizontal overflow; representative reduced-motion checks reported no remaining main-content animations.
- Calendar month/week, inbox/list/thread, workout management, goals and support passed at **1280 × 900**, **640 px**, **390 × 844** and a **320 px CSS viewport** initial 400% equivalent with one H1, one main landmark and no page-level horizontal overflow.
- Platform-admin dashboard, gym applications, off-platform payments and feedback returned HTTP 200 and reflowed at the same four widths; the previous off-platform-payment 500 is repaired.
- The 18 July remote continuation human-confirmed one concise Narrator login-validation announcement, then deferred the remaining audible journey until James is back at the computer. Chrome, Edge and WebKit mobile/touch emulation covered the critical four-role routes while he was remote.
- The remote continuation repaired the reproduced duplicate login validation announcement, the 32 px onboarding-tour Skip action and Charlie overlap on the final trainer/gym dashboard Inbox card. The final tour action is 44 px high and the repaired cards have zero fixed-control overlap.

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

### Phase 2 — concise public journeys and section continuity

- Reduced Home, About and Pricing to four-stage public journeys and kept FAQ focused on search, five common answers and support recovery.
- Preserved pricing checkout/subscription branches, public destinations, page titles, British English and existing SEO-facing route behaviour.
- Added one shared public hierarchy for headings, proof, roles, actions and final calls to action without introducing a new design system.
- Added restrained section reveals using the existing route-duration/easing tokens and a complete reduced-motion fallback.
- Repaired rapid-scroll and keyboard-focus edge cases so skipped or focused sections cannot remain invisible.
- Added `PublicPageConsistencyContractTest`, rebuilt production CSS and browser-checked all four routes at **1280 × 900** and **390 × 844**.

### Phase 3 — accessibility and responsive verification started

- Verified public keyboard order and visible focus treatment on the desktop Home journey.
- Verified the 390 × 844 mobile menu opens from the keyboard, moves focus into navigation, closes with Escape and restores focus to its trigger.
- Verified FAQ search filtering and disclosure expansion using only the keyboard, including a visible 3 px focus indicator.
- Verified reduced-motion rendering across Home, About, Pricing and FAQ: all sections remain visible and route motion collapses to `0.01 ms`.
- Verified the 200% desktop reflow equivalent at a 640 px CSS viewport on all four routes with no horizontal overflow.

### Phase 3 — authentication and four-role baseline

- Browser-checked login and sign-up at desktop and **390 × 844**, including the client, trainer and gym role-specific credential layouts.
- Corrected the login tab keyboard contract so Arrow Left/Right and Home/End move focus, selection, `tabindex` and the labelled panel together.
- Browser-checked client, trainer, gym-admin and platform-admin dashboards at **1280 × 900**, **390 × 844** and the **640 px** 200% reflow equivalent with one main landmark and no horizontal overflow.
- Repaired client-dashboard reveal handling for rapid scroll, keyboard focus and reduced motion; the full mobile page no longer develops blank content regions.
- Strengthened the onboarding tour's trapped-focus indicator so wrapped focus remains visibly outlined.
- Verified representative reduced-motion states across authentication and all four dashboards with no remaining main-content animation.
- Added regression contracts for the client reveal fallback, tour focus visibility and login roving focus.

### Phase 3 — shared operational-journey baseline

- Added missing month/week calendar headings and made the schedule drawer a labelled modal surface with closed-state `inert`, visibility-aware initial focus, Tab/Shift+Tab trapping, Escape dismissal and focus return.
- Converted goal timeframe controls and Workout Studio views into complete roving-tab patterns; made exercise filters keyboard controls with pressed state.
- Linked every custom-exercise label to its control, trapped and returned modal focus, wired Cancel to close, and exposed dynamic workout feedback as status/alert announcements.
- Cleared the narrow inbox send action from the two floating controls without changing the shared shell.
- Added unique accessible names, captions and live status/error semantics to platform-admin operational controls; made the payment-attempt table independently scrollable when populated.
- Corrected `/admin/off-platform-payments` to its existing template path, removing the verified HTTP 500.
- Versioned the changed calendar, goals and workout scripts through the shared cache token and advanced delivery to **`20260717p10`**.
- Rebuilt production CSS, passed JavaScript syntax checks and completed the **490-test** Gradle suite with no failures or skips.
- Browser verification passed at **1280 × 900**, **640 px**, **390 × 844** and **320 px**. Reduced-motion durations collapse to `0.01 ms`, repaired routes have no page-level horizontal overflow, and the checked consoles are error-free.

### Phase 3 — assistive-technology and high-zoom release QA

- Audited login/sign-up, all four dashboards, calendar, inbox, workouts, goals, support and populated platform-admin controls through Chromium's accessibility tree. The checked visible journeys retain a main landmark, ordered page heading and accessible control names.
- Exercised browser-native 200% and 400% zoom on representative login/client journeys, then checked all four roles at the equivalent 310 px CSS viewport. Repaired the client dashboard's narrow primary-action decoration and coach identity wrapping; the final client shell and main content both measure **268/268 px** with a **300/300 px** document at the 400%-equivalent width.
- Verified Windows forced colours and reduced motion together on the populated 17-row Dev Hub table. Its caption and four headers remain exposed, and a keyboard-focused select receives a visible **3 px** system-colour outline.
- Repaired undersized authentication, shared profile, calendar, workout, inbox, client-dashboard flyout, guest-navigation and footer targets. The final 390 × 844 login scan reports no visible interactive target below **44 × 44 px**.
- Added the missing client-dashboard H1, preserved role-aware support headings, and verified native validation moves focus to the first invalid field with browser feedback on login and support.
- Rebuilt production CSS, syntax-checked all seven changed JavaScript controllers and completed **494 tests: 494 passed, 0 failed, 0 skipped**. Cache-safe delivery is **`20260717p15`**.
- NVDA is not installed. Windows Narrator is available, but its spoken audio cannot be captured or judged reliably by the automated browser session. Audible reading order, labels and live announcements therefore remain a supervised manual release gate rather than a claimed pass.

### Phase 3 — final supervised release gate attempted

- Launched Windows Narrator against the real Chrome sign-up window. Windows UI Automation exposed the banner, main landmark, ordered H1, named Client/Trainer/Gym links and footer navigation; Narrator's visible scan highlight advanced through the account-type links in DOM order. Narrator was exited after the check.
- Re-ran all critical client, trainer, gym-admin and platform-admin routes at **390 × 844**. Each checked route retained one H1, one main landmark, no page-level horizontal overflow and no unnamed visible controls.
- Reproduced and repaired the remaining target-size defects: authenticated brand links, client inline dashboard actions, trainer inbox actions, gym-application actions and platform feedback actions now meet the intended target contract. The shortest client inline action resolves to **45 × 45 px** in the final mobile browser.
- Added explicit labels for platform feedback status/reply fields and cache-versioned the shared inbox controller so the repaired action classes cannot be hidden by a stale browser script.
- Rechecked the calendar drawer and Workout Studio custom-exercise panel: initial focus, Tab/Shift+Tab containment, Escape close, closed-state `inert`/`aria-hidden` and opener focus return all pass.
- Rechecked forced colours with reduced motion on the populated Dev Hub table. Its caption, four headers and 18 table rows including the header remain exposed, and keyboard focus receives a visible **3 px** system-colour outline.
- Rebuilt production CSS, syntax-checked seven JavaScript controllers and completed **495 tests: 495 passed, 0 failed, 0 skipped**. Cache-safe delivery is **`20260717p17`**, HTTP 200 remains available on port 8081, and the active local database is H2.
- Phase 3 is **not formally closed**: this environment cannot hear or assess Narrator speech, NVDA is not installed, and no physical touch device is exposed. Audible landmark/label/validation/live-announcement confirmation and representative real-device touch/reflow/fixed-control clearance remain the final human gate.

### Phase 3 — remote continuation and reproduced-defect repairs

- Continued the human gate on **18 July 2026**. Windows Narrator was started with the correct system toggle, **Ctrl + Windows key + Enter**. The user reproduced duplicate login validation speech caused by overlapping visible-label, placeholder and browser-native validation output.
- Replaced browser-native login validation with one explicit focus-linked message, removed the duplicate username placeholders and retained the correct textbox role announcement. The user retested the empty login submit with Narrator and confirmed **PASS**. This is the only newly completed audible result; the wider Narrator/NVDA journey was explicitly deferred until the user is back at the computer.
- Because the user was working remotely, used real Chrome, Edge and WebKit browser sessions as the strongest available touch substitute at **390 × 844**. Login validation, role dashboards, calendar, inbox, workouts, goals, support and representative gym/platform operational pages retained one H1, one main landmark, named controls and no page-level horizontal overflow.
- Reproduced and repaired a **32 px** onboarding-tour Skip action. It now computes to a **44 px** minimum in all four role sessions and retains labelled modal semantics.
- Reproduced Charlie covering roughly **50 × 38 px** of the final trainer and gym dashboard Inbox card at maximum scroll. The shared shell now reserves the floating-control height plus its existing gap; both final cards end above Charlie with **zero overlap**.
- Rechecked the critical routes at **1280 × 900** after explicitly completing the demo onboarding tour. Every requested URL returned HTTP 200, loaded **`20260718p20`**, retained one role-appropriate H1 and main landmark, and had no unnamed visible controls or horizontal overflow.
- Rebuilt production CSS, passed `node --check` for the seven relevant JavaScript controllers and completed **496 tests: 496 passed, 0 failed, 0 skipped**. HTTP 200 remains available on port 8081 using local H2.
- Phase 3 remains **not formally closed**. Real physical-device touch/GPU behaviour and the remaining audible landmarks, headings, names, live announcements and modal/drawer focus output still require short human confirmation when the user is back at the computer.

### Phase 3 — human gate resumed remotely

- Resumed the supervised gate on **19 July 2026** from the already-passed login validation announcement. With Narrator scan mode, the user confirmed **PASS** for the login page's “Welcome back” level-one heading.
- The following main-landmark announcement check was skipped at the user's request, and the remaining Narrator/NVDA checks remain pending rather than failed.
- Offered the first physical website check through the verified LAN login URL, covering portrait reflow and the Client/Trainer/Gym touch targets. The user confirmed they were remote and could not access the physical-device gate; no physical result or defect was recorded.
- Restarted the local H2 application after the previous process ended overnight. HTTP 200 and **`20260718p20`** are again live on port 8081.
- Phase 3 remains **not formally closed**. The next human session should resume with either the skipped main-landmark announcement or the first physical login reflow/touch check, depending on which testing environment is available.

### Phase 3 — supervised gate resumed locally

- Resumed on **27 July 2026** from the human-confirmed login-validation result. Narrator's visible scan cursor reached the page landmark and the Home H1 in the expected order, but this environment cannot hear or assess the spoken output, so those observations are not recorded as audible passes.
- Verified Charlie's Escape path closes the panel and restores focus to its `Open Charlie` trigger. The onboarding tour visibly trapped Tab focus between its actions; a password-manager extension interfered with the post-close browser focus observation, so extension focus was not treated as an application defect.
- Reproduced two calendar live-announcement defects in both month and week views: `Jump to today` announced `Jumped to selected date`, and temporarily disabling the jump controls dropped keyboard focus onto the document body.
- Repaired the two calendar controllers so the polite live region now announces `Jumped to today` and focus returns to the activated jump control. A fresh Chromium context verified the final month and week results.
- Reproduced stale delivery of the repaired calendar controllers because their script tags lacked the shared asset token. Month and week now use the versioned script contract, covered by `RoleAwareSharedSurfaceContractTest`, and the shared token is **`20260727p22`**.
- Windows reports the NVIDIA GeForce RTX 3070 Ti and Intel UHD Graphics 770 as healthy. No HID touchscreen/digitiser is exposed, Android device tooling is unavailable, and NVDA is not installed; no physical touch or NVDA pass is claimed.
- JavaScript syntax checks passed and the complete Gradle suite finished with **497 tests: 497 passed, 0 failed, 0 skipped**. The local app remains available on port 8081 with the `local` profile and H2.
- A supplementary all-role standards matrix generated a partial report before exceeding its ten-minute runner limit. It was stopped and is not counted as a pass or failure; the previously completed cross-role browser evidence remains the baseline.
- Phase 3 remains **not formally closed**. James must still listen to the remaining Narrator announcements and run the representative client, trainer, gym and admin journeys on a physical touchscreen device.

### Phase 3 — performance, accessibility and responsive release gate complete

- James confirmed that the preceding human release gate passed; Phase 3 is now formally complete.
- Added `tools/qa/playwright-release-gate.mjs` and `npm run qa:release` for repeatable responsive, Axe, cold-cache throttled-performance and Lighthouse checks across representative public, login, client, trainer, gym and admin journeys.
- Repaired only reproduced defects: invalid disclosure ARIA, focusable content inside hidden dashboard panels, incomplete tab semantics, an empty tooltip, scroll-region focus, two sub-AA emerald treatments, a distorted mobile logo, automatic geolocation prompting and segmented-code paste cancellation.
- Reduced the two demo profile assets responsible for the cold-load regression from **3.50 MB to 133 KB** and **3.44 MB to 128 KB**; the shared nav logo fell from **118 KB to 24 KB**.
- The final cold-cache Slow 4G/4× CPU medians are FCP **868–1,228 ms**, LCP **1,100–1,948 ms**, load **1,065–2,315 ms** and CLS **0–0.001**. Gym load fell from **18.1 s to 1.86 s**.
- Final automated results: **88/88 responsive cases**, **22/22 Axe cases**, **6/6 throttled journeys**, **6/6 Lighthouse journeys** and **499/499 Gradle tests** passed. The app remains live on port 8081 using local H2.

### Phase 4 — inventory and isolated transactional integration

- Completed a read-only inventory of direct Java/npm dependencies, deployment configuration, environment variables, provider endpoints, callback/webhook routes, scheduled jobs, uploads, storage, sessions and production data stores. The durable evidence is in `PHASE4_PRODUCTION_READINESS.md`.
- Ran all transaction checks in isolated local JAR processes with disposable H2. Stripe used the application simulation path, email was disabled/no-op, SMS was console-only and AI was disabled. No charge, refund, real provider message, production upload or production-data mutation occurred.
- Completed representative client simulated-merch, trainer conversation/message, gym trainer-invite and platform-admin trainer-approval journeys.
- Reproduced and repaired five defects: webhook CSRF interception, unlimited signed-webhook age, a trainer Message GET/POST mismatch, platform-admin denial from the trainer-verification queue, and empty-host SMTP selection.
- Added integration/contract coverage for each repaired boundary. The full suite now reports **505 tests passed, 0 failed, 0 skipped** across 126 suites.
- Rebuilt production CSS and ran the final-code release gate against an isolated server: **88 responsive cases**, **22 Axe cases**, **6 Slow 4G/4× CPU journeys** and **6 Lighthouse journeys** passed with zero findings.
- Final Phase 4 Lighthouse scores are public **94/100/100/100**, login **98/100/100/100**, client **85/100/100**, trainer **90/100/100**, gym **91/100/100** and admin **93/100/100** for performance/accessibility/best practices, with SEO included for public/login.
- The production decision is **NO-GO**. P0 blockers are the known shared-password production demo seed, ephemeral uploads, absence of an isolated staging/provider boundary, unproved real Stripe test-mode and email/SMS lifecycles, and missing migration/backup/restore/rollback evidence.

### Phase 4 — local P0 hardening and staging design

- Reproduced the Render profile's automatic `render-data.sql` execution and removed the production demo seed, including the shared-password platform-admin account.
- Added Flyway's PostgreSQL module and `V1__baseline_schema.sql`; Render now disables Spring SQL initialisation, validates the schema through JPA and uses a versioned migration history. Local/test H2 explicitly keeps Flyway disabled.
- Reproduced the mismatch between configurable upload directories and the fixed `file:uploads/` resource root. Each public upload route now serves from its matching configured storage directory.
- Added the unapplied `render-staging.yaml` proposal and `Web_App/docs/phase4-staging-runbook.md`. The design uses a dedicated staging Hobby workspace, Starter web service, fresh Basic-256mb PostgreSQL 17 database with public database access blocked, and a 1 GB persistent upload disk. Automatic deploys and all external providers are disabled initially.
- Inspected current Render pricing. The minimum durable staging design is approximately **US$13.55/month**: US$7 web compute, approximately US$6 database compute, US$0.30 database storage and US$0.25 persistent-disk storage, before usage overages. No resource was created.
- The first full run reproduced one Flyway/H2 test-profile collision. Explicit test-profile isolation repaired the root cause; the final result is **508 passed, 0 failed, 0 skipped** across 127 suites.
- Production CSS rebuilt successfully. The final local release gate on port 8094 passed **88 responsive cases**, **22 Axe cases**, **6 throttled-performance journeys** and **6 Lighthouse journeys** with zero findings.
- No Render workspace/service/database/disk, production data, real charge, real message, provider credential or webhook was accessed or changed. The production decision remains **NO-GO** until James approves staging cost and the external provider, upload persistence, PostgreSQL migration and recovery drills pass there.

## Important implementation files

### Project evidence and handoff

- `PROJECT_HANDOFF.md` — this continuation point.
- `PHASE4_PRODUCTION_READINESS.md` — external-boundary inventory, transactional evidence, launch blockers and go/no-go checklist.
- `render-staging.yaml` — unapplied isolated staging Blueprint proposal.
- `Web_App/docs/phase4-staging-runbook.md` — approval boundary, provider test plan and recovery/rollback procedure.
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
- `Web_App/src/main/resources/static/css/components/misc/public-sections.css`
- `Web_App/src/main/resources/static/css/entries/`
- `Web_App/src/main/resources/static/css/bundles/`
- `Web_App/src/main/resources/static/css/app.css`
- `Web_App/tools/qa/playwright-release-gate.mjs`

### Shell ownership and fixed surfaces

- `Web_App/src/main/resources/static/js/core/overlay-manager.js`
- `Web_App/src/main/resources/static/js/core/navbar-page.js`
- `Web_App/src/main/resources/static/js/core/platform-panel.js`
- `Web_App/src/main/resources/static/js/core/quick-actions.js`
- `Web_App/src/main/resources/static/js/chat/chat.js`
- `Web_App/src/main/resources/static/js/dashboard/client-dashboard-page.js`
- `Web_App/src/main/resources/static/js/public/public-sections.js`
- `Web_App/src/main/resources/static/js/auth/login-page.js`
- `Web_App/src/main/resources/static/css/components/tutorial/site-tour.css`
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
- `Web_App/src/test/java/uk/ac/cf/_5/group14/One_To_One/SecurityTests/PublicPageConsistencyContractTest.java`
- `Web_App/src/test/java/uk/ac/cf/_5/group14/One_To_One/SecurityTests/LoginPageAccessibilityContractTest.java`
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
npm run qa:release
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

Continue **Phase 4 — staging and launch-blocker clearance**.

The read-only inventory, isolated local transactions, confirmed repairs and automated release gates are complete. The next priority is to establish the safe external boundary needed for real integration proof.

Priority order:

1. Obtain James's explicit approval for the approximately **US$13.55/month** minimum staging cost.
2. Create a dedicated staging Render workspace and apply the reviewed staging Blueprint there only.
3. Prove the fresh PostgreSQL Flyway V1 result contains no demo users and that uploads survive a staging redeploy.
4. Configure SMTP/Twilio/Stripe sandbox credentials only, then prove delivery, failures, retries and duplicate webhook handling.
5. Complete logical backup/restore and application rollback. Obtain separate approval before any billable Render PITR recovery instance.
6. Rerun the complete 508-test baseline and `npm run qa:release` against the final staging release candidate.

## Phase 4 gates still required

- Stripe test-mode payment completion, renewal, cancellation, failure, retry and duplicate webhook delivery.
- External SMTP/Twilio sandbox verification and password-recovery delivery.
- Durable upload/storage limits, validation, redeploy recovery and deletion.
- Production-like PostgreSQL migration, backup, restore and rollback proof.
- Health monitoring, provider observability, privileged audit and operational ownership.

## Rules for future updates to this file

After each implementation phase:

1. Update the date, current phase and next step at the top.
2. Record the exact full test count and failures/skips.
3. Record any CSS/JS version-token change.
4. Add the main files introduced or changed.
5. Record browser sizes and routes verified.
6. Clearly distinguish completed work from release QA still required.
7. Preserve previous phase summaries so the document remains a useful project history.

