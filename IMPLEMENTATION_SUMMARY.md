# One-to-One Implementation Summary

## 1. Platform Overview
The platform currently supports gym membership products with price-change auditing and notifications, trainer verification workflows, advanced goals with check-ins and adherence logic, trainer templates with coaching phases, weekly check-ins, workouts, notes, inbox messaging, and ChatV2 with a coach chat widget.
Legacy and v2 systems exist in parallel.
Build status at last update: build successful, all 24 tests passing, no compilation errors, schema updates completed.

## 2. Implementation Timeline (Ordered)

### Phase 0 — Gym Membership Products & Trainer Verification
#### Scope Delivered
- Gym admins can create and manage membership offerings with controlled price changes, audit logging, and member notifications.
- Gym admins can create/request trainer accounts that require Super Admin verification before activation.
- Trainer verification workflow supports approval, rejection, and needs-info cycles with notes and notifications.

#### Entities / Enums / DTOs
- GymMembershipProduct (includes `getPriceDollars()` helper).
- GymMemberSubscription (unique constraint on user and gym).
- PriceChangeEvent (audit trail for price changes).
- BillingPeriod enum (MONTHLY).
- SubscriptionStatus enum (ACTIVE, CANCELLED, EXPIRED).
- TrainerVerificationRequest.
- VerificationStatus enum (PENDING, APPROVED, REJECTED, NEEDS_INFO).
- users: added `trainerVerified` boolean (default false).

#### Services / Business Logic
- EmailService interface: `sendPriceChangeNotification()`, `sendTrainerVerificationUpdate()`.
- EmailServiceImpl logs email notifications to the console (ready for real integration).
- MembershipProductService: `createProduct()`, `updateProduct()`, `initiatePriceChange()`, `getPriceChangeHistory()`, `createSubscription()`, `cancelSubscription()`.
- Membership price-change rules: reason and effective date required, effective date not in the past, price must change, price applies at renewal, notifications sent to affected subscribers, audit trail recorded.
- TrainerVerificationService: `createVerificationRequest()`, `getPendingRequests()`, `getRequestsByGym()`, `approveTrainer()`, `rejectTrainer()`, `requestMoreInfo()`, `updateTrainerNotes()`, `isTrainerVerified()`.
- Verification rules: trainers start unverified, only one pending request per trainer, status transitions PENDING to APPROVED/REJECTED/NEEDS_INFO and NEEDS_INFO back to PENDING, notifications on all status changes.
- Repositories: GymMembershipProductRepository, GymMemberSubscriptionRepository, PriceChangeEventRepository, TrainerVerificationRequestRepository.
- Email integration guidance: replace EmailServiceImpl log statements with real email sends, consider Spring Mail or third-party providers, add email templates, add retry logic for failed sends.

#### Controllers & Routes
- GymAdminMembershipController:
  - GET /gym/admin/memberships
  - GET /gym/admin/memberships/create
  - POST /gym/admin/memberships/create
  - GET /gym/admin/memberships/{id}/edit
  - POST /gym/admin/memberships/{id}/edit
  - GET /gym/admin/memberships/{id}/price-change
  - POST /gym/admin/memberships/{id}/price-change
  - GET /gym/admin/memberships/{id}/price-history
- GymAdminTrainerController:
  - GET /gym/admin/trainers
  - POST /gym/admin/trainers/create
  - POST /gym/admin/trainers/{id}/update-notes
- SuperAdminVerificationController:
  - GET /super-admin/verification/queue
  - GET /super-admin/verification/{id}
  - POST /super-admin/verification/{id}/approve
  - POST /super-admin/verification/{id}/reject
  - POST /super-admin/verification/{id}/request-info

#### Templates / Fragments
- None documented for this phase (see Open UI Template Gaps).

#### Security / Ownership Rules
- Gym admin endpoints check `admin.getGymId()` to ensure admins only manage their own gym data.
- Super admin endpoints protected with `@PreAuthorize("hasRole('SUPER_ADMIN')")`.
- Trainers cannot accept clients or appear in the marketplace until verified.
- Validation on all user inputs (reason, effective date, etc.).
- Audit trail captures who made changes and when.
- Foreign key constraints prevent orphaned records.

#### Database Changes
- New tables: gym_membership_products, gym_member_subscriptions, price_change_events, trainer_verification_requests.
- Updated tables: users (added `trainer_verified BOOLEAN NOT NULL DEFAULT FALSE`).
- Constraints and integrity: unique constraint on (user_id, gym_id) for subscriptions; foreign keys for users/products/reviewers.
- Performance: indexed foreign keys for efficient queries; price change operations are transactional.
- Pagination should be added for large product/subscription/audit lists.

```sql
-- Gym Membership Products
CREATE TABLE gym_membership_products (
    id BIGSERIAL PRIMARY KEY,
    gym_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    price_cents INTEGER NOT NULL CHECK (price_cents >= 0),
    billing_period VARCHAR(20) NOT NULL DEFAULT 'MONTHLY',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Gym Member Subscriptions
CREATE TABLE gym_member_subscriptions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    gym_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    started_at TIMESTAMP NOT NULL DEFAULT NOW(),
    renews_at TIMESTAMP NOT NULL,
    cancelled_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_subscriptions_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_subscriptions_product FOREIGN KEY (product_id) REFERENCES gym_membership_products (id) ON DELETE CASCADE,
    CONSTRAINT uq_user_gym_subscription UNIQUE (user_id, gym_id)
);

-- Price Change Events
CREATE TABLE price_change_events (
    id BIGSERIAL PRIMARY KEY,
    gym_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    old_price_cents INTEGER NOT NULL,
    new_price_cents INTEGER NOT NULL,
    effective_at TIMESTAMP NOT NULL,
    reason VARCHAR(500) NOT NULL,
    changed_by_user_id BIGINT NOT NULL,
    affected_member_count INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_price_change_product FOREIGN KEY (product_id) REFERENCES gym_membership_products (id) ON DELETE CASCADE,
    CONSTRAINT fk_price_change_user FOREIGN KEY (changed_by_user_id) REFERENCES users (id) ON DELETE SET NULL
);

-- Trainer Verification Requests
CREATE TABLE trainer_verification_requests (
    id BIGSERIAL PRIMARY KEY,
    trainer_user_id BIGINT NOT NULL,
    gym_id BIGINT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    notes VARCHAR(1000),
    admin_notes VARCHAR(1000),
    submitted_at TIMESTAMP NOT NULL DEFAULT NOW(),
    reviewed_at TIMESTAMP,
    reviewed_by_user_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_verification_trainer FOREIGN KEY (trainer_user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_verification_reviewer FOREIGN KEY (reviewed_by_user_id) REFERENCES users (id) ON DELETE SET NULL
);
```

#### Tests Added
- MembershipProductServiceTest (11 tests): create product, price change validation and flow, subscription creation/duplication, cancellation.
- TrainerVerificationServiceTest (13 tests): request creation, duplicate prevention, approval/rejection/needs-info flows, status checks.
- All 24 tests passing successfully.
- Build successful, no compilation errors, schema updates completed.

#### Manual Test Checklist
1. Create a membership product and verify it appears in the gym admin list.
2. Edit the product name/description/active status and confirm changes persist.
3. Initiate a price change with a valid reason and future effective date; verify the affected member count and audit entry.
4. Attempt a price change with a past date or unchanged price and confirm validation errors appear.
5. Create a trainer via gym admin and submit for verification.
6. As super admin, approve the trainer and confirm trainerVerified is set and notifications are logged.
7. As super admin, reject a trainer and confirm the trainer remains unverified with rejection notes.
8. Request more info, submit updated notes, and confirm the request returns to PENDING.

### Phase 1 — Advanced Goals System
#### Scope Delivered
- Introduced goals domain: Goal, GoalLink, GoalCheckIn with enums for type, status, link type, and source.
- Enforced ownership and trainer visibility rules at the service layer (client-only access, trainer access only with ACTIVE link).
- Added weekly adherence engine aggregating linked tasks, schedule occurrences, and workout player sessions.
- Added goals UI (list/create/edit/detail/check-ins) plus reusable goal chip fragment.
- Integrated goal chips into calendar day tasks, goal selection into workout player sessions, and active goals summary into trainer client detail.

#### Entities / Enums / DTOs
- Goal, GoalLink, GoalCheckIn.
- Enums for goal type, goal status, link type, and source.

#### Services / Business Logic
- Weekly adherence engine aggregates linked tasks, schedule occurrences, and workout player sessions.
- Ownership and trainer visibility rules enforced in the service layer (client-only access, trainer access only with ACTIVE link).

#### Controllers & Routes
- Goals (controller name not specified in previous summary):
  - GET /goals
  - GET /goals/create
  - POST /goals/create
  - GET /goals/{id}
  - GET /goals/{id}/edit
  - POST /goals/{id}/edit
  - GET /goals/{id}/checkins
  - POST /goals/{id}/checkins
  - POST /goals/{id}/links
- Workout sessions (controller name not specified in previous summary):
  - GET /workouts/session/{sessionId}
  - POST /workouts/session/{sessionId}/goal

#### Templates / Fragments
- templates/goals/index.html
- templates/goals/create.html
- templates/goals/edit.html
- templates/goals/detail.html
- templates/goals/checkins.html
- templates/fragments/goals/goal-chip.html

#### Security / Ownership Rules
- Client-only access enforced for goal ownership.
- Trainer access only with ACTIVE trainer-client link.

#### Database Changes
- Clarification: Database changes for Phase 1 were not listed in the previous summary.

#### Tests Added
- GoalsTests/GoalAccessControlTest.
- GoalsTests/GoalAdherenceServiceTest.

#### Manual Test Checklist
1. Create a goal as a client and confirm it appears in the goals list.
2. Edit the goal and verify trainer-owned fields respect guardrails.
3. Add a goal link and verify it appears in the goal detail view.
4. Submit a weekly check-in and confirm it appears in the check-ins history.
5. View a workout session and attach a goal; confirm the link is saved.
6. Validate trainer access to a client goal only when the trainer-client link is ACTIVE.

### Phase 3 — Advanced Trainer Power Tools
#### Scope Delivered
- Trainer-scale tooling: reusable schedule templates with preview/apply, coaching phases with audit, and weekly check-ins linking goals, notes, and notifications.

#### Entities / Enums / DTOs
- TrainerScheduleTemplate.
- TrainerScheduleTemplateEntry.
- TrainerScheduleTemplateEntryType (TASK, WORKOUT, NOTE).
- TrainerCheckInQuestion.
- WeeklyCheckIn.
- WeeklyCheckInStatus (SUBMITTED, RESPONDED).
- CoachingPhase (ONBOARDING, BUILD, PEAK, RECOVERY, MAINTENANCE, CUSTOM).
- CoachingPhaseChange.
- TrainerClientLink (added coaching phase fields).
- CalendarTask, ScheduleOccurrence, VaultNote (added template tracking fields).

#### Services / Business Logic
- TrainerScheduleTemplateServiceImpl: `createTemplate()`, `updateTemplate()`, `addEntry()`, `deleteEntry()`, `cloneTemplate()`, `previewApply()`, `applyTemplate()` (idempotent option).
- WeeklyCheckInServiceImpl: `submitCheckIn()`, `respondToCheckIn()`, `addQuestion()`, `deleteQuestion()`.
- TrainerClientLinkService: `changeCoachingPhase()` (writes audit and updates link).

#### Controllers & Routes
- Trainer Templates:
  - GET /trainer/templates
  - GET /trainer/templates/create
  - POST /trainer/templates/create
  - GET /trainer/templates/{id}/edit
  - POST /trainer/templates/{id}/edit
  - POST /trainer/templates/{id}/clone
  - POST /trainer/templates/{id}/entries
  - POST /trainer/templates/{id}/entries/{entryId}/delete
  - POST /trainer/templates/{id}/questions
  - POST /trainer/templates/{id}/questions/{questionId}/delete
  - GET /trainer/templates/{id}/apply
  - POST /trainer/templates/{id}/apply
- Coaching Phase:
  - POST /trainer/clients/{id}/phase
- Weekly Check-ins:
  - GET /checkins/client-submit
  - POST /checkins/client-submit
  - GET /checkins/trainer-review/{id}
  - POST /checkins/trainer-review/{id}

#### Templates / Fragments
- templates/trainer/templates/index.html
- templates/trainer/templates/edit.html
- templates/trainer/templates/apply.html
- templates/checkins/client-submit.html
- templates/checkins/trainer-review.html

#### Security / Ownership Rules
- Trainers can only manage their own templates; enforced in TrainerScheduleTemplateServiceImpl and TrainerScheduleTemplateController.
- Templates can only be applied to ACTIVE trainer-client links; enforced via AccessGuard in TrainerScheduleTemplateServiceImpl.
- Coaching phase updates require trainer role and ACTIVE link; enforced in TrainerClientLinkService and TrainerClientsController.
- Weekly check-in submission requires an ACTIVE trainer link; trainer review requires ACTIVE link to client; enforced in WeeklyCheckInServiceImpl and AccessGuard.

#### Database Changes
- New tables: trainer_schedule_templates, trainer_schedule_template_entries, trainer_checkin_questions, weekly_check_ins, coaching_phase_changes.
- New columns: trainer_client_links.coaching_phase* (phase, label, timestamps).
- Template tracking: calendar_tasks.trainer_template_id, calendar_tasks.trainer_template_entry_id, schedule_occurrences.trainer_template_id, schedule_occurrences.trainer_template_entry_id, vault_notes.trainer_template_id, vault_notes.trainer_template_entry_id.
- Accountability alignment: added user_streaks, weekly_summaries, and quiet hours/missed columns in user_settings, calendar_tasks, and schedule_occurrences.
- Clarification: These DB items appear to belong to Phase 2/7 in the roadmap, but are listed here because they were present in the previous summary.

#### Tests Added
- TrainerTemplates/TrainerScheduleTemplateServiceTest: apply creates schedule occurrences, blocked without ACTIVE link, idempotent apply prevents duplicates.

#### Manual Test Checklist
1. As trainer, create a template and add TASK/WORKOUT/NOTE entries.
2. Add weekly check-in questions to the template.
3. Preview apply for a client and verify duplicates are flagged.
4. Apply the template with idempotent checked; re-apply and confirm no duplicates.
5. Visit trainer client detail and update coaching phase and label.
6. As client, submit a weekly check-in using the template.
7. As trainer, review the check-in and respond with next-week focus.
8. Confirm notifications appear for both submission and response.
9. Verify applied workout occurrences and tasks appear in the client calendar.

## 3. Consolidated Indexes (Canonical)

### 3.1 Templates Index (All Known Templates)
- Memberships: None documented.
- Verification: None documented.
- Goals:
  - templates/goals/index.html
  - templates/goals/create.html
  - templates/goals/edit.html
  - templates/goals/detail.html
  - templates/goals/checkins.html
- Trainer Templates:
  - templates/trainer/templates/index.html
  - templates/trainer/templates/edit.html
  - templates/trainer/templates/apply.html
  - templates/trainer/clients.html
  - templates/trainer/active-clients.html
  - templates/trainer/client-detail.html
  - templates/client/plan.html
- Check-ins:
  - templates/checkins/client-submit.html
  - templates/checkins/trainer-review.html
- Workouts:
  - templates/workouts/index.html
  - templates/workouts/edit.html
  - templates/workouts/start.html
- Notes:
  - templates/notes/index.html
- ChatV2:
  - templates/chat/thread.html
  - templates/chat/hub.html
  - templates/chat/folder.html
- Inbox:
  - templates/inbox/index.html
  - templates/inbox/thread.html
- Fragments:
  - templates/fragments/goals/goal-chip.html
  - templates/fragments/chat/sidebar.html
  - templates/fragments/chat/chat-widget.html

### 3.2 Endpoints Index (All Known Routes)
- Gym Admin:
  - GET /gym/admin/memberships
  - GET /gym/admin/memberships/create
  - POST /gym/admin/memberships/create
  - GET /gym/admin/memberships/{id}/edit
  - POST /gym/admin/memberships/{id}/edit
  - GET /gym/admin/memberships/{id}/price-change
  - POST /gym/admin/memberships/{id}/price-change
  - GET /gym/admin/memberships/{id}/price-history
  - GET /gym/admin/trainers
  - POST /gym/admin/trainers/create
  - POST /gym/admin/trainers/{id}/update-notes
- Super Admin:
  - GET /super-admin/verification/queue
  - GET /super-admin/verification/{id}
  - POST /super-admin/verification/{id}/approve
  - POST /super-admin/verification/{id}/reject
  - POST /super-admin/verification/{id}/request-info
- Trainer:
  - GET /trainer/templates
  - GET /trainer/templates/create
  - POST /trainer/templates/create
  - GET /trainer/templates/{id}/edit
  - POST /trainer/templates/{id}/edit
  - POST /trainer/templates/{id}/clone
  - POST /trainer/templates/{id}/entries
  - POST /trainer/templates/{id}/entries/{entryId}/delete
  - POST /trainer/templates/{id}/questions
  - POST /trainer/templates/{id}/questions/{questionId}/delete
  - GET /trainer/templates/{id}/apply
  - POST /trainer/templates/{id}/apply
  - POST /trainer/clients/{id}/phase
  - GET /checkins/trainer-review/{id}
  - POST /checkins/trainer-review/{id}
- Client:
  - GET /checkins/client-submit
  - POST /checkins/client-submit
- Shared:
  - GET /goals
  - GET /goals/create
  - POST /goals/create
  - GET /goals/{id}
  - GET /goals/{id}/edit
  - POST /goals/{id}/edit
  - GET /goals/{id}/checkins
  - POST /goals/{id}/checkins
  - POST /goals/{id}/links
  - GET /workouts/session/{sessionId}
  - POST /workouts/session/{sessionId}/goal

### 3.3 Tests Index (All Known Tests)
- Memberships:
  - MembershipProductServiceTest
- Trainer Verification:
  - TrainerVerificationServiceTest
- Goals:
  - GoalsTests/GoalAccessControlTest
  - GoalsTests/GoalAdherenceServiceTest
- Trainer Templates:
  - TrainerTemplates/TrainerScheduleTemplateServiceTest

## 4. Open UI Template Gaps (If Mentioned)
1. gym-admin-memberships.html - Product list with create/edit/price change buttons
2. gym-admin-membership-form.html - Create/edit product form
3. gym-admin-price-change.html - Price change confirmation with affected member count
4. gym-admin-price-history.html - Timeline view of price changes
5. gym-admin-trainers.html - Trainer list and verification request status
6. super-admin-verification-queue.html - Pending requests queue with approve/reject/request-info actions
7. super-admin-verification-detail.html - Detailed view of verification request
