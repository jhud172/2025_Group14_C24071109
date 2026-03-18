package uk.ac.cf._5.group14.One_To_One.Security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static String getUsername(Authentication authentication) {
        if (authentication == null) {
            return null;
        }
        return authentication.getName();
    }

    public static boolean hasRole(Authentication authentication, String role) {
        if (authentication == null || role == null || role.isBlank()) {
            return false;
        }

        String required = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (required.equals(authority.getAuthority())) {
                return true;
            }
        }
        return false;
    }

    public static String getPrimaryRole(Authentication authentication) {
        if (hasRole(authentication, "SUPER_ADMIN")) return "SUPER_ADMIN";
        if (hasRole(authentication, "PLATFORM_ADMIN")) return "PLATFORM_ADMIN";
        if (hasRole(authentication, "GYM_ADMIN")) return "GYM_ADMIN";
        if (hasRole(authentication, "TRAINER")) return "TRAINER";
        if (hasRole(authentication, "CLIENT")) return "CLIENT";
        if (hasRole(authentication, "USER")) return "USER";
        return null;
    }
}
