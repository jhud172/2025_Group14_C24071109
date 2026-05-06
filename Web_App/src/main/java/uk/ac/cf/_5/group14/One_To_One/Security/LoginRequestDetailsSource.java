package uk.ac.cf._5.group14.One_To_One.Security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.stereotype.Component;

@Component
public class LoginRequestDetailsSource implements AuthenticationDetailsSource<HttpServletRequest, LoginRequestDetails> {

    @Override
    public LoginRequestDetails buildDetails(HttpServletRequest context) {
        return new LoginRequestDetails(
            trimToNull(context.getParameter("loginType")),
            trimToNull(context.getParameter("trainerCode")),
            trimToNull(context.getParameter("gymSecretCode"))
        );
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
