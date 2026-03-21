# Project Improvement Audit

Date: 2026-03-21
Project: One To One
Scope: Whole repository review across architecture, UX, security, integration quality, and role-based journeys.

## 1. Executive Summary

One To One is already a feature-rich coaching platform. It is not a small student prototype anymore. The codebase contains:

- 69 controller classes
- 173 Thymeleaf templates
- 107 test files
- role-specific experiences for guest, client, trainer, gym admin, and platform/admin users

The project’s main risk is no longer missing ideas. The main risk is consistency.

The app currently has strong breadth:

- role-based dashboards
- trainer-client relationship flows
- calendar, goals, workouts, nutrition, health and notes
- gym memberships and merch
- AI-assisted features
- messaging, inbox, chat, notifications, and admin moderation surfaces

The biggest improvement opportunities are:

1. Consolidate overlapping systems so the app feels like one product instead of several parallel products.
2. Standardise authentication and access control so security, testing, and route ownership all work the same way.
3. Fix user-facing polish issues that make the platform feel unfinished even when the underlying feature exists.
4. Move stubbed or semi-integrated features into clearly production-ready implementations.
5. Restore test reliability so future development stops breaking silently.

If the team does only a few things next, the best return will come from:

- unifying auth/session handling
- reducing duplicate messaging/chat surfaces
- fixing test infrastructure around security/dev-mode
- replacing placeholder commerce/payment behaviour
- cleaning encoding and template debt in public and calendar-facing pages

## 2. What The Project Already Does Well

Before focusing on change, it is worth saying what is already strong:

- The role model is clear. The app understands different user states and is trying to give each one a tailored experience.
- The client dashboard is ambitious and much richer than a basic CRUD app.
- There is a real design system direction in place through `base.html`, shared CSS, shared JS modules, and reusable fragments.
- There are meaningful tests across security, trainer/client access, merch stock updates, verification, vault access, and workout features.
- The project already shows good instincts around ownership checks in several controllers.
- The platform has a strong product concept: combining coaching, planning, accountability, communication, and personalisation in one place.

This means the next phase should be consolidation and hardening, not endless feature sprawl.

## 3. Highest Priority Findings

### P0: Security, trust, and production-readiness

#### 3.1 State-changing actions still use `GET`

These routes should be `POST` or `DELETE`, not `GET`:

- `src/main/java/uk/ac/cf/_5/group14/One_To_One/ScheduleData/ScheduleController.java`
  - `@GetMapping("/{id}/delete")`
  - `@GetMapping("/{id}/deactivate")`
  - `@GetMapping("/applied/{appliedId}/remove")`

Why this matters:

- `GET` should be safe and non-mutating.
- Crawlers, browser prefetching, and copied links can trigger destructive actions.
- It weakens CSRF expectations and makes the platform feel less trustworthy.

What to change:

- Convert these to `POST`.
- Add explicit confirm UI with CSRF-backed forms.
- Use a shared destructive-action pattern across schedules, merch, notes, and workouts.

#### 3.2 Method-level security is being relied on without method security being explicitly enabled

Current evidence:

- `src/main/java/uk/ac/cf/_5/group14/One_To_One/Verification/SuperAdminVerificationController.java`
- `src/main/java/uk/ac/cf/_5/group14/One_To_One/FavouriteData/FavouriteApiController.java`
- `src/main/java/uk/ac/cf/_5/group14/One_To_One/ExerciseData/ExerciseApiController.java`
- `src/main/java/uk/ac/cf/_5/group14/One_To_One/Security/SecurityConfig.java`

Observed concerns:

- `@PreAuthorize` is used on controllers.
- No `@EnableMethodSecurity` was found in the main Java source.
- `/super-admin/**` is not explicitly protected in `SecurityConfig`, while `/admin/**` is.

Why this matters:

- The project clearly intends to rely on annotation-based route protection in some places.
- If method security is not active, protection may silently fall back to broader authentication rules.
- Super-admin routes should never rely on one protection layer only.

What to change:

- Enable method security explicitly.
- Also add explicit URL matcher protection for `/super-admin/**`.
- Audit every `@PreAuthorize` usage and make sure route-level and method-level protections agree.

#### 3.3 Merch checkout is still a placeholder flow

Current evidence:

- `src/main/resources/templates/merch/checkout.html`
- `src/main/resources/static/js/merch/merch-checkout-page.js`
- `src/main/java/uk/ac/cf/_5/group14/One_To_One/MerchOrders/MerchOrderServiceImpl.java`

Observed behaviour:

- The checkout JS generates a stub token like `tok_<last4>_<timestamp>`.
- The comment explicitly says a real payment-provider SDK is not used yet.
- Orders are marked `CONFIRMED` immediately when `placeOrder` runs.

Why this matters:

- The UI implies a real payment flow.
- Users can complete a “purchase” without real card authorisation.
- This is the biggest feature-completeness risk in the repo because it crosses trust, money, and product credibility.

What to change:

- Integrate a real provider such as Stripe or equivalent.
- Separate payment intent creation, confirmation, and order finalisation.
- Introduce payment statuses like `PENDING_PAYMENT`, `PAID`, `FAILED`, `REFUNDED`.
- Keep saved-card logic token-based only; never simulate real payment success in production mode.

#### 3.4 Authentication is implemented in multiple incompatible ways

Current evidence:

- `src/main/java/uk/ac/cf/_5/group14/One_To_One/Users/AuthHelper.java`
- `src/main/java/uk/ac/cf/_5/group14/One_To_One/ScheduleData/ScheduleController.java`
- `src/main/java/uk/ac/cf/_5/group14/One_To_One/Inbox/InboxController.java`
- `src/main/java/uk/ac/cf/_5/group14/One_To_One/Messaging/MessagingController.java`
- `src/main/java/uk/ac/cf/_5/group14/One_To_One/Chat/ChatController.java`
- `src/main/java/uk/ac/cf/_5/group14/One_To_One/Security/CustomAuthenticationSuccessHandler.java`

Observed patterns:

- `AuthHelper` reads a `user` object from the HTTP session.
- Some controllers depend on `@SessionAttribute("user")`.
- Some controllers use `SecurityContextHolder`.
- Some use `Principal`.
- Login success manually writes the full `User` into the session.

Why this matters:

- It creates different sources of truth for identity.
- Security bugs become easier to introduce during refactors.
- Test setup becomes fragile because one controller may need a session user while another needs Spring Security auth.
- It increases the chance of stale session data versus database state.

What to change:

- Pick one primary identity model: Spring Security authentication.
- Replace `@SessionAttribute("user")` and most `AuthHelper` session reads with a single user resolver service built on the authenticated principal.
- Only keep session state for explicitly session-like concerns, not for the whole user object.
- Add a consistent ownership/access utility for all controllers.

### P1: Stability and maintainability

#### 3.5 Dev mode is tightly coupled into the main security chain

Current evidence:

- `src/main/java/uk/ac/cf/_5/group14/One_To_One/Security/SecurityConfig.java`
- `src/main/java/uk/ac/cf/_5/group14/One_To_One/DevMode/DevModePageRestrictionFilter.java`
- `src/main/java/uk/ac/cf/_5/group14/One_To_One/DevMode/DevModePageAccessService.java`
- `build/test-results/test/TEST-uk.ac.cf._5.group14.One_To_One.ProfileTests.ProfileRouteAccessTest.xml`
- `build/test-results/test/TEST-uk.ac.cf._5.group14.One_To_One.CalendarTests.CalendarDayHealthMvcTest.xml`

Observed impact:

- `SecurityConfig` always wires `DevModePageRestrictionFilter`.
- The filter depends on `DevModePageAccessService`.
- A large portion of `@WebMvcTest` failures now come from missing `DevModePageAccessService` beans in slice tests.

Why this matters:

- Dev tooling is now breaking normal test isolation.
- A development-only concern is coupled into core request security.
- This is a strong sign that platform concerns need clearer boundaries.

What to change:

- Guard the filter with a dedicated conditional bean.
- Make the filter optional or no-op in slice tests.
- Move dev-mode route availability behind explicit configuration classes or feature flags.
- Add one integration test suite for dev mode instead of making every MVC slice depend on it.

#### 3.6 The full test suite is currently too unstable

Verified on 2026-03-21:

- `./gradlew test`
- Result: 367 tests completed, 63 failed

Main failing clusters:

- many calendar MVC tests
- dashboard MVC tests
- profile route access tests
- goals access/adherence tests
- login integration test
- schedule summary MVC test

Why this matters:

- A repo with this many active features cannot keep moving safely with this failure rate.
- It becomes hard to trust refactors, security changes, and UI cleanups.
- The current failure mix suggests infrastructure breakage, not just a single feature bug.

What to change:

- Treat test recovery as a product task, not a side task.
- First restore test-slice bootstrapping around security/dev-mode.
- Then stabilise the calendar/goals path.
- Add CI status reporting by package so regressions are visible immediately.

#### 3.7 The codebase has several oversized “god files”

Examples:

- `src/main/java/uk/ac/cf/_5/group14/One_To_One/Dashboard/DashboardController.java` at 1601 lines
- `src/main/java/uk/ac/cf/_5/group14/One_To_One/ScheduleData/CalendarController.java` at 1191 lines
- `src/main/resources/templates/profile/profile.html` at 1426 lines
- `src/main/resources/templates/calendar/day.html` at 1015 lines
- `src/main/resources/templates/home/public.html` at 716 lines

Why this matters:

- Big files slow down safe editing.
- They hide ownership boundaries.
- They make role-specific UX improvements harder because everything is entangled.

What to change:

- Split by responsibility, not by random file size.
- Move dashboard controller logic into role-focused services/view builders.
- Split calendar day view into fragments for task drawer, focus card, reflection area, and completion rail.
- Break profile into fragments for appearance, health modules, cards, settings, and modal drawers.

## 4. Product And UX Findings By User State

### 4.1 Guest / Non-Logged-In Experience

Strengths:

- The public homepage is ambitious and visually rich.
- There are public routes for home, about, FAQ, pricing, and explore.

Problems:

- The public homepage links users to `/trainers`, but that route is protected by security while the public discovery route is `/explore`.
  - Evidence:
    - `src/main/resources/templates/home/public.html`
    - `src/main/java/uk/ac/cf/_5/group14/One_To_One/Security/SecurityConfig.java`
- There is widespread character-encoding corruption in public and calendar templates.
  - Examples:
    - `src/main/resources/templates/home/public.html`
    - `src/main/resources/templates/payments/pricing.html`
    - `src/main/resources/templates/merch/shop.html`
    - `docs/System-Overview.md`

Why this matters:

- First impressions are heavily affected by polish.
- Broken arrows, bullets, symbols, and currency text make the platform look unreliable.
- Public-to-signup conversion suffers when navigation sends users into protected routes unexpectedly.

What to change:

- Fix the homepage CTA to use the correct public route.
- Run a repository-wide encoding cleanup pass and enforce UTF-8.
- Add a guest-specific “how it works” path for the three public personas: client, trainer, gym.
- Add clearer “what you can do before signing up” and “what requires login” signalling.
- Make sure public marketing pages only promise features that are fully implemented.

### 4.2 Client Logged-In Experience

Strengths:

- The client dashboard is the strongest product surface in the app.
- It already combines tasks, planning, trainer context, and personalisation.

Problems:

- The client experience spans dashboard, calendar, goals, notes, workout pages, messages, inbox, chat, and chatv2. That breadth is good, but the journey is fragmented.
- The app has multiple communication surfaces:
  - `MessagingController`
  - `InboxController`
  - `ChatController`
  - `ChatV2Controller`
- This creates uncertainty around where a client should actually go to communicate, ask for help, or review history.

What to change:

- Pick one primary communication surface for trainer-client messages.
- Pick one primary AI coach surface.
- Make the dashboard “next action” always deep-link into the canonical flow.
- Add a client journey map in the UI:
  - Today
  - Plan
  - Coach
  - Progress
  - Settings

Why this matters:

- Clients should feel supported, not overloaded.
- The current feature richness is valuable, but only if the app consistently answers “where do I go next?”

### 4.3 Trainer Logged-In Experience

Strengths:

- Trainers have profile, client, library, and assignment tooling.
- There are clear trainer routes and security rules.

Problems:

- Trainer sharing and library views still have repeated inline dialog behaviour.
  - Examples:
    - `src/main/resources/templates/trainer/exercises/view.html`
    - `src/main/resources/templates/trainer/programmes/view.html`
    - `src/main/resources/templates/trainer/workouts/view.html`
- Trainer UX appears split between profile, clients, templates, workouts, messages, and dashboard surfaces without one strong trainer “home cockpit”.
- Some trainer dashboard quick links appear to be wired to historical or incorrect routes.

What to change:

- Build a real trainer control centre:
  - active clients
  - pending requests
  - recent check-ins
  - assigned plans
  - unread messages
- Extract one shared share-dialog component for trainer content types.
- Standardise trainer-side empty states so new trainers understand what to do first.
- Audit every trainer dashboard CTA against live controller routes before adding more trainer features.

Why this matters:

- Trainers are power users.
- They need speed, clarity, and low-friction repeated actions more than decorative complexity.

### 4.4 Gym Admin Logged-In Experience

Strengths:

- Gym membership management is more mature than expected.
- Price history and change scheduling are solid foundations.

Problems:

- Route naming and admin terminology are inconsistent.
- Some pages mix gym-admin actions with broader admin concepts, which can confuse role boundaries.
- The gym dashboard should feel more operations-focused than it currently does.
- Some gym dashboard links do not match the implemented gym-admin routes.
  - Evidence:
    - `src/main/resources/templates/dashboard/gym-dashboard.html`
    - `src/main/java/uk/ac/cf/_5/group14/One_To_One/Verification/GymAdminTrainerController.java`
    - `src/main/java/uk/ac/cf/_5/group14/One_To_One/Membership/GymAdminMembershipController.java`

What to change:

- Make “gym admin” a clearly separate operational product area in navigation and language.
- Prioritise:
- memberships
- trainer onboarding
- trainer status
- billing issues
- compliance notices
- Add a gym admin home page with operational KPIs rather than generic quick links.
- Replace broken or generic `/admin/...` links with gym-admin-specific routes.

Why this matters:

- Gym admins are business users, not coaching users.
- Their primary needs are oversight, reliability, and clear actions.

### 4.5 Admin / Platform Admin Experience

Strengths:

- There is already moderation and verification thinking in the codebase.
- Trainer verification appears to be a real platform concern, not an afterthought.

Problems:

- Admin pathways are present but feel less unified than client and trainer pathways.
- Verification, moderation, dev-page controls, and platform oversight are spread across different patterns.
- There is also a route-boundary gap between platform admin and super admin naming and enforcement.

What to change:

- Build a single platform-admin console with sections for:
  - verification queue
  - content/review moderation
- trainer/gym issues
- feature availability/dev controls
- audit and trust events
- Make the distinction between gym admin, platform admin, and super admin explicit in navigation, dashboards, and security rules.

Why this matters:

- Platform admin work depends on clarity, traceability, and consistency more than surface-level UI polish.

## 5. Architecture And Integration Findings

### 5.1 Multiple overlapping communication systems should be consolidated

Current evidence:

- `src/main/java/uk/ac/cf/_5/group14/One_To_One/Messaging/MessagingController.java`
- `src/main/java/uk/ac/cf/_5/group14/One_To_One/Inbox/InboxController.java`
- `src/main/java/uk/ac/cf/_5/group14/One_To_One/Chat/ChatController.java`
- `src/main/java/uk/ac/cf/_5/group14/One_To_One/ChatV2/ChatV2Controller.java`

Why this matters:

- It increases code duplication.
- It splits mental models for users.
- It makes notifications and unread states harder to keep coherent.

Recommended direction:

- One human messaging subsystem
- One AI assistant subsystem
- Shared notification state between both
- Shared conversation/thread concepts where possible

### 5.2 AI integration is useful but still infrastructure-light

Current evidence:

- `src/main/java/uk/ac/cf/_5/group14/One_To_One/Chat/ChatService.java`
- several AI-dependent services in calendar, vault, workout, reflection, feedback, and focus modules

Observed concerns:

- Hardcoded model string in `ChatService`
- direct HTTP integration in the main app service
- AI features spread across many product areas

Why this matters:

- Model upgrades become expensive.
- Error handling, rate limits, privacy rules, and fallback behaviour can drift apart between features.
- AI usage is now important enough to deserve a dedicated application layer.

What to change:

- Introduce a central AI gateway/service abstraction.
- Centralise model selection, retries, timeouts, cost/rate limits, safety logging, and feature flags.
- Add per-feature “AI unavailable” fallback UX standards.
- Add product-level copy explaining what data is sent to AI and when.

### 5.3 Dev-mode, demo data, and production concerns are too close together

Current evidence:

- `src/main/java/uk/ac/cf/_5/group14/One_To_One/Config/DevModeProperties.java`
- `src/main/resources/data/00-auth-demo.sql`
- `src/main/java/uk/ac/cf/_5/group14/One_To_One/Security/SecurityConfig.java`

Observed concerns:

- `.env` is read manually and copied into system properties.
- Dev-mode behaviour influences login and route availability.
- Demo users are a core part of the product experience.

Why this matters:

- Development convenience is valuable, but it should not complicate production behaviour or tests.

What to change:

- Move dev/demo bootstrapping behind explicit profiles and configuration classes.
- Keep demo accounts in seed profile data only.
- Remove manual environment bootstrapping when Spring configuration already provides stronger patterns.

### 5.4 Some route and template contracts no longer match each other

Current evidence:

- `src/main/java/uk/ac/cf/_5/group14/One_To_One/HomePage/HomePageController.java`
- `src/main/resources/templates/home/`
- `src/main/resources/templates/dashboard/trainer-dashboard.html`
- `src/main/resources/templates/dashboard/gym-dashboard.html`

Observed concerns:

- `/home` still appears to route users by role, but the `home` template directory does not contain `home/trainer.html` or `home/gym.html`.
- Some dashboard quick-action links point to routes that do not reflect the current controller structure.

Why this matters:

- This is the sort of issue users experience immediately.
- It weakens trust because the app surface looks larger than the working navigation behind it.

What to change:

- Run a route-contract audit across all dashboard and homepage CTAs.
- Add tests that visit every dashboard quick link for each role.
- Remove dead route assumptions and old template contracts.

## 6. Frontend, UI, And Design System Findings

### 6.1 Frontend debt is known and should be treated as a real project stream

Current evidence:

- `docs/frontend-template-structure-audit-2026-03-20.md`

Key signal from that audit:

- 47 templates were identified as having meaningful inline CSS, inline JS, inline event handlers, or structural debt.

Why this matters:

- The project already has a good shared frontend direction.
- The remaining debt is exactly the kind that slows future design and feature work.

What to change:

- Continue the extraction plan already documented in that audit.
- Prioritise pages that users see most:
  - pricing
  - merch shop
  - calendar
  - FAQ
  - schedule pages
  - workout builders

### 6.2 Character encoding corruption is now a design and trust issue

This is not just a technical nuisance anymore. It is visible in:

- dashboards
- calendar pages
- public pages
- pricing
- merch
- documentation

What to change:

- Force UTF-8 in editor config and build pipeline.
- Replace corrupted copied symbols with plain ASCII or correct Unicode.
- Add one QA pass specifically for visible text and symbols.

### 6.3 Some page flows still feel like development surfaces rather than polished product surfaces

Examples:

- schedule builder flows
- merch checkout
- some trainer detail/share flows
- dev-mode and restricted pages

What to change:

- For each role, define the top 5 “must feel finished” pages.
- Put those through a product-quality pass:
  - route clarity
  - empty states
  - error messaging
  - success feedback
  - accessibility
  - mobile layout

## 7. Security And Trust Improvements Beyond The Immediate P0 Items

### 7.1 Reduce production debug output

Current evidence includes `System.out`, `System.err`, and `printStackTrace()` use in:

- `src/main/java/uk/ac/cf/_5/group14/One_To_One/Config/DevModeProperties.java`
- `src/main/java/uk/ac/cf/_5/group14/One_To_One/ErrorHandling/CustomErrorController.java`
- `src/main/java/uk/ac/cf/_5/group14/One_To_One/ExerciseLog/PdfService.java`
- `src/main/java/uk/ac/cf/_5/group14/One_To_One/CalendarData/CalendarTaskServiceImpl.java`
- `src/main/java/uk/ac/cf/_5/group14/One_To_One/ScheduleData/CalendarController.java`
- `src/main/java/uk/ac/cf/_5/group14/One_To_One/Security/CustomAuthenticationSuccessHandler.java`
- `src/main/java/uk/ac/cf/_5/group14/One_To_One/Security/CustomAuthenticationFailureHandler.java`

Why this matters:

- Logging should be structured and role-appropriate.
- Debug prints can leak implementation details and make operational logs noisy.

What to change:

- Replace direct prints with `Slf4j`.
- Set consistent log levels by environment.
- Add audit-event logging for sensitive actions.

### 7.2 Harden file upload validation

Current evidence:

- `src/main/java/uk/ac/cf/_5/group14/One_To_One/Profile/ProfileImageStorageService.java`

Good:

- file size cap exists
- allowed content types are limited
- path traversal is considered on delete

Still worth improving:

- validation currently trusts MIME type reported by the upload
- no image re-encoding or content inspection step

What to change:

- inspect file signature, not only content type
- re-encode uploaded images server-side
- optionally add malware scanning if the platform becomes public-facing at scale

## 8. Suggested Roadmap

### Phase 1: Stabilise The Platform

Focus:

- unify auth/session handling
- fix dev-mode test breakages
- convert mutating `GET` routes
- replace debug output with proper logging
- restore failing test suite

Expected result:

- safer refactoring
- clearer security model
- CI becomes trustworthy again

### Phase 2: Consolidate User Journeys

Focus:

- choose one primary messaging flow
- choose one primary AI assistant flow
- fix role navigation and public route consistency
- clean the visible encoding issues
- define polished entry/dashboard pages for each role

Expected result:

- the product feels coherent instead of feature-dense but scattered

### Phase 3: Finish The “Almost Production” Features

Focus:

- real payment integration for merch and card storage
- complete frontend extraction plan
- improve role-based admin/gym operations surfaces
- add trust/compliance copy for AI and verification flows

Expected result:

- stronger trust, better conversion, fewer “demo-like” behaviours

## 9. Best Single Ideas Per Area

If the team wants one sharp idea per theme:

- User experience: create one canonical left-nav and top action model per role so every page reinforces the same journey.
- Security: remove session-stored full-user dependence and let Spring Security be the only source of identity truth.
- Design/UI: run one encoding-and-polish sweep across all public and calendar pages before adding more visual features.
- Integration: replace the merch checkout placeholder with a real payment lifecycle.
- Architecture: split the biggest controllers into role or domain coordinators plus view-model builders.
- Reliability: spend one full sprint on test recovery and CI trust.

## 10. Final Assessment

This project has enough features to be genuinely impressive. The next stage should not be “add more things everywhere.” The next stage should be:

- harden what exists
- simplify overlapping systems
- finish the flows that currently feel half-integrated
- make every role feel intentionally designed

The strongest version of One To One is not a bigger app. It is a more coherent app.

That means the best outcome now is to combine the current breadth with:

- one consistent security model
- one consistent design system
- one clear journey per user role
- one trustworthy test baseline

If those four things are solved, the platform will feel much more secure, user-friendly, and professionally integrated without needing to invent a completely new product direction.
