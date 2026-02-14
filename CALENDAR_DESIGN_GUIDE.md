# Calendar Redesign - Visual Design Guide

## Design Tokens

### Colors

#### Emerald Theme (Primary)
- Primary: `rgb(16, 185, 129)` / `#10b981`
- Hover: `rgb(5, 150, 105)` / `#059669`
- Active: `rgb(4, 120, 87)` / `#047857`

#### Type Colors (Left Borders)
- Task: `rgba(59, 130, 246, 0.6)` - Blue
- Workout: `rgba(16, 185, 129, 0.6)` - Emerald
- Nutrition: `rgba(234, 179, 8, 0.6)` - Yellow
- Event: `rgba(168, 85, 247, 0.6)` - Purple

#### Priority Colors
- High: `rgba(239, 68, 68, 0.7)` - Red
- Medium: `rgba(245, 158, 11, 0.7)` - Orange
- Low: `rgba(59, 130, 246, 0.5)` - Blue

#### Status Colors
- Completed: Emerald gradient
- Overdue: Red tint
- Today: Blue ring with emerald accent
- Tomorrow: Emerald ring (premium)

### Typography

#### Headers
- Month/Week Names: `text-3xl font-bold` with emerald gradient
- Year: `text-slate-500` lighter weight
- Day Numbers: `text-base font-semibold`

#### Card Text
- Task Title: `font-semibold text-slate-900`
- Task Time: `text-slate-600` smaller size
- Schedule From: `text-[10px] text-slate-600` muted

### Spacing

#### Cards
- Day Card Padding: `p-3` mobile, `p-4` desktop
- Item Card Padding: `p-2` (0.5rem)
- Gap Between Items: `space-y-2` (0.5rem)

#### Grid
- Column Gap: `gap-3` (0.75rem)
- Row Gap: Inherits from grid gap
- Max Width: `max-w-7xl`

### Shadows

#### Card Shadows
- Base: `0 18px 40px -30px rgba(15,23,42,0.5)`
- Hover: `0 22px 48px -32px rgba(15,23,42,0.6)`
- Item: `0 4px 12px -2px rgba(16, 185, 129, 0.1)`

#### Button Shadows
- Primary: `0 4px 12px -2px rgba(16, 185, 129, 0.3)`
- Primary Hover: `0 6px 16px -3px rgba(16, 185, 129, 0.4)`

### Border Radius

- Day Cards: `rounded-2xl` (1rem)
- Item Cards: `rounded-2xl` (1rem)
- Badges: `rounded-full` (9999px)
- Buttons: `rounded-xl` (0.75rem)

### Animations

#### Timing Functions
- Default: `cubic-bezier(0.4, 0, 0.2, 1)`
- Duration: 200-300ms for most interactions

#### Transforms
- Hover: `translateY(-4px) scale(1.01)` for day cards
- Hover: `translateX(4px) scale(1.02)` for item cards
- Active: Reset to normal or slight press

## Component Structure

### Calendar Day Card
```
┌─────────────────────────────────┐
│ [heat overlay]                   │ ← Gradient overlay for workout intensity
│ [heat icons]                     │ ← Top-right icons (workout, nutrition, PR)
│                                  │
│ Day Number          [Badge]      │ ← Day number + Today/Tomorrow badge
│                                  │
│ ┌─────────────────────────────┐ │
│ │ Task Title          [✓]     │ │ ← Task item with colored left border
│ │ Time                        │ │
│ └─────────────────────────────┘ │
│ ┌─────────────────────────────┐ │
│ │ Workout Name                │ │ ← Workout item (different background)
│ │ From: Schedule              │ │
│ └─────────────────────────────┘ │
│                                  │
│ [+X more]                        │ ← Link to day view
│                                  │
│ [Free day]                       │ ← Empty state (when no items)
└─────────────────────────────────┘
```

### Calendar Item Card States

#### Default State
- White/slate gradient background
- 3px colored left border (type-based)
- Subtle shadow

#### Hover State
- Slide right: `translateX(4px)`
- Scale up: `scale(1.02)`
- Emerald accent overlay fades in
- Left border changes to emerald
- Enhanced shadow

#### Completed State
- Emerald gradient background
- Emerald gradient left border (4px)
- Check mark (✓) on right
- Line-through text

#### Overdue State
- Red-tinted background gradient
- Red left border (7px)
- No other special styling (subtle)

## Responsive Breakpoints

### Mobile (< 640px)
- Day cards: `min-height: 11rem`, `p-3`
- Item cards: Smaller padding, `min-height: 2.5rem`
- Text: Slightly smaller

### Tablet (641px - 1024px)
- Day cards: `min-height: 12rem`
- Standard padding maintained

### Desktop (1025px - 1279px)
- Standard sizing from templates

### Large Desktop (1280px+)
- Day cards: `min-height: 13rem`
- More vertical space for items

## Accessibility Features

### Focus States
- Outline: `3px solid rgba(16, 185, 129, 0.6)`
- Offset: `3px`
- Shadow: `0 0 0 6px rgba(16, 185, 129, 0.15)`

### Focus Visible (Keyboard Only)
- Outline: `3px solid rgb(16, 185, 129)`
- More prominent for keyboard users

### ARIA Support
- Maintained from original templates
- Proper labels on icons
- Screen reader text where needed

## Dark Mode Variations

### Background Adjustments
- Day cards: More transparent, darker base
- Item cards: Darker slate tones
- Borders: Lighter for contrast

### Text Adjustments
- Primary text: `text-slate-100` (lighter)
- Secondary text: `text-slate-300` or `text-slate-400`
- Muted text: `text-slate-400` or `text-slate-500`

### Glow Effects
- Stronger shadows for visibility
- Emerald glows more pronounced

## Implementation Guidelines

### Adding New Calendar Items

1. **Always use data attributes** for type and state:
   ```html
   data-type="task|workout|nutrition|event"
   data-priority="high|medium|low"
   data-completed="true|false"
   data-overdue="true|false"
   ```

2. **Use semantic class names**:
   - Base: `.calendar-item`
   - Apply layout: `flex items-center justify-between gap-2`
   - Apply styling: `rounded-2xl border border-white/10`

3. **Follow the gradient pattern**:
   ```css
   background: linear-gradient(to right,
     rgba(start-color) 0%,
     rgba(end-color) 100%);
   ```

### Creating New Item Types

1. Define color in CSS:
   ```css
   .calendar-item[data-type="new-type"] {
     border-left-color: rgba(R, G, B, 0.6);
   }
   ```

2. Add hover state if needed:
   ```css
   .calendar-item[data-type="new-type"]:hover {
     background: /* gradient with new type color */
     border-left-color: /* stronger version */
   }
   ```

### Best Practices

1. **Maintain consistency** with existing color scheme
2. **Test in both light and dark modes**
3. **Ensure adequate contrast** for accessibility
4. **Use transitions** for smooth interactions
5. **Keep animations subtle** and purposeful
6. **Provide fallbacks** for older browsers

## CSS Utilities Available

### Calendar-Specific
- `.calendar-day-card` - Main day container
- `.calendar-item` - Individual task/workout item
- `.calendar-empty-state` - Empty day indicator
- `.calendar-heat-overlay` - Workout intensity overlay
- `.calendar-heat-icons` - Icon container
- `.calendar-category-badge` - Category badges
- `.calendar-streak-badge` - Streak indicators
- `.calendar-time-indicator` - Time badges
- `.calendar-day-summary` - Summary cards
- `.calendar-motivation` - Quote containers

### Shadow Utilities
- `.calendar-card-shadow-sm` - Small shadow
- `.calendar-card-shadow-md` - Medium shadow
- `.calendar-card-shadow-lg` - Large shadow

## Future Extensibility

The design supports these optional features:
- Quick action buttons (hover to reveal)
- Time slot indicators
- Category badges
- Streak tracking
- Day summaries
- Motivational elements
- Overdue alerts
- Custom backgrounds per category

All CSS classes are in place and ready to use when backend support is added.

---

**Design Version**: 1.0  
**Last Updated**: February 2026  
**Emerald Theme**: `#10b981`
