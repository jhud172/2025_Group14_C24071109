package uk.ac.cf._5.group14.One_To_One.Users;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.ac.cf._5.group14.One_To_One.Security.CurrentUserResolver;

@Component
public class AuthHelper {

    private final CurrentUserResolver currentUserResolver;

    public AuthHelper() {
        this.currentUserResolver = null;
    }

    @Autowired
    public AuthHelper(CurrentUserResolver currentUserResolver) {
        this.currentUserResolver = currentUserResolver;
    }

    public User getAuthenticatedUser() {
        if (currentUserResolver != null) {
            User user = currentUserResolver.resolveCurrentUser();
            if (user != null) {
                return user;
            }
        }
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null || attributes.getRequest() == null) {
            return null;
        }
        return getAuthenticatedUser(attributes.getRequest().getSession(false));
    }

    public User getAuthenticatedUser(HttpSession session) {
        if (currentUserResolver != null) {
            User user = currentUserResolver.resolveCurrentUser(
                    uk.ac.cf._5.group14.One_To_One.Security.SecurityUtils.getAuthentication(),
                    session);
            if (user != null) {
                return user;
            }
        }
        if (session != null && session.getAttribute("user") instanceof User user) {
            return user;
        }
        return null;
    }

    public boolean isAuthenticated() {
        return getAuthenticatedUser() != null;
    }

    public boolean isAuthenticated(HttpSession session) {
        return getAuthenticatedUser(session) != null;
    }

    public void requireAuthentication() {
        if (!isAuthenticated()) {
            throw new SecurityException("User not authenticated");
        }
    }

    public Long getAuthenticatedUserId() {
        User user = getAuthenticatedUser();
        return user != null ? user.getId() : null;
    }
}
