package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Membership;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

@Slf4j
@Service
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
}
