package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserRepository;

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
        return userRepository.findByUsername(username).orElse(null);
    }
    // Inside UserService.java

    @Transactional
    public User saveUser(User user) {
        // password encoder
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(true);
        user.setSubscriptionStatus(false);

        User savedUser = userRepository.save(user);

        assignRoleToUser(user.getUsername(), "USER");

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

        // Link user to role
        String insertSql = "INSERT INTO users_roles (username, role_id) VALUES (?, ?)";
        jdbcTemplate.update(insertSql, username, roleId);
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}