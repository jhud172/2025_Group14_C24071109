package uk.ac.cf._5.group14.One_To_One.Membership;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

/**
 * Audit log for membership product price changes.
 */
@Entity
@Table(name = "price_change_events")
public class PriceChangeEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "gym_id", nullable = false)
    private Long gymId;

    @NotNull
    @Column(name = "product_id", nullable = false)
    private Long productId;

    @NotNull
    @Column(name = "old_price_cents", nullable = false)
    private Integer oldPriceCents;

    @NotNull
    @Column(name = "new_price_cents", nullable = false)
    private Integer newPriceCents;

    @NotNull
    @Column(name = "effective_at", nullable = false)
    private Instant effectiveAt;

    @NotBlank
    @Size(max = 500)
    @Column(name = "reason", nullable = false, length = 500)
    private String reason;

    @NotNull
    @Column(name = "changed_by_user_id", nullable = false)
    private Long changedByUserId;

    @Column(name = "affected_member_count", nullable = false)
    private int affectedMemberCount = 0;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public PriceChangeEvent() {
    }

    public PriceChangeEvent(Long gymId, Long productId, Integer oldPriceCents, Integer newPriceCents,
                           Instant effectiveAt, String reason, Long changedByUserId) {
        this.gymId = gymId;
        this.productId = productId;
        this.oldPriceCents = oldPriceCents;
        this.newPriceCents = newPriceCents;
        this.effectiveAt = effectiveAt;
        this.reason = reason;
        this.changedByUserId = changedByUserId;
    }

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    // Getters and setters
    public Long getId() {
        return id;
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

    public Integer getOldPriceCents() {
        return oldPriceCents;
    }

    public void setOldPriceCents(Integer oldPriceCents) {
        this.oldPriceCents = oldPriceCents;
    }

    public Integer getNewPriceCents() {
        return newPriceCents;
    }

    public void setNewPriceCents(Integer newPriceCents) {
        this.newPriceCents = newPriceCents;
    }

    public Instant getEffectiveAt() {
        return effectiveAt;
    }

    public void setEffectiveAt(Instant effectiveAt) {
        this.effectiveAt = effectiveAt;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Long getChangedByUserId() {
        return changedByUserId;
    }

    public void setChangedByUserId(Long changedByUserId) {
        this.changedByUserId = changedByUserId;
    }

    public int getAffectedMemberCount() {
        return affectedMemberCount;
    }

    public void setAffectedMemberCount(int affectedMemberCount) {
        this.affectedMemberCount = affectedMemberCount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public double getOldPriceDollars() {
        return oldPriceCents / 100.0;
    }

    public double getNewPriceDollars() {
        return newPriceCents / 100.0;
    }
}
