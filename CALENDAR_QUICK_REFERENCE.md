# Calendar Redesign - Quick Reference

## Key CSS Patterns

### Glass Morphism Card
```css
background: linear-gradient(135deg, 
  rgba(255, 255, 255, 0.95) 0%, 
  rgba(248, 250, 252, 0.9) 100%);
border: 1px solid rgba(226, 232, 240, 0.6);
backdrop-filter: blur(10px);
```

### Hover Transform
```css
transform: translateY(-4px) scale(1.01);
box-shadow: 0 20px 40px -15px rgba(16, 185, 129, 0.15), 
            0 10px 20px -10px rgba(0, 0, 0, 0.1);
```

### Item Card with Colored Border
```css
background: linear-gradient(to right,
  rgba(255, 255, 255, 0.98) 0%, 
  rgba(249, 250, 251, 0.95) 100%);
border-left-width: 3px;
border-left-color: rgba(148, 163, 184, 0.3); /* Type-based */
```

### Completed Item
```css
background: linear-gradient(135deg,
  rgba(16, 185, 129, 0.1) 0%,
  rgba(16, 185, 129, 0.05) 100%);
border-left-color: rgba(16, 185, 129, 0.8);
```

### Pulse Animation
```css
@keyframes pulse-today {
  0%, 100% {
    box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.25),
                0 0 20px rgba(16, 185, 129, 0.15);
  }
  50% {
    box-shadow: 0 0 0 4px rgba(16, 185, 129, 0.3),
                0 0 25px rgba(16, 185, 129, 0.2);
  }
}
```

### Gradient Button
```css
background: linear-gradient(135deg, 
  rgb(16, 185, 129) 0%, 
  rgb(5, 150, 105) 100%);
box-shadow: 0 4px 12px -2px rgba(16, 185, 129, 0.3);
```

### Gradient Text
```css
.bg-gradient-to-r {
  background: linear-gradient(to right, var(--tw-gradient-stops));
}
.from-emerald-600 { --tw-gradient-from: rgb(5 150 105); }
.to-emerald-500 { --tw-gradient-to: rgb(16 185 129); }
.bg-clip-text { background-clip: text; }
.text-transparent { color: transparent; }
```

## Color Reference

### Emerald Theme
- Primary: `#10b981` / `rgb(16, 185, 129)`
- Hover: `#059669` / `rgb(5, 150, 105)`
- Active: `#047857` / `rgb(4, 120, 87)`

### Type Colors
- Task: `rgba(59, 130, 246, 0.6)` - Blue
- Workout: `rgba(16, 185, 129, 0.6)` - Emerald
- Nutrition: `rgba(234, 179, 8, 0.6)` - Yellow
- Event: `rgba(168, 85, 247, 0.6)` - Purple

### Priority Colors
- High: `rgba(239, 68, 68, 0.7)` - Red (4px border)
- Medium: `rgba(245, 158, 11, 0.7)` - Orange (4px border)
- Low: `rgba(59, 130, 246, 0.5)` - Blue (4px border)

## HTML Patterns

### Calendar Day Card
```html
<div class="calendar-day-card relative h-44 overflow-hidden rounded-2xl 
            ring-1 ring-white/10 bg-white/70 p-3 shadow-[...] backdrop-blur 
            dark:bg-white/5 sm:h-48 sm:p-4"
     data-date="2026-02-14">
  <div class="calendar-heat-overlay"></div>
  <div class="calendar-heat-icons">...</div>
  <!-- Day content -->
</div>
```

### Task Item Card
```html
<div class="calendar-item relative flex items-center justify-between gap-2 
            rounded-2xl border border-white/10 bg-white/80 p-2 text-xs 
            shadow-sm transition dark:bg-white/5"
     data-type="task"
     data-priority="medium"
     data-completed="false">
  <div class="flex min-w-0 flex-1 flex-col leading-tight">
    <span class="truncate font-semibold text-slate-900 dark:text-slate-100">
      Task Title
    </span>
    <span class="truncate text-slate-600 dark:text-slate-300">
      10:00 AM
    </span>
  </div>
</div>
```

### Workout Item Card
```html
<div class="calendar-item relative flex items-center justify-between gap-2 
            rounded-2xl border border-white/10 bg-slate-50/70 p-2 text-xs 
            shadow-sm transition dark:bg-white/5"
     data-type="workout">
  <div class="flex min-w-0 flex-1 flex-col leading-tight">
    <span class="truncate font-semibold text-slate-900 dark:text-slate-100">
      Workout Name
    </span>
    <span class="truncate text-[10px] text-slate-600 dark:text-slate-400">
      From: Schedule Name
    </span>
  </div>
</div>
```

### Empty State
```html
<div class="calendar-empty-state">
  <svg class="calendar-empty-state-icon" fill="none" stroke="currentColor" 
       viewBox="0 0 24 24">
    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" 
          d="M12 4v16m8-8H4"></path>
  </svg>
  <span class="text-xs text-slate-500 dark:text-slate-400">Free day</span>
</div>
```

### Gradient Header
```html
<h1 class="text-xl font-bold tracking-tight text-slate-900 dark:text-slate-100 
           sm:text-3xl">
  <span class="bg-gradient-to-r from-emerald-600 to-emerald-500 bg-clip-text 
               text-transparent dark:from-emerald-400 dark:to-emerald-300">
    February
  </span>
  <span class="text-slate-500 dark:text-slate-400">2026</span>
</h1>
```

## Responsive Breakpoints

```css
/* Mobile (< 640px) */
@media (max-width: 640px) {
  .calendar-day-card { min-height: 11rem; padding: 0.875rem; }
  .calendar-item { min-height: 2.5rem; }
}

/* Tablet (641-1024px) */
@media (min-width: 641px) and (max-width: 1024px) {
  .calendar-day-card { min-height: 12rem; }
}

/* Large Desktop (1280px+) */
@media (min-width: 1280px) {
  .calendar-day-card { min-height: 13rem; }
}
```

## Animation Timing

```css
/* Standard transitions */
transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);

/* Pulse animation */
animation: pulse-today 2s ease-in-out infinite;

/* Celebrate animation */
animation: celebrate 0.5s ease-out;

/* Shimmer loading */
animation: shimmer 1.5s infinite;
```

## Usage Examples

### Adding a New Priority Level
```css
.calendar-item[data-priority="urgent"] {
  border-left-width: 5px;
  border-left-color: rgba(220, 38, 38, 0.9); /* Darker red */
}
```

### Adding a New Item Type
```css
.calendar-item[data-type="meeting"] {
  border-left-color: rgba(139, 92, 246, 0.6); /* Violet */
}

.calendar-item[data-type="meeting"]:hover {
  background: linear-gradient(to right,
    rgba(237, 233, 254, 0.95) 0%,
    rgba(243, 244, 246, 0.9) 100%);
  border-left-color: rgba(139, 92, 246, 0.8);
}
```

### Creating a New Badge
```css
.calendar-priority-badge {
  display: inline-flex;
  padding: 0.125rem 0.5rem;
  border-radius: 9999px;
  font-size: 0.625rem;
  font-weight: 600;
  text-transform: uppercase;
  background: rgba(239, 68, 68, 0.1);
  color: rgb(185, 28, 28);
  border: 1px solid rgba(239, 68, 68, 0.2);
}
```

## Common Tasks

### Change Theme Color
Replace all instances of:
- `rgb(16, 185, 129)` → New color
- `rgb(5, 150, 105)` → Darker shade
- `rgba(16, 185, 129, X)` → New color with opacity

### Adjust Hover Animation
```css
.calendar-day-card:hover {
  transform: translateY(-6px) scale(1.02); /* More dramatic */
  /* or */
  transform: translateY(-2px) scale(1.005); /* More subtle */
}
```

### Modify Shadow Intensity
```css
/* Lighter */
box-shadow: 0 10px 20px -10px rgba(16, 185, 129, 0.1);

/* Stronger */
box-shadow: 0 20px 40px -15px rgba(16, 185, 129, 0.25),
            0 10px 20px -10px rgba(0, 0, 0, 0.15);
```

---

**Quick Reference Version**: 1.0  
**Last Updated**: February 2026  
**For**: Calendar Redesign Implementation
