package uk.ac.cf._5.group14.One_To_One.Security;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class PrivilegedAuditFilterTest {

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void recordsSanitisedOutcomeForPrivilegedMutation() throws Exception {
        PrivilegedAuditService auditService = mock(PrivilegedAuditService.class);
        PrivilegedAuditFilter filter = new PrivilegedAuditFilter(auditService);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "operator",
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_PLATFORM_ADMIN"))));
        MockHttpServletRequest request = new MockHttpServletRequest("DELETE", "/admin/users/42");
        request.setQueryString("token=must-not-be-recorded");
        request.setRemoteAddr("192.0.2.30");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = (servletRequest, servletResponse) ->
                ((MockHttpServletResponse) servletResponse).setStatus(204);

        filter.doFilter(request, response, chain);

        verify(auditService).record(
                anyString(),
                org.mockito.ArgumentMatchers.eq("operator"),
                org.mockito.ArgumentMatchers.eq("ROLE_PLATFORM_ADMIN"),
                org.mockito.ArgumentMatchers.eq("DELETE"),
                org.mockito.ArgumentMatchers.eq("/admin/users/42"),
                org.mockito.ArgumentMatchers.eq(204),
                org.mockito.ArgumentMatchers.eq("192.0.2.30"));
    }

    @Test
    void ignoresReadOnlyAndNonPrivilegedRequests() throws Exception {
        PrivilegedAuditService auditService = mock(PrivilegedAuditService.class);
        PrivilegedAuditFilter filter = new PrivilegedAuditFilter(auditService);
        FilterChain chain = (request, response) -> {
        };

        filter.doFilter(
                new MockHttpServletRequest("GET", "/admin/dashboard"),
                new MockHttpServletResponse(),
                chain);
        filter.doFilter(
                new MockHttpServletRequest("POST", "/profile"),
                new MockHttpServletResponse(),
                chain);

        verify(auditService, never()).record(
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                org.mockito.ArgumentMatchers.anyInt(),
                anyString());
    }
}
