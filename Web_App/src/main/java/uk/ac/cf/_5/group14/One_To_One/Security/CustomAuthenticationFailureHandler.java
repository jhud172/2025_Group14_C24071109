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

    public static final String LOGIN_IDENTIFIER_SESSION_ATTRIBUTE = "AUTH_LOGIN_IDENTIFIER";

    @Autowired(required = false)
    private LoginAttemptService loginAttemptService;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailVerificationService emailVerificationService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String identifier = normalizeIdentifier(request.getParameter("username"));
        String loginRole = normalizeLoginRole(request.getParameter("loginType"));
        log.warn("Authentication failure for {} on {}", identifier, request.getRequestURI());

        if (identifier != null) {
            request.getSession(true).setAttribute(LOGIN_IDENTIFIER_SESSION_ATTRIBUTE, identifier);
        }

        // Record failed attempts for basic throttling.
        if (loginAttemptService != null) {
            loginAttemptService.recordFailure(request);
        }

        // Prevent user enumeration: do not distinguish "user not found" vs "wrong password".
        if (exception instanceof DisabledException) {
            User user = resolveUser(identifier);
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
            response.sendRedirect(buildLoginRedirect("disabled", loginRole));
            return;
        }

        if (exception instanceof LockedException) {
            response.sendRedirect(buildLoginRedirect("locked", loginRole));
            return;
        }

        response.sendRedirect(buildLoginRedirect("invalid", loginRole));
    }

    private String normalizeIdentifier(String identifier) {
        if (identifier == null || identifier.isBlank()) {
            return null;
        }
        String trimmed = identifier.trim();
        return trimmed.length() <= 100 ? trimmed : trimmed.substring(0, 100);
    }

    private String normalizeLoginRole(String role) {
        if ("trainer".equalsIgnoreCase(role)) {
            return "trainer";
        }
        if ("gym".equalsIgnoreCase(role)) {
            return "gym";
        }
        return "client";
    }

    private String buildLoginRedirect(String error, String role) {
        return "/login?error=" + error + "&role=" + role;
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
