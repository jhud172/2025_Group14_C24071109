package uk.ac.cf._5.group14.BehaviourChangeGroupProject.UsersTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserService;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("test@test.com", "Test", "User", "testuser", "password");
    }

    @Test
    void saveUserToEncodePasswordAndSaveUserAndRole() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // mock JDBC calls for role assignment
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq("USER")))
                .thenReturn(1);

        userService.saveUser(user);

        verify(passwordEncoder).encode("password");
        verify(userRepository).save(user);

        verify(jdbcTemplate).update(contains("INSERT INTO users_roles"), eq("testuser"), eq(1));
    }

    @Test
    void emailExistsToReturnTrueIfEmailExists() {
        when(userRepository.existsByEmail("test@test.com")).thenReturn(true);
        assertTrue(userService.emailExists("test@test.com"));
    }
}
