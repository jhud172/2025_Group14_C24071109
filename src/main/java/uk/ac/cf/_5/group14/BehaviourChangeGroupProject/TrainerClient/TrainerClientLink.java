package uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerClient;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "trainer_client_links")
public class TrainerClientLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_id", nullable = false)
    private Long clientUserId;

    @Column(name = "trainer_id", nullable = false)
    private Long trainerUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TrainerClientLinkStatus status;

    @Column(name = "requested_at")
    private Instant requestedAt;

    @Column(name = "activated_at")
    private Instant activatedAt;

    @Column(name = "paused_at")
    private Instant pausedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "ended_at")
    private Instant endedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "coaching_phase", length = 30)
    private CoachingPhase coachingPhase;

    @Column(name = "coaching_phase_label", length = 120)
    private String coachingPhaseLabel;

    @Column(name = "coaching_phase_started_at")
    private Instant coachingPhaseStartedAt;

    @Column(name = "coaching_phase_updated_at")
    private Instant coachingPhaseUpdatedAt;

    public TrainerClientLink() {
    }

    public TrainerClientLink(Long clientUserId, Long trainerUserId, TrainerClientLinkStatus status) {
        this.clientUserId = clientUserId;
        this.trainerUserId = trainerUserId;
        this.status = status;
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

    public Long getClientUserId() {
        return clientUserId;
    }

    public void setClientUserId(Long clientUserId) {
        this.clientUserId = clientUserId;
    }

    public Long getTrainerUserId() {
        return trainerUserId;
    }

    public void setTrainerUserId(Long trainerUserId) {
        this.trainerUserId = trainerUserId;
    }

    public TrainerClientLinkStatus getStatus() {
        return status;
    }

    public void setStatus(TrainerClientLinkStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(Instant requestedAt) {
        this.requestedAt = requestedAt;
    }

    public Instant getActivatedAt() {
        return activatedAt;
    }

    public void setActivatedAt(Instant activatedAt) {
        this.activatedAt = activatedAt;
    }

    public Instant getPausedAt() {
        return pausedAt;
    }

    public void setPausedAt(Instant pausedAt) {
        this.pausedAt = pausedAt;
    }

    public Instant getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(Instant endedAt) {
        this.endedAt = endedAt;
    }

    public CoachingPhase getCoachingPhase() {
        return coachingPhase;
    }

    public void setCoachingPhase(CoachingPhase coachingPhase) {
        this.coachingPhase = coachingPhase;
    }

    public String getCoachingPhaseLabel() {
        return coachingPhaseLabel;
    }

    public void setCoachingPhaseLabel(String coachingPhaseLabel) {
        this.coachingPhaseLabel = coachingPhaseLabel;
    }

    public Instant getCoachingPhaseStartedAt() {
        return coachingPhaseStartedAt;
    }

    public void setCoachingPhaseStartedAt(Instant coachingPhaseStartedAt) {
        this.coachingPhaseStartedAt = coachingPhaseStartedAt;
    }

    public Instant getCoachingPhaseUpdatedAt() {
        return coachingPhaseUpdatedAt;
    }

    public void setCoachingPhaseUpdatedAt(Instant coachingPhaseUpdatedAt) {
        this.coachingPhaseUpdatedAt = coachingPhaseUpdatedAt;
    }
}
