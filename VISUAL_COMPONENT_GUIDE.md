# Training Structure Designer - Visual Component Guide

## Component Hierarchy

```
Schedule Designer
├── Breadcrumb Navigation
├── Save Panel
│   ├── Schedule Name Input
│   ├── Save Timestamp
│   └── Save Button
├── Schedule Type Selector
│   ├── Weekly Option (Most Popular)
│   ├── Daily Option
│   ├── Custom Option
│   │   └── Day Count Controls
│   └── Rotation Mode Selector (for Custom)
├── Template Library (collapsible)
│   ├── Template Grid
│   │   ├── Push/Pull/Legs Card
│   │   ├── Upper/Lower Card
│   │   ├── Full Body Card
│   │   ├── Bodybuilding Split Card
│   │   ├── Strength Training Card
│   │   └── Daily Routine Card
│   └── Hide Templates Button
├── Main Grid (2 columns)
│   ├── Workout Library Sidebar (sticky)
│   │   ├── Header with "Create New" button
│   │   ├── Search Input
│   │   └── Draggable Workout Items
│   └── Schedule Grid
│       ├── Info Banner
│       ├── Day Cards (dynamic count)
│       │   ├── Day Card Header
│       │   │   ├── Day Title
│       │   │   └── Workout Count Badge
│       │   └── Drop Zone
│       │       ├── Rest Day State (when empty)
│       │       └── Workout Chips
│       │           ├── Drag Handle
│       │           ├── Workout Name
│       │           └── Action Buttons (Duplicate, Remove)
│       ├── Intelligence Panel
│       │   ├── Panel Header
│       │   ├── Statistics Grid
│       │   │   ├── Total Workouts
│       │   │   ├── Training Days
│       │   │   ├── Frequency %
│       │   │   └── Avg Per Day
│       │   └── Insights List (dynamic)
│       │       ├── Warning Insights (amber)
│       │       ├── Success Insights (emerald)
│       │       └── Info Insights (blue)
│       └── Advanced Controls
│           ├── Duplicate Schedule
│           └── Clear All
```

## Visual States

### Schedule Type Cards

**Weekly (Selected State)**:
```
┌────────────────────────────────┐
│ 📅  Weekly Schedule            │
│     [Most Popular]             │
│                                │
│ Traditional Monday–Sunday      │
│ schedule. Perfect for          │
│ consistent weekly training.    │
└────────────────────────────────┘
• Border: emerald-500 (2px)
• Background: emerald gradient
• Icon: emerald-600
```

**Daily (Unselected State)**:
```
┌────────────────────────────────┐
│ 🕐  Daily Schedule             │
│                                │
│ Single day routine. Ideal      │
│ for simple, consistent daily   │
│ workouts.                      │
└────────────────────────────────┘
• Border: slate-200
• Background: white
• Icon: slate-600
```

### Day Card States

**Empty Day (Rest State)**:
```
┌────────────────────────────┐
│ Monday               ⚫ 0  │ ← Header (slate gradient)
├────────────────────────────┤
│                            │
│        🌙                  │
│     Rest Day               │
│ Click to mark as active    │
│     recovery               │
│                            │
└────────────────────────────┘
• Border: slate-200 (2px dashed)
• Icon: moon (slate-300)
```

**Active Day (With Workouts)**:
```
┌────────────────────────────┐
│ Monday               ⚫ 2  │ ← Header (emerald gradient)
├────────────────────────────┤
│ ╔════════════════════╗     │
│ ║ ≡  Push Day    [×] ║     │ ← Workout Chip
│ ╚════════════════════╝     │
│ ╔════════════════════╗     │
│ ║ ≡  Cardio      [×] ║     │
│ ╚════════════════════╝     │
└────────────────────────────┘
• Border: emerald-200 (solid)
• Background: emerald-50
• Chips: emerald-100 gradient
```

### Workout Chip (Hover State)

```
┌─────────────────────────────┐
│ ≡  Push Day    [📋] [×]    │ ← Actions visible on hover
└─────────────────────────────┘
• Drag handle: emerald-500
• Background: emerald-200 (hover)
• Shadow: medium
• Transform: scale(1.01)
```

### Intelligence Panel

```
┌──────────────────────────────────────────┐
│  💡  Training Intelligence               │
│      Smart insights about your schedule  │
├──────────────────────────────────────────┤
│ ┌─────────┬─────────┬─────────┬────────┐│
│ │TOTAL    │TRAINING │FREQUENCY│AVG PER ││
│ │WORKOUTS │DAYS     │         │DAY     ││
│ │   8     │   5     │  71%    │  1.6   ││
│ └─────────┴─────────┴─────────┴────────┘│
├──────────────────────────────────────────┤
│ ⚠️  High Training Frequency               │
│     You're training 5 days. Ensure       │
│     adequate recovery between sessions.  │
│                                          │
│ ✅  Balanced Training Schedule            │
│     Great balance with 5 training days   │
│     and 2 rest days.                     │
└──────────────────────────────────────────┘
```

## Color Palette

### Primary Colors
- **Emerald-50**: `#ecfdf5` - Light backgrounds
- **Emerald-100**: `#d1fae5` - Workout chips
- **Emerald-200**: `#a7f3d0` - Borders (active)
- **Emerald-300**: `#6ee7b7` - Hover borders
- **Emerald-400**: `#34d399` - Badge text
- **Emerald-500**: `#10b981` - Primary accent
- **Emerald-600**: `#059669` - Buttons
- **Emerald-700**: `#047857` - Dark text

### Neutral Colors
- **Slate-50**: `#f8fafc` - Light backgrounds
- **Slate-100**: `#f1f5f9` - Card backgrounds
- **Slate-200**: `#e2e8f0` - Borders
- **Slate-300**: `#cbd5e1` - Dividers
- **Slate-600**: `#475569` - Body text
- **Slate-700**: `#334155` - Headings
- **Slate-900**: `#0f172a` - Primary text

### Semantic Colors
- **Amber** (Warning): `#f59e0b`
- **Blue** (Info): `#3b82f6`
- **Red** (Danger): `#ef4444`

## Spacing System

```
Gap Sizes:
- xs:  0.5rem (8px)  - Between close elements
- sm:  0.75rem (12px) - Between related items
- md:  1rem (16px)    - Default spacing
- lg:  1.5rem (24px)  - Section spacing
- xl:  2rem (32px)    - Major sections
- 2xl: 3rem (48px)    - Page sections
```

## Typography

```
Font Sizes:
- xs:   0.75rem (12px) - Metadata, badges
- sm:   0.875rem (14px) - Body text, labels
- base: 1rem (16px)     - Default text
- lg:   1.125rem (18px) - Subheadings
- xl:   1.25rem (20px)  - Headings
- 2xl:  1.5rem (24px)   - Large stats

Font Weights:
- normal: 400 - Body text
- medium: 500 - Labels
- semibold: 600 - Emphasis
- bold: 700 - Headings
```

## Animations

### Scale In (Day Cards)
```css
@keyframes scaleIn {
    from { opacity: 0; transform: scale(0.95); }
    to { opacity: 1; transform: scale(1); }
}
duration: 0.3s
easing: ease-out
```

### Fade In (Workout Chips)
```css
@keyframes fadeIn {
    from { opacity: 0; }
    to { opacity: 1; }
}
duration: 0.2s
easing: ease-out
```

### Slide In (Insights)
```css
@keyframes slideIn {
    from { opacity: 0; transform: translateY(-10px); }
    to { opacity: 1; transform: translateY(0); }
}
duration: 0.3s
easing: ease-out
```

### Drag Transition
```css
transition: all 0.2s ease
hover: scale(1.02), shadow-md
active: scale(0.98)
```

## Responsive Breakpoints

```
Mobile:    < 640px  (sm)
Tablet:    640px-1024px (md-lg)
Desktop:   > 1024px (lg+)

Layout Changes:
- Mobile:  1 column, stacked cards, full-width controls
- Tablet:  2 columns, condensed sidebar
- Desktop: 7 columns (weekly), sidebar sticky
```

## Interactive States

### Focus States
```
Ring: 2px emerald-500
Offset: 2px
Applied to: Buttons, inputs, draggable items
```

### Disabled States
```
Opacity: 0.5
Cursor: not-allowed
Pointer events: none
```

### Loading States
```
Animation: pulse
Background: slate-200 → slate-300
Border-radius: inherited
```

## Accessibility Features

1. **Keyboard Navigation**
   - Tab through all interactive elements
   - Enter/Space to activate buttons
   - Escape to close modals

2. **Screen Reader Support**
   - Semantic HTML structure
   - ARIA labels where needed
   - Live regions for dynamic updates

3. **Color Contrast**
   - All text meets WCAG AA standards
   - Icons paired with text labels
   - States indicated by multiple cues (color + text + icon)

4. **Touch Targets**
   - Minimum 44x44px for mobile
   - Adequate spacing between elements
   - Visual feedback on touch

## Icon System

All icons from Heroicons (outline variant):
- Calendar: Schedule types
- Clock: Daily schedule
- Adjustments: Custom schedule
- Template: Template library
- Lightbulb: Intelligence
- Duplicate: Copy actions
- Trash: Delete actions
- X: Close/Remove
- Bars: Drag handle
- Moon: Rest day

## Grid System

### Desktop (Weekly)
```
7 columns, equal width
gap: 1rem
max-width: 1280px
```

### Desktop (Custom)
```
4-5 columns, equal width
gap: 1rem
dynamic based on day count
```

### Mobile
```
1 column
gap: 0.75rem
full width
```

## Summary

The Training Structure Designer uses:
- **Emerald accent** for primary actions and states
- **Slate neutrals** for structure and text
- **Card-based layout** for content organization
- **Smooth animations** for state transitions
- **Responsive grid** that adapts to content
- **Consistent spacing** following 8px base unit
- **Accessible interactions** with keyboard and screen reader support
- **Premium feel** through gradients, shadows, and animations

All components work together to create a cohesive, intelligent, and flexible training schedule building experience.
