package uk.ac.cf._5.group14.One_To_One.PlatformBilling;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "gym_subscriptions",
    indexes = {
        @Index(name = "idx_gym_subscriptions_user", columnList = "user_id"),
        @Index(name = "idx_gym_subscriptions_gym", columnList = "gym_id")
    })
@Getter
@Setter
public class GymSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "gym_id", nullable = false)
    private Long gymId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private GymSubscriptionStatus status = GymSubscriptionStatus.ACTIVE;

    @Column(name = "current_period_end")
    private Instant currentPeriodEnd;

    @Column(name = "cancel_at_period_end", nullable = false)
    private boolean cancelAtPeriodEnd = false;
}
