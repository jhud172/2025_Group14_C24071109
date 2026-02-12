# One-to-One UI Templates Summary

## 1. Purpose of This Document
This document is the authoritative functional specification for all UI templates present in the repository. It explains what each page does, who can access it, and which backend systems it depends on. It should be used by developers, markers, and reviewers to understand the current UI surface area and its alignment with backend behaviour. It complements [SYSTEM_OVERVIEW.md](SYSTEM_OVERVIEW.md) for architecture context and [IMPLEMENTATION_SUMMARY.md](IMPLEMENTATION_SUMMARY.md) for feature delivery history.

## 2. Global Layout & UI Architecture
The UI is server-rendered using Thymeleaf. [templates/base.html](templates/base.html) provides the base HTML layout and is composed with shared fragments for navigation, banners, and common widgets. Role-aware rendering is achieved by server-side model attributes and Spring Security tag support. Conditional banners and warnings are rendered when account state or policy enforcement applies, such as verification status, payment policy violations, or inactive trainer-client links. Server-side state drives UI behaviour by providing the data structures required for lists, forms, and detail views; templates do not implement business rules, they reflect validated state from controllers and services.

## 3. Public & Authentication Pages
- [templates/index.html](templates/index.html) — Roles: Unauthenticated. Content: public landing content. Actions: navigation to signup/login and pricing. Backend: HomePage.
- [templates/about.html](templates/about.html) — Roles: Unauthenticated. Content: platform overview text. Actions: navigation only. Backend: none (static content).
- [templates/pricing.html](templates/pricing.html) — Roles: Unauthenticated/Authenticated. Content: subscription tiers and plan descriptions. Actions: select a plan. Backend: Payments.
- [templates/pricing-checkout.html](templates/pricing-checkout.html) — Roles: Unauthenticated/Authenticated. Content: checkout/plan confirmation. Actions: submit checkout. Backend: Payments (stub provider).
- [templates/home/public.html](templates/home/public.html) — Roles: Unauthenticated. Content: public home variant. Actions: navigation to signup/login. Backend: HomePage.
- [templates/home/auth.html](templates/home/auth.html) — Roles: Authenticated. Content: personalised home summary. Actions: navigate to core features. Backend: HomePage.
- [templates/home/user.html](templates/home/user.html) — Roles: Authenticated. Content: alternate authenticated home view. Actions: navigate to core features. Backend: HomePage.
- [templates/homepage/HomePage.html](templates/homepage/HomePage.html) — Roles: Unauthenticated/Authenticated. Content: homepage content blocks. Actions: navigation. Backend: HomePage.
- [templates/User/login.html](templates/User/login.html) — Roles: Unauthenticated. Content: login form. Actions: submit credentials. Backend: Security, Users.
- [templates/User/signup.html](templates/User/signup.html) — Roles: Unauthenticated. Content: generic signup form. Actions: register account. Backend: Users.
- [templates/User/signup-choice.html](templates/User/signup-choice.html) — Roles: Unauthenticated. Content: role selection (client/trainer/gym). Actions: choose signup path. Backend: Users.
- [templates/User/signup-client.html](templates/User/signup-client.html) — Roles: Unauthenticated. Content: client signup form. Actions: create client account. Backend: Users.
- [templates/User/signup-trainer.html](templates/User/signup-trainer.html) — Roles: Unauthenticated. Content: trainer signup form with verification fields. Actions: create trainer account. Backend: Users, Verification.
- [templates/User/signup-gym.html](templates/User/signup-gym.html) — Roles: Unauthenticated. Content: gym admin signup form. Actions: create gym admin account. Backend: Users, GymProfile.
- [templates/User/forgot-password.html](templates/User/forgot-password.html) — Roles: Unauthenticated. Content: password reset request. Actions: submit email. Backend: Users (password reset).
- [templates/User/reset-password.html](templates/User/reset-password.html) — Roles: Unauthenticated. Content: reset password form. Actions: submit new password using token. Backend: Users (password reset).
- [templates/auth/confirm-logout.html](templates/auth/confirm-logout.html) — Roles: Authenticated. Content: logout confirmation. Actions: confirm logout. Backend: Security.
- [templates/confirm-logout.html](templates/confirm-logout.html) — Roles: Authenticated. Content: logout confirmation. Actions: confirm logout. Backend: Security. Duplicate: alternate template path for the same flow.
- [templates/verify/email-confirm.html](templates/verify/email-confirm.html) — Roles: Unauthenticated. Content: email verification result. Actions: none beyond navigation. Backend: Verification.
- [templates/conditions-preference/preference-form.html](templates/conditions-preference/preference-form.html) — Roles: Authenticated. Content: condition preferences form. Actions: submit preference changes. Backend: ConditionsPreferences.
- [templates/conditions-preference/view-preferences.html](templates/conditions-preference/view-preferences.html) — Roles: Authenticated. Content: saved condition preferences. Actions: navigation to edit. Backend: ConditionsPreferences.

## 4. Dashboards (Role-Specific)

### Client Dashboard
- [templates/dashboard/client-dashboard.html](templates/dashboard/client-dashboard.html) — Roles: Client. Data summarised: upcoming tasks, workout schedule, progress indicators, and recent activity. Actions: navigate into workouts, goals, calendar, and messaging. Does not allow: administrative actions or trainer-only controls. Constraints: client-only data, ownership enforced in services.

### Trainer Dashboard
- [templates/dashboard/trainer-dashboard.html](templates/dashboard/trainer-dashboard.html) — Roles: Trainer. Data summarised: client counts, active plans, and pending requests. Actions: navigate to client management, templates, and reviews. Does not allow: gym admin or super admin actions. Constraints: trainer must access only linked clients.

### Gym Admin Dashboard
- [templates/dashboard/gym-dashboard.html](templates/dashboard/gym-dashboard.html) — Roles: Gym admin. Data summarised: membership and revenue snapshots. Actions: navigate to memberships and trainer management. Does not allow: trainer/client operations outside gym scope. Constraints: gym ownership enforced by services.

## 5. Communication Systems

### 5.1 Trainer–Client Messaging (Legacy)
- [templates/messages/client-inbox.html](templates/messages/client-inbox.html) — Roles: Client. Purpose: list trainer-client threads. Actions: open thread. Backend: Messaging.
- [templates/messages/trainer-inbox.html](templates/messages/trainer-inbox.html) — Roles: Trainer. Purpose: list client threads. Actions: open thread. Backend: Messaging.
- [templates/messages/thread.html](templates/messages/thread.html) — Roles: Client/Trainer. Purpose: message thread view. Actions: send messages and view history. Enforcement: off-platform payment keyword detection and warning banner; relationship status banners. Backend: Messaging, Policies.
- [templates/inbox/index.html](templates/inbox/index.html) — Roles: Client/Trainer. Purpose: conversation list for inbox system. Actions: open conversation. Backend: Inbox, Notifications.
- [templates/inbox/thread.html](templates/inbox/thread.html) — Roles: Client/Trainer. Purpose: inbox conversation view. Actions: send and read messages. Enforcement: conversation participants enforced. Backend: Inbox.

### 5.2 AI Chat (ChatV2)
- [templates/chat/hub.html](templates/chat/hub.html) — Roles: Authenticated. Purpose: list folders and recent AI threads. Actions: open folder or thread. Backend: ChatV2.
- [templates/chat/folder.html](templates/chat/folder.html) — Roles: Authenticated. Purpose: list threads within a folder. Actions: open thread. Backend: ChatV2.
- [templates/chat/thread.html](templates/chat/thread.html) — Roles: Authenticated. Purpose: AI chat conversation. Actions: send prompts, view structured responses. Backend: ChatV2.
- [templates/chat/chat.html](templates/chat/chat.html) — Roles: Authenticated. Purpose: legacy/alternate AI chat view. Actions: send prompts. Backend: Chat.

## 6. Calendar, Scheduling & Planning
- [templates/calendar/month.html](templates/calendar/month.html) — Roles: Authenticated. Scope: monthly. Entities: schedule occurrences and tasks. Actions: navigate days, open task detail. Ownership: trainer-applied items are read-only for clients.
- [templates/calendar/week.html](templates/calendar/week.html) — Roles: Authenticated. Scope: weekly. Entities: tasks and workouts. Actions: navigate and open detail. Ownership: same as month view.
- [templates/calendar/day.html](templates/calendar/day.html) — Roles: Authenticated. Scope: daily. Entities: tasks, workouts, and schedule entries. Actions: complete tasks and view details. Ownership: trainer-applied items remain immutable to clients.
- [templates/calendar/task-detail.html](templates/calendar/task-detail.html) — Roles: Authenticated. Scope: single task/occurrence. Entities: task details, linked workout or note. Actions: view details and completion state. Ownership: restrictions enforced by services.
- [templates/schedule/builder.html](templates/schedule/builder.html) — Roles: Trainer. Scope: schedule template creation. Entities: schedule entries and template metadata. Actions: build and save templates. Ownership: trainer-only.
- [templates/schedule/add-entry.html](templates/schedule/add-entry.html) — Roles: Authenticated. Scope: single entry creation. Entities: schedule entry form. Actions: create entry. Ownership: user-owned entries only.
- [templates/schedule/list.html](templates/schedule/list.html) — Roles: Authenticated. Scope: schedule list. Entities: schedules and templates. Actions: view and select schedules. Ownership: only owned or assigned schedules.
- [templates/schedule/select-schedule.html](templates/schedule/select-schedule.html) — Roles: Authenticated. Scope: selecting a template. Entities: schedule templates. Actions: choose template for apply. Ownership: trainer-owned templates for trainer actions.
- [templates/schedule/apply.html](templates/schedule/apply.html) — Roles: Authenticated. Scope: template apply. Entities: template and date range. Actions: apply template to schedule. Ownership: trainer-applied templates require active trainer link.
- [templates/schedule/workout.html](templates/schedule/workout.html) — Roles: Authenticated. Scope: scheduled workout detail. Entities: workout summary and schedule context. Actions: start workout or view details. Ownership: based on assigned plan.

## 7. Workouts, Health & Progress Tracking

### Workouts
- [templates/workouts/index.html](templates/workouts/index.html) — Roles: Trainer/Client. Purpose: workout template list or assigned workouts. Actions: open or edit. Backend: Workouts, TrainerLibrary.
- [templates/workouts/edit.html](templates/workouts/edit.html) — Roles: Trainer. Purpose: workout builder editor. Actions: create and update templates. Backend: Workouts.
- [templates/workouts/start.html](templates/workouts/start.html) — Roles: Client. Purpose: workout player for assigned sessions. Actions: log sets, track completion. Backend: Workouts, Goals, ScheduleData.

### Strength Logs
- [templates/strengthlog/workout-session.html](templates/strengthlog/workout-session.html) — Roles: Client. Purpose: live strength session logging. Actions: record sets and reps. Backend: StrengthLog.
- [templates/strengthlog/exercise-session.html](templates/strengthlog/exercise-session.html) — Roles: Client. Purpose: log single exercise in a session. Actions: record sets. Backend: StrengthLog.
- [templates/strengthlog/completion.html](templates/strengthlog/completion.html) — Roles: Client. Purpose: session summary. Actions: review completion. Backend: StrengthLog.

### Health Records
- [templates/health-record/health-record-list.html](templates/health-record/health-record-list.html) — Roles: Client. Purpose: list health records. Actions: open record, create new. Backend: HealthDataInput.
- [templates/health-record/health-record-form.html](templates/health-record/health-record-form.html) — Roles: Client. Purpose: health record input. Actions: submit new record. Backend: HealthDataInput.
- [templates/health-record/health-record-view.html](templates/health-record/health-record-view.html) — Roles: Client. Purpose: view record details. Actions: none beyond navigation. Backend: HealthDataInput.

### Exercise Logs
- [templates/exercise-log/exercise-log-list.html](templates/exercise-log/exercise-log-list.html) — Roles: Client. Purpose: list exercise logs. Actions: open log, create new. Backend: ExerciseLog.
- [templates/exercise-log/exercise-log-form.html](templates/exercise-log/exercise-log-form.html) — Roles: Client. Purpose: log exercise. Actions: submit log entry. Backend: ExerciseLog.
- [templates/exercise-log/exercise-log-view.html](templates/exercise-log/exercise-log-view.html) — Roles: Client. Purpose: log detail view. Actions: view details. Backend: ExerciseLog.
- [templates/exercise-log/ExerciseTutorial.html](templates/exercise-log/ExerciseTutorial.html) — Roles: Client. Purpose: tutorial content. Actions: none beyond navigation. Backend: ExerciseLog.

## 8. Goals, Check-ins & Accountability
- [templates/goals/index.html](templates/goals/index.html) — Roles: Client/Trainer (active link). Purpose: list goals and adherence summaries. Actions: filter and open goals. Backend: Goals, CalendarData.
- [templates/goals/create.html](templates/goals/create.html) — Roles: Client. Purpose: create goal. Actions: submit goal form. Backend: Goals.
- [templates/goals/edit.html](templates/goals/edit.html) — Roles: Client/Trainer (active link). Purpose: edit goal details. Actions: update goal fields. Backend: Goals.
- [templates/goals/detail.html](templates/goals/detail.html) — Roles: Client/Trainer (active link). Purpose: goal detail and linked items. Actions: view links and adherence. Backend: Goals.
- [templates/goals/checkins.html](templates/goals/checkins.html) — Roles: Client/Trainer (active link). Purpose: weekly check-in history. Actions: add check-ins where permitted. Backend: Goals, Checkins.
- [templates/checkins/client-submit.html](templates/checkins/client-submit.html) — Roles: Client. Purpose: weekly check-in submission. Actions: submit responses. Backend: Checkins, Notifications.
- [templates/checkins/trainer-review.html](templates/checkins/trainer-review.html) — Roles: Trainer. Purpose: review client check-ins and respond. Actions: submit response. Backend: Checkins, TrainerClient.

## 9. Trainer-Only Tools
- [templates/trainer/clients.html](templates/trainer/clients.html) — Preconditions: trainer role. Scope: client roster. Control: view and navigate to client detail. Visibility: active and pending links only.
- [templates/trainer/active-clients.html](templates/trainer/active-clients.html) — Preconditions: trainer role. Scope: active links. Control: manage active client relationships. Visibility: active links only.
- [templates/trainer/client-requests.html](templates/trainer/client-requests.html) — Preconditions: trainer role. Scope: requests inbox. Control: accept or reject requests. Visibility: pending requests only.
- [templates/trainer/client-detail.html](templates/trainer/client-detail.html) — Preconditions: trainer role and active link. Scope: client plan, goals, and adherence. Control: update coaching phase and review check-ins. Visibility: linked client only.
- [templates/trainer/library.html](templates/trainer/library.html) — Preconditions: trainer role. Scope: template library. Control: manage trainer templates. Visibility: trainer-owned templates.
- [templates/trainer/exercises/list.html](templates/trainer/exercises/list.html) — Preconditions: trainer role. Scope: custom exercises list. Control: navigate CRUD. Visibility: trainer-owned exercises.
- [templates/trainer/exercises/create.html](templates/trainer/exercises/create.html) — Preconditions: trainer role. Scope: create exercise. Control: submit form. Visibility: trainer-owned exercises.
- [templates/trainer/exercises/edit.html](templates/trainer/exercises/edit.html) — Preconditions: trainer role. Scope: edit exercise. Control: update fields. Visibility: trainer-owned exercises.
- [templates/trainer/exercises/view.html](templates/trainer/exercises/view.html) — Preconditions: trainer role. Scope: exercise detail. Control: view only. Visibility: trainer-owned exercises.
- [templates/trainer/workouts/list.html](templates/trainer/workouts/list.html) — Preconditions: trainer role. Scope: workout templates list. Control: navigate CRUD. Visibility: trainer-owned workouts.
- [templates/trainer/workouts/create.html](templates/trainer/workouts/create.html) — Preconditions: trainer role. Scope: create workout template. Control: submit form. Visibility: trainer-owned workouts.
- [templates/trainer/workouts/edit.html](templates/trainer/workouts/edit.html) — Preconditions: trainer role. Scope: edit workout template. Control: update template. Visibility: trainer-owned workouts.
- [templates/trainer/workouts/view.html](templates/trainer/workouts/view.html) — Preconditions: trainer role. Scope: workout template detail. Control: view only. Visibility: trainer-owned workouts.
- [templates/trainer/programmes/list.html](templates/trainer/programmes/list.html) — Preconditions: trainer role. Scope: programmes list. Control: navigate CRUD. Visibility: trainer-owned programmes.
- [templates/trainer/programmes/create.html](templates/trainer/programmes/create.html) — Preconditions: trainer role. Scope: create programme. Control: submit form. Visibility: trainer-owned programmes.
- [templates/trainer/programmes/edit.html](templates/trainer/programmes/edit.html) — Preconditions: trainer role. Scope: edit programme. Control: update programme. Visibility: trainer-owned programmes.
- [templates/trainer/programmes/view.html](templates/trainer/programmes/view.html) — Preconditions: trainer role. Scope: programme detail. Control: view only. Visibility: trainer-owned programmes.
- [templates/trainer/templates/index.html](templates/trainer/templates/index.html) — Preconditions: trainer role. Scope: schedule templates list. Control: create/apply templates. Visibility: trainer-owned templates.
- [templates/trainer/templates/edit.html](templates/trainer/templates/edit.html) — Preconditions: trainer role. Scope: template editor. Control: manage entries and questions. Visibility: trainer-owned templates.
- [templates/trainer/templates/apply.html](templates/trainer/templates/apply.html) — Preconditions: trainer role and active link for target client. Scope: apply template. Control: preview and apply. Visibility: linked clients only.
- [templates/trainer/profile/view.html](templates/trainer/profile/view.html) — Preconditions: trainer role. Scope: profile view. Control: view only. Visibility: trainer profile data.
- [templates/trainer/profile/edit.html](templates/trainer/profile/edit.html) — Preconditions: trainer role. Scope: profile edit. Control: update profile fields. Visibility: trainer profile data.
- Weekly check-in review is handled in [templates/checkins/trainer-review.html](templates/checkins/trainer-review.html) and is described in section 8.

## 10. Client-Only Pages
- [templates/client/trainers.html](templates/client/trainers.html) — Client can browse trainers. Read-only list and filters. Trainer visibility is constrained to verified trainers.
- [templates/client/my-trainer.html](templates/client/my-trainer.html) — Client can view current trainer relationship and contract state. Client cannot alter trainer verification or admin-level attributes.
- [templates/client/assigned-plan.html](templates/client/assigned-plan.html) — Client can view assigned plan. Read-only, trainer-controlled.
- [templates/client/assessment-form.html](templates/client/assessment-form.html) — Client submits assessment intake. Trainer can later review but cannot modify the original submission.
- [templates/client/plan.html](templates/client/plan.html) — Client plan overview. Read-only structure with progress context.

## 11. Gym Admin & Platform Admin Pages

### Gym Admin
- [templates/gym-admin/trainers.html](templates/gym-admin/trainers.html) — Authority: gym admin. Actions: create trainers and track verification status. Compliance: trainer verification workflow.
- [templates/gym-admin/memberships/list.html](templates/gym-admin/memberships/list.html) — Authority: gym admin. Actions: view membership products. Compliance: none beyond gym ownership.
- [templates/gym-admin/memberships/form.html](templates/gym-admin/memberships/form.html) — Authority: gym admin. Actions: create/edit membership products. Compliance: price change rules enforced by service.
- [templates/gym-admin/memberships/price-change.html](templates/gym-admin/memberships/price-change.html) — Authority: gym admin. Actions: initiate price change. Irreversible aspects: audit trail creation and notifications.
- [templates/gym-admin/memberships/price-history.html](templates/gym-admin/memberships/price-history.html) — Authority: gym admin. Actions: view price change audit history.

### Super Admin / Platform Admin
- [templates/super-admin/verification-queue.html](templates/super-admin/verification-queue.html) — Authority: super admin. Actions: approve/reject trainer verification. Compliance: verification audit log.
- [templates/super-admin/verification-detail.html](templates/super-admin/verification-detail.html) — Authority: super admin. Actions: review request details, approve, reject, or request info with admin notes. Compliance: verification audit log.
- [templates/admin/off-platform-payments.html](templates/admin/off-platform-payments.html) — Authority: platform admin/super admin. Actions: review flagged off-platform payment attempts. Compliance: messaging policy enforcement.

## 12. Gamification & Engagement
- [templates/levels/me.html](templates/levels/me.html) — Roles: Authenticated. Displays XP, level progress, and streaks. Actions: informational only.
- [templates/levels/leaderboard.html](templates/levels/leaderboard.html) — Roles: Authenticated. Displays ranked user activity. Actions: informational only.

## 13. Shared Fragments & UI Guards
- [templates/fragments/ui-shell.html](templates/fragments/ui-shell.html) — Layout shell for role-aware pages.
- [templates/fragments/navbar.html](templates/fragments/navbar.html) — Role-aware navigation links.
- [templates/fragments/footer.html](templates/fragments/footer.html) — Global footer with policy links.
- [templates/fragments/banner.html](templates/fragments/banner.html) — Conditional banners for verification and policy warnings.
- [templates/fragments/username-logout.html](templates/fragments/username-logout.html) — Authenticated user menu and logout.
- [templates/fragments/profile-modules.html](templates/fragments/profile-modules.html) — Reusable profile sections.
- [templates/fragments/chat/chat-widget.html](templates/fragments/chat/chat-widget.html) — Embedded chat widget and notifications panel.
- [templates/fragments/chat/sidebar.html](templates/fragments/chat/sidebar.html) — ChatV2 navigation sidebar.
- [templates/fragments/chat/blocks.html](templates/fragments/chat/blocks.html) — Structured AI response block renderer.
- [templates/fragments/goals/goal-chip.html](templates/fragments/goals/goal-chip.html) — Goal chip fragment used in goal and calendar contexts.
- [templates/fragments/chatbot.html](templates/fragments/chatbot.html) — Legacy chatbot widget.
- [templates/fragments/daily-streak-bar.html](templates/fragments/daily-streak-bar.html) — Streak progress bar.
- [templates/fragments/edit-task.html](templates/fragments/edit-task.html) — Task edit modal fragment.
- [templates/fragments/tailwind-components.html](templates/fragments/tailwind-components.html) — UI component library fragments.
- [templates/fragments/slimselectCss.html](templates/fragments/slimselectCss.html) — SlimSelect CSS include.
- [templates/fragments/slimselectJs.html](templates/fragments/slimselectJs.html) — SlimSelect JS include.
- [templates/fragments/AccountHomePage.html](templates/fragments/AccountHomePage.html) — Account summary fragment.
- [templates/fragments/workout/searchbar.html](templates/fragments/workout/searchbar.html) — Workout search fragment.
- [templates/fragments/workout/workout-frags.html](templates/fragments/workout/workout-frags.html) — Workout UI fragments.

## 14. Summary of UI Guarantees
- Clients only see and edit their own data, with trainer edits limited to linked relationships.
- Trainers cannot access client data without an active trainer-client link.
- Gym admins can only administer data for their own gym.
- Super admins exclusively control trainer verification approvals.
- Price changes and policy enforcement actions are auditable and reflected in admin views.
- UI always reflects server-validated state and does not bypass ownership or role checks.
