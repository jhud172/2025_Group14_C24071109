package uk.ac.cf._5.group14.One_To_One.PaymentsTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "app.payments.stripe.webhook-secret=false")
@AutoConfigureMockMvc
@ActiveProfiles("test")
class StripeWebhookSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void webhookCanReachItsSignatureValidationWithoutBrowserCsrfToken() throws Exception {
        mockMvc.perform(post("/pricing/webhook/stripe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Stripe-Signature", "t=0,v1=invalid")
                        .content("{\"type\":\"sandbox.probe\"}"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.ok").value(false));
    }
}
