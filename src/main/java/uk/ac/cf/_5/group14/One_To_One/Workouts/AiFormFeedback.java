package uk.ac.cf._5.group14.One_To_One.Workouts;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "ai_form_feedback")
@Getter
@Setter
public class AiFormFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "video_id")
    private WorkoutSetVideo video;

    @Column(name = "rep_count")
    private Integer repCount;

    @Column(length = 40)
    private String tempo;

    @Column(name = "flags_json", columnDefinition = "TEXT")
    private String flagsJson;

    @Column
    private Double confidence;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
