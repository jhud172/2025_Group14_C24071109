package uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerLibrary;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

@Entity
@Table(name = "trainer_library_exercises")
public class TrainerLibraryExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "trainer_id", nullable = false)
    private Long trainerId;

    @NotBlank
    @Size(max = 120)
    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotBlank
    @Size(max = 255)
    @Column(name = "primary_muscles", nullable = false, length = 255)
    private String primaryMuscles;

    @NotBlank
    @Size(max = 255)
    @Column(name = "equipment", nullable = false, length = 255)
    private String equipment;

    @NotBlank
    @Size(max = 30)
    @Column(name = "difficulty", nullable = false, length = 30)
    private String difficulty;

    @Size(max = 500)
    @Column(name = "video_url", length = 500)
    private String videoUrl;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public TrainerLibraryExercise() {
    }

    public TrainerLibraryExercise(Long trainerId) {
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

    public String getPrimaryMuscles() {
        return primaryMuscles;
    }

    public void setPrimaryMuscles(String primaryMuscles) {
        this.primaryMuscles = primaryMuscles;
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
