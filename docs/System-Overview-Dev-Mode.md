# System Overview - Dev Mode

## Purpose Of This File
This file is the development-mode companion to [System-Overview.md](/g:/No%20OneDrive%20Work/My%20Website/Crystal-Productions-OneToOne/One%20To%20One/2025_Group14_C24071109/docs/System-Overview.md).

Use the two files differently:
- `System-Overview.md` describes the platform at a stable, broad, product level.
- `System-Overview-Dev-Mode.md` describes what the website currently does in the active development state, especially features that have been changed recently and may still be settling.

In short:
- The system overview is the baseline map of the website.
- The dev-mode overview is the live implementation snapshot.

## Current Dev Mode Snapshot
As of 15 March 2026, the website remains a Spring Boot behaviour-change coaching platform with role-based dashboards, planning, tracking, trainer-client collaboration, messaging, profile customisation, and saved user settings.

Dev mode currently reflects a more up-to-date UI and interaction layer than the stable overview in a few important areas:
- Client dashboard interactions have been refined beyond the broader stable description.
- Shared profile preview behaviour has been standardised across more surfaces.
- Profile completion guidance behaviour has changed on the profile page.
- Preferences flow has been reworked into a one-time quick setup followed by a full editable preferences editor.
- Authentication and verification behaviour is now stricter than the broad baseline summary suggests.
- Dev Mode itself now has more explicit platform-level behaviour, including a redesigned Dev Hub and admin-managed page availability states.

## What Dev Mode Covers That The Stable Overview Does Not

### 1. Latest Behaviour Changes
This file should track recent feature changes before the stable overview is rewritten to absorb them.

Current examples:
- Profile bios now use shared read more / read less logic across profile-related preview surfaces, and short bios do not show the toggle.
- The client dashboard planner strip uses the same drag and touch interaction pattern as the weather scroller.
- The profile completion radar now behaves differently when the user reaches 100 percent completion, including a dismiss flow instead of the previous hover-driven interaction.
- Preferences now use a one-time quick setup flow instead of always opening directly into the older setup experience.
- Protected pages such as profile, dashboard, calendar, goals, and workouts are now enforced at the security layer instead of relying only on page-level assumptions.
- Email verification is now part of the actual login gate, including resend-on-login-attempt behaviour for unverified users.
- Verification pages now use a stronger resend-code flow with cooldown timing, clearer helper messaging, and a more polished card layout.
- Authentication pages now share a tighter, more consistent layout system with reduced top whitespace and more controlled footer/scroll behaviour.
- Dev Hub page availability now comes from configurable page states, not just static dev-mode messaging.

### 2. Active UX Direction
Dev mode should describe how the site is currently being shaped, not only what pages exist.

The current direction includes:
- More shared UI logic instead of duplicating behaviour per page.
- More polished stateful interactions such as drag-scroll, dismiss states, read-more handling, and sticky save prompts.
- Cleaner separation between onboarding flows and long-term settings management.
- Stronger use of external JS and CSS modules instead of template-local behaviour.
- More centralised access control and environment-aware route behaviour.
- More visible development-state communication for testers, guests, and admins.

### 3. Features That Are Present But Still Evolving
Some features are already usable but are better described here because their behaviour is still changing.

These currently include:
- Dashboard recommendation and planner presentation
- Weather and ambience interaction behaviour
- Profile preview cards and profile completion widgets
- Preferences setup and editing experience
- Dev Mode page availability and restricted-page handling
- Authentication, verification, and shared auth-page layout behaviour

## Dev Mode Feature State By Area

### Client Dashboard
The client dashboard is one of the most actively refined parts of the website in dev mode.

Current dev-mode notes:
- Action Hub remains a key recommendation surface.
- Planner preview cards are being tuned for alignment, readability, and horizontal dragging behaviour.
- Weekly summary, trainer relationship, and profile rail remain active and interconnected.
- Weather and ambience components act as a reference implementation for richer drag/touch interactions.

### Profile Experience
Profile work in dev mode goes beyond simple profile editing.

Current dev-mode notes:
- Profile previews are shared between navbar, dashboard, and profile surfaces.
- Bio expansion behaviour is standardised so long bios collapse cleanly and short bios stay fully visible.
- The profile completion radar now supports a completed-and-dismissed state rather than continuing to behave like an incomplete-progress hover prompt.
- Profile appearance settings remain tied to saved user settings and shared preview rendering.

### Preferences Experience
Preferences are now split into two different experiences in dev mode:

#### One-Time Quick Setup
- The quick setup page is intended to appear only once per user.
- It provides a fast preset-based setup path.
- Once completed or skipped, it should no longer appear again for that user.

#### Full Preferences Editor
- The main preferences page becomes the long-term settings editor.
- Preferences are grouped into cleaner categories such as design, accessibility, training choices, conditions, and smart defaults.
- Users can adjust saved values directly instead of treating preferences as a one-off setup wizard.
- A bottom save prompt appears when the page becomes dirty after an edit.

This is one of the clearest examples of why this dev-mode file exists: the stable overview can say the site has preferences support, while this file explains how that experience currently behaves.

### Authentication And Verification Experience
Authentication and verification now need to be described more specifically in dev mode than in the stable overview.

Current dev-mode notes:
- Core logged-in routes are actively protected through the security layer rather than only by navigation visibility.
- New accounts are redirected into email verification as part of signup completion.
- Users cannot complete login until email verification succeeds.
- If an unverified user attempts to log in, the platform resends verification and redirects them back into the verification flow.
- Email verification pages now include resend cooldown behaviour, spam-folder guidance, and a cleaner reusable layout.
- Phone verification uses the same modern card structure and spacing system as the email verification experience.
- Login, signup, forgot-password, reset-password, and verification pages now share a more consistent auth layout with reduced vertical dead space.

### Dev Mode Platform Controls
Dev mode is now more than a passive banner or informational state.

Current dev-mode notes:
- Dev Mode indicators have been simplified in the navbar and positioned more intentionally for logged-in users.
- The homepage shows a development-session warning and Dev Hub entry point for logged-out users.
- The Dev Hub now acts as a structured testing dashboard rather than a simple route list.
- Pages are grouped by availability state such as Public, Login Required, and Restricted.
- Admins can change page states from the admin dashboard, and the Dev Hub updates from that configuration.
- Restricted routes can redirect users to a dedicated unavailable-during-development notice page.

## Relationship To The Main System Overview
The main system overview should answer:
- What can the website do overall?
- What roles exist?
- What major page groups and data relationships exist?

This dev-mode overview should answer:
- What has changed recently?
- What is the website currently doing in the active branch or latest implementation state?
- Which parts of the UX are newer, more experimental, or mid-refactor?
- Which feature behaviours are more specific than the broad stable overview needs to be?

The two files should stay aligned, but they should not be identical.

## Suggested Update Rule
When a feature changes noticeably in behaviour, layout, or user flow:
1. Update `System-Overview-Dev-Mode.md` first.
2. Keep the entry specific about what changed.
3. Update `System-Overview.md` later when the change feels stable enough to become part of the baseline product description.

## Current Dev Mode Summary
Right now, dev mode represents a more refined and more current version of the platform than the stable overview alone shows.

The website in dev mode currently emphasises:
- richer dashboard interactions
- stronger shared profile component behaviour
- better completion and dismissal states
- a more modern preferences flow with once-only onboarding and ongoing editable settings
- stricter authentication and verification flow enforcement
- a more explicit Dev Mode testing layer with configurable page availability

That makes this file the best place to track the latest website behaviour, while `System-Overview.md` remains the broad system map.
