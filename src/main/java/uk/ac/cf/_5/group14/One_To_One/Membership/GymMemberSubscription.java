package uk.ac.cf._5.group14.One_To_One.Membership;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

/**
 * Represents a user's subscription to a gym membership product.
 */
@Entity
@Table(name = "gym_member_subscriptions",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "gym_id"}))
public class GymMemberSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull
    @Column(name = "gym_id", nullable = false)
    private Long gymId;

    @NotNull
    @Column(name = "product_id", nullable = false)
    private Long productId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SubscriptionStatus status = SubscriptionStatus.ACTIVE;

    @NotNull
    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @NotNull
    @Column(name = "renews_at", nullable = false)
    private Instant renewsAt;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public GymMemberSubscription() {
    }

    public GymMemberSubscription(Long userId, Long gymId, Long productId, Instant renewsAt) {
        this.userId = userId;
        this.gymId = gymId;
        this.productId = productId;
        this.startedAt = Instant.now();
        this.renewsAt = renewsAt;
    }

    @PrePersist
    void prePersist() {
        Instant now = Instant.now();
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getGymId() {
        return gymId;
    }

    public void setGymId(Long gymId) {
        this.gymId = gymId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public SubscriptionStatus getStatus() {
        return status;
    }

    public void setStatus(SubscriptionStatus status) {
        this.status = status;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getRenewsAt() {
        return renewsAt;
    }

    public void setRenewsAt(Instant renewsAt) {
        this.renewsAt = renewsAt;
    }

    public Instant getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(Instant cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
