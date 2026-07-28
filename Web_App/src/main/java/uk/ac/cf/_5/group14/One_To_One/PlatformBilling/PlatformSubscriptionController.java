package uk.ac.cf._5.group14.One_To_One.PlatformBilling;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.cf._5.group14.One_To_One.Payments.PaymentProviderService;
import uk.ac.cf._5.group14.One_To_One.Payments.PaymentSubscriptionUpdate;
import uk.ac.cf._5.group14.One_To_One.Users.AuthHelper;
import uk.ac.cf._5.group14.One_To_One.Users.User;

@Controller
public class PlatformSubscriptionController {

    private final AuthHelper authHelper;
    private final PlatformSubscriptionService subscriptionService;
    private final PaymentProviderService paymentProviderService;

    public PlatformSubscriptionController(AuthHelper authHelper,
                                          PlatformSubscriptionService subscriptionService,
                                          PaymentProviderService paymentProviderService) {
        this.authHelper = authHelper;
        this.subscriptionService = subscriptionService;
        this.paymentProviderService = paymentProviderService;
    }

    @PostMapping("/profile/subscription/cancel")
    public String toggleCancel(@RequestParam("cancelAtPeriodEnd") boolean cancelAtPeriodEnd,
                               RedirectAttributes redirectAttributes) {
        User user = authHelper.getAuthenticatedUser();
        if (user != null) {
            try {
                PlatformSubscription subscription = subscriptionService.findByUserId(user.getId()).orElse(null);
                if (subscription == null) {
                    redirectAttributes.addFlashAttribute("subscriptionError", "No active subscription was found.");
                    return "redirect:/profile";
                }

                String providerSubId = subscription.getProviderSubId();
                if (providerSubId == null || providerSubId.isBlank() || providerSubId.startsWith("sim-")) {
                    subscriptionService.updateCancelAtPeriodEnd(user.getId(), cancelAtPeriodEnd);
                } else {
                    PaymentSubscriptionUpdate update =
                            paymentProviderService.updateSubscriptionCancellation(providerSubId, cancelAtPeriodEnd);
                    if (!update.successful()) {
                        redirectAttributes.addFlashAttribute("subscriptionError", update.message());
                        return "redirect:/profile";
                    }
                    subscriptionService.syncProviderSubscription(
                            providerSubId,
                            update.currentPeriodEnd(),
                            update.cancelAtPeriodEnd(),
                            true);
                }
                redirectAttributes.addFlashAttribute("subscriptionUpdated", true);
            } catch (RuntimeException ex) {
                redirectAttributes.addFlashAttribute(
                        "subscriptionError",
                        "Subscription could not be updated with the payment provider.");
            }
        }
        return "redirect:/profile";
    }

    @PostMapping("/profile/subscription/plan")
    public String updatePlan(@RequestParam("plan") PlatformPlan plan,
                             RedirectAttributes redirectAttributes) {
        User user = authHelper.getAuthenticatedUser();
        if (user != null) {
            subscriptionService.updatePlan(user.getId(), plan);
            redirectAttributes.addFlashAttribute("subscriptionUpdated", true);
        }
        return "redirect:/profile";
    }
}
