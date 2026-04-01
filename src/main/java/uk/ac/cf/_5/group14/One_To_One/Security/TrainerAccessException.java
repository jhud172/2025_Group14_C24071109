package uk.ac.cf._5.group14.One_To_One.Security;

import org.springframework.security.access.AccessDeniedException;

public class TrainerAccessException extends AccessDeniedException {

    public enum Reason {
        TRAINER_NOT_VERIFIED
    }

    private final Reason reason;

    public TrainerAccessException(Reason reason) {
        super(reason.name());
        this.reason = reason;
    }

    public Reason getReason() {
        return reason;
    }
}
