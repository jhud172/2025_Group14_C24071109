package uk.ac.cf._5.group14.One_To_One.Security.SocialAuth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public class ProvisionedOAuth2User implements OAuth2User {

    private final Collection<? extends GrantedAuthority> authorities;
    private final Map<String, Object> attributes;
    private final String applicationUsername;

    public ProvisionedOAuth2User(Collection<? extends GrantedAuthority> authorities,
                                 Map<String, Object> attributes,
                                 String applicationUsername) {
        this.authorities = authorities;
        this.attributes = attributes;
        this.applicationUsername = applicationUsername;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return applicationUsername;
    }
}
