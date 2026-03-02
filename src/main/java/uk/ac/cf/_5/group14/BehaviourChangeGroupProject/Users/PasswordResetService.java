package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Membership.EmailService;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private static final Duration TOKEN_TTL = Duration.ofMinutes(45);

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final EmailService emailService;
    private final Clock clock;

    @Value("${app.site.base-url:https://crystal-production.com}")
    private String baseUrl = "https://crystal-production.com";

    @Transactional
    public void requestPasswordReset(String email) {
        if (email == null || email.isBlank()) {
            return;
        }

        Optional<User> userOpt = userRepository.findByEmailIgnoreCase(email.trim());
        if (userOpt.isEmpty()) {
            return;
        }

        User user = userOpt.get();
        Instant now = Instant.now(clock);
        tokenRepository.markUsedForUser(user.getId(), now);

        PasswordResetToken token = new PasswordResetToken();
        token.setUserId(user.getId());
        token.setToken(UUID.randomUUID().toString());
        token.setExpiresAt(now.plus(TOKEN_TTL));
        token.setUsedAt(null);

        PasswordResetToken saved = tokenRepository.save(token);
        String resetUrl = buildResetUrl(saved.getToken());
        emailService.sendPasswordReset(user, resetUrl, saved.getExpiresAt());
    }

    public Optional<PasswordResetToken> getValidToken(String tokenValue) {
        if (tokenValue == null || tokenValue.isBlank()) {
            return Optional.empty();
        }
        return tokenRepository.findByToken(tokenValue)
            .filter(token -> token.getUsedAt() == null)
            .filter(token -> token.getExpiresAt().isAfter(Instant.now(clock)));
    }

    @Transactional
    public boolean resetPassword(String tokenValue, String newPassword) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(tokenValue);
        if (tokenOpt.isEmpty()) {
            return false;
        }

        PasswordResetToken token = tokenOpt.get();
        Instant now = Instant.now(clock);

        if (token.getUsedAt() != null || !token.getExpiresAt().isAfter(now)) {
            return false;
        }

        Optional<User> userOpt = userRepository.findById(token.getUserId());
        if (userOpt.isEmpty()) {
            return false;
        }

        userService.updatePassword(userOpt.get(), newPassword);
        token.setUsedAt(now);
        tokenRepository.save(token);
        return true;
    }

    private String buildResetUrl(String tokenValue) {
        String encoded = URLEncoder.encode(tokenValue, StandardCharsets.UTF_8);
        return baseUrl + "/reset-password?token=" + encoded;
    }
}
