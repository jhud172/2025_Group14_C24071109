# Dependency Migration Plan - 2026-04-11

This note captures the next larger dependency upgrades that should happen after the targeted `jsoup` refresh.

## Completed In This Pass

- `org.jsoup:jsoup` upgraded from `1.18.1` to `1.22.1`
- dashboard MVC test aligned with the current ambience module heading

## Tailwind 4 Migration Plan

### Why This Needs A Separate Pass

The frontend toolchain currently depends on:

- `tailwindcss` `3.4.x`
- `tailwind-scrollbar` `3.x`
- PostCSS-based compilation into `src/main/resources/static/css/app.css`

Tailwind 4 changes configuration, plugin compatibility, and build expectations enough that this should be treated as a dedicated frontend migration.

### Proposed Order

1. inventory current Tailwind plugins and config usage
2. verify `tailwind-scrollbar` v4 compatibility and any syntax changes
3. migrate `tailwind.config.js` and `postcss` pipeline in isolation
4. rebuild CSS and verify shared layout surfaces first:
   - navbar
   - footer
   - auth pages
   - client dashboard
5. run a Playwright pass across desktop and mobile breakpoints

### Acceptance Checks

- `npm run build:css` succeeds without compatibility warnings
- no utility-class regressions in shared layouts
- no broken responsive spacing on public or dashboard pages

## Spring Boot 4 Migration Plan

### Why This Needs A Separate Pass

The current Gradle dependency report shows Spring Boot `4.x` and Spring Security `7.x` only as milestone upgrades. That is not a safe repo-hygiene change; it is a framework migration.

### Proposed Order

1. wait for a stable Spring Boot 4 release target for this repo
2. review Spring Security 7 API changes and any servlet/JPA baseline changes
3. run `./gradlew test --warning-mode all` before touching versions
4. upgrade Spring Boot and re-evaluate managed dependency changes together
5. fix compile/runtime breaks before touching UI behavior
6. run full tests and a focused browser verification pass

### Expected Review Areas

- security filter chain and OAuth client config
- Thymeleaf integration
- JPA / Hibernate behavior
- test annotations and WebMvc test setup
- any Gradle 9 follow-on work once plugin compatibility is clear
