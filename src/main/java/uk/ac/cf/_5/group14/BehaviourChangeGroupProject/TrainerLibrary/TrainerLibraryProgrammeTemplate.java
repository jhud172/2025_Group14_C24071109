package uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerLibrary;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

@Entity
@Table(name = "trainer_library_programme_templates")
public class TrainerLibraryProgrammeTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "trainer_id", nullable = false)
    private Long trainerId;

    @NotBlank
    @Size(max = 120)
    @Column(name = "title", nullable = false, length = 120)
    private String title;

    @Min(1)
    @Column(name = "weeks")
    private Integer weeks;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public TrainerLibraryProgrammeTemplate() {
    }

    public TrainerLibraryProgrammeTemplate(Long trainerId) {
        this.trainerId = trainerId;
    }

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getWeeks() {
        return weeks;
    }

    public void setWeeks(Integer weeks) {
        this.weeks = weeks;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
