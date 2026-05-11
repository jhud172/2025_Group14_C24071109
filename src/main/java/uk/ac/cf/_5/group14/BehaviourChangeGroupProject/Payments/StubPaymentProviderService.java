package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Payments;

import org.springframework.stereotype.Service;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.PlatformBilling.PlatformPlan;

@Service
public class StubPaymentProviderService implements PaymentProviderService {

    @Override
    public PaymentProviderResult createCheckoutSession(PlatformPlan plan) {
        return new PaymentProviderResult(false, "Payment provider not configured yet.");
    }

    @Override
    public PaymentProviderResult handleWebhook(String eventPayload) {
        return new PaymentProviderResult(false, "Payment provider not configured yet.");
    }
}
