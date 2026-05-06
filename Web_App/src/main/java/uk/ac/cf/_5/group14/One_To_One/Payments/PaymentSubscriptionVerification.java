package uk.ac.cf._5.group14.One_To_One.Payments;

import java.time.Instant;

public record PaymentSubscriptionVerification(boolean active,
                                              String provider,
                                              String customerId,
                                              String subscriptionId,
                                              Instant currentPeriodEnd,
                                              String message) {
}
