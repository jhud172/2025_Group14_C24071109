# Calendar Month Carousel Implementation

## Overview
The month view supports an expand mode that renders a 3-up infinite horizontal carousel. Users can scroll left/right to load adjacent months dynamically while keeping the current month centered and the header in sync.

## Backend
- **Endpoint**: `/calendar/month-fragment`
- **Returns**: `calendar/month :: monthPane`
- **Data**: Uses `CalendarDayModelBuilder.buildMonthCells()` to include placeholder cells and `CalendarDayModel` data for each real day.

## Frontend Markup
- **Container**: `#month-carousel`
- **Track**: `#month-carousel-track`
- **Pane**: `.month-pane` with required attributes:
  - `data-month-pane="true"`
  - `data-pane-year="YYYY"`
  - `data-pane-month="M"`
  - `data-pane-key="YYYY-MM"`
- **Center marker**: `data-pane-center="true"` is set by JavaScript on the active pane.

## JavaScript Behavior (month.js)
- **Toggle**: `#month-expand-toggle` expands/collapses the carousel.
- **Caching**: `paneCache` stores month-pane HTML by `YYYY-MM`.
- **Fetch**: `fetchMonthPane()` uses `/calendar/month-fragment` and validates:
  - `data-month-pane` is present
  - at least one `.calendar-day-card[data-date]` exists
- **Loading state**: `createLoadingPane()` inserts a skeleton grid.
- **Error state**: `createErrorPane()` shows a retry button and invalidates cache on retry.
- **Append/Prepend**:
  - `appendMonthPane()` adds next month to the end.
  - `prependMonthPane()` inserts previous month and adjusts `scrollLeft` by the inserted width + gap to prevent jumps.
- **Observer**:
  - `IntersectionObserver` uses `root: #month-carousel` and `threshold: 0.5`.
  - Observes only the first and last panes; reconnects after DOM changes.
- **Pruning**:
  - `prunePanes()` keeps at most 7 panes (center +/- 3).
- **Header sync**:
  - `updateCenterState()` updates `#month-name` / `#month-year` from the centered pane.

## Loading and Error States
- **Loading class**: `.month-pane--loading`
- **Error class**: `.month-pane--error`
- **Retry**: Button with `[data-retry]` re-fetches the pane.

## Manual Test Checklist
1. Open `/calendar?view=month`.
2. Click **Expand** and verify previous/current/next months render.
3. Scroll right to load the next month; scroll left to load the previous month.
4. Confirm no visible jump when prepending.
5. Keep scrolling until pruning removes older panes (max 7).
6. Disconnect network to trigger an error pane; reconnect and retry.
7. Click **Collapse** and confirm the centered pane remains.

## Notes
- No explicit keyboard navigation is implemented for the carousel.
- Scroll snapping is not enforced; alignment is driven by CSS layout and center detection.
