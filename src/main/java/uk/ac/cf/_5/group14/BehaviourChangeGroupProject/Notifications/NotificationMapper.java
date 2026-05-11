package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Notifications;

public final class NotificationMapper {

    private NotificationMapper() {
    }

    public static NotificationDto toDto(Notification notification) {
        return NotificationDto.from(notification);
    }
}
