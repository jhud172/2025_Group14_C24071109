package uk.ac.cf._5.group14.One_To_One.PaymentsTests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import uk.ac.cf._5.group14.One_To_One.Payments.PaymentProviderService;
import uk.ac.cf._5.group14.One_To_One.Payments.PaymentSubscriptionUpdate;
import uk.ac.cf._5.group14.One_To_One.PlatformBilling.PlatformPlan;
import uk.ac.cf._5.group14.One_To_One.PlatformBilling.PlatformSubscription;
import uk.ac.cf._5.group14.One_To_One.PlatformBilling.PlatformSubscriptionController;
import uk.ac.cf._5.group14.One_To_One.PlatformBilling.PlatformSubscriptionService;
import uk.ac.cf._5.group14.One_To_One.Users.AuthHelper;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlatformSubscriptionControllerTest {

    @Mock
    private AuthHelper authHelper;

    @Mock
    private PlatformSubscriptionService subscriptionService;

    @Mock
    private PaymentProviderService paymentProviderService;

    @Test
    void cancellationIsConfirmedWithStripeBeforeLocalStateChanges() {
        User user = new User();
        user.setId(7L);
        PlatformSubscription subscription = new PlatformSubscription();
        subscription.setUserId(7L);
        subscription.setPlan(PlatformPlan.MONTHLY);
        subscription.setProviderSubId("sub_test_1");
        Instant periodEnd = Instant.parse("2026-08-28T00:00:00Z");

        when(authHelper.getAuthenticatedUser()).thenReturn(user);
        when(subscriptionService.findByUserId(7L)).thenReturn(Optional.of(subscription));
        when(paymentProviderService.updateSubscriptionCancellation("sub_test_1", true))
                .thenReturn(new PaymentSubscriptionUpdate(true, true, periodEnd, "Updated."));

        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        PlatformSubscriptionController controller = new PlatformSubscriptionController(
                authHelper,
                subscriptionService,
                paymentProviderService);

        String view = controller.toggleCancel(true, redirectAttributes);

        assertThat(view).isEqualTo("redirect:/profile");
        assertThat(redirectAttributes.getFlashAttributes().get("subscriptionUpdated")).isEqualTo(true);
        verify(subscriptionService).syncProviderSubscription("sub_test_1", periodEnd, true, true);
        verify(subscriptionService, never()).updateCancelAtPeriodEnd(7L, true);
    }

    @Test
    void providerFailureDoesNotChangeLocalCancellationState() {
        User user = new User();
        user.setId(7L);
        PlatformSubscription subscription = new PlatformSubscription();
        subscription.setUserId(7L);
        subscription.setProviderSubId("sub_test_1");

        when(authHelper.getAuthenticatedUser()).thenReturn(user);
        when(subscriptionService.findByUserId(7L)).thenReturn(Optional.of(subscription));
        when(paymentProviderService.updateSubscriptionCancellation("sub_test_1", true))
                .thenReturn(new PaymentSubscriptionUpdate(false, true, null, "Stripe rejected the update."));

        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        PlatformSubscriptionController controller = new PlatformSubscriptionController(
                authHelper,
                subscriptionService,
                paymentProviderService);

        controller.toggleCancel(true, redirectAttributes);

        assertThat(redirectAttributes.getFlashAttributes().get("subscriptionError"))
                .isEqualTo("Stripe rejected the update.");
        verify(subscriptionService, never()).syncProviderSubscription(
                "sub_test_1",
                null,
                true,
                true);
        verify(subscriptionService, never()).updateCancelAtPeriodEnd(7L, true);
    }
}
