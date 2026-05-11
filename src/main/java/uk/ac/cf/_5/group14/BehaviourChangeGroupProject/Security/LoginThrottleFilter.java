package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class LoginThrottleFilter extends OncePerRequestFilter {

    @Autowired(required = false)
    private LoginAttemptService loginAttemptService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (isLoginPost(request)) {
            if (loginAttemptService != null && loginAttemptService.isBlocked(request)) {
                response.sendRedirect("/login?error=throttled");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isLoginPost(HttpServletRequest request) {
        if (!"POST".equalsIgnoreCase(request.getMethod())) return false;

        String path = request.getServletPath();
        if (path == null || path.isBlank()) {
            path = request.getRequestURI();
        }
        return "/login".equals(path);
    }
}
