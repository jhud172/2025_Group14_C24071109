package uk.ac.cf._5.group14.One_To_One.Notifications;

import org.springframework.stereotype.Service;
import uk.ac.cf._5.group14.One_To_One.Users.User;

@Service
public class AiNotificationService {

    private final NotificationService notificationService;

    public AiNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public void notify(User user, String message) {
        if (user == null || message == null || message.isBlank()) return;
        notificationService.create(user, NotificationType.AI, "Coach Bot", message);
    }
}
