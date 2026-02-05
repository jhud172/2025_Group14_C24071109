package uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerClient;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "coaching_phase_changes")
public class CoachingPhaseChange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "link_id", nullable = false)
    private Long linkId;

    @Column(name = "trainer_id", nullable = false)
    private Long trainerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "old_phase", length = 30)
    private CoachingPhase oldPhase;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_phase", length = 30, nullable = false)
    private CoachingPhase newPhase;

    @Column(name = "old_label", length = 120)
    private String oldLabel;

    @Column(name = "new_label", length = 120)
    private String newLabel;

    @Column(name = "changed_at", nullable = false)
    private Instant changedAt;

    @Column(name = "notes", length = 800)
    private String notes;

    @PrePersist
    void prePersist() {
        if (changedAt == null) {
            changedAt = Instant.now();
        }
    }

    public Long getId() {
        return id;
    }

    public Long getLinkId() {
        return linkId;
    }

    public void setLinkId(Long linkId) {
        this.linkId = linkId;
    }

    public Long getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(Long trainerId) {
        this.trainerId = trainerId;
    }

    public CoachingPhase getOldPhase() {
        return oldPhase;
    }

    public void setOldPhase(CoachingPhase oldPhase) {
        this.oldPhase = oldPhase;
    }

    public CoachingPhase getNewPhase() {
        return newPhase;
    }

    public void setNewPhase(CoachingPhase newPhase) {
        this.newPhase = newPhase;
    }

    public String getOldLabel() {
        return oldLabel;
    }

    public void setOldLabel(String oldLabel) {
        this.oldLabel = oldLabel;
    }

    public String getNewLabel() {
        return newLabel;
    }

    public void setNewLabel(String newLabel) {
        this.newLabel = newLabel;
    }

    public Instant getChangedAt() {
        return changedAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
