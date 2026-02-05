package uk.ac.cf._5.group14.BehaviourChangeGroupProject.UsersTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Membership.EmailService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.PasswordResetService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.PasswordResetToken;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.PasswordResetTokenRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserService;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private EmailService emailService;

    private Clock clock;

    @InjectMocks
    private PasswordResetService passwordResetService;

    private Instant now;

    @BeforeEach
    void setUp() {
        now = Instant.parse("2026-01-25T10:15:30Z");
        clock = Clock.fixed(now, ZoneOffset.UTC);
        passwordResetService = new PasswordResetService(tokenRepository, userRepository, userService, emailService, clock);
    }

    @Test
    void getValidToken_returnsEmptyWhenExpired() {
        PasswordResetToken token = new PasswordResetToken();
        token.setToken("expired-token");
        token.setExpiresAt(now.minusSeconds(1));
        token.setUsedAt(null);

        when(tokenRepository.findByToken("expired-token")).thenReturn(Optional.of(token));

        assertTrue(passwordResetService.getValidToken("expired-token").isEmpty());
    }

    @Test
    void resetPassword_singleUse() {
        PasswordResetToken token = new PasswordResetToken();
        token.setToken("valid-token");
        token.setUserId(10L);
        token.setExpiresAt(now.plusSeconds(1200));
        token.setUsedAt(null);

        User user = new User();
        user.setId(10L);

        when(tokenRepository.findByToken("valid-token")).thenReturn(Optional.of(token));
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));

        assertTrue(passwordResetService.resetPassword("valid-token", "NewPassword1!"));
        assertNotNull(token.getUsedAt());
        verify(userService, times(1)).updatePassword(user, "NewPassword1!");

        assertFalse(passwordResetService.resetPassword("valid-token", "NewPassword1!"));
        verify(userService, times(1)).updatePassword(user, "NewPassword1!");
    }
}
