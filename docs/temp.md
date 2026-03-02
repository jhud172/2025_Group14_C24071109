# Dashboard + Navbar + Tutorial Refactor Tasks

## Instructions for Agent

Complete **every task** in this file sequentially.

Rules:
- Work from top to bottom.
- Fully implement each task before moving to the next.
- Maintain consistency across light and dark mode.
- Do not introduce layout instability.
- Do not reload the page if smooth transitions are possible.
- Preserve premium design standards.
- Ensure responsiveness across all screen sizes.
- Tick off each task as completed.

---

# Tasks

## Tutorial System

- [ ] Implement a structured tutorial system that guides the user step-by-step through the application.
- [ ] Use controlled pop-ups with a continue button.
- [ ] Physically navigate the user to relevant pages during the tutorial so features are shown in context.
- [ ] Allow the user to skip the tutorial at any point.
- [ ] Ensure tutorial flow differs per user role.

---

## Navbar Improvements

- [ ] Disable text selection across the entire navbar (logo, links, buttons, icons).
- [ ] Replace current logo hover effect with a strong border that fades in on hover and fades out on exit.
- [ ] Make the border pulse between dark green and neon green while hovered.
- [ ] Keep underline effect active.
- [ ] Increase size of “One to One” text and add spacing from logo icon.
- [ ] Re-centre navbar links after resizing.
- [ ] Redesign logout button styling for both light and dark mode with consistent hover, active, and focus states.

---

## Dashboard Structure

- [ ] Move “This Week” section directly above Tasks Today and Workouts Today.
- [ ] Highlight today clearly in the week strip.
- [ ] Label today explicitly as “Today”.

---

## Week Strip Behaviour

- [ ] Update hover behaviour so hovering a day shows task/workout counts.
- [ ] Add secondary hover interaction to reveal task/workout names.
- [ ] Keep hover compact and readable.
- [ ] Remove “Add Task” button from week strip.

---

## Dashboard Timeline

- [ ] Add view-only timeline below “This Week”.
- [ ] Reuse day view timeline concept but resize for dashboard.
- [ ] Include visible current-time indicator line.
- [ ] Allow horizontal scroll.
- [ ] Add previous and next day buttons.
- [ ] Implement smooth swipe/slide transitions between days.
- [ ] Prevent page reload during transitions.
- [ ] Auto-scroll to current time on load.
- [ ] Add subtle density indicators for busy periods.

---

## Weekly Progress Refactor

- [ ] Redesign layout to reduce empty space and rebalance positioning.
- [ ] Redesign streak counter visual presentation.
- [ ] Remove stars entirely.
- [ ] Replace stars with weekly goal tracker.
- [ ] Add weekly progress vs target indicator.
- [ ] Add comparison vs last week metric.
- [ ] Add weekly momentum indicator (ahead/on track/behind).
- [ ] Add subtle animation when streak increases.
- [ ] Add recovery tone when streak resets.

---

## Primary Action System

- [ ] Add dominant primary action at top of dashboard.
- [ ] Make primary action dynamic based on context (start workout, complete task, plan day).
- [ ] Ensure primary action is visually dominant but balanced.

---

## Time-Aware Header

- [ ] Implement header that adapts based on time of day.
- [ ] Morning encourages preparation.
- [ ] Midday encourages execution.
- [ ] Evening encourages review/completion.

---

## Next Up Strip

- [ ] Add “Next Up” strip above timeline.
- [ ] Display next scheduled task/workout with time.
- [ ] Suggest planning if no scheduled items exist.

---

## Micro Insights & Intelligence

- [ ] Add rotating micro insight section (behaviour patterns, trends).
- [ ] Add contextual hover tooltips explaining metrics.
- [ ] Add subtle activity heat indicator beneath weekly strip.
- [ ] Add “day status” badge (On Track / At Risk / Complete).

---

## Premium Experience

- [ ] Replace static premium box with experiential premium preview.
- [ ] Show blurred advanced metric for non-premium users.
- [ ] Show full live metric for premium users.
- [ ] Add consistency score metric blending streak, completion rate, adherence.

---

## Visual Motion & Polish

- [ ] Add soft animated glow to completed tasks/workouts in week strip.
- [ ] Add slight elevation on hover for dashboard cards.
- [ ] Add gentle fade-ins on load.
- [ ] Add smooth transitions when metrics update.
- [ ] Ensure empty states display structured guidance instead of blank areas.

---

## Weekly Focus

- [ ] Add “Weekly Focus” line under week strip.
- [ ] Pull content dynamically from user goals or preferences.

---

## Charlie Integration

- [ ] Add subtle assistant suggestion line on dashboard.
- [ ] Surface contextual recommendations occasionally.
- [ ] Ensure it does not overwhelm interface.

---

## Hierarchy Balance

- [ ] Ensure clear visual hierarchy:
  - Primary action dominant
  - Timeline operational
  - Weekly metrics analytical
  - Quick actions supportive
- [ ] Reduce visual competition between modules.

---

## Final Review

- [ ] Ensure dashboard feels behaviour-driven rather than status-driven.
- [ ] Ensure interface reinforces momentum and identity.
- [ ] Ensure theme consistency.
- [ ] Ensure responsiveness.
- [ ] Ensure premium polish throughout.