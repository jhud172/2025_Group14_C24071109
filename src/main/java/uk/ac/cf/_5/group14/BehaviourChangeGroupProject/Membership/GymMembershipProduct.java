package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Membership;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

/**
 * Represents a gym membership product offering.
 */
@Entity
@Table(name = "gym_membership_products")
public class GymMembershipProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "gym_id", nullable = false)
    private Long gymId;

    @NotBlank
    @Size(max = 200)
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Size(max = 1000)
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull
    @Min(0)
    @Column(name = "price_cents", nullable = false)
    private Integer priceCents;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "billing_period", nullable = false, length = 20)
    private BillingPeriod billingPeriod = BillingPeriod.MONTHLY;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public GymMembershipProduct() {
    }

    public GymMembershipProduct(Long gymId, String name, Integer priceCents) {
        this.gymId = gymId;
        this.name = name;
        this.priceCents = priceCents;
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

    public Long getGymId() {
        return gymId;
    }

    public void setGymId(Long gymId) {
        this.gymId = gymId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPriceCents() {
        return priceCents;
    }

    public void setPriceCents(Integer priceCents) {
        this.priceCents = priceCents;
    }

    public BillingPeriod getBillingPeriod() {
        return billingPeriod;
    }

    public void setBillingPeriod(BillingPeriod billingPeriod) {
        this.billingPeriod = billingPeriod;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Get price in dollars for display.
     */
    public double getPriceDollars() {
        return priceCents / 100.0;
    }
}
