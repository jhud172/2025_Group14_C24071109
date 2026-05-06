# Frontend Template Structure Audit

Audit date: 2026-03-29

Repository root: `G:\No OneDrive Work\My Website\Crystal-Productions-OneToOne\One To One\2025_Group14_C24071109`

This is the current frontend template audit for the repository and supersedes the older duplicated copies that previously lived elsewhere in `docs/`.

## 1. Executive summary

- Inspected `172` active HTML/Thymeleaf files under `src/main/resources/templates`.
- Found `0` active inline `<style>` blocks and `0` active inline `<script>` blocks in the inspected templates.
- Found `9` active template violations of the repository rule set, all via `th:style` or inline event-handler attributes.
- Findings are unchanged from the previous verified baseline: the same 9 violations remain active in the current repository state.
- Main problems found: dynamic inline colour/width styling in calendar/chat/schedule templates; repeated inline dialog handlers in trainer library detail pages; several oversized templates that are still maintainable but harder to edit safely; one stale asset-path issue in `calendar/focus.html`; and naming/organisation inconsistencies such as `templates/User` and `static/css/components/Cards`.
- Priority areas to fix first:
  1. Remove inline `onclick` usage from the three trainer library detail pages and share the modal markup/behaviour.
  2. Replace `th:style` usage in chat, schedule, and calendar day templates with data attributes plus existing shared JS.
  3. Align `calendar/focus.html` with the current CSS/layout structure so it stops depending on stale stylesheet paths.
  4. Fragment only the largest mixed-responsibility templates: `profile/profile.html`, `calendar/day.html`, `home/public.html`, `fragments/navbar.html`, and the larger dashboard/schedule shells.

## 2. File-by-file audit

Legend: inline CSS includes `style` / `th:style` usage; inline JS covers literal `<script>` blocks; event handlers covers attributes such as `onclick`.

### achievements

| File path | Inline CSS | Inline JS | Event handlers | What should be extracted | Exact destination file(s) recommended | Append/create | Fragment recommendation | Short rationale |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/achievements/index.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |

### admin

| File path | Inline CSS | Inline JS | Event handlers | What should be extracted | Exact destination file(s) recommended | Append/create | Fragment recommendation | Short rationale |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/admin/feedback.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/admin/off-platform-payments.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |

### auth

| File path | Inline CSS | Inline JS | Event handlers | What should be extracted | Exact destination file(s) recommended | Append/create | Fragment recommendation | Short rationale |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/auth/confirm-logout.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |

### base.html

| File path | Inline CSS | Inline JS | Event handlers | What should be extracted | Exact destination file(s) recommended | Append/create | Fragment recommendation | Short rationale |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/base.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Shared layout shell; already centralises global CSS/JS and fragment insertion correctly. |

### calendar

| File path | Inline CSS | Inline JS | Event handlers | What should be extracted | Exact destination file(s) recommended | Append/create | Fragment recommendation | Short rationale |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/calendar/day.html` | Yes | No | No | Remove the th:style width from #day-main-progress-fill and initialise width from the existing data-progress-pct attributes instead. | `src/main/resources/static/js/calendar/day-enhancements.js` | Append to existing file | Split into fragments | Largest active template. Natural split points already exist: header/status strip, tasks panel, workouts panel, day overview, day health, and modal/drawer blocks. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/calendar/focus.html` | No | No | No | None from the template body, but the page should stop referencing stale stylesheet paths. | `Use existing src/main/resources/static/css/components/calendar/day-view.css through src/main/resources/templates/base.html / src/main/resources/static/css/app.css, or correct the direct stylesheet targets if this page remains standalone.` | Append to existing asset path/layout approach | Leave as-is | Small standalone page, but its current /css/global.css and /css/components/day-view.css links do not match the current static/css structure. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/calendar/fragments/schedule-drawer-month.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Already a reusable fragment; no inline CSS/JS was found. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/calendar/fragments/schedule-drawer-week.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Already a reusable fragment; no inline CSS/JS was found. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/calendar/month.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/calendar/task-detail.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/calendar/week.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |

### chat

| File path | Inline CSS | Inline JS | Event handlers | What should be extracted | Exact destination file(s) recommended | Append/create | Fragment recommendation | Short rationale |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/chat/chat.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/chat/folder.html` | Yes | No | No | Replace the dynamic th:style colour badge with data-colour attributes and let shared chat JS apply the swatch styling. | `src/main/resources/static/js/chat/chat-v2.js` | Append to existing file | Leave as-is | Page is otherwise compact; the only violation is rendered inline colour styling. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/chat/hub.html` | Yes | No | No | Replace the dynamic th:style colour badge with data-colour attributes and let shared chat JS apply the swatch styling. | `src/main/resources/static/js/chat/chat-v2.js` | Append to existing file | Leave as-is | Page is otherwise compact; the only violation is rendered inline colour styling. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/chat/thread.html` | Yes | No | No | Replace the dynamic th:style colour badge with data-colour attributes and let shared chat JS apply the swatch styling. | `src/main/resources/static/js/chat/chat-v2.js` | Append to existing file | Leave as-is | Thread page already uses shared chat JS; keeping the swatch logic there avoids a new file. |

### checkins

| File path | Inline CSS | Inline JS | Event handlers | What should be extracted | Exact destination file(s) recommended | Append/create | Fragment recommendation | Short rationale |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/checkins/client-submit.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/checkins/trainer-review.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |

### client

| File path | Inline CSS | Inline JS | Event handlers | What should be extracted | Exact destination file(s) recommended | Append/create | Fragment recommendation | Short rationale |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/client/assessment-form.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/client/assigned-plan.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/client/my-trainer.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/client/plan.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/client/trainers.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |

### conditions-preference

| File path | Inline CSS | Inline JS | Event handlers | What should be extracted | Exact destination file(s) recommended | Append/create | Fragment recommendation | Short rationale |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/conditions-preference/quick-preferences.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/conditions-preference/select-preferences.html` | No | No | No | None. | `n/a` | n/a | Partially fragment | Long preference editor with reusable section cards; fragment only the repeated preference-group blocks, not the whole form. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/conditions-preference/view-preferences.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |

### dashboard

| File path | Inline CSS | Inline JS | Event handlers | What should be extracted | Exact destination file(s) recommended | Append/create | Fragment recommendation | Short rationale |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/dashboard/admin-dashboard.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/dashboard/client-dashboard.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/dashboard/client-dashboard-public.html` | No | No | No | None. | `n/a` | n/a | Partially fragment | Large showcase/dashboard hybrid with clear hero, activity, and support blocks that can be separated without over-fragmenting. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/dashboard/fragments/client-dashboard-identity.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Already a reusable fragment; no inline CSS/JS was found. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/dashboard/fragments/client-dashboard-shell.html` | No | No | No | None. | `n/a` | n/a | Partially fragment | Already fragment-oriented, but 700+ lines in one file makes concurrent editing awkward. Split only by rail/main-column ownership if this file keeps growing. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/dashboard/gym-dashboard.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/dashboard/trainer-dashboard.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |

### dev-mode

| File path | Inline CSS | Inline JS | Event handlers | What should be extracted | Exact destination file(s) recommended | Append/create | Fragment recommendation | Short rationale |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/dev-mode/hub.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/dev-mode/restricted.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/dev-mode/unauthorized.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |

### error

| File path | Inline CSS | Inline JS | Event handlers | What should be extracted | Exact destination file(s) recommended | Append/create | Fragment recommendation | Short rationale |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/error/403.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/error/404.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/error/500.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/error/error.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |

### exercise-log

| File path | Inline CSS | Inline JS | Event handlers | What should be extracted | Exact destination file(s) recommended | Append/create | Fragment recommendation | Short rationale |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/exercise-log/exercise-log-form.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/exercise-log/exercise-log-list.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/exercise-log/exercise-log-view.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/exercise-log/ExerciseTutorial.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |

### explore

| File path | Inline CSS | Inline JS | Event handlers | What should be extracted | Exact destination file(s) recommended | Append/create | Fragment recommendation | Short rationale |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/explore/index.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |

### fragments

| File path | Inline CSS | Inline JS | Event handlers | What should be extracted | Exact destination file(s) recommended | Append/create | Fragment recommendation | Short rationale |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/fragments/banner.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Already a reusable fragment; no inline CSS/JS was found. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/fragments/chat/blocks.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Already a reusable fragment; no inline CSS/JS was found. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/fragments/chat/chat-widget.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Already a reusable fragment; no inline CSS/JS was found. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/fragments/chat/sidebar.html` | Yes | No | No | Replace th:style swatches on folder/thread icons with data-colour attributes and let the shared chat script paint them. | `src/main/resources/static/js/chat/chat-v2.js` | Append to existing file | Leave as-is | Already the right reusable fragment; only the colour application mechanism needs to move out of the markup. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/fragments/chatbot.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Already a reusable fragment; no inline CSS/JS was found. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/fragments/daily-streak-bar.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Already a reusable fragment; no inline CSS/JS was found. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/fragments/dev-mode.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Already a reusable fragment; no inline CSS/JS was found. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/fragments/dev-page-display.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Already a reusable fragment; no inline CSS/JS was found. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/fragments/edit-task.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Already a reusable fragment; no inline CSS/JS was found. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/fragments/footer.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Already a reusable fragment; no inline CSS/JS was found. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/fragments/goals/goal-chip.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Already a reusable fragment; no inline CSS/JS was found. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/fragments/navbar.html` | No | No | No | None. | `n/a` | n/a | Partially fragment | The fragment is correct, but desktop links, mobile drawer, and account controls are all sizeable enough to justify separate subfragments if navbar work continues. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/fragments/profile-modules.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Already a reusable fragment; no inline CSS/JS was found. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/fragments/quick-actions.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Already a reusable fragment; no inline CSS/JS was found. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/fragments/slimselectCss.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Already a reusable fragment; no inline CSS/JS was found. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/fragments/slimselectJs.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Already a reusable fragment; no inline CSS/JS was found. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/fragments/tailwind-components.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Already a reusable fragment; no inline CSS/JS was found. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/fragments/ui-shell.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Already a reusable fragment; no inline CSS/JS was found. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/fragments/username-logout.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Already a reusable fragment; no inline CSS/JS was found. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/fragments/workout/searchbar.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Already a reusable fragment; no inline CSS/JS was found. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/fragments/workout/workout-frags.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Already a reusable fragment; no inline CSS/JS was found. |

### goals

| File path | Inline CSS | Inline JS | Event handlers | What should be extracted | Exact destination file(s) recommended | Append/create | Fragment recommendation | Short rationale |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/goals/checkins.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/goals/create.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/goals/detail.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/goals/edit.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/goals/index.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |

### gym-admin

| File path | Inline CSS | Inline JS | Event handlers | What should be extracted | Exact destination file(s) recommended | Append/create | Fragment recommendation | Short rationale |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/gym-admin/memberships/form.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/gym-admin/memberships/list.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/gym-admin/memberships/price-change.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/gym-admin/memberships/price-history.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/gym-admin/trainers.html` | No | No | No | None. | `n/a` | n/a | Partially fragment | Verification list, filters, and detail actions are distinct editing units and can be split if this page changes frequently. |

### health

| File path | Inline CSS | Inline JS | Event handlers | What should be extracted | Exact destination file(s) recommended | Append/create | Fragment recommendation | Short rationale |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/health/blood-pressure.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/health/blood-pressure-edit.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |

### health-record

| File path | Inline CSS | Inline JS | Event handlers | What should be extracted | Exact destination file(s) recommended | Append/create | Fragment recommendation | Short rationale |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/health-record/health-record-form.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/health-record/health-record-list.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/health-record/health-record-view.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |

### home

| File path | Inline CSS | Inline JS | Event handlers | What should be extracted | Exact destination file(s) recommended | Append/create | Fragment recommendation | Short rationale |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/home/auth.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/home/public.html` | No | No | No | None. | `n/a` | n/a | Partially fragment | Large marketing page with natural section boundaries: hero, comparison, trust, workflow, role cards, support form, and CTA. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/home/user.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |

### homepage

| File path | Inline CSS | Inline JS | Event handlers | What should be extracted | Exact destination file(s) recommended | Append/create | Fragment recommendation | Short rationale |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/homepage/HomePage.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |

### inbox

| File path | Inline CSS | Inline JS | Event handlers | What should be extracted | Exact destination file(s) recommended | Append/create | Fragment recommendation | Short rationale |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/inbox/index.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/inbox/thread.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |

### index.html

| File path | Inline CSS | Inline JS | Event handlers | What should be extracted | Exact destination file(s) recommended | Append/create | Fragment recommendation | Short rationale |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/index.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |

### levels

| File path | Inline CSS | Inline JS | Event handlers | What should be extracted | Exact destination file(s) recommended | Append/create | Fragment recommendation | Short rationale |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/levels/leaderboard.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/levels/me.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |

### merch

| File path | Inline CSS | Inline JS | Event handlers | What should be extracted | Exact destination file(s) recommended | Append/create | Fragment recommendation | Short rationale |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/merch/admin-form.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/merch/admin-list.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/merch/checkout.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/merch/orders.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/merch/shop.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |

### messages

| File path | Inline CSS | Inline JS | Event handlers | What should be extracted | Exact destination file(s) recommended | Append/create | Fragment recommendation | Short rationale |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/messages/client-inbox.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/messages/thread.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/messages/trainer-inbox.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |

### notes

| File path | Inline CSS | Inline JS | Event handlers | What should be extracted | Exact destination file(s) recommended | Append/create | Fragment recommendation | Short rationale |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/notes/folders.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/notes/index.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/notes/note-form.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/notes/note-view.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |

### nutrition

| File path | Inline CSS | Inline JS | Event handlers | What should be extracted | Exact destination file(s) recommended | Append/create | Fragment recommendation | Short rationale |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/nutrition/daily-log.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |

### orders

| File path | Inline CSS | Inline JS | Event handlers | What should be extracted | Exact destination file(s) recommended | Append/create | Fragment recommendation | Short rationale |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/orders/orders.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |

### payments

| File path | Inline CSS | Inline JS | Event handlers | What should be extracted | Exact destination file(s) recommended | Append/create | Fragment recommendation | Short rationale |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/payments/pricing.html` | No | No | No | None. | `n/a` | n/a | Partially fragment | Plan cards, unlock-features grid, comparison table, and FAQ are clear reusable sections; fragment those, not every card. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/payments/pricing-checkout.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |

### policies

| File path | Inline CSS | Inline JS | Event handlers | What should be extracted | Exact destination file(s) recommended | Append/create | Fragment recommendation | Short rationale |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/policies/payments.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/policies/subscription-terms.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/policies/terms.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |

### profile

| File path | Inline CSS | Inline JS | Event handlers | What should be extracted | Exact destination file(s) recommended | Append/create | Fragment recommendation | Short rationale |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/profile/profile.html` | No | No | No | None. | `n/a` | n/a | Split into fragments | 1516-line account/settings/subscriptions page. Split settings panels, purchases panel, modal stack, and profile detail form into dedicated fragments for maintainability. |

### public

| File path | Inline CSS | Inline JS | Event handlers | What should be extracted | Exact destination file(s) recommended | Append/create | Fragment recommendation | Short rationale |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/public/about.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/public/faq.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/public/profile.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |

### review

| File path | Inline CSS | Inline JS | Event handlers | What should be extracted | Exact destination file(s) recommended | Append/create | Fragment recommendation | Short rationale |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/review/form.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |

### schedule

| File path | Inline CSS | Inline JS | Event handlers | What should be extracted | Exact destination file(s) recommended | Append/create | Fragment recommendation | Short rationale |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/schedule/add-entry.html` | No | No | No | None. | `n/a` | n/a | Partially fragment | Sidebar insights, apply modal, and add-entry modal are distinct UI regions that can be separated without breaking flow. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/schedule/apply.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/schedule/builder.html` | No | No | No | None. | `n/a` | n/a | Partially fragment | Designer canvas and modal controls are natural fragment candidates if this builder keeps evolving. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/schedule/builder-old.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/schedule/builder-redesigned.html` | No | No | No | None. | `n/a` | n/a | Partially fragment | Same rationale as schedule/builder.html; keep parity if either builder is refactored. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/schedule/list.html` | No | No | No | None. | `n/a` | n/a | Partially fragment | Active schedule card, schedule list, preview rail, and create-new preview are clean fragment boundaries. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/schedule/select-schedule.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/schedule/workout.html` | Yes | No | No | Replace the th:style colour dot with data-colour attributes and move swatch rendering into the existing builder JS. Also remove the JS-generated inline style string in create-workouts.js so the same violation is not reintroduced at runtime. | `src/main/resources/static/js/schedule/create-workouts.js` | Append to existing file | Leave as-is | The page already reuses fragments/workout/workout-frags.html; only the colour-dot rendering path needs cleanup. |

### super-admin

| File path | Inline CSS | Inline JS | Event handlers | What should be extracted | Exact destination file(s) recommended | Append/create | Fragment recommendation | Short rationale |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/super-admin/verification-detail.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/super-admin/verification-queue.html` | No | No | No | None. | `n/a` | n/a | Partially fragment | Queue header/filter controls and verification result cards are the meaningful split points. |

### trainer

| File path | Inline CSS | Inline JS | Event handlers | What should be extracted | Exact destination file(s) recommended | Append/create | Fragment recommendation | Short rationale |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/trainer/active-clients.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/trainer/client-detail.html` | No | No | No | None. | `n/a` | n/a | Partially fragment | Assignment cards, shared signals, and active goals are separate concerns and can become dedicated fragments. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/trainer/client-requests.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/trainer/clients.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/trainer/exercises/create.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/trainer/exercises/edit.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/trainer/exercises/list.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/trainer/exercises/view.html` | No | No | Yes | Move dialog open/close behaviour out of onclick attributes and bind it in shared JS. Also extract the repeated share dialog markup into a shared fragment reused by the trainer library detail pages. | `src/main/resources/static/js/trainer/library-share-dialog.js and src/main/resources/templates/fragments/trainer/library-share-dialog.html` | Create new shared file | Partially fragment | The same share modal behaviour and markup appears in three trainer library detail pages; a shared fragment + JS file is the cleanest option. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/trainer/library.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/trainer/profile/edit.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/trainer/profile/view.html` | No | No | No | None. | `n/a` | n/a | Partially fragment | Profile summary, verification/prompts, and review sections are distinct editing zones and could be split if this page continues to grow. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/trainer/programmes/create.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/trainer/programmes/edit.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/trainer/programmes/list.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/trainer/programmes/view.html` | No | No | Yes | Move dialog open/close behaviour out of onclick attributes and bind it in shared JS. Also extract the repeated share dialog markup into a shared fragment reused by the trainer library detail pages. | `src/main/resources/static/js/trainer/library-share-dialog.js and src/main/resources/templates/fragments/trainer/library-share-dialog.html` | Create new shared file | Partially fragment | The same share modal behaviour and markup appears in three trainer library detail pages; a shared fragment + JS file is the cleanest option. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/trainer/templates/apply.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/trainer/templates/edit.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/trainer/templates/index.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/trainer/workouts/create.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/trainer/workouts/edit.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/trainer/workouts/list.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/trainer/workouts/view.html` | No | No | Yes | Move dialog open/close behaviour out of onclick attributes and bind it in shared JS. Also extract the repeated share dialog markup into a shared fragment reused by the trainer library detail pages. | `src/main/resources/static/js/trainer/library-share-dialog.js and src/main/resources/templates/fragments/trainer/library-share-dialog.html` | Create new shared file | Partially fragment | The same share modal behaviour and markup appears in three trainer library detail pages; a shared fragment + JS file is the cleanest option. |

### tutorial

| File path | Inline CSS | Inline JS | Event handlers | What should be extracted | Exact destination file(s) recommended | Append/create | Fragment recommendation | Short rationale |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/tutorial/tutorial.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |

### User

| File path | Inline CSS | Inline JS | Event handlers | What should be extracted | Exact destination file(s) recommended | Append/create | Fragment recommendation | Short rationale |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/User/forgot-password.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/User/login.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/User/login-demo.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/User/reset-password.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/User/signup.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/User/signup-choice.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/User/signup-client.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/User/signup-gym.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/User/signup-trainer.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/User/signup-trainer-success.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |

### vault

| File path | Inline CSS | Inline JS | Event handlers | What should be extracted | Exact destination file(s) recommended | Append/create | Fragment recommendation | Short rationale |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/vault/index.html` | No | No | No | None. | `n/a` | n/a | Partially fragment | Vault search/filter rail and note list/grid are natural fragment boundaries if vault work continues. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/vault/note-form.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/vault/note-view.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |

### verify

| File path | Inline CSS | Inline JS | Event handlers | What should be extracted | Exact destination file(s) recommended | Append/create | Fragment recommendation | Short rationale |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/verify/email-code.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/verify/email-confirm.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/verify/phone-code.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |

### workout-management

| File path | Inline CSS | Inline JS | Event handlers | What should be extracted | Exact destination file(s) recommended | Append/create | Fragment recommendation | Short rationale |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/workout-management/index.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |

### workouts

| File path | Inline CSS | Inline JS | Event handlers | What should be extracted | Exact destination file(s) recommended | Append/create | Fragment recommendation | Short rationale |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/workouts/edit.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/workouts/index.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/workouts/index-old.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/workouts/start.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |

### workout-session

| File path | Inline CSS | Inline JS | Event handlers | What should be extracted | Exact destination file(s) recommended | Append/create | Fragment recommendation | Short rationale |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/workout-session/complete.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/workout-session/session.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |

### workout-templates

| File path | Inline CSS | Inline JS | Event handlers | What should be extracted | Exact destination file(s) recommended | Append/create | Fragment recommendation | Short rationale |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/workout-templates/builder.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |
| `G:/No OneDrive Work/My Website/Crystal-Productions-OneToOne/One To One/2025_Group14_C24071109/src/main/resources/templates/workout-templates/index.html` | No | No | No | None. | `n/a` | n/a | Leave as-is | Single-purpose template; no inline CSS/JS was found and assets are already externalised. |

## 3. Shared file recommendations

- Shared CSS files that should absorb extracted styles:
  - No new shared CSS extraction is required for the current violations, because the remaining inline CSS is dynamic runtime styling rather than reusable rule blocks.
  - Keep calendar day/focus presentation owned by `src/main/resources/static/css/components/calendar/day-view.css` via the existing app.css build.
  - If a reusable non-dynamic swatch helper is added later, the most sensible destination is `src/main/resources/static/css/components/core/chips.css`, not a new chat- or schedule-only stylesheet.
- Shared JS files that should absorb extracted scripts/behaviours:
  - `src/main/resources/static/js/calendar/day-enhancements.js` for day progress-bar width initialisation and updates.
  - `src/main/resources/static/js/chat/chat-v2.js` for chat folder/thread icon swatch hydration across `chat/hub.html`, `chat/folder.html`, `chat/thread.html`, and `fragments/chat/sidebar.html`.
  - `src/main/resources/static/js/schedule/create-workouts.js` for custom exercise colour-dot hydration in `schedule/workout.html` and for removing the same runtime inline style currently emitted by JS.
- New files that should be created only if necessary:
  - `src/main/resources/static/js/trainer/library-share-dialog.js` because the modal open/close logic is reused across three trainer library detail pages and no existing trainer detail-page script owns that concern.
  - `src/main/resources/templates/fragments/trainer/library-share-dialog.html` because the share-dialog markup is duplicated across `trainer/exercises/view.html`, `trainer/programmes/view.html`, and `trainer/workouts/view.html`.
  - No new CSS file is justified by the current findings.

## 4. Fragmentation plan

- `src/main/resources/templates/profile/profile.html` -> extract `fragments/profile/profile-details-form.html`, `fragments/profile/profile-settings-panels.html`, `fragments/profile/profile-purchases-panel.html`, and `fragments/profile/profile-modals.html`. This reduces a 1500+ line file into clear ownership zones without fragmenting every field.
- `src/main/resources/templates/calendar/day.html` -> extract `calendar/fragments/day-header.html`, `calendar/fragments/day-tasks-panel.html`, `calendar/fragments/day-workouts-panel.html`, and `calendar/fragments/day-modals.html`. The file already has natural panel boundaries and several modal/drawer clusters.
- `src/main/resources/templates/home/public.html` -> extract marketing sections such as `home/fragments/public-hero.html`, `home/fragments/public-workflow.html`, and `home/fragments/public-cta.html` only if the landing page will keep changing. Keep the current single-file flow if edits are rare.
- `src/main/resources/templates/fragments/navbar.html` -> split the current fragment into desktop navigation, mobile drawer, and account controls if multiple people keep touching the navbar. The current `navLinks` fragment can stay shared.
- `src/main/resources/templates/trainer/exercises/view.html`, `src/main/resources/templates/trainer/programmes/view.html`, and `src/main/resources/templates/trainer/workouts/view.html` -> extract a shared `fragments/trainer/library-share-dialog.html` fragment to remove repeated modal markup and keep the pages focused on their unique content blocks.
- `src/main/resources/templates/schedule/list.html` and `src/main/resources/templates/schedule/add-entry.html` -> extract preview/sidebar/modal blocks only. Do not fragment the central workflow canvas into tiny pieces.

## 5. Risk notes

- `calendar/day.html`: removing the initial inline width from the progress bar can introduce a short first-paint mismatch until JS runs. Use the existing `data-progress-pct` immediately in `day-enhancements.js` on DOM ready and test no-JS fallback expectations.
- `chat/*` and `fragments/chat/sidebar.html`: colour values look user-configurable. Keep sanitisation/validation on the server and treat any JS-applied colour value as untrusted input.
- `schedule/workout.html`: the template violation is mirrored in `src/main/resources/static/js/schedule/create-workouts.js`, which currently generates `<span style="background:...">` at runtime. Fix both together or the cleanup will regress immediately.
- `trainer/*/view.html`: moving modal logic out of `onclick` is low risk, but confirm keyboard/focus behaviour for `<dialog>` remains unchanged after extracting to shared JS.
- `calendar/focus.html`: currently references stale stylesheet paths. Fix this before moving other calendar styling around, otherwise the page may already be partially unstyled.
- `src/main/resources/templates/home/public.html.backup` is not an active template, but it still contains inline `<style>` and `<script>` blocks. Keep it out of the templates tree or delete/archive it to avoid future confusion.
- CDN-loaded assets still live directly in templates/fragments (`notes/index.html` for Quill, `schedule/workout.html` for Sortable, `fragments/slimselectCss.html`, `fragments/slimselectJs.html`). That is not an inline-code violation, but it is a consistency risk if you later standardise vendor loading.

## 6. Final action plan

1. Create `src/main/resources/static/js/trainer/library-share-dialog.js` and `src/main/resources/templates/fragments/trainer/library-share-dialog.html`, then switch the three trainer library detail pages away from inline `onclick` handlers.
2. Replace `th:style` in `calendar/day.html` with data-driven progress initialisation handled by `src/main/resources/static/js/calendar/day-enhancements.js`.
3. Replace chat swatch `th:style` usage in `chat/hub.html`, `chat/folder.html`, `chat/thread.html`, and `fragments/chat/sidebar.html` with data attributes handled by `src/main/resources/static/js/chat/chat-v2.js`.
4. Replace the colour-dot `th:style` in `schedule/workout.html` and the matching runtime inline style in `src/main/resources/static/js/schedule/create-workouts.js`.
5. Fix `calendar/focus.html` so it uses the current stylesheet ownership model (`base.html` + `app.css`, or the correct `components/calendar/day-view.css` path if kept standalone).
6. After the inline-code cleanup is merged, refactor only the largest templates: start with `profile/profile.html` and `calendar/day.html`, then move to `home/public.html`, `fragments/navbar.html`, and the schedule/dashboard shells if they still slow edits down.
7. As a cleanup pass, decide whether legacy files such as `workouts/index-old.html`, `schedule/builder-old.html`, and `home/public.html.backup` should remain in the active template tree.

