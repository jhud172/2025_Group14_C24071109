package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Membership;

import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.Instant;

public interface EmailService {
    
    /**
     * Send price change notification to a member
     * @param user The user to notify
     * @param productName Name of the membership product
     * @param oldPriceDollars Old price in dollars
     * @param newPriceDollars New price in dollars
     * @param effectiveAt When the new price takes effect
     * @param reason Reason for the price change
     * @param manageUrl Manage/cancel membership link
     */
    void sendPriceChangeNotification(
        User user,
        String productName,
        double oldPriceDollars,
        double newPriceDollars,
        Instant effectiveAt,
        String reason,
        String manageUrl
    );
    
    /**
     * Send trainer verification status update to trainer
     * @param user The trainer to notify
     * @param status New verification status
     * @param adminNotes Optional feedback from admin
     */
    void sendTrainerVerificationUpdate(
        User user,
        String status,
        String adminNotes
    );

    /**
     * Send a password reset email to a user
     * @param user The user to notify
     * @param resetUrl The reset URL containing the token
     * @param expiresAt When the token expires
     */
    void sendPasswordReset(
        User user,
        String resetUrl,
        Instant expiresAt
    );

    /**
     * Send an email verification link and code to a user
     * @param user The user to notify
     * @param verifyUrl The verification URL containing the token
     * @param code The 6-digit verification code
     * @param expiresAt When the token expires
     */
    void sendEmailVerification(
        User user,
        String verifyUrl,
        String code,
        Instant expiresAt
    );

    /**
     * Send an admin-authored outbound email message.
     *
     * @param to Recipient email
     * @param subject Email subject
     * @param body Email plain text body
     */
    void sendAdminMessage(String to, String subject, String body);
}
