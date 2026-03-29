package uk.ac.cf._5.group14.One_To_One.Security;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import uk.ac.cf._5.group14.One_To_One.Users.User;
import uk.ac.cf._5.group14.One_To_One.Users.UserLookupService;

@Component
public class CurrentUserResolver {

    private final UserLookupService userLookupService;

    public CurrentUserResolver(UserLookupService userLookupService) {
        this.userLookupService = userLookupService;
    }

    public User resolveCurrentUser() {
        return resolveCurrentUser(SecurityUtils.getAuthentication(), null);
    }

    public User resolveCurrentUser(Authentication authentication) {
        return resolveCurrentUser(authentication, null);
    }

    public User resolveCurrentUser(Authentication authentication, HttpSession session) {
        if (SecurityUtils.isAuthenticated(authentication)) {
            User user = userLookupService.findByLoginIdentifier(SecurityUtils.getUsername(authentication));
            if (user != null) {
                return user;
            }
        }
        if (session != null) {
            Object sessionUser = session.getAttribute("user");
            if (sessionUser instanceof User user) {
                return user;
            }
        }
        return null;
    }

    public User requireCurrentUser() {
        return requireCurrentUser(SecurityUtils.getAuthentication(), null);
    }

    public User requireCurrentUser(Authentication authentication) {
        return requireCurrentUser(authentication, null);
    }

    public User requireCurrentUser(Authentication authentication, HttpSession session) {
        User user = resolveCurrentUser(authentication, session);
        if (user == null) {
            throw new AccessDeniedException("Authenticated user not found");
        }
        return user;
    }
}
