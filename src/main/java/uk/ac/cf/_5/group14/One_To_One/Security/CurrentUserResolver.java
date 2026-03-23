package uk.ac.cf._5.group14.One_To_One.Security;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import uk.ac.cf._5.group14.One_To_One.Users.User;
import uk.ac.cf._5.group14.One_To_One.Users.UserService;

@Component
public class CurrentUserResolver {

    private final UserService userService;

    public CurrentUserResolver(UserService userService) {
        this.userService = userService;
    }

    public User resolveCurrentUser() {
        return resolveCurrentUser(SecurityUtils.getAuthentication(), null);
    }

    public User resolveCurrentUser(Authentication authentication) {
        return resolveCurrentUser(authentication, null);
    }

    public User resolveCurrentUser(Authentication authentication, HttpSession session) {
        if (SecurityUtils.isAuthenticated(authentication)) {
            return userService.findByUsername(SecurityUtils.getUsername(authentication));
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
