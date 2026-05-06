# UI Interaction Follow-up

This file records the issues that remain open after the current browser audit and source fix pass.

Audit basis:
- Browser-driven interaction sweep across client, trainer, and gym routes using the Playwright role audit.
- Source inspection across the shared dashboard, profile, calendar, and access-control files.

Important note:
- The latest browser rerun still hit the currently deployed Render bundle, so some calendar console errors shown in the browser artifacts reflect the live deployment state rather than the local source files patched in this pass.

## Open issues

### Client dashboard mobile layout is still heavily compressed
- Page: `/dashboard`
- Area: main dashboard shell at narrow mobile width
- Current behaviour: the dashboard cards collapse into a very narrow stacked column with large empty space below, and the page feels clipped rather than intentionally condensed.
- Expected behaviour: the mobile dashboard should use the intended flyout and stacked layout without the main content being squeezed into a thin strip.
- Classification: CSS layout / responsive behaviour
- Likely files:
  - [client-dashboard-refresh.css](G:/No%20OneDrive%20Work/My%20Website/Crystal-Productions-OneToOne/One%20To%20One/2025_Group14_C24071109/src/main/resources/static/css/components/dashboard/client-dashboard-refresh.css)
  - [client-dashboard-page.js](G:/No%20OneDrive%20Work/My%20Website/Crystal-Productions-OneToOne/One%20To%20One/2025_Group14_C24071109/src/main/resources/static/js/dashboard/client-dashboard-page.js)
  - [client-dashboard-shell.html](G:/No%20OneDrive%20Work/My%20Website/Crystal-Productions-OneToOne/One%20To%20One/2025_Group14_C24071109/src/main/resources/templates/dashboard/fragments/client-dashboard-shell.html)

### Weather card copy and interaction state are still mismatched
- Page: `/dashboard`
- Area: live dashboard ambience weather card
- Current behaviour: the card exposes graph-mode controls, but the wording and rendered output are not aligned cleanly. The UI reads like graph-first controls while the content reads as a mixed forecast/timeline card.
- Expected behaviour: the labels, active state, and rendered output should describe the same thing clearly, with temperature and trend modes feeling deliberate rather than ambiguous.
- Classification: HTML copy plus JS behaviour
- Likely files:
  - [client-dashboard-shell.html](G:/No%20OneDrive%20Work/My%20Website/Crystal-Productions-OneToOne/One%20To%20One/2025_Group14_C24071109/src/main/resources/templates/dashboard/fragments/client-dashboard-shell.html)
  - [client-dashboard-page.js](G:/No%20OneDrive Work/My%20Website/Crystal-Productions-OneToOne/One%20To%20One/2025_Group14_C24071109/src/main/resources/static/js/dashboard/client-dashboard-page.js)

### Navbar profile preview still falls back to placeholder values
- Pages: `/dashboard`, `/profile`, cross-role navigation
- Area: top-right floating profile preview card
- Current behaviour: the preview still shows fallback bio, zero-point, and empty-milestone content in audited logged-in flows instead of matching the signed-in user’s actual profile state.
- Expected behaviour: the preview card should render the authenticated user’s real bio, points, level, and selected milestones consistently across client, trainer, and gym views.
- Classification: backend-rendered data
- Likely files:
  - [username-logout.html](G:/No%20OneDrive%20Work/My%20Website/Crystal-Productions-OneToOne/One%20To%20One/2025_Group14_C24071109/src/main/resources/templates/fragments/username-logout.html)
  - [UserSettingsModelAdvice.java](G:/No%20OneDrive%20Work/My%20Website/Crystal-Productions-OneToOne/One%20To%20One/2025_Group14_C24071109/src/main/java/uk/ac/cf/_5/group14/One_To_One/UserSettings/UserSettingsModelAdvice.java)
  - [CurrentUserResolver.java](G:/No%20OneDrive%20Work/My%20Website/Crystal-Productions-OneToOne/One%20To%20One/2025_Group14_C24071109/src/main/java/uk/ac/cf/_5/group14/One_To_One/Security/CurrentUserResolver.java)

### Trainer and gym profile routes are still rendering the demo client profile
- Pages: `/profile` while authenticated as trainer or gym in the audit flows
- Area: profile page identity and account data
- Current behaviour: the trainer and gym profile screenshots still render the client demo profile (`Avery`, `@demo_client`) instead of role-correct account data.
- Expected behaviour: each authenticated role should see its own account data, and dev preview fallback should not override a real signed-in trainer or gym profile.
- Classification: backend-rendered data / role resolution
- Likely files:
  - [ProfileController.java](G:/No%20OneDrive%20Work/My%20Website/Crystal-Productions-OneToOne/One%20To%20One/2025_Group14_C24071109/src/main/java/uk/ac/cf/_5/group14/One_To_One/Profile/ProfileController.java)
  - [AuthHelper.java](G:/No%20OneDrive%20Work/My%20Website/Crystal-Productions-OneToOne/One%20To%20One/2025_Group14_C24071109/src/main/java/uk/ac/cf/_5/group14/One_To_One/Users/AuthHelper.java)
  - [CurrentUserResolver.java](G:/No%20OneDrive%20Work/My%20Website/Crystal-Productions-OneToOne/One%20To%20One/2025_Group14_C24071109/src/main/java/uk/ac/cf/_5/group14/One_To_One/Security/CurrentUserResolver.java)

### Client trainer-directory flow is still blocked by dev-mode restriction
- Page: `/client/trainers`
- Area: primary client trainer-discovery route
- Current behaviour: the audited client flow lands on `dev-mode/restricted?pageKey=client-trainers` instead of the trainer directory page.
- Expected behaviour: an eligible client should be able to reach the trainer directory flow directly, or the restriction should be replaced by a deliberate product lock message only where intended.
- Classification: backend routing / access control
- Likely files:
  - [SecurityConfig.java](G:/No%20OneDrive%20Work/My%20Website/Crystal-Productions-OneToOne/One%20To%20One/2025_Group14_C24071109/src/main/java/uk/ac/cf/_5/group14/One_To_One/Security/SecurityConfig.java)
  - relevant dev-mode restriction controller/interceptor files under `src/main/java/uk/ac/cf/_5/group14/One_To_One/DevMode`

### Gym admin trainer and membership routes still resolve to access denied
- Pages: `/gym/admin/trainers`, `/gym/admin/memberships`
- Area: gym admin management flows
- Current behaviour: the audited gym account lands on `Access Denied` instead of the expected admin pages.
- Expected behaviour: either the seeded gym audit account should have the correct permissions, or the route guards should be corrected so valid gym-admin users can reach these pages.
- Classification: backend routing / role-based access control
- Likely files:
  - [SecurityConfig.java](G:/No%20OneDrive%20Work/My%20Website/Crystal-Productions-OneToOne/One%20To%20One/2025_Group14_C24071109/src/main/java/uk/ac/cf/_5/group14/One_To_One/Security/SecurityConfig.java)
  - gym admin controller and role-check files under `src/main/java/uk/ac/cf/_5/group14/One_To_One/Gym`

### Live notifications SSE connection still fails repeatedly
- Pages: `/`, `/dashboard`, `/profile`, `/calendar/day/*`, and other pages that initialise notifications/chat
- Area: notification and chat live-update bootstrap
- Current behaviour: Firefox audit sessions repeatedly log failed connections to `/api/notifications/stream`, creating persistent console noise and likely disabling live notification behaviour.
- Expected behaviour: the stream should connect cleanly for authenticated sessions, or the front end should degrade silently when the stream is unavailable.
- Classification: JS behaviour plus backend streaming endpoint
- Likely files:
  - [chat.js](G:/No%20OneDrive%20Work/My%20Website/Crystal-Productions-OneToOne/One%20To%20One/2025_Group14_C24071109/src/main/resources/static/js/chat/chat.js)
  - [day-enhancements.js](G:/No%20OneDrive%20Work/My%20Website/Crystal-Productions-OneToOne/One%20To%20One/2025_Group14_C24071109/src/main/resources/static/js/calendar/day-enhancements.js)
  - notification stream controller/service files under `src/main/java/uk/ac/cf/_5/group14/One_To_One`

### Month view still needs post-deploy verification for the heatmap update path
- Pages: `/calendar?view=month` across client, trainer, and gym
- Area: month-view heatmap summary fetch and render pass
- Current behaviour: the live browser audit still logs a month-view error from `month.js:534`, even though the local source was hardened in this pass.
- Expected behaviour: once the updated source is deployed, the month heatmap should load without console errors and without breaking the calendar pane.
- Classification: JS behaviour with deployment follow-up required
- Likely files:
  - [month.js](G:/No%20OneDrive%20Work/My%20Website/Crystal-Productions-OneToOne/One%20To%20One/2025_Group14_C24071109/src/main/resources/static/js/calendar/month.js)
  - [CalendarSummaryApiController.java](G:/No%20OneDrive%20Work/My%20Website/Crystal-Productions-OneToOne/One%20To%20One/2025_Group14_C24071109/src/main/java/uk/ac/cf/_5/group14/One_To_One/ScheduleData/CalendarSummaryApiController.java)
  - [CalendarSummaryService.java](G:/No%20OneDrive%20Work/My%20Website/Crystal-Productions-OneToOne/One%20To%20One/2025_Group14_C24071109/src/main/java/uk/ac/cf/_5/group14/One_To_One/ScheduleData/CalendarSummaryService.java)

### Week view still needs post-deploy verification for the calendar log modal path
- Pages: `/calendar?view=week` across client, trainer, and gym
- Area: week-view preview and log modal interaction
- Current behaviour: the live browser audit still logs `ReferenceError: logForm is not defined`, but the local source has already been corrected in this pass.
- Expected behaviour: once the updated source is deployed, week-view interactions should run without the missing-variable error.
- Classification: JS behaviour with deployment follow-up required
- Likely files:
  - [week.js](G:/No%20OneDrive%20Work/My%20Website/Crystal-Productions-OneToOne/One%20To%20One/2025_Group14_C24071109/src/main/resources/static/js/calendar/week.js)

## Verification follow-up

After the current source changes are deployed, rerun:
- `node tools/qa/playwright-role-audit.mjs`

Then review at minimum:
- `client-dashboard-mobile-390.png`
- `client-calendar-view-month-desktop-1440.png`
- `client-calendar-view-week-desktop-1440.png`
- `trainer-profile-desktop-1440.png`
- `gym-profile-desktop-1440.png`
