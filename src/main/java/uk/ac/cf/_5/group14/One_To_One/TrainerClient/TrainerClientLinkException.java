package uk.ac.cf._5.group14.One_To_One.TrainerClient;

public class TrainerClientLinkException extends RuntimeException {

    public enum Reason {
        CLIENT_ALREADY_HAS_ACTIVE_TRAINER,
        TRAINER_NOT_VERIFIED
    }

    private final Reason reason;

    public TrainerClientLinkException(Reason reason, String message) {
        super(message);
        this.reason = reason;
    }

    public Reason getReason() {
        return reason;
    }
}
