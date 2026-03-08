package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Verification;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Membership.EmailService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserRepository;

@Slf4j
@Service
public class EmailVerificationService {

    private static final int MAX_ATTEMPTS = 5;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final EmailVerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final Clock clock;

    @Value("${app.site.base-url:https://crystal-production.com}")
    private String baseUrl;

    public EmailVerificationService(EmailVerificationTokenRepository tokenRepository,
                                    UserRepository userRepository,
                                    EmailService emailService,
                                    Clock clock) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.clock = clock;
    }

    @Transactional
    public void sendVerification(User user) {
        if (user == null) {
            return;
        }

        if (user.isEmailVerified()) {
            return;
        }

        String token = UUID.randomUUID().toString();
        String plainCode = String.format("%06d", RANDOM.nextInt(1_000_000));
        Instant expiresAt = Instant.now(clock).plus(24, ChronoUnit.HOURS);

        EmailVerificationToken entity = new EmailVerificationToken();
        entity.setUser(userRepository.getReferenceById(user.getId()));
        entity.setToken(token);
        entity.setCode(hashCode(plainCode));
        entity.setExpiresAt(expiresAt);
        tokenRepository.save(entity);

        String link = baseUrl + "/verify/email?token=" + token;
        emailService.sendEmailVerification(user, link, plainCode, expiresAt);
    }

    @Transactional
    public Optional<String> verifyToken(String token) {
        if (token == null || token.isBlank()) {
            return Optional.of("Invalid verification token.");
        }
        EmailVerificationToken entity = tokenRepository.findByToken(token).orElse(null);
        if (entity == null) {
            return Optional.of("Verification token not found.");
        }
        if (entity.getUsedAt() != null) {
            return Optional.of("Verification token already used.");
        }
        Instant now = Instant.now(clock);
        if (entity.getExpiresAt() != null && entity.getExpiresAt().isBefore(now)) {
            return Optional.of("Verification token expired.");
        }

        entity.setUsedAt(now);
        User user = entity.getUser();
        if (user != null) {
            user.setEmailVerified(true);
            user.setEmailVerifiedAt(now);
            userRepository.save(user);
        } else {
            log.warn("Email verification token {} has no user", token);
        }
        tokenRepository.save(entity);
        return Optional.empty();
    }

    @Transactional
    public Optional<String> confirmCode(User user, String code) {
        if (user == null) {
            return Optional.of("User not found.");
        }

        if (user.isEmailVerified()) {
            return Optional.empty();
        }

        EmailVerificationToken latest = tokenRepository.findTopByUserOrderByCreatedAtDesc(user).orElse(null);
        if (latest == null) {
            return Optional.of("No verification code found. Please request a new code.");
        }
        if (latest.getUsedAt() != null) {
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
            tokenRepository.save(latest);
            return Optional.of("Invalid code. Please try again.");
        }

        latest.setUsedAt(now);
        tokenRepository.save(latest);
        User managedUser = latest.getUser();
        if (managedUser != null) {
            managedUser.setEmailVerified(true);
            managedUser.setEmailVerifiedAt(now);
            userRepository.save(managedUser);
        }
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
