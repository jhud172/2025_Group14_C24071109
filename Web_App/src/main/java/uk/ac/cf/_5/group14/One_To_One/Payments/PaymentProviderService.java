package uk.ac.cf._5.group14.One_To_One.Payments;

import uk.ac.cf._5.group14.One_To_One.PlatformBilling.PlatformPlan;
import uk.ac.cf._5.group14.One_To_One.Users.User;

public interface PaymentProviderService {

    boolean isConfigured();

    boolean isSimulationMode();

    String providerName();

    PaymentCheckoutSession createCheckoutSession(User user, PlatformPlan plan, String successUrl, String cancelUrl);

    PaymentSubscriptionVerification verifyCheckoutSession(String sessionId);

    PaymentSubscriptionUpdate updateSubscriptionCancellation(String subscriptionId, boolean cancelAtPeriodEnd);
}
