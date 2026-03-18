package uk.ac.cf._5.group14.One_To_One.Security;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import uk.ac.cf._5.group14.One_To_One.Users.User;
import uk.ac.cf._5.group14.One_To_One.Users.UserService;

@Component
public class PostLoginSessionInitializer implements ApplicationListener<InteractiveAuthenticationSuccessEvent> {

    private final UserService userService;
    private final ObjectProvider<LoginAttemptService> loginAttemptServiceProvider;

    public PostLoginSessionInitializer(
            UserService userService,
            ObjectProvider<LoginAttemptService> loginAttemptServiceProvider
    ) {
        this.userService = userService;
        this.loginAttemptServiceProvider = loginAttemptServiceProvider;
    }

    @Override
    public void onApplicationEvent(InteractiveAuthenticationSuccessEvent event) {
        Authentication authentication = event.getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            return;
        }

        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (!(attrs instanceof ServletRequestAttributes servletAttrs)) {
            return;
        }

        HttpServletRequest request = servletAttrs.getRequest();
        if (request == null) {
            return;
        }

        LoginAttemptService loginAttemptService = loginAttemptServiceProvider.getIfAvailable();
        if (loginAttemptService != null) {
            loginAttemptService.recordSuccess(request);
        }

        HttpSession session = request.getSession(true);
        if (session.getAttribute("user") != null) {
            return;
        }

        User user = userService.findByUsername(authentication.getName());
        session.setAttribute("user", user);
    }
}
