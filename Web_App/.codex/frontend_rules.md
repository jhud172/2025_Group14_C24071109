# Frontend Agent Rules — One to One

## Purpose

This document defines the rules Codex must follow when performing frontend work in the **One to One** repository.

Its purpose is to keep frontend automation safe, focused, clean, and maintainable.

Codex must treat this file as an operational boundary.

It exists to prevent:

- accidental backend edits during frontend tasks
- duplicated UI systems
- inconsistent CSS and JS behaviour
- low-quality speculative redesigns
- feature bloat
- structural mess

---

## Primary Scope

Unless a task explicitly says otherwise, Codex is only allowed to work on frontend-facing files related to presentation, layout, styling, and client-side interaction.

### Allowed areas

Codex may inspect and modify:

- `src/main/resources/templates/**`
- `src/main/resources/static/css/**`
- `src/main/resources/static/js/**`

Codex may also inspect:

- `docs/**`
- `.codex/**`

when those files provide UI instructions, design rules, workflow context, or pattern references.

---

## Restricted Areas

Codex must not modify these areas during frontend-only tasks unless the prompt explicitly requests it.

### Backend and system logic

Do not modify:

- `src/main/java/**`
- controllers
- services
- repositories
- entities
- DTOs
- configuration classes
- filters
- security classes
- exception handling
- database setup
- migration logic
- environment config
- build scripts unrelated to frontend tooling

### Examples of files not to touch by default

Do not freely modify:

- Spring Security configuration
- route authorisation logic
- Dev Mode access filters
- services that load or persist workout data
- repositories and entity relationships
- Java request/response logic
- application properties
- Gradle backend configuration

If a frontend problem is caused by backend logic, Codex should report it clearly rather than improvising a broad backend change.

---

## Architecture Rules

These rules are strict.

### HTML / Thymeleaf

HTML and Thymeleaf files must contain:

- no inline CSS
- no inline JavaScript

HTML files should contain:

- structure
- semantic markup
- references to imported CSS
- references to imported JS

### CSS

All styling must live in dedicated CSS files.

Codex should:

- reuse existing CSS structure where possible
- avoid scattering styling across many new files without reason
- prefer consolidation when duplication is obvious and safe

### JavaScript

All UI behaviour must live in dedicated JS files.

Codex should:

- reuse shared interaction logic where possible
- avoid duplicating event handling patterns
- avoid page-specific scripts when a shared module makes more sense

---

## Design Intent

The One to One platform must feel:

- premium
- modern
- clean
- structured
- deliberate
- smooth across devices

The UI must not feel:

- generic
- bloated
- inconsistent
- overly flashy
- cluttered
- awkward on mobile

Codex should preserve this product direction at all times.

---

## Reuse-First Rule

Before creating a new style, component, or interaction, Codex must check whether a similar pattern already exists.

Codex must prefer the following order:

1. reuse an existing pattern directly
2. extend an existing pattern safely
3. refactor duplicated patterns into a shared pattern
4. create a new pattern only if no suitable existing one exists

Codex must not create duplicate systems for:

- cards
- buttons
- popups
- modals
- sticky save bars
- hover panels
- schedule cards
- workout action cards
- panel transitions
- animation timing

If a newly created feature visually duplicates another existing feature, Codex should align the new feature to the stronger existing pattern.

---

## UI Pattern Reference

When making frontend decisions, Codex must consult:

- `.codex/ui_patterns.md`

That file defines the canonical UI patterns for the repository.

If a task conflicts with those patterns, Codex should minimise divergence and explain the reason.

---

## Frontend Behaviour Rules

Codex may improve frontend behaviour related to:

- layout responsiveness
- spacing consistency
- overflow and clipping
- hover states
- focus states
- active states
- panel animations
- modal animations
- drag-scroll presentation
- sticky prompts
- visual hierarchy
- readability
- consistency of workout/task/calendar cards
- consistency of dashboard modules

Codex must preserve the intended function of the page.

Do not remove working features just to simplify styling.

---

## Frontend Refactor Rules

Codex may refactor frontend files when the change clearly improves maintainability.

Examples of acceptable frontend refactors:

- moving duplicated CSS into shared classes
- merging duplicated UI JS behaviour into a shared file
- cleaning up dead frontend code after confirming it is unused
- renaming frontend classes or files for clarity if references are updated safely
- removing obsolete duplicate templates after a canonical one is chosen

Examples of unacceptable frontend refactors without explicit permission:

- rewriting whole page structures for aesthetic preference alone
- changing product flow
- changing security-sensitive route behaviour
- changing workout/session ownership rules
- changing controller logic
- deleting uncertain code without tracing usage

---

## Workout-Specific Rules

The workout system is sensitive and should not be casually split into multiple UI flows.

When working on workout-related frontend surfaces, Codex must prefer:

- one canonical workout session display flow
- one clear workout creation/editing entry area
- one consistent route family for workout entry points
- one consistent visual system for workout cards and actions

Frontend work must not reintroduce fragmented workout experiences.

If different templates or JS flows appear to duplicate workout behaviour, Codex should flag them for consolidation instead of preserving parallel systems.

---

## Calendar And Schedule Rules

For calendar, planner, and schedule UI:

Codex should maintain consistency across:

- month view
- week view
- day view
- hover preview panels
- quick action controls
- workout entry cards
- task entry cards

Schedule surfaces should feel like one unified system, not three unrelated interfaces wearing the same coat.

---

## Responsiveness Rules

Every frontend change must consider:

- desktop
- laptop
- tablet
- mobile

Codex should check for:

- collapsed layouts
- clipped buttons
- broken hover-only assumptions on touch devices
- unreadable spacing
- card overflow
- modal overflow
- unusable side panels
- inconsistent stacking behaviour

Preferred behaviour:

- maintain readable spacing
- preserve button usability
- avoid horizontal scroll unless intentionally designed
- keep interactions obvious on touch screens

---

## Animation And Motion Rules

Motion should improve clarity, not become circus fog.

Codex may refine:

- hover transitions
- panel open/close transitions
- modal transitions
- sticky prompt reveal animations
- dashboard interaction motion

Codex should prefer:

- subtle transitions
- quick response
- consistent timing
- smooth easing

Codex must avoid:

- excessive bounce
- long delays
- distracting motion
- inconsistent animation timing across similar components

---

## Accessibility And Usability Rules

Even when improving visuals, Codex must preserve usability.

Codex should maintain or improve:

- readable contrast
- visible focus states
- button clarity
- form clarity
- keyboard-friendly modal behaviour where already present
- touch-friendly interaction spacing
- clear active states
- readable text hierarchy

Codex should not remove useful labels, focus behaviour, or clear interaction cues for the sake of minimalism.

---

## Safe Change Rules

Codex should prefer minimal, high-confidence changes.

When uncertain, Codex should:

- inspect more files first
- compare against existing patterns
- report the issue clearly
- avoid speculative rewrites

Codex must not make broad repo-wide visual rewrites from a vague prompt.

Codex must not silently change unrelated files while working on a focused frontend task.

---

## Deletion Rules

Codex may remove frontend files or functions only when at least one of the following is true:

- the file is clearly obsolete and replaced by a canonical version
- references confirm it is unused
- the task explicitly requests cleanup
- the removal is part of a route/template consolidation with verified replacements

Before deleting, Codex should trace where the file, class, or function is used.

Do not delete uncertain files just because they look old.

No random archaeological vandalism.

---

## Reporting Rules

For substantial frontend tasks, Codex should report:

1. what it changed
2. why it changed it
3. what patterns it reused
4. what files were modified
5. what files were removed, if any
6. any issues that appear to require backend attention
7. any risky or manual-review items

This keeps the workflow auditable and prevents mystery edits.

---

## Automation Rules

For recurring automations, Codex should behave conservatively.

Daily or scheduled frontend automations should:

- inspect frontend-only scope
- prefer reporting and minimal safe edits
- avoid backend changes
- avoid route logic changes unless explicitly requested
- avoid creating new design systems
- avoid auto-merging broad UI refactors

Automations should reinforce consistency, not invent product direction.

---

## Non-Negotiables

Codex must always follow these rules:

- no inline CSS in HTML / Thymeleaf
- no inline JS in HTML / Thymeleaf
- CSS in CSS files
- JS in JS files
- reuse existing patterns first
- protect premium UX consistency
- avoid feature bloat
- avoid backend edits during frontend tasks unless explicitly requested
- prefer one canonical flow over duplicated UI systems
- keep changes modular, clean, and maintainable

---

## Default Frontend Task Behaviour

Unless the task says otherwise, Codex should assume the correct approach is:

- inspect existing frontend patterns first
- compare against `.codex/ui_patterns.md`
- make minimal safe improvements
- consolidate duplication where confidence is high
- preserve function
- report clearly

This is the default operating mode for frontend work in the One to One repository.