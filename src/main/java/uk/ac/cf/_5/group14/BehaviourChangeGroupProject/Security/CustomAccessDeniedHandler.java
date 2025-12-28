package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        System.out.println("Access Denied Handler invoked: " + accessDeniedException.getMessage());
        System.out.println(request.getRequestURI());
        response.sendRedirect("/access-denied");
    }
}
