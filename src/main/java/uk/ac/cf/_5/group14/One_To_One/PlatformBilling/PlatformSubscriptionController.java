package uk.ac.cf._5.group14.One_To_One.PlatformBilling;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.cf._5.group14.One_To_One.Users.AuthHelper;
import uk.ac.cf._5.group14.One_To_One.Users.User;

@Controller
public class PlatformSubscriptionController {

    private final AuthHelper authHelper;
    private final PlatformSubscriptionService subscriptionService;

    public PlatformSubscriptionController(AuthHelper authHelper,
                                          PlatformSubscriptionService subscriptionService) {
        this.authHelper = authHelper;
        this.subscriptionService = subscriptionService;
    }

    @PostMapping("/profile/subscription/cancel")
    public String toggleCancel(@RequestParam("cancelAtPeriodEnd") boolean cancelAtPeriodEnd,
                               RedirectAttributes redirectAttributes) {
        User user = authHelper.getAuthenticatedUser();
        if (user != null) {
            subscriptionService.updateCancelAtPeriodEnd(user.getId(), cancelAtPeriodEnd);
            redirectAttributes.addFlashAttribute("subscriptionUpdated", true);
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
