package uk.ac.cf._5.group14.One_To_One.Security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import uk.ac.cf._5.group14.One_To_One.Users.User;
import uk.ac.cf._5.group14.One_To_One.Users.UserService;
import uk.ac.cf._5.group14.One_To_One.Verification.EmailVerificationService;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Autowired(required = false)
    private LoginAttemptService loginAttemptService;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailVerificationService emailVerificationService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.warn("Authentication failure for {} on {}", request.getParameter("username"), request.getRequestURI());

        // Record failed attempts for basic throttling.
        if (loginAttemptService != null) {
            loginAttemptService.recordFailure(request);
        }

        // Prevent user enumeration: do not distinguish "user not found" vs "wrong password".
        if (exception instanceof DisabledException) {
            User user = resolveUser(request.getParameter("username"));
            if (user != null && !user.isEmailVerified()) {
                long cooldownRemaining = emailVerificationService.getResendCooldownRemainingSeconds(user);
                if (cooldownRemaining > 0) {
                    response.sendRedirect(buildVerificationRedirect(user.getEmail(), "cooldown"));
                    return;
                }
                try {
                    emailVerificationService.sendVerification(user);
                    response.sendRedirect(buildVerificationRedirect(user.getEmail(), "resent"));
                } catch (Exception ex) {
                    response.sendRedirect(buildVerificationRedirect(user.getEmail(), "resendError"));
                }
                return;
            }
            response.sendRedirect("/login?error=disabled");
            return;
        }

        if (exception instanceof LockedException) {
            response.sendRedirect("/login?error=locked");
            return;
        }

        response.sendRedirect("/login?error=invalid");
    }

    private User resolveUser(String identifier) {
        if (identifier == null || identifier.isBlank()) {
            return null;
        }
        String trimmed = identifier.trim();
        if (trimmed.contains("@")) {
            return userService.findByEmail(trimmed);
        }
        return userService.findByUsername(trimmed);
    }

    private String buildVerificationRedirect(String email, String state) {
        String encodedEmail = email == null
                ? ""
                : URLEncoder.encode(email, StandardCharsets.UTF_8);
        return "/verify/email/code?email=" + encodedEmail + "&" + state + "=1";
    }
}
