# Month Template QA Report

**Last Updated:** 2026-02-23
**File:** `src/main/resources/templates/calendar/month.html`
**Status:** Updated for carousel + calendar cell model. Build/tests not re-run in this update.

## Scope
- Month view template rendering with `calendarCells` (including placeholder cells).
- Carousel fragment requirements (`data-month-pane`, `data-pane-year`, `data-pane-month`, `data-pane-key`).
- Task and schedule data attributes (`data-type="task"` and `data-type="occurrence"`).

## Template Structure Notes
- Month view iterates `calendarCells` and renders placeholders via `cell.placeholder`.
- Real day cells use `cell.dayModel` (CalendarDayModel) for tasks and occurrences.
- Schedule items are standardized as `data-type="occurrence"`.

## QA Checklist (Pending)
- [ ] Build with Gradle (`./gradlew clean assemble`).
- [ ] Verify month grid renders placeholders and real day cards.
- [ ] Confirm `data-month-pane`, `data-pane-year`, `data-pane-month`, `data-pane-key` on the month pane.
- [ ] Confirm day cards include `data-date` and `data-day-link`.
- [ ] Confirm tasks use `data-type="task"`.
- [ ] Confirm occurrences use `data-type="occurrence"`.
- [ ] Confirm detailed vs grouped layout preference switches content correctly.
- [ ] Confirm the "Free day" empty state appears only when both counts are zero.

## Notes
This report reflects the current template structure but does not claim a successful build or test run for this update. Use the checklist above to re-validate.
