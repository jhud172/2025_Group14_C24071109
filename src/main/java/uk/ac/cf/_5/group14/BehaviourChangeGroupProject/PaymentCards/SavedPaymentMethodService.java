package uk.ac.cf._5.group14.BehaviourChangeGroupProject.PaymentCards;

import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.util.List;
import java.util.Optional;

public interface SavedPaymentMethodService {

    List<SavedPaymentMethod> getCardsForUser(Long userId);

    Optional<SavedPaymentMethod> findByIdForUser(Long id, Long userId);

    /**
     * Adds a new card for the user.
     * @param user           owner
     * @param cardHolderName name on card
     * @param fullCardNumber raw PAN – encrypted before storage
     * @param brand          Visa / Mastercard / Amex etc.
     * @param expiryMonth    1-12
     * @param expiryYear     4-digit year
     * @param makeDefault    whether to mark this card as default
     * @return the persisted entity
     */
    SavedPaymentMethod addCard(User user,
                               String cardHolderName,
                               String fullCardNumber,
                               String brand,
                               short expiryMonth,
                               short expiryYear,
                               boolean makeDefault);

    /**
     * Updates an existing card's non-sensitive fields.
     */
    SavedPaymentMethod updateCard(User user,
                                  Long cardId,
                                  String cardHolderName,
                                  String brand,
                                  short expiryMonth,
                                  short expiryYear,
                                  boolean makeDefault);

    void deleteCard(User user, Long cardId);

    void setDefault(User user, Long cardId);
}
