package uk.ac.cf._5.group14.One_To_One.Security.SocialAuth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.cf._5.group14.One_To_One.TrainerProfile.TrainerProfileService;
import uk.ac.cf._5.group14.One_To_One.Users.Role;
import uk.ac.cf._5.group14.One_To_One.Users.SocialAuthProvider;
import uk.ac.cf._5.group14.One_To_One.Users.User;
import uk.ac.cf._5.group14.One_To_One.Users.UserRepository;
import uk.ac.cf._5.group14.One_To_One.Users.UserService;
import uk.ac.cf._5.group14.One_To_One.Users.UserSocialIdentity;
import uk.ac.cf._5.group14.One_To_One.Users.UserSocialIdentityRepository;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

@Service
public class SocialAuthAccountService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final UserService userService;
    private final UserSocialIdentityRepository userSocialIdentityRepository;
    private final TrainerProfileService trainerProfileService;

    public SocialAuthAccountService(UserRepository userRepository,
                                    UserService userService,
                                    UserSocialIdentityRepository userSocialIdentityRepository,
                                    TrainerProfileService trainerProfileService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.userSocialIdentityRepository = userSocialIdentityRepository;
        this.trainerProfileService = trainerProfileService;
    }

    public void storeRequestedRole(HttpServletRequest request, Role role) {
        request.getSession(true).setAttribute(SocialAuthContext.SESSION_ROLE_KEY, role.name());
    }

    public void clearRequestedRole(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(SocialAuthContext.SESSION_ROLE_KEY);
        }
    }

    public Role requireRequestedRole(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw socialAuthException("social_role_missing", "Choose client or trainer before continuing.");
        }

        Object rawValue = session.getAttribute(SocialAuthContext.SESSION_ROLE_KEY);
        if (!(rawValue instanceof String value)) {
            throw socialAuthException("social_role_missing", "Choose client or trainer before continuing.");
        }

        try {
            Role role = Role.valueOf(value);
            if (role != Role.CLIENT && role != Role.TRAINER) {
                throw socialAuthException("social_role_invalid", "Social sign-in is available for client and trainer accounts only.");
            }
            return role;
        } catch (IllegalArgumentException ex) {
            throw socialAuthException("social_role_invalid", "Social sign-in is available for client and trainer accounts only.");
        }
    }

    @Transactional
    public User resolveOrProvisionUser(String registrationId,
                                       Role requestedRole,
                                       String providerSubject,
                                       String email,
                                       String firstName,
                                       String lastName,
                                       String displayName,
                                       String profileImageUrl) {
        SocialAuthProvider provider = SocialAuthProvider.fromRegistrationId(registrationId);
        String normalizedSubject = trimToNull(providerSubject);
        if (normalizedSubject == null) {
            throw socialAuthException("social_subject_missing", "The social provider did not return a stable account identifier.");
        }

        UserSocialIdentity existingIdentity = userSocialIdentityRepository
            .findByProviderAndProviderSubject(provider, normalizedSubject)
            .orElse(null);

        if (existingIdentity != null) {
            User linkedUser = userRepository.findById(existingIdentity.getUserId())
                .orElseThrow(() -> socialAuthException("social_user_missing", "The linked account no longer exists."));
            validateExistingRole(linkedUser, requestedRole);
            updateUserFromProvider(linkedUser, firstName, lastName, email, profileImageUrl);
            saveIdentity(existingIdentity, email, displayName, profileImageUrl);
            return linkedUser;
        }

        String normalizedEmail = normalizeEmail(email);
        if (normalizedEmail == null) {
            throw socialAuthException("social_email_missing", "The social provider did not return an email address.");
        }

        User existingUser = userRepository.findByEmailIgnoreCase(normalizedEmail).orElse(null);
        if (existingUser != null) {
            validateExistingRole(existingUser, requestedRole);
            updateUserFromProvider(existingUser, firstName, lastName, normalizedEmail, profileImageUrl);
            linkIdentity(existingUser, provider, normalizedSubject, normalizedEmail, displayName, profileImageUrl);
            return existingUser;
        }

        String[] names = resolveNames(firstName, lastName, displayName, normalizedEmail, requestedRole);
        User newUser = new User(
            normalizedEmail,
            names[0],
            names[1],
            generateUniqueUsername(normalizedEmail, names[0], names[1], requestedRole),
            generateRandomPassword()
        );
        newUser.setRole(requestedRole);
        newUser.setEmailVerified(true);
        newUser.setEmailVerifiedAt(Instant.now());
        newUser.setProfileImageUrl(trimToNull(profileImageUrl));

        User savedUser = userService.saveUser(newUser);
        if (requestedRole == Role.TRAINER) {
            trainerProfileService.getOrCreateProfile(savedUser.getId());
        }
        linkIdentity(savedUser, provider, normalizedSubject, normalizedEmail, displayName, profileImageUrl);
        return savedUser;
    }

    public Collection<? extends GrantedAuthority> buildAuthorities(User user) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        if (user.getRole() != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
        }
        return authorities;
    }

    private void validateExistingRole(User user, Role requestedRole) {
        if (user.getRole() != requestedRole) {
            throw socialAuthException(
                "social_role_mismatch",
                "This social account is already linked to a " + user.getRole().name().toLowerCase(Locale.ROOT) + " account."
            );
        }
        if (user.getRole() != Role.CLIENT && user.getRole() != Role.TRAINER) {
            throw socialAuthException("social_role_unsupported", "Social sign-in is not available for this account type.");
        }
        if (!user.isEnabled()) {
            throw new DisabledException("Account disabled");
        }
    }

    private void updateUserFromProvider(User user,
                                        String firstName,
                                        String lastName,
                                        String email,
                                        String profileImageUrl) {
        boolean dirty = false;

        if ((user.getFirstName() == null || user.getFirstName().isBlank()) && trimToNull(firstName) != null) {
            user.setFirstName(firstName.trim());
            dirty = true;
        }
        if ((user.getLastName() == null || user.getLastName().isBlank()) && trimToNull(lastName) != null) {
            user.setLastName(lastName.trim());
            dirty = true;
        }
        String normalizedEmail = normalizeEmail(email);
        if (normalizedEmail != null && !normalizedEmail.equalsIgnoreCase(user.getEmail())) {
            user.setEmail(normalizedEmail);
            dirty = true;
        }
        if ((user.getProfileImageUrl() == null || user.getProfileImageUrl().isBlank()) && trimToNull(profileImageUrl) != null) {
            user.setProfileImageUrl(profileImageUrl.trim());
            dirty = true;
        }
        if (!user.isEmailVerified()) {
            user.setEmailVerified(true);
            user.setEmailVerifiedAt(Instant.now());
            dirty = true;
        }
        if (dirty) {
            userRepository.save(user);
        }
    }

    private void linkIdentity(User user,
                              SocialAuthProvider provider,
                              String providerSubject,
                              String email,
                              String displayName,
                              String profileImageUrl) {
        UserSocialIdentity identity = userSocialIdentityRepository.findByUserIdAndProvider(user.getId(), provider)
            .orElseGet(UserSocialIdentity::new);
        identity.setUserId(user.getId());
        identity.setProvider(provider);
        identity.setProviderSubject(providerSubject);
        saveIdentity(identity, email, displayName, profileImageUrl);
    }

    private void saveIdentity(UserSocialIdentity identity,
                              String email,
                              String displayName,
                              String profileImageUrl) {
        identity.setProviderEmail(normalizeEmail(email));
        identity.setProviderDisplayName(trimToNull(displayName));
        identity.setProfileImageUrl(trimToNull(profileImageUrl));
        userSocialIdentityRepository.save(identity);
    }

    private String generateUniqueUsername(String email,
                                          String firstName,
                                          String lastName,
                                          Role role) {
        String candidate = buildBaseUsername(email, firstName, lastName, role);
        String base = candidate;
        int suffix = 1;
        while (userRepository.existsByUsernameIgnoreCase(candidate)) {
            candidate = base + suffix;
            suffix++;
        }
        return candidate;
    }

    private String buildBaseUsername(String email, String firstName, String lastName, Role role) {
        String combined = (trimToNull(firstName) == null ? "" : firstName)
            + (trimToNull(lastName) == null ? "" : lastName);
        String normalized = combined.replaceAll("[^A-Za-z0-9_]", "").toLowerCase(Locale.ROOT);
        if (normalized.length() < 3 && email != null && email.contains("@")) {
            normalized = email.substring(0, email.indexOf('@')).replaceAll("[^A-Za-z0-9_]", "").toLowerCase(Locale.ROOT);
        }
        if (normalized.length() < 3) {
            normalized = role == Role.TRAINER ? "trainer" : "client";
        }
        if (normalized.length() > 18) {
            normalized = normalized.substring(0, 18);
        }
        return normalized;
    }

    private String generateRandomPassword() {
        String alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789!@#$%^&*";
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 24; i++) {
            builder.append(alphabet.charAt(RANDOM.nextInt(alphabet.length())));
        }
        return builder.toString();
    }

    private String[] resolveNames(String firstName,
                                  String lastName,
                                  String displayName,
                                  String email,
                                  Role role) {
        String resolvedFirst = trimToNull(firstName);
        String resolvedLast = trimToNull(lastName);

        if ((resolvedFirst == null || resolvedLast == null) && trimToNull(displayName) != null) {
            String[] parts = displayName.trim().split("\\s+", 2);
            if (resolvedFirst == null && parts.length > 0) {
                resolvedFirst = trimToNull(parts[0]);
            }
            if (resolvedLast == null && parts.length > 1) {
                resolvedLast = trimToNull(parts[1]);
            }
        }

        if (resolvedFirst == null && email != null && email.contains("@")) {
            resolvedFirst = email.substring(0, email.indexOf('@')).replaceAll("[^A-Za-z0-9]", "");
        }
        if (resolvedFirst == null) {
            resolvedFirst = role == Role.TRAINER ? "Trainer" : "Client";
        }
        if (resolvedLast == null) {
            resolvedLast = role == Role.TRAINER ? "Account" : "Member";
        }

        return new String[]{resolvedFirst, resolvedLast};
    }

    private String normalizeEmail(String email) {
        String trimmed = trimToNull(email);
        return trimmed == null ? null : trimmed.toLowerCase(Locale.ROOT);
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private OAuth2AuthenticationException socialAuthException(String code, String description) {
        return new OAuth2AuthenticationException(new OAuth2Error(code, description, null));
    }
}
