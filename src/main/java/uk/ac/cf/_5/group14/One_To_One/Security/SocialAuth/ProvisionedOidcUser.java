package uk.ac.cf._5.group14.One_To_One.Security.SocialAuth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

import java.util.Collection;

public class ProvisionedOidcUser extends DefaultOidcUser {

    private final String applicationUsername;

    public ProvisionedOidcUser(Collection<? extends GrantedAuthority> authorities,
                               OidcIdToken idToken,
                               OidcUserInfo userInfo,
                               String applicationUsername) {
        super(authorities, idToken, userInfo, IdTokenClaimNames.SUB);
        this.applicationUsername = applicationUsername;
    }

    public ProvisionedOidcUser(Collection<? extends GrantedAuthority> authorities,
                               OidcIdToken idToken,
                               String applicationUsername) {
        super(authorities, idToken, IdTokenClaimNames.SUB);
        this.applicationUsername = applicationUsername;
    }

    @Override
    public String getName() {
        return applicationUsername;
    }
}
