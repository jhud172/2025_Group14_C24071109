package uk.ac.cf._5.group14.One_To_One.Payments;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class StripeWebhookController {

    private final StripeWebhookService stripeWebhookService;

    public StripeWebhookController(StripeWebhookService stripeWebhookService) {
        this.stripeWebhookService = stripeWebhookService;
    }

    @PostMapping(path = "/pricing/webhook/stripe", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, Object>> handleStripeWebhook(
            @RequestBody(required = false) String payload,
            @RequestHeader(name = "Stripe-Signature", required = false) String signatureHeader) {
        StripeWebhookHandlingResult result = stripeWebhookService.handleWebhook(payload, signatureHeader);
        if (!result.accepted()) {
            return ResponseEntity.status(stripeWebhookService.isConfigured() ? HttpStatus.BAD_REQUEST : HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("ok", false, "message", result.message()));
        }
        return ResponseEntity.ok(Map.of("ok", true, "message", result.message()));
    }
}
