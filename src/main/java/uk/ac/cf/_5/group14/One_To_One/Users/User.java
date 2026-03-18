package uk.ac.cf._5.group14.One_To_One.Users;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

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
import uk.ac.cf._5.group14.One_To_One.ExerciseLog.ExerciseLog;
import uk.ac.cf._5.group14.One_To_One.HealthDataInput.HealthRecord;

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

    @Column(name = "phone_country", length = 2)
    private String phoneCountry = "GB";

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

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

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

    @Column(name = "has_seen_tutorial", nullable = false)
    private boolean hasSeenTutorial = false;

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
    private List<uk.ac.cf._5.group14.One_To_One.Notes.NoteFolder> noteFolders = new ArrayList<>();

    /**
     * A user can have many notes. Notes are removed when the user is removed.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<uk.ac.cf._5.group14.One_To_One.Notes.Note> notes = new ArrayList<>();

    /**
     * Holds the user's accumulated points and level for gamification.
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private uk.ac.cf._5.group14.One_To_One.Level.LevelProgress levelProgress;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public Instant getEmailVerifiedAt() {
        return emailVerifiedAt;
    }

    public void setEmailVerifiedAt(Instant emailVerifiedAt) {
        this.emailVerifiedAt = emailVerifiedAt;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneCountry() {
        return phoneCountry;
    }

    public void setPhoneCountry(String phoneCountry) {
        this.phoneCountry = phoneCountry;
    }

    public boolean isPhoneVerified() {
        return phoneVerified;
    }

    public void setPhoneVerified(boolean phoneVerified) {
        this.phoneVerified = phoneVerified;
    }

    public Instant getPhoneVerifiedAt() {
        return phoneVerifiedAt;
    }

    public void setPhoneVerifiedAt(Instant phoneVerifiedAt) {
        this.phoneVerifiedAt = phoneVerifiedAt;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Instant getUsernameChangedAt() {
        return usernameChangedAt;
    }

    public void setUsernameChangedAt(Instant usernameChangedAt) {
        this.usernameChangedAt = usernameChangedAt;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isSubscriptionStatus() {
        return subscriptionStatus;
    }

    public void setSubscriptionStatus(boolean subscriptionStatus) {
        this.subscriptionStatus = subscriptionStatus;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Long getGymId() {
        return gymId;
    }

    public void setGymId(Long gymId) {
        this.gymId = gymId;
    }

    public Long getTrainerProfileId() {
        return trainerProfileId;
    }

    public void setTrainerProfileId(Long trainerProfileId) {
        this.trainerProfileId = trainerProfileId;
    }

    public boolean isTrainerVerified() {
        return trainerVerified;
    }

    public void setTrainerVerified(boolean trainerVerified) {
        this.trainerVerified = trainerVerified;
    }

    public boolean isHasSeenTutorial() {
        return hasSeenTutorial;
    }

    public void setHasSeenTutorial(boolean hasSeenTutorial) {
        this.hasSeenTutorial = hasSeenTutorial;
    }

    public String getPublicId() {
        return publicId;
    }

    public List<HealthRecord> getHealthRecords() {
        return healthRecords;
    }

    public void setHealthRecords(List<HealthRecord> healthRecords) {
        this.healthRecords = healthRecords;
    }

    public List<ExerciseLog> getExerciseLogs() {
        return exerciseLogs;
    }

    public void setExerciseLogs(List<ExerciseLog> exerciseLogs) {
        this.exerciseLogs = exerciseLogs;
    }

    @PrePersist
    void prePersist() {
        if (publicId == null || publicId.isBlank()) {
            publicId = java.util.UUID.randomUUID().toString();
        }
    }
}
