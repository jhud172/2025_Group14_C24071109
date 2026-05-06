# Frontend Template Structure Audit

Date: 2026-03-31
Scope: full audit of src/main/resources/templates for inline CSS/JS, inline event handlers, and fragment structure.

## 1. Executive summary

- Overall health: strong. 177 templates were inspected, 168 are already compliant with the no-inline-CSS / no-inline-JS rule, and only 9 contain real structure violations.
- Main problems found: the repo does not use inline style blocks or inline script blocks at all; the remaining breaches are limited to a handful of th:style attributes for dynamic colours/widths and three inline onclick handlers on trainer library detail pages.
- Priority areas to fix first:
  1. Remove the shared chat colour-chip th:style usage from the V2 chat pages and shared sidebar fragment.
  2. Replace the three trainer-library onclick handlers with one shared JS module and one shared share-dialog fragment.
  3. Remove the two remaining dynamic style attributes in calendar/day.html and schedule/workout.html.
  4. Plan structural cleanup for the oversized templates listed below, starting with profile/profile.html, calendar/day.html, home/public.html, and fragments/navbar.html.
- Largest templates by line count:
  - src/main/resources/templates/profile/profile.html (1426 lines)
  - src/main/resources/templates/calendar/day.html (1015 lines)
  - src/main/resources/templates/home/public.html (716 lines)
  - src/main/resources/templates/dashboard/fragments/client-dashboard-shell.html (695 lines)
  - src/main/resources/templates/fragments/navbar.html (666 lines)
- Structural consistency notes:
  - Shared layout loading is centralised correctly in base.html and most page-specific JS already lives in dedicated files.
  - Fragment use is uneven: some areas are strongly componentised (dashboard, calendar, chat sidebar), while other large templates are still monolithic (profile/profile.html, home/public.html, fragments/navbar.html).
  - Naming is inconsistent across templates: the repo mixes lowercase paths with User/, HomePage.html, and ExerciseTutorial.html, plus legacy variants such as *-old.html and builder-redesigned.html.
  - Template inventory has grown since the 2026-03-30 snapshot; the newly added admin gym-application screens, social-auth fragment, and gym-application status page are all structurally compliant.

## 2. File-by-file audit

### Files requiring extraction

- src/main/resources/templates/calendar/day.html
  - inline CSS: Yes (th:style progress width)
  - inline JS: No
  - inline event handlers: No
  - what should be extracted: Move the progress-bar width out of the template.
  - exact destination file(s): Append width initialisation to src/main/resources/static/js/calendar/day-enhancements.js and let src/main/resources/static/css/components/calendar/day-view.css read a CSS variable or JS-set width.
  - append or create: Append to existing files
  - fragment recommendation: Partially fragment
  - rationale: The page is compliant apart from one dynamic width style, but at 1015 lines it is also large enough that the progress/header strip can be split into day subfragments when touched again.
- src/main/resources/templates/chat/folder.html
  - inline CSS: Yes (th:style thread colour chip)
  - inline JS: No
  - inline event handlers: No
  - what should be extracted: Replace the inline colour style on the thread icon with data attributes.
  - exact destination file(s): Reuse src/main/resources/static/js/chat/chat-v2.js to hydrate data-chat-accent-color badges. If a shared class is needed, append it to src/main/resources/static/css/components/chat/chat-page.css.
  - append or create: Append to existing files
  - fragment recommendation: Leave as-is
  - rationale: Structure is already clean and reuses the shared chat sidebar fragment; only the dynamic colour application belongs outside the template.
- src/main/resources/templates/chat/hub.html
  - inline CSS: Yes (th:style thread colour chip)
  - inline JS: No
  - inline event handlers: No
  - what should be extracted: Replace the inline colour style on the thread icon with data attributes.
  - exact destination file(s): Reuse src/main/resources/static/js/chat/chat-v2.js to hydrate data-chat-accent-color badges. If a shared class is needed, append it to src/main/resources/static/css/components/chat/chat-page.css.
  - append or create: Append to existing files
  - fragment recommendation: Leave as-is
  - rationale: The page is already thin and relies on the shared sidebar; the only breach is the repeated dynamic colour style.
- src/main/resources/templates/chat/thread.html
  - inline CSS: Yes (th:style thread colour chip)
  - inline JS: No
  - inline event handlers: No
  - what should be extracted: Replace the inline colour style on the thread header icon with data attributes.
  - exact destination file(s): Reuse src/main/resources/static/js/chat/chat-v2.js to hydrate data-chat-accent-color badges. If a shared class is needed, append it to src/main/resources/static/css/components/chat/chat-page.css.
  - append or create: Append to existing files
  - fragment recommendation: Leave as-is
  - rationale: The layout is already componentised around the shared chat sidebar and a dedicated page script.
- src/main/resources/templates/fragments/chat/sidebar.html
  - inline CSS: Yes (th:style folder/thread colour chips)
  - inline JS: No
  - inline event handlers: No
  - what should be extracted: Replace the three repeated inline colour styles with data attributes on the icon elements.
  - exact destination file(s): Reuse src/main/resources/static/js/chat/chat-v2.js for colour hydration and optionally normalise icon styling in src/main/resources/static/css/components/chat/chat-page.css.
  - append or create: Append to existing files
  - fragment recommendation: Leave as-is (already fragment)
  - rationale: This fragment is the shared source of the repeated chat badge styling, so fixing it here removes most of the duplication in one place.
- src/main/resources/templates/schedule/workout.html
  - inline CSS: Yes (th:style custom exercise colour dot)
  - inline JS: No
  - inline event handlers: No
  - what should be extracted: Move the custom exercise colour swatch styling out of the template.
  - exact destination file(s): Append colour hydration to src/main/resources/static/js/schedule/create-workouts.js; if a dedicated swatch selector is needed, append it to src/main/resources/static/css/components/training/workout-studio.css.
  - append or create: Append to existing files
  - fragment recommendation: Partially fragment
  - rationale: The page is functionally cohesive but large enough that the suggestions panel, custom exercise form, and existing workouts panel can be separated when this area is refactored.
- src/main/resources/templates/trainer/exercises/view.html
  - inline CSS: No
  - inline JS: No
  - inline event handlers: Yes (onclick open/close share dialog)
  - what should be extracted: Move the share-dialog open/close behaviour out of the buttons.
  - exact destination file(s): Create src/main/resources/static/js/trainer/trainer-library-share-dialog.js and include it via a page script fragment. Pair it with a reusable dialog fragment at src/main/resources/templates/trainer/fragments/library-share-dialog.html.
  - append or create: Create new shared JS and fragment
  - fragment recommendation: Partially fragment
  - rationale: The page shares the same modal behaviour and near-identical dialog markup as the programme and workout detail pages; a shared fragment and shared JS file are cleaner than three separate page files.
- src/main/resources/templates/trainer/programmes/view.html
  - inline CSS: No
  - inline JS: No
  - inline event handlers: Yes (onclick open/close share dialog)
  - what should be extracted: Move the share-dialog open/close behaviour out of the buttons.
  - exact destination file(s): Create src/main/resources/static/js/trainer/trainer-library-share-dialog.js and include it via a page script fragment. Pair it with a reusable dialog fragment at src/main/resources/templates/trainer/fragments/library-share-dialog.html.
  - append or create: Create new shared JS and fragment
  - fragment recommendation: Partially fragment
  - rationale: This page duplicates the exercise/workout share-dialog behaviour closely enough that a shared trainer-library dialog fragment is the cleanest destination.
- src/main/resources/templates/trainer/workouts/view.html
  - inline CSS: No
  - inline JS: No
  - inline event handlers: Yes (onclick open/close share dialog)
  - what should be extracted: Move the share-dialog open/close behaviour out of the buttons.
  - exact destination file(s): Create src/main/resources/static/js/trainer/trainer-library-share-dialog.js and include it via a page script fragment. Pair it with a reusable dialog fragment at src/main/resources/templates/trainer/fragments/library-share-dialog.html.
  - append or create: Create new shared JS and fragment
  - fragment recommendation: Partially fragment
  - rationale: This is the third copy of the same trainer-library detail pattern, so centralising the dialog is worth the extra file.

### Remaining compliant templates

All files in this subsection have: inline CSS = No, inline JS = No, inline event handlers = No, what to extract = None, destination = N/A, append/create = N/A.

#### src\main\resources\templates
- src/main/resources/templates/base.html | fragment recommendation: Leave as-is | rationale: Shared layout shell; already the correct aggregation point for global CSS and JS imports.
- src/main/resources/templates/index.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.

#### src\main\resources\templates\achievements
- src/main/resources/templates/achievements/index.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.

#### src\main\resources\templates\admin
- src/main/resources/templates/admin/feedback.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/admin/gym-application-detail.html | fragment recommendation: Leave as-is | rationale: Review detail, messaging, and decision forms are cohesive in one admin flow; fragmenting now would not materially improve maintainability.
- src/main/resources/templates/admin/gym-applications.html | fragment recommendation: Leave as-is | rationale: Small queue page with one repeated card pattern; it is already easy to edit and does not yet justify more fragment structure.
- src/main/resources/templates/admin/off-platform-payments.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.

#### src\main\resources\templates\auth
- src/main/resources/templates/auth/confirm-logout.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.

#### src\main\resources\templates\calendar
- src/main/resources/templates/calendar/focus.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/calendar/month.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/calendar/task-detail.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/calendar/week.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.

#### src\main\resources\templates\calendar\fragments
- src/main/resources/templates/calendar/fragments/schedule-drawer-month.html | fragment recommendation: Leave as-is (already fragment) | rationale: Already extracted for reuse; no further split needed now.
- src/main/resources/templates/calendar/fragments/schedule-drawer-week.html | fragment recommendation: Leave as-is (already fragment) | rationale: Already extracted for reuse; no further split needed now.

#### src\main\resources\templates\chat
- src/main/resources/templates/chat/chat.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.

#### src\main\resources\templates\checkins
- src/main/resources/templates/checkins/client-submit.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/checkins/trainer-review.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.

#### src\main\resources\templates\client
- src/main/resources/templates/client/assessment-form.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/client/assigned-plan.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/client/my-trainer.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/client/plan.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/client/trainers.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.

#### src\main\resources\templates\conditions-preference
- src/main/resources/templates/conditions-preference/quick-preferences.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/conditions-preference/select-preferences.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/conditions-preference/view-preferences.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.

#### src\main\resources\templates\dashboard
- src/main/resources/templates/dashboard/admin-dashboard.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/dashboard/client-dashboard.html | fragment recommendation: Leave as-is | rationale: Already a thin wrapper around dashboard/fragments/client-dashboard-shell.html plus a page script.
- src/main/resources/templates/dashboard/client-dashboard-public.html | fragment recommendation: Partially fragment | rationale: The public dashboard preview has several presentational blocks that can be split into reusable read-only dashboard cards without affecting behaviour.
- src/main/resources/templates/dashboard/gym-dashboard.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/dashboard/trainer-dashboard.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.

#### src\main\resources\templates\dashboard\fragments
- src/main/resources/templates/dashboard/fragments/client-dashboard-identity.html | fragment recommendation: Leave as-is (already fragment) | rationale: Already extracted for reuse; no further split needed now.
- src/main/resources/templates/dashboard/fragments/client-dashboard-shell.html | fragment recommendation: Partially fragment | rationale: Already fragment-based, but still large enough that left-rail, main-column, and right-rail modules could be moved into separate fragment files if editing frequency stays high.

#### src\main\resources\templates\dev-mode
- src/main/resources/templates/dev-mode/hub.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/dev-mode/restricted.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/dev-mode/unauthorized.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.

#### src\main\resources\templates\error
- src/main/resources/templates/error/403.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/error/404.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/error/500.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/error/error.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.

#### src\main\resources\templates\exercise-log
- src/main/resources/templates/exercise-log/exercise-log-form.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/exercise-log/exercise-log-list.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/exercise-log/exercise-log-view.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/exercise-log/ExerciseTutorial.html | fragment recommendation: Leave as-is | rationale: Small leaf template; naming is inconsistent, but fragmentation would not help.

#### src\main\resources\templates\explore
- src/main/resources/templates/explore/index.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.

#### src\main\resources\templates\fragments
- src/main/resources/templates/fragments/banner.html | fragment recommendation: Leave as-is (already fragment) | rationale: Already extracted for reuse; no further split needed now.
- src/main/resources/templates/fragments/chatbot.html | fragment recommendation: Leave as-is (already fragment) | rationale: Already extracted for reuse; no further split needed now.
- src/main/resources/templates/fragments/daily-streak-bar.html | fragment recommendation: Leave as-is (already fragment) | rationale: Already extracted for reuse; no further split needed now.
- src/main/resources/templates/fragments/dev-mode.html | fragment recommendation: Leave as-is (already fragment) | rationale: Already extracted for reuse; no further split needed now.
- src/main/resources/templates/fragments/dev-page-display.html | fragment recommendation: Leave as-is (already fragment) | rationale: Already extracted for reuse; no further split needed now.
- src/main/resources/templates/fragments/edit-task.html | fragment recommendation: Leave as-is (already fragment) | rationale: Already extracted for reuse; no further split needed now.
- src/main/resources/templates/fragments/footer.html | fragment recommendation: Leave as-is (already fragment) | rationale: Already extracted for reuse; no further split needed now.
- src/main/resources/templates/fragments/navbar.html | fragment recommendation: Partially fragment | rationale: Role-specific navigation sections are long enough to justify smaller guest/client/trainer/gym/admin link fragments, especially if navbar edits continue.
- src/main/resources/templates/fragments/profile-modules.html | fragment recommendation: Leave as-is (already fragment) | rationale: Already extracted for reuse; no further split needed now.
- src/main/resources/templates/fragments/quick-actions.html | fragment recommendation: Leave as-is (already fragment) | rationale: Already extracted for reuse; no further split needed now.
- src/main/resources/templates/fragments/slimselectCss.html | fragment recommendation: Leave as-is (already fragment) | rationale: Already extracted for reuse; no further split needed now.
- src/main/resources/templates/fragments/slimselectJs.html | fragment recommendation: Leave as-is (already fragment) | rationale: Already extracted for reuse; no further split needed now.
- src/main/resources/templates/fragments/social-auth-buttons.html | fragment recommendation: Leave as-is (already fragment) | rationale: This is already the right reusable surface for provider buttons across auth flows, so further splitting would be unnecessary.
- src/main/resources/templates/fragments/tailwind-components.html | fragment recommendation: Leave as-is (already fragment) | rationale: Already extracted for reuse; no further split needed now.
- src/main/resources/templates/fragments/ui-shell.html | fragment recommendation: Leave as-is (already fragment) | rationale: Already extracted for reuse; no further split needed now.
- src/main/resources/templates/fragments/username-logout.html | fragment recommendation: Leave as-is (already fragment) | rationale: Already extracted for reuse; no further split needed now.

#### src\main\resources\templates\fragments\chat
- src/main/resources/templates/fragments/chat/blocks.html | fragment recommendation: Leave as-is (already fragment) | rationale: Already extracted for reuse; no further split needed now.
- src/main/resources/templates/fragments/chat/chat-widget.html | fragment recommendation: Leave as-is (already fragment) | rationale: Already extracted for reuse; no further split needed now.

#### src\main\resources\templates\fragments\goals
- src/main/resources/templates/fragments/goals/goal-chip.html | fragment recommendation: Leave as-is (already fragment) | rationale: Already extracted for reuse; no further split needed now.

#### src\main\resources\templates\fragments\workout
- src/main/resources/templates/fragments/workout/searchbar.html | fragment recommendation: Leave as-is (already fragment) | rationale: Already extracted for reuse; no further split needed now.
- src/main/resources/templates/fragments/workout/workout-frags.html | fragment recommendation: Leave as-is (already fragment) | rationale: Already extracted for reuse; no further split needed now.

#### src\main\resources\templates\goals
- src/main/resources/templates/goals/checkins.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/goals/create.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/goals/detail.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/goals/edit.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/goals/index.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.

#### src\main\resources\templates\gym-admin
- src/main/resources/templates/gym-admin/trainers.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.

#### src\main\resources\templates\gym-admin\memberships
- src/main/resources/templates/gym-admin/memberships/form.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/gym-admin/memberships/list.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/gym-admin/memberships/price-change.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/gym-admin/memberships/price-history.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.

#### src\main\resources\templates\health
- src/main/resources/templates/health/blood-pressure.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/health/blood-pressure-edit.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.

#### src\main\resources\templates\health-record
- src/main/resources/templates/health-record/health-record-form.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/health-record/health-record-list.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/health-record/health-record-view.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.

#### src\main\resources\templates\home
- src/main/resources/templates/home/auth.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/home/public.html | fragment recommendation: Partially fragment | rationale: Large public landing page; split hero, feature-tabs/card grid, role cards, and trust/CTA blocks into home/fragments only when this page is next revised.
- src/main/resources/templates/home/user.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.

#### src\main\resources\templates\homepage
- src/main/resources/templates/homepage/HomePage.html | fragment recommendation: Leave as-is | rationale: Legacy wrapper template; do not fragment further until its route/value is confirmed.

#### src\main\resources\templates\inbox
- src/main/resources/templates/inbox/index.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/inbox/thread.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.

#### src\main\resources\templates\levels
- src/main/resources/templates/levels/leaderboard.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/levels/me.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.

#### src\main\resources\templates\merch
- src/main/resources/templates/merch/admin-form.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/merch/admin-list.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/merch/checkout.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/merch/orders.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/merch/shop.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.

#### src\main\resources\templates\messages
- src/main/resources/templates/messages/client-inbox.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/messages/thread.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/messages/trainer-inbox.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.

#### src\main\resources\templates\notes
- src/main/resources/templates/notes/folders.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/notes/index.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/notes/note-form.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/notes/note-view.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.

#### src\main\resources\templates\nutrition
- src/main/resources/templates/nutrition/daily-log.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.

#### src\main\resources\templates\orders
- src/main/resources/templates/orders/orders.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.

#### src\main\resources\templates\payments
- src/main/resources/templates/payments/pricing.html | fragment recommendation: Partially fragment | rationale: Pricing tiers, comparison blocks, and checkout CTA sections are fragment-friendly if pricing copy changes often.
- src/main/resources/templates/payments/pricing-checkout.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.

#### src\main\resources\templates\policies
- src/main/resources/templates/policies/payments.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/policies/privacy.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/policies/subscription-terms.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/policies/terms.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.

#### src\main\resources\templates\profile
- src/main/resources/templates/profile/profile.html | fragment recommendation: Partially fragment | rationale: At 1426 lines this page is too large for easy editing; split the sidebar card, alerts/drawers, and main form sections, reusing fragments/profile-modules.html where possible.

#### src\main\resources\templates\public
- src/main/resources/templates/public/about.html | fragment recommendation: Partially fragment | rationale: Static marketing sections can be broken into hero, audience grids, steps, and platform features if copy iteration continues.
- src/main/resources/templates/public/faq.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/public/profile.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.

#### src\main\resources\templates\review
- src/main/resources/templates/review/form.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.

#### src\main\resources\templates\schedule
- src/main/resources/templates/schedule/add-entry.html | fragment recommendation: Partially fragment | rationale: The schedule editor mixes header, workflow, day columns, and modal-like controls; split only if this legacy flow stays active.
- src/main/resources/templates/schedule/apply.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/schedule/builder.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/schedule/builder-old.html | fragment recommendation: Leave as-is | rationale: Legacy template; prefer retirement/consolidation over further structural work.
- src/main/resources/templates/schedule/builder-redesigned.html | fragment recommendation: Leave as-is | rationale: Competing builder variant; decide the canonical flow before fragmenting.
- src/main/resources/templates/schedule/list.html | fragment recommendation: Partially fragment | rationale: The active-schedule panel and schedule-card grid could become reusable schedule fragments without changing behaviour.
- src/main/resources/templates/schedule/select-schedule.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.

#### src\main\resources\templates\super-admin
- src/main/resources/templates/super-admin/verification-detail.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/super-admin/verification-queue.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.

#### src\main\resources\templates\trainer
- src/main/resources/templates/trainer/active-clients.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/trainer/client-detail.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/trainer/client-requests.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/trainer/clients.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/trainer/library.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.

#### src\main\resources\templates\trainer\exercises
- src/main/resources/templates/trainer/exercises/create.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/trainer/exercises/edit.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/trainer/exercises/list.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.

#### src\main\resources\templates\trainer\profile
- src/main/resources/templates/trainer/profile/edit.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/trainer/profile/view.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.

#### src\main\resources\templates\trainer\programmes
- src/main/resources/templates/trainer/programmes/create.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/trainer/programmes/edit.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/trainer/programmes/list.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.

#### src\main\resources\templates\trainer\templates
- src/main/resources/templates/trainer/templates/apply.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/trainer/templates/edit.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/trainer/templates/index.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.

#### src\main\resources\templates\trainer\workouts
- src/main/resources/templates/trainer/workouts/create.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/trainer/workouts/edit.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/trainer/workouts/list.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.

#### src\main\resources\templates\tutorial
- src/main/resources/templates/tutorial/tutorial.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.

#### src\main\resources\templates\User
- src/main/resources/templates/User/forgot-password.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/User/login.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/User/login-demo.html | fragment recommendation: Leave as-is | rationale: Demo/auth page is already self-contained; normalise naming before investing in more structure.
- src/main/resources/templates/User/reset-password.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/User/signup.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/User/signup-choice.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/User/signup-client.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/User/signup-gym.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/User/signup-gym-application.html | fragment recommendation: Leave as-is | rationale: Status view plus reply form are intentionally kept together for a single applicant flow; fragmenting now would add indirection without reuse.
- src/main/resources/templates/User/signup-trainer.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/User/signup-trainer-success.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.

#### src\main\resources\templates\vault
- src/main/resources/templates/vault/index.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/vault/note-form.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/vault/note-view.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.

#### src\main\resources\templates\verify
- src/main/resources/templates/verify/email-code.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/verify/email-confirm.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/verify/phone-code.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.

#### src\main\resources\templates\workout-management
- src/main/resources/templates/workout-management/index.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.

#### src\main\resources\templates\workouts
- src/main/resources/templates/workouts/edit.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/workouts/index.html | fragment recommendation: Partially fragment | rationale: The multi-mode workout studio is cohesive but large; builder, library, and empty/card states can be split if this page keeps expanding.
- src/main/resources/templates/workouts/index-old.html | fragment recommendation: Leave as-is | rationale: Legacy template; prefer retirement/consolidation over additional fragment work.
- src/main/resources/templates/workouts/start.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.

#### src\main\resources\templates\workout-session
- src/main/resources/templates/workout-session/complete.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/workout-session/session.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.

#### src\main\resources\templates\workout-templates
- src/main/resources/templates/workout-templates/builder.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.
- src/main/resources/templates/workout-templates/index.html | fragment recommendation: Leave as-is | rationale: Compliant and appropriately scoped for its current responsibility.

## 3. Shared file recommendations

- Shared CSS files that should absorb extracted styles:
  - src/main/resources/static/css/components/calendar/day-view.css for the day-progress fill width once it is driven by JS/CSS variables instead of th:style.
  - src/main/resources/static/css/components/chat/chat-page.css only if a small shared selector/class is needed for chat accent badges after moving colour application to JS.
  - src/main/resources/static/css/components/training/workout-studio.css only if the custom exercise colour dot needs a stable selector after the inline style is removed.
- Shared JS files that should absorb extracted scripts/behaviours:
  - src/main/resources/static/js/calendar/day-enhancements.js for day-progress fill hydration.
  - src/main/resources/static/js/chat/chat-v2.js for chat accent badge colour hydration across hub/folder/thread/sidebar.
  - src/main/resources/static/js/schedule/create-workouts.js for the custom exercise colour swatch on schedule/workout.html.
- New files that should be created only if necessary:
  - src/main/resources/static/js/trainer/trainer-library-share-dialog.js because there is no existing trainer page script that cleanly owns the shared open/close dialog behaviour used by three separate trainer detail pages.
  - src/main/resources/templates/trainer/fragments/library-share-dialog.html because the share dialog markup is duplicated almost verbatim across exercise/programme/workout detail pages and fragmenting it reduces repetition without creating feature sprawl.
  - No new CSS files are justified by this audit.

## 4. Fragmentation plan

- src/main/resources/templates/trainer/fragments/library-share-dialog.html
  - source templates: trainer/exercises/view.html, trainer/programmes/view.html, trainer/workouts/view.html
  - why: removes duplicated dialog markup and aligns the three trainer-library detail pages around a shared pattern.
- src/main/resources/templates/home/fragments/* (hero, feature-tabs, role-cards, trust/CTA blocks)
  - source template: home/public.html
  - why: the page is large, largely presentational, and likely to change section-by-section rather than as one monolith.
- src/main/resources/templates/profile/fragments/* or deeper reuse of fragments/profile-modules.html
  - source template: profile/profile.html
  - why: the current profile page mixes sidebar presentation, alerts, drawers, privacy/settings controls, and the main edit form in one oversized file.
- src/main/resources/templates/fragments/navbar/* or role-specific subfragments
  - source template: fragments/navbar.html
  - why: guest/client/trainer/gym/admin nav branches are long enough to merit separation and easier role-specific edits.
- src/main/resources/templates/calendar/fragments/day-*
  - source template: calendar/day.html
  - why: the day view contains several strong section boundaries and is already one of the largest files in the repo.
- src/main/resources/templates/schedule/fragments/* for active schedule cards / control-centre panels
  - source templates: schedule/list.html, potentially schedule/add-entry.html
  - why: card and control-centre patterns are large enough to reuse, but only worth doing if the schedule flows remain active rather than being consolidated.

## 5. Risk notes

- Dynamic colours moved from Thymeleaf attributes into JS must validate the incoming colour values before applying them to style.*; do not trust arbitrary user-provided strings.
- Moving the day-progress width from server-rendered th:style to JS changes first-paint behaviour slightly; preserve the existing text/ARIA values in the HTML and let JS only update the visual width.
- The trainer-library share dialog extraction must preserve CSRF fields, form binding (shareForm), and keyboard/dialog close behaviour across all three pages.
- The repo contains legacy or overlapping templates (schedule/builder-old.html, schedule/builder-redesigned.html, workouts/index-old.html, homepage/HomePage.html, User/login-demo.html); avoid large structural refactors there until the canonical route/page is confirmed.
- profile/profile.html, calendar/day.html, home/public.html, and fragments/navbar.html are large enough that any extraction work should be done incrementally with UI checks after each step.

## 6. Final action plan

1. Remove the four chat th:style usages by switching to data-chat-accent-color and extending src/main/resources/static/js/chat/chat-v2.js.
2. Replace the single th:style usage in calendar/day.html with data-driven width initialisation in src/main/resources/static/js/calendar/day-enhancements.js plus a stable CSS rule in src/main/resources/static/css/components/calendar/day-view.css.
3. Replace the single th:style usage in schedule/workout.html with data-driven colour swatch hydration in src/main/resources/static/js/schedule/create-workouts.js.
4. Create src/main/resources/static/js/trainer/trainer-library-share-dialog.js, wire it into the three trainer detail pages, and remove the inline onclick handlers.
5. Extract the duplicated trainer-library share dialog into src/main/resources/templates/trainer/fragments/library-share-dialog.html.
6. After the nine violations are cleared, tackle structural cleanup in this order: profile/profile.html, fragments/navbar.html, home/public.html, then calendar/day.html.
7. Normalise naming when convenient: prefer lowercase directory/file names for templates and review whether legacy old/alternate builder templates can be retired.
