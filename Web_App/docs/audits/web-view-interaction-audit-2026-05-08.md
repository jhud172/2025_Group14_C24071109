# Web View and Interaction Audit - 2026-05-08

## Summary

Scope: `Web_App` only. Phone-App is intentionally excluded.

Static surface checked from the repository:

- `164` Thymeleaf templates under `src/main/resources/templates`.
- `158` templates contain visible links, forms, buttons, inputs, or `data-*` behaviour hooks.
- `79` JavaScript files under `src/main/resources/static/js`; `76` contain interaction logic, DOM hooks, storage, or fetch calls.
- `71` CSS files under `src/main/resources/static/css`.

Architecture compliance:

- No inline `style=`, inline `<style>`, inline event handlers, `th:onclick`, or `javascript:` template usage was found by static scan.
- Template `<script src=...>` imports exist and are acceptable under the project rules.

First fixed issue:

- `/admin/gym-applications`, `/admin/gym-applications/{id}`, and `/admin/feedback` returned non-existent template paths. Controllers now return the matching `admin-views/admin/...` templates.

Runtime audit pass:

- Added `tools/qa/playwright-local-view-audit.mjs` for repeatable local browser checks against `http://localhost:8081`.
- Latest run captured `45` public/client/trainer/gym/admin/super-admin page and viewport checks with no active findings.
- Evidence is written to `output/playwright/local-view-audit/results.json`, `summary.json`, and screenshots in the same folder.
- Fixed a calendar month API failure where `/api/calendar/summary` returned `500` for authenticated browser fetches.
- Fixed schedule metadata fetch auth resolution for `/api/schedules/...` endpoints used by trainer schedule pages.
- Added accessible names to icon-only schedule modal close buttons and the global chat send button.

## View and Interaction Matrix

| Area | Views / templates | Role and access expectation | Interactive features to verify | JS/CSS touchpoints | Priority |
| --- | --- | --- | --- | --- | --- |
| Global layout | `base.html`, navbar, footer, username/logout, quick actions, chat widget, dev fragments | Public shell with authenticated enhancements; logout and profile menus require auth | Mobile nav, account menu, logout form, floating profile card, quick action buttons, chatbot open/filter/clear/send, history/back buttons | `core/navbar-premium.js`, `core/quick-actions.js`, `chat/chat.js`, `core/confirm-action.js`, `core/back-navigation.js`, `css/components/core/*`, `css/components/chat/*` | P0 |
| Public and policies | Home, logged-out/logged-in home, about, FAQ, public profile, pricing, checkout, legal pages, verification pages | Public pages should remain accessible; checkout/payment pages must preserve in-platform payment handling | CTA links, FAQ search/toggle, pricing selection, simulated payment form, legal acknowledgement, email/phone code forms and resend | `public/*.js`, `payments/simulated-payment-form.js`, `core/verification-resend.js`, `policies/legal-confirmation.js`, `css/components/misc/*`, account/payment CSS | P1 |
| Auth | Login, demo login, signup choice, client/trainer/gym signup, trainer success, gym application portal, forgot/reset password, social auth fragment | Public; authenticated users should be redirected where intended; role-specific login must not cross roles incorrectly | Role slider, password toggles, multi-part trainer/gym code inputs, signup validation, copy trainer code, social provider links/disabled buttons | `auth/*.js`, `css/components/account/auth.css`, `css/components/account/login-demo.css` | P0 |
| Client | Dashboard, explore, trainers, my trainer, assigned plan, assessment, goals, achievements, preferences, health record, blood pressure, profile | Client/user routes authenticated; one active trainer rule must hold; trainer request states must lock correctly | Dashboard rails/flyouts/tabs/weather/activity/message box, trainer filters/request forms, goal CRUD/check-ins, preferences save/reset, health forms, profile cards/image/settings/export | `dashboard/client-dashboard-page.js`, `goals/goal-pages.js`, `health/*.js`, `profile/*.js`, `core/profile-preview.js`, dashboard/profile/goal CSS | P0 |
| Trainer | Dashboard, requests/clients/client detail, check-in review, workouts, workout templates, library, exercises, programmes, profile, schedules | Trainer-only routes; clients and assignments must be scoped to current trainer | Accept/pause/end clients, phase/workout/schedule assignment, workout builder, set/video studio, template actions, library share dialog, schedule builder/list/preview/duplicate/apply, profile report modal | `trainer/*.js`, `workouts/*.js`, `schedule/*.js`, training CSS | P0 |
| Gym/Admin | Gym dashboard, gym trainer management, memberships, admin dashboard, feedback, gym applications, merch admin, verification queue/detail, off-platform payments | Gym routes gym-admin only; platform/super-admin routes locked; verification and off-platform payment actions auditable | Trainer creation/notes, membership forms and price changes, outreach, dev page locks, feedback responses, gym application approve/decline/request-info, merch product CRUD, verification modals | `admin/*.js`, `trainer/gym-admin-trainers-page.js`, `merch/price-change-page.js`, core/admin CSS | P0 |
| Shared workspace | Calendar, inbox/messages, chat/chatv2, notes, vault, nutrition, reviews, merch shop/orders/checkout, workout session/management, levels, tutorial | Auth required unless explicitly public; sensitive health/messages/payment data scoped to owner/trainer relationship | Calendar day/week/month drawers and task forms, inbox fetch/thread send, chat attachments/actions, notes editor/folders, vault AI actions, nutrition log, review rating, merch filters/checkout, workout set add/toggle/delete/complete | `calendar/*.js`, `messaging/*.js`, `chat/*.js`, `notes/*.js`, `merch/*.js`, `workouts/workouts-player.js`, calendar/chat/notes/merch CSS | P0 |
| System/dev/error | Dev hub/restricted/unauthorized, 403/404/500/error pages | Dev pages follow configured page locks; error pages do not expose sensitive data | Back buttons, dev hub navigation, restricted-page CTAs | `core/back-navigation.js`, `css/components/dev/*`, `css/components/core/dev-*` | P2 |

## Template Inventory by Area

### Admin views

- `admin-views/admin/feedback.html`
- `admin-views/admin/gym-application-detail.html`
- `admin-views/admin/gym-applications.html`
- `admin-views/admin/off-platform-payments.html`
- `admin-views/dashboard/admin-dashboard.html`
- `admin-views/merch/admin-form.html`
- `admin-views/merch/admin-list.html`
- `admin-views/super-admin/verification-detail.html`
- `admin-views/super-admin/verification-queue.html`

### Client views

- `client-views/achievements/index.html`
- `client-views/checkins/client-submit.html`
- `client-views/client/assessment-form.html`
- `client-views/client/assigned-plan.html`
- `client-views/client/my-trainer.html`
- `client-views/client/plan.html`
- `client-views/client/trainers.html`
- `client-views/conditions-preference/quick-preferences.html`
- `client-views/conditions-preference/select-preferences.html`
- `client-views/conditions-preference/view-preferences.html`
- `client-views/dashboard/client-dashboard.html`
- `client-views/dashboard/fragments/client-dashboard-identity.html`
- `client-views/dashboard/fragments/client-dashboard-shell.html`
- `client-views/explore/index.html`
- `client-views/goals/checkins.html`
- `client-views/goals/create.html`
- `client-views/goals/detail.html`
- `client-views/goals/edit.html`
- `client-views/goals/fragments/goal-chip.html`
- `client-views/goals/index.html`
- `client-views/health-record/health-record-form.html`
- `client-views/health-record/health-record-list.html`
- `client-views/health-record/health-record-view.html`
- `client-views/health/blood-pressure-edit.html`
- `client-views/health/blood-pressure.html`
- `client-views/profile/profile.html`

### Gym views

- `gym-views/dashboard/gym-dashboard.html`
- `gym-views/gym-admin/memberships/form.html`
- `gym-views/gym-admin/memberships/list.html`
- `gym-views/gym-admin/memberships/price-change.html`
- `gym-views/gym-admin/memberships/price-history.html`
- `gym-views/gym-admin/trainers.html`

### Public and auth views

- `public-views/auth/confirm-logout.html`
- `public-views/auth/forgot-password.html`
- `public-views/auth/fragments/social-auth-buttons.html`
- `public-views/auth/login-demo.html`
- `public-views/auth/login.html`
- `public-views/auth/reset-password.html`
- `public-views/auth/signup-choice.html`
- `public-views/auth/signup-client.html`
- `public-views/auth/signup-gym-application.html`
- `public-views/auth/signup-gym.html`
- `public-views/auth/signup-trainer-success.html`
- `public-views/auth/signup-trainer.html`
- `public-views/auth/signup.html`
- `public-views/dashboard/client-dashboard-public.html`
- `public-views/home/public.html`
- `public-views/home/user.html`
- `public-views/payments/pricing-checkout.html`
- `public-views/payments/pricing.html`
- `public-views/policies/payments.html`
- `public-views/policies/privacy.html`
- `public-views/policies/subscription-terms.html`
- `public-views/policies/terms.html`
- `public-views/public/about.html`
- `public-views/public/faq.html`
- `public-views/public/profile.html`
- `public-views/verify/email-code.html`
- `public-views/verify/email-confirm.html`
- `public-views/verify/phone-code.html`

### Shared views

- `shared-views/calendar/day.html`
- `shared-views/calendar/focus.html`
- `shared-views/calendar/fragments/daily-streak-bar.html`
- `shared-views/calendar/fragments/schedule-drawer-month.html`
- `shared-views/calendar/fragments/schedule-drawer-week.html`
- `shared-views/calendar/month.html`
- `shared-views/calendar/task-detail.html`
- `shared-views/calendar/week.html`
- `shared-views/chat/chat.html`
- `shared-views/chat/folder.html`
- `shared-views/chat/fragments/sidebar.html`
- `shared-views/chat/hub.html`
- `shared-views/chat/thread.html`
- `shared-views/exercise-log/ExerciseTutorial.html`
- `shared-views/exercise-log/exercise-log-form.html`
- `shared-views/exercise-log/exercise-log-list.html`
- `shared-views/exercise-log/exercise-log-view.html`
- `shared-views/inbox/index.html`
- `shared-views/inbox/thread.html`
- `shared-views/levels/leaderboard.html`
- `shared-views/levels/me.html`
- `shared-views/merch/checkout.html`
- `shared-views/merch/orders.html`
- `shared-views/merch/shop.html`
- `shared-views/messages/client-inbox.html`
- `shared-views/messages/thread.html`
- `shared-views/messages/trainer-inbox.html`
- `shared-views/notes/folders.html`
- `shared-views/notes/index.html`
- `shared-views/notes/note-form.html`
- `shared-views/notes/note-view.html`
- `shared-views/nutrition/daily-log.html`
- `shared-views/orders/orders.html`
- `shared-views/review/form.html`
- `shared-views/tutorial/tutorial.html`
- `shared-views/vault/index.html`
- `shared-views/vault/note-form.html`
- `shared-views/vault/note-view.html`
- `shared-views/workout-management/index.html`
- `shared-views/workout-session/complete.html`
- `shared-views/workout-session/session.html`

### Trainer views

- `trainer-views/checkins/trainer-review.html`
- `trainer-views/dashboard/trainer-dashboard.html`
- `trainer-views/schedule/add-entry.html`
- `trainer-views/schedule/apply.html`
- `trainer-views/schedule/builder-old.html`
- `trainer-views/schedule/builder.html`
- `trainer-views/schedule/list.html`
- `trainer-views/schedule/select-schedule.html`
- `trainer-views/schedule/workout.html`
- `trainer-views/trainer/active-clients.html`
- `trainer-views/trainer/client-detail.html`
- `trainer-views/trainer/client-requests.html`
- `trainer-views/trainer/clients.html`
- `trainer-views/trainer/exercises/create.html`
- `trainer-views/trainer/exercises/edit.html`
- `trainer-views/trainer/exercises/list.html`
- `trainer-views/trainer/exercises/view.html`
- `trainer-views/trainer/fragments/library-share-dialog.html`
- `trainer-views/trainer/library.html`
- `trainer-views/trainer/profile/edit.html`
- `trainer-views/trainer/profile/view.html`
- `trainer-views/trainer/programmes/create.html`
- `trainer-views/trainer/programmes/edit.html`
- `trainer-views/trainer/programmes/list.html`
- `trainer-views/trainer/programmes/view.html`
- `trainer-views/trainer/templates/apply.html`
- `trainer-views/trainer/templates/edit.html`
- `trainer-views/trainer/templates/index.html`
- `trainer-views/trainer/workouts/create.html`
- `trainer-views/trainer/workouts/edit.html`
- `trainer-views/trainer/workouts/list.html`
- `trainer-views/trainer/workouts/view.html`
- `trainer-views/workout-templates/builder.html`
- `trainer-views/workout-templates/index.html`
- `trainer-views/workouts/edit.html`
- `trainer-views/workouts/fragments/searchbar.html`
- `trainer-views/workouts/fragments/workout-frags.html`
- `trainer-views/workouts/index.html`
- `trainer-views/workouts/start.html`

### System and universal fragments

- `base.html`
- `system-views/dev-mode/hub.html`
- `system-views/dev-mode/restricted.html`
- `system-views/dev-mode/unauthorized.html`
- `system-views/error/403.html`
- `system-views/error/404.html`
- `system-views/error/500.html`
- `system-views/error/error.html`
- `universal-fragments/chat/chat-widget.html`
- `universal-fragments/dev/dev-mode.html`
- `universal-fragments/dev/dev-page-display.html`
- `universal-fragments/layout/footer.html`
- `universal-fragments/layout/navbar.html`
- `universal-fragments/layout/quick-actions.html`
- `universal-fragments/layout/username-logout.html`

## Interaction Categories to Exercise

- Navigation: primary nav, mobile nav, footer links, dashboard rails, card CTAs, public CTAs, back buttons, deep links, empty-state links.
- Forms: auth, verification, profile, preferences, goals, check-ins, health records, blood pressure, client-trainer actions, workout/schedule builders, merch checkout, notes/vault, admin moderation.
- Buttons and controls: password toggles, role slider, tabs, accordions, modals, drawers, filters, search, sort, auto-submit selects, confirmations, copy actions, image/profile controls.
- Async behaviour: dashboard messages/activity, notifications, quick actions, chat, calendar updates, schedule preview/duplicate, workout set/video actions, notes search/autosave, inbox fetches, public dashboard fetches.
- Responsive states: desktop, tablet, mobile, with focus on navbar, dashboards, calendar drawers, profile cards, modals, action bars, schedule/workout builders, and checkout forms.
- Access and lifecycle: unauthenticated redirects, role guards, dev-mode locks, one-active-trainer enforcement, trainer verification visibility, payment-in-platform constraints, GDPR-sensitive profile/health/message flows.

## Priority Fix Queue

1. P0 fixed: admin application and feedback controller template paths.
2. P0 fixed: calendar month summary API now uses the shared current-user resolver for authenticated AJAX requests and returns `401` instead of `500` when unauthenticated.
3. P0 fixed: schedule API endpoints now use the shared current-user resolver for authenticated AJAX metadata, preview, duplicate, deployment, and undo calls.
4. P0 fixed: JS-driven schedule duplication now sends the CSRF header from global CSRF metadata.
5. P0 fixed: client-only trainer discovery/security routes no longer accept broad `ROLE_USER`, which allowed trainer accounts to reach `/client/trainers`.
6. P0 verified: local browser audit covers global layout, auth controls, dashboard, calendar, trainer schedules, gym/admin screens, role logins, invalid login feedback, unauthenticated redirects, role locks, schedule search/modals/duplicate confirmation, and chat widget open/send-empty handling.
7. P1 fixed: deeper client data-changing browser workflows now cover goal create/detail, goal check-in submit, notes create/autosave/search/delete, and health-record submit.
8. P1 fixed: notes API delete endpoints now return `204 No Content`, matching the shared JS request helper and allowing note/folder delete UI to clear correctly.
9. P1 fixed: workout exercise library API now returns a dedicated DTO instead of full JPA entities, preventing recursive workout/exercise JSON from breaking the trainer workout studio.
10. P1 fixed: profile payment-card submit and trainer workout custom-exercise close controls now have explicit accessible names.
11. P1 fixed: second-depth workflows now cover profile update submission, blood-pressure create/edit/delete, vault create/edit/pin/delete, inbox send when a thread exists, and merch checkout availability/simulated-payment locking.
12. P1 fixed: blood-pressure create/edit/delete forms now include CSRF tokens, and date/time controls render browser-safe `yyyy-MM-dd` / `HH:mm` values instead of locale-formatted values.
13. P1 fixed: remaining high-risk workflow coverage now includes trainer-client lifecycle visibility/access, trainer library exercise/workout/programme create-edit-delete, schedule deployment preview/impact APIs, vault AI insight/summarise/rewrite handling, blood-pressure JSON API, inbox/notification/calendar APIs, admin merch mutation plus feedback/gym-application moderation availability, and payment success/cancel edge states.
14. P1 verified: schedule apply/deploy remains non-destructive in the local harness; deployment preview/impact and calendar summary are checked, while applying generated entries is left for seeded/demo-only records or an intercepted browser pass.
15. P1 next: add focused backend regression tests only when future deeper payment, messaging, health, trainer-client lifecycle, or schedule-deployment testing confirms an application defect rather than harness coverage.
16. P2: polish lower-risk static pages, dev-mode pages, and error-page CTAs after core flows are stable.

## Current Static Findings

- Template separation rules currently pass the static inline-code scan.
- The highest-risk confirmed defect was stale controller template names for admin screens; this has been corrected.
- The current local browser workflow audit passes with no findings after the P0/P1 fixes above: `69` checks, `0` findings, evidence in `output/playwright/local-view-audit/`.
- Data-changing form/API coverage now includes representative profile update, goal, check-in, notes, health-record, blood-pressure CRUD and JSON API, vault CRUD and AI actions, inbox UI/API send/read, notification read-all, trainer library CRUD, admin merch create/edit/delete/deactivate, and merch payment edge-state handling. Trainer-client lifecycle and admin moderation are documented with safe availability/access checks; destructive accept/reject/approve/decline actions remain notAvailable unless a seeded demo record is explicitly provided for mutation.
