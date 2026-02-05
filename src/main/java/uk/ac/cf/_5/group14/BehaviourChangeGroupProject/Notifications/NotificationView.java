package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Notifications;

import java.time.Instant;

public record NotificationView(Long id, String title, String message, Instant createdAt) {}
