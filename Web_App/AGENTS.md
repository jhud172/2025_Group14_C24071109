# AGENTS.md

## Project
You are working on the **1 to 1** platform repository. Treat this repository as the source of truth unless the user explicitly says otherwise.

This is a premium fitness platform combining:
- a marketplace for clients, verified personal trainers, and gyms
- a management platform for bookings, payments, and communication
- a fitness operating system for progress tracking, goals, and analytics

Target users:
- beginners starting fitness safely
- regular gym users who want structure
- personal trainers managing clients
- gyms managing trainer operations

Launch direction:
- UK-first
- global expansion later

---

## Core product rules
These rules are not optional.

- The platform must feel premium in design and experience.
- All trainers are verified.
- A client may have only one active trainer at a time.
- Trainers may belong to multiple gyms or operate independently.
- Payments must occur inside the platform.
- GDPR and data privacy are mandatory.
- Avoid feature bloat.
- Do not turn the product into a generic fitness tracking app.
- All features must help either the client train or the trainer manage clients.

If a proposed change weakens the marketplace, lowers the premium feel, or turns the product into a generic tracker, push back clearly and explain why.

---

## Current development phase
Assume the project is in:
- planning and product definition
- screen-by-screen behaviour definition
- lifecycle flow definition
- page locking behaviour
- preparation for database schema
- preparation for API design

Do not jump ahead into unnecessary full design systems, branding exercises, external integrations, or speculative architecture unless the user explicitly asks.

---

## Strict code architecture rules
These rules are mandatory.

- HTML files must contain **no JavaScript code**.
- HTML files must contain **no CSS styling**.
- HTML files must contain only markup and imports.
- All JavaScript must live in dedicated JS files.
- All styling must live in dedicated CSS files.
- Never add inline `<script>` or `<style>` blocks.
- Follow separation of concerns:
  - HTML = structure
  - CSS = styling
  - JS = behaviour

If inline logic or styling exists, prefer refactoring it into separate files.

---

## How to work in this repository
Before making changes:

1. Inspect the existing structure first.
2. Identify the exact files that should be changed.
3. Prefer modifying existing components over rewriting them.
4. Reuse existing patterns, naming, and file placement.
5. Keep changes maintainable, scalable, and consistent.

When proposing or making changes:
- say which files should be edited
- explain why those files are the right place
- avoid unnecessary rewrites
- preserve existing flows unless the user asked to redesign them

---

## UI and UX standards
The product must feel premium, clean, modern, and reliable.

When working on UI:
- preserve a premium, polished experience
- prioritise clarity, hierarchy, spacing, and consistency
- check layout behaviour across desktop, tablet, and mobile
- consider loading, empty, error, hover, focus, and active states
- avoid clunky, generic admin-style layouts unless the page truly requires it
- preserve responsiveness and avoid overlap, clipping, cramped controls, and dead space

When working on responsive issues:
- identify the affected breakpoints
- describe what is breaking
- propose the smallest clean fix first
- verify nav, banners, cards, modals, sidebars, and action bars on narrow widths

---

## Behaviour for debugging
When debugging:
- identify likely root causes first
- trace through the relevant files and flows
- avoid symptom-only patches
- state what caused the issue if it can be determined
- call out uncertainty explicitly if the cause is not proven

For deployment or live-environment issues:
- compare repository config with deployment config
- check env vars, startup behaviour, profiles, and service wiring
- use logs and service metadata before guessing

---

## Backend standards
When working on backend or data flows:
- preserve security and validation
- preserve role-based access control
- flag schema or migration impacts clearly
- keep env var usage centralised and predictable
- avoid duplicating business logic in controllers, templates, and services

For payments, AI, and user-sensitive data:
- be careful with privacy, auditability, and compliance
- do not weaken verification, payment integrity, or user-data protection

---

## Render and live-service workflow
If Render MCP is available:
- use it to inspect services, deploys, logs, env var surface, and configuration mismatches
- prefer evidence from live logs over assumptions
- distinguish between local issues and deployed issues

---

## Browser and UI investigation workflow
If a browser-capable MCP server is available:
- use it for page interaction, state validation, and flow testing
- verify whether issues are structural, styling-related, data-related, or JS-related
- inspect the actual page state before proposing UI fixes
- report what is visibly wrong in practical UI terms

Do not claim to have visually verified a page unless a browser-capable MCP or equivalent tooling was actually used.

---

## Output expectations
Default to practical engineering output.

For substantial tasks:
- provide a short implementation plan
- list files to inspect or modify
- then execute

For smaller tasks:
- be direct
- state the fix cleanly
- avoid filler

Prefer production-quality solutions over temporary hacks.