package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Payments;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uk.ac.cf._5.group14.BehaviourChangeGroupProject.PlatformBilling.PlatformPlan;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.PlatformBilling.PlatformSubscription;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.PlatformBilling.PlatformSubscriptionService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

@Controller
public class PricingController {

    private final AuthHelper authHelper;
    private final PlatformSubscriptionService platformSubscriptionService;
    private final PaymentProviderService paymentProviderService;

    public PricingController(AuthHelper authHelper,
                             PlatformSubscriptionService platformSubscriptionService,
                             PaymentProviderService paymentProviderService) {
        this.authHelper = authHelper;
        this.platformSubscriptionService = platformSubscriptionService;
        this.paymentProviderService = paymentProviderService;
    }

    @GetMapping("/pricing")
    public String pricingPage(Model model) {
        model.addAttribute("pageTitle", "Pricing");
        model.addAttribute("plans", List.of(
            new CheckoutPlanInfo(PlatformPlan.MONTHLY, "Monthly", "£12 / month", "Most flexible",
                List.of("Premium dashboards", "Weekly insights", "Priority support")),
            new CheckoutPlanInfo(PlatformPlan.YEARLY, "Yearly", "£108 / year", "Recommended",
                List.of("2 months free", "Premium dashboards", "Weekly insights", "Priority support"))
        ));
        return "payments/pricing";
    }

    @GetMapping("/pricing/checkout")
    public String checkoutPage(@RequestParam("plan") PlatformPlan plan, Model model, RedirectAttributes redirectAttributes) {
        User user = authHelper.getAuthenticatedUser();
        if (user == null) {
            return "redirect:/login?next=/pricing/checkout?plan=" + plan.name();
        }

        if (isAccountIncomplete(user)) {
            redirectAttributes.addFlashAttribute(
                "verifyError",
                "Verify your email and phone before starting checkout."
            );
            return "redirect:/profile";
        }

        model.addAttribute("pageTitle", "Checkout");
        model.addAttribute("selectedPlan", plan);

        CheckoutPlanInfo planInfo = plan == PlatformPlan.YEARLY
            ? new CheckoutPlanInfo(PlatformPlan.YEARLY, "Yearly", "£108 / year", "Recommended",
                List.of("2 months free", "Premium dashboards", "Weekly insights", "Priority support"))
            : new CheckoutPlanInfo(PlatformPlan.MONTHLY, "Monthly", "£12 / month", "Most flexible",
                List.of("Premium dashboards", "Weekly insights", "Priority support"));

        model.addAttribute("planInfo", planInfo);
        PaymentProviderResult providerResult = paymentProviderService.createCheckoutSession(plan);
        model.addAttribute("providerResult", providerResult);

        PlatformSubscription subscription = platformSubscriptionService.findByUserId(user.getId()).orElse(null);
        model.addAttribute("platformSubscription", subscription);

        return "payments/pricing-checkout";
    }

    private boolean isAccountIncomplete(User user) {
        if (user == null) {
            return true;
        }
        boolean emailUnverified = !user.isEmailVerified();
        boolean phoneUnverified = user.getPhoneNumber() != null && !user.getPhoneNumber().isBlank() && !user.isPhoneVerified();
        return emailUnverified || phoneUnverified;
    }
}
