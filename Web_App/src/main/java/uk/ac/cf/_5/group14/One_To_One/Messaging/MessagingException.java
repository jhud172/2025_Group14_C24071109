package uk.ac.cf._5.group14.One_To_One.Messaging;

public class MessagingException extends RuntimeException {

    public enum Reason {
        THREAD_LOCKED,
        THREAD_NOT_ACTIVE,
        OFF_PLATFORM_PAYMENT
    }

    private final Reason reason;

    public MessagingException(Reason reason) {
        super(reason.name());
        this.reason = reason;
    }

    public Reason getReason() {
        return reason;
    }
}
