# Test Coverage and Failure Audit (No Fixes Applied)

## Scope
This file documents:
1. What currently fails in tests.
2. Which files/features are likely not fully covered by tests.
3. What should be updated for schema-aligned test reliability.

## Latest Test Run Snapshot
- Source: latest captured test output artifact from this workspace session.
- Result summary: **131 passed, 95 failed**.
- Deduplicated failing test classes: **36**.

## Failing Tests (Unique Classes)
1. `uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExploreTests.ExploreDirectoryVisibilityTest`
2. `uk.ac.cf._5.group14.BehaviourChangeGroupProject.Membership.GymAdminMembershipControllerTest`
3. `uk.ac.cf._5.group14.BehaviourChangeGroupProject.Membership.MembershipProductServiceTest`
4. `uk.ac.cf._5.group14.BehaviourChangeGroupProject.Membership.GymAdminMembershipPriceChangeControllerTest`
5. `uk.ac.cf._5.group14.BehaviourChangeGroupProject.Messaging.MessagingSecurityTest`
6. `uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseData.ExerciseControllerTest`
7. `uk.ac.cf._5.group14.BehaviourChangeGroupProject.BehaviourMemoryData.BehaviourMemoryPersistenceTest`
8. `uk.ac.cf._5.group14.BehaviourChangeGroupProject.GoalsTests.GoalAccessControlTest`
9. `uk.ac.cf._5.group14.BehaviourChangeGroupProject.GoalsTests.GoalAdherenceServiceTest`
10. `uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarTests.CalendarDayViewStreakBarTooltipAccessibilityTest`
11. `uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarTests.CalendarDayTimeThemeAccentMvcTest`
12. `uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarTests.CalendarDayAiTaskAddTest`
13. `uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarTests.CalendarDayViewStreakBarTest`
14. `uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarTests.CalendarDayDailyFocusOptionsFromTodayItemsTest`
15. `uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarTests.CalendarDayReflectionGatingMvcTest`
16. `uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarTests.CalendarFragmentEndpointsTest`
17. `uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarTests.CalendarDayTimeThemeAttributeMvcTest`
18. `uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarTests.CalendarDayTimedFocusSectionTest`
19. `uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarTests.CalendarDayScheduledWorkoutsSectionTest`
20. `uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarTests.CalendarDayTimeOfDayMoodMvcTest`
21. `uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarTests.CalendarDayHubHeaderMvcTest`
22. `uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarTests.CalendarDayCompletionAutoUpdateTest`
23. `uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarTests.CalendarDayDailyFocusAutoSelectAiTest`
24. `uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarTests.CalendarDayTaskPreferencesUpdateTest`
25. `uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarTests.CalendarDayHealthMvcTest`
26. `uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarTests.CalendarDayReflectionPostMvcTest`
27. `uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarTests.CalendarDayAddTaskModalTest`
28. `uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarTests.CalendarDayViewStreakBarClickNavigationTest`
29. `uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarTests.CalendarDayTaskDetailLinkRenderTest`
30. `uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarTests.CalendarTaskDetailViewTest`
31. `uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarTests.CalendarDayDailyFocusChoosePreferencesButtonTest`
32. `uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarTests.CalendarDayTaskDrawerRenderTest`
33. `uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarTests.CalendarDayOrderingAppliedTest`
34. `uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarTests.CalendarDayTemplatesRenderTest`
35. `uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarTests.CalendarDayDailyFocusEditTest`
36. `uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarTests.CalendarDayTaskKebabMenuRenderTest`

## Primary Failure Buckets Observed
- **Data integrity cleanup failures** during setup/teardown (notably FK from `user_preferences` to `users`).
- **Application context load failures** in MVC slice tests due missing beans introduced by global model advice dependencies (e.g., `DayModeModelAdvice` requiring `AuthHelper`).
- **SQL/schema mismatch failures** (example symptoms included missing/unknown columns like `attachment_name`).
- **Unit expectation drift** in at least one service test (`MembershipProductServiceTest`) where mock interaction expectations no longer match current implementation flow.

## Test/Schema Files That Need Updating First
1. `src/test/resources/schema.sql`
   - Align structure with `src/main/resources/schema.sql` for recently added/changed columns and constraints.
   - Verify parity for messaging/chat fields (e.g., `attachment_name`) and any recent migration-era columns.
2. `src/test/resources/data.sql`
   - Ensure fixtures include all required columns and valid FK references after schema updates.
3. `src/test/resources/data/*.sql`
   - Review seed ordering and FK consistency with new constraints.
4. `src/test/resources/application-test.properties`
   - Confirm test DB init path uses the intended schema/data scripts consistently.

## Concrete Tests Likely Needing Fixture/Cleanup Changes
- `src/test/java/uk/ac/cf/_5/group14/BehaviourChangeGroupProject/ExploreTests/ExploreDirectoryVisibilityTest.java`
- `src/test/java/uk/ac/cf/_5/group14/BehaviourChangeGroupProject/Membership/GymAdminMembershipControllerTest.java`
- `src/test/java/uk/ac/cf/_5/group14/BehaviourChangeGroupProject/Membership/GymAdminMembershipPriceChangeControllerTest.java`
- `src/test/java/uk/ac/cf/_5/group14/BehaviourChangeGroupProject/Messaging/MessagingSecurityTest.java`
- `src/test/java/uk/ac/cf/_5/group14/BehaviourChangeGroupProject/GoalsTests/GoalAccessControlTest.java`
- `src/test/java/uk/ac/cf/_5/group14/BehaviourChangeGroupProject/GoalsTests/GoalAdherenceServiceTest.java`
- (And related `CalendarTests/*` listed above, many of which fail after shared context/data setup issues.)

## Files/Areas Likely Not Fully Covered by Tests (High Priority)
### Notifications + SSE
- `src/main/java/uk/ac/cf/_5/group14/BehaviourChangeGroupProject/Notifications/NotificationsController.java`
- `src/main/java/uk/ac/cf/_5/group14/BehaviourChangeGroupProject/Notifications/NotificationSseRegistry.java`
- `src/main/java/uk/ac/cf/_5/group14/BehaviourChangeGroupProject/Notifications/NotificationService.java`
- `src/main/java/uk/ac/cf/_5/group14/BehaviourChangeGroupProject/Notifications/NotificationRepository.java`

### Day Mode model advice wiring
- `src/main/java/uk/ac/cf/_5/group14/BehaviourChangeGroupProject/DayMode/DayModeModelAdvice.java`

### Quick Actions endpoint layer
- `src/main/java/uk/ac/cf/_5/group14/BehaviourChangeGroupProject/QuickActions/QuickActionApiController.java`
- `src/main/java/uk/ac/cf/_5/group14/BehaviourChangeGroupProject/QuickActions/QuickActionDefinitionRepository.java`

### Chat V2 feature set
- `src/main/java/uk/ac/cf/_5/group14/BehaviourChangeGroupProject/ChatV2/ChatV2Controller.java`
- `src/main/java/uk/ac/cf/_5/group14/BehaviourChangeGroupProject/ChatV2/ChatV2ThreadService.java`
- `src/main/java/uk/ac/cf/_5/group14/BehaviourChangeGroupProject/ChatV2/ChatThread.java`
- `src/main/java/uk/ac/cf/_5/group14/BehaviourChangeGroupProject/ChatV2/ChatMessage.java`
- `src/main/java/uk/ac/cf/_5/group14/BehaviourChangeGroupProject/ChatV2/ChatType.java`

### Payments / pricing pages
- `src/main/java/uk/ac/cf/_5/group14/BehaviourChangeGroupProject/Payments/PricingController.java`

## Suggested Update Order (Instructions)
1. **Schema parity pass**
   - Diff `src/test/resources/schema.sql` against `src/main/resources/schema.sql` and all migration scripts in `src/main/resources/migrations/`.
   - Add/adjust missing columns, constraints, indexes, and default values in test schema.
2. **Fixture parity pass**
   - Update `src/test/resources/data.sql` and `src/test/resources/data/*.sql` for new non-null and FK requirements.
   - Ensure deletes/truncates in tests respect FK order or use cascade-safe cleanup.
3. **Slice test wiring pass**
   - For `@WebMvcTest` classes, explicitly mock/import dependencies introduced by global advice/config (e.g., `AuthHelper` for `DayModeModelAdvice`).
4. **Feature coverage pass**
   - Add focused tests for the high-priority uncovered files above (controller happy path + auth + repository/service behavior).
5. **Re-run test suite and re-baseline**
   - Re-run after each pass to keep failures attributable and prevent compounding errors.

## Notes
- This document intentionally records failures/gaps only.
- No production or test fixes were applied as requested.