package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Payments;

public class PaymentProviderResult {

    private final boolean configured;
    private final String message;

    public PaymentProviderResult(boolean configured, String message) {
        this.configured = configured;
        this.message = message;
    }

    public boolean isConfigured() {
        return configured;
    }

    public String getMessage() {
        return message;
    }
}
