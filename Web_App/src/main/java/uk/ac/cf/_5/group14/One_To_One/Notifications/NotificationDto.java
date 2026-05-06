package uk.ac.cf._5.group14.One_To_One.Notifications;

import java.time.Instant;

public record NotificationDto(
        Long id,
        NotificationType type,
        String title,
        String message,
    String ctaUrl,
        Instant createdAt,
        Instant readAt,
        Instant dismissedAt
) {
    public static NotificationDto from(Notification n) {
        if (n == null) return null;
        return new NotificationDto(
                n.getId(),
                n.getType(),
                n.getTitle(),
                n.getMessage(),
            n.getCtaUrl(),
                n.getCreatedAt(),
                n.getReadAt(),
                n.getDismissedAt()
        );
    }
}
