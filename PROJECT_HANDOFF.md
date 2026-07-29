# One To One — project handoff and continuation point

**Last updated:** 29 July 2026
**Repository:** `G:\No OneDrive Work\My Website\Crystal-Powers-OneToOne\One To One\One-To-One`
**Application:** Spring web app in `Web_App`  
**Current branch:** `James/phase4-staging-readiness`
**Current phase:** Phase 4 launch controls; persistent saved-card encryption, configuration ownership, accurate provider wording and required CI are complete, while real provider credentials remain deferred to the final pre-launch gate
**Local preview:** `http://localhost:8081`

## Purpose

This file is the durable reference point for continuing the One To One project on another device or in a new Codex session. Read this file first, then read `ONE_TO_ONE_UX_AUDIT.md` for the full evidence, defect inventory and phased plan.

## Transfer status

The Phase 4 provider/recovery implementation and this handoff belong on
`James/phase4-staging-readiness`. Confirm that the latest branch revision is
present before continuing on another device.

## Resume prompt for a new Codex session

Use this prompt after opening the repository on the new device:

> Read `PROJECT_HANDOFF.md`, `PHASE4_PRODUCTION_READINESS.md`, `ONE_TO_ONE_UX_AUDIT.md`, `Web_App/docs/phase4-staging-runbook.md`, `Web_App/docs/phase4-environment-secret-manifest.md` and `Web_App/AGENTS.md`. Preserve all existing work. Continue Phase 4 with the remaining launch decisions: decide whether production accepts a documented maintenance handover or requires explicitly approved two-instance Render topology, and assign named primary and backup production monitoring/security owners. Keep the SMTP, Twilio and Stripe values at the intentional `"2bd"` placeholders until the final pre-launch provider gate. Do not use real recipients, make real charges, alter production webhooks, create billable resources or access the existing `public` data without explicit approval.

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
- Latest full Gradle result: **568 tests passed, 0 failed, 0 skipped** across 146 suites.
- Saved provider tokens are AES-256-GCM ciphertext at rest. Render fails closed
  without its persistent key and verifies the continuity marker plus every
  saved token at startup. Deploy `dep-d9l03oj7uimc7389hllg` verified one
  synthetic saved token after a controlled restart without changing its
  ciphertext fingerprint; the synthetic account and card were then deleted.
- The configuration/secret inventory and rotation/redaction controls are
  versioned in `Web_App/docs/phase4-environment-secret-manifest.md`.
- Provider-facing admin wording now accurately says email delivery was
  attempted synchronously and is not tracked; it no longer claims a queue.
- GitHub Actions run `30456277694` passed with the pinned Java 21/Node 22
  toolchain. The strict `Release gate` status is required on `main`.
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
- Added the unapplied `render-staging.yaml` proposal and `Web_App/docs/phase4-staging-runbook.md`. After James directed reuse of the existing populated `1to-one` PostgreSQL instance, the design was revised to isolate staging in the `one_to_one_staging` schema. Flyway, Hibernate and the JDBC `currentSchema` now share that explicit boundary. The Starter web service remains separate, uses a 1 GB persistent upload disk, disables automatic deploys and starts with all external providers disabled.
- Captured an exact read-only `public` baseline before staging: 6 users, 12 user-role links, 42 support requests, 2 trainer profiles, 1 gym profile, 4 platform subscriptions, 7 mobile authentication tokens and 1 waitlist email. These counts must remain unchanged by staging provisioning and migration drills.
- Inspected current Render pricing. James approved the original **US$13.55/month** ceiling. Reusing the existing database reduces the expected incremental minimum to approximately **US$7.25/month**: US$7 web compute and US$0.25 persistent-disk storage, before usage overages. The empty `One To One Staging` Hobby workspace was created, but no service, database or disk was created there.
- The first full run reproduced one Flyway/H2 test-profile collision. Explicit test-profile isolation repaired the root cause. Four database-schema boundary tests were then added; the final result is **512 passed, 0 failed, 0 skipped** across 128 suites.
- Production CSS rebuilt successfully. The final local release gate on port 8094 passed **88 responsive cases**, **22 Axe cases**, **6 throttled-performance journeys** and **6 Lighthouse journeys** with zero findings.
- No Render service/database/disk, real charge, real message, provider credential or webhook was changed. A read-only inventory confirmed that the existing database's `public` schema is populated, so staging is prohibited from targeting it. The production decision remains **NO-GO** until the schema boundary, external providers, upload persistence, PostgreSQL migration and recovery drills pass.

### Phase 4 — isolated Render staging and migration proof

- Created the approved `one-to-one-staging-jhuds` Starter web service (`srv-d9kct35aeets73ant7k0`) in the existing `one-to-one` Render environment. Automatic deploys are disabled. A 1 GB persistent disk (`dsk-d9kct35aeets73ant8ag`) is mounted at `/var/data/uploads`. Incremental minimum cost is approximately **US$7.25/month**.
- Reused PostgreSQL `1to-one` only through `one_to_one_staging`. The first clean boot applied Flyway V1 with zero users and no demo seed. Controlled forward upgrades then applied V2–V4. The current staging state is one synthetic client, five successful Flyway history rows including schema creation, current version **4**, and one durable profile-upload reference.
- Reproduced four PostgreSQL/Hibernate defects during real staging boots and repaired only those failures: conflicting legacy/V2 `chat_messages` ownership, missing `chat_threads.chat_type` and `peer_user_id`, incompatible health-record numeric types, and `saved_payment_methods.last_four` being fixed-width instead of variable-width. Mapping/migration contract coverage was added.
- Deployed commit `50a2be4597db48e87857ec02793da213e33cef89`. Deployments `dep-d9kd8e5g1s2s73fsq100` and `dep-d9kdebrm8hqs73c7rlhg` reached live. A later rollback to the earlier successful artifact also reached live without a database downgrade.
- Created and verified a staging-only client through sign-up, verification and login while email remained disabled. No real message was sent. A profile upload returned HTTP 200 before and after redeploy and rollback with the same **139,305-byte** size and SHA-256 `054f1b7337602ac967057876fe0f166b9ce9a7d9ba8d80498f22a391605a738f`.
- The `public` fingerprint remained exactly unchanged after schema creation, migrations, the synthetic transaction, redeploy and rollback: 6 users, 12 user-role links, 42 support requests, 2 trainer profiles, 1 gym profile, 4 platform subscriptions, 7 mobile authentication tokens and 1 waitlist email.
- Render completed an on-demand logical export at **28 July 2026 17:28 BST** and retains it for seven days. It was not downloaded because it includes the out-of-scope `public` schema. Render PITR always creates a separate billable database, so no restore was started without explicit approval.
- The final local verification passed: production CSS build; **513/513 Gradle tests** across 129 suites; **88/88 responsive cases**; **22/22 Axe cases**; **6/6 throttled journeys**; and **6/6 Lighthouse journeys**, with zero findings.
- No Stripe, SMTP or Twilio credential is configured. No real charge, refund, email, SMS, production webhook or production-service setting was changed. The decision remains **NO-GO** pending provider sandbox lifecycle proof, an approved isolated restore, and the remaining operational P1 decisions.
- A follow-up provider-lifecycle attempt on 28 July 2026 rechecked the live Render environment rather than trusting the handoff assertion. `APP_EMAIL_PROVIDER` was still `none`, `APP_SMS_PROVIDER` was still `console`, and no `SPRING_MAIL_*`, `TWILIO_*`, `STRIPE_SECRET_KEY` or `STRIPE_WEBHOOK_SECRET` keys, secret files or linked environment groups existed on `one-to-one-staging-jhuds`. No provider request was made and no lifecycle pass is claimed.
- The Render recovery customisation screen confirmed the lowest selectable recovery configuration as Basic-256mb at **US$6/month** plus 1 GB storage at **US$0.30/month**, for an exact **US$6.30/month**, billed and prorated by the second. The default copied 15 GB storage configuration would be **US$10.50/month**. The form was inspected only; `Create Database` was not activated.

### Phase 4 — provider and recovery continuation

- Forced a staging-only redeploy after provider variables were added and
  confirmed that the new process selected SMTP and Twilio rather than the
  previous no-op/console implementations.
- SMTP verification and password-reset delivery both remain blocked because
  the configured staging mail host is invalid. The password-reset page
  correctly retained its generic anti-enumeration response, but no test-inbox
  delivery occurred.
- Added the documented Twilio test sender to staging after reproducing the
  missing-sender failure. The request then reached Twilio and returned
  authentication error 20003, proving that the configured test Account
  SID/auth token is invalid. No SMS was sent.
- Stripe checkout reached Stripe in test configuration and returned HTTP 401
  for an invalid API key. No Checkout Session, subscription or charge was
  created, and no production webhook was touched.
- Reproduced three Stripe lifecycle defects in tests before editing: duplicate
  event IDs were processed twice, invoice failure/recovery events were ignored,
  and cancellation changed only the local database. Added a persistent webhook
  event ledger (Flyway V5), payment failure/recovery state transitions and
  provider-first cancellation so local state is not committed after a provider
  failure.
- With James's separate approval, created temporary recovery database
  `one-to-one-phase4-recovery-20260728`
  (`dpg-d9kgb35aeets73aupr70-a`) at the approved Basic-256mb/1 GB
  **US$6.30/month prorated-by-the-second** configuration. The isolated restore
  reached available, reported successful schema creation and Flyway V1–V4,
  122 base tables, one expected synthetic user and one profile-upload
  reference. No `public` row query was made.
- Deleted the exact temporary recovery database immediately after validation
  and confirmed that only the original source database remains available,
  stopping further recovery-instance charges.
- Final verification passed: `npm run build:css`, `bootJar`, **520/520 Gradle
  tests** across 131 suites, **88/88 responsive**, **22/22 Axe**, **6/6
  throttled** and **6/6 Lighthouse** cases with zero findings. Latest
  Lighthouse scores are public **93/100/100/100**, login
  **98/100/100/100**, client **83/100/100**, trainer **91/100/100**, gym
  **89/100/100** and admin **94/100/100**.
- Commit `f99024cf` was pushed and Render deploy
  `dep-d9kgqgh42hec73doqmkg` reached live. Startup validated six Flyway
  history entries, applied V5 successfully to `one_to_one_staging`, and served
  the public homepage normally.
- Production remains **NO-GO** until valid non-delivering SMTP sandbox,
  Twilio test and Stripe test-mode credentials are installed and their live
  staging lifecycles pass.
- James directed the real provider credential work to remain deferred until
  the final pre-launch gate. With the relevant SMTP, Twilio and Stripe
  environment values set to the intentional invalid placeholder `"2bd"`, a
  focused **19-test / 6-suite** provider and payment safety run passed with
  zero failures. The live evidence remains the expected safe outcome: SMTP
  cannot resolve the placeholder host, Twilio returns authentication error
  20003, Stripe returns HTTP 401, and no message, Checkout Session,
  subscription, charge or local cancellation success is created.

### Phase 4 — chat, merchandise and workout storage acceptance

- Reproduced on the live isolated staging service that chat images and workout
  videos were served as public static resources: the owner, an unrelated
  authenticated client and an anonymous request all received HTTP 200. The
  merchandise image remained intentionally public.
- Repaired only the reproduced private-read defect. Chat and workout files now
  pass through authenticated owner checks, return HTTP 401 to anonymous
  requests and HTTP 404 to another owner, and use `Cache-Control: no-store`.
  Profile and merchandise image routes remain public.
- Chat upload now rejects more than five files rather than silently truncating,
  rejects files over 4 MiB, removes partial writes after failure and refuses a
  stored attachment URL owned by another user. Clearing a conversation removes
  only that owner's durable chat files.
- Merchandise replacement/deactivation now removes an unreferenced stored
  image, retains images referenced by order snapshots and removes a newly
  written file when repository persistence fails.
- Workout form video upload now enforces an 8 MiB limit, retains the MP4/WebM
  signature checks, removes a written file after persistence failure and
  exposes an owner-scoped deletion journey in the workout player.
- The global multipart transport limit is explicitly **8 MB per file** and
  **25 MB per request**, with a bounded **32 MB Tomcat swallow limit** so
  rejected clients receive structured HTTP 413 JSON. Real embedded-Tomcat
  coverage proves 8 MiB acceptance, 8 MiB + 1 byte rejection and aggregate
  request rejection above 25 MiB.
- Synthetic staging fixtures were created only in `one_to_one_staging`. Before
  deploy, chat and merchandise images were both 23,044 bytes with SHA-256
  `d08fc3b55a4a7d1c50c77f8929cd7ac0ca69656652f9bab9fc19f11510fa613a`;
  the synthetic workout video was 24 bytes with SHA-256
  `c8c5af84ac765d911a9ab05bc9a19d15d0b1bc5cf0654eff4469ce536410654e`.
- Commit `7c4fce55` was pushed and Render deploy
  `dep-d9kst0rl550s73f6kdmg` reached live. All three files retained identical
  sizes and hashes after the redeploy. Owner reads returned 200, unrelated
  chat/workout reads returned 404, anonymous chat/workout reads returned 401,
  and anonymous merchandise read returned 200.
- Application deletion passed for all three boundaries; every durable fixture
  subsequently returned 404 and workout latest returned `NONE`. The three
  labelled users, workout template and merchandise product were then removed
  from the staging schema, with zero labelled rows remaining.
- Final verification passed: `npm ci`, `npm run build:css`, `bootJar`,
  **544/544 Gradle tests** across 135 suites, **88/88 responsive**, **22/22
  Axe**, **6/6 throttled-performance** and **6/6 Lighthouse** journeys, with
  zero release-gate findings.
- No `public` row was queried or changed during this work. The existing service,
  PostgreSQL instance, staging schema and disk were reused; no resource cost
  was added. The `"2bd"` provider placeholders and production service/webhooks
  were unchanged.

### Phase 4 — operational health, shared state and audit ownership

- Reproduced that aggregate Actuator health, readiness and liveness all
  returned HTTP 401 and that Render was using `/login` as its health check.
  Added status-only public `/actuator/health/liveness` and
  `/actuator/health/readiness` contracts; aggregate `/actuator/health` remains
  protected. Readiness includes application availability, PostgreSQL and disk
  checks without exposing component details.
- Configured the existing staging service health path to
  `/actuator/health/readiness`. Live probes return HTTP 200 with
  `{"status":"UP"}`, while aggregate health remains HTTP 401.
- Reproduced that a valid authenticated session and login-attempt counters were
  lost on application restart. Replaced both with PostgreSQL-backed state:
  Spring Session JDBC uses `SESSION` cookies and the login throttle stores only
  SHA-256 identity/network keys in `login_attempts`.
- Reproduced restart behaviour twice. The same synthetic authenticated session
  returned the client dashboard after both redeploys, and a staged throttle
  remained effective after restart. Raw usernames and source addresses are not
  retained in the throttle table.
- Added database-owned leases for every scheduled job. A job now fails closed
  when ownership cannot be acquired, preventing concurrent execution after
  horizontal scale-out.
- Added retained audit events for mutating `/admin/`, `/super-admin/` and
  `/gym/` actions. Evidence records actor, roles, path, method, response,
  result, timestamp, request ID and a hashed source address; request bodies and
  query strings are excluded. Retention is 180 days.
- A live staging denial probe reproduced that an `/access-denied` redirect was
  initially classified as successful. Commit `b79f79bb` repairs that exact
  outcome and adds regression coverage.
- The subsequent full-suite run exposed that Spring could not bind the
  scheduler annotation when a real scheduled proxy fired. Commit `1fabec8e`
  resolves the annotation from the concrete target method, fails closed if it
  cannot be resolved and adds real AOP proxy coverage. Final Render deploy
  `dep-d9kvft2d0e5s73egun20` contains both fixes.
- Flyway V6 creates the Spring Session, login-throttle, scheduler-lease and
  privileged-audit tables. Validation was restricted to
  `one_to_one_staging`; no `public` row or production provider configuration
  was accessed.
- James is the interim staging monitor, incident commander and privileged-audit
  access owner until named primary and backup production owners are assigned.
  Render workspace failure notifications are active. The response procedure is
  documented in the staging runbook.
- The service still has one Starter instance. A controlled redeploy produced a
  brief HTTP 502 during handover, so a zero-downtime launch requires either an
  explicitly accepted maintenance window or approval for at least two
  instances. No billable scale change was made.
- `npm ci` reported zero vulnerabilities. The workstation used Node 24.18/npm
  11.14 rather than the repository-pinned Node 22.22/npm 11.11; the release
  gate passed, but CI should use the pinned toolchain.

## Important implementation files

### Project evidence and handoff

- `PROJECT_HANDOFF.md` — this continuation point.
- `PHASE4_PRODUCTION_READINESS.md` — external-boundary inventory, transactional evidence, launch blockers and go/no-go checklist.
- `render-staging.yaml` — staging Blueprint reference; the live service was configured manually in Render.
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

Continue **Phase 4 — launch decision and final provider gate**.

The isolated Render service, schema boundary, clean and forward migrations,
all four upload boundaries, logical export, application rollback, isolated
restore, authentication-safe health checks, shared sessions/throttles,
scheduled-job ownership, privileged audit retention and automated gates are
complete. Real provider proof remains a mandatory final pre-launch gate but is
intentionally deferred.

Priority order:

1. Decide whether production accepts a documented maintenance handover or
   requires two Render instances for zero-downtime deployment. Scaling is
   billable and requires explicit approval.
2. Assign named primary and backup production monitoring/security incident
   owners; James remains the interim staging owner only.
3. At the final pre-launch gate, replace the invalid provider placeholders and
   prove the deferred SMTP, Twilio and Stripe lifecycles, then rerun every
   release gate.

## Phase 4 gates still required

- Stripe test-mode payment completion, renewal, cancellation, failure, retry and duplicate webhook delivery.
- External SMTP/Twilio sandbox verification and password-recovery delivery.
- Production topology and named primary/backup incident ownership.
- Final sandbox-provider lifecycle proof with real test credentials.

## Rules for future updates to this file

After each implementation phase:

1. Update the date, current phase and next step at the top.
2. Record the exact full test count and failures/skips.
3. Record any CSS/JS version-token change.
4. Add the main files introduced or changed.
5. Record browser sizes and routes verified.
6. Clearly distinguish completed work from release QA still required.
7. Preserve previous phase summaries so the document remains a useful project history.

