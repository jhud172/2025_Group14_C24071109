package uk.ac.cf._5.group14.One_To_One.Security.SocialAuth;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.ac.cf._5.group14.One_To_One.Users.Role;
import uk.ac.cf._5.group14.One_To_One.Users.User;

@Service
public class SocialOidcUserService extends OidcUserService {

    private final SocialAuthAccountService socialAuthAccountService;

    public SocialOidcUserService(SocialAuthAccountService socialAuthAccountService) {
        this.socialAuthAccountService = socialAuthAccountService;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        OidcUser oidcUser = super.loadUser(userRequest);
        HttpServletRequest request = currentRequest();
        Role requestedRole = socialAuthAccountService.requireRequestedRole(request);

        User user = socialAuthAccountService.resolveOrProvisionUser(
            userRequest.getClientRegistration().getRegistrationId(),
            requestedRole,
            oidcUser.getSubject(),
            firstPresent(oidcUser.getEmail(), stringClaim(oidcUser, "preferred_username")),
            stringClaim(oidcUser, "given_name"),
            stringClaim(oidcUser, "family_name"),
            oidcUser.getFullName(),
            stringClaim(oidcUser, "picture")
        );

        if (oidcUser.getUserInfo() != null) {
            return new ProvisionedOidcUser(
                socialAuthAccountService.buildAuthorities(user),
                oidcUser.getIdToken(),
                oidcUser.getUserInfo(),
                user.getUsername()
            );
        }

        return new ProvisionedOidcUser(
            socialAuthAccountService.buildAuthorities(user),
            oidcUser.getIdToken(),
            user.getUsername()
        );
    }

    private HttpServletRequest currentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new IllegalStateException("No active servlet request.");
        }
        return attributes.getRequest();
    }

    private String stringClaim(OidcUser oidcUser, String claimName) {
        Object value = oidcUser.getClaims().get(claimName);
        return value instanceof String stringValue ? stringValue : null;
    }

    private String firstPresent(String first, String second) {
        if (first != null && !first.isBlank()) {
            return first;
        }
        return second;
    }
}
