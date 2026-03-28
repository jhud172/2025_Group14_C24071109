package uk.ac.cf._5.group14.One_To_One.Users;

import org.springframework.lang.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import uk.ac.cf._5.group14.One_To_One.GymProfile.GymProfile;
import uk.ac.cf._5.group14.One_To_One.GymProfile.GymProfileRepository;
import uk.ac.cf._5.group14.One_To_One.GymProfile.GymProfileService;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final GymProfileRepository gymProfileRepository;

    public CustomUserDetailsService(UserRepository userRepository,
                                    GymProfileRepository gymProfileRepository) {
        this.userRepository = userRepository;
        this.gymProfileRepository = gymProfileRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        if (identifier == null || identifier.isBlank()) {
            throw new UsernameNotFoundException("User not found");
        }
        String trimmed = identifier.trim();
        User user = resolveUser(trimmed);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        if (user.getRole() != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .disabled(!user.isEnabled() || !user.isEmailVerified())
                .authorities(authorities)
                .build();
    }

    @Nullable
    private User resolveUser(String identifier) {
        User user = identifier.contains("@")
                ? userRepository.findByEmailIgnoreCase(identifier).orElse(null)
                : userRepository.findByUsernameIgnoreCase(identifier).orElse(null);

        if (user != null) {
            return user;
        }

        user = userRepository.findByUsernameIgnoreCase(identifier).orElse(null);
        if (user != null) {
            return user;
        }

        String normalizedGymCode = GymProfileService.normalizeGymCode(identifier);
        if (normalizedGymCode == null) {
            return null;
        }

        GymProfile gymProfile = gymProfileRepository.findByGymCodeIgnoreCase(normalizedGymCode).orElse(null);
        if (gymProfile == null) {
            return null;
        }

        return userRepository.findById(gymProfile.getUserId()).orElse(null);
    }
}
