package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Payments;

import uk.ac.cf._5.group14.BehaviourChangeGroupProject.PlatformBilling.PlatformPlan;

public interface PaymentProviderService {

    PaymentProviderResult createCheckoutSession(PlatformPlan plan);

    PaymentProviderResult handleWebhook(String eventPayload);
}
