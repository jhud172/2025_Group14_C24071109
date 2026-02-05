package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Policies;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PolicyController {

    @GetMapping("/policies/payments")
    public String paymentsPolicy() {
        return "policies/payments";
    }

    @GetMapping("/policies/terms")
    public String termsPolicy() {
        return "policies/terms";
    }

    @GetMapping("/policies/subscription-terms")
    public String subscriptionTermsPolicy() {
        return "policies/subscription-terms";
    }
}
