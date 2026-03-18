package uk.ac.cf._5.group14.One_To_One.PlatformBilling;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public PlatformPlan getPlan() {
        return plan;
    }

    public void setPlan(PlatformPlan plan) {
        this.plan = plan;
    }

    public PlatformSubscriptionStatus getStatus() {
        return status;
    }

    public void setStatus(PlatformSubscriptionStatus status) {
        this.status = status;
    }

    public Instant getCurrentPeriodEnd() {
        return currentPeriodEnd;
    }

    public void setCurrentPeriodEnd(Instant currentPeriodEnd) {
        this.currentPeriodEnd = currentPeriodEnd;
    }

    public boolean isCancelAtPeriodEnd() {
        return cancelAtPeriodEnd;
    }

    public void setCancelAtPeriodEnd(boolean cancelAtPeriodEnd) {
        this.cancelAtPeriodEnd = cancelAtPeriodEnd;
    }

    public String getProviderCustomerId() {
        return providerCustomerId;
    }

    public void setProviderCustomerId(String providerCustomerId) {
        this.providerCustomerId = providerCustomerId;
    }

    public String getProviderSubId() {
        return providerSubId;
    }

    public void setProviderSubId(String providerSubId) {
        this.providerSubId = providerSubId;
    }
}
