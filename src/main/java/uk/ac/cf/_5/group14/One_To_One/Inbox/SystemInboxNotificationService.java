package uk.ac.cf._5.group14.One_To_One.Inbox;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.cf._5.group14.One_To_One.Notifications.NotificationService;
import uk.ac.cf._5.group14.One_To_One.Notifications.NotificationType;
import uk.ac.cf._5.group14.One_To_One.Users.User;

@Service
public class SystemInboxNotificationService {

    private final NotificationService notificationService;

    public SystemInboxNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Transactional
    public void sendNotification(User recipient, String body) {
        if (recipient == null || recipient.getId() == null || body == null || body.isBlank()) {
            return;
        }
        notificationService.create(recipient, NotificationType.SYSTEM, "Platform update", body.trim());
    }
}
