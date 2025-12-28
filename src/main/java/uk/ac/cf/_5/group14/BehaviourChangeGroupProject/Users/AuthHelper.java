package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;


@Component
public class AuthHelper {

    public User getAuthenticatedUser() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes()).getRequest();
        HttpSession session = request.getSession(false);

        if (session != null) {
            return (User) session.getAttribute("user");
        }
        return null;
    }

    public User getAuthenticatedUser(HttpSession session) {
        if (session != null) {
            return (User) session.getAttribute("user");
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