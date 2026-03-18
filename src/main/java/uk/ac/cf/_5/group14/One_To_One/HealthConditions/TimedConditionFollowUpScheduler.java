package uk.ac.cf._5.group14.One_To_One.HealthConditions;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.cf._5.group14.One_To_One.Inbox.SystemInboxNotificationService;
import uk.ac.cf._5.group14.One_To_One.PlatformBilling.PlatformSubscriptionService;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class TimedConditionFollowUpScheduler {

    private final UserHealthConditionService conditionService;
    private final PlatformSubscriptionService subscriptionService;
    private final SystemInboxNotificationService notificationService;
    private final Clock clock;

    public TimedConditionFollowUpScheduler(UserHealthConditionService conditionService,
                                           PlatformSubscriptionService subscriptionService,
                                           SystemInboxNotificationService notificationService,
                                           Clock clock) {
        this.conditionService = conditionService;
        this.subscriptionService = subscriptionService;
        this.notificationService = notificationService;
        this.clock = clock;
    }

    @Scheduled(cron = "0 15 2 * * *")
    @Transactional
    public void sendTimedConditionFollowUps() {
        LocalDate today = LocalDate.now(clock);
        List<UserHealthCondition> expired = conditionService.findExpiredTimedConditions(today.plusDays(1));

        for (UserHealthCondition condition : expired) {
            User user = condition.getUser();
            if (user == null || user.getId() == null) {
                continue;
            }
            if (!subscriptionService.isPremium(user.getId(), clock)) {
                continue;
            }
            if (condition.getFollowUpSentAt() != null) {
                continue;
            }

            String endDate = condition.getEndDate() != null
                    ? condition.getEndDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
                    : "today";
            String message = "Your timed condition \"" + condition.getName() + "\" ended on " + endDate
                    + ". Please update your profile to extend the duration or mark yourself as recovered.";

            notificationService.sendNotification(user, message);
            condition.setStatus(HealthConditionStatus.ENDED);
            condition.setFollowUpSentAt(java.time.Instant.now(clock));
            conditionService.save(condition);
        }
    }
}
