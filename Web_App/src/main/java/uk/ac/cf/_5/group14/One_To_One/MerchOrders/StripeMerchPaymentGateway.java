package uk.ac.cf._5.group14.One_To_One.MerchOrders;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.ac.cf._5.group14.One_To_One.Merch.MerchProduct;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class StripeMerchPaymentGateway implements MerchPaymentGateway {

    private static final Logger log = LoggerFactory.getLogger(StripeMerchPaymentGateway.class);
    private static final String STRIPE_API_BASE = "https://api.stripe.com/v1";

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${app.payments.stripe.secret-key:}")
    private String secretKey;

    @Value("${app.payments.currency:gbp}")
    private String currency;

    @Override
    public boolean isConfigured() {
        return !normalizedSecretKey().isBlank();
    }

    @Override
    public boolean isSimulationMode() {
        return "false".equalsIgnoreCase(normalizedSecretKey());
    }

    @Override
    public String providerName() {
        return isSimulationMode() ? "Simulated checkout" : "Stripe";
    }

    @Override
    public MerchHostedCheckoutSession createCheckoutSession(MerchOrder order,
                                                            MerchProduct product,
                                                            int quantity,
                                                            String successUrl,
                                                            String cancelUrl) {
        ensureConfigured();

        FormBody.Builder form = new FormBody.Builder()
                .add("mode", "payment")
                .add("success_url", successUrl)
                .add("cancel_url", cancelUrl)
                .add("client_reference_id", String.valueOf(order.getId()))
                .add("metadata[scope]", "merch")
                .add("metadata[orderId]", String.valueOf(order.getId()))
                .add("payment_intent_data[metadata][scope]", "merch")
                .add("payment_intent_data[metadata][orderId]", String.valueOf(order.getId()))
                .add("line_items[0][quantity]", String.valueOf(quantity))
                .add("line_items[0][price_data][currency]", currency)
                .add("line_items[0][price_data][unit_amount]", String.valueOf(toMinorUnits(product.getPrice())))
                .add("line_items[0][price_data][product_data][name]", product.getName());

        if (product.getDescription() != null && !product.getDescription().isBlank()) {
            form.add("line_items[0][price_data][product_data][description]", product.getDescription());
        }
        Request request = new Request.Builder()
                .url(STRIPE_API_BASE + "/checkout/sessions")
                .post(form.build())
                .addHeader("Authorization", "Bearer " + normalizedSecretKey())
                .build();

        try (Response response = client.newCall(request).execute()) {
            String body = response.body() != null ? response.body().string() : "{}";
            if (!response.isSuccessful()) {
                log.warn("Stripe checkout session creation failed with status {} body={}", response.code(), body);
                throw new IllegalStateException("Secure checkout is unavailable right now.");
            }

            JsonNode root = mapper.readTree(body);
            String sessionId = root.path("id").asText("");
            String checkoutUrl = root.path("url").asText("");
            if (sessionId.isBlank() || checkoutUrl.isBlank()) {
                throw new IllegalStateException("Secure checkout is unavailable right now.");
            }
            return new MerchHostedCheckoutSession(providerName(), sessionId, checkoutUrl);
        } catch (IOException e) {
            log.warn("Stripe checkout session creation failed", e);
            throw new IllegalStateException("Secure checkout is unavailable right now.", e);
        }
    }

    @Override
    public MerchPaymentVerification verifyCheckoutSession(String paymentReference) {
        ensureConfigured();
        if (paymentReference == null || paymentReference.isBlank()) {
            return new MerchPaymentVerification(false, providerName(), paymentReference, "Missing payment reference.");
        }

        Request request = new Request.Builder()
                .url(STRIPE_API_BASE + "/checkout/sessions/" + paymentReference)
                .get()
                .addHeader("Authorization", "Bearer " + normalizedSecretKey())
                .build();

        try (Response response = client.newCall(request).execute()) {
            String body = response.body() != null ? response.body().string() : "{}";
            if (!response.isSuccessful()) {
                log.warn("Stripe checkout session verification failed with status {} body={}", response.code(), body);
                return new MerchPaymentVerification(false, providerName(), paymentReference, "Secure checkout could not be verified.");
            }

            JsonNode root = mapper.readTree(body);
            String paymentStatus = root.path("payment_status").asText("");
            String status = root.path("status").asText("");
            boolean paid = "paid".equalsIgnoreCase(paymentStatus) || "complete".equalsIgnoreCase(status);
            String message = paid ? "Payment confirmed." : "Payment was not completed.";
            return new MerchPaymentVerification(paid, providerName(), paymentReference, message);
        } catch (IOException e) {
            log.warn("Stripe checkout session verification failed", e);
            return new MerchPaymentVerification(false, providerName(), paymentReference, "Secure checkout could not be verified.");
        }
    }

    private void ensureConfigured() {
        if (!isConfigured()) {
            throw new IllegalStateException("Secure checkout is not configured yet.");
        }
    }

    private String normalizedSecretKey() {
        return secretKey == null ? "" : secretKey.trim();
    }

    private long toMinorUnits(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount is required.");
        }
        return amount.movePointRight(2).setScale(0, RoundingMode.HALF_UP).longValueExact();
    }
}
