package uk.ac.cf._5.group14.One_To_One.GymProfile;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.time.Instant;

@Entity
@Table(name = "gym_profiles")
public class GymProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Size(max = 120)
    @Column(name = "gym_name", length = 120, nullable = false)
    private String gymName;

    @Size(max = 16)
    @Column(name = "gym_code", length = 16, unique = true)
    private String gymCode;

    @Size(max = 200)
    @Column(name = "address", length = 200)
    private String address;

    @Size(max = 120)
    @Column(name = "city", length = 120)
    private String city;

    @Size(max = 120)
    @Column(name = "contact_name", length = 120)
    private String contactName;

    @Size(max = 40)
    @Column(name = "contact_phone", length = 40)
    private String contactPhone;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public GymProfile() {
    }

    public GymProfile(Long userId, String gymName) {
        this.userId = userId;
        this.gymName = gymName;
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

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getGymName() {
        return gymName;
    }

    public void setGymName(String gymName) {
        this.gymName = gymName;
    }

    public String getGymCode() {
        return gymCode;
    }

    public void setGymCode(String gymCode) {
        this.gymCode = gymCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
