package uk.ac.cf._5.group14.One_To_One.TrainerTemplates;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trainer_schedule_templates")
public class TrainerScheduleTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trainer_id", nullable = false)
    private Long trainerId;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 800)
    private String description;

    @Column(length = 500)
    private String tags;

    @Column(nullable = false)
    private int version = 1;

    @Column(nullable = false)
    private boolean archived = false;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private List<TrainerScheduleTemplateEntry> entries = new ArrayList<>();

    public TrainerScheduleTemplate() {
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

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public List<TrainerScheduleTemplateEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<TrainerScheduleTemplateEntry> entries) {
        this.entries = entries;
    }
}
