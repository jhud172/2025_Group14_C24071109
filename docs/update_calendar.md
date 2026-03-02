# Calendar (Month + Week) + Schedules Panel Upgrade Tasks

## Instructions for Agent
- Implement every task in this file in order.
- Keep the design premium and minimal; no feature bloat.
- Ensure everything works in both light mode and dark mode.
- Prefer smooth transitions over full page reloads.
- Ensure hover interactions also have a mobile-friendly equivalent (press-and-hold).
- Ensure all new UI has clear empty states and does not add clutter.
- Tick tasks off as completed.

---

# Schedules Panel (when clicking “Schedules”) — Make it useful, not just data

## Panel layout + purpose
- [ ] Redesign the schedules panel so it reads as a “Schedule Control Centre” rather than a list of cards, keeping it compact and consistent with the calendar styling.
- [ ] Add a clear top header state with a short description of what schedules do and what “Apply” means, but keep it subtle and not wordy.
- [ ] Ensure the panel has a consistent visual rhythm: header → filters → schedule list → actions.

## Filters that add value without clutter
- [ ] Replace the current “Visible range / Selected day / Entries in range” blocks with a more useful summary row that tells the user what they’re looking at in plain language.
- [ ] Add simple filter chips that are actually useful: Active, Archived, Favourites, Applied this month, Applies to this view (Month/Week).
- [ ] Add a “Sort” control with meaningful options such as Recently applied, Most used, Upcoming soonest, Name.

## Schedule cards (make them actionable and informative)
- [ ] Redesign each schedule card to show only the useful “at-a-glance” info, and hide the rest behind an expandable “Details” area.
- [ ] Each schedule card should show a clean summary: schedule name, schedule type, frequency, active days, and a small “status pill” (Active / Archived / Applied / Not applied).
- [ ] Add “Next occurrence” preview on each card (what day(s) it will hit next), so the schedule feels alive.
- [ ] Add “Last applied” as a subtle line, not a main field.
- [ ] Ensure schedule cards have premium hover states and do not feel clunky.

## Actions (make them flow)
- [ ] Replace the row of small buttons (Edit / Duplicate / Archive / Apply) with a cleaner action layout that matches the site (primary action + secondary menu).
- [ ] Make “Apply” the primary action, and make it clear what it applies to (visible range / selected day / custom range) without being confusing.
- [ ] When applying a schedule, show a short confirmation preview (what will be added and where), then allow confirm/cancel, so users don’t fear pressing Apply.
- [ ] Add a lightweight “Undo” option after applying, so the experience feels safe and premium.

## Quick preview (so it’s not just numbers)
- [ ] Add an expandable “Preview week” mini section inside a schedule card that shows a simple preview of which days get workouts/tasks (no heavy UI, just clear dots/badges).
- [ ] Add conflict awareness: if applying will overlap or replace existing items, show a subtle warning and what will happen.

## Favourites + pinning (small but high value)
- [ ] Make the star icon meaningful: allow users to favourite schedules and pin favourites to the top when filtered/sorted.
- [ ] Ensure favourite state is visible and consistent in dark mode.

## Search and empty states
- [ ] Improve “Search schedules…” so it filters smoothly and highlights matches.
- [ ] Add good empty states for: no schedules, no matches, all archived, etc, with a single clear action to create a schedule.

## Create schedule CTA
- [ ] Redesign the “Create new schedule” CTA so it looks premium, consistent, and doesn’t compete visually with the schedule list.
- [ ] Add a short hint under the CTA describing what a schedule is used for, but keep it subtle.

---

# Week View + Month View — Fixes + polish you listed

## Hover + interaction parity
- [ ] Ensure hover previews are active and working in Week View exactly like Month View.
- [ ] Add mobile support: press-and-hold triggers the hover preview and releases to close.

## Remove unwanted symbols (Week view only)
- [ ] Remove the dot, squiggly line, and star symbols from the top of week cards (these should not appear at all).

## Today highlight (Week + Month)
- [ ] Highlight which day is today in the Mon–Sun header and keep the “Today” badge on the card as well for both week and month view.

## Card typography (Week view only)
- [ ] Reduce the overpowering bold date number in week cards so it doesn’t dominate the content, while keeping it readable.

## Card separators (Week + Month)
- [ ] Ensure the tasks/workouts separator appears on every card including Today, for both week and month view.

## Date range display (Week view)
- [ ] Replace the current week title display with “Mar 2nd – Mar 8th” and show the year beneath it on a separate subtle line.

## Swipe/drag navigation (Week + Month)
- [ ] Implement swipe/drag navigation for week and month that does not reload the page and transitions smoothly.
- [ ] Update Next/Prev buttons to use the same smooth transition (no reload).
- [ ] Ensure month view drag works even when dragging on cards (drag-to-scroll should not feel blocked or inconsistent).

## Workout heatmap (Week view)
- [ ] Replace the current random/digit-heavy heatmap with something that actually communicates value, such as:
  - workload intensity bands,
  - completed vs planned,
  - adherence indicator,
  - “busy days” highlight,
  while keeping it compact and on-theme.

## Schedules button visibility (Week + Month)
- [ ] Fix the Schedules button text/contrast in dark mode so it’s clearly readable and matches theme.

## Jump-to controls (Week + Month)
- [ ] Fix all Jump-to buttons so they work correctly in both views.
- [ ] Ensure jumping transitions smoothly and does not reload the page.
- [ ] Fix the date picker UI in dark mode so it isn’t bright white.

## Hover content correctness (Week + Month)
- [ ] Fix hover preview content for scheduled workouts so it labels the item as “Workout” not “Schedule”.
- [ ] In workout hover preview, show:
  - workout name,
  - time,
  - complete workout action,
  - and “From: <schedule name>” as a subtle footer line.
- [ ] Keep task hover preview as-is since it’s already correct.

## Month day labels (Month view)
- [ ] Update month day numbers to include ordinal suffixes (2nd, 3rd, 28th) in a clean, subtle way.

## Expand mode behaviour (Month view)
- [ ] Fix expand mode so it does not endlessly create months.
- [ ] Only spawn next/previous month when the user scrolls near the edge (halfway onto screen), based on scroll direction.
- [ ] Add an information icon on the expand button and a short tooltip explaining how expand works.

## Motivation feature (Month + Week)
- [ ] Remove the “Motivation View” mode toggle and keep only a single Motivation button.
- [ ] Motivation button should display the monthly goal progress + month stats + motivational message (without changing the calendar layout).
- [ ] Add a motivation badge on day cards (top-right) without shifting card layout.
- [ ] Badge hover/hold should show a quick message:
  - if at least one task OR workout completed, show “Completed something today”.
  - if at least one task AND one workout completed, upgrade badge to a stronger star with a pulse animation and message “Task + workout completed”.

## Free day design (Week + Month)
- [ ] Replace “Free day” text with a large plus icon inside a rounded box to signal “Add something here”, while keeping it subtle and on-theme.

## Title syncing during transitions (Week + Month)
- [ ] Ensure during smooth week/month transitions the displayed month/week name updates correctly with the new range and never desyncs.

---

# Final checks
- [ ] Verify week view and month view work well on small screens without content becoming unreadable or squashed.
- [ ] Verify all hover previews have a mobile equivalent (press-and-hold).
- [ ] Verify dark mode contrast and readability everywhere, including buttons and date picker.
- [ ] Verify transitions are smooth and do not jitter or reload content unnecessarily.