package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Verification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Membership.EmailService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserRepository;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class EmailVerificationService {

    private final EmailVerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final Clock clock;

    @Value("${app.site.base-url:http://localhost:8080}")
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
        String token = UUID.randomUUID().toString();
        Instant expiresAt = Instant.now(clock).plus(24, ChronoUnit.HOURS);

        EmailVerificationToken entity = new EmailVerificationToken();
        entity.setUser(userRepository.getReferenceById(user.getId()));
        entity.setToken(token);
        entity.setExpiresAt(expiresAt);
        tokenRepository.save(entity);

        String link = baseUrl + "/verify/email?token=" + token;
        emailService.sendEmailVerification(user, link, expiresAt);
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
}
