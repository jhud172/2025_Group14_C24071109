package uk.ac.cf._5.group14.One_To_One.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class PrivilegedAuditFilter extends OncePerRequestFilter {

    private static final Set<String> MUTATING_METHODS = Set.of("POST", "PUT", "PATCH", "DELETE");
    private static final Set<String> PRIVILEGED_PREFIXES = Set.of("/admin/", "/super-admin/", "/gym/");

    private final PrivilegedAuditService auditService;

    @Autowired
    public PrivilegedAuditFilter(ObjectProvider<PrivilegedAuditService> auditServiceProvider) {
        this.auditService = auditServiceProvider.getIfAvailable();
    }

    PrivilegedAuditFilter(PrivilegedAuditService auditService) {
        this.auditService = auditService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (auditService == null || !MUTATING_METHODS.contains(request.getMethod())) {
            return true;
        }
        String path = request.getRequestURI();
        return path == null || PRIVILEGED_PREFIXES.stream().noneMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String requestId = UUID.randomUUID().toString();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String actor = authentication == null ? "anonymous" : authentication.getName();
        String authorities = authentication == null
                ? ""
                : authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .sorted()
                        .collect(Collectors.joining(","));
        boolean failedWithException = false;

        try {
            filterChain.doFilter(request, response);
        } catch (ServletException | IOException | RuntimeException ex) {
            failedWithException = true;
            throw ex;
        } finally {
            int status = failedWithException && response.getStatus() < 400
                    ? HttpServletResponse.SC_INTERNAL_SERVER_ERROR
                    : response.getStatus();
            boolean succeeded = !failedWithException
                    && status < 400
                    && !isSecurityRedirect(response.getHeader("Location"));
            auditService.record(
                    requestId,
                    actor,
                    authorities,
                    request.getMethod(),
                    request.getRequestURI(),
                    status,
                    succeeded,
                    request.getRemoteAddr());
        }
    }

    private static boolean isSecurityRedirect(String location) {
        if (location == null || location.isBlank()) {
            return false;
        }
        try {
            String path = new URI(location).getPath();
            return "/access-denied".equals(path) || "/login".equals(path);
        } catch (URISyntaxException ex) {
            return false;
        }
    }
}
