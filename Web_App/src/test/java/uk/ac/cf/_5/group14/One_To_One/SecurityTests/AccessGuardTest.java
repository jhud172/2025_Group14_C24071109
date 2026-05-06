package uk.ac.cf._5.group14.One_To_One.SecurityTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import uk.ac.cf._5.group14.One_To_One.Security.AccessGuard;
import uk.ac.cf._5.group14.One_To_One.TrainerClient.TrainerClientLinkRepository;
import uk.ac.cf._5.group14.One_To_One.TrainerClient.TrainerClientLinkStatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccessGuardTest {

    @Mock
    private TrainerClientLinkRepository linkRepository;

    private AccessGuard accessGuard;

    @BeforeEach
    void setUp() {
        accessGuard = new AccessGuard(linkRepository);
    }

    @Test
    void canTrainerAccessClient_ShouldReturnTrue_WhenActiveLinkExists() {
        Long trainerId = 1L;
        Long clientId = 2L;
        when(linkRepository.existsByTrainerUserIdAndClientUserIdAndStatus(trainerId, clientId, TrainerClientLinkStatus.ACTIVE))
                .thenReturn(true);

        assertTrue(accessGuard.canTrainerAccessClient(trainerId, clientId));
    }

    @Test
    void canTrainerAccessClient_ShouldReturnFalse_WhenNoActiveLink() {
        Long trainerId = 1L;
        Long clientId = 2L;
        when(linkRepository.existsByTrainerUserIdAndClientUserIdAndStatus(trainerId, clientId, TrainerClientLinkStatus.ACTIVE))
                .thenReturn(false);

        assertFalse(accessGuard.canTrainerAccessClient(trainerId, clientId));
    }

    @Test
    void requireTrainerAccessClient_ShouldThrowException_WhenNoActiveLink() {
        Long trainerId = 1L;
        Long clientId = 2L;
        when(linkRepository.existsByTrainerUserIdAndClientUserIdAndStatus(trainerId, clientId, TrainerClientLinkStatus.ACTIVE))
                .thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> accessGuard.requireTrainerAccessClient(trainerId, clientId));
    }

    @Test
    void requireTrainerAccessClient_ShouldNotThrow_WhenActiveLinkExists() {
        Long trainerId = 1L;
        Long clientId = 2L;
        when(linkRepository.existsByTrainerUserIdAndClientUserIdAndStatus(trainerId, clientId, TrainerClientLinkStatus.ACTIVE))
                .thenReturn(true);

        assertDoesNotThrow(() -> accessGuard.requireTrainerAccessClient(trainerId, clientId));
    }

    @Test
    void requireOwnership_ShouldThrow_WhenIdsDoNotMatch() {
        assertThrows(AccessDeniedException.class, () -> accessGuard.requireOwnership(1L, 2L));
    }

    @Test
    void requireOwnership_ShouldPass_WhenIdsMatch() {
        assertDoesNotThrow(() -> accessGuard.requireOwnership(1L, 1L));
    }
}
