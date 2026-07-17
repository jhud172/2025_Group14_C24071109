# One To One — full-site UX, motion and visual-quality audit

**Audit date:** 13 July 2026; implementation status updated 15 July 2026
**Environment:** local Spring application at `http://localhost:8081`  
**Status:** audit complete; Phase 0, Phase 1 and website stabilisation are complete; Phase 2 is in progress

## Completion snapshot

| Audit stream | Status | Result |
|---|---|---|
| Public desktop and mobile | Complete | Home, marketing, auth, menu, pricing and trainer discovery checked. |
| Authenticated roles | Complete | Client, trainer, gym admin and platform admin journeys checked. |
| Shared product surfaces | Complete | Inbox, workouts, goals, calendar, profile, Charlie and platform navigation checked. |
| Responsive and motion | Complete | Desktop and 390 × 844 mobile states, overlay timing, overflow and layout transitions checked. |
| Source and automated verification | Complete | Template/CSS/JS inventory, route checks, root-cause tracing and current 483-test Gradle run completed. |
| Phase 0 implementation | Complete | Release-blocking calendar, gym, pricing, dashboard and authenticated mobile-layer defects repaired; 456 tests pass. |
| Phase 1 continuity | Complete | One shared overlay owner, named motion/layer tokens and explicit high-risk transitions implemented; 461 tests pass. |
| Phase 1 performance and shell continuity | Complete | Core CSS reduced by about 83%, feature styles load by route, fixed surfaces share measured reservations, and gzip/cache revalidation is verified; 467 tests pass. |
| Website test and local-startup stabilisation | Complete | Eight failing tests repaired, H2 fixture identities corrected, and local `bootRun` verified against the root `.env`; 467 tests pass and localhost returns HTTP 200. |
| Phase 2 role-aware authentication/onboarding | Complete | Semantic role panels, grouped recovery, destination enforcement and role-specific onboarding implemented; 472 tests pass. |
| Phase 2 four-role dashboard consistency | Complete | Shared typography, measured contrast, action hierarchy and useful empty states applied across client, trainer, gym-admin and platform-admin dashboards; 477 tests pass. |
| Phase 2 role-aware shared surfaces | Complete | Inbox, support, shared-tool intent, platform actions and useful recovery states now match client, trainer, gym-admin and platform-admin roles; 483 tests pass. |
| Assistive technology and physical devices | Release QA required | Screen-reader output, real touch devices, GPU performance, 200%/400% zoom and measured contrast require dedicated hardware/tool passes. |

**Next implementation step:** continue Phase 2 by shortening long public pages and standardising section transitions with the existing motion tokens.

## Phase 2 implementation update — third workstream complete

Completed on 15 July 2026:

- Added a server-side role-surface model covering client, trainer, gym-admin, platform-admin/super-admin and signed-out support contexts.
- Updated the shared inbox and thread so headings, notification guidance, composer prompts, dashboard links and empty-state actions match the active role.
- Added a dedicated `/support` page and corrected gym support navigation; platform admins retain a distinct operational support inbox with a useful empty state.
- Restricted platform-panel defaults to role-valid actions and destinations, with saved selections scoped by role.
- Clarified workout and goal intent for coaching versus operational accounts and repaired the hidden custom-exercise dialog's inert, Escape and focus-return behaviour.
- Added shared role-surface CSS with 44 px actions, explicit focus/hover states, explicit transition properties and a reduced-motion path.
- Added `RoleAwareSharedSurfaceContractTest` and updated route contracts.
- Rebuilt production CSS. Full Gradle suite: **483 tests, 483 passed, 0 failed, 0 skipped**.
- Browser-checked client inbox, trainer workout studio, gym support and platform-admin support inbox at **1280 × 900** and **390 × 844**, including each role's action tray.
- Current delivery token: **`20260715p8`**.

## Phase 2 implementation update — second workstream complete

Completed on 15 July 2026:

- Introduced a shared `.app-dashboard` contract for page and section typography, measured text colours, surfaces, metrics, focus rings, action hierarchy, fields, tables and empty-state recovery.
- Applied the contract to the client, trainer, gym-admin and platform-admin dashboards without changing their route or workflow behaviour.
- Measured normal-text contrast against the intended surfaces: the lowest ratio is **4.76:1** in light mode and **6.92:1** in dark mode; title, body and accent combinations range up to **17.85:1**.
- Standardised visible dashboard actions to at least 44 px. Client chip links use a 45 px CSS minimum so transformed panels still measure at or above 44 px in the browser.
- Added role-specific empty explanations and recovery actions; restored the client milestone state expected by the dashboard contract.
- Added `DashboardConsistencyContractTest` coverage for contrast calculations, all four role wrappers, hierarchy/action tokens, reduced motion and useful zero-data copy.
- Rebuilt production CSS and browser-checked client, trainer, gym-admin and platform-admin dashboards at **1280 × 900** and **390 × 844**. Each verified route retained one main landmark and no horizontal overflow.
- Full Gradle suite: **477 tests, 477 passed, 0 failed, 0 skipped**.
- Current delivery token: **`20260715p5d`**.

## Phase 2 implementation update — first workstream complete

Completed on 15 July 2026:

- Client, trainer and gym login states now render the correct controls and sign-up route on the server; inactive panels are `hidden`, `inert` and excluded from assistive navigation.
- Trainer/gym access codes have grouped legends, labelled segments, preserved identifiers, role-specific recovery copy and focused invalid-state feedback.
- Authentication now rejects selected-role/account-role mismatches without revealing account information.
- Returning clients, trainers, gym administrators and platform administrators reach their correct dashboards; valid saved requests remain supported.
- Platform administrators now receive admin onboarding and complete it to the admin dashboard.
- Tutorial artwork uses a consistent inline SVG family with stable, reduced-motion-aware transitions.
- Charlie is intentionally absent from focused authentication/sign-up layouts, preventing the mobile credential collision found during browser QA.
- Desktop client/trainer/gym states and the 390 × 844 gym state were browser-checked; the mobile page has no horizontal overflow.
- Changed CSS and JavaScript use cache token `20260715p2a`.
- Focused role-flow tests: **10 tests, 10 passed**.
- Full Gradle suite: **472 tests, 472 passed, 0 failed, 0 skipped**.

## Website stabilisation update — complete

Completed on 14 July 2026:

- Updated stale trainer and gym access codes in the deterministic test fixture.
- Reset generated H2 IDs after fixed-ID seed rows, removing primary-key collisions in goals and trainer schedule-template tests.
- Isolated `local` and `test` profiles from production database variables in `.env` while preserving Render/PostgreSQL startup handling.
- Focused regression set: **13 tests, 13 passed**.
- Full Gradle suite: **467 tests, 467 passed, 0 failed, 0 skipped**.
- Local runtime: **HTTP 200** on port 8081 using profile `local` and in-memory H2.

## Phase 0 implementation update — complete

Completed on 13 July 2026:

- **Calendar rendering:** moved enum decisions out of Thymeleaf and into typed controller booleans/labels. Month and week views now render their complete grids and data.
- **Gym operational access:** linked seeded and newly approved gym-admin users to their gym profile. Trainers and Memberships now open successfully for the gym demo account.
- **Gym support and CTAs:** Support now opens the public support form rather than platform moderation. Gym dashboard actions use explicit, readable primary/secondary styling and mobile-safe hit areas.
- **Trainer pricing:** replaced invalid string binding with a typed formatted price property; directory cards now show values such as `£55 / session`.
- **Client dashboard:** removed the nested main landmark, restored a two-column laptop layout, deferred the third rail to wide screens, and reserved space for the fixed platform panel.
- **Mobile navigation/layers:** added a safe-area-aware internal scroller, scroll lock, background `inert`, focus trapping/return, Escape handling, and removal of Charlie/quick actions/platform controls while the menu owns the layer. Logout remains reachable at 390 × 844.
- **Charlie accessibility contract:** inactive Charlie, quick-actions and platform settings panels now use `inert`; Charlie is the canonical assistant name in global navigation and tests.
- **Existing homepage requests retained:** Charlie stays visible as an animated launcher after closing, Login typography matches the navigation, the 1,600-character message counter remains active, and up to five image attachments remain scrollable/removable.

Verification completed:

- CSS rebuilt successfully and all changed JavaScript files pass `node --check`.
- Focused calendar, gym application and template-route contracts pass.
- Full Gradle suite: **456 tests, 456 passed, 0 failed, 0 skipped**.
- Browser QA confirmed rendered month data, formatted trainer prices, successful gym Trainers/Memberships routes, corrected Support destination, one client-dashboard main landmark, and guest/authenticated mobile menus with hidden competing controls.

## Phase 1 continuity implementation update — complete

Completed on 13 July 2026:

- **Shared overlay ownership:** added one global coordinator for the `site-navigation`, `charlie`, `quick-actions` and `platform-customizer` surfaces. Opening a surface now closes the previous owner through its own accessibility-safe close path rather than simulating unrelated button clicks.
- **Nested modal ownership:** Charlie's media lightbox uses a separate modal group, keeps the assistant as its parent surface, makes the underlying panel inert, restores focus on close, and closes with its parent during a surface handoff.
- **Named layer scale:** replaced four/five-digit escalation in the high-risk global components with the agreed scale: sticky navigation `30`, platform panel `40`, dropdown `50`, modal `70`, assistant `80`, assistant modal `88`, critical toast `90`.
- **Shared motion scale:** introduced Instant `120 ms`, Micro `180 ms`, Panel `300 ms` and Route `380 ms` tokens with standard, entrance and exit easing. Reduced-motion mode collapses every duration token to `0.01 ms`.
- **Explicit high-risk transitions:** removed all 17 `transition: all` / `transition-all` rules from navigation, Charlie and Quick Actions. Those components now animate only transform, opacity, visibility, colour, border and shadow properties that visibly change.
- **Continuous panel states:** navigation, Charlie, Quick Actions, platform settings and the lightbox remain mounted for opacity/transform exits, use inert while closed, expose synchronized `aria-hidden` / `aria-expanded` state, and return focus for explicit close or Escape actions.
- **Regression guard:** added a source contract that fails if broad transitions or escalating layer values return to the four global overlay stylesheets.

Verification completed:

- CSS rebuilt successfully; all five changed JavaScript controllers pass `node --check`.
- Phase 1 continuity contract: **5 tests, 5 passed**.
- Full Gradle suite: **461 tests, 461 passed, 0 failed, 0 skipped**.
- In-app browser QA passed for guest, premium client and gym-admin states at `390 × 844`, `800 × 900` and `1440 × 900`.
- Live handoff checks confirmed Quick Actions → Charlie → platform settings, Charlie → tablet navigation, mobile full-screen settling, closed-panel inert state, Escape focus return and computed layer values `30 / 40 / 80 / 88`.
- The updated application is running at `http://localhost:8081` with `app.css?v=20260713e` and the shared overlay manager loaded.

## Phase 1 performance and shell continuity implementation update — complete

Completed on 13 July 2026:

- **Smaller shared CSS core:** split the previous all-surface stylesheet into a 207.4 KiB minified core plus conditional assistant, authenticated-shell, auth, calendar, content, dashboard, profile and training bundles. The core fell from about 1.24 MiB to 207.4 KiB—an approximately **83% reduction**—and transfers at about **34 KiB with gzip**.
- **Route-aware delivery:** added one server-side bundle map so templates request only the feature families required by the current route. Public home, login, dashboard and calendar checks each loaded the expected core and optional bundles without pulling every product surface.
- **Repeatable production build:** the CSS pipeline now builds the core and feature entries together, minifies production output with cssnano, and removes invalid safelist patterns that produced warnings without preserving any real utility classes.
- **One fixed-panel reservation contract:** introduced shared platform-panel, local-dock, safe-area and floating-control variables. The platform rail and client mobile dock measure their rendered heights; page padding, Charlie and Quick Actions consume the same values instead of maintaining separate hard-coded offsets.
- **Continuous responsive shell:** desktop, tablet and `390 × 844` mobile checks showed no horizontal overflow. On client mobile, the dashboard dock retains a 9.6 px gap above the platform rail and Quick Actions retains a 10.8 px gap above the dock.
- **Production delivery contract:** static CSS now returns public one-day cache headers, `Last-Modified`, gzip compression and a successful `304 Not Modified` revalidation. Dynamic HTML remains gzip-compressed and `no-store`.
- **Cache-safe rollout:** stylesheet and shell-script URLs now share the `20260713p1b` version so existing browser caches cannot mix old layout controllers with the new reservation contract.

Verification completed:

- CSS core and all eight feature bundles rebuilt successfully; npm audit reports **0 vulnerabilities**.
- JavaScript syntax checks pass for the platform-panel and client-dashboard reservation controllers.
- Phase 1 performance contract: **6 tests, 6 passed**.
- Full isolated Gradle suite: **467 tests, 467 passed, 0 failed, 0 skipped**.
- In-app browser QA passed on public home, login, authenticated dashboard and calendar at desktop, tablet and mobile sizes, including conditional bundle loading, fixed-surface gaps and overflow checks.

## Executive verdict

**Overall health: Amber/Red, with five release-blocking defect groups.**

The public site has a strong visual direction: clean typography, generous spacing, restrained emerald accents, readable cards and a mostly convincing premium feel. Trainer and admin surfaces also show a usable design system. The experience stops feeling like one continuous product when it reaches the client dashboard, calendar and global overlay layer. Those areas introduce clipping, blank content, fixed-panel collisions, inconsistent states and visible rendering seams.

The fastest path to the requested “fluent river” feel is not more animation. It is to stabilize layout first, consolidate motion second, then add a small number of purposeful transitions. The current codebase already contains **370 broad `transition: all` / `transition-all` uses, 316 animation declarations, 198 keyframe blocks and 53 fixed-position rules**. There is enough motion machinery; it needs governance and simplification.

### Health by surface

| Surface | Health | Summary |
|---|---|---|
| Public home and marketing | Good / Amber | Visually strongest area; long pages, shell seams and CTA inconsistency remain. |
| Mobile public navigation | Amber | Smooth opening, but content bleeds through and Charlie/login compete with the menu. |
| Authentication and signup | Good / Amber | Clear hierarchy; role semantics, errors and typography need tightening. |
| Client dashboard | Critical / Red | Major clipping, cramped columns, nested landmarks and fixed-panel overlap. |
| Client calendar | Critical / Red | Calendar body is blank in the tested client state. |
| Trainer dashboard and clients | Good / Amber | Coherent; contrast, empty-state density and bottom-panel overlap need work. |
| Gym workflow | Critical / Red | Dashboard opens, but Trainers and Memberships return 403, Support opens admin moderation, and mobile CTAs/menu layers clip. |
| Admin dashboard and feedback | Good / Amber | Clear hierarchy; oversized empty states and bottom-panel overlap remain. |
| Charlie assistant | Good / Amber | Current build has the requested counter and five-photo logic; overlay semantics still need QA. |

## Scope and method

- Inventoried **166 templates, 72 CSS files and 80 JavaScript files** across public, client, trainer, gym, admin, shared and system views.
- Tested desktop at **1440 × 900** and mobile at **390 × 844**.
- Exercised Home, Explore, Pricing, signup choice, Login, mobile menu, client tutorial, client dashboard, calendar, trainer dashboard, trainer clients, gym tutorial, gym dashboard, trainers, memberships, support, gym profile, shared inbox, workouts, goals, admin dashboard, admin feedback and Charlie.
- Checked navigation transitions, overlays, fixed panels, responsive overflow, empty states, form feedback, character limits and attachments.
- Public HTTP smoke check: **14 intended public routes returned 200**, including privacy, terms and subscription terms.
- The original audit run used an isolated temporary build directory so the live server could remain running: **455 tests completed, 454 passed and 1 failed**. That stale Charlie naming contract was corrected in Phase 0; the current full suite passes all 456 tests.

This is a full-site system audit with representative journey coverage, not a claim that every state of all 166 templates was manually exercised. Payment completion, real verification, destructive admin actions, screen-reader output and real-device GPU performance were outside this pass.

## Numbered visual audit

### 1. Public home — healthy foundation

![Home desktop](C:/Users/marty/.codex/visualizations/2026/07/13/019f5c66-fb99-74e1-b853-e9626f32fc6f/site-ux-audit/01-home-desktop.png)

**Health: Good.**

- Strong hierarchy, brand consistency, restrained color, good whitespace and clear actions.
- Hover/micro-transition timings generally sit around 180–350 ms and feel controlled.
- The desktop page is about 7,692 px tall and mobile about 12,163 px tall, creating a long journey.
- **Fix:** remove repetitive explanatory sections, reserve media dimensions and use one reveal pattern.
- **Add:** stronger repeated conversion anchors; only add section progress after shortening the page.

### 2. Explore trainers — visible data-binding defect

![Explore desktop](C:/Users/marty/.codex/visualizations/2026/07/13/019f5c66-fb99-74e1-b853-e9626f32fc6f/site-ux-audit/02-explore-desktop.png)

**Health: Critical for trust.**

- A card visibly renders `£card.pricePerSession / session` instead of a price.
- Guest cards expose “Open inbox” and “Request trainer” without enough authentication context.
- “Back to home” duplicates global navigation.
- **Fix:** format the final price in a typed server-side view model and add rendered-template tests for null, integer and decimal prices.
- **Add:** a signed-out “Log in to request” state with a return-to URL.
- **Acceptance:** raw property names or expression fragments can never appear in cards.

### 3. Pricing and signup choice — page-shell seams

![Pricing desktop](C:/Users/marty/.codex/visualizations/2026/07/13/019f5c66-fb99-74e1-b853-e9626f32fc6f/site-ux-audit/03-pricing-desktop.png)

![Signup choice](C:/Users/marty/.codex/visualizations/2026/07/13/019f5c66-fb99-74e1-b853-e9626f32fc6f/site-ux-audit/05-signup-choice-desktop.png)

**Health: Good; role-drift findings implemented and verified.**

- Both pages are clear and easy to scan.
- Black rectangular seams appear at shell edges in several states. The base body is dark while light page layers include transparent gaps.
- The pricing warning has weak yellow contrast and CTA typography drifts from the product sans-serif.
- Signup choice leaves excessive empty height.
- **Fix:** give light pages an opaque full-viewport surface, eliminate shell gaps and bind CTA typography to nav/body tokens.
- **Add:** screenshot checks that inspect all viewport edges for unintended dark seams.

### 4. Login and roles — visually strong, semantically mixed

![Login desktop](C:/Users/marty/.codex/visualizations/2026/07/13/019f5c66-fb99-74e1-b853-e9626f32fc6f/site-ux-audit/04-login-desktop.png)

**Health: Good / Amber.**

- Strong focus, clear card hierarchy and understandable Client / Trainer / Gym selection.
- Hidden trainer-code fields remain in the semantic tree on the Client tab.
- An admin can enter through the Client presentation, then receives client-style onboarding.
- Gym feedback says only “Invalid username or password”, not whether the segmented code group was invalid.
- The source now gives Login `font-family: inherit`; size/weight/line-height should still use the same nav token to prevent build/cache drift.
- **Fix:** make inactive role panels `hidden`/`inert`, use correct tab semantics and route each role to relevant onboarding.
- **Add:** group-level segmented-code validation and neutral credential-combination feedback.

### 5. Mobile home and menu — smooth transition, incomplete layer handling

![Mobile home](C:/Users/marty/.codex/visualizations/2026/07/13/019f5c66-fb99-74e1-b853-e9626f32fc6f/site-ux-audit/06-home-mobile.png)

![Mobile menu open](C:/Users/marty/.codex/visualizations/2026/07/13/019f5c66-fb99-74e1-b853-e9626f32fc6f/site-ux-audit/07-mobile-menu-open.png)

**Health: Amber.**

- No horizontal document overflow; the menu settles cleanly in about 260 ms with transform/opacity.
- Underlying text remains too visible; Login is repeated; Charlie remains available under the menu.
- **Fix:** one modal-layer manager should make the page and Charlie inert, apply an adequate scrim and lock background scroll.
- **Add:** Escape close, focus trap, focus return and reduced-motion instant state.

### 6. Tutorial — clear progression, inconsistent product language

![Client tutorial](C:/Users/marty/.codex/visualizations/2026/07/13/019f5c66-fb99-74e1-b853-e9626f32fc6f/site-ux-audit/08-client-tutorial.png)

**Health: Amber.**

- Progression works without obvious jitter.
- The card leaves a very large inactive backdrop and emoji conflict with the premium icon language.
- Admins receive the client-oriented six-step tutorial.
- **Fix:** use a shared role-aware tutorial component and one icon family.
- **Add:** resumable progress, “Don’t show again” and a preview of the next destination.

### 7. Client dashboard — primary release blocker

![Client dashboard](C:/Users/marty/.codex/visualizations/2026/07/13/019f5c66-fb99-74e1-b853-e9626f32fc6f/site-ux-audit/09-client-dashboard.png)

**Health: Critical.**

- Text/cards are visibly cropped inside a three-column shell even though the document reports no horizontal overflow.
- Multiple `overflow: hidden` containers conceal failure instead of reflowing.
- Desktop rules enforce three narrow columns with minimum widths at laptop sizes.
- The template introduces a second `<main>` inside the base layout’s `<main>`.
- The fixed platform panel overlaps lower content; visual density hides the primary task.
- **Fix:** replace inner `<main>` with a section; keep two columns until at least 1536 px; move the third rail below or into a drawer; remove clipping.
- **Fix:** reserve platform-panel space on the true scrolling container for expanded/collapsed/customized states.
- **Add:** one primary Today area, one secondary progress area and progressively disclosed support/profile rails.
- **Acceptance:** no clipped glyphs, hidden actions or overlap at 1024, 1280, 1366, 1440, 1536 and 1920 px.

### 8. Client calendar — blank core workflow

![Client calendar](C:/Users/marty/.codex/visualizations/2026/07/13/019f5c66-fb99-74e1-b853-e9626f32fc6f/site-ux-audit/10-client-calendar.png)

**Health: Critical.**

- The tested client and gym admin both see the view toggle but no calendar body, proving this is a global calendar defect rather than client data alone.
- The delivered HTML ends immediately inside the Month View label, at the template expression that compares `calendarLayout` with the enum type. The remaining calendar markup and `month.js` script are absent from the response.
- Source contains a full implementation, so the strongest evidence is a server-side Thymeleaf render abort after the response has already been committed, not CSS collapse.
- There is no error or recovery state; the browser receives a partial 200-looking page.
- **Fix:** calculate a plain `calendarLayoutLabel`/boolean in the controller instead of resolving the enum type in the template; make render exceptions fail the request before committing; add controller and rendered-template tests for empty/populated views.
- **Add:** permanent empty content with date range, Add task, Apply plan and Go to today actions.
- **Acceptance:** active view always shows a grid/list skeleton or explicit empty state before enhancement runs.

### 9. Trainer dashboard — strong base, contrast and overlap issues

![Trainer dashboard](C:/Users/marty/.codex/visualizations/2026/07/13/019f5c66-fb99-74e1-b853-e9626f32fc6f/site-ux-audit/11-trainer-dashboard.png)

**Health: Good / Amber.**

- Schedule, week overview and primary cards are coherent.
- A secondary CTA is nearly invisible on white; pale labels are weak; platform panel overlaps cards.
- **Fix:** enforce contrast tokens and remove transparent white CTAs on white surfaces.
- **Add:** compact active-state feedback using border/color and 1–2 px transform, not layout-changing scale.

### 10. Trainer clients — clear state, too much dead space

![Trainer clients](C:/Users/marty/.codex/visualizations/2026/07/13/019f5c66-fb99-74e1-b853-e9626f32fc6f/site-ux-audit/12-trainer-clients.png)

**Health: Amber.**

- Current/empty client states are clear.
- Large empty areas lack guidance; search boundary is weak; action styles are mixed.
- **Fix:** add invite/search/filter guidance, one action hierarchy and stronger input/focus borders.
- **Add:** a gentle result-count transition and skeleton rows for real loading.

### 11. Gym authentication and onboarding — functional, with abrupt progression

![Gym tutorial](C:/Users/marty/.codex/visualizations/2026/07/13/019f5c66-fb99-74e1-b853-e9626f32fc6f/site-ux-audit/18-gym-tutorial.png)

**Health: Amber.**

- The segmented code accepts continuous entry and auto-advances; gym authentication succeeds when the full fixture code is entered.
- Incorrect credentials still produce a generic group-level message with limited recovery guidance.
- The six-step gym tutorial is role-specific, but Next swaps the title and body immediately with no meaningful transition.
- Emoji artwork conflicts with the otherwise premium icon system.
- **Fix:** preserve username, focus and announce the invalid code group; animate tutorial content with a short opacity/translate transition while locking the card dimensions.
- **Add:** paste-across-segments guidance, grouped field legend, reduced-motion instant state and one consistent icon family.

### 12. Admin — usable operations, global chrome dominates

![Admin dashboard](C:/Users/marty/.codex/visualizations/2026/07/13/019f5c66-fb99-74e1-b853-e9626f32fc6f/site-ux-audit/14-admin-dashboard.png)

![Admin feedback](C:/Users/marty/.codex/visualizations/2026/07/13/019f5c66-fb99-74e1-b853-e9626f32fc6f/site-ux-audit/15-admin-feedback.png)

**Health: Good / Amber.**

- Operational metrics and shortcuts scan well.
- The fixed client-oriented platform bar occupies every admin screen.
- Empty feedback has little guidance; profile text is weak over a bright gradient.
- **Fix:** make platform navigation role-aware/collapsed for admin, reserve height and improve profile contrast.
- **Add:** feedback empty-state actions and collection-settings link.

### 13. Charlie — requested limits are present; overlay system needs consolidation

![Charlie open](C:/Users/marty/.codex/visualizations/2026/07/13/019f5c66-fb99-74e1-b853-e9626f32fc6f/site-ux-audit/17-charlie-open.png)

**Health: Good / Amber.**

- Current build visibly shows `0 / 1,600`; near/at/over states exist and over-limit sending is blocked.
- Attachments allow five photos, show `n / 5 photos`, use a scrollable tray and support full-size lightbox viewing.
- Closed Charlie content remains discoverable in the semantic snapshot despite `aria-hidden`.
- Charlie, mobile navigation, quick actions, lightbox and notification layers compete from z-index `9998` to `10020`.
- **Fix:** add `inert`/true hiding when closed, trap/return focus and move overlays onto named layer tokens.
- **Add:** thumbnail size controls only if user testing needs them; first guarantee five-photo scrolling/removal on touch/keyboard.
- **Acceptance:** five photos can be added, scrolled, removed, expanded and sent at 390 and 1440 px.

### 14. Gym dashboard — readable structure, broken action presentation

![Gym dashboard](C:/Users/marty/.codex/visualizations/2026/07/13/019f5c66-fb99-74e1-b853-e9626f32fc6f/site-ux-audit/19-gym-dashboard.png)

**Health: Critical / Red.**

- Today, week and quick-action grouping is understandable.
- Primary text and headings use weak gray contrast; green text sits on a blue gradient; the second Full Calendar CTA is effectively invisible.
- The orange profile card has weak text contrast and the fixed platform panel overlaps lower content.
- At 390 px, View Today clips and the second CTA remains blank, so this is both a token and responsive-layout failure.
- **Fix:** use semantic surface/action tokens with verified contrast, keep CTA labels in normal flow and stack the paired actions below the card copy on narrow widths.
- **Add:** visual tests for both CTA labels and all four contrast combinations at desktop/mobile.

### 15. Gym Trainers and Memberships — fixture relationship causes 403

![Gym Trainers 403](C:/Users/marty/.codex/visualizations/2026/07/13/019f5c66-fb99-74e1-b853-e9626f32fc6f/site-ux-audit/20-gym-trainers.png)

![Gym Memberships 403](C:/Users/marty/.codex/visualizations/2026/07/13/019f5c66-fb99-74e1-b853-e9626f32fc6f/site-ux-audit/21b-gym-memberships.png)

**Health: Critical / Red.**

- Both primary gym navigation destinations return 403 for the supplied `demo_gym` account.
- Security permits `GYM_ADMIN`; the controllers reject the request because `demo_gym` has no `users.gym_id` relationship.
- The seed creates the user and gym profile but omits the relationship update used by the older gym fixture.
- **Fix:** make gym-admin creation transactional and always bind `users.gym_id`; repair the demo seed; decide whether a missing relationship should show a recoverable setup state rather than a generic 403.
- **Add:** authenticated route-contract tests for every gym navigation item using the same fixture that starts the application.

### 16. Gym Support — wrong product destination

![Gym Support mismatch](C:/Users/marty/.codex/visualizations/2026/07/13/019f5c66-fb99-74e1-b853-e9626f32fc6f/site-ux-audit/22b-gym-support.png)

**Health: Critical for role clarity.**

- Support opens `/admin/feedback`, an administrative moderation inbox with “respond by email” and “Back to admin panel” copy.
- A gym administrator is therefore placed into a platform-admin workflow instead of a help/contact workflow.
- **Fix:** point Gym Support to a role-appropriate support request/help centre; reserve feedback moderation for platform admins.
- **Add:** role-labelled page headings and authorization/route tests that verify destination intent, not only HTTP access.

### 17. Gym profile — functional, with secret and overlap risks

![Gym profile](C:/Users/marty/.codex/visualizations/2026/07/13/019f5c66-fb99-74e1-b853-e9626f32fc6f/site-ux-audit/23-gym-profile.png)

**Health: Amber.**

- Core profile editing renders cleanly.
- The gym secret code is displayed in full with Copy, increasing shoulder-surfing and accidental disclosure risk.
- Code text is low contrast and the fixed platform bar overlaps lower form content.
- **Fix:** mask the code by default with a deliberate reveal/copy confirmation, audit access, and reserve fixed-panel space.
- **Add:** code rotation/revocation guidance and a clear explanation of who may use it.

### 18. Shared inbox, workouts and goals — clean empty states, role and semantics drift

![Shared inbox](C:/Users/marty/.codex/visualizations/2026/07/13/019f5c66-fb99-74e1-b853-e9626f32fc6f/site-ux-audit/24b-shared-inbox.png)

![Shared workouts](C:/Users/marty/.codex/visualizations/2026/07/13/019f5c66-fb99-74e1-b853-e9626f32fc6f/site-ux-audit/25-shared-workouts.png)

![Shared goals](C:/Users/marty/.codex/visualizations/2026/07/13/019f5c66-fb99-74e1-b853-e9626f32fc6f/site-ux-audit/26b-shared-goals.png)

**Health: Amber.**

- Inbox, support, workout and goal copy/actions now resolve from the active account role.
- Gym and platform-admin action trays no longer advertise client/trainer workout, goal or messaging destinations as operational tools.
- The custom-exercise panel is inert and hidden to interaction until opened, closes with Escape and restores focus.
- Inbox and support empty states now route each role to its next valid task; the archived-goal control uses its stable labelled row.
- Representative desktop and 390 × 844 role journeys have passed browser review; physical-device and assistive-technology checks remain in Phase 3.

### 19. Global calendar response — confirmed cross-role failure

![Gym calendar blank](C:/Users/marty/.codex/visualizations/2026/07/13/019f5c66-fb99-74e1-b853-e9626f32fc6f/site-ux-audit/27-gym-calendar.png)

**Health: Critical / Red.**

- The same partial response occurs for client and gym admin.
- The month view container is only about 108 px tall because the server never sends the calendar body, not because the full body is visually collapsed.
- **Fix:** treat calendar template rendering as a P0 backend/view contract failure, then verify Week, Month and Day with JavaScript disabled and enabled.

### 20. Authenticated mobile navigation — overflow and competing layers

![Gym mobile dashboard](C:/Users/marty/.codex/visualizations/2026/07/13/019f5c66-fb99-74e1-b853-e9626f32fc6f/site-ux-audit/28b-gym-dashboard-mobile.png)

![Gym mobile menu](C:/Users/marty/.codex/visualizations/2026/07/13/019f5c66-fb99-74e1-b853-e9626f32fc6f/site-ux-audit/29b-gym-mobile-menu.png)

**Health: Critical / Red.**

- The authenticated menu is taller than the 844 px viewport; Logout lands at/below the bottom edge.
- Charlie and quick-actions controls remain above the menu and cover account/logout content.
- The page is not made inert and background controls remain semantically reachable.
- The bottom navigation clips Dashboard and exposes only part of the action shelf.
- **Fix:** make the menu an owned modal layer with an internal safe-area-aware scroller, hide/inert background tools, lock page scroll and constrain all fixed actions to the same layer contract.
- **Add:** keyboard focus trap/return, Escape close, touch scroll tests and 320/360/390/430 px screenshot coverage.

### 21. Accessibility and automated checks — actionable failures found

**Health: Critical / Red.**

- On the gym dashboard, 74 visible controls were detected; 43 measured below 44 × 44 px, including profile/logout and several primary CTA heights.
- A closed `aria-hidden` region still contained many focusable controls from Charlie, quick actions and platform navigation.
- No duplicate IDs were found in the sampled dashboard state; heading order was coherent from H1 to H2.
- Original audit result: **455 tests, 1 failure**. The stale “Open coach” contract was updated to the canonical Charlie label in Phase 0; the current suite is fully green.
- **Fix:** make inactive layers non-focusable with `inert`/`hidden`; increase interactive hit areas; choose Charlie as the canonical label and update the stale test, or revert product copy intentionally.
- **Add:** axe, keyboard, screen-reader and zoom checks to CI/release QA; retain screenshot checks for visible focus and overlays.

## Prioritized fix backlog

### P0 — implemented and verified

- **Client dashboard clipping/overlap**
  - Remove nested landmark, clipping and premature three-column layout.
  - Reflow rails at laptop widths and reserve bottom-panel space.
  - Add screenshot regression coverage at target widths.

- **Blank calendar**
  - Remove the enum/type resolution from the view and stop partial template responses.
  - Add rendered controller tests and no-JavaScript fallback.
  - Verify Week, Month and Day for empty/populated users.

- **Raw trainer price expression**
  - Format in a typed presentation model.
  - Fail tests on `card.` / `${` / `#{` leakage.

- **Broken gym primary routes**
  - Bind the demo gym user to its gym record and make the relationship mandatory in gym-admin creation.
  - Add authenticated route contracts for Dashboard, Trainers, Memberships, Profile and Support.
  - Replace the generic 403 with a recoverable setup state only where authorization remains valid.

- **Wrong gym Support destination**
  - Route gym users to support/help submission, not platform feedback moderation.
  - Restrict moderation copy and controls to the platform-admin role.

- **Authenticated mobile clipping and layer collision**
  - Repair the invisible/clipped gym CTAs and bottom-navigation labels.
  - Put menu, Charlie, quick actions and platform navigation under one layer owner.
  - Make background content inert and keep Logout reachable at 320–430 px widths.

### P1 — restore continuous interaction (shared foundations complete)

- **One overlay/fixed-layer system — implemented and verified**
  - Define named layers: base `0`, sticky nav `30`, platform `40`, dropdown `50`, modal `70`, assistant `80`, critical toast `90`.
  - Remove four/five-digit escalation; one owner per layer.
  - Coordinate menu, Charlie, quick actions, modals and lightbox through one body-state controller.

- **One motion token system — implemented for the high-risk global surfaces**
  - Replace `transition: all` with explicit properties.
  - Use one duration/easing scale.
  - Avoid layout-property animation when transform/opacity or grid-row interpolation works.

- **Reduce core CSS payload — implemented and verified**
  - Core `app.css` is **212,420 bytes (207.4 KiB)** after the split/minified production build, down approximately 83%.
  - Eight route-aware feature bundles prevent every surface from shipping in the shared core.
  - Static CSS is gzip-compressed, publicly cached for one day and supports `304` revalidation; dynamic HTML remains `no-store`.
  - Throttled CPU/network testing remains assigned to Phase 3 release QA.

- **Role-aware auth/onboarding and shared copy**
  - Hide inactive fields semantically, improve grouped errors and route each role correctly.
- **Implemented:** make inbox, support and shared-tool language match the active role.

- **Accessibility contract**
  - Remove focusable controls from `aria-hidden` regions and increase small hit targets.
  - Resolve the Charlie/coach naming contract and restore a fully passing suite.

- **Opaque page shell and fixed-panel reservation — implemented and verified**
  - Give light pages an opaque full-height surface.
  - Calculate page padding, local-dock spacing and global floating-control offsets from the same measured custom properties.

### P2 — clarity and consistency

- Standardize nav/CTA typography, including Login.
- Repair yellow warning, white-on-white CTA and bright profile-card contrast.
- Replace tutorial emoji with the established icon library.
- Add meaningful empty states for trainer clients, feedback, calendar and inbox surfaces.
- Remove redundant Back links where global navigation already provides the route.
- Shorten marketing pages and add contextual conversion anchors.
- Standardize search, button hierarchy, card radii, focus rings and skeletons.

### P3 — polish after stability

- Add list/card entrance staggering only for new data, capped at 40–60 ms per item.
- Add optimistic save/send/upload feedback.
- Add calendar view cross-fades only after every view renders reliably.
- Add subtle section progress on long public pages.
- Use tiny scale/translate feedback for primary actions without changing layout.

## “Fluent river” motion specification

| Token | Duration | Use |
|---|---:|---|
| Instant | 100–140 ms | press, icon swap, focus acknowledgement |
| Micro | 160–220 ms | hover, tabs, chips, validation, counters |
| Panel | 240–320 ms | menu, drawer, Charlie, disclosure |
| Route/section | 280–420 ms | page shell or major view change |

Use `cubic-bezier(0.22, 1, 0.36, 1)` for entrances and a faster ease-in for exits. Avoid bounce on authentication, payment and destructive admin actions.

### Anti-jitter rules

- Animate `transform` and `opacity` by default; never add new `transition: all`.
- Reserve image, chart, avatar and skeleton dimensions before data arrives.
- Use `grid-template-rows: 0fr → 1fr` or measured FLIP for disclosures; do not tween unknown `height: auto`.
- Use one scroll container per page; avoid nested vertical scrolling except deliberate panels.
- Preserve scroll position during tabs/view changes.
- Disable pointer events only during unsafe transition frames.
- Delay decoration until critical content has painted.
- Every pattern gets a reduced-motion path with no travel.

### Loading continuity

- Show a slim route progress indicator after roughly 250 ms.
- Match skeletons to final dashboard/calendar dimensions.
- Keep the previous view until the next has a stable frame, then cross-fade.
- Acknowledge save/send/upload immediately without moving surrounding controls.
- Target no unexpected layout shift and no animation-driven task over 50 ms on mid-range mobile.

## Phased implementation plan

### Phase 0 — emergency stability

- Fix client dashboard, calendar, trainer price, demo gym relationship/403 routes, gym Support destination and authenticated mobile collisions.
- Resolve the Charlie/coach contract failure and add visual/route regression baselines.
- **Exit:** core client and gym journeys are complete/readable at target widths and all 455 tests pass.

### Phase 1 — shared foundations

- **Complete:** add CSS custom properties for duration, easing and layer ownership.
- **Complete:** build one overlay coordinator while each surface preserves its focus, inert and scroll-lock behavior.
- **Complete for the highest-risk global surfaces:** replace broad transitions in navigation, Charlie and Quick Actions.
- **Remaining:** migrate dashboard/calendar broad transitions after their layout-property inventory is reduced.
- **Complete:** split/minify CSS into a compact shared core and conditional route bundles; verify gzip, public static caching and `304` revalidation.
- **Complete:** unify measured platform-panel and local-dock reservations across page padding, Charlie and Quick Actions.
- **Continuity exit met:** menu, Charlie, Quick Actions, platform settings and Charlie's lightbox do not collide or expose closed controls.
- **Phase exit met:** the shared shell no longer ships the monolithic CSS payload, fixed surfaces reserve one continuous stack, and production delivery headers are verified.

### Phase 2 — surface consistency

- **Complete:** make authentication and onboarding role-aware.
- **Complete for client, trainer, gym-admin and platform-admin dashboards:** apply shared typography, measured contrast, action hierarchy and useful empty-state standards.
- **Complete:** make shared inbox, support and tool copy role-aware.
- Extend the completed standards to remaining shared and public surfaces as those workstreams are addressed.
- Shorten public pages and standardise section transitions.
- **Exit:** all roles share the same visual/recovery language.

### Phase 3 — polish and verification

- Add purposeful calendar/page/list transitions using tokens.
- Run keyboard, screen-reader and reduced-motion passes.
- Run regression at 390, 768, 1024, 1280, 1366, 1440, 1536 and 1920 px.
- Test throttled CPU/network and 200% zoom.
- **Exit:** no clipping, gaps, fixed-layer collisions, inaccessible hidden content or motion-related context loss.

## Accessibility caveat

Screenshots can reveal hierarchy, contrast concerns and clipping, but do **not** prove WCAG conformance. Release QA still needs keyboard navigation, focus order/trapping, screen-reader announcements, 200%/400% zoom, measured contrast, touch targets, reduced motion and axe/Lighthouse checks. Hidden role panels and closed Charlie content need particular attention.

## Audit limits and closure

The audit itself is complete. No remaining repository or browser access blocker prevented representative coverage of the public, client, trainer, gym-admin, platform-admin and shared experiences. The remaining work is implementation and release validation, not additional discovery.

Not fully verifiable in this local pass: live payment completion, external email/verification delivery, destructive production-like admin operations, real assistive-technology output, physical-device GPU/touch behaviour and measured 200%/400% zoom. These are explicitly assigned to Phase 3 release QA.

## Definition of done

- Zero visible template expressions or raw property names.
- Calendar always renders content, skeleton or purposeful empty state.
- No clipped text/unreachable actions at target widths.
- No fixed panel covers page content.
- One active overlay at a time; background inert and scroll-locked.
- No new `transition: all`; high-traffic rules use explicit properties.
- Motion respects reduced motion.
- Charlie supports five queued photos and clear 1,600-character feedback on desktop/mobile.
- Public smoke checks and stable authenticated role fixtures pass.
- All automated tests pass; no stale naming/route contracts remain.
- Visual regression, keyboard and performance checks pass before release.
