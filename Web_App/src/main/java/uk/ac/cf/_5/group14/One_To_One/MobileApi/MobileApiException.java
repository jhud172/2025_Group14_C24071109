package uk.ac.cf._5.group14.One_To_One.MobileApi;

public class MobileApiException extends RuntimeException {
    private final int status;

    public MobileApiException(int status, String message) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
