package uk.ac.cf._5.group14.One_To_One.Payments;

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
import uk.ac.cf._5.group14.One_To_One.PlatformBilling.PlatformPlan;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.io.IOException;
import java.time.Instant;

@Service
public class StripePaymentProviderService implements PaymentProviderService {

    private static final Logger log = LoggerFactory.getLogger(StripePaymentProviderService.class);
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
    public PaymentCheckoutSession createCheckoutSession(User user, PlatformPlan plan, String successUrl, String cancelUrl) {
        ensureConfigured();
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("User is required for checkout.");
        }

        FormBody.Builder form = new FormBody.Builder()
                .add("mode", "subscription")
                .add("success_url", successUrl)
                .add("cancel_url", cancelUrl)
                .add("client_reference_id", String.valueOf(user.getId()))
                .add("metadata[scope]", "platform_premium")
                .add("metadata[userId]", String.valueOf(user.getId()))
                .add("metadata[plan]", plan.name())
                .add("subscription_data[metadata][scope]", "platform_premium")
                .add("subscription_data[metadata][userId]", String.valueOf(user.getId()))
                .add("subscription_data[metadata][plan]", plan.name())
                .add("line_items[0][quantity]", "1")
                .add("line_items[0][price_data][currency]", currency)
                .add("line_items[0][price_data][product_data][name]", productName(plan))
                .add("line_items[0][price_data][recurring][interval]", recurringInterval(plan))
                .add("line_items[0][price_data][unit_amount]", String.valueOf(unitAmount(plan)));

        Request request = new Request.Builder()
                .url(STRIPE_API_BASE + "/checkout/sessions")
                .post(form.build())
                .addHeader("Authorization", "Bearer " + normalizedSecretKey())
                .build();

        try (Response response = client.newCall(request).execute()) {
            String body = response.body() != null ? response.body().string() : "{}";
            if (!response.isSuccessful()) {
                log.warn("Stripe pricing checkout creation failed with status {} body={}", response.code(), body);
                throw new IllegalStateException("Secure subscription checkout is unavailable right now.");
            }

            JsonNode root = mapper.readTree(body);
            String sessionId = root.path("id").asText("");
            String checkoutUrl = root.path("url").asText("");
            if (sessionId.isBlank() || checkoutUrl.isBlank()) {
                throw new IllegalStateException("Secure subscription checkout is unavailable right now.");
            }
            return new PaymentCheckoutSession(providerName(), sessionId, checkoutUrl);
        } catch (IOException e) {
            log.warn("Stripe pricing checkout creation failed", e);
            throw new IllegalStateException("Secure subscription checkout is unavailable right now.", e);
        }
    }

    @Override
    public PaymentSubscriptionVerification verifyCheckoutSession(String sessionId) {
        ensureConfigured();
        if (sessionId == null || sessionId.isBlank()) {
            return new PaymentSubscriptionVerification(false, providerName(), null, null, null, "Missing checkout session.");
        }

        Request request = new Request.Builder()
                .url(STRIPE_API_BASE + "/checkout/sessions/" + sessionId)
                .get()
                .addHeader("Authorization", "Bearer " + normalizedSecretKey())
                .build();

        try (Response response = client.newCall(request).execute()) {
            String body = response.body() != null ? response.body().string() : "{}";
            if (!response.isSuccessful()) {
                log.warn("Stripe pricing checkout verification failed with status {} body={}", response.code(), body);
                return new PaymentSubscriptionVerification(false, providerName(), null, null, null, "Subscription checkout could not be verified.");
            }

            JsonNode session = mapper.readTree(body);
            String status = session.path("status").asText("");
            String customerId = session.path("customer").asText("");
            String subscriptionId = session.path("subscription").asText("");
            if (!"complete".equalsIgnoreCase(status) || subscriptionId.isBlank()) {
                return new PaymentSubscriptionVerification(false, providerName(), customerId, subscriptionId, null, "Subscription checkout was not completed.");
            }

            Instant currentPeriodEnd = fetchCurrentPeriodEnd(subscriptionId);
            return new PaymentSubscriptionVerification(true, providerName(), customerId, subscriptionId, currentPeriodEnd, "Subscription activated.");
        } catch (IOException e) {
            log.warn("Stripe pricing checkout verification failed", e);
            return new PaymentSubscriptionVerification(false, providerName(), null, null, null, "Subscription checkout could not be verified.");
        }
    }

    @Override
    public PaymentSubscriptionUpdate updateSubscriptionCancellation(String subscriptionId, boolean cancelAtPeriodEnd) {
        ensureConfigured();
        if (subscriptionId == null || subscriptionId.isBlank()) {
            return new PaymentSubscriptionUpdate(false, cancelAtPeriodEnd, null, "Missing subscription id.");
        }
        if (isSimulationMode() || subscriptionId.startsWith("sim-")) {
            return new PaymentSubscriptionUpdate(
                    true,
                    cancelAtPeriodEnd,
                    null,
                    "Simulated subscription updated.");
        }

        Request request = new Request.Builder()
                .url(STRIPE_API_BASE + "/subscriptions/" + subscriptionId.trim())
                .post(new FormBody.Builder()
                        .add("cancel_at_period_end", String.valueOf(cancelAtPeriodEnd))
                        .build())
                .addHeader("Authorization", "Bearer " + normalizedSecretKey())
                .build();

        try (Response response = client.newCall(request).execute()) {
            String body = response.body() != null ? response.body().string() : "{}";
            if (!response.isSuccessful()) {
                log.warn("Stripe subscription cancellation update failed with status {} body={}", response.code(), body);
                return new PaymentSubscriptionUpdate(
                        false,
                        cancelAtPeriodEnd,
                        null,
                        "Subscription could not be updated with Stripe.");
            }

            JsonNode subscription = mapper.readTree(body);
            boolean providerCancelAtPeriodEnd = subscription.path("cancel_at_period_end").asBoolean(cancelAtPeriodEnd);
            long epochSeconds = subscription.path("current_period_end").asLong(0L);
            Instant periodEnd = epochSeconds > 0L ? Instant.ofEpochSecond(epochSeconds) : null;
            return new PaymentSubscriptionUpdate(
                    true,
                    providerCancelAtPeriodEnd,
                    periodEnd,
                    providerCancelAtPeriodEnd
                            ? "Subscription will cancel at the end of the billing period."
                            : "Subscription cancellation was reversed.");
        } catch (IOException e) {
            log.warn("Stripe subscription cancellation update failed", e);
            return new PaymentSubscriptionUpdate(
                    false,
                    cancelAtPeriodEnd,
                    null,
                    "Subscription could not be updated with Stripe.");
        }
    }

    private Instant fetchCurrentPeriodEnd(String subscriptionId) throws IOException {
        Request request = new Request.Builder()
                .url(STRIPE_API_BASE + "/subscriptions/" + subscriptionId)
                .get()
                .addHeader("Authorization", "Bearer " + normalizedSecretKey())
                .build();

        try (Response response = client.newCall(request).execute()) {
            String body = response.body() != null ? response.body().string() : "{}";
            if (!response.isSuccessful()) {
                log.warn("Stripe subscription retrieval failed with status {} body={}", response.code(), body);
                return null;
            }
            JsonNode subscription = mapper.readTree(body);
            long epochSeconds = subscription.path("current_period_end").asLong(0L);
            return epochSeconds > 0L ? Instant.ofEpochSecond(epochSeconds) : null;
        }
    }

    private String productName(PlatformPlan plan) {
        return switch (plan) {
            case MONTHLY -> "Platform Premium Monthly";
            case YEARLY -> "Platform Premium Yearly";
            case INFINITE -> "Platform Premium";
        };
    }

    private String recurringInterval(PlatformPlan plan) {
        return plan == PlatformPlan.YEARLY ? "year" : "month";
    }

    private long unitAmount(PlatformPlan plan) {
        return switch (plan) {
            case MONTHLY -> 1200L;
            case YEARLY -> 10800L;
            case INFINITE -> throw new IllegalArgumentException("Infinite plan cannot be purchased through recurring checkout.");
        };
    }

    private void ensureConfigured() {
        if (!isConfigured()) {
            throw new IllegalStateException("Secure subscription checkout is not configured yet.");
        }
    }

    private String normalizedSecretKey() {
        return secretKey == null ? "" : secretKey.trim();
    }
}
