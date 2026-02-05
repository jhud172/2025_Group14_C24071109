# System Overview

This document describes what each major Java package (folder) does and provides a concise description of every HTML template in the system.

## Java Source Areas (src/main/java)

Package root:
- `uk.ac.cf._5.group14.BehaviourChangeGroupProject`

### Application Entry Point
- `BehaviourChangeGroupProjectApplication.java` — Spring Boot application entry point.

### Feature/Domain Packages
Below is a folder-by-folder summary of the Java areas under the package root:

- `BehaviourMemoryData/` — Behaviour memory tracking and persistence (capturing and storing behavioural responses over time).
- `CalendarData/` — Calendar and scheduling data models and services (tasks, dates, planner views).
- `Chat/` — Chat features, controllers, and messaging flow for real-time or threaded chat.
- `ConditionsPreferences/` — Conditions and preferences logic (health conditions, user constraints, and preferences).
- `CustomExerciseData/` — User-created/custom exercise entities and related operations.
- `Dashboard/` — Dashboard aggregation, summary widgets, and role-specific overview logic.
- `DayHealthData/` — Daily health data models (metrics logged per day).
- `ExerciseData/` — Core exercise catalogue and exercise metadata.
- `ExerciseLog/` — Exercise logging, viewing, and CRUD workflows.
- `Explore/` — Explore/browse features (discover content, exercises, or programs).
- `FavouriteData/` — Favorites/bookmarks for exercises or content.
- `FeedbackData/` — Feedback submission and analytics data.
- `FocusData/` — Focus area tracking and user-specific focus settings.
- `HealthDataInput/` — Health record input flows and validation.
- `HomePage/` — Home/landing page logic for authenticated and public views.
- `Inbox/` — Messaging inbox views and message list threads.
- `Level/` — Leveling and progression system (leaderboards, level data).
- `Membership/` — Gym membership products, subscriptions, and pricing workflows.
- `Messaging/` — Direct messaging, threads, message delivery, and off-platform payment enforcement (keyword detection + moderation logging).
- `Notes/` — Notes CRUD, folders, and note visibility.
- `Policies/` — Public policy pages (payments policy and related policy surfaces).
- `Profile/` — User profile display/edit features.
- `ReflectionData/` — Reflection/journaling entries and analytics.
- `Reviews/` — Trainer or exercise reviews submission/management.
- `ScheduleData/` — Scheduling data structures (plans, events, and templates).
- `Security/` — Authentication, authorization, and security configuration.
- `StrengthLog/` — Strength training logs and session data.
- `TrainerClient/` — Trainer/client relationships, requests, and approvals.
- `TrainerLibrary/` — Trainer content library (exercises, workouts, programs).
- `TrainerProfile/` — Trainer profile management and public-facing trainer pages.
- `Users/` — User management, roles, and account lifecycle.
- `UserSettings/` — User settings and preferences (theme, layout, etc.).
- `Vault/` — Secure vault functionality for private notes/data.
- `Verification/` — Trainer verification workflows and audit trails.
- `Workout/` — Workout plans, templates, and execution.

## Templates (src/main/resources/templates)

Below is every template and a short explanation of its purpose. Templates are grouped by folder for clarity.

### Root-level templates
- `base.html` — Base layout shell used by other pages (shared head, navbar, scripts, layout slots).
- `index.html` — Root landing page / index entry.
- `home-public.html` — Public (logged-out) home page.
- `home-auth.html` — Authenticated home page.
- `confirm-logout.html` — Logout confirmation page.
- `client-assessment-form.html` — Client assessment form for onboarding or evaluations.
- `client-assigned-plan.html` — Client view of assigned plan.
- `client-my-trainer.html` — Client view of assigned trainer, with payment policy banner for active relationships.
- `review-form.html` — Review submission form.
- `trainer-active-clients.html` — Trainer view of active clients list.
- `trainer-client-requests.html` — Trainer view of pending client requests.
- `trainer-library.html` — Trainer content library overview.
- `trainer-profile.html` — Trainer profile view page.
- `trainer-profile-edit.html` — Trainer profile edit form.
- `trainer-exercises-list.html` — Trainer exercise list view.
- `trainer-exercises-create.html` — Trainer create exercise form.
- `trainer-exercises-edit.html` — Trainer edit exercise form.
- `trainer-exercises-view.html` — Trainer exercise details view.
- `trainer-programmes-list.html` — Trainer programmes list view.
- `trainer-programmes-create.html` — Trainer create programme form.
- `trainer-programmes-edit.html` — Trainer edit programme form.
- `trainer-programmes-view.html` — Trainer programme details view.
- `trainer-workouts-list.html` — Trainer workouts list view.
- `trainer-workouts-create.html` — Trainer create workout form.
- `trainer-workouts-edit.html` — Trainer edit workout form.
- `trainer-workouts-view.html` — Trainer workout details view.
- `gym-admin-memberships.html` — Gym admin membership products list.
- `gym-admin-membership-form.html` — Gym admin create/edit membership product form.
- `gym-admin-price-change.html` — Gym admin price change form with audit warning.
- `gym-admin-price-history.html` — Gym admin price change history timeline.
- `gym-admin-trainers.html` — Gym admin trainer verification submissions and statuses.
- `super-admin-verification-queue.html` — Super admin verification queue and actions.

### calendar/
- `calendar/day.html` — Daily calendar view.
- `calendar/week.html` — Weekly calendar view.
- `calendar/month.html` — Monthly calendar view.
- `calendar/task-detail.html` — Task detail view from calendar.

### chat/
- `chat/chat.html` — Primary chat UI.

### conditions-preference/
- `conditions-preference/preference-form.html` — Condition preferences form.
- `conditions-preference/view-preferences.html` — View saved condition preferences.

### dashboard/
- `dashboard/client-dashboard.html` — Client dashboard overview.
- `dashboard/trainer-dashboard.html` — Trainer dashboard overview.
- `dashboard/gym-dashboard.html` — Gym admin dashboard overview.

### error/
- `error/403.html` — Access denied page.

### exercise-log/
- `exercise-log/exercise-log-list.html` — Exercise logs list view.
- `exercise-log/exercise-log-form.html` — Exercise log create/edit form.
- `exercise-log/exercise-log-view.html` — Exercise log detail view.
- `exercise-log/ExerciseTutorial.html` — Exercise tutorial view.

### explore/
- `explore/index.html` — Explore directory of verified trainers with filters, sorting, and premium cards.

### fragments/
- `fragments/AccountHomePage.html` — Account home snippet/fragment.
- `fragments/banner.html` — Banner component fragment.
- `fragments/chatbot.html` — Chatbot widget fragment.
- `fragments/daily-streak-bar.html` — Daily streak progress bar fragment.
- `fragments/edit-task.html` — Task edit modal/fragment.
- `fragments/footer.html` — Global footer fragment with policy/support links and role-aware items.
- `fragments/navbar.html` — Shared navigation bar fragment.
- `fragments/profile-modules.html` — Profile modules fragment.
- `fragments/slimselectCss.html` — SlimSelect CSS include fragment.
- `fragments/slimselectJs.html` — SlimSelect JS include fragment.
- `fragments/tailwind-components.html` — Tailwind helper components fragment.
- `fragments/username-logout.html` — Username/logout snippet fragment.
- `fragments/chat/chat-widget.html` — Inline chat widget fragment.
- `fragments/workout/searchbar.html` — Workout search bar fragment.
- `fragments/workout/workout-frags.html` — Workout UI fragments.

### health-record/
- `health-record/health-record-list.html` — Health record list view.
- `health-record/health-record-form.html` — Health record create/edit form.
- `health-record/health-record-view.html` — Health record details view.

### homepage/
- `homepage/HomePage.html` — Main homepage content template.

### inbox/
- `inbox/index.html` — Inbox list view.
- `inbox/thread.html` — Inbox thread view.

### levels/
- `levels/leaderboard.html` — Leaderboard view.
- `levels/me.html` — User level/progress page.

### messages/
- `messages/client-inbox.html` — Client inbox view.
- `messages/trainer-inbox.html` — Trainer inbox view.
- `messages/thread.html` — Messaging thread view with off-platform payment warning banner and client-side blocking on detected payment keywords.

### admin/
- `admin/off-platform-payments.html` — Platform admin list of flagged off-platform payment attempts.

### policies/
- `policies/payments.html` — Public payments policy page and examples of blocked content.

### notes/
- `notes/folders.html` — Notes folders view.
- `notes/note-form.html` — Note create/edit form.
- `notes/note-view.html` — Note detail view.

### profile/
- `profile/profile.html` — Profile view page.

### schedule/
- `schedule/add-entry.html` — Add schedule entry form.
- `schedule/apply.html` — Apply schedule template view.
- `schedule/builder.html` — Schedule builder UI.
- `schedule/list.html` — Schedule list view.
- `schedule/select-schedule.html` — Select schedule view.
- `schedule/workout.html` — Scheduled workout view.

### strengthlog/
- `strengthlog/completion.html` — Strength log completion summary.
- `strengthlog/exercise-session.html` — Strength exercise session view.
- `strengthlog/workout-session.html` — Strength workout session view.

### User/
- `User/login.html` — Login form.
- `User/signup.html` — Signup/registration form.

### vault/
- `vault/index.html` — Vault index/list view.
- `vault/note-form.html` — Vault note create/edit form.
- `vault/note-view.html` — Vault note detail view.


