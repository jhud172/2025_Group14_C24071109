package uk.ac.cf._5.group14.One_To_One.Security.SocialAuth;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.ac.cf._5.group14.One_To_One.Users.Role;
import uk.ac.cf._5.group14.One_To_One.Users.User;

@Service
public class SocialOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
    private final SocialAuthAccountService socialAuthAccountService;

    public SocialOAuth2UserService(SocialAuthAccountService socialAuthAccountService) {
        this.socialAuthAccountService = socialAuthAccountService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oauth2User = delegate.loadUser(userRequest);
        HttpServletRequest request = currentRequest();
        Role requestedRole = socialAuthAccountService.requireRequestedRole(request);

        User user = socialAuthAccountService.resolveOrProvisionUser(
            userRequest.getClientRegistration().getRegistrationId(),
            requestedRole,
            oauth2User.getName(),
            attribute(oauth2User, "email"),
            attribute(oauth2User, "given_name"),
            attribute(oauth2User, "family_name"),
            attribute(oauth2User, "name"),
            attribute(oauth2User, "picture")
        );

        return new ProvisionedOAuth2User(
            socialAuthAccountService.buildAuthorities(user),
            oauth2User.getAttributes(),
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

    private String attribute(OAuth2User oauth2User, String key) {
        Object value = oauth2User.getAttributes().get(key);
        return value instanceof String stringValue ? stringValue : null;
    }
}
