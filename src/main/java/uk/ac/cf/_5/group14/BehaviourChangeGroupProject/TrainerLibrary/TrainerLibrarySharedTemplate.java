package uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerLibrary;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

@Entity
@Table(name = "trainer_library_shared_templates")
public class TrainerLibrarySharedTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "trainer_id", nullable = false)
    private Long trainerId;

    @NotNull
    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "template_type", nullable = false, length = 20)
    private TrainerLibraryTemplateType templateType;

    @NotNull
    @Column(name = "template_id", nullable = false)
    private Long templateId;

    @Column(name = "shared_at", nullable = false)
    private Instant sharedAt;

    public TrainerLibrarySharedTemplate() {
    }

    public TrainerLibrarySharedTemplate(Long trainerId, Long clientId, TrainerLibraryTemplateType templateType, Long templateId) {
        this.trainerId = trainerId;
        this.clientId = clientId;
        this.templateType = templateType;
        this.templateId = templateId;
    }

    @PrePersist
    void prePersist() {
        if (sharedAt == null) {
            sharedAt = Instant.now();
        }
    }

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

    public TrainerLibraryTemplateType getTemplateType() {
        return templateType;
    }

    public void setTemplateType(TrainerLibraryTemplateType templateType) {
        this.templateType = templateType;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public Instant getSharedAt() {
        return sharedAt;
    }
}
