package uk.ac.cf._5.group14.One_To_One.Security;

import java.io.IOException;
import java.net.URI;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import uk.ac.cf._5.group14.One_To_One.Users.User;
import uk.ac.cf._5.group14.One_To_One.Users.UserLookupService;

@Component
@Slf4j
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final RequestCache requestCache = new HttpSessionRequestCache();

    public CustomAuthenticationSuccessHandler() {
        setRequestCache(requestCache);
        setDefaultTargetUrl("/");
    }

    @Autowired
    private UserLookupService userLookupService;

    @Autowired(required = false)
    private LoginAttemptService loginAttemptService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {
        log.info("Authentication success for user {}", authentication.getName());

        if (loginAttemptService != null) {
            loginAttemptService.recordSuccess(request);
        }

        String username = authentication.getName();
        User user = userLookupService.findByLoginIdentifier(username);

        // First-login: redirect to the tutorial before the normal flow
        if (user != null && !user.isHasSeenTutorial()) {
            clearAuthenticationAttributes(request);
            requestCache.removeRequest(request, response);
            getRedirectStrategy().sendRedirect(request, response, "/tutorial");
            return;
        }

        String next = request.getParameter("next");
        if (isSafeRedirect(next)) {
            clearAuthenticationAttributes(request);
            getRedirectStrategy().sendRedirect(request, response, next);
            return;
        }

        SavedRequest savedRequest = requestCache.getRequest(request, response);
        if (savedRequest != null) {
            String redirectUrl = savedRequest.getRedirectUrl();
            if (redirectUrl != null && isStaticAssetUrl(redirectUrl)) {
                requestCache.removeRequest(request, response);
                clearAuthenticationAttributes(request);
                getRedirectStrategy().sendRedirect(request, response, "/");
                return;
            }

            if (redirectUrl != null && isChatApiUrl(redirectUrl)) {
                requestCache.removeRequest(request, response);
                clearAuthenticationAttributes(request);
                getRedirectStrategy().sendRedirect(request, response, "/");
                return;
            }
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }

    private static boolean isSafeRedirect(String next) {
        if (next == null || next.isBlank()) {
            return false;
        }
        if (!next.startsWith("/")) {
            return false;
        }
        return !next.startsWith("//");
    }

    private static boolean isStaticAssetUrl(String redirectUrl) {
        String path = redirectUrl;
        try {
            URI uri = URI.create(redirectUrl);
            if (uri.getPath() != null) {
                path = uri.getPath();
            }
        } catch (IllegalArgumentException ignored) {
            // If it's not a valid URI, fall back to raw string checks.
        }

        if (path == null) {
            return false;
        }

        return path.startsWith("/js/")
                || path.startsWith("/css/")
                || path.startsWith("/img/")
                || path.startsWith("/static/")
                || path.startsWith("/webjars/")
                || path.equals("/favicon.ico");
    }

    private static boolean isChatApiUrl(String redirectUrl) {
        String path = redirectUrl;
        try {
            URI uri = URI.create(redirectUrl);
            if (uri.getPath() != null) {
                path = uri.getPath();
            }
        } catch (IllegalArgumentException ignored) {
            // If it's not a valid URI, fall back to raw string checks.
        }

        if (path == null) {
            return false;
        }

        return path.startsWith("/chat/history")
                || path.startsWith("/chat/api")
                || path.startsWith("/chat/clear");
    }
}
