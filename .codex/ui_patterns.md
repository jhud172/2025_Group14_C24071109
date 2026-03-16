# UI Pattern Inventory — One to One

## Purpose

This document defines the **canonical frontend UI patterns** used across the One to One platform.

Codex should treat these patterns as the **source of truth** when auditing or modifying UI.

If new UI elements are created, Codex must first check whether an existing pattern already exists here before creating a new one.

The goal is to maintain:

- visual consistency
- reusable components
- minimal CSS duplication
- smooth interactions
- a premium modern interface

---

# Global Design Principles

The One to One UI should feel:

- **premium**
- **clean**
- **minimal but powerful**
- **fluid with motion**
- **responsive across devices**

Avoid:

- cluttered layouts
- duplicate component styles
- inconsistent animations
- inconsistent spacing systems

---

# Layout System

## Page Structure

Pages follow this hierarchy:
Navbar
Main Page Container
Section
Section Header
Content Grid / Cards
Footer


Spacing between major sections should remain visually balanced.

Preferred layout spacing values:
Section spacing: 3rem–5rem
Card padding: 1.25rem–1.75rem
Card gap: 1rem–1.5rem


---

# Card Components

Cards are a core UI component across the platform.

Used for:

- dashboard widgets
- workout cards
- planner entries
- trainer previews
- analytics summaries

## Card Structure
Card
├ Header
├ Content
└ Optional Footer


## Visual Style

Cards should use:

- soft shadows
- smooth hover elevation
- rounded corners
- subtle border contrast

Example behaviour:

Hover → lift slightly  
Focus → subtle outline  
Active → slight press effect

Codex should **reuse existing card classes** instead of creating new styles.

---

# Buttons

Buttons must follow consistent hierarchy.

## Primary Button

Used for:

- main actions
- confirmations
- submission

Style principles:

- strong contrast
- rounded corners
- smooth hover transition

## Secondary Button

Used for:

- alternative actions
- cancel actions
- optional flows

Lower visual weight than primary.

## Icon Button

Used for:

- compact actions
- inline controls
- toolbar elements

Should maintain consistent size and hover behaviour.

---

# Navigation Patterns

Navigation should always remain predictable.

## Top Navigation Bar

Contains:

- logo / home link
- primary navigation
- user profile access
- dev mode indicator (if active)

Navbar behaviour:

- fixed while scrolling
- smooth shadow transition when page scroll begins

---

# Dashboard Layout

Dashboard contains modular blocks.

Common modules include:

- Action Hub
- Planner Preview
- This Week So Far
- Trainer Relationship
- Weather / ambience widgets

Cards may support **horizontal drag scroll** for compact layouts.

Codex should preserve the **dashboard hierarchy and visual rhythm**.

---

# Popups and Hover Panels

Used in:

- calendar hover cards
- workout previews
- quick action menus

Rules:

- appear instantly
- subtle fade animation
- must not block the entire screen
- maintain readable spacing

Hover panels should remain **lightweight and responsive**.

---

# Modal Windows

Modals are used for:

- editing tasks
- editing workouts
- confirming actions
- onboarding flows

Modal rules:

- background dim overlay
- smooth scale + fade animation
- clear close button
- escape key support
- mobile-friendly layout

Avoid extremely large modal content blocks.

---

# Forms

Forms should remain simple and readable.

## Input Fields

Preferred behaviour:

- clear labels
- visible focus state
- error messaging below field
- spacing between inputs

Avoid placeholder-only inputs.

## Form Layout

Standard layout:
Form
├ Field Group
├ Field Group
├ Action Buttons


---

# Calendar UI

Calendar views include:

- month view
- week view
- day view

Common behaviours:

- hover preview panels
- quick action controls
- clickable scheduled items

Workout and task entries must use **consistent card styling**.

---

# Workout Interaction Patterns

Workout flow must remain consistent.

Entry points may include:

- calendar views
- dashboard actions
- planner cards

When starting a workout:
User action
→ workout session route
→ workout display template
→ exercise progression
→ completion screen


All entry points must use **the same controller and session flow**.

---

# Sticky Save Prompts

Used in:

- preferences pages
- settings editors
- configurable dashboards

Behaviour:

- appears when changes are detected
- fixed position
- contains Save and Cancel

---

# Animation Principles

Animations should feel **fast and intentional**.

Recommended durations:
Hover: 150ms
Panel open: 200ms
Modal open: 250ms
Page transitions: 250–300ms


Avoid:

- long animations
- bounce effects
- distracting motion

Motion should support usability.

---

# Responsive Design Rules

All layouts must work across:

- desktop
- tablet
- mobile

Common breakpoints:
Desktop: >1200px
Laptop: 1024px–1200px
Tablet: 768px–1024px
Mobile: <768px


Responsive adjustments should focus on:

- stacking columns
- reducing card width
- adjusting spacing
- maintaining readable typography

---

# Pattern Reuse Rules

Before creating a new component style, Codex must check whether a similar component already exists.

Prefer:

1. reuse existing CSS classes
2. extend existing components
3. adjust existing JS interactions

Avoid:

- creating duplicate card systems
- creating duplicate button classes
- adding new animation styles without reason

---

# Codex Behaviour Rules

When modifying UI, Codex should:

- inspect existing patterns
- reuse existing styles
- make minimal safe improvements
- avoid large structural changes unless requested

Codex should never:

- redesign major layouts without instruction
- modify backend logic during UI tasks
- introduce inline CSS or JS

---

# Future Expansion

This file may later include patterns for:

- trainer marketplace cards
- analytics dashboards
- progress visualisations
- subscription UI
- messaging interfaces
- AI interaction panels