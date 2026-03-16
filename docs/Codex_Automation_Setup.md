# Codex Automation Setup Guide

## Purpose

This document defines how Codex should be used on the **1 to 1** project for recurring frontend design and UI consistency work.

The goal is to use Codex in a controlled, maintainable way so it can help with:

- CSS consistency
- JavaScript UI interaction consistency
- responsive behaviour across device sizes
- animation and transition polish
- reducing duplicated frontend patterns
- keeping templates, CSS, and JS clean and reusable

This is **not** a licence for Codex to redesign the entire platform freely or change backend logic without instruction.

---

## Codex Capabilities Relevant To This Project

The Codex app supports:

- **projects** for working inside a selected codebase directory
- **automations** for recurring background tasks
- **worktrees** for isolated parallel changes in Git repositories
- **local environments** for setup scripts and shared project actions

For automations in Git repositories, Codex can run either in the local checkout or in a dedicated worktree. Worktrees are preferred for this project because they isolate changes from active development work. Local environment setup is configured through the `.codex` folder in the project root. :contentReference[oaicite:1]{index=1}

---

## Core Rule For This Repository

Codex must follow the project architecture rules already established for **1 to 1**:

- HTML / Thymeleaf files must contain **no inline JavaScript**
- HTML / Thymeleaf files must contain **no inline CSS**
- CSS must live in dedicated CSS files
- JavaScript must live in dedicated JS files
- existing shared patterns must be reused before creating new ones
- backend logic must not be changed during frontend automation tasks unless explicitly requested

---

## Recommended Initial Codex Scope

Codex should initially be used for **frontend design and consistency work only**.

This includes:

- `src/main/resources/templates/**`
- `src/main/resources/static/css/**`
- `src/main/resources/static/js/**`

Codex should **not** freely modify:

- Java controllers
- services
- repositories
- entities
- security configuration
- database migration logic

unless the task explicitly says so.

---

## Recommended Project Setup In Codex

## 1. Create The Project In Codex

Open the Codex app and add the main **1 to 1** repository as a project.

Use the root repository folder as the selected project so Codex can inspect:

- templates
- CSS
- JS
- shared assets
- any frontend-related docs

If the repository is later split into distinct frontend/backend project folders, separate Codex projects can be created. For now, keep it as one project. Codex projects are directory-scoped, and OpenAI recommends splitting distinct apps into separate projects only when that makes sandboxing cleaner. :contentReference[oaicite:2]{index=2}

---

## 2. Use Worktrees, Not Local, For Automations

For recurring automations, use:

- **Working tree:** `Worktree`

Do **not** use the local checkout for recurring automations unless there is a very specific reason.

Reason:
- worktrees isolate automation changes from local unfinished work
- automations in Git repositories are specifically designed to run safely in worktrees
- this reduces the chance of Codex trampling over active development changes like a caffeinated badger

Codex documentation states that automations can run locally or in a dedicated worktree, and worktrees are intended to isolate changes from unfinished local work. :contentReference[oaicite:3]{index=3}

---

## 3. Configure A Shared Local Environment

Create a `.codex` folder in the project root.

This is where Codex local environment configuration should live so it can be shared and picked up correctly by the app. Codex troubleshooting guidance explicitly notes that shared local environment configuration must be inside the `.codex` folder at the root of the project. :contentReference[oaicite:4]{index=4}

Recommended purpose of the local environment:

- install dependencies needed for worktrees
- run lightweight validation commands
- prepare frontend tooling so automation runs are not missing packages

### Suggested local environment intent

The local environment should prepare only what is necessary for frontend audit and UI refinement tasks.

Keep it lightweight.

Example setup steps to consider:
- install Node dependencies if required
- run a frontend build check if safe
- avoid automatically starting the backend server
- avoid heavy dev-only commands that make automations slow and noisy

---

## Codex Automation Strategy

Only start with **two automations**.

That is enough to get value without turning the repo into a haunted forest of random agent changes.

### Automation 1
**Daily UI Consistency Audit**

Purpose:
- detect duplicated CSS and JS behaviour
- detect inconsistent card, button, spacing, radius, and shadow patterns
- detect inline CSS/JS violations
- detect components that should be using existing shared patterns
- detect weak responsive behaviour

### Automation 2
**Responsive Polish Pass**

Purpose:
- review recently changed frontend surfaces
- improve layout behaviour across desktop, tablet, and mobile
- refine animations and transitions
- improve consistency of panels, popups, buttons, forms, and cards

---

## Recommended Automation 1 Setup

### Title
`One to One - Daily UI Consistency Audit`

### Template
Use a **custom automation**, not a built-in template.

Reason:
this project has specific architectural and design rules, so a generic template is more likely to be a loose inspiration than a good operational fit.

Codex automations support custom prompts, and OpenAI guidance recommends using automations once a workflow is stable and repetitive. :contentReference[oaicite:5]{index=5}

### Working tree
`Worktree`

### Select project
Select the main **1 to 1** repository project.

### Schedule
`Daily at 06:30`

This time is a practical recommendation so the audit runs before the main development day starts.

### Prompt
Use the following prompt:

Inspect the selected One to One project and focus only on frontend design-related files: Thymeleaf templates, CSS, and JavaScript used for UI behaviour. Do not modify backend Java, security configuration, database code, API contracts, or business logic.

Your goal is to audit and, where safe, propose minimal improvements for:
- duplicated UI patterns
- duplicated CSS rules
- duplicated JS interaction behaviour
- inconsistent spacing, radius, shadows, and typography
- inconsistent transitions and animations
- weak responsive behaviour across desktop, tablet, and mobile
- components that should reuse an existing shared pattern instead of having page-specific styling or JS
- any inline CSS or inline JS that violates project rules

Follow these repository rules strictly:
- HTML/Thymeleaf must contain no inline CSS
- HTML/Thymeleaf must contain no inline JavaScript
- CSS must remain in dedicated external CSS files
- JavaScript must remain in dedicated external JS files
- preserve current functionality
- prefer reusing existing shared frontend patterns instead of inventing new ones
- keep the design premium, clean, and usable
- avoid feature bloat
- do not auto-merge anything

Check whether any new or changed UI behaviour duplicates another existing interaction elsewhere in the repo. If so, favour the existing shared pattern and adjust towards it.

Output:
1. a short audit summary
2. a list of files inspected
3. recommended changes grouped by severity
4. only minimal safe edits if confidence is high
5. a clear note for anything that should be reviewed manually

---

## Recommended Automation 2 Setup

### Title
`One to One - Responsive Polish Pass`

### Template
Use a **custom automation**, not a built-in template.

### Working tree
`Worktree`

### Select project
Select the main **1 to 1** repository project.

### Schedule
`Weekly on Sunday at 08:00`

This should not run daily. It is heavier and more invasive than the consistency audit.

### Prompt
Use the following prompt:

Inspect the selected One to One project and focus only on changed or recently active frontend design files: Thymeleaf templates, CSS, and JavaScript related to UI presentation and interaction.

Do not change backend Java, route structure, security, data models, services, repositories, or business logic unless a tiny frontend wiring fix is absolutely required to make a design improvement function correctly. If such a case appears, report it instead of making broad backend changes.

Your goal is to improve the UI for desktop, tablet, and mobile by making minimal, high-confidence changes to:
- layout responsiveness
- overflow and clipping issues
- spacing and visual hierarchy
- hover, focus, and active states
- animations and transitions
- consistency of cards, buttons, popups, and panels
- reusable CSS/JS patterns that should be shared instead of duplicated

Rules:
- keep HTML/Thymeleaf free of inline CSS and inline JS
- keep CSS in dedicated CSS files
- keep JS in dedicated JS files
- preserve the premium look and feel of One to One
- prefer improving existing components over creating new patterns
- do not remove functionality
- do not touch unrelated files
- do not auto-merge

If a new UI or interaction duplicates an existing pattern elsewhere, align it to the stronger existing pattern instead of keeping two different versions.

Output:
1. a short summary of responsive issues found
2. proposed fixes by file
3. minimal safe edits only
4. manual review notes for anything risky

---

## Optional Later Automation

Do **not** add this immediately.

Later, once the first two automations prove reliable, a third automation may be added:

### Frontend Duplicate Pattern Gate
Purpose:
- inspect newly created UI surfaces
- check if the same interaction, card style, modal style, or button treatment already exists elsewhere
- recommend reuse of canonical patterns
- flag duplication before it spreads

This should initially be reporting-only, not self-authorising broad changes.

---

## What Codex Should Never Freestyle

Codex should not be left to roam the repo making “helpful” decisions without boundaries.

Do **not** use recurring automations for:
- full redesigns of dashboard architecture
- changing backend data flow because of a design task
- refactoring security rules unless explicitly asked
- changing route structures unless the task is specifically about flow correction
- inventing new UI patterns when an existing one could be reused
- auto-merging changes

---

## Review Workflow

For each automation run:

1. review the generated summary
2. inspect the worktree diff
3. accept only minimal, coherent improvements
4. reject broad speculative changes
5. fold useful patterns back into the shared frontend structure

The purpose of Codex here is not to become the product designer of record.

The purpose is to:
- reduce repetition
- improve polish
- enforce consistency
- save time on repetitive frontend review

---

## Recommended Companion Docs

To make Codex more reliable, this repository should also contain:

### `docs/FRONTEND_PATTERN_INVENTORY.md`
A human-written source of truth for:
- button styles
- card types
- panel behaviour
- modal structure
- sticky save prompts
- popups
- hover behaviour
- animation principles
- responsive breakpoint expectations

### `docs/FRONTEND_AGENT_RULES.md`
A short rules file for Codex covering:
- what files it may touch
- what it must not touch
- reuse-first approach
- no inline CSS/JS
- premium UX expectations
- avoid feature bloat

Codex performs better when it is comparing against explicit project instructions rather than improvising from mixed repo history. OpenAI’s Codex guidance emphasises explicit context, clear prompts, and stable repeated workflows for automations. :contentReference[oaicite:6]{index=6}

---

## Operational Notes

- The Codex app must be running for app-based automations to execute.
- The selected project must be available on disk.
- Frequent automations may create many worktrees over time, so old ones should be cleaned up periodically.
- Model and reasoning settings can remain on default initially unless there is a proven reason to tune them.

Codex documentation describes automations as app-managed recurring tasks with project, prompt, cadence, and execution environment controls, and worktree/local behaviour is part of that setup. :contentReference[oaicite:7]{index=7}

---

## Recommended Starting Order

1. create the Codex project for the repository
2. add the `.codex` local environment setup
3. create **Daily UI Consistency Audit**
4. let it run for several days and review output quality
5. only then create **Responsive Polish Pass**
6. add further automations only when the workflow is stable

This keeps the system controlled and useful rather than noisy.

---

## Decision Summary

For **1 to 1**, the recommended Codex setup is:

### Automation 1
- **Title:** `One to One - Daily UI Consistency Audit`
- **Type:** custom
- **Working tree:** `Worktree`
- **Project:** main One to One repository
- **Schedule:** daily at 06:30

### Automation 2
- **Title:** `One to One - Responsive Polish Pass`
- **Type:** custom
- **Working tree:** `Worktree`
- **Project:** main One to One repository
- **Schedule:** weekly on Sunday at 08:00

This gives a clean starting point for background Codex support without turning the codebase into experimental soup.