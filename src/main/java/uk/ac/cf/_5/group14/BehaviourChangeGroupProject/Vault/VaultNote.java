package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Vault;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "vault_notes")
public class VaultNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "note_type", nullable = false, length = 20)
    private VaultNoteType noteType;

    @Column(name = "title", nullable = false, length = 120)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "linked_date")
    private LocalDate linkedDate;

    @Column(name = "linked_workout_session_id")
    private Long linkedWorkoutSessionId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public VaultNote() {
    }

    public VaultNote(Long userId, VaultNoteType noteType, String title, String content) {
        this.userId = userId;
        this.noteType = noteType;
        this.title = title;
        this.content = content;
    }

    @PrePersist
    void prePersist() {
        Instant now = Instant.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
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

    public VaultNoteType getNoteType() {
        return noteType;
    }

    public void setNoteType(VaultNoteType noteType) {
        this.noteType = noteType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDate getLinkedDate() {
        return linkedDate;
    }

    public void setLinkedDate(LocalDate linkedDate) {
        this.linkedDate = linkedDate;
    }

    public Long getLinkedWorkoutSessionId() {
        return linkedWorkoutSessionId;
    }

    public void setLinkedWorkoutSessionId(Long linkedWorkoutSessionId) {
        this.linkedWorkoutSessionId = linkedWorkoutSessionId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
