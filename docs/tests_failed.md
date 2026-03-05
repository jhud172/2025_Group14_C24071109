 *  Executing task: gradle: build 


> Task :compileTestJava

> Task :test

BehaviourChangeGroupProjectApplicationTests > contextLoads() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:180
        Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException at ConstructorResolver.java:804
            Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException at ConstructorResolver.java:804
                Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException at DefaultListableBeanFactory.java:2314


> Task :test

BehaviourMemoryPersistenceTest > shouldNotPersistDetachedUserWhenCreatingMemory() FAILED
    org.springframework.dao.InvalidDataAccessResourceUsageException at BehaviourMemoryPersistenceTest.java:42
        Caused by: org.hibernate.exception.SQLGrammarException at BehaviourMemoryPersistenceTest.java:42
            Caused by: org.h2.jdbc.JdbcSQLSyntaxErrorException at BehaviourMemoryPersistenceTest.java:42

CalendarDayAddTaskModalTest > dayViewRendersAddTaskButtonAndModal() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:180
        Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException at AutowiredAnnotationBeanPostProcessor.java:788
            Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException at DefaultListableBeanFactory.java:2314

CalendarDayAiTaskAddTest > postAddTaskAiCallsGeneratorAndCreatesTask() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:180
        Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException at AutowiredAnnotationBeanPostProcessor.java:788
            Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException at DefaultListableBeanFactory.java:2314

CalendarDayCompletionAutoUpdateTest > streakBarSummaryShouldUpdateAfterTaskToggle() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:180
        Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException at AutowiredAnnotationBeanPostProcessor.java:788
            Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException at DefaultListableBeanFactory.java:2314

CalendarDayDailyFocusAutoSelectAiTest > dayViewAutoSelectsDailyFocusWhenMissing() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:180
        Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException at AutowiredAnnotationBeanPostProcessor.java:788
            Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException at DefaultListableBeanFactory.java:2314

CalendarDayDailyFocusChoosePreferencesButtonTest > dayViewShowsChoosePreferencesButtonWhenNoPreferencesSelected() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:180
        Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException at AutowiredAnnotationBeanPostProcessor.java:788
            Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException at DefaultListableBeanFactory.java:2314

CalendarDayDailyFocusEditTest > dayViewRendersDailyFocusSection() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:180
        Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException at AutowiredAnnotationBeanPostProcessor.java:788
            Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException at DefaultListableBeanFactory.java:2314

CalendarDayDailyFocusEditTest > postDailyFocusRedirectsBackToDayWithQueryParam() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

CalendarDayDailyFocusOptionsFromTodayItemsTest > dayViewDailyFocusOptionsIncludeTasksAndScheduledWorkoutsAndCustom() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:180
        Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException at AutowiredAnnotationBeanPostProcessor.java:788
            Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException at DefaultListableBeanFactory.java:2314

CalendarDayHealthMvcTest > shouldShowGenerateButtonWhenNoAdvice() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:180
        Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException at AutowiredAnnotationBeanPostProcessor.java:788
            Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException at DefaultListableBeanFactory.java:2314

CalendarDayHealthMvcTest > postGenerateDayHealthRedirectsBackToDay() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

CalendarDayHealthMvcTest > shouldRenderDayHealthWhenProvided() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

CalendarDayHealthMvcTest > shouldRemoveGenerateButtonAfterGeneration() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

CalendarDayHubHeaderMvcTest > dayViewRendersHubHeaderAndTitleContainsDate() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:180
        Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException at AutowiredAnnotationBeanPostProcessor.java:788
            Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException at DefaultListableBeanFactory.java:2314

CalendarDayHubHeaderMvcTest > dayViewRendersTodayBadgeWhenDateIsToday() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

CalendarDayOrderingAppliedTest > dayViewAppliesAlphabeticalOrderingOnLoad() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:180
        Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException at AutowiredAnnotationBeanPostProcessor.java:788
            Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException at DefaultListableBeanFactory.java:2314

CalendarDayReflectionGatingMvcTest > shouldShowReflectionOnlyWhenGreen() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:180
        Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException at AutowiredAnnotationBeanPostProcessor.java:788
            Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException at DefaultListableBeanFactory.java:2314

CalendarDayReflectionGatingMvcTest > shouldHideReflectionWhenNotGreen() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

CalendarDayReflectionPostMvcTest > shouldStoreAiResultInFlashAttributes() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:180
        Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException at AutowiredAnnotationBeanPostProcessor.java:788
            Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException at DefaultListableBeanFactory.java:2314

CalendarDayScheduledWorkoutsSectionTest > dayViewRendersScheduledWorkoutsWithCompletionLink() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:180
        Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException at AutowiredAnnotationBeanPostProcessor.java:788
            Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException at DefaultListableBeanFactory.java:2314

CalendarDayTaskDetailLinkRenderTest > dayViewRendersTaskDetailLink() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:180
        Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException at AutowiredAnnotationBeanPostProcessor.java:788
            Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException at DefaultListableBeanFactory.java:2314

CalendarDayTaskDrawerRenderTest > dayViewRendersTaskDrawerAndHiddenContent() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:180
        Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException at AutowiredAnnotationBeanPostProcessor.java:788
            Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException at DefaultListableBeanFactory.java:2314

CalendarDayTaskKebabMenuRenderTest > dayViewRendersTaskKebabMenu() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:180
        Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException at AutowiredAnnotationBeanPostProcessor.java:788
            Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException at DefaultListableBeanFactory.java:2314

CalendarDayTaskPreferencesUpdateTest > postUpdatesPreferencesAndRedirects() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:180
        Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException at AutowiredAnnotationBeanPostProcessor.java:788
            Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException at DefaultListableBeanFactory.java:2314

CalendarDayTemplatesRenderTest > dayViewRendersTemplateSections() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:180
        Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException at AutowiredAnnotationBeanPostProcessor.java:788
            Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException at DefaultListableBeanFactory.java:2314

CalendarDayTimeOfDayMoodMvcTest > dayViewAddsMorningMoodClasses() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:180
        Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException at AutowiredAnnotationBeanPostProcessor.java:788
            Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException at DefaultListableBeanFactory.java:2314

CalendarDayTimeOfDayMoodMvcTest > dayViewAddsNightMoodClasses() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

CalendarDayTimeOfDayMoodMvcTest > dayViewAddsMiddayMoodClasses() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

CalendarDayTimeOfDayMoodMvcTest > dayViewAddsEveningMoodClasses() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

CalendarDayTimeThemeAccentMvcTest > middayThemeShouldRenderBlueAccent() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:180
        Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException at AutowiredAnnotationBeanPostProcessor.java:788
            Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException at DefaultListableBeanFactory.java:2314

CalendarDayTimeThemeAccentMvcTest > nightThemeShouldRenderEmeraldAccent() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

CalendarDayTimeThemeAccentMvcTest > eveningThemeShouldRenderIndigoAccent() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

CalendarDayTimeThemeAccentMvcTest > morningThemeShouldRenderAmberAccent() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

CalendarDayTimeThemeAttributeMvcTest > dayViewShouldRenderDataTimeThemeAttribute() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:180
        Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException at AutowiredAnnotationBeanPostProcessor.java:788
            Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException at DefaultListableBeanFactory.java:2314

CalendarDayTimedFocusSectionTest > dayViewRendersTimedFocusSection() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:180
        Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException at AutowiredAnnotationBeanPostProcessor.java:788
            Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException at DefaultListableBeanFactory.java:2314

CalendarDayViewStreakBarClickNavigationTest > clickingAStreakDayShouldLinkToThatDayView() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:180
        Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException at AutowiredAnnotationBeanPostProcessor.java:788
            Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException at DefaultListableBeanFactory.java:2314

CalendarDayViewStreakBarTest > dayViewShouldRenderDailyStreakBar() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:180
        Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException at AutowiredAnnotationBeanPostProcessor.java:788
            Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException at DefaultListableBeanFactory.java:2314

CalendarDayViewStreakBarTooltipAccessibilityTest > streakPillTooltipShouldBeKeyboardAccessible() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:180
        Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException at AutowiredAnnotationBeanPostProcessor.java:788
            Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException at DefaultListableBeanFactory.java:2314

CalendarFragmentEndpointsTest > weekFragmentRendersWeekPane() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:180
        Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException at AutowiredAnnotationBeanPostProcessor.java:788
            Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException at DefaultListableBeanFactory.java:2314

CalendarMonthWeekViewDataDisplayTest > monthViewFebruary2026GridStructure() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:180
        Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException at AutowiredAnnotationBeanPostProcessor.java:788
            Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException at DefaultListableBeanFactory.java:2314

CalendarMonthWeekViewDataDisplayTest > monthViewRendersCorrectDataTypeAttributes() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

CalendarMonthWeekViewDataDisplayTest > weekViewDisplaysTasksAndSchedulesCorrectly() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

CalendarMonthWeekViewDataDisplayTest > monthViewGeneratesCorrectGridSizeWithPlaceholders() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

CalendarMonthWeekViewDataDisplayTest > weekViewRendersCorrectDataTypeAttributes() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

CalendarMonthWeekViewDataDisplayTest > monthViewDisplaysTasksAndSchedulesCorrectly() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

CalendarMonthWeekViewDataDisplayTest > monthViewHandlesEmptyDataCorrectly() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

CalendarMonthWeekViewDataDisplayTest > monthViewPlaceholderCellsDoNotEmitDataAttributes() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

CalendarMonthWeekViewDataDisplayTest > monthViewRendersCorrectDataDateAttributes() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

CalendarNavigationAndJumpControlsTest > weekViewReturns200WithRequiredModelAttributes() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:180
        Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException at AutowiredAnnotationBeanPostProcessor.java:788
            Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException at DefaultListableBeanFactory.java:2314

CalendarNavigationAndJumpControlsTest > dayViewRendersAllThreeViewToggleLinks() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

CalendarNavigationAndJumpControlsTest > monthViewRendersDayViewLink() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

CalendarNavigationAndJumpControlsTest > dayViewReturns200WithRequiredModelAttributes() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

CalendarNavigationAndJumpControlsTest > weekViewRendersDayViewLink() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

CalendarNavigationAndJumpControlsTest > monthViewReturns200WithRequiredModelAttributes() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

CalendarNavigationAndJumpControlsTest > dayViewRendersCanonicalShortcutsButtonOnce() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

CalendarNavigationAndJumpControlsTest > monthViewRendersJumpControlsWithCorrectIds() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

CalendarTaskDetailViewTest > taskDetailPageRendersForOwnedTask() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:180
        Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException at AutowiredAnnotationBeanPostProcessor.java:788
            Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException at DefaultListableBeanFactory.java:2314

DailyCompletionRepositoryTest > shouldSaveAndLoadByUserAndDate() FAILED
    org.hibernate.exception.SQLGrammarException at DailyCompletionRepositoryTest.java:31
        Caused by: org.h2.jdbc.JdbcSQLSyntaxErrorException at DailyCompletionRepositoryTest.java:31

StickerCalendarMonthMvcTest > monthViewEmbedsStickerSessionDataForCompletedWorkouts() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:180
        Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException at AutowiredAnnotationBeanPostProcessor.java:788
            Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException at DefaultListableBeanFactory.java:2314

StickerCalendarMonthMvcTest > monthViewHtmlContainsStickerViewPanel() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

StickerCalendarMonthMvcTest > monthViewModelContainsStickerAttributes() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

StickerCalendarMonthMvcTest > monthViewHtmlContainsModeToggle() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

TaskTemplateRepositoryTest > savesAndLoadsTemplate() FAILED
    org.springframework.dao.InvalidDataAccessResourceUsageException at TaskTemplateRepositoryTest.java:27
        Caused by: org.hibernate.exception.SQLGrammarException at TaskTemplateRepositoryTest.java:27
            Caused by: org.h2.jdbc.JdbcSQLSyntaxErrorException at TaskTemplateRepositoryTest.java:27

TaskTemplateServiceTest > upsertFromTaskCreatesThenUpdatesLastUsedAt() FAILED
    org.springframework.dao.InvalidDataAccessResourceUsageException at TaskTemplateServiceTest.java:29
        Caused by: org.hibernate.exception.SQLGrammarException at TaskTemplateServiceTest.java:29
            Caused by: org.h2.jdbc.JdbcSQLSyntaxErrorException at TaskTemplateServiceTest.java:29

DayHealthRepositoryTest > shouldSaveAndLoadByUserAndDate() FAILED
    org.hibernate.exception.SQLGrammarException at DayHealthRepositoryTest.java:30
        Caused by: org.h2.jdbc.JdbcSQLSyntaxErrorException at DayHealthRepositoryTest.java:30

AdaptiveFeedbackRepositoryTest > shouldReturnMostRecentFirst() FAILED
    org.hibernate.exception.SQLGrammarException at AdaptiveFeedbackRepositoryTest.java:54
        Caused by: org.h2.jdbc.JdbcSQLSyntaxErrorException at AdaptiveFeedbackRepositoryTest.java:54

AdaptiveFeedbackRepositoryTest > shouldSaveAndLoadByUserAndDate() FAILED
    org.hibernate.exception.SQLGrammarException at AdaptiveFeedbackRepositoryTest.java:31
        Caused by: org.h2.jdbc.JdbcSQLSyntaxErrorException at AdaptiveFeedbackRepositoryTest.java:31

DailyFocusRepositoryTest > shouldSaveAndLoadByUserAndDate() FAILED
    org.hibernate.exception.SQLGrammarException at DailyFocusRepositoryTest.java:30
        Caused by: org.h2.jdbc.JdbcSQLSyntaxErrorException at DailyFocusRepositoryTest.java:30

GoalAccessControlTest > trainerAccessRequiresActiveLink() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:180
        Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException at ConstructorResolver.java:804
            Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException at ConstructorResolver.java:804
                Caused by: org.springframework.beans.factory.BeanCreationException at AbstractAutowireCapableBeanFactory.java:1826
                    Caused by: org.springframework.jdbc.datasource.init.ScriptStatementFailedException at ScriptUtils.java:293
                        Caused by: org.postgresql.util.PSQLException at QueryExecutorImpl.java:2736

GoalAccessControlTest > goalLinkRejectsOtherUsersItems() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

GoalAccessControlTest > clientCannotAccessOtherClientsGoal() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

GoalAdherenceServiceTest > adherenceCountsLinkedItemsInWeek() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

BloodPressureMvcTest > hubPopulatesModelAttributes() FAILED
    jakarta.servlet.ServletException at BloodPressureMvcTest.java:102
        Caused by: org.thymeleaf.exceptions.TemplateProcessingException at BloodPressureMvcTest.java:102
            Caused by: org.springframework.expression.spel.SpelEvaluationException at BloodPressureMvcTest.java:102
                Caused by: org.springframework.expression.AccessException at BloodPressureMvcTest.java:102
                    Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException at BloodPressureMvcTest.java:102

BloodPressureMvcTest > hubReturns200WhenAuthenticated() FAILED
    jakarta.servlet.ServletException at BloodPressureMvcTest.java:86
        Caused by: org.thymeleaf.exceptions.TemplateProcessingException at BloodPressureMvcTest.java:86
            Caused by: org.springframework.expression.spel.SpelEvaluationException at BloodPressureMvcTest.java:86
                Caused by: org.springframework.expression.AccessException at BloodPressureMvcTest.java:86
                    Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException at BloodPressureMvcTest.java:86

BloodPressureRepositoryTest > shouldFindTop14ByUserOrderedByDateDesc() FAILED
    org.hibernate.exception.SQLGrammarException at BloodPressureRepositoryTest.java:35
        Caused by: org.h2.jdbc.JdbcSQLSyntaxErrorException at BloodPressureRepositoryTest.java:35

BloodPressureRepositoryTest > shouldSaveAndFindReading() FAILED
    org.hibernate.exception.SQLGrammarException at BloodPressureRepositoryTest.java:35
        Caused by: org.h2.jdbc.JdbcSQLSyntaxErrorException at BloodPressureRepositoryTest.java:35

BloodPressureRepositoryTest > shouldFindForDateRange() FAILED
    org.hibernate.exception.SQLGrammarException at BloodPressureRepositoryTest.java:35
        Caused by: org.h2.jdbc.JdbcSQLSyntaxErrorException at BloodPressureRepositoryTest.java:35

BloodPressureRepositoryTest > shouldFindByUserAndNullReadingTime() FAILED
    org.hibernate.exception.SQLGrammarException at BloodPressureRepositoryTest.java:35
        Caused by: org.h2.jdbc.JdbcSQLSyntaxErrorException at BloodPressureRepositoryTest.java:35

GymAdminMembershipControllerTest > createRequiresNameAndPrice() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:180
        Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException at ConstructorResolver.java:804
            Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException at ConstructorResolver.java:804
                Caused by: org.springframework.beans.factory.BeanCreationException at AbstractAutowireCapableBeanFactory.java:1826
                    Caused by: org.springframework.jdbc.datasource.init.ScriptStatementFailedException at ScriptUtils.java:293
                        Caused by: org.postgresql.util.PSQLException at QueryExecutorImpl.java:2736

GymAdminMembershipControllerTest > listShowsOnlyAdminGymProductsWithPaginationAndCounts() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

GymAdminMembershipControllerTest > gymAdminCanToggleProductStatus() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

GymAdminMembershipControllerTest > priceHistoryDeniesOtherGymProduct() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

GymAdminMembershipControllerTest > editRequiresName() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

DailyNutritionLogRepositoryTest > shouldSaveAndFindByUserAndDate() FAILED
    org.hibernate.exception.SQLGrammarException at DailyNutritionLogRepositoryTest.java:29
        Caused by: org.h2.jdbc.JdbcSQLSyntaxErrorException at DailyNutritionLogRepositoryTest.java:29

ProfileRouteAccessTest > profileRouteReturns200WhenSessionUserPresent() FAILED
    jakarta.servlet.ServletException at ProfileRouteAccessTest.java:100
        Caused by: org.thymeleaf.exceptions.TemplateProcessingException at ProfileRouteAccessTest.java:100
            Caused by: org.springframework.expression.spel.SpelEvaluationException at ProfileRouteAccessTest.java:100
                Caused by: org.springframework.expression.AccessException at ProfileRouteAccessTest.java:100
                    Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException at ProfileRouteAccessTest.java:100

TrainerReviewServiceTest > testOnlyVisibleReviewsCountInAverage() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

TrainerReviewServiceTest > testClientWithActiveLinkCanReview() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

TrainerReviewServiceTest > testReviewStatusDefaultsToVisible() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

TrainerReviewServiceTest > testReviewMustHaveValidStarRating() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

TrainerReviewServiceTest > testClientCanOnlyReviewOncePerLink() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

TrainerReviewServiceTest > testClientWithPausedLinkCannotReview() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

TrainerReviewServiceTest > testClientWithoutLinkCannotReview() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

TrainerReviewServiceTest > testTrainerCannotDeleteOwnReviews() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

TrainerReviewServiceTest > testClientWithEndedLinkCanReview() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

TrainerReviewServiceTest > testGetAverageRating() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

TrainerReviewServiceTest > testClientWithRequestedLinkCannotReview() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

TrainerReviewServiceTest > testOnlyAdminsCanHideReviews() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

DevModeSecurityTest > devMode_unauthenticatedCannotAccessTrainersDirectory() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:180
        Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException at ConstructorResolver.java:804
            Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException at ConstructorResolver.java:804
                Caused by: org.springframework.beans.factory.BeanCreationException at AbstractAutowireCapableBeanFactory.java:1826
                    Caused by: org.springframework.jdbc.datasource.init.ScriptStatementFailedException at ScriptUtils.java:293
                        Caused by: org.postgresql.util.PSQLException at QueryExecutorImpl.java:2736

DevModeSecurityTest > devMode_unauthenticatedCannotAccessTrainingVault() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

DevModeSecurityTest > devMode_unauthenticatedCannotAccessTrainerArea() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

DevModeSecurityTest > devMode_unauthenticatedCannotAccessLeaderboard() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

DevModeSecurityTest > devMode_unauthenticatedCanAccessDashboard() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

LoginIntegrationTest > demoUserCanLoginWithSeededCredentials() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

RoleDashboardAccessTest > userRoleCannotAccessTrainerDashboard() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

ClientTrainerDirectoryVisibilityTest > clientDirectoryOnlyShowsVerifiedAndEnabledTrainers() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

TrainerClientLinkServiceTest > acceptRequestBlockedForUnverifiedTrainer() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

TrainerClientLinkServiceTest > acceptRequestRequiresRequestedStatus() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

TrainerClientLinkServiceTest > acceptRequestEndsOtherActiveLinks() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

TrainerLibrarySecurityTest > clientAssignedPlanOnlyShowsTemplatesFromActiveTrainer() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

TrainerLibrarySecurityTest > unverifiedTrainerCannotCreateExercise() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

TrainerLibrarySecurityTest > shareRequiresActiveTrainerClientLink() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

TrainerLibrarySecurityTest > shareRequiresCsrf() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

TrainerLibrarySecurityTest > trainerCannotViewAnotherTrainersExercise() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

TrainerScheduleTemplateServiceTest > idempotentApplyPreventsDuplicates() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

TrainerScheduleTemplateServiceTest > createTemplateBlockedForUnverifiedTrainer() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

TrainerScheduleTemplateServiceTest > applyTemplateBlockedWithoutActiveLink() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

TrainerScheduleTemplateServiceTest > applyTemplateCreatesScheduleOccurrencesForClient() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

UserSettingsCalendarLayoutPreferencePersistenceTest > updateCalendarPreferencesPersistsLayout() FAILED
    org.springframework.dao.InvalidDataAccessResourceUsageException at UserSettingsCalendarLayoutPreferencePersistenceTest.java:35
        Caused by: org.hibernate.exception.SQLGrammarException at UserSettingsCalendarLayoutPreferencePersistenceTest.java:35
            Caused by: org.h2.jdbc.JdbcSQLSyntaxErrorException at UserSettingsCalendarLayoutPreferencePersistenceTest.java:35

UserSettingsCalendarOrderingPreferencePersistenceTest > updateCalendarPreferencesPersistsOrdering() FAILED
    org.springframework.dao.InvalidDataAccessResourceUsageException at UserSettingsCalendarOrderingPreferencePersistenceTest.java:34
        Caused by: org.hibernate.exception.SQLGrammarException at UserSettingsCalendarOrderingPreferencePersistenceTest.java:34
            Caused by: org.h2.jdbc.JdbcSQLSyntaxErrorException at UserSettingsCalendarOrderingPreferencePersistenceTest.java:34

UserSettingsSmartDefaultsPersistenceTest > updateSmartDefaultsPersistsValues() FAILED
    org.springframework.dao.InvalidDataAccessResourceUsageException at UserSettingsSmartDefaultsPersistenceTest.java:33
        Caused by: org.hibernate.exception.SQLGrammarException at UserSettingsSmartDefaultsPersistenceTest.java:33
            Caused by: org.h2.jdbc.JdbcSQLSyntaxErrorException at UserSettingsSmartDefaultsPersistenceTest.java:33

UserSettingsTrainerSharingPersistenceTest > updateTrainerSharingPersistsPreferences() FAILED
    org.springframework.dao.InvalidDataAccessResourceUsageException at UserSettingsTrainerSharingPersistenceTest.java:33
        Caused by: org.hibernate.exception.SQLGrammarException at UserSettingsTrainerSharingPersistenceTest.java:33
            Caused by: org.h2.jdbc.JdbcSQLSyntaxErrorException at UserSettingsTrainerSharingPersistenceTest.java:33

VaultSecurityTest > cannotViewAnotherUsersVaultNote() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

VaultSecurityTest > ownerCanViewAndInvokeAi() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

VaultSecurityTest > cannotInvokeAiOnAnotherUsersNote() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

GymAdminTrainerControllerTest > updateNotesDeniesOtherGymRequest() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

GymAdminTrainerControllerTest > updateNotesTransitionsNeedsInfoToPending() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:145

2026-03-03T13:51:11.146Z  INFO 30340 --- [ionShutdownHook] j.LocalContainerEntityManagerFactoryBean : Closing JPA EntityManagerFactory for persistence unit 'default'
2026-03-03T13:51:11.147Z  INFO 30340 --- [ionShutdownHook] j.LocalContainerEntityManagerFactoryBean : Closing JPA EntityManagerFactory for persistence unit 'default'
2026-03-03T13:51:11.149Z  INFO 30340 --- [ionShutdownHook] j.LocalContainerEntityManagerFactoryBean : Closing JPA EntityManagerFactory for persistence unit 'default'
2026-03-03T13:51:11.149Z DEBUG 30340 --- [ionShutdownHook] o.s.w.c.s.GenericWebApplicationContext   : Closing org.springframework.web.context.support.GenericWebApplicationContext@2afaa43f, started on Tue Mar 03 13:49:35 GMT 2026
2026-03-03T13:51:11.150Z DEBUG 30340 --- [ionShutdownHook] o.s.w.c.s.GenericWebApplicationContext   : Closing org.springframework.web.context.support.GenericWebApplicationContext@766b14f3, started on Tue Mar 03 13:50:22 GMT 2026
2026-03-03T13:51:11.151Z DEBUG 30340 --- [ionShutdownHook] o.s.w.c.s.GenericWebApplicationContext   : Closing org.springframework.web.context.support.GenericWebApplicationContext@6915dbff, started on Tue Mar 03 13:50:23 GMT 2026
2026-03-03T13:51:11.151Z  INFO 30340 --- [ionShutdownHook] j.LocalContainerEntityManagerFactoryBean : Closing JPA EntityManagerFactory for persistence unit 'default'
<==========---> 76% EXECUTING [3m 22s]
> :test
> IDLE
Note: G:\No OneDrive Work\My Website\Crystal-Productions-OneToOne\One To One\2025_Group14_C24071109\src\test\java\uk\ac\cf\_5\group14\BehaviourChangeGroupProject\CalendarTests\CalendarDayModelBuilderTest.java uses or overrides a deprecated API.
Note: Recompile with -Xlint:deprecation for details.
OpenJDK 64-Bit Server VM warning: Sharing is only supported for boot loader classes because bootstrap classpath has been appended

366 tests completed, 127 failed

FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':test'.
> There were failing tests. See the report at: file:///G:/No%20OneDrive%20Work/My%20Website/Crystal-Productions-OneToOne/One%20To%20One/2025_Group14_C24071109/build/reports/tests/test/index.html

* Try:

> Task :test FAILED

[Incubating] Problems report is available at: file:///G:/No%20OneDrive%20Work/My%20Website/Crystal-Productions-OneToOne/One%20To%20One/2025_Group14_C24071109/build/reports/problems/problems-report.html
8 actionable tasks: 8 executed
Could not execute build using connection to Gradle distribution 'https://services.gradle.org/distributions/gradle-8.14.3-bin.zip'.
 *  The terminal process terminated with exit code: 1. 
