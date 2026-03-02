package uk.ac.cf._5.group14.BehaviourChangeGroupProject.PaymentCards;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SavedPaymentMethodServiceImpl implements SavedPaymentMethodService {

    private static final int MAX_CARDS_PER_USER = 10;

    private final SavedPaymentMethodRepository repo;
    private final CardEncryptionService encryptionService;

    public SavedPaymentMethodServiceImpl(SavedPaymentMethodRepository repo,
                                         CardEncryptionService encryptionService) {
        this.repo = repo;
        this.encryptionService = encryptionService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SavedPaymentMethod> getCardsForUser(Long userId) {
        return repo.findByUserIdOrderByIsDefaultDescCreatedAtDesc(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SavedPaymentMethod> findByIdForUser(Long id, Long userId) {
        return repo.findByIdAndUserId(id, userId);
    }

    @Override
    public SavedPaymentMethod addCard(User user,
                                      String cardHolderName,
                                      String fullCardNumber,
                                      String brand,
                                      short expiryMonth,
                                      short expiryYear,
                                      boolean makeDefault) {
        if (repo.countByUserId(user.getId()) >= MAX_CARDS_PER_USER) {
            throw new IllegalStateException("Maximum of " + MAX_CARDS_PER_USER + " cards allowed per account.");
        }

        String cleaned = fullCardNumber.replaceAll("\\s+", "");
        String lastFour = cleaned.substring(cleaned.length() - 4);
        String encrypted = encryptionService.encrypt(cleaned);

        if (makeDefault) {
            repo.clearDefaultForUser(user.getId());
        }

        SavedPaymentMethod card = new SavedPaymentMethod();
        card.setUser(user);
        card.setCardHolderName(cardHolderName.trim());
        card.setLastFour(lastFour);
        card.setBrand(brand.trim());
        card.setExpiryMonth(expiryMonth);
        card.setExpiryYear(expiryYear);
        card.setEncryptedCardToken(encrypted);
        card.setDefault(makeDefault || repo.countByUserId(user.getId()) == 0);

        return repo.save(card);
    }

    @Override
    public SavedPaymentMethod updateCard(User user,
                                         Long cardId,
                                         String cardHolderName,
                                         String brand,
                                         short expiryMonth,
                                         short expiryYear,
                                         boolean makeDefault) {
        SavedPaymentMethod card = repo.findByIdAndUserId(cardId, user.getId())
                .orElseThrow(() -> new AccessDeniedException("Card not found"));

        card.setCardHolderName(cardHolderName.trim());
        card.setBrand(brand.trim());
        card.setExpiryMonth(expiryMonth);
        card.setExpiryYear(expiryYear);

        if (makeDefault) {
            repo.clearDefaultForUser(user.getId());
            card.setDefault(true);
        }

        return repo.save(card);
    }

    @Override
    public void deleteCard(User user, Long cardId) {
        SavedPaymentMethod card = repo.findByIdAndUserId(cardId, user.getId())
                .orElseThrow(() -> new AccessDeniedException("Card not found"));
        boolean wasDefault = card.isDefault();
        repo.delete(card);

        // If the deleted card was default, promote the oldest remaining card
        if (wasDefault) {
            List<SavedPaymentMethod> remaining =
                    repo.findByUserIdOrderByIsDefaultDescCreatedAtDesc(user.getId());
            if (!remaining.isEmpty()) {
                repo.clearDefaultForUser(user.getId());
                SavedPaymentMethod newDefault = remaining.get(0);
                newDefault.setDefault(true);
                repo.save(newDefault);
            }
        }
    }

    @Override
    public void setDefault(User user, Long cardId) {
        SavedPaymentMethod card = repo.findByIdAndUserId(cardId, user.getId())
                .orElseThrow(() -> new AccessDeniedException("Card not found"));
        repo.clearDefaultForUser(user.getId());
        card.setDefault(true);
        repo.save(card);
    }
}
