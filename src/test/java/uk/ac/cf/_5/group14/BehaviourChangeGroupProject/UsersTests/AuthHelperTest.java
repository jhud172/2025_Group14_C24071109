package uk.ac.cf._5.group14.BehaviourChangeGroupProject.UsersTests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import static org.junit.jupiter.api.Assertions.*;

class AuthHelperTest {

    private AuthHelper authHelper;
    private MockHttpServletRequest request;
    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        authHelper = new AuthHelper();
        request = new MockHttpServletRequest();
        session = new MockHttpSession();
        request.setSession(session);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @AfterEach
    void reset() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void getAuthenticatedUserToReturnUserDuringSession() {
        User user = new User();
        user.setUsername("testuser");
        session.setAttribute("user", user);

        User result = authHelper.getAuthenticatedUser();
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void getAuthenticatedUserToReturnNullDuringNoSession() {
        User result = authHelper.getAuthenticatedUser();
        assertNull(result);
    }

    @Test
    void requireAuthenticationToThrowExceptionWhenNotAuthenticated() {
        assertThrows(SecurityException.class, () -> authHelper.requireAuthentication());
    }
}
