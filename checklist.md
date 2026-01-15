# Checklist

> Copilot Instructions (READ FIRST)
>
> You are an autonomous implementation + testing agent.
>
> You must:
> - Work through this checklist TOP → BOTTOM.
> - Implement EVERY item.
> - Create tests for EVERY item.
> - Tick checkboxes ONLY when:
>   1) Feature is implemented
>   2) Tests exist
>   3) Tests pass locally
>   4) Tests pass in CI
>
> Do not skip items. Do not batch items together.
> Make small, safe changes that fit the existing Spring Boot + Thymeleaf + Tailwind stack.


## 1) Day Header redesign (clean + futuristic but minimal)
### Goal
Make top of day page feel like a “hub” without adding clutter.

- [x] Replace the current header with a “Day Hub Header” layout: (Day header now includes right-side status pills; tests: `./gradlew test`)
  - Left: “Calendar” label + “Day view” title
  - Right: compact pills for `Timed Focus`, `Daily Focus` status, and `Completion %`
- [x] Add subtle background effect using Tailwind only: (Added dark ring crispness on hub header; tests: `./gradlew test`)
  - Use `bg-white/70 backdrop-blur border shadow-sm` in light
  - Use `dark:bg-slate-900/50 dark:border-slate-800` in dark
  - Add `ring-1 ring-white/20` in dark for crispness
- [x] Add a small “Today” badge if date == today. (Rendered badge when viewing today; tests: `./gradlew test`)

### Tests
- [x] MVC test: GET `/calendar/day/{date}` renders header and includes `data-testid="day-hub-header"`. (Added `CalendarDayHubHeaderMvcTest`; tests: `./gradlew test`)
- [x] Assert the title contains the date string. (Asserted `2026-01-15` is present; tests: `./gradlew test`)
- [x] If today date is used, verify “Today” badge renders. (Added today-based MVC assertion; tests: `./gradlew test`)

---

## 2) Global streak bar across the site (colour-coded)
### Goal
A streak strip that shows each day status:
- Grey = no completions (or no items to complete, depending on rules)
- Green = fully completed
- Orange = partially completed
- Red = skipped (items existed but none completed / no logs and day elapsed)

### Implementation
- [ ] Ensure a DB table exists to justify the bar: `daily_completion` with fields:
  - user_id, date (composite key)
  - completion_status (ENUM: GREY, GREEN, ORANGE, RED)
  - completion_percentage (0–100)
  - updated_at
- [ ] Create/confirm a service that calculates status for a date range (e.g. 14–30 days):
  - based on tasks completed + workout sessions completed + required logs.
  - status rules must be deterministic and unit-testable.
- [ ] Update `fragments/daily-streak-bar` to:
  - Render a horizontal row of small pills/dots
  - Each item has colour class based on status
  - Each item shows `x/y` and `%` on hover (tooltip)
  - Tooltip shows breakdown: `tasks left`, `workouts left`, `logs needed`
- [ ] Tooltip must be accessible:
  - uses `aria-describedby`
  - keyboard focusable
  - also visible on focus, not just hover
- [ ] Clicking a streak day navigates to that day view.

### Tests
- [ ] Unit tests for status calculation: GREEN/ORANGE/RED/GREY scenarios.
- [ ] Repository test: save + fetch daily_completion for a range works.
- [ ] MVC test: streak bar renders N items and includes tooltip content.

---

## 3) Day “Timed Focus” visual theme (morning/noon/evening/night)
### Goal
Make the day page subtly change “mood” based on timed focus, without being loud.

### Implementation
- [ ] Add a `data-time-theme` attribute on `<body>` or the main wrapper:
  - values: `morning|midday|evening|night`
- [ ] In Tailwind only, apply theme accents by conditionally adding classes:
  - Morning: slightly warmer (amber highlight)
  - Midday: neutral (blue)
  - Evening: purple/indigo
  - Night: slate/emerald
- [ ] Use minimal accent: top border gradient line OR a small glow behind header.

### Tests
- [ ] MVC test: timed focus value appears and wrapper has correct `data-testid="timed-focus"` + theme value.

---

## 4) “Daily Focus” becomes a quick “pin” with editable dropdown
### Goal
Keep Daily Focus, but present it as a clean pinned selection.

### Implementation
- [ ] Refactor Daily Focus section to a compact card:
  - Left: label + short description
  - Right: select dropdown and Save
- [ ] Add “Set to item in today” dropdown:
  - options include tasks + scheduled workouts + “Custom focus”
- [ ] When saved, store to `daily_focus` table for (user, date).

### Tests
- [ ] Unit test: daily focus save updates DB record.
- [ ] MVC test: POST `/calendar/day/{date}/daily-focus` redirects back and persists.

---

## 5) “Day Health” becomes context-aware (past + future)
### Goal
AI-like advice must be meaningful: not just “today”, but compares:
- yesterday/last week patterns
- tomorrow/upcoming heavy day
- week load balance suggestion

### Implementation
- [ ] Update day health generator to accept:
  - current day counts
  - previous 7 days completion patterns
  - next 7 days scheduled load (tasks + workouts)
- [ ] Render Day Health as:
  - short primary message
  - 2 bullet “suggestions”
  - 1 “watch out” if tomorrow is heavy
- [ ] Ensure message rotates (no repeated same phrasing):
  - Use a small template pool and pick based on hash(date+userId) or random with seed.

### Tests
- [ ] Unit test: day health output changes when next-day load changes.
- [ ] Unit test: output differs across at least two seeded days (no identical message).
- [ ] MVC test: section appears only when dayHealth non-empty.

---

## 6) Task area overhaul: clean, powerful, not cluttered
### Goal
Tasks are the main interaction point. Clean list + deep detail on click.

### UI rules
- [ ] Replace “Edit” button with a kebab menu (⋯) per task (Tailwind + minimal JS).
- [ ] Clicking a task opens a detail drawer (right side on desktop, bottom sheet on mobile):
  - title, time, notes, warnings, log status
  - actions: complete toggle, edit fields, delete
- [ ] Keep the main list minimal: title + time + status chip only.

### Ordering & layout preferences
- [ ] Keep existing user preference:
  - ordering: chronological vs alphabetical
  - layout: combined vs separated
- [ ] Add a new preference: `GROUPING_MODE`:
  - `ALL_TOGETHER` vs `SEPARATE_SECTIONS`
  - When `ALL_TOGETHER`, show tasks + workouts merged into one timeline.
  - When `SEPARATE_SECTIONS`, show Tasks section and Scheduled Workouts section.

### Tests
- [ ] Unit tests: preference persistence (UserSettings update).
- [ ] MVC test: ordering change re-renders correctly.
- [ ] JS test (if no JS test setup exists, add minimal Playwright or Cypress):
  - opening drawer works
  - closing works
  - focus returns to trigger

---

## 7) Add Task becomes a single “Add” button + smart templates + AI input
### Goal
Add Task should not load full form immediately. It opens a modal/drawer.

### Implementation
- [ ] Keep the Add Task modal, but redesign content into tabs:
  - Tab 1: “Quick Add” (title, time)
  - Tab 2: “Templates” (Recents/Favourites/All)
  - Tab 3: “AI Add” (“I need to make my bed today”)
- [ ] Template system:
  - user can mark template favourite/unfavourite
  - user can save a task as template when creating/editing
  - `task_templates` table supports: title, notes, is_exercise, is_favourite, last_used_at
- [ ] When user uses a template, update `last_used_at`.
- [ ] AI add endpoint:
  - POST `/calendar/day/{date}/add-task-ai`
  - AI should return: title, time (optional), notes, exercise flag
  - If AI fails, show friendly error.
- [ ] Add “Recents” quick chips above the input.

### Tests
- [ ] Unit tests for templates:
  - create template
  - favourite toggle
  - last_used_at update when used
- [ ] MVC test: POST AI add creates calendar task.
- [ ] MVC test: template lists are populated into model.

---

## 8) Warnings system (“Warning 1”, “Warning 2”, etc)
### Goal
Optional warnings per task:
- timed warning (at specific time)
- conditional warning (after completing another selected task)
- grace period logic:
  - within grace window: orange soft pulse
  - beyond grace: red state + “Late — log it”

### Implementation
- [ ] Add DB table: `task_warnings`
  - id, task_id, type (TIME|AFTER_TASK), trigger_time, trigger_task_id, message, created_at
- [ ] Add optional UI in task drawer:
  - “Add warning” button
  - list existing warnings
  - remove warning
- [ ] Grace period already exists: ensure logic:
  - if now > dueTime and now <= dueTime+grace: inGrace true
  - if now > dueTime+grace: late true
- [ ] Tailwind animation:
  - inGrace: `ring-2 ring-orange-400/60 animate-pulse`
  - late: `border-red-300 bg-red-50/80`
- [ ] Add fun label:
  - inGrace: “Nearly out of time”
  - late: “Late — log it”

### Tests
- [ ] Unit tests for grace/late calculation with fixed clock (inject Clock).
- [ ] Repository tests for warnings CRUD.
- [ ] MVC test: warnings appear in drawer output.

---

## 9) Scheduled Workouts UX + “Completion” routing
### Goal
Remove the old “Workouts” section; show “Scheduled Workouts” only, each with clear completion CTA.

### Implementation
- [ ] Ensure each scheduled workout card has:
  - name, optional notes, status chip (Complete/Incomplete)
  - button “Complete workout” linking to `/workout-session/{id}/completion?day={date}`
- [ ] Add “Merge into timeline” mode:
  - When `GROUPING_MODE=ALL_TOGETHER`, workouts appear in same list with tasks, sorted by time if available.

### Tests
- [ ] MVC test: workout completion links exist.
- [ ] Unit test: timeline merge sorts properly.

---

## 10) Inline Reflection (only when GREEN day)
### Goal
Reflection appears only when day status is GREEN.

### Implementation
- [ ] Keep existing reflection section but redesign as:
  - compact prompt + textarea
  - button “Generate summary”
  - output blocks: performance summary + improvements
- [ ] Must include user reflection text in prompt so it’s personal.
- [ ] Only render when dayCompletionStatus == GREEN.

### Tests
- [ ] MVC test: reflection hidden for non-GREEN days.
- [ ] MVC test: reflection shown for GREEN day.
- [ ] Unit test: reflection generator includes user text.

---

## 11) “Remembering behaviour” (Behaviour Memory)
### Goal
Create a behaviour memory record that influences Day Health + suggestions.

### Implementation
- [ ] Ensure `behaviour_memory` is updated once per day load (not insert+update).
- [ ] Add a “Behaviour Insights” mini-card (collapsed by default):
  - average completion %, high load days count, time pressure score
  - “based on last 14 days”
- [ ] Make it expandable (details accordion).

### Tests
- [ ] Unit test: behaviour memory calculation with sample completion data.
- [ ] Assert only one DB write occurs per day load (mock repo verify).

---

## 12) Reduce duplicated DB calls (performance pass)
### Goal
Fix repeated queries visible in logs.

### Implementation
- [ ] Refactor controller/service so:
  - tasks for range fetched once
  - workout schedule/session fetched once
  - daily completion range fetched once
- [ ] Add caching inside request scope (simple variables, not global cache).
- [ ] Remove any loops that call repositories repeatedly.

### Tests
- [ ] Add a controller test with mocked repositories:
  - verify each repo method called once per request
- [ ] Add `@DataJpaTest` performance sanity (optional) – no N+1 in schedule occurrences.

---

## 13) Tailwind “Design System” for this page
### Goal
Consistent components: cards, pills, buttons, list items.

### Implementation
- [ ] Create small Thymeleaf fragments for:
  - `ui/card`
  - `ui/pill`
  - `ui/button`
- [ ] Replace repeated class blobs with fragment usage where possible.
- [ ] Add hover polish:
  - cards: `transition hover:shadow-md hover:bg-slate-50/60`
  - buttons: `active:translate-y-[1px]`
  - focus: consistent `focus-visible:ring-2 ring-blue-500`

### Tests
- [ ] MVC test: page renders core fragments (presence via `data-testid`).

---

## 14) Day.js enhancements (animations + UX)
### Goal
Make it feel alive but not annoying.

### Implementation
- [ ] Implement “reveal on load” animation:
  - elements with `.js-reveal` start hidden, then become visible with stagger.
- [ ] Add modal open/close animation:
  - fade backdrop + slide panel
- [ ] Add tooltip behaviour for streak bar (if needed beyond CSS).

### Tests
- [ ] If JS test runner exists: verify reveal adds class.
- [ ] Otherwise add minimal Playwright:
  - modal opens and closes
  - tooltip appears on focus

---

## 15) Final QA checklist
- [ ] Run `./gradlew test` locally (all green).
- [ ] Run application and visually check:
  - mobile layout for day view
  - drawer + modal behaviour
  - streak tooltip accessibility (tab + enter)
- [ ] Ensure no new console errors in browser.
- [ ] Ensure SQL duplication reduced (compare logs before/after).