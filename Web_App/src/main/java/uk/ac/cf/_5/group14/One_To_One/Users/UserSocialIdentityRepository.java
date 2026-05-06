package uk.ac.cf._5.group14.One_To_One.Users;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSocialIdentityRepository extends JpaRepository<UserSocialIdentity, Long> {
    Optional<UserSocialIdentity> findByProviderAndProviderSubject(SocialAuthProvider provider, String providerSubject);
    Optional<UserSocialIdentity> findByUserIdAndProvider(Long userId, SocialAuthProvider provider);
}
