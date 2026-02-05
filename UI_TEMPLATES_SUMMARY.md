# One-to-One UI Templates Documentation

This document provides a comprehensive overview of the User Interface templates within the **One-to-One** application. The templates are organized by module and function.

## 1. Public & Authentication
These pages are accessible to visitors or handle the user onboarding and authentication flows.

### Root Pages
*   **`index.html`** / **`home/public.html`**: The landing page for unauthenticated visitors.Showcases value proposition, features, and calls to action (Login/Signup).
*   **`about.html`**: Information about the One-to-One platform and mission.
*   **`pricing.html`**: Displays subscription tiers and pricing plans.
*   **`pricing-checkout.html`**: The payment/checkout flow for selected pricing plans.

### User Authentication (`User/`, `auth/`)
*   **`User/login.html`**: Standard username/password login form.
*   **`User/signup-choice.html`**: Interstitial page asking users to select their role (Client, Trainer, Gym).
*   **`User/signup-client.html`**: Registration form specifically for Clients.
*   **`User/signup-trainer.html`**: Registration form for Trainers (includes verification details).
*   **`User/signup-gym.html`**: Registration form for Gym Administrators.
*   **`User/forgot-password.html`**: Request form to trigger password reset emails.
*   **`User/reset-password.html`**: Form to define a new password (reached via email link).
*   **`auth/confirm-logout.html`**: Confirmation screen before ending the session.

---

## 2. Dashboards (`dashboard/`)
Role-specific landing pages that serve as the main hub for logged-in users.

*   **`client-dashboard.html`**: The Client's home. Features:
    *   Daily task summary.
    *   Upcoming workout schedule.
    *   Quick access to chat/AI.
    *   Progress bars (Gamification/Levels).
*   **`trainer-dashboard.html`**: The Trainer's command center. Features:
    *   Stats overview (Total Clients, Active Plans).
    *   Pending client requests.
    *   Quick links to library management.
*   **`gym-dashboard.html`**: Dashboard for Gym managers showing membership stats and revenue.

---

## 3. Communication

### Legacy Messaging (`messages/`)
The primary system for Human-to-Human (Trainer-Client) direct messaging.
*   **`client-inbox.html`**: List of message threads for a client (usually with their Trainer).
*   **`trainer-inbox.html`**: List of all client conversations for a trainer.
*   **`thread.html`**: The actual chat interface.
    *   **Features**: Real-time message history, sending text, "Check-in" prompts (Mood/Energy), and relationship status banners (Paused/Ended).

### AI Chat V2 (`chat/`)
The advanced "Personal AI" system featuring conversational AI and folders.
*   **`hub.html`**: Main overview of AI interactions. Lists all folders and recent threads.
*   **`folder.html`**: View of a specific folder (e.g., "Nutrition", "Workouts") containing multiple threads.
*   **`thread.html`**: The AI chat interface.
    *   **Features**: Markdown rendering, AI streaming responses, Slash commands (e.g., `/schedule`), and context-aware prompts.
*   **`chat.html`**: (Legacy/Alternative) Simplistic chat view.

---

## 4. Calendar & Planning (`calendar/`)
The visual representation of time, tasks, and scheduled events.

*   **`month.html`**: High-level monthly grid view. Shows distribution of workouts and milestones.
*   **`week.html`**: Weekly agenda view. Detail focus on time-blocking.
*   **`day.html`**: Daily breakdown.
    *   **Features**: Hour-by-hour timeline, task completion toggles, and "Add Entry" triggers.
*   **`task-detail.html`**: Deep dive into a specific calendar item (e.g., specific workout details or reminder).

---

## 5. Scheduling & Workouts (`schedule/`, `workout/`)
Tools for building and assigning fitness plans.

*   **`builder.html`**: A complex drag-and-drop or form-based tool to construct routine schedules.
*   **`add-entry.html`**: Form to add ad-hoc items to the schedule.
*   **`list.html`**: List view of all scheduled items.
*   **`select-schedule.html`**: Picker interface for choosing existing templates to apply.
*   **`apply.html`**: Confirmation page for applying a schedule template to a date range.

---

## 6. Health & Tracking
Modules for logging metrics and progress.

### Health Records (`health-record/`)
*   **`health-record-list.html`**: History of logged health metrics (Weight, BMI, etc.).
*   **`health-record-form.html`**: Input form for new health data points.
*   **`health-record-view.html`**: Detail view of a specific record.

### Exercise Logs (`exercise-log/`, `strengthlog/`)
*   **`exercise-log-list.html`**: History of performed exercises.
*   **`exercise-log-form.html`**: Logger for cardio/general activity.
*   **`strengthlog/workout-session.html`**: Interface for tracking a live strength training session (sets/reps/weight).
*   **`strengthlog/exercise-session.html`**: Input for a single exercise within a session.
*   **`strengthlog/completion.html`**: Summary screen after finishing a workout.

### Notes (`notes/`, `vault/`)
*   **`notes/folders.html`**: Organization of personal notes into categories.
*   **`notes/note-form.html`**: Rich text editor for creating notes.
*   **`vault/index.html`**: A specialized secure or "forever" storage area for training wisdom/resources.

---

## 7. Trainer Tools (`trainer/`)
Dedicated area for Trainers to manage their business and library.

*   **`clients.html`**: Roster of all connected clients.
*   **`active-clients.html`**: Filtered view of currently active coaching relationships.
*   **`client-requests.html`**: Inbox for new coaching requests requiring approval/denial.
*   **`library.html`**: The Trainer's database of templates.
    *   **`exercises/`**: CRUD pages (`create`, `edit`, `list`, `view`) for custom exercises.
    *   **`workouts/`**: CRUD pages for workout templates (combinations of exercises).
    *   **`programmes/`**: CRUD pages for multi-week training programs.
*   **`profile/`**: Management of the Trainer's public profile (Bio, Specialties).

## 8. Client Domain (`client/`)
Pages where the Client interacts with Trainer features.
*   **`trainers.html`**: Directory/Explore page to find new trainers.
*   **`my-trainer.html`**: Dashboard for the current trainer relationship (Link status, contract details).
*   **`assigned-plan.html`**: Read-only view of the plan assigned by the Trainer.
*   **`assessment-form.html`**: Intake form for new clients.

---

## 9. Administration

### Gym Admin (`gym-admin/`)
*   **`trainers.html`**: Management of trainers employed by the gym.
*   **`memberships/`**:
    *   **`list.html`**: Overview of gym membership types.
    *   **`form.html`**: Create/Edit membership tiers.
    *   **`price-change.html`**: Tools to adjust pricing logic.
    *   **`price-history.html`**: Audit log of price changes.

### Platform Admin (`admin/`, `super-admin/`)
*   **`super-admin/verification-queue.html`**: Back-office tool for approving new Trainer verifications.
*   **`admin/off-platform-payments.html`**: Monitoring tool for potential policy violations.

---

## 10. Gamification & Engagement (`levels/`)
*   **`me.html`**: User's personalized level progress, XP stats, and badges.
*   **`leaderboard.html`**: Ranking of users based on activity/XP.

---

## 11. Core Fragments (`fragments/`)
Reusable UI components shared across the application to ensure consistency.
*   **`ui-shell.html`**: The master layout wrapper containing the sidebar, header, and content area.
*   **`navbar.html`**: The main navigation bar with role-based links.
*   **`footer.html`**: Standard site footer.
*   **`banner.html`**: Global alert banners (e.g., "Account not verified").
*   **`chat-widget.html`**: The floating chat bubble implementation.
*   **`tailwind-components.html`**: A library of standardized atomic components (Buttons, Cards, Inputs).
*   **`profile-modules.html`**: Reusable blocks for user profiles (e.g., "About Me" card).
