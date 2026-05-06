package uk.ac.cf._5.group14.One_To_One.DevMode;

import java.io.IOException;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import uk.ac.cf._5.group14.One_To_One.Config.DevModeProperties;

@Component
public class DevModePageRestrictionFilter extends OncePerRequestFilter {

    @Nullable
    private final DevModeProperties devModeProperties;
    @Nullable
    private final DevModePageAccessService pageAccessService;

    public DevModePageRestrictionFilter(ObjectProvider<DevModeProperties> devModeProperties,
                                        ObjectProvider<DevModePageAccessService> pageAccessService) {
        this.devModeProperties = devModeProperties.getIfAvailable();
        this.pageAccessService = pageAccessService.getIfAvailable();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (devModeProperties == null || !devModeProperties.isDevMode() || pageAccessService == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String requestPath = normalisePath(request);
        if (requestPath.startsWith("/dev-mode/restricted")) {
            filterChain.doFilter(request, response);
            return;
        }

        DevModePageAccessService.RestrictedRedirect redirect =
                pageAccessService.resolveRestrictedRedirect(requestPath).orElse(null);
        if (redirect != null) {
            response.sendRedirect(request.getContextPath() + redirect.redirectPath());
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String normalisePath(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String path = (contextPath != null && !contextPath.isBlank() && requestUri.startsWith(contextPath))
                ? requestUri.substring(contextPath.length())
                : requestUri;
        if (path == null || path.isBlank()) {
            return "/";
        }
        if (path.length() > 1 && path.endsWith("/")) {
            return path.substring(0, path.length() - 1);
        }
        return path;
    }
}
