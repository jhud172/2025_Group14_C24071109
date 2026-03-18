package uk.ac.cf._5.group14.One_To_One.Reviews;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.Instant;

/**
 * Private assessment of a client by their trainer.
 * Never shown to client or other users - trainer-only.
 */
@Entity
@Table(name = "client_assessments",
       uniqueConstraints = @UniqueConstraint(columnNames = {"trainer_user_id", "client_user_id"}))
public class ClientAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trainer_user_id", nullable = false)
    private Long trainerId;

    @Column(name = "client_user_id", nullable = false)
    private Long clientId;

    @Min(1)
    @Max(5)
    @Column(name = "reliability_score")
    private Integer reliabilityScore;

    @Min(1)
    @Max(5)
    @Column(name = "communication_score")
    private Integer communicationScore;

    @Column(name = "private_notes", columnDefinition = "TEXT")
    private String privateNotes;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public ClientAssessment() {
    }

    public ClientAssessment(Long trainerId, Long clientId) {
        this.trainerId = trainerId;
        this.clientId = clientId;
    }

    @PrePersist
    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public Long getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(Long trainerId) {
        this.trainerId = trainerId;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Integer getReliabilityScore() {
        return reliabilityScore;
    }

    public void setReliabilityScore(Integer reliabilityScore) {
        this.reliabilityScore = reliabilityScore;
    }

    public Integer getCommunicationScore() {
        return communicationScore;
    }

    public void setCommunicationScore(Integer communicationScore) {
        this.communicationScore = communicationScore;
    }

    public String getPrivateNotes() {
        return privateNotes;
    }

    public void setPrivateNotes(String privateNotes) {
        this.privateNotes = privateNotes;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
