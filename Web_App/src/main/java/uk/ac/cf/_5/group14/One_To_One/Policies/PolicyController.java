package uk.ac.cf._5.group14.One_To_One.Policies;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PolicyController {

    @GetMapping("/policies/privacy")
    public String privacyPolicy() {
        return "public-views/policies/privacy";
    }

    @GetMapping("/policies/payments")
    public String paymentsPolicy() {
        return "public-views/policies/payments";
    }

    @GetMapping("/policies/terms")
    public String termsPolicy() {
        return "public-views/policies/terms";
    }

    @GetMapping("/policies/subscription-terms")
    public String subscriptionTermsPolicy() {
        return "public-views/policies/subscription-terms";
    }
}
