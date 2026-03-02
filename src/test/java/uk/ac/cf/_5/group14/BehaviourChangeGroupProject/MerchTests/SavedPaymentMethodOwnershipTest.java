package uk.ac.cf._5.group14.BehaviourChangeGroupProject.MerchTests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.PaymentCards.SavedPaymentMethod;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.PaymentCards.SavedPaymentMethodRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.PaymentCards.SavedPaymentMethodServiceImpl;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Verifies that a user cannot use, edit, or delete another user's saved payment method.
 */
@ExtendWith(MockitoExtension.class)
class SavedPaymentMethodOwnershipTest {

    @Mock
    private SavedPaymentMethodRepository repo;

    @InjectMocks
    private SavedPaymentMethodServiceImpl service;

    private User user(long id) {
        User u = new User();
        u.setId(id);
        return u;
    }

    @Test
    void findByIdForUser_returnsEmptyForWrongUser() {
        // Card 10 belongs to user 1
        when(repo.findByIdAndUserId(10L, 2L)).thenReturn(Optional.empty());

        Optional<SavedPaymentMethod> result = service.findByIdForUser(10L, 2L);

        assertTrue(result.isEmpty(), "Should not return a card belonging to another user");
    }

    @Test
    void deleteCard_throwsAccessDeniedForWrongUser() {
        // Card 10 belongs to user 1; user 2 tries to delete it
        when(repo.findByIdAndUserId(10L, 2L)).thenReturn(Optional.empty());

        assertThrows(AccessDeniedException.class,
                () -> service.deleteCard(user(2L), 10L));

        verify(repo, never()).delete(any());
    }

    @Test
    void updateCard_throwsAccessDeniedForWrongUser() {
        when(repo.findByIdAndUserId(10L, 2L)).thenReturn(Optional.empty());

        assertThrows(AccessDeniedException.class,
                () -> service.updateCard(user(2L), 10L, "Name", "Visa", (short) 1, (short) 2030, false));

        verify(repo, never()).save(any());
    }

    @Test
    void setDefault_throwsAccessDeniedForWrongUser() {
        when(repo.findByIdAndUserId(10L, 2L)).thenReturn(Optional.empty());

        assertThrows(AccessDeniedException.class,
                () -> service.setDefault(user(2L), 10L));

        verify(repo, never()).save(any());
    }

    @Test
    void addCard_requiresProviderToken() {
        User u = user(1L);
        when(repo.countByUserId(1L)).thenReturn(0);

        assertThrows(IllegalArgumentException.class,
                () -> service.addCard(u, "Name", "", "4242", "Visa", (short) 1, (short) 2030, false));

        verify(repo, never()).save(any());
    }

    @Test
    void addCard_requiresValidLastFour() {
        User u = user(1L);
        when(repo.countByUserId(1L)).thenReturn(0);

        assertThrows(IllegalArgumentException.class,
                () -> service.addCard(u, "Name", "tok_abc", "99", "Visa", (short) 1, (short) 2030, false));

        verify(repo, never()).save(any());
    }
}
