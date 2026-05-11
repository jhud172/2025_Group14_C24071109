package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Notifications;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class AiNotificationHelper {

    private static final Pattern NOTIFY_PATTERN = Pattern.compile("\\[\\[notify:(.*?)\\]\\]", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    private AiNotificationHelper() {
    }

    public static ExtractedNotification extract(String text) {
        if (text == null || text.isBlank()) {
            return new ExtractedNotification(text, null);
        }
        Matcher matcher = NOTIFY_PATTERN.matcher(text);
        if (!matcher.find()) {
            return new ExtractedNotification(text, null);
        }

        String message = matcher.group(1) != null ? matcher.group(1).trim() : null;
        String cleaned = matcher.replaceAll("").trim();
        return new ExtractedNotification(cleaned, message != null && !message.isBlank() ? message : null);
    }

    public record ExtractedNotification(String cleanedText, String notificationMessage) {}
}
