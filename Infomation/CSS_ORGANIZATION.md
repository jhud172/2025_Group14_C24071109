# CSS Organization Documentation

## Overview
This document describes the CSS architecture and organization for the Healthy Habits application. The project uses a modular, component-based CSS structure with Tailwind CSS as the foundation.

## File Structure

```
src/main/resources/static/css/
├── app.css                    # ⚡ COMPILED - Main CSS file loaded on all pages
├── tailwind.css              # 🔧 SOURCE - Imports all components & Tailwind config
├── calendar-heatmap.css      # 📅 Page-specific - Only for calendar pages
└── components/               # 📦 Component modules
    ├── about.css
    ├── auth.css
    ├── buttons.css
    ├── calendar.css
    ├── cards.css
    ├── chat-widget.css
    ├── chips.css
    ├── client-dashboard.css
    ├── dashboard-enhanced.css
    ├── day-view.css
    ├── detail-view.css
    ├── footer.css
    ├── forms.css
    ├── navbar.css
    ├── profile.css
    ├── quick-actions-widget.css
    └── workout-schedule.css
```

## How It Works

### 1. Core CSS Loading (All Pages)
Every page using `base.html` loads **one main CSS file**:
```html
<link rel="stylesheet" th:href="@{/css/app.css}">
```

### 2. Build Process
The `app.css` file is **automatically generated** from `tailwind.css`:

```bash
npm run build:css
```

This command:
- Reads `tailwind.css` (which imports all component CSS files)
- Processes with PostCSS and Tailwind
- Outputs compiled `app.css` with all styles combined

### 3. Component Organization
All component CSS files are in `/components/` directory and follow these principles:

- **Modern Tailwind @layer syntax**: Use `@layer components` for reusable components
- **Scoped naming**: Component classes prefixed with component name (e.g., `.auth-container`, `.navbar-link`)
- **Self-contained**: Each file contains all styles for one component
- **No duplication**: Components are imported once in `tailwind.css`

### 4. Page-Specific CSS
For pages that need unique styles NOT used elsewhere:

- Keep CSS in root `css/` directory (e.g., `calendar-heatmap.css`)
- Load directly in template using `<link>` tag
- Document in `tailwind.css` with comment explaining page-specific usage

## Template Structure

### Base Template (`base.html`)
```html
<head>
    <!-- Core styles (compiled Tailwind + all component styles) -->
    <link rel="stylesheet" th:href="@{/css/app.css}">
    
    <!-- Page-specific styles -->
    <th:block th:if="${pageStyles != null}">
        <th:block th:replace="${pageStyles}"></th:block>
    </th:block>
</head>
```

### Page Template Example
```html
<th:block th:replace="~{base :: layout('Page Title', ~{::pageStyles}, ~{::content}, ~{::pageScripts})}">

    <th:block th:fragment="pageStyles">
        <!-- Only add page-specific CSS here if NOT already in app.css -->
        <link rel="stylesheet" href="/css/page-specific.css">
    </th:block>
    
    <div th:fragment="content">
        <!-- Page content -->
    </div>
</th:block>
```

## Migration from Old Structure

### What Changed (2025-02-15)
We reorganized CSS to eliminate duplication and improve performance:

#### ✅ Removed Obsolete Files
- `navBar.css` → Now using `components/navbar.css`
- `chat.css` → Now using `components/chat-widget.css`
- `day.css` → Now using `components/day-view.css`
- `Exercise.css`, `Main.css` → Consolidated into component files
- `exerciselogform.css`, `health-record.css`, `levels.css`, `notes.css`, `template.css`, `month.css` → Unused files removed

#### ✅ Fixed Double-Loading
- Removed duplicate `quick-actions-widget.css` link from `base.html`
- Removed redundant component imports from individual templates (auth.css, about.css, profile.css)

#### ✅ Simplified Structure
- **Before**: 28 CSS files across root and components folders
- **After**: 3 root files + 17 organized component files = 20 total files
- **Result**: ~35% reduction in CSS files, eliminated all duplication

## Development Workflow

### Making CSS Changes

1. **Edit component CSS** in `/components/` directory:
   ```bash
   # Edit the appropriate component file
   vim src/main/resources/static/css/components/navbar.css
   ```

2. **Rebuild CSS**:
   ```bash
   npm run build:css
   ```

3. **Verify changes**:
   - Check browser DevTools
   - Test affected pages
   - Verify no style regressions

### Adding New Component CSS

1. **Create component file**:
   ```bash
   touch src/main/resources/static/css/components/new-component.css
   ```

2. **Add to tailwind.css**:
   ```css
   @import "./components/new-component.css";
   ```

3. **Build CSS**:
   ```bash
   npm run build:css
   ```

### Adding Page-Specific CSS

For CSS only used on 1-2 pages:

1. **Create CSS file** in root css directory
2. **Document in tailwind.css** with comment
3. **Link directly in template**:
   ```html
   <th:block th:fragment="pageStyles">
       <link rel="stylesheet" href="/css/page-specific.css">
   </th:block>
   ```

## Best Practices

### ✅ DO
- Keep component CSS in `/components/` directory
- Use descriptive, namespaced class names
- Follow Tailwind `@layer` structure
- Build CSS after changes
- Test on multiple pages
- Document page-specific CSS usage

### ❌ DON'T
- Load component CSS directly in templates (it's already in app.css)
- Duplicate CSS between files
- Create CSS files outside `/components/` unless page-specific
- Forget to rebuild CSS after changes
- Use inline styles for component styling

## Troubleshooting

### Styles Not Appearing
1. Did you rebuild CSS? `npm run build:css`
2. Is browser caching old CSS? Hard refresh (Ctrl+Shift+R)
3. Is component imported in `tailwind.css`?
4. Check browser console for 404 errors

### Duplicate Styles
1. Check if CSS is loaded both in app.css AND separately
2. Verify no duplicate imports in `tailwind.css`
3. Remove any direct `<link>` tags for component CSS

### Build Errors
1. Check CSS syntax in component files
2. Verify all imported files exist
3. Run `npm install` if missing dependencies
4. Check PostCSS/Tailwind configuration

## Performance Benefits

### Before Reorganization
- Multiple CSS files loaded per page
- Duplicate styles from overlapping files
- ~500KB+ CSS loaded
- Browser made multiple CSS requests

### After Reorganization
- Single `app.css` file (~393KB compiled)
- Zero duplication
- One HTTP request for all styles
- Better browser caching
- Faster page loads

## References

- **Tailwind CSS**: https://tailwindcss.com/docs
- **PostCSS**: https://postcss.org/
- **Thymeleaf Layouts**: https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html#template-layout

---

**Last Updated**: 2025-02-15  
**Maintained By**: Development Team
