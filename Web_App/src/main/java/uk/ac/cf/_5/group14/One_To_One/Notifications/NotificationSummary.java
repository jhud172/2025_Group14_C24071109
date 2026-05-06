package uk.ac.cf._5.group14.One_To_One.Notifications;

import java.time.Instant;

public record NotificationSummary(Long id, String message, Instant createdAt) {}
