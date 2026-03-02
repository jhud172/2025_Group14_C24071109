package uk.ac.cf._5.group14.BehaviourChangeGroupProject.PaymentCards;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.Instant;

@Entity
@Table(name = "saved_payment_methods")
@Getter
@Setter
public class SavedPaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "card_holder_name", nullable = false, length = 200)
    private String cardHolderName;

    /** Last four digits of the card – stored in plaintext for display. */
    @Column(name = "last_four", nullable = false, length = 4)
    private String lastFour;

    /** Card brand/network e.g. Visa, Mastercard, Amex. */
    @Column(name = "brand", nullable = false, length = 50)
    private String brand;

    @Column(name = "expiry_month", nullable = false)
    private short expiryMonth;

    @Column(name = "expiry_year", nullable = false)
    private short expiryYear;

    /**
     * AES-GCM encrypted token that holds the full card number (PAN).
     * Never returned to the client.
     */
    @Column(name = "encrypted_card_token", nullable = false, columnDefinition = "TEXT")
    private String encryptedCardToken;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void onCreate() {
        Instant now = Instant.now();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = Instant.now();
    }
}
