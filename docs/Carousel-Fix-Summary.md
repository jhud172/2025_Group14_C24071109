# Carousel Fix Summary

## Current Safeguards (As Implemented)
- **Consistent cache keys**: `paneKey()` uses `YYYY-MM` formatting for all panes.
- **Duplicate load prevention**: `pendingLoads` prevents concurrent requests for the same month.
- **Observer scope**: Only the first and last panes are observed, reconnected after DOM changes.
- **Prepend scroll adjustment**: Inserts a loading pane, adjusts `scrollLeft` by width + gap, then reconciles after replacement.
- **Validation guards**: Fetched panes must include `data-month-pane` and day cards with `data-date`.
- **Pruning**: Keeps at most 7 panes (center +/- 3) to limit DOM size.
- **Retry flow**: Error panes include a retry button that clears cached HTML for the month.

## Known Omissions
- No explicit keyboard navigation for the carousel.
- No debug logging in production code.
