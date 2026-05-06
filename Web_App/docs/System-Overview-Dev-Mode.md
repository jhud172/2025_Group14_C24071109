# System Overview - Dev Mode Active

## Purpose

This file describes the repository behavior when development mode is active.

Use it when you need the current answer to:

- what changes when `DEV_MODE=true`
- which routes stay public, protected, or restricted
- how the Dev Hub works
- which UI and testing behaviors are specific to development sessions

For the normal platform baseline, use [System-Overview.md](./System-Overview.md).

## Activation

Dev mode is controlled by `DEV_MODE`.

The repository currently resolves that flag from:

- the process environment
- system properties
- a local `.env` file read by `DevModeProperties`

Relevant code:

- [`src/main/java/uk/ac/cf/_5/group14/One_To_One/Config/DevModeProperties.java`](../src/main/java/uk/ac/cf/_5/group14/One_To_One/Config/DevModeProperties.java)
- [`src/main/java/uk/ac/cf/_5/group14/One_To_One/Security/SecurityConfig.java`](../src/main/java/uk/ac/cf/_5/group14/One_To_One/Security/SecurityConfig.java)

## What Changes In Dev Mode

### Login Entry

When dev mode is active:

- `GET /login` renders the development login landing page
- `GET /login?devLogin=1` renders the normal login form
- the auth pages still use the shared auth layout

Relevant code:

- [`src/main/java/uk/ac/cf/_5/group14/One_To_One/Users/UserController.java`](../src/main/java/uk/ac/cf/_5/group14/One_To_One/Users/UserController.java)

### Public Browsing Rules

Dev mode keeps a wider browsing surface open for testers. Public pages remain directly accessible, including:

- `/`
- `/about`
- `/faq`
- `/pricing`
- `/explore`
- `/signup/**`
- `/policies/**`
- `/dev-mode/**`

### Signed-In Rules That Still Apply

Dev mode does not turn the entire app into a guest-access system. The main signed-in surfaces still require authentication, including:

- `/dashboard/**`
- `/calendar/**`
- `/goals/**`
- `/workouts/**`
- `/workout-session/**`
- `/workout-management/**`
- `/profile/**`
- `/inbox/**`
- `/chat/**`
- `/merch/**`

Role-gated areas also stay protected in dev mode:

- `/trainer/**`
- `/gym/**`
- `/admin/**` subsets
- `/super-admin/**`

## Dev Hub

The Dev Hub is the main development-mode navigation surface for testers.

Current behavior:

- the Dev Hub groups routes into `Public`, `Login Required`, and `Restricted`
- guests can use it to discover public routes
- signed-in users can jump into authenticated test areas
- restricted pages point users at a dedicated restricted notice page
- admin controls can change a page's effective dev-mode access state

Relevant code:

- [`src/main/java/uk/ac/cf/_5/group14/One_To_One/DevMode/DevModePageAccessService.java`](../src/main/java/uk/ac/cf/_5/group14/One_To_One/DevMode/DevModePageAccessService.java)
- [`src/main/java/uk/ac/cf/_5/group14/One_To_One/DevMode/DevModePageRestrictionFilter.java`](../src/main/java/uk/ac/cf/_5/group14/One_To_One/DevMode/DevModePageRestrictionFilter.java)
- [`src/main/resources/templates/dev-mode/hub.html`](../src/main/resources/templates/dev-mode/hub.html)

### Default Dev Hub Route Groups

By default, the Dev Hub currently treats these pages as public:

- home
- about
- pricing
- faq
- explore
- signup
- privacy policy
- terms

By default, it currently treats these pages as login-required:

- dashboard
- calendar
- goals
- workouts
- profile
- messages/inbox

By default, it currently treats these pages as restricted:

- leaderboard
- training vault
- client trainers

These defaults come from `DevModePageAccessService` and can be overridden by stored page settings.

## Dev-Mode Route Restriction Flow

When dev mode is active, the request filter checks whether a route is currently marked as blocked in the Dev Hub configuration.

If a page is restricted:

- the user is redirected to `/dev-mode/restricted?pageKey=...`
- the restricted page shows the page title, icon, and restriction message

If a page is not restricted:

- the request continues through the normal route and security rules

## Navbar And Header Behavior In Dev Mode

The current navbar behavior for authenticated users in dev mode is:

- the `DEV MODE` pill sits outside the profile container, to the left of the profile shell
- the logged-in profile shell keeps its normal layout instead of compressing around the dev badge
- the navbar profile preview now resolves the real logged-in user rather than relying on placeholder values

The current profile preview pulls from the authenticated user plus available settings/progress data, including:

- display name
- avatar initials fallback
- bio
- visible milestones
- points and level progress

Relevant files:

- [`src/main/resources/templates/fragments/navbar.html`](../src/main/resources/templates/fragments/navbar.html)
- [`src/main/resources/templates/fragments/username-logout.html`](../src/main/resources/templates/fragments/username-logout.html)
- [`src/main/resources/static/css/components/core/navbar.css`](../src/main/resources/static/css/components/core/navbar.css)

## Identity Resolution In Active Dev Work

The current repository state resolves the logged-in user more reliably than the older session-only path.

The active identity path now prefers the authenticated principal and falls back to session state only when needed. Login identifiers are resolved against:

- username
- email
- normalized gym code

Relevant files:

- [`src/main/java/uk/ac/cf/_5/group14/One_To_One/Security/CurrentUserResolver.java`](../src/main/java/uk/ac/cf/_5/group14/One_To_One/Security/CurrentUserResolver.java)
- [`src/main/java/uk/ac/cf/_5/group14/One_To_One/Users/UserLookupService.java`](../src/main/java/uk/ac/cf/_5/group14/One_To_One/Users/UserLookupService.java)
- [`src/main/java/uk/ac/cf/_5/group14/One_To_One/Security/CustomAuthenticationSuccessHandler.java`](../src/main/java/uk/ac/cf/_5/group14/One_To_One/Security/CustomAuthenticationSuccessHandler.java)

## Development Notes

### Local Development

Dev mode is mainly a local or controlled testing feature. The usual setup is:

1. set `DEV_MODE=true`
2. run the app locally
3. use `/login` for the dev entry surface
4. use `/login?devLogin=1` when the full login form is needed

### Production And Hosted Environments

Hosted environments should leave dev mode off unless a deliberate testing session is being run. Normal Render deployment should treat `DEV_MODE` as `false` unless the team explicitly wants the development browsing behavior.

## Update Rule

Update this document when a change affects development-only behavior such as:

- Dev Hub groups or restriction defaults
- dev login flow
- development-only route access rules
- dev-only banners, pills, notices, or testing surfaces
- temporary UI behavior being tested before it becomes the stable baseline

When a change is no longer dev-specific, move it into [System-Overview.md](./System-Overview.md).
