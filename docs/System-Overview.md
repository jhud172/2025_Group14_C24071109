# System Overview

## Snapshot
The One to One platform is a Spring Boot monolith for behaviour-change coaching, planning, tracking, and trainer-client collaboration. It currently delivers role-based dashboards, calendar planning, nutrition and health logging, goals and check-ins, trainer plan delivery, messaging, gym and billing flows, and profile/personalisation features.

Current project state as of 14 March 2026:
- Client dashboard is active and heavily customised, including Action Hub, planner, weekly summary, trainer relationship card, profile rail, and live dashboard ambience/weather.
- Profile customisation is active, including banner/ring/card themes, bio, visible milestone selection, profile previews, and navbar live preview.
- Trainer, client, gym admin, and super admin flows exist in the codebase.
- Frontend is server-rendered with Thymeleaf fragments plus external JS/CSS modules.
- The focused dashboard/profile tests pass.
- The full Gradle suite is not fully green yet: 361 tests ran and 5 failed in unrelated calendar/goal areas.

## Platform Purpose
The system is designed to help:
- Clients manage day-to-day actions, logging, goals, planning, and coach communication.
- Trainers manage clients, share workouts/programmes/schedules, review check-ins, and stay connected through messaging.
- Gym admins manage memberships, trainers, verification workflows, and business-facing administration.
- Super admins oversee platform-level verification, safety, moderation, and compliance-sensitive areas.

## Main Roles And What They Can Do

### Client
- Use the client dashboard as the main operating surface.
- View current trainer relationship status and open trainer messages.
- See a recommended next action in the Action Hub.
- Review the next 7 days planner and weekly summary cards.
- Log nutrition, health data, goals, workouts, habits, and other progress inputs.
- View live ambience/weather with location-aware forecast data.
- Choose saved preferences such as theme, weather unit, weather display mode, and time format.
- Edit profile details, bio, visible milestones, and profile appearance.

### Trainer
- Maintain a trainer profile and public-facing coaching identity.
- Manage trainer-client links and coaching phases.
- Share workouts, programmes, and schedules with clients.
- Review client check-ins and reply with trainer feedback.
- Access trainer library, templates, assignments, and client-facing planning tools.
- Use trainer dashboard and messaging flows to monitor and support clients.

### Gym Admin
- Manage gym account details and trainer onboarding.
- Review subscription/membership data.
- Access gym dashboard and gym profile flows.
- Support trainer verification, memberships, and operational administration.

### Super Admin / Platform Admin
- Review trainer verification workflows.
- Monitor policy-sensitive areas such as payment safety and off-platform messaging flags.
- Access admin moderation and oversight pages.

## Core User Journeys

### Client Daily Journey
1. Open dashboard.
2. Review Action Hub recommended next action.
3. Check planner for the next 7 days.
4. Review weather/ambience and local time context.
5. Log meals, tasks, workouts, health records, and goals.
6. Message trainer if needed.

### Trainer Support Journey
1. Link with client.
2. Assign workouts, plans, or schedules.
3. Review check-ins and progress.
4. Send messages or respond to client updates.
5. Keep the client relationship visible through dashboard/profile previews.

### Gym Operations Journey
1. Manage gym profile and operational settings.
2. Oversee trainer onboarding and memberships.
3. Track subscriptions and platform billing context.

## Key Dashboard Surfaces

### Client Dashboard
Current client dashboard sections include:
- Explore Platform
- Track Body
- Track Schedule
- Live Dashboard Ambience
- Trainer Overview
- Action Hub
- Planner: Your Next 7 Days
- Weekly Summary
- Goals
- Help and Trust
- Your Profile

### Action Hub
The Action Hub is the client’s prioritisation card. It currently supports:
- Recommended view
- All view
- Task, workout, and meal/log-meal recommendation cards
- Primary recommendation plus smaller follow-up cards
- Countdown/timed states
- Sliding tab transition between Recommended and All

### Live Dashboard Ambience / Weather
The ambience panel currently supports:
- Local time + weather context
- Location permission handling
- Reload on permission allow
- Denied-permission fallback message
- 24 hour weather overview summary
- 24 hour forecast timeline
- Saved Celsius / Fahrenheit preference
- Saved Visual Weather View / Graph View preference
- Graph toggle between temperature graph and weather trend graph
- Horizontal drag-scroll forecast interaction

### Trainer Relationship Card
Current trainer card behaviour includes:
- Click-to-open trainer banner on dashboard
- Trainer bio with read more / read less logic
- Selected visible milestones only
- Empty fallback when no milestones are selected
- Trainer activity and messaging areas

### Shared Profile Preview Components
Shared preview behaviour exists in:
- Navbar floating profile preview
- Dashboard profile rail
- Trainer reveal card on dashboard
- Profile sidebar preview

Shared preview features include:
- Theme-aware banners and text colours
- Bio read-more handling
- Selected milestone-only rendering
- Hover preview in navbar
- Click-driven dashboard/profile reveal panels

## Major Page Groups

### Public / Marketing Pages
- Landing page
- About page
- Pricing and checkout pages
- Public homepage variants

### Authentication / Verification
- Login
- Signup choice and role-specific signup pages
- Forgot/reset password
- Email confirmation
- Logout confirmation
- Verification flows for email, phone, and trainer review

### Dashboard Pages
- Client dashboard
- Trainer dashboard
- Gym dashboard
- Admin-facing dashboard areas

### Planning / Calendar Pages
- Day view
- Week view
- Month view
- Task detail and planning flows
- Schedule selection and schedule application flows

### Goals / Check-ins
- Goals index
- Goal create/edit/detail
- Goal check-in history
- Weekly client submission and trainer review pages
- Milestone/achievement views

### Health / Nutrition / Tracking
- Health record input pages
- Nutrition logging pages
- Daily health and day-mode related pages
- Strength/workout logging flows

### Coaching / Messaging / Relationship Pages
- Explore trainers and related discovery pages
- Trainer relationship and plan pages
- Legacy messaging and inbox/chat areas
- Notifications and quick-action driven surfaces

### Profile / Settings / Preferences
- Profile page
- Profile settings drawers
- Preferences form
- Preferences summary page
- Public trainer profile

### Gym / Billing / Membership
- Membership management pages
- Platform billing pages
- Gym profile and operational pages
- Payment/pricing stub flows

## Important Data Connections

### User -> UserSettings
Stores:
- Theme and accessibility settings
- Weather temperature unit
- Weather display mode
- Time display format
- Profile banner/ring/card appearance
- Visible profile milestones

### Client -> TrainerClientLink -> Trainer
Controls:
- Whether trainer access is active/requested/inactive
- Which trainer data is shown on the dashboard
- Whether trainer messaging/review pages are allowed

### Trainer -> Assignments / Library -> Client
Connects:
- Shared workouts
- Shared programmes
- Shared schedules
- Trainer notes and coaching context

### Client -> Dashboard Summary / Calendar / Nutrition / Health
Feeds:
- Action Hub card composition
- Planner preview
- Weekly summary metrics
- Dashboard ambience state
- Progress and streak summaries

### User -> Profile Preview Components
Feeds:
- Bio
- Visible milestones
- Profile themes
- Points and level
- Navbar preview card
- Dashboard right-rail profile card

## Current Preference Support
The preference system currently includes saved options for:
- Theme
- Easy mode
- Weather temperature unit
- Weather display mode
- Time display format
- Dashboard weekly summary cards
- Nutrition and macro-related fields
- Equipment and other wellness defaults

## Architecture Notes
- Backend: Java 21, Spring Boot, Spring MVC, Spring Security, Spring Data JPA, Hibernate.
- Frontend: Thymeleaf templates, fragments, external JS modules, external CSS/Tailwind build pipeline.
- Database: H2 for tests/dev support, PostgreSQL-style runtime usage.
- Security: role-aware routes, ownership checks, trainer-client link enforcement.
- Notifications: application notifications and real-time update handling.
- Messaging safety: payment/off-platform detection exists in legacy messaging areas.

## Current Frontend Structure Rules
The current direction in the codebase is:
- No inline JavaScript in touched templates.
- No inline CSS in touched templates.
- Shared interactions live in external JS modules.
- Shared styling lives in external CSS files and compiled `app.css`.

This has already been applied to the updated dashboard/profile/weather work, including:
- Dashboard weather/ambience interactions
- Shared profile preview behaviour
- Profile card management handlers

## Testing Status

### Verified During This Update
Passed:
- `node --check src/main/resources/static/js/dashboard/client-dashboard-page.js`
- `node --check src/main/resources/static/js/core/profile-preview.js`
- `node --check src/main/resources/static/js/profile/profile-page.js`
- `node --check src/main/resources/static/js/profile/profile-cards-page.js`
- `npm run build:css`
- `./gradlew test --tests uk.ac.cf._5.group14.BehaviourChangeGroupProject.DashboardTests.ClientDashboardMvcTest`
- `./gradlew test --tests uk.ac.cf._5.group14.BehaviourChangeGroupProject.ProfileTests.ProfileRouteAccessTest`

### Full Suite Status
`./gradlew test` is currently not fully green.

Latest result:
- 361 tests completed
- 5 tests failed

Current failing tests observed:
- `CalendarNavigationAndJumpControlsTest > dayViewRendersAllThreeViewToggleLinks()`
- `GoalAccessControlTest > trainerAccessRequiresActiveLink()`
- `GoalAccessControlTest > goalLinkRejectsOtherUsersItems()`
- `GoalAccessControlTest > clientCannotAccessOtherClientsGoal()`
- `GoalAdherenceServiceTest > adherenceCountsLinkedItemsInWeek()`

Observed failure themes:
- One calendar assertion failure
- Four goal/H2 SQL grammar/resource usage failures

These failures were present in the broader suite and are not caused by the dashboard/profile/weather work completed in this pass.

## Progress So Far
The project is in a strong feature-rich state for client experience and role-based flows:
- The client dashboard is highly developed and now supports richer action selection, trainer relationship previews, and weather context.
- Profile personalisation is well established.
- Trainer and gym pathways are present and connected into the wider platform.
- Shared UI fragments and behaviour are becoming more standardised.

The main remaining quality signal is test stability in some non-dashboard areas, especially calendar and goal-related tests. Outside of that, the current dashboard/profile surface is moving in a clear and maintainable direction.
