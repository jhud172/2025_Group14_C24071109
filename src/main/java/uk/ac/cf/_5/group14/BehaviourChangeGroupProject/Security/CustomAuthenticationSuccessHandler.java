package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserService;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {
        System.out.println("Authentication Success Handler invoked: " + authentication.getName());
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        request.getSession().setAttribute("user", user);
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
