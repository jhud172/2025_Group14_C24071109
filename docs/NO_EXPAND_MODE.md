# No Expand Mode

Expand mode and the month carousel/infinite-scroll system were permanently removed on 2026-02-23.

Do not reintroduce:
- any expand toggle UI or aria-pressed logic
- any carousel/slot layout, scroll-snap, or track translate/width tricks
- any month fragment endpoint or fragment-only templates

The month view is a single, static grid for the current month only.
