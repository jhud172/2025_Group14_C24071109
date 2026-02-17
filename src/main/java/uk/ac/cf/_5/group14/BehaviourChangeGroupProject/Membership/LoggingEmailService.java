package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Membership;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "app.email", name = "provider", havingValue = "log", matchIfMissing = true)
public class LoggingEmailService implements EmailService {

    private static final DateTimeFormatter DATE_FORMATTER =
        DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm").withZone(ZoneId.systemDefault());

    @Override
    public void sendPriceChangeNotification(
        User user,
        String productName,
        double oldPriceDollars,
        double newPriceDollars,
        Instant effectiveAt,
        String reason,
        String manageUrl
    ) {
        logNotificationQueued("PRICE_CHANGE", user,
            "product=", productName,
            "effectiveAt=", DATE_FORMATTER.format(effectiveAt));
        log.info("=== PRICE CHANGE NOTIFICATION ===");
        log.info("To: {} <{}>", user.getEmail(), user.getEmail());
        log.info("Subject: Price Change Notice for {}", productName);
        log.info("");
        log.info("Dear {} {},", user.getFirstName(), user.getLastName());
        log.info("");
        log.info("We're writing to inform you of a price change for your {} membership.", productName);
        log.info("");
        log.info("Current Price: ${}", String.format("%.2f", oldPriceDollars));
        log.info("New Price: ${}", String.format("%.2f", newPriceDollars));
        log.info("Effective Date: {}", DATE_FORMATTER.format(effectiveAt));
        log.info("");
        log.info("Reason: {}", reason);
        log.info("");
        log.info("The new price will apply when your membership renews on {}.", DATE_FORMATTER.format(effectiveAt));
        log.info("Your current billing period will not be affected.");
        log.info("");
        log.info("Manage or cancel your membership: {}", manageUrl);
        log.info("");
        log.info("Thank you for your understanding.");
        log.info("================================");
    }

    @Override
    public void sendTrainerVerificationUpdate(User user, String status, String adminNotes) {
        logNotificationQueued("TRAINER_VERIFICATION", user, "status=", status);
        log.info("=== TRAINER VERIFICATION UPDATE ===");
        log.info("To: {} <{}>", user.getEmail(), user.getEmail());
        log.info("Subject: Trainer Verification Status Update");
        log.info("");
        log.info("Dear {} {},", user.getFirstName(), user.getLastName());
        log.info("");
        log.info("Your trainer verification request status has been updated to: {}", status);

        if (adminNotes != null && !adminNotes.isBlank()) {
            log.info("");
            log.info("Admin Notes: {}", adminNotes);
        }

        log.info("");
        log.info("Thank you.");
        log.info("===================================");
    }

    @Override
    public void sendPasswordReset(User user, String resetUrl, Instant expiresAt) {
        logNotificationQueued("PASSWORD_RESET", user, "expiresAt=", DATE_FORMATTER.format(expiresAt));
        log.info("=== PASSWORD RESET ===");
        log.info("To: {} <{}>", user.getEmail(), user.getEmail());
        log.info("Subject: Reset your password");
        log.info("");
        log.info("Hello {} {},", user.getFirstName(), user.getLastName());
        log.info("");
        log.info("We received a request to reset your password.");
        log.info("Reset link (valid until {}):", DATE_FORMATTER.format(expiresAt));
        log.info("{}", resetUrl);
        log.info("");
        log.info("If you did not request this, you can safely ignore this email.");
        log.info("=======================");
    }

    @Override
    public void sendEmailVerification(User user, String verifyUrl, Instant expiresAt) {
        logNotificationQueued("EMAIL_VERIFICATION", user, "expiresAt=", DATE_FORMATTER.format(expiresAt));
        log.info("=== EMAIL VERIFICATION ===");
        log.info("To: {} <{}>", user.getEmail(), user.getEmail());
        log.info("Subject: Verify your email");
        log.info("");
        log.info("Hello {} {},", user.getFirstName(), user.getLastName());
        log.info("");
        log.info("Please verify your email address by clicking the link below.");
        log.info("This link is valid until {}:", DATE_FORMATTER.format(expiresAt));
        log.info("{}", verifyUrl);
        log.info("");
        log.info("If you did not request this, you can ignore this email.");
        log.info("===========================");
    }

    private void logNotificationQueued(String type, User user, String... details) {
        StringBuilder extra = new StringBuilder();
        for (int i = 0; i + 1 < details.length; i += 2) {
            if (extra.length() > 0) {
                extra.append(" ");
            }
            extra.append(details[i]).append(details[i + 1]);
        }
        log.info("NOTIFICATION_QUEUED type={} userId={} email={} {}",
            type,
            user != null ? user.getId() : null,
            user != null ? user.getEmail() : null,
            extra.toString().trim());
    }
}
