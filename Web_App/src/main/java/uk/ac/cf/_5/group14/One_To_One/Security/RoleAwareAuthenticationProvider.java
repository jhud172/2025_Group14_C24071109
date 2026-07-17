package uk.ac.cf._5.group14.One_To_One.Security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import uk.ac.cf._5.group14.One_To_One.GymProfile.GymProfile;
import uk.ac.cf._5.group14.One_To_One.GymProfile.GymProfileRepository;
import uk.ac.cf._5.group14.One_To_One.GymProfile.GymProfileService;
import uk.ac.cf._5.group14.One_To_One.TrainerProfile.TrainerProfile;
import uk.ac.cf._5.group14.One_To_One.TrainerProfile.TrainerProfileRepository;
import uk.ac.cf._5.group14.One_To_One.TrainerProfile.TrainerProfileService;
import uk.ac.cf._5.group14.One_To_One.Users.Role;
import uk.ac.cf._5.group14.One_To_One.Users.User;
import uk.ac.cf._5.group14.One_To_One.Users.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Component
public class RoleAwareAuthenticationProvider implements AuthenticationProvider {

    private final UserRepository userRepository;
    private final TrainerProfileRepository trainerProfileRepository;
    private final GymProfileRepository gymProfileRepository;
    private final PasswordEncoder passwordEncoder;

    public RoleAwareAuthenticationProvider(UserRepository userRepository,
                                           TrainerProfileRepository trainerProfileRepository,
                                           GymProfileRepository gymProfileRepository,
                                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.trainerProfileRepository = trainerProfileRepository;
        this.gymProfileRepository = gymProfileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String identifier = authentication.getName() == null ? "" : authentication.getName().trim();
        String password = authentication.getCredentials() == null ? "" : authentication.getCredentials().toString();
        User user = resolveUser(identifier);

        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        if (!user.isEnabled() || !user.isEmailVerified()) {
            throw new DisabledException("Account disabled");
        }

        LoginRequestDetails details = authentication.getDetails() instanceof LoginRequestDetails loginRequestDetails
            ? loginRequestDetails
            : new LoginRequestDetails(null, null, null);

        validateSelectedRole(user, details.getLoginType());
        validateRoleSpecificCredentials(user, identifier, details);

        UserDetails principal = org.springframework.security.core.userdetails.User.builder()
            .username(user.getUsername())
            .password(user.getPassword())
            .disabled(false)
            .authorities(buildAuthorities(user))
            .build();

        UsernamePasswordAuthenticationToken authenticated = new UsernamePasswordAuthenticationToken(
            principal,
            authentication.getCredentials(),
            principal.getAuthorities()
        );
        authenticated.setDetails(authentication.getDetails());
        return authenticated;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private User resolveUser(String identifier) {
        if (identifier == null || identifier.isBlank()) {
            return null;
        }
        if (identifier.contains("@")) {
            return userRepository.findByEmailIgnoreCase(identifier).orElse(null);
        }
        return userRepository.findByUsernameIgnoreCase(identifier).orElse(null);
    }

    private void validateRoleSpecificCredentials(User user, String identifier, LoginRequestDetails details) {
        if (user.getRole() == Role.TRAINER) {
            String submittedTrainerCode = TrainerProfileService.normalizeTrainerCode(details.getTrainerCode());
            TrainerProfile profile = trainerProfileRepository.findByUserId(user.getId()).orElse(null);
            String expectedTrainerCode = profile == null
                ? null
                : TrainerProfileService.normalizeTrainerCode(profile.getTrainerCode());

            if (submittedTrainerCode == null || expectedTrainerCode == null || !expectedTrainerCode.equals(submittedTrainerCode)) {
                throw new BadCredentialsException("Invalid trainer code");
            }
            return;
        }

        if (user.getRole() == Role.GYM_ADMIN) {
            GymProfile profile = gymProfileRepository.findByUserId(user.getId()).orElse(null);
            String submittedGymSecretCode = GymProfileService.normalizeGymCode(details.getGymSecretCode());
            String expectedGymSecretCode = profile == null
                ? null
                : GymProfileService.normalizeGymCode(profile.getGymCode());

            if (!user.getUsername().equalsIgnoreCase(identifier)
                || submittedGymSecretCode == null
                || expectedGymSecretCode == null
                || !expectedGymSecretCode.equals(submittedGymSecretCode)) {
                throw new BadCredentialsException("Invalid gym credentials");
            }
        }
    }

    private void validateSelectedRole(User user, String submittedLoginType) {
        String loginType = submittedLoginType == null ? "client" : submittedLoginType.trim().toLowerCase();
        boolean matches = switch (loginType) {
            case "trainer" -> user.getRole() == Role.TRAINER;
            case "gym" -> user.getRole() == Role.GYM_ADMIN;
            case "client" -> user.getRole() == null
                    || user.getRole() == Role.CLIENT
                    || user.getRole() == Role.PLATFORM_ADMIN
                    || user.getRole() == Role.SUPER_ADMIN;
            default -> false;
        };

        if (!matches) {
            throw new BadCredentialsException("Invalid credentials for selected account type");
        }
    }

    private List<GrantedAuthority> buildAuthorities(User user) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        if (user.getRole() != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
        }
        return authorities;
    }
}
