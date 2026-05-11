package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Notifications;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository repository;
    private final NotificationSseRegistry sseRegistry;
    private final Clock clock;

    public NotificationService(NotificationRepository repository,
                               NotificationSseRegistry sseRegistry,
                               Clock clock) {
        this.repository = repository;
        this.sseRegistry = sseRegistry;
        this.clock = clock;
    }

    @Transactional
    public Notification create(User user, NotificationType type, String title, String message) {
        if (user == null || user.getId() == null || message == null || message.isBlank() || type == null) {
            return null;
        }

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(type);
        notification.setTitle(title != null && !title.isBlank() ? title.trim() : null);
        notification.setMessage(message.trim());
        notification.setCreatedAt(Instant.now(clock));

        Notification saved = repository.save(notification);
        sseRegistry.send(user.getId(), NotificationDto.from(saved));
        return saved;
    }

    /**
     * Creates a notification only if a similar one hasn't been sent recently.
     */
    @Transactional
    public Notification createIfNotRecentlySent(User user, NotificationType type, String title, String message, long withinMinutes) {
        Instant after = Instant.now(clock).minusSeconds(withinMinutes * 60);
        if (repository.existsByUserAndTypeAndMessageAndCreatedAtAfter(user, type, message, after)) {
            return null;
        }
        return create(user, type, title, message);
    }

    @Transactional(readOnly = true)
    public List<Notification> list(User user, int limit) {
        int capped = Math.min(Math.max(limit, 1), 50);
        return repository.findByUserOrderByCreatedAtDesc(user, PageRequest.of(0, capped));
    }

    @Transactional(readOnly = true)
    public long unreadCount(User user) {
        if (user == null) return 0;
        return repository.countByUserAndReadAtIsNullAndDismissedAtIsNull(user);
    }

    @Transactional
    public boolean markRead(User user, Long id) {
        if (user == null || id == null) return false;
        Notification notification = repository.findByIdAndUser(id, user).orElse(null);
        if (notification == null) return false;
        if (notification.getReadAt() != null) return true;
        notification.setReadAt(Instant.now(clock));
        repository.save(notification);
        return true;
    }

    @Transactional
    public boolean dismiss(User user, Long id) {
        if (user == null || id == null) return false;
        Notification notification = repository.findByIdAndUser(id, user).orElse(null);
        if (notification == null) return false;
        if (notification.getDismissedAt() != null) return true;
        notification.setDismissedAt(Instant.now(clock));
        repository.save(notification);
        return true;
    }

    @Transactional
    public int markAllRead(User user) {
        if (user == null) return 0;
        return repository.markAllRead(user, Instant.now(clock));
    }

    @Transactional(readOnly = true)
    public boolean existsRecent(User user, NotificationType type, String message, Instant after) {
        if (user == null || type == null || message == null || after == null) return false;
        return repository.existsByUserAndTypeAndMessageAndCreatedAtAfter(user, type, message, after);
    }
}
