package uk.ac.cf._5.group14.One_To_One.Users;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import uk.ac.cf._5.group14.One_To_One.GymProfile.GymProfile;
import uk.ac.cf._5.group14.One_To_One.GymProfile.GymProfileRepository;
import uk.ac.cf._5.group14.One_To_One.GymProfile.GymProfileService;

@Service
public class UserLookupService {

    private final UserRepository userRepository;
    private final GymProfileRepository gymProfileRepository;

    public UserLookupService(UserRepository userRepository,
                             GymProfileRepository gymProfileRepository) {
        this.userRepository = userRepository;
        this.gymProfileRepository = gymProfileRepository;
    }

    public User findByUsernameOrNull(String username) {
        return findByLoginIdentifier(username);
    }

    @Nullable
    public User findByLoginIdentifier(String identifier) {
        if (identifier == null) {
            return null;
        }
        String normalized = identifier.trim();
        if (normalized.isEmpty()) {
            return null;
        }

        User user = userRepository.findByUsernameIgnoreCase(normalized).orElse(null);
        if (user != null) {
            return user;
        }

        user = userRepository.findByEmailIgnoreCase(normalized).orElse(null);
        if (user != null) {
            return user;
        }

        String normalizedGymCode = GymProfileService.normalizeGymCode(normalized);
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
