package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Autowired(required = false)
    private LoginAttemptService loginAttemptService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        System.out.println("Authentication Failure Handler invoked: " + exception.getMessage());
        System.out.println("Request URI: " + request.getRequestURI());

        // Record failed attempts for basic throttling.
        if (loginAttemptService != null) {
            loginAttemptService.recordFailure(request);
        }

        // Prevent user enumeration: do not distinguish "user not found" vs "wrong password".
        if (exception instanceof DisabledException) {
            response.sendRedirect("/login?error=disabled");
            return;
        }

        if (exception instanceof LockedException) {
            response.sendRedirect("/login?error=locked");
            return;
        }

        response.sendRedirect("/login?error=invalid");
    }
}
