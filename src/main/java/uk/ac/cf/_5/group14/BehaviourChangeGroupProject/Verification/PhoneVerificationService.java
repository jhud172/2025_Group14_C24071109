package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Verification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserRepository;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Optional;
import java.util.Base64;
import java.security.MessageDigest;

@Slf4j
@Service
public class PhoneVerificationService {

    private static final int MAX_ATTEMPTS = 5;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final PhoneVerificationCodeRepository codeRepository;
    private final UserRepository userRepository;
    private final Clock clock;
    private final Environment environment;

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

        if (Arrays.asList(environment.getActiveProfiles()).contains("dev")) {
            log.info("DEV OTP for user {} ({}): {}", user.getId(), user.getPhoneNumber(), code);
        }
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
