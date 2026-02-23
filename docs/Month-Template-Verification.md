# Month Template Verification

**Last Updated:** 2026-02-23
**Template:** `src/main/resources/templates/calendar/month.html`

## What This Document Covers
- Rendering structure for month view using `calendarCells`.
- Data attributes required by the month carousel fragment.
- Task and occurrence item attributes used by JS.

## Current Expected Structure
- **Month pane container**:
  - `data-month-pane="true"`
  - `data-pane-year="YYYY"`
  - `data-pane-month="M"`
  - `data-pane-key="YYYY-MM"`
- **Placeholder cells**:
  - `.calendar-day-card--placeholder`
- **Real day cells**:
  - `.calendar-day-card[data-date]`
  - `data-day-link` with `/calendar/day/{isoDate}`
- **Items**:
  - Tasks: `data-type="task"`
  - Occurrences: `data-type="occurrence"`

## Verification Status
This document has been updated to match the current template structure, but no automated build or test run was executed as part of this update. Use the QA checklist in Month-Template-QA-Report for re-validation steps.
