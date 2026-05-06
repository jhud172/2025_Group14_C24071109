package uk.ac.cf._5.group14.One_To_One.Reviews;

public class TrainerReviewException extends RuntimeException {

    public enum Reason {
        REVIEW_ALREADY_EXISTS,
        LINK_NOT_ELIGIBLE,
        USER_NOT_CLIENT
    }

    private final Reason reason;

    public TrainerReviewException(Reason reason, String message) {
        super(message);
        this.reason = reason;
    }

    public Reason getReason() {
        return reason;
    }
}
