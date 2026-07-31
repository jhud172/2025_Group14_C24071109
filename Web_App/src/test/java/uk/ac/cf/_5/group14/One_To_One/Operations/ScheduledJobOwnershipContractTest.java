package uk.ac.cf._5.group14.One_To_One.Operations;

import org.junit.jupiter.api.Test;
import org.springframework.scheduling.annotation.Scheduled;
import uk.ac.cf._5.group14.One_To_One.HealthConditions.TimedConditionFollowUpScheduler;
import uk.ac.cf._5.group14.One_To_One.MerchOrders.MerchOrderServiceImpl;
import uk.ac.cf._5.group14.One_To_One.Notifications.ProteinNudgeNotificationScheduler;
import uk.ac.cf._5.group14.One_To_One.Notifications.UpcomingTaskNotificationScheduler;
import uk.ac.cf._5.group14.One_To_One.Workouts.WorkoutFormFeedbackService;

import java.lang.reflect.Method;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ScheduledJobOwnershipContractTest {

    @Test
    void everyScheduledMethodDeclaresDatabaseBackedOwnership() {
        List<Class<?>> scheduledTypes = List.of(
                WorkoutFormFeedbackService.class,
                UpcomingTaskNotificationScheduler.class,
                ProteinNudgeNotificationScheduler.class,
                TimedConditionFollowUpScheduler.class,
                MerchOrderServiceImpl.class,
                OperationalRetentionScheduler.class);

        for (Class<?> scheduledType : scheduledTypes) {
            for (Method method : scheduledType.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Scheduled.class)) {
                    assertThat(method.getAnnotation(ExclusiveScheduledJob.class))
                            .as("%s.%s must declare exclusive ownership", scheduledType.getSimpleName(), method.getName())
                            .isNotNull();
                }
            }
        }
    }
}
