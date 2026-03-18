package uk.ac.cf._5.group14.One_To_One.Payments;

import uk.ac.cf._5.group14.One_To_One.PlatformBilling.PlatformPlan;

public interface PaymentProviderService {

    PaymentProviderResult createCheckoutSession(PlatformPlan plan);

    PaymentProviderResult handleWebhook(String eventPayload);
}
