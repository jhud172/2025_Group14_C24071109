package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Notifications;

import org.springframework.stereotype.Component;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

@Component
public class NotificationAccessGuard {
    public boolean canAccess(User current, User target) {
        if (current == null || target == null || current.getId() == null || target.getId() == null) {
            return false;
        }
        return current.getId().equals(target.getId());
    }
}
