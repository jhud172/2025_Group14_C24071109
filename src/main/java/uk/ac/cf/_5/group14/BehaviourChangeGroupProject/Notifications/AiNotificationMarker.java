package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Notifications;

public final class AiNotificationMarker {

    private AiNotificationMarker() {
    }

    public static String wrap(String message) {
        if (message == null) return null;
        return "[[notify:" + message + "]]";
    }
}
