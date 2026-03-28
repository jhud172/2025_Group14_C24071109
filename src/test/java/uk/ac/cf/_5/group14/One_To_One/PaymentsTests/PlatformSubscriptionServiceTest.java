package uk.ac.cf._5.group14.One_To_One.PaymentsTests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.cf._5.group14.One_To_One.PlatformBilling.PlatformPlan;
import uk.ac.cf._5.group14.One_To_One.PlatformBilling.PlatformSubscription;
import uk.ac.cf._5.group14.One_To_One.PlatformBilling.PlatformSubscriptionRepository;
import uk.ac.cf._5.group14.One_To_One.PlatformBilling.PlatformSubscriptionService;
import uk.ac.cf._5.group14.One_To_One.PlatformBilling.PlatformSubscriptionStatus;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlatformSubscriptionServiceTest {

    @Mock
    private PlatformSubscriptionRepository repository;

    @InjectMocks
    private PlatformSubscriptionService service;

    @Test
    void activateSubscription_createsOrUpdatesActivePremiumRecord() {
        Instant periodEnd = Instant.parse("2026-04-27T00:00:00Z");

        when(repository.findByUserId(7L)).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PlatformSubscription subscription = service.activateSubscription(
                7L,
                PlatformPlan.YEARLY,
                "cus_1",
                "sub_1",
                periodEnd);

        assertThat(subscription.getUserId()).isEqualTo(7L);
        assertThat(subscription.getPlan()).isEqualTo(PlatformPlan.YEARLY);
        assertThat(subscription.getStatus()).isEqualTo(PlatformSubscriptionStatus.ACTIVE);
        assertThat(subscription.getProviderCustomerId()).isEqualTo("cus_1");
        assertThat(subscription.getProviderSubId()).isEqualTo("sub_1");
        assertThat(subscription.getCurrentPeriodEnd()).isEqualTo(periodEnd);
    }
}
