package uk.ac.cf._5.group14.One_To_One.Users;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uk.ac.cf._5.group14.One_To_One.Users.User;
import uk.ac.cf._5.group14.One_To_One.Users.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JdbcTemplate jdbcTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jdbcTemplate = jdbcTemplate;
    }

    public User findByUsername(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }
        return userRepository.findByUsernameIgnoreCase(username.trim()).orElse(null);
    }
    // Inside UserService.java

    @Transactional
    public User saveUser(User user) {
        return saveUser(user, false);
    }

    @Transactional
    public User saveUser(User user, boolean passwordAlreadyEncoded) {
        if (user.getUsername() != null) {
            user.setUsername(user.getUsername().trim().toLowerCase());
        }
        if (user.getEmail() != null) {
            user.setEmail(user.getEmail().trim().toLowerCase());
        }
        if (!passwordAlreadyEncoded) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        user.setEnabled(true);
        user.setSubscriptionStatus(false);

        if (user.getRole() == null) {
            user.setRole(Role.CLIENT);
        }

        User savedUser = userRepository.save(user);

        // Keep legacy ROLE_USER for broad compatibility.
        assignRoleToUser(user.getUsername(), "USER");
        // Also assign the new primary role used by role-based dashboards.
        assignRoleToUser(user.getUsername(), user.getRole().name());

        return savedUser;
    }

    private void assignRoleToUser(String username, String roleName) {
        // Find the ID for the role (e.g., 'USER' -> 1)
        String findRoleSql = "SELECT role_id FROM roles WHERE name = ?";
        Integer roleId;

        try {
            roleId = jdbcTemplate.queryForObject(findRoleSql, Integer.class, roleName);
        } catch (Exception e) {
            // Safety net: If role doesn't exist, create it on the fly
            jdbcTemplate.update("INSERT INTO roles (name) VALUES (?)", roleName);
            roleId = jdbcTemplate.queryForObject(findRoleSql, Integer.class, roleName);
        }

        // Link user to role (idempotent)
        String insertSql = """
                INSERT INTO users_roles (username, role_id)
                SELECT ?, ?
                WHERE NOT EXISTS (
                    SELECT 1 FROM users_roles ur
                    WHERE ur.username = ?
                      AND ur.role_id = ?
                )
                """;
        jdbcTemplate.update(insertSql, username, roleId, username, roleId);
    }

    public boolean emailExists(String email) {
        if (email == null) {
            return false;
        }
        return userRepository.existsByEmailIgnoreCase(email.trim());
    }

    public boolean usernameExists(String username) {
        if (username == null) {
            return false;
        }
        return userRepository.existsByUsernameIgnoreCase(username.trim());
    }

    public User findByEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }
        return userRepository.findByEmailIgnoreCase(email.trim()).orElse(null);
    }

    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Transactional
    public void markTutorialSeen(User user) {
        if (user == null) {
            return;
        }
        user.setHasSeenTutorial(true);
        userRepository.save(user);
    }

    @Transactional
    public void updatePassword(User user, String rawPassword) {
        if (user == null || rawPassword == null) {
            return;
        }
        user.setPassword(passwordEncoder.encode(rawPassword));
        userRepository.save(user);
    }
}
