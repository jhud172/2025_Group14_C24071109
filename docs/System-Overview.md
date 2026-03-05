# System Overview

## 1. Platform Purpose & Scope
The One-to-One platform is a Spring Boot web application for behaviour-change coaching. It supports clients logging goals, schedules, health data, workouts, and check-ins; trainers managing clients, templates, programmes, and reviews; gym admins administering memberships and trainer onboarding; and super admins verifying trainers and monitoring policy compliance.
The system solves: structured coaching workflows, verified trainer access, membership product management, and safe messaging with policy enforcement.
Out of scope: real payment processing (only stub provider exists), external service integration beyond configured email placeholders, and native mobile clients.

## 2. High-Level Architecture
The system is a single Spring Boot monolith with domain packages under `uk.ac.cf._5.group14.BehaviourChangeGroupProject`. Spring MVC controllers drive server-rendered Thymeleaf templates. Business logic resides in services, with persistence via Spring Data JPA repositories and Hibernate against H2/PostgreSQL.

Tech stack summary:
- Java 21, Spring Boot 3.5.7, Spring MVC, Spring Data JPA, Spring Security.
- Thymeleaf + Spring Security extras for server-side rendering.
- PostgreSQL (runtime) and H2 (local/dev) with SQL init scripts.
- OkHttp (HTTP client), OpenPDF (PDF export), Jsoup (HTML sanitising).
- Tailwind CSS for styling (configured in the build, not described here).

Data flow:
- Controllers build model attributes from services and repositories.
- Services implement business rules and ownership checks.
- Repositories map entities to database tables.
- Templates render server-side HTML with role-aware fragments.

Role-based access shapes architecture:
- Security configuration and helper utilities enforce authentication and role checks.
- Access guards and service-level checks enforce trainer-client link state and gym ownership.
- Controllers route to role-specific pages and APIs.

## 3. Java Package Responsibilities (Authoritative)

### Accountability
Handles accountability tracking models such as user streaks and weekly summaries. Contains repositories and entities for streak types and summary records used by dashboards and adherence reporting.

### BehaviourMemoryData
Captures and persists behaviour memory entries. Provides repository and service support for storing longitudinal behavioural responses.

### CalendarData
Owns calendar task domain, warning triggers, and daily completion metrics. Provides services and repositories for calendar tasks, warnings, and streak/completion calculations used by schedules and adherence.

### Chat
Legacy coach chat system with conversation, message, and action parsing. Includes controllers, services, and repositories for chat history, action pipelines, and usage tracking.

### ChatV2
Newer AI chat system with folders, threads, messages, action parsing, and response shaping. Includes controllers, repositories, and parsers for conversational UI flows.

### Checkins
Weekly check-in workflow for trainer prompts and client submissions. Includes entities, repositories, services, and a controller to submit and review check-ins.

### ConditionsPreferences
Health condition preferences and user preferences. Includes user preference forms, services, and controllers that read and update preference data.

### Config
Shared configuration classes, currently time-related configuration used across scheduling and notifications.

### CustomExerciseData
Custom exercise CRUD for trainer or user-created exercise definitions. Includes entities, repositories, services, and controller endpoints.

### Dashboard
Dashboard layout and summary data for role-specific landing pages. Contains layout entities, update requests, summary DTOs, and a controller for dashboard views.

### DataExport
Data export request tracking with status handling. Includes request entity, repository, service, and status enums.

### DayHealthData
Daily health metrics and AI summaries. Provides entities, repositories, and services for daily health logging and aggregation.

### DayMode
Daily mode selection and context handling for personalised UX and behavioural flow adjustments. Includes day-mode entities, services, and related orchestration.

### ExerciseData
Core exercise catalogue (non-custom). Contains exercise entities, tags, repository, service, and controller for catalogue access.

### ExerciseLog
Exercise logging workflows including PDF export. Contains log entities, repository, service, form DTOs, and controller endpoints.

### Explore
Trainer discovery and browse features. Controller coordinates explore results and filters for public access.

### FavouriteData
Favourite/bookmark system for entities. Includes favourite entity, repository, service, and controller for add/remove/list.

### FeedbackData
Adaptive feedback capture and AI-linked feedback entries. Contains feedback entities, repositories, and services for tone/key management.

### FocusData
Focus tracking and time-based focus actions. Includes focus entities, repositories, and services for daily and timed focus records.

### Goals
Goal management, linking, check-ins, and adherence calculations. Contains goal entities, link and check-in services, repositories, and controller endpoints.

### GymProfile
Gym profile model and service layer for gym-level profile data. Includes profile entity and repository.

### HealthConditions
User health conditions and follow-up scheduling. Includes condition entities, repositories, service logic, and timed follow-up scheduler.

### HealthDataInput
Health record input and physical condition tracking. Includes health record entities, form DTOs, repositories, services, and controllers for CRUD flows.

### HomePage
Home page aggregation logic for authenticated and public users. Provides controller and supporting DTOs for home view data.

### Inbox
Threaded inbox messaging system distinct from legacy messaging. Includes conversation entities, message models, repositories, services, controllers, and API controller for thread lists and messages.

### Level
Gamification system with level progress and leaderboard data. Includes level entities, repository, service, and controller for views.

### Membership
Gym membership products and subscriptions with price change auditing and email notifications. Includes entities, repositories, service logic, price change DTOs, and email service implementations.

### Messaging
Legacy trainer-client messaging with off-platform payment detection. Includes message/thread entities, repositories, a controller, and keyword detection with audit logging of policy breaches.

### Notes
Notes and folders with sanitisation and preview DTOs. Includes entities, repositories, services, and controllers for notes CRUD.

### Nutrition
Daily nutrition logging with per-day entries, range summaries for calendar overlays, and summary text used for AI-ready prompts. Includes log entity, repository, validation-aware services, and UI/API controllers.

### Notifications
System notifications, SSE delivery, and scheduled notifications. Includes notification entities, repositories, DTOs, access guard, SSE registry, and controller APIs.

### Payments
Pricing and checkout stubs. Includes payment provider abstraction, stub implementation, and a pricing controller.

### PlatformBilling
Platform and gym subscription tracking. Includes subscription entities, repositories, services, controllers, and plan/status enums.

### Policies
Public policy pages. Controller serves payments and related policy content.

### Profile
User profile data, profile image storage, and tracker DTOs. Includes controller, services, and storage helpers.

### PublicProfile
Public trainer profile presentation. Controller serves public profile view based on trainer data.

### QuickActions
Quick action shortcuts and contextual action surfacing for dashboards/home experiences. Includes models/services used to compose actionable UI prompts.

### ReflectionData
Reflection and journalling data with AI-derived results. Includes entities and services for reflection analysis.

### Reviews
Trainer review submission and moderation. Includes entities, repositories, controller, and moderation services.

### ScheduleData
Schedules, schedule entries, occurrences, and calendar integration. Includes entities, repositories, services, and controllers for schedule CRUD and calendar views.

### Security
Authentication, authorisation, login throttling, session handling, and security configuration. Includes Spring Security config, access guard utilities, and login handlers.

### StrengthLog
Strength training sessions and set logging. Includes entities, repositories, services, and controller for strength training logs.

### TrainerAssignments
Trainer-assigned schedules and client plans. Includes assigned schedule/workout entities, services, and controllers for trainer-to-client plan delivery.

### TrainerClient
Trainer-client link management with coaching phase tracking. Includes entities, repositories, services, and controller for relationship lifecycle and phase changes.

### TrainerLibrary
Trainer-managed template library for exercises, workouts, and programmes. Includes template entities, repositories, service layer, and controller endpoints for CRUD and sharing.

### TrainerProfile
Trainer profile domain. Includes profile entity, repository, controller, and validation helpers.

### TrainerTemplates
Trainer schedule template system with entries, previews, and apply workflows. Includes template entities, repositories, service, and controller.

### Users
User accounts, roles, signup forms, password reset flow, and user lookup. Includes user entity, repositories, service implementations, controllers, and form validation.

### UserSettings
User settings for theme, calendar preferences, accessibility flags, and language. Includes settings entity, repositories, services, and interceptors for locale and per-request caching.

### Vault
Secure notes repository (vault). Includes vault entities, repositories, services, and controller for private storage workflows.

### Verification
Email/phone verification and trainer verification workflows. Includes verification entities, repositories, services, and admin controllers.

### Workout
Workout domain and AI suggestions. Includes workout entity, repository, service, controller, and DTOs for saving workouts.

### Workouts
Workout builder and player system with sessions, set logs, feedback, and video tracking. Includes entities, repositories, services, and controllers for workout creation and play flows.

## 4. Cross-Cutting Systems

### Security & Authorisation
Spring Security enforces authentication, role checks, and access control. Custom handlers manage login success/failure and invalid sessions. AccessGuard and helper utilities apply ownership constraints within services and controllers.

### Trainer–Client Relationship Enforcement
Trainer access is gated on active trainer-client links. Services such as trainer templates, check-ins, and coaching phase changes check link status before allowing write access or viewing client data.

### Membership & Payment Gating
Membership products and subscriptions are managed in the Membership package. Pricing and checkout are stubbed in Payments, with platform subscription tracking in PlatformBilling. Membership changes and trainer verification are controlled by gym and super admin roles.

### Notifications & Messaging Safeguards
Notifications are delivered via system notification services and SSE for near-real-time updates. Legacy messaging includes off-platform payment keyword detection, with audit logging and admin review pages for flagged content.

### Audit Trails and Compliance Logging
Membership price changes are fully audited. Trainer verification workflows record reviewer actions. Off-platform payment attempts are logged for administrative review.

## 5. Template System Overview
Thymeleaf is used for server-rendered HTML to keep UI state aligned with server-side business rules. Templates use a base layout and fragments for shared navigation, banners, and widgets. Role-aware rendering is implemented through server-side model attributes and security-aware fragments. Controllers populate model data; templates read those attributes to render lists, forms, and detailed views.

## 6. Template Catalogue (Complete & Grouped)

### Public and Marketing
- templates/index.html — Public landing page. Access: Public. Data: public homepage content and CTA flags.
- templates/public/about.html — About page. Access: Public. Data: static content.
- templates/payments/pricing.html — Pricing tiers. Access: Public. Data: plan list and pricing text.
- templates/payments/pricing-checkout.html — Checkout flow. Access: Public/Authenticated. Data: selected plan and pricing details.
- templates/home/public.html — Public home variant. Access: Public. Data: public homepage content.

### Authentication and Verification
- templates/User/login.html — Login form. Access: Public. Data: login form model and error flags.
- templates/User/signup.html — Unified signup form entry point. Access: Public. Data: signup form model.
- templates/User/signup-choice.html — Role selection. Access: Public. Data: role options.
- templates/User/signup-client.html — Client sign-up. Access: Public. Data: client signup form.
- templates/User/signup-trainer.html — Trainer sign-up with verification fields. Access: Public. Data: trainer signup form.
- templates/User/signup-gym.html — Gym admin sign-up. Access: Public. Data: gym signup form.
- templates/User/forgot-password.html — Password reset request. Access: Public. Data: email input model.
- templates/User/reset-password.html — Password reset form. Access: Public. Data: reset token and password form.
- templates/auth/confirm-logout.html — Logout confirmation. Access: Authenticated. Data: session state.
- templates/verify/email-confirm.html — Email verification confirmation. Access: Public. Data: verification outcome.

### Home and Dashboards
- templates/home/auth.html — Authenticated home view. Access: Authenticated. Data: user summary and upcoming items.
- templates/home/user.html — Authenticated home variant. Access: Authenticated. Data: user summary and upcoming items.
- templates/homepage/HomePage.html — Main homepage content. Access: Public/Auth. Data: homepage content blocks.
- templates/dashboard/client-dashboard.html — Client dashboard. Access: Client. Data: tasks, upcoming workouts, progress summaries.
- templates/dashboard/trainer-dashboard.html — Trainer dashboard. Access: Trainer. Data: client stats, pending requests.
- templates/dashboard/gym-dashboard.html — Gym admin dashboard. Access: Gym admin. Data: membership and revenue summaries.

### Calendar
- templates/calendar/day.html — Day view. Access: Authenticated. Data: day schedule entries and tasks.
- templates/calendar/week.html — Week view. Access: Authenticated. Data: weekly schedule entries.
- templates/calendar/month.html — Month view. Access: Authenticated. Data: monthly schedule entries.
- templates/calendar/task-detail.html — Task detail view. Access: Authenticated. Data: selected task/occurrence details.

### Schedule Builder and Planning
- templates/schedule/builder.html — Schedule builder. Access: Trainer/Gym admin (as configured). Data: schedule template and entries.
- templates/schedule/add-entry.html — Add schedule entry. Access: Authenticated. Data: entry form model.
- templates/schedule/list.html — Schedule list. Access: Authenticated. Data: schedules list.
- templates/schedule/select-schedule.html — Select schedule template. Access: Authenticated. Data: template list.
- templates/schedule/apply.html — Apply schedule template. Access: Authenticated. Data: selected template and date range.
- templates/schedule/workout.html — Scheduled workout view. Access: Authenticated. Data: workout details and schedule context.

### Goals and Check-ins
- templates/goals/index.html — Goals list. Access: Client/Trainer (active link). Data: goals list and filters.
- templates/goals/create.html — Goal creation. Access: Client. Data: goal form fields.
- templates/goals/edit.html — Goal edit. Access: Client/Trainer (active link). Data: goal and edit fields.
- templates/goals/detail.html — Goal detail. Access: Client/Trainer (active link). Data: goal, links, adherence metrics.
- templates/goals/checkins.html — Goal check-in history. Access: Client/Trainer (active link). Data: check-in list and form.
- templates/checkins/client-submit.html — Weekly check-in submission. Access: Client. Data: check-in form and questions.
- templates/checkins/trainer-review.html — Weekly check-in review. Access: Trainer. Data: check-in content and response form.

### Workouts
- templates/workouts/index.html — Workout templates list. Access: Trainer/Client (as assigned). Data: workout templates list.
- templates/workouts/edit.html — Workout builder editor. Access: Trainer. Data: template, exercise list, set definitions.
- templates/workouts/start.html — Workout player. Access: Client. Data: active session and set logging state.
- templates/strengthlog/workout-session.html — Strength workout session. Access: Client. Data: session sets and logging inputs.
- templates/strengthlog/exercise-session.html — Strength exercise session. Access: Client. Data: exercise set inputs.
- templates/strengthlog/completion.html — Strength session summary. Access: Client. Data: session summary.

### Exercise Logs and Health Records
- templates/exercise-log/exercise-log-list.html — Exercise logs list. Access: Client. Data: log list.
- templates/exercise-log/exercise-log-form.html — Exercise log form. Access: Client. Data: log form fields.
- templates/exercise-log/exercise-log-view.html — Exercise log detail. Access: Client. Data: log entry.
- templates/exercise-log/ExerciseTutorial.html — Exercise tutorial. Access: Client. Data: tutorial content.
- templates/health-record/health-record-list.html — Health records list. Access: Client. Data: record list.
- templates/health-record/health-record-form.html — Health record form. Access: Client. Data: record form fields.
- templates/health-record/health-record-view.html — Health record detail. Access: Client. Data: record entry.

### Conditions and Preferences
- templates/conditions-preference/preference-form.html — Condition preferences form. Access: Authenticated. Data: preference form fields.
- templates/conditions-preference/view-preferences.html — View saved condition preferences. Access: Authenticated. Data: saved preferences and summaries.

### Explore and Nutrition
- templates/explore/index.html — Trainer explore/discovery view. Access: Public/Auth (as configured). Data: trainer browse list and filters.
- templates/nutrition/daily-log.html — Daily nutrition tracker. Access: Authenticated. Data: day entries and nutrition summary.

### Notes and Vault
- templates/notes/index.html — Notes list. Access: Authenticated. Data: notes list.
- templates/notes/folders.html — Notes folder view. Access: Authenticated. Data: folder list and counts.
- templates/notes/note-form.html — Note editor. Access: Authenticated. Data: note form fields.
- templates/notes/note-view.html — Note detail. Access: Authenticated. Data: note content.
- templates/vault/index.html — Vault list. Access: Authenticated. Data: vault notes list.
- templates/vault/note-form.html — Vault note editor. Access: Authenticated. Data: vault note form.
- templates/vault/note-view.html — Vault note detail. Access: Authenticated. Data: vault note content.

### Trainer Tools
- templates/trainer/clients.html — Trainer client roster. Access: Trainer. Data: client list and statuses.
- templates/trainer/active-clients.html — Active client list. Access: Trainer. Data: active links.
- templates/trainer/client-requests.html — Client requests. Access: Trainer. Data: pending requests.
- templates/trainer/client-detail.html — Client detail. Access: Trainer. Data: client profile, plan summary, goals.
- templates/trainer/library.html — Trainer library. Access: Trainer. Data: templates and library stats.
- templates/trainer/profile/view.html — Trainer profile view. Access: Trainer. Data: profile fields.
- templates/trainer/profile/edit.html — Trainer profile edit. Access: Trainer. Data: edit form fields.
- templates/trainer/exercises/list.html — Trainer exercises list. Access: Trainer. Data: exercise list.
- templates/trainer/exercises/create.html — Create exercise. Access: Trainer. Data: exercise form.
- templates/trainer/exercises/edit.html — Edit exercise. Access: Trainer. Data: exercise form and existing values.
- templates/trainer/exercises/view.html — Exercise detail. Access: Trainer. Data: exercise content.
- templates/trainer/workouts/list.html — Trainer workouts list. Access: Trainer. Data: workout templates list.
- templates/trainer/workouts/create.html — Create workout template. Access: Trainer. Data: workout form.
- templates/trainer/workouts/edit.html — Edit workout template. Access: Trainer. Data: workout and exercise list.
- templates/trainer/workouts/view.html — Workout template view. Access: Trainer. Data: workout detail.
- templates/trainer/programmes/list.html — Programmes list. Access: Trainer. Data: programme list.
- templates/trainer/programmes/create.html — Create programme. Access: Trainer. Data: programme form.
- templates/trainer/programmes/edit.html — Edit programme. Access: Trainer. Data: programme details.
- templates/trainer/programmes/view.html — Programme detail. Access: Trainer. Data: programme structure.
- templates/trainer/templates/index.html — Trainer schedule templates list. Access: Trainer. Data: template list.
- templates/trainer/templates/edit.html — Trainer schedule template editor. Access: Trainer. Data: template and entry list.
- templates/trainer/templates/apply.html — Apply trainer template. Access: Trainer. Data: preview and apply options.

### Client Domain
- templates/client/trainers.html — Trainer directory. Access: Client. Data: trainer list and filters.
- templates/client/my-trainer.html — Current trainer relationship. Access: Client. Data: link status and contract details.
- templates/client/assigned-plan.html — Assigned plan view. Access: Client. Data: plan summary and items.
- templates/client/assessment-form.html — Client assessment intake. Access: Client. Data: assessment form.
- templates/client/plan.html — Client plan overview. Access: Client. Data: plan summary.

### Membership and Admin
- templates/gym-admin/trainers.html — Gym trainer management. Access: Gym admin. Data: trainer list and verification status.
- templates/gym-admin/memberships/list.html — Membership list. Access: Gym admin. Data: products list.
- templates/gym-admin/memberships/form.html — Membership create/edit. Access: Gym admin. Data: product form.
- templates/gym-admin/memberships/price-change.html — Price change form. Access: Gym admin. Data: product and audit details.
- templates/gym-admin/memberships/price-history.html — Price change audit. Access: Gym admin. Data: audit history.
- templates/super-admin/verification-queue.html — Verification queue. Access: Super admin. Data: pending requests.
- templates/super-admin/verification-detail.html — Verification review detail. Access: Super admin. Data: verification submission details and decision actions.
- templates/admin/off-platform-payments.html — Off-platform payment review. Access: Admin/Super admin. Data: flagged attempts list.

### Messaging and Inbox
- templates/messages/client-inbox.html — Legacy client inbox. Access: Client. Data: thread list.
- templates/messages/trainer-inbox.html — Legacy trainer inbox. Access: Trainer. Data: thread list.
- templates/messages/thread.html — Legacy message thread. Access: Client/Trainer. Data: messages, thread status, policy flags.
- templates/inbox/index.html — Inbox conversation list. Access: Client/Trainer. Data: conversations list.
- templates/inbox/thread.html — Inbox thread view. Access: Client/Trainer. Data: messages and participants.

### ChatV2 and Coach Chat
- templates/chat/hub.html — ChatV2 hub. Access: Authenticated. Data: folders and recent threads.
- templates/chat/folder.html — ChatV2 folder view. Access: Authenticated. Data: folder and thread list.
- templates/chat/thread.html — ChatV2 thread view. Access: Authenticated. Data: thread, messages, action prompts.
- templates/chat/chat.html — Legacy/alternate chat view. Access: Authenticated. Data: chat transcript.

### Reviews and Policies
- templates/review/form.html — Review submission. Access: Client. Data: review form.
- templates/policies/payments.html — Payments policy. Access: Public. Data: static policy content.
- templates/policies/terms.html — Terms of service. Access: Public. Data: static policy content.
- templates/policies/subscription-terms.html — Subscription terms. Access: Public. Data: static policy content.

### Profile and Public Profiles
- templates/profile/profile.html — User profile view. Access: Authenticated. Data: user profile fields.
- templates/public/profile.html — Public trainer profile view. Access: Public. Data: trainer profile fields.

### Levels
- templates/levels/me.html — Personal level progress. Access: Authenticated. Data: level progress and XP stats.
- templates/levels/leaderboard.html — Leaderboard. Access: Authenticated. Data: ranking list.

### Error Pages
- templates/error/403.html — Access denied. Access: All. Data: error context.

### Fragments and Shared Components
- templates/base.html — Base HTML layout. Access: Shared. Data: page title and content blocks.
- templates/fragments/ui-shell.html — Primary layout shell. Access: Shared. Data: layout slots.
- templates/fragments/navbar.html — Navigation bar. Access: Shared. Data: user role and links.
- templates/fragments/footer.html — Footer. Access: Shared. Data: policy/support links.
- templates/fragments/banner.html — Global banners. Access: Shared. Data: banner message state.
- templates/fragments/username-logout.html — User menu. Access: Shared. Data: username.
- templates/fragments/profile-modules.html — Profile modules. Access: Shared. Data: profile sections.
- templates/fragments/quick-actions.html — Quick actions panel fragment. Access: Shared. Data: action shortcuts and state.
- templates/fragments/chat/chat-widget.html — Chat widget. Access: Shared. Data: chat status and notifications.
- templates/fragments/chat/sidebar.html — ChatV2 sidebar. Access: Shared. Data: folders/threads list.
- templates/fragments/chat/blocks.html — ChatV2 block rendering. Access: Shared. Data: structured block items.
- templates/fragments/goals/goal-chip.html — Goal chip fragment. Access: Shared. Data: goal metadata and status.
- templates/fragments/chatbot.html — Chatbot widget. Access: Shared. Data: chatbot state.
- templates/fragments/daily-streak-bar.html — Streak bar. Access: Shared. Data: streak values.
- templates/fragments/edit-task.html — Task edit fragment. Access: Shared. Data: task form.
- templates/fragments/tailwind-components.html — UI component fragments. Access: Shared. Data: component parameters.
- templates/fragments/slimselectCss.html — SlimSelect CSS include. Access: Shared. Data: none.
- templates/fragments/slimselectJs.html — SlimSelect JS include. Access: Shared. Data: none.
- templates/fragments/workout/searchbar.html — Workout search. Access: Shared. Data: search params.
- templates/fragments/workout/workout-frags.html — Workout fragments. Access: Shared. Data: workout UI elements.

## 7. Key User Journeys (End-to-End)

### Client onboarding to ongoing coaching
Client signs up, completes assessment, links to a trainer, receives an assigned plan, and logs workouts and health records. Goals and weekly check-ins track adherence and progress, with notifications for upcoming tasks and trainer feedback.

### Trainer onboarding to client management
Trainer signs up, submits verification, and receives approval. Trainer manages clients, creates templates and programmes, assigns plans, and reviews client check-ins and goals, enforcing active trainer-client link rules.

### Gym admin membership management
Gym admin creates membership products, edits details, and initiates price changes. Price change events are audited and notifications are sent to affected subscribers, with history viewable in the admin UI.

### Messaging and policy enforcement
Clients and trainers exchange messages. Off-platform payment keywords are detected, logged, and surfaced to admin review tools, with policy pages defining permitted content.

## 8. System Boundaries & Invariants
- Trainers cannot access client data unless an active trainer-client link exists.
- Clients cannot modify trainer-owned templates or schedule structures.
- Gym admins can only manage membership products and trainers for their own gym.
- Super admins are the only role permitted to approve or reject trainer verification requests.
- Membership price changes require a reason, a future effective date, and are fully audited.
- Off-platform payment attempts are logged and surfaced for administrative review.
- Notifications and check-ins are scoped to authenticated users and their relationships.

## 9. Current State Summary
Core coaching workflows, membership products, trainer verification, messaging, notifications, nutrition logging, and trainer discovery are implemented and operational in the codebase. The UI is fully server-rendered with a comprehensive template set, including quick-action fragments and expanded verification/admin review pages. Stubbed integrations remain for email delivery and payment processing, which are intentionally incomplete in the current repository.


