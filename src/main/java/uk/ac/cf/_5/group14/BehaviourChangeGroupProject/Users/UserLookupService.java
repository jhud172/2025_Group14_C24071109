package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users;

import org.springframework.stereotype.Service;

@Service
public class UserLookupService {

    private final UserRepository userRepository;

    public UserLookupService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findByUsernameOrNull(String username) {
        if (username == null) {
            return null;
        }
        String normalized = username.trim();
        if (normalized.isEmpty()) {
            return null;
        }
        return userRepository.findByUsername(normalized).orElse(null);
    }
}
