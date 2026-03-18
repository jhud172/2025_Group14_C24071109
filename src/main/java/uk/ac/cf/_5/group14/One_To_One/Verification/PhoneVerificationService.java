package uk.ac.cf._5.group14.One_To_One.Verification;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.cf._5.group14.One_To_One.Users.User;
import uk.ac.cf._5.group14.One_To_One.Users.UserRepository;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Optional;
import java.util.Base64;
import java.security.MessageDigest;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Slf4j
@Service
public class PhoneVerificationService {

    private static final int MAX_ATTEMPTS = 5;
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Pattern E164_PATTERN = Pattern.compile("^\\+[1-9]\\d{7,14}$");

    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .callTimeout(10, TimeUnit.SECONDS)
            .build();

    private final PhoneVerificationCodeRepository codeRepository;
    private final UserRepository userRepository;
    private final Clock clock;
    private final Environment environment;

    @Value("${app.sms.provider:console}")
    private String smsProvider;

    @Value("${app.sms.fail-on-error:false}")
    private boolean failOnSmsError;

    @Value("${app.sms.twilio.account-sid:}")
    private String twilioAccountSid;

    @Value("${app.sms.twilio.auth-token:}")
    private String twilioAuthToken;

    @Value("${app.sms.twilio.from-number:}")
    private String twilioFromNumber;

    @Value("${app.sms.twilio.messaging-service-sid:}")
    private String twilioMessagingServiceSid;

    public PhoneVerificationService(PhoneVerificationCodeRepository codeRepository,
                                    UserRepository userRepository,
                                    Clock clock,
                                    Environment environment) {
        this.codeRepository = codeRepository;
        this.userRepository = userRepository;
        this.clock = clock;
        this.environment = environment;
    }

    @Transactional
    public void sendCode(User user) {
        if (user == null || user.getPhoneNumber() == null || user.getPhoneNumber().isBlank()) {
            return;
        }

        if (user.isPhoneVerified()) {
            return;
        }

        String code = String.format("%06d", RANDOM.nextInt(1_000_000));
        Instant expiresAt = Instant.now(clock).plus(10, ChronoUnit.MINUTES);

        PhoneVerificationCode entity = new PhoneVerificationCode();
        entity.setUser(userRepository.getReferenceById(user.getId()));
        entity.setCode(hashCode(code));
        entity.setExpiresAt(expiresAt);
        codeRepository.save(entity);

        String targetPhone = normalizePhoneNumber(user.getPhoneNumber());
        String provider = resolveProvider();
        log.info("Phone verification send requested for user {} using provider {} to {}",
                user.getId(), provider, maskPhone(targetPhone));

        try {
            sendSmsCode(provider, targetPhone, code);
            log.info("Phone verification send completed for user {} via {} to {}",
                    user.getId(), provider, maskPhone(targetPhone));
        } catch (Exception ex) {
            String safeMessage = ex.getMessage() == null || ex.getMessage().isBlank()
                    ? "Unable to send SMS verification code right now."
                    : ex.getMessage();

            log.error("Phone verification send failed for user {} via {} to {}: {}",
                    user.getId(), provider, maskPhone(targetPhone), safeMessage, ex);

            if (!failOnSmsError) {
                log.warn("app.sms.fail-on-error is false, but verification send failures are still returned to the user to prevent false success messages.");
            }
            throw new IllegalStateException(safeMessage, ex);
        }

        if (Arrays.asList(environment.getActiveProfiles()).contains("dev") || "console".equals(provider)) {
            log.info("DEV OTP for user {} ({}): {}", user.getId(), targetPhone, code);
        }
    }

    private void sendSmsCode(String provider, String toPhone, String code) throws Exception {
        if (!E164_PATTERN.matcher(toPhone).matches()) {
            throw new IllegalStateException("Phone number format is invalid. Use full international format like +447123456789.");
        }

        if ("console".equals(provider) || provider.isBlank()) {
            log.warn("SMS provider is set to console. No external SMS is sent; code is logged for development only.");
            return;
        }

        if (!"twilio".equals(provider)) {
            throw new IllegalStateException("Unsupported SMS provider: " + smsProvider);
        }

        if (twilioAccountSid == null || twilioAccountSid.isBlank()
                || twilioAuthToken == null || twilioAuthToken.isBlank()) {
            throw new IllegalStateException("Twilio credentials are missing.");
        }

        RequestBody formBody = buildTwilioFormBody(toPhone, code);
        Request request = new Request.Builder()
                .url("https://api.twilio.com/2010-04-01/Accounts/" + twilioAccountSid + "/Messages.json")
                .addHeader("Authorization", Credentials.basic(twilioAccountSid, twilioAuthToken))
                .post(formBody)
                .build();

        log.info("Sending Twilio verification SMS to {} using {}",
                maskPhone(toPhone),
                twilioMessagingServiceSid != null && !twilioMessagingServiceSid.isBlank()
                        ? "messaging service sid"
                        : "from-number");

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String responseBody = response.body() != null ? response.body().string() : "";
                throw new IllegalStateException("Twilio send failed (HTTP " + response.code() + "): " + summarizeTwilioError(responseBody));
            }
        }
    }

    private String resolveProvider() {
        return smsProvider != null ? smsProvider.trim().toLowerCase(Locale.ROOT) : "console";
    }

    private RequestBody buildTwilioFormBody(String toPhone, String code) {
        FormBody.Builder builder = new FormBody.Builder()
                .add("To", toPhone)
                .add("Body", "Your One To One verification code is " + code + ". It expires in 10 minutes.");

        if (twilioMessagingServiceSid != null && !twilioMessagingServiceSid.isBlank()) {
            builder.add("MessagingServiceSid", twilioMessagingServiceSid);
            return builder.build();
        }

        if (twilioFromNumber == null || twilioFromNumber.isBlank()) {
            throw new IllegalStateException("Twilio sender is missing. Set from-number or messaging-service-sid.");
        }

        builder.add("From", twilioFromNumber.trim());
        return builder.build();
    }

    private String normalizePhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            return "";
        }
        String value = phoneNumber.trim().replaceAll("[^0-9+]", "");
        if (value.startsWith("+")) {
            return value;
        }
        if (value.startsWith("00")) {
            return "+" + value.substring(2);
        }
        if (value.startsWith("0")) {
            return "+44" + value.substring(1);
        }
        return "+" + value;
    }

    private String summarizeTwilioError(String responseBody) {
        if (responseBody == null || responseBody.isBlank()) {
            return "No response body.";
        }

        String flattened = responseBody.replaceAll("\\s+", " ").trim();
        if (flattened.length() > 240) {
            return flattened.substring(0, 240) + "...";
        }
        return flattened;
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return "[empty]";
        }
        String normalized = phone.trim();
        int len = normalized.length();
        if (len <= 4) {
            return "****";
        }
        return "*".repeat(Math.max(0, len - 4)) + normalized.substring(len - 4);
    }

    @Transactional
    public Optional<String> confirmCode(User user, String code) {
        if (user == null) {
            return Optional.of("User not found.");
        }

        if (user.isPhoneVerified()) {
            return Optional.empty();
        }

        PhoneVerificationCode latest = codeRepository.findTopByUserOrderByCreatedAtDesc(user).orElse(null);
        if (latest == null) {
            return Optional.of("No verification code found. Please request a new code.");
        }
        if (latest.getVerifiedAt() != null) {
            return Optional.of("Verification code already used.");
        }
        Instant now = Instant.now(clock);
        if (latest.getExpiresAt() != null && latest.getExpiresAt().isBefore(now)) {
            return Optional.of("Verification code expired. Please request a new code.");
        }
        if (latest.getAttempts() >= MAX_ATTEMPTS) {
            return Optional.of("Too many attempts. Please request a new code.");
        }
        if (code == null || code.isBlank() || !hashCode(code).equals(latest.getCode())) {
            latest.setAttempts(latest.getAttempts() + 1);
            codeRepository.save(latest);
            return Optional.of("Invalid code. Please try again.");
        }

        latest.setVerifiedAt(now);
        codeRepository.save(latest);
        user.setPhoneVerified(true);
        user.setPhoneVerifiedAt(now);
        userRepository.save(user);
        return Optional.empty();
    }

    private String hashCode(String code) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(code.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashed);
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to hash verification code.", ex);
        }
    }
}
