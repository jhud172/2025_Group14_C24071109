package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseLog.ExerciseLog;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.HealthDataInput.HealthRecord;

import java.time.Instant;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Please enter your first name.")
    @Size(max = 100, message = "First name cannot exceed 100 characters")
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @NotBlank(message = "Please enter a valid email address.")
    @Email(message = "Please enter a valid email address.")
    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false;

    @Column(name = "email_verified_at")
    private Instant emailVerifiedAt;

    @Column(name = "phone_number", length = 30)
    private String phoneNumber;

    @Column(name = "phone_verified", nullable = false)
    private boolean phoneVerified = false;

    @Column(name = "phone_verified_at")
    private Instant phoneVerifiedAt;

    @NotBlank(message = "Please enter your last name.")
    @Size(max = 100, message = "Last name cannot exceed 100 characters")
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @NotBlank(message = "Please enter a username")
    @Size(min = 3, max = 100, message = "Your username must be between 3 and 100 characters")
    @Column(name = "username", nullable = false, length = 100)
    private String username;

    @Column(name = "username_changed_at")
    private Instant usernameChangedAt;

    @Column(name = "bio", length = 800)
    private String bio;

    @Column(name = "profile_image_url", length = 300)
    private String profileImageUrl;

    @NotBlank(message = "Please enter a password.")
    @Size(min = 8, message = "Your password must be at least 8 characters long")
    @Column(name = "password", nullable = false, length = 500)
    private String password;

    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    @Column(name = "subscription_status", nullable = false)
    private boolean subscriptionStatus = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    private Role role = Role.CLIENT;

    @Column(name = "gym_id")
    private Long gymId;

    @Column(name = "trainer_profile_id")
    private Long trainerProfileId;

    @Column(name = "trainer_verified", nullable = false)
    private boolean trainerVerified = false;

    @Setter(AccessLevel.NONE)
    @Column(name = "public_id", nullable = false, length = 36, unique = true, updatable = false)
    private String publicId;

    @OneToMany(mappedBy = "user")
    private List<HealthRecord> healthRecords = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<ExerciseLog> exerciseLogs = new ArrayList<>();

    // ===== Notes and Gamification =====
    /**
     * A user can have many note folders. These are removed when the user is removed.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<uk.ac.cf._5.group14.BehaviourChangeGroupProject.Notes.NoteFolder> noteFolders = new ArrayList<>();

    /**
     * A user can have many notes. Notes are removed when the user is removed.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<uk.ac.cf._5.group14.BehaviourChangeGroupProject.Notes.Note> notes = new ArrayList<>();

    /**
     * Holds the user's accumulated points and level for gamification.
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private uk.ac.cf._5.group14.BehaviourChangeGroupProject.Level.LevelProgress levelProgress;

    public User() {}

    public User(String email, String firstName, String lastName,
                String username, String password) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.enabled = true;
        this.subscriptionStatus = false;
        this.role = Role.CLIENT;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @PrePersist
    void prePersist() {
        if (publicId == null || publicId.isBlank()) {
            publicId = java.util.UUID.randomUUID().toString();
        }
    }
}