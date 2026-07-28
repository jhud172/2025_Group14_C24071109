package uk.ac.cf._5.group14.One_To_One.Payments;

import java.time.Instant;

public record PaymentSubscriptionUpdate(
        boolean successful,
        boolean cancelAtPeriodEnd,
        Instant currentPeriodEnd,
        String message
) {
}
