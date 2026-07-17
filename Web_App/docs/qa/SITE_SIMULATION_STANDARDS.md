# Automated site simulation standards

This document is the acceptance baseline for the One To One browser simulation. The executable source of truth is [`tools/qa/site-simulation.config.mjs`](../../tools/qa/site-simulation.config.mjs). When a requirement changes, update the product decision here and the executable contract together.

The simulation uses six profiles: public visitor, client, trainer, gym administrator, platform administrator and super administrator. It combines the existing feature-workflow suite with a standards audit across desktop and mobile viewports.

## Result levels

| Level | Meaning | Release expectation |
|---|---|---|
| Critical | A page cannot be used, authentication is broken, or access control is wrong | Must be fixed before release |
| Major | A core page, control, accessibility rule or runtime dependency is broken | Must normally be fixed before release |
| Minor | Quality, copy, hierarchy or small-target issue that does not block the main journey | Fix before final polish or record an explicit exception |

Automated checks are evidence, not a replacement for manual usability, assistive-technology, payment-provider or visual-brand review.

## Standards by page

Every configured page must:

1. Return its accepted HTTP status without a navigation failure (`PAGE-001`).
2. Keep an authenticated user in the correct profile session (`PAGE-006`).
3. Have a useful 8–70 character document title (`PAGE-002`).
4. Have exactly one visible `main` landmark (`PAGE-003`).
5. Have exactly one visible, descriptive H1 (`PAGE-004`).
6. Contain the required elements in its page contract (`PAGE-007`).
7. Pass the element, design, accessibility, text and runtime standards below.

Public marketing pages must also have a page-specific 50–170 character meta description (`PAGE-005`).

| Profile | Pages and page-specific contract |
|---|---|
| Public | Home: main, H1, navigation. About, FAQ, pricing, explore, merchandise and policies: main and H1. Login: login form, password field and submit action. Sign-up: main and H1. Forgotten password: main and form. |
| Client | Dashboard, profile, calendar, inbox, trainer marketplace, goals, workout management, achievements, health records, blood pressure and vault: main and H1. Support and preferences: main and form. |
| Trainer | Dashboard, profile, calendar, inbox, clients, library, exercises, workout templates, schedules and workouts: main and H1. Support: main and form. |
| Gym administrator | Dashboard, profile, calendar, inbox, trainers and memberships: main and H1. Support: main and form. |
| Platform administrator | Dashboard, profile, calendar, inbox, feedback, gym applications and merchandise management: main and H1. Support: main and form. |
| Super administrator | Admin dashboard, verification queue, profile, calendar and inbox: main and H1. Support: main and form. |

Dynamic detail pages are exercised by the feature workflows when an ID must first be created or discovered. Their create, read, update and delete results appear in the feature-workflow section of the report.

## Standards by element

| ID | Correct | Wrong | Expected fix |
|---|---|---|---|
| `ELEMENT-001` | Every visible link, button and field has a meaningful accessible name | Icon-only or empty control with no label | Add visible text, a connected label or precise ARIA name |
| `ELEMENT-002` | Every visible input, select and textarea has a programmatic label | Placeholder-only field or disconnected label | Connect `label[for]` to a unique ID or use `aria-labelledby` |
| `ELEMENT-003` | Every image has `alt`; decorative images use `alt=""` | Missing `alt` attribute | Add concise alternative text or an empty decorative alternative |
| `ELEMENT-004` | IDs are unique in the rendered document | Repeated component IDs | Generate unique IDs and update labels, ARIA references and selectors |
| `ELEMENT-005` | Links point to a route, URL or existing in-page anchor | Empty, `#`, missing anchor or `javascript:` link | Implement the destination/behaviour or use a real button |
| `ELEMENT-006` | Images load and have intrinsic dimensions | Broken source or zero-width loaded asset | Correct the path and preserve dimensions/aspect ratio |
| `ELEMENT-007` | Heading levels describe a logical nested structure | H2 followed directly by H4 for visual sizing | Use the correct heading level and style it with CSS |

## Design and responsive standards

| ID | Correct | Wrong | Expected fix |
|---|---|---|---|
| `DESIGN-001` | Content fits 1440px desktop, 1024px tablet when selected, and 390px mobile | Horizontal scrolling, clipped controls or off-screen positioned content | Fix fixed widths, grid minimums, wrapping, transforms or positioning at that breakpoint |
| `DESIGN-002` | Non-inline mobile targets are at least 24×24 CSS pixels (WCAG 2.2 AA minimum) | Tiny icon/button hit area | Increase padding or target spacing; aim for 44×44 where the layout allows |
| `DESIGN-003` | Interface text is at least 12px and remains visible | Microscopic or zero-sized/clipped copy | Increase font size or remove the clipping constraint; body copy should normally be 16px+ |
| `A11Y-001` | No serious/critical Axe WCAG 2.0/2.1 A or AA violations | Contrast, name, role, landmark or relationship failure | Apply the reported Axe fix and manually verify keyboard/screen-reader behaviour |

Premium visual acceptance still requires a human check for spacing rhythm, hierarchy, brand consistency, hover/focus/active states, empty/loading/error states and whether the interface feels deliberate rather than generic.

## Text standards

| ID | Correct | Wrong | Expected fix |
|---|---|---|---|
| `TEXT-001` | Clean UTF-8 punctuation and symbols | Mojibake such as `Ã`, `Â`, `â€` or replacement characters | Save as UTF-8 and replace the corrupted source text |
| `TEXT-002` | Final user-facing copy | Lorem ipsum, TODO, FIXME, TBD, generic placeholder or coming-soon copy | Write the final product copy or remove the unfinished surface |
| `TEXT-003` | Every visible heading describes its section | Empty heading used for spacing | Add useful text or remove the element |
| `TEXT-004` | Public prose uses British English | American spellings such as `behavior`, `color`, `organize` and `optimize` | Use `behaviour`, `colour`, `organise` and `optimise`, excluding APIs/proper names |

Copy must also be truthful, concise, reassuring and appropriate for a UK-first fitness platform. It must not promise medical outcomes, imply unverified trainers are available, weaken the one-active-trainer rule, or imply off-platform payment is supported.

## Runtime and journey standards

| ID | Correct | Wrong | Expected fix |
|---|---|---|---|
| `RUNTIME-001` | No uncaught browser console errors | JavaScript exception or unhandled dependency error | Trace and repair the originating script/state |
| `RUNTIME-002` | Same-origin documents, scripts, styles, images and APIs succeed | Failed request or 4xx/5xx dependency | Correct the URL/server response or handle the optional dependency cleanly |

The full workflow simulation additionally covers invalid login, complete trainer and gym-owner login lifecycles, role locks, mobile navigation, trainer-client lifecycle, trainer library CRUD, schedule deployment/undo, client goals, notes, health records, profile updates, blood pressure, vault and AI fallbacks, inbox and APIs, merchandise checkout, payment constraints, support, admin feedback, gym applications and merchandise administration.

Trainer and gym-owner authentication must pass all of these criteria: the requested role is preselected; only the correct fields are submitted; password visibility controls work; segmented codes compose into the expected hidden credential; incomplete codes are blocked in the browser; invalid codes show a role-specific error while retaining the role and username; valid credentials reach the correct dashboard; the session survives a reload; logout clears the session; and the protected dashboard redirects back to login afterwards.

These workflows change disposable local H2 data. The runner does not execute them against a remote URL unless `--allow-remote-mutations` is explicitly supplied.

## Running the simulation

From `Web_App`:

```powershell
# Start the app automatically, run full feature workflows, then audit all profiles/pages
npm run qa:simulate

# Faster read-only standards pass
npm run qa:simulate:standards

# CI mode: fail the command on any major or critical standards finding
npm run qa:simulate:ci

# Focused authentication runs (the app starts automatically when needed)
npm run qa:login:roles
npm run qa:login:trainer
npm run qa:login:gym
```

The runner installs the isolated QA dependencies and matching Chromium runtime when they are missing. This does not add Playwright to the production application bundle.

Useful filters:

```powershell
node tools/qa/run-site-simulation.mjs --skip-workflows --profiles public,client --viewports desktop,mobile
node tools/qa/run-site-simulation.mjs --base-url http://localhost:8081 --no-start --screenshots all
node tools/qa/run-site-simulation.mjs --skip-workflows --fail-on critical
```

Generated evidence is written to `output/playwright/site-simulation/`:

- `report.md`: readable page-by-page findings and recommended fixes
- `report.json`: complete criteria, evidence and machine-readable results
- `junit.xml`: CI/test-report integration
- `screenshots/`: failure screenshots by default
- `server.log`: Spring Boot output when the runner starts the app

The existing detailed feature evidence remains in `output/playwright/local-view-audit/`.
