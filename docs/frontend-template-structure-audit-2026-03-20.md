# Frontend Template Structure Audit

Date: 2026-03-20
Scope: `src/main/resources/templates`

## 1. Executive summary

- Overall health: mixed. The repository already has a substantial shared CSS/JS structure, and 125 of 172 audited templates are clean. The main debt is concentrated in 47 templates.
- Main problems found:
  - Large inline CSS blocks in public-facing and builder-style pages.
  - Inline bootstrap scripts that pass server data into page JS via globals.
  - Inline event handlers spread across schedule, calendar, admin, error, and trainer detail templates.
  - A few structural inconsistencies: standalone templates bypassing `base.html`, legacy template variants, inconsistent naming (`templates/User`, `components/Cards`), and duplicated small UI patterns that should be fragments.
- Priority areas to fix first:
  - Extract the large inline CSS blocks from `payments/pricing.html`, `merch/shop.html`, `workout-templates/builder.html`, `public/faq.html`, `dev-mode/unauthorized.html`, and `calendar/focus.html`.
  - Replace inline bootstrap scripts in `schedule/add-entry.html`, `notes/index.html`, `workouts/start.html`, `tutorial/tutorial.html`, `User/reset-password.html`, `User/signup-trainer-success.html`, and `workout-templates/builder.html`.
  - Remove inline handlers in `schedule/list.html`, `gym-admin/trainers.html`, `super-admin/verification-queue.html`, the four error templates, and the trainer library detail pages.
  - Clean up structural inconsistencies around `calendar/focus.html`, `tutorial/tutorial.html`, `profile/profile.html`, `home/public.html`, and the multiple old/new builder variants.

## 2. File-by-file audit

### Affected templates

Default meaning in this section:
- `append` = extend an existing CSS/JS file
- `create` = add a new file because there is no sensible existing home

- `src/main/resources/templates/calendar/day.html`
  - Inline CSS: yes, via `style="color:inherit"`
  - Inline JS: no
  - Inline event handlers: yes, `onchange` and `onsubmit`
  - Extract: warning-label inline color, daily-focus auto-submit, task-delete confirm
  - Destination: append CSS to `src/main/resources/static/css/components/calendar/day-view.css`; bind events in `src/main/resources/static/js/calendar/day-enhancements.js` or shared `src/main/resources/static/js/core/confirm-action.js` if standardised repo-wide
  - Append or create: append
  - Fragment recommendation: partially fragment
  - Rationale: very large page; task drawer and related task actions should be isolated into reusable calendar fragments

- `src/main/resources/templates/calendar/focus.html`
  - Inline CSS: yes, `<style>` block
  - Inline JS: no
  - Inline event handlers: no
  - Extract: focus-page shell, container, header, panel, fixed exit button styling
  - Destination: append to `src/main/resources/static/css/components/calendar/day-view.css`
  - Append or create: append
  - Fragment recommendation: leave as-is
  - Rationale: this is a focused day-view variant, so its shell styles belong with day-view styling; also review moving the template onto `base.html` and fixing the legacy CSS paths

- `src/main/resources/templates/calendar/month.html`
  - Inline CSS: yes, `style="display:none"` and `style="width:0%"`
  - Inline JS: yes, inline JSON script block
  - Inline event handlers: no
  - Extract: sticker pane bootstrap JSON, sticker panel hidden state, target-fill initial width
  - Destination: append CSS to `src/main/resources/static/css/components/calendar/sticker-calendar.css`; update `src/main/resources/static/js/calendar/month.js` to read JSON from a non-script container such as a hidden `<template>`/`textarea` or `data-*`
  - Append or create: append
  - Fragment recommendation: leave as-is
  - Rationale: the sticker calendar is already a discrete subsystem with matching CSS/JS files

- `src/main/resources/templates/calendar/task-detail.html`
  - Inline CSS: no
  - Inline JS: no
  - Inline event handlers: yes, delete confirm
  - Extract: delete confirmation
  - Destination: shared `src/main/resources/static/js/core/confirm-action.js`
  - Append or create: create
  - Fragment recommendation: leave as-is
  - Rationale: small page; use shared confirm binding rather than a page-specific JS file

- `src/main/resources/templates/calendar/week.html`
  - Inline CSS: yes, slider widths and initial transform
  - Inline JS: no
  - Inline event handlers: no
  - Extract: `width:300%`, `width:33.333333%`, initial slider transform
  - Destination: append classes to `src/main/resources/static/css/components/calendar/calendar.css`; keep dynamic transforms in `src/main/resources/static/js/calendar/week.js`
  - Append or create: append
  - Fragment recommendation: leave as-is
  - Rationale: this is week-view layout plumbing, not page-specific visual styling

- `src/main/resources/templates/chat/chat.html`
  - Inline CSS: yes, muted pill opacity and ring transition
  - Inline JS: no
  - Inline event handlers: no
  - Extract: muted streak-pill state and metrics-ring transition rule
  - Destination: append to `src/main/resources/static/css/components/chat/chat-page.css`
  - Append or create: append
  - Fragment recommendation: leave as-is
  - Rationale: tiny visual exceptions inside an existing chat page styling file

- `src/main/resources/templates/checkins/client-submit.html`
  - Inline CSS: no
  - Inline JS: no
  - Inline event handlers: yes, template select redirect
  - Extract: auto-redirect/auto-submit behaviour
  - Destination: shared `src/main/resources/static/js/core/form-auto-submit.js`
  - Append or create: create
  - Fragment recommendation: leave as-is
  - Rationale: this behaviour is generic and reused elsewhere

- `src/main/resources/templates/dev-mode/unauthorized.html`
  - Inline CSS: yes, full page style block
  - Inline JS: no
  - Inline event handlers: no
  - Extract: all `.devunauth-*` styles and keyframes
  - Destination: append to `src/main/resources/static/css/components/dev/dev-mode-pages.css`
  - Append or create: append
  - Fragment recommendation: leave as-is
  - Rationale: same visual domain as the other dev-mode pages; no need for a separate file

- `src/main/resources/templates/error/403.html`
  - Inline CSS: no
  - Inline JS: no
  - Inline event handlers: yes, `history.back()`
  - Extract: back-button behaviour
  - Destination: shared `src/main/resources/static/js/core/back-navigation.js`
  - Append or create: create
  - Fragment recommendation: partially fragment
  - Rationale: error pages are nearly identical; a shared action fragment and one shared JS hook will reduce duplication

- `src/main/resources/templates/error/404.html`
  - Inline CSS: no
  - Inline JS: no
  - Inline event handlers: yes, `history.back()`
  - Extract: back-button behaviour
  - Destination: shared `src/main/resources/static/js/core/back-navigation.js`
  - Append or create: create
  - Fragment recommendation: partially fragment
  - Rationale: same as above

- `src/main/resources/templates/error/500.html`
  - Inline CSS: no
  - Inline JS: no
  - Inline event handlers: yes, `history.back()`
  - Extract: back-button behaviour
  - Destination: shared `src/main/resources/static/js/core/back-navigation.js`
  - Append or create: create
  - Fragment recommendation: partially fragment
  - Rationale: same as above

- `src/main/resources/templates/error/error.html`
  - Inline CSS: no
  - Inline JS: no
  - Inline event handlers: yes, `history.back()`
  - Extract: back-button behaviour
  - Destination: shared `src/main/resources/static/js/core/back-navigation.js`
  - Append or create: create
  - Fragment recommendation: partially fragment
  - Rationale: same as above

- `src/main/resources/templates/explore/index.html`
  - Inline CSS: no
  - Inline JS: no
  - Inline event handlers: yes, sort auto-submit
  - Extract: select auto-submit
  - Destination: shared `src/main/resources/static/js/core/form-auto-submit.js`
  - Append or create: create
  - Fragment recommendation: leave as-is
  - Rationale: generic form behaviour, not page-specific logic

- `src/main/resources/templates/fragments/chat/chat-widget.html`
  - Inline CSS: no
  - Inline JS: no
  - Inline event handlers: yes, inline chat toggle
  - Extract: widget toggle handler
  - Destination: append to `src/main/resources/static/js/chat/chat.js`
  - Append or create: append
  - Fragment recommendation: leave as-is
  - Rationale: the fragment already depends on `chat.js`; use data hooks instead of inline conditionals

- `src/main/resources/templates/fragments/profile-modules.html`
  - Inline CSS: no
  - Inline JS: no
  - Inline event handlers: yes, `selectHealthRecordCharts()`
  - Extract: chart selector change binding
  - Destination: append to `src/main/resources/static/js/profile/profile-page.js`
  - Append or create: append
  - Fragment recommendation: leave as-is
  - Rationale: this handler is currently not defined anywhere, so this is both an inline-handler issue and a likely broken behaviour issue

- `src/main/resources/templates/goals/create.html`
  - Inline CSS: yes, checkbox label/input styling
  - Inline JS: no
  - Inline event handlers: no
  - Extract: archived-checkbox row styling
  - Destination: append to `src/main/resources/static/css/components/goals/goal-pages.css`
  - Append or create: append
  - Fragment recommendation: leave as-is
  - Rationale: same styling pattern also exists in `goals/edit.html`

- `src/main/resources/templates/goals/detail.html`
  - Inline CSS: yes, action row, spacing, button margins
  - Inline JS: no
  - Inline event handlers: no
  - Extract: detail action row spacing helpers and link-form spacing helpers
  - Destination: append to `src/main/resources/static/css/components/goals/goal-pages.css`
  - Append or create: append
  - Fragment recommendation: leave as-is
  - Rationale: this is a clean extension of the existing goals stylesheet

- `src/main/resources/templates/goals/edit.html`
  - Inline CSS: yes, checkbox label/input styling
  - Inline JS: no
  - Inline event handlers: no
  - Extract: archived-checkbox row styling
  - Destination: append to `src/main/resources/static/css/components/goals/goal-pages.css`
  - Append or create: append
  - Fragment recommendation: leave as-is
  - Rationale: same extracted pattern as `goals/create.html`

- `src/main/resources/templates/goals/index.html`
  - Inline CSS: yes, filter-row spacing and checkbox layout
  - Inline JS: no
  - Inline event handlers: no
  - Extract: filter-row spacing, checkbox row, checkbox input sizing
  - Destination: append to `src/main/resources/static/css/components/goals/goal-pages.css`
  - Append or create: append
  - Fragment recommendation: leave as-is
  - Rationale: this is goals-filter styling, not generic site styling

- `src/main/resources/templates/gym-admin/trainers.html`
  - Inline CSS: no
  - Inline JS: yes, modal bootstrap globals
  - Inline event handlers: yes, modal open/close
  - Extract: modal open/close event binding and bootstrap data
  - Destination: append to `src/main/resources/static/js/trainer/gym-admin-trainers-page.js`; move bootstrap data to `data-*` on the modal/root container
  - Append or create: append
  - Fragment recommendation: partially fragment
  - Rationale: page already has a page JS file; modal is a good extraction boundary if the gym-admin area grows

- `src/main/resources/templates/health/blood-pressure.html`
  - Inline CSS: no
  - Inline JS: no
  - Inline event handlers: yes, delete confirm
  - Extract: delete confirmation
  - Destination: append to `src/main/resources/static/js/health/blood-pressure-page.js` or use shared `src/main/resources/static/js/core/confirm-action.js`
  - Append or create: append if kept page-local, create if standardised shared
  - Fragment recommendation: leave as-is
  - Rationale: small page with an existing page JS file

- `src/main/resources/templates/merch/admin-list.html`
  - Inline CSS: no
  - Inline JS: no
  - Inline event handlers: yes, delete confirm
  - Extract: delete confirmation
  - Destination: shared `src/main/resources/static/js/core/confirm-action.js`
  - Append or create: create
  - Fragment recommendation: leave as-is
  - Rationale: generic confirm behaviour; no need for a dedicated admin-list JS file

- `src/main/resources/templates/merch/checkout.html`
  - Inline CSS: no
  - Inline JS: no
  - Inline event handlers: yes, card-number input sanitiser
  - Extract: numeric card-input sanitising
  - Destination: append to `src/main/resources/static/js/merch/merch-checkout-page.js`
  - Append or create: append
  - Fragment recommendation: leave as-is
  - Rationale: checkout page already has a page JS file handling the same form

- `src/main/resources/templates/merch/shop.html`
  - Inline CSS: yes, large `<style>` block plus repeated `style="..."` marketing surface accents
  - Inline JS: no
  - Inline event handlers: no
  - Extract: entire `.store-page` styling system and the repeated emerald accent surface styles
  - Destination: create `src/main/resources/static/css/components/merch/store-page.css`
  - Append or create: create
  - Fragment recommendation: partially fragment
  - Rationale: the style block is large and page-specific; putting it into a merch-specific CSS file is clearer than bloating `misc` or `app.css`

- `src/main/resources/templates/notes/index.html`
  - Inline CSS: yes, Quill/editor overrides
  - Inline JS: yes, notes bootstrap globals
  - Inline event handlers: no
  - Extract: Quill overrides and notes bootstrap data
  - Destination: create `src/main/resources/static/css/components/misc/notes.css`; append JS bootstrap reading to `src/main/resources/static/js/notes/notes.js`
  - Append or create: create CSS, append JS
  - Fragment recommendation: leave as-is
  - Rationale: there is no existing notes CSS file, and the page already has a notes JS file that should own bootstrap parsing

- `src/main/resources/templates/notes/note-form.html`
  - Inline CSS: yes, `white-space: pre-wrap`
  - Inline JS: no
  - Inline event handlers: no
  - Extract: editor white-space rule
  - Destination: `src/main/resources/static/css/components/misc/notes.css`, or replace with an existing utility class if available in current Tailwind build
  - Append or create: append if `notes.css` is created for `notes/index.html`
  - Fragment recommendation: leave as-is
  - Rationale: keep all note-editor-specific styling together

- `src/main/resources/templates/payments/pricing.html`
  - Inline CSS: yes, large `<style>` block plus many repeated accent surface styles
  - Inline JS: no
  - Inline event handlers: no
  - Extract: entire `.pricing-page` styling system and inline accent surfaces
  - Destination: create `src/main/resources/static/css/components/misc/pricing-page.css`
  - Append or create: create
  - Fragment recommendation: partially fragment
  - Rationale: this is a large page-specific visual system; a dedicated CSS file is cleaner than pushing it into `about.css` or `app.css`

- `src/main/resources/templates/policies/subscription-terms.html`
  - Inline CSS: yes, `style="display:flex..."` and hidden success message styling
  - Inline JS: no
  - Inline event handlers: yes, `legalConfirm(...)`
  - Extract: legal-confirm styling and click binding
  - Destination: use existing utility classes for the flex/hidden/text styles; replace duplicate JS with new shared `src/main/resources/static/js/policies/legal-confirmation.js`
  - Append or create: create JS, no new CSS file needed if utility classes are used
  - Fragment recommendation: partially fragment
  - Rationale: this confirmation block is duplicated almost exactly across both policy templates

- `src/main/resources/templates/policies/terms.html`
  - Inline CSS: yes, `style="display:flex..."` and hidden success message styling
  - Inline JS: no
  - Inline event handlers: yes, `legalConfirm(...)`
  - Extract: legal-confirm styling and click binding
  - Destination: use existing utility classes for the flex/hidden/text styles; replace duplicate JS with new shared `src/main/resources/static/js/policies/legal-confirmation.js`
  - Append or create: create JS, no new CSS file needed if utility classes are used
  - Fragment recommendation: partially fragment
  - Rationale: same as above

- `src/main/resources/templates/public/faq.html`
  - Inline CSS: yes, FAQ shell/style block
  - Inline JS: no
  - Inline event handlers: no
  - Extract: `.faq-*` styles
  - Destination: create `src/main/resources/static/css/components/misc/faq.css`
  - Append or create: create
  - Fragment recommendation: leave as-is
  - Rationale: there is already a page JS file for FAQ, but no CSS file; the styles are small but clearly page-specific

- `src/main/resources/templates/review/form.html`
  - Inline CSS: no
  - Inline JS: no
  - Inline event handlers: yes, star-rating `onclick`s
  - Extract: rating click handling
  - Destination: append to `src/main/resources/static/js/health/review-form-page.js`
  - Append or create: append
  - Fragment recommendation: leave as-is
  - Rationale: page already includes a review JS file; also clean up the duplicate `pageScripts` fragment block while touching this template

- `src/main/resources/templates/schedule/add-entry.html`
  - Inline CSS: yes, modal `display:none`
  - Inline JS: yes, schedule bootstrap globals
  - Inline event handlers: no
  - Extract: modal hidden state and page bootstrap data
  - Destination: use existing modal classes in `src/main/resources/static/css/components/training/schedule-studio.css`; append bootstrap reading to `src/main/resources/static/js/schedule/schedule-studio.js`
  - Append or create: append
  - Fragment recommendation: partially fragment
  - Rationale: modal markup is a clear fragment boundary, and the JS already owns the schedule-studio state machine

- `src/main/resources/templates/schedule/apply.html`
  - Inline CSS: yes, many `style="..."` attributes
  - Inline JS: no
  - Inline event handlers: yes, hover/focus inline effects
  - Extract: entire apply-page visual treatment and all hover/focus states
  - Destination: create shared `src/main/resources/static/css/components/training/schedule-apply.css`; bind any needed interaction states with CSS classes only
  - Append or create: create
  - Fragment recommendation: leave as-is
  - Rationale: `apply.html` and `select-schedule.html` share the same neon apply-flow language and should use one CSS file

- `src/main/resources/templates/schedule/list.html`
  - Inline CSS: yes, many accent surface styles and modal-card styles
  - Inline JS: no
  - Inline event handlers: yes, tabs, modal open/close, preview, confirm actions, auto-submit toggles
  - Extract: page-only accent styles, all inline click handlers, auto-submit toggles, confirm hooks
  - Destination: append CSS to `src/main/resources/static/css/components/training/training-control-centre.css`; append all page bindings to `src/main/resources/static/js/schedule/schedule-list-page.js`
  - Append or create: append
  - Fragment recommendation: partially fragment
  - Rationale: the page already has a dedicated JS file and shared TCC stylesheet; move everything there instead of adding more files

- `src/main/resources/templates/schedule/select-schedule.html`
  - Inline CSS: yes, many `style="..."` attributes
  - Inline JS: no
  - Inline event handlers: yes, hover inline effects
  - Extract: select-page visual treatment and hover states
  - Destination: create shared `src/main/resources/static/css/components/training/schedule-apply.css`
  - Append or create: create
  - Fragment recommendation: leave as-is
  - Rationale: pairs naturally with `schedule/apply.html`

- `src/main/resources/templates/super-admin/verification-queue.html`
  - Inline CSS: no
  - Inline JS: no
  - Inline event handlers: yes, row select and modal action buttons
  - Extract: all inline admin action handlers
  - Destination: append to `src/main/resources/static/js/admin/verification-queue-page.js`
  - Append or create: append
  - Fragment recommendation: partially fragment
  - Rationale: page already has a JS file; modal bodies are a reasonable fragment boundary if this screen grows further

- `src/main/resources/templates/trainer/exercises/view.html`
  - Inline CSS: no
  - Inline JS: no
  - Inline event handlers: yes, dialog open/close
  - Extract: share-dialog open/close bindings
  - Destination: create shared `src/main/resources/static/js/trainer/library-share-dialog.js`
  - Append or create: create
  - Fragment recommendation: partially fragment
  - Rationale: the share dialog is duplicated across exercise/programme/workout detail pages

- `src/main/resources/templates/trainer/profile/view.html`
  - Inline CSS: no
  - Inline JS: no
  - Inline event handlers: yes, report modal open/close
  - Extract: modal open/close bindings
  - Destination: append to `src/main/resources/static/js/trainer/trainer-profile-view-page.js`
  - Append or create: append
  - Fragment recommendation: leave as-is
  - Rationale: page already includes the matching page JS file

- `src/main/resources/templates/trainer/programmes/view.html`
  - Inline CSS: no
  - Inline JS: no
  - Inline event handlers: yes, dialog open/close
  - Extract: share-dialog open/close bindings
  - Destination: create shared `src/main/resources/static/js/trainer/library-share-dialog.js`
  - Append or create: create
  - Fragment recommendation: partially fragment
  - Rationale: same duplicated share-dialog pattern as the other library detail views

- `src/main/resources/templates/trainer/workouts/view.html`
  - Inline CSS: no
  - Inline JS: no
  - Inline event handlers: yes, dialog open/close
  - Extract: share-dialog open/close bindings
  - Destination: create shared `src/main/resources/static/js/trainer/library-share-dialog.js`
  - Append or create: create
  - Fragment recommendation: partially fragment
  - Rationale: same duplicated share-dialog pattern as the other library detail views

- `src/main/resources/templates/tutorial/tutorial.html`
  - Inline CSS: yes, progress width style
  - Inline JS: yes, theme preload and `tutorialRole` bootstrap
  - Inline event handlers: yes, next/back buttons
  - Extract: top-of-page theme bootstrap, role bootstrap, button handlers, initial progress width
  - Destination: use existing `src/main/resources/static/js/core/theme-preload.js` for theme preload; append role/data reading and click listeners to `src/main/resources/static/js/public/tutorial-page.js`; replace progress width style with a utility class or a tiny class in a new CSS file only if utilities are insufficient
  - Append or create: append; avoid new CSS unless needed
  - Fragment recommendation: leave as-is
  - Rationale: standalone page is fine, but it should still use the shared theme preload and external page JS

- `src/main/resources/templates/User/reset-password.html`
  - Inline CSS: no
  - Inline JS: yes, password visibility toggles
  - Inline event handlers: no
  - Extract: whole reset-password script
  - Destination: create `src/main/resources/static/js/auth/reset-password-page.js`
  - Append or create: create
  - Fragment recommendation: leave as-is
  - Rationale: there is no existing reset-password page JS file, and the behaviour is page-specific

- `src/main/resources/templates/User/signup-trainer-success.html`
  - Inline CSS: no
  - Inline JS: yes, `trainerCodeData`
  - Inline event handlers: no
  - Extract: trainer-code bootstrap data
  - Destination: append to `src/main/resources/static/js/auth/signup-trainer-success-page.js`; read from `data-trainer-code` or a hidden input instead of a global
  - Append or create: append
  - Fragment recommendation: leave as-is
  - Rationale: the page already has its own JS file

- `src/main/resources/templates/workouts/index.html`
  - Inline CSS: yes, `style="display:inline"`
  - Inline JS: no
  - Inline event handlers: yes, open-builder buttons and delete confirm
  - Extract: inline display style, open-builder clicks, delete confirmation
  - Destination: use existing utility class instead of inline style; append click/confirm bindings to `src/main/resources/static/js/workouts/workout-studio.js`
  - Append or create: append
  - Fragment recommendation: partially fragment
  - Rationale: this page mixes listing, library, and builder responsibilities in one large template; tab panels are a good fragment boundary

- `src/main/resources/templates/workouts/start.html`
  - Inline CSS: no
  - Inline JS: yes, workout-player bootstrap
  - Inline event handlers: yes, goal select auto-submit
  - Extract: bootstrap globals and goal auto-submit
  - Destination: append bootstrap reading to `src/main/resources/static/js/workouts/workouts-player.js`; move auto-submit to shared `src/main/resources/static/js/core/form-auto-submit.js` or the same page JS if preferred
  - Append or create: append JS, optionally create shared auto-submit helper
  - Fragment recommendation: leave as-is
  - Rationale: page already has a strong page JS boundary

- `src/main/resources/templates/workout-templates/builder.html`
  - Inline CSS: yes, full builder style block plus `display:none`
  - Inline JS: yes, template-config bootstrap global
  - Inline event handlers: no
  - Extract: full builder layout styles, hidden form state, config bootstrap
  - Destination: create `src/main/resources/static/css/components/training/workout-template-builder.css`; append bootstrap reading to `src/main/resources/static/js/workouts/workout-templates-builder-page.js`
  - Append or create: create CSS, append JS
  - Fragment recommendation: leave as-is
  - Rationale: the builder has its own component vocabulary and deserves a dedicated stylesheet

- `src/main/resources/templates/workout-templates/index.html`
  - Inline CSS: no
  - Inline JS: no
  - Inline event handlers: yes, delete confirm
  - Extract: delete confirmation
  - Destination: append to `src/main/resources/static/js/workouts/workout-templates-index-page.js`
  - Append or create: append
  - Fragment recommendation: leave as-is
  - Rationale: page already has a dedicated JS file

### Clean templates

Default status for every template in this subsection:
- Inline CSS: no
- Inline JS: no
- Inline event handlers: no
- Extract: none
- Destination: n/a
- Append/create: n/a
- Fragment recommendation: leave as-is unless noted in section 4

- `src/main/resources/templates/achievements`: `index.html`
- `src/main/resources/templates/admin`: `feedback.html`, `off-platform-payments.html`
- `src/main/resources/templates/auth`: `confirm-logout.html`
- `src/main/resources/templates`: `base.html`, `index.html`
- `src/main/resources/templates/calendar/fragments`: `schedule-drawer-month.html`, `schedule-drawer-week.html`
- `src/main/resources/templates/chat`: `folder.html`, `hub.html`, `thread.html`
- `src/main/resources/templates/checkins`: `trainer-review.html`
- `src/main/resources/templates/client`: `assessment-form.html`, `assigned-plan.html`, `my-trainer.html`, `plan.html`, `trainers.html`
- `src/main/resources/templates/conditions-preference`: `quick-preferences.html`, `select-preferences.html`, `view-preferences.html`
- `src/main/resources/templates/dashboard`: `admin-dashboard.html`, `client-dashboard.html`, `client-dashboard-public.html`, `gym-dashboard.html`, `trainer-dashboard.html`
- `src/main/resources/templates/dashboard/fragments`: `client-dashboard-identity.html`, `client-dashboard-shell.html`
- `src/main/resources/templates/dev-mode`: `hub.html`, `restricted.html`
- `src/main/resources/templates/exercise-log`: `exercise-log-form.html`, `exercise-log-list.html`, `exercise-log-view.html`, `ExerciseTutorial.html`
- `src/main/resources/templates/fragments`: `banner.html`, `chatbot.html`, `daily-streak-bar.html`, `dev-mode.html`, `dev-page-display.html`, `edit-task.html`, `footer.html`, `navbar.html`, `quick-actions.html`, `slimselectCss.html`, `slimselectJs.html`, `tailwind-components.html`, `ui-shell.html`, `username-logout.html`
- `src/main/resources/templates/fragments/chat`: `blocks.html`, `sidebar.html`
- `src/main/resources/templates/fragments/goals`: `goal-chip.html`
- `src/main/resources/templates/fragments/workout`: `searchbar.html`, `workout-frags.html`
- `src/main/resources/templates/goals`: `checkins.html`
- `src/main/resources/templates/gym-admin/memberships`: `form.html`, `list.html`, `price-change.html`, `price-history.html`
- `src/main/resources/templates/health`: `blood-pressure-edit.html`
- `src/main/resources/templates/health-record`: `health-record-form.html`, `health-record-list.html`, `health-record-view.html`
- `src/main/resources/templates/home`: `auth.html`, `public.html`, `user.html`
- `src/main/resources/templates/homepage`: `HomePage.html`
- `src/main/resources/templates/inbox`: `index.html`, `thread.html`
- `src/main/resources/templates/levels`: `leaderboard.html`, `me.html`
- `src/main/resources/templates/merch`: `admin-form.html`, `orders.html`
- `src/main/resources/templates/messages`: `client-inbox.html`, `thread.html`, `trainer-inbox.html`
- `src/main/resources/templates/notes`: `folders.html`, `note-view.html`
- `src/main/resources/templates/nutrition`: `daily-log.html`
- `src/main/resources/templates/orders`: `orders.html`
- `src/main/resources/templates/payments`: `pricing-checkout.html`
- `src/main/resources/templates/policies`: `payments.html`
- `src/main/resources/templates/profile`: `profile.html`
- `src/main/resources/templates/public`: `about.html`, `profile.html`
- `src/main/resources/templates/schedule`: `builder.html`, `builder-old.html`, `builder-redesigned.html`, `workout.html`
- `src/main/resources/templates/super-admin`: `verification-detail.html`
- `src/main/resources/templates/trainer`: `active-clients.html`, `client-detail.html`, `client-requests.html`, `clients.html`, `library.html`
- `src/main/resources/templates/trainer/exercises`: `create.html`, `edit.html`, `list.html`
- `src/main/resources/templates/trainer/profile`: `edit.html`
- `src/main/resources/templates/trainer/programmes`: `create.html`, `edit.html`, `list.html`
- `src/main/resources/templates/trainer/templates`: `apply.html`, `edit.html`, `index.html`
- `src/main/resources/templates/trainer/workouts`: `create.html`, `edit.html`, `list.html`
- `src/main/resources/templates/User`: `forgot-password.html`, `login.html`, `login-demo.html`, `signup.html`, `signup-choice.html`, `signup-client.html`, `signup-gym.html`, `signup-trainer.html`
- `src/main/resources/templates/vault`: `index.html`, `note-form.html`, `note-view.html`
- `src/main/resources/templates/verify`: `email-code.html`, `email-confirm.html`, `phone-code.html`
- `src/main/resources/templates/workout-management`: `index.html`
- `src/main/resources/templates/workouts`: `edit.html`, `index-old.html`
- `src/main/resources/templates/workout-session`: `complete.html`, `session.html`

## 3. Shared file recommendations

### Shared CSS files that should absorb extracted styles

- `src/main/resources/static/css/components/calendar/day-view.css`
  - absorb `calendar/focus.html` focus-shell styles
  - absorb the one-off label inheritance fix from `calendar/day.html`
- `src/main/resources/static/css/components/calendar/sticker-calendar.css`
  - absorb `calendar/month.html` sticker panel initial state rules
- `src/main/resources/static/css/components/calendar/calendar.css`
  - absorb `calendar/week.html` slider-track and pane-slot classes
- `src/main/resources/static/css/components/chat/chat-page.css`
  - absorb the two inline chat style exceptions from `chat/chat.html`
- `src/main/resources/static/css/components/dev/dev-mode-pages.css`
  - absorb all `.devunauth-*` rules from `dev-mode/unauthorized.html`
- `src/main/resources/static/css/components/goals/goal-pages.css`
  - absorb inline goal filter, checkbox, action-row, and spacing rules from `goals/create.html`, `goals/edit.html`, `goals/index.html`, and `goals/detail.html`
- `src/main/resources/static/css/components/training/training-control-centre.css`
  - absorb `schedule/list.html` empty-state, modal-card, and accent surface styling

### Shared JS files that should absorb extracted scripts/handlers

- `src/main/resources/static/js/calendar/day-enhancements.js`
  - absorb `calendar/day.html` page-local form/drawer bindings
- `src/main/resources/static/js/calendar/month.js`
  - read sticker bootstrap data from a non-script container
- `src/main/resources/static/js/chat/chat.js`
  - bind chat widget toggle from data attributes instead of inline JS
- `src/main/resources/static/js/notes/notes.js`
  - read bootstrap values from `data-*`/hidden markup instead of `window.__notesBootstrap`
- `src/main/resources/static/js/public/tutorial-page.js`
  - bind next/back buttons and read role from markup instead of globals
- `src/main/resources/static/js/schedule/schedule-list-page.js`
  - bind tab, modal, preview, duplicate, delete, and toggle events from data attributes
- `src/main/resources/static/js/schedule/schedule-studio.js`
  - read schedule bootstrap data from markup instead of globals
- `src/main/resources/static/js/trainer/gym-admin-trainers-page.js`
  - bind modal actions and read bootstrap data from the DOM
- `src/main/resources/static/js/trainer/trainer-profile-view-page.js`
  - bind report modal from data attributes
- `src/main/resources/static/js/workouts/workouts-player.js`
  - read player bootstrap data from markup instead of `window.__workoutPlayerBootstrap`
- `src/main/resources/static/js/workouts/workout-studio.js`
  - own the workouts index create/delete button bindings
- `src/main/resources/static/js/workouts/workout-templates-builder-page.js`
  - read initial config from non-script markup
- `src/main/resources/static/js/workouts/workout-templates-index-page.js`
  - bind delete confirmation without inline `onclick`
- `src/main/resources/static/js/merch/merch-checkout-page.js`
  - own the card-input sanitiser instead of inline `oninput`
- `src/main/resources/static/js/health/review-form-page.js`
  - own the star-rating click handling
- `src/main/resources/static/js/profile/profile-page.js`
  - own the health-record chart selector binding

### New files that should be created only where necessary

- `src/main/resources/static/css/components/merch/store-page.css`
  - justified because `merch/shop.html` contains a large page-specific style system with its own `.store-*` namespace
- `src/main/resources/static/css/components/misc/pricing-page.css`
  - justified because `payments/pricing.html` contains a large page-specific style system with its own `.pricing-*` namespace
- `src/main/resources/static/css/components/misc/faq.css`
  - justified because `public/faq.html` has page-specific styles and no current CSS home
- `src/main/resources/static/css/components/misc/notes.css`
  - justified because notes editor styling is currently split between inline styles and note templates with no dedicated CSS file
- `src/main/resources/static/css/components/training/schedule-apply.css`
  - justified because `schedule/apply.html` and `schedule/select-schedule.html` share a distinct mini-flow not covered by current training CSS files
- `src/main/resources/static/css/components/training/workout-template-builder.css`
  - justified because `workout-templates/builder.html` has a self-contained builder layout and component vocabulary
- `src/main/resources/static/js/core/form-auto-submit.js`
  - justified because the same inline auto-submit pattern appears in multiple unrelated templates
- `src/main/resources/static/js/core/confirm-action.js`
  - justified because confirm prompts are repeated across calendar, merch, workouts, health, and schedule templates
- `src/main/resources/static/js/core/back-navigation.js`
  - justified because all four error templates duplicate the same back-button behaviour
- `src/main/resources/static/js/policies/legal-confirmation.js`
  - justified because `terms-page.js` and `subscription-terms-page.js` currently duplicate the same behaviour
- `src/main/resources/static/js/trainer/library-share-dialog.js`
  - justified because the exercise/programme/workout detail pages share the same dialog interaction pattern
- `src/main/resources/static/js/auth/reset-password-page.js`
  - justified because reset-password behaviour is page-specific and currently has no JS home

## 4. Fragmentation plan

### Reusable fragments recommended

- `src/main/resources/templates/fragments/trainer/share-dialog.html`
  - source templates: `trainer/exercises/view.html`, `trainer/programmes/view.html`, `trainer/workouts/view.html`
  - why: the share dialog structure is materially duplicated three times with the same open/close behaviour

- `src/main/resources/templates/fragments/policies/legal-confirmation.html`
  - source templates: `policies/terms.html`, `policies/subscription-terms.html`
  - why: identical confirmation card with only the confirmation key and copy changing

- `src/main/resources/templates/fragments/error/action-stack.html`
  - source templates: `error/error.html`, `error/403.html`, `error/404.html`, `error/500.html`
  - why: the action area is duplicated and only needs one shared back-button/data hook pattern

- `src/main/resources/templates/calendar/fragments/task-drawer.html`
  - source templates: primarily `calendar/day.html`
  - why: task drawer markup and related controls are a clear editing unit inside a 1000+ line page

- `src/main/resources/templates/schedule/fragments/studio-modals.html`
  - source templates: `schedule/add-entry.html`
  - why: the apply modal and add-entry modal are self-contained UI blocks that make the main flow harder to scan

- `src/main/resources/templates/workouts/fragments/index-builder-panel.html`
  - source templates: `workouts/index.html`
  - why: the page currently mixes list, library, and builder markup in one template; splitting by tab panel will improve editability

### Partial fragmentation recommended, but not blanket fragmentation

- `src/main/resources/templates/home/public.html`
  - split into section fragments such as hero, trust, workflow, roles, gallery, and final CTA
  - reason: 795-line marketing template is hard to edit safely, but the sections are already clearly delineated

- `src/main/resources/templates/profile/profile.html`
  - split out drawers/modals and possibly the sidebar card fragment
  - reason: 1516-line account page already has clear UI modules; fragmenting the whole file would be overkill, but modal/drawer extraction would pay off

- `src/main/resources/templates/payments/pricing.html`
  - split into plan grid, comparison/assurance blocks, and FAQ
  - reason: once CSS is extracted, the template will still be large and marketing-section oriented

- `src/main/resources/templates/merch/shop.html`
  - split hero, highlights, product grid, and FAQ/assurance blocks if the page continues to grow
  - reason: page has a clear section model but does not yet need extreme fragmenting

- `src/main/resources/templates/super-admin/verification-queue.html`
  - split modal markup into fragments if more actions are added
  - reason: the list/detail layout is understandable today, but the modal cluster is the first maintainability hotspot

- `src/main/resources/templates/gym-admin/trainers.html`
  - split update-notes modal if more trainer actions are added
  - reason: current template is still manageable after handler extraction

### Leave as-is where fragmenting would be unnecessary

- Most small forms, lists, and simple detail templates under `client`, `verify`, `health-record`, `auth`, `conditions-preference`, `messages`, and `workout-session`
- Existing fragment libraries like `dashboard/fragments/client-dashboard-shell.html`, `fragments/navbar.html`, and `calendar/fragments/*`

## 5. Risk notes

- `src/main/resources/templates/fragments/profile-modules.html`
  - `selectHealthRecordCharts()` is referenced inline but no implementation exists in the repository. This needs manual review because the current behaviour may already be broken.

- `src/main/resources/templates/calendar/focus.html`
  - Uses legacy asset paths (`/css/global.css`, `/css/components/day-view.css`) and bypasses `base.html`. Move carefully so the page does not lose theme or shared metadata behaviour.

- `src/main/resources/templates/tutorial/tutorial.html`
  - Also bypasses `base.html` and currently duplicates theme-preload behaviour inline. If moved to shared infrastructure, verify first paint/theme flash behaviour.

- `src/main/resources/templates/schedule/add-entry.html`
  - The page JS currently expects global variables. Converting to `data-*`/hidden markup needs careful escaping for arrays/JSON.

- `src/main/resources/templates/notes/index.html`
  - `activeNoteContent` can contain HTML. Do not push raw note HTML into unsafe `data-*` attributes without escaping strategy; use a hidden container or template node.

- `src/main/resources/templates/workout-templates/builder.html`
  - `configJson` is JSON text and should not be shoved into an unsafe attribute without encoding; a hidden `<textarea>`/`template>` is safer.

- `src/main/resources/templates/workouts/start.html`
  - Workout player bootstrap includes CSRF values and session id. Keep those bindings intact while removing globals.

- `src/main/resources/templates/gym-admin/trainers.html`
  - Modal auto-open depends on server-provided data. Preserve that server-driven state when replacing inline bootstrap globals.

- `src/main/resources/templates/policies/terms.html` and `src/main/resources/templates/policies/subscription-terms.html`
  - Current JS mutates `style.display`; if moved to class toggles, update both JS and markup at the same time.

- `src/main/resources/templates/review/form.html`
  - Contains duplicate `pageScripts` fragment declarations. That should be cleaned up during the handler extraction.

- Legacy/duplicate template variants
  - `workouts/index-old.html`, `schedule/builder-old.html`, `schedule/builder-redesigned.html`, `homepage/HomePage.html`, and the non-audited `home/public.html.backup` increase ambiguity about which frontend structure is canonical.

## 6. Final action plan

1. Create the shared JS utilities first: `core/form-auto-submit.js`, `core/confirm-action.js`, `core/back-navigation.js`, `policies/legal-confirmation.js`, and `trainer/library-share-dialog.js`.
2. Extract the largest inline CSS blocks into dedicated files: `pricing-page.css`, `store-page.css`, `faq.css`, `notes.css`, `schedule-apply.css`, and `workout-template-builder.css`.
3. Update existing page JS files to read bootstrap data from `data-*`, hidden inputs, or hidden template nodes instead of inline globals.
4. Replace inline event handlers with `data-*` hooks wired in either the shared utilities or existing page JS files.
5. Remove leftover `style="..."` attributes by either moving rules into the target CSS files or replacing them with existing utility classes where that is cleaner.
6. After extraction, tackle the best-value fragment work only: trainer share dialog, policy legal confirmation, error action stack, schedule studio modals, calendar task drawer, and workouts index tab panels.
7. Finish with a regression pass on the risky templates: `calendar/focus.html`, `tutorial/tutorial.html`, `schedule/add-entry.html`, `notes/index.html`, `workout-templates/builder.html`, `gym-admin/trainers.html`, and the policy confirmation pages.
