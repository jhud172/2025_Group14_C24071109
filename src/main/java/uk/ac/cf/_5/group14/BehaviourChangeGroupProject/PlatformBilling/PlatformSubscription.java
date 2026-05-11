package uk.ac.cf._5.group14.BehaviourChangeGroupProject.PlatformBilling;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "platform_subscriptions",
    indexes = {
        @Index(name = "idx_platform_subscriptions_user", columnList = "user_id")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_platform_subscription_user", columnNames = "user_id")
    })
@Getter
@Setter
public class PlatformSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan", nullable = false, length = 20)
    private PlatformPlan plan;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PlatformSubscriptionStatus status = PlatformSubscriptionStatus.ACTIVE;

    @Column(name = "current_period_end")
    private Instant currentPeriodEnd;

    @Column(name = "cancel_at_period_end", nullable = false)
    private boolean cancelAtPeriodEnd = false;

    @Column(name = "provider_customer_id")
    private String providerCustomerId;

    @Column(name = "provider_sub_id")
    private String providerSubId;
}
