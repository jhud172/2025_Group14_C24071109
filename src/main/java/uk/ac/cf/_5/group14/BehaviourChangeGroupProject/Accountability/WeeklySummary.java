package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Accountability;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "weekly_summaries",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "week_start"}))
@Getter
@Setter
public class WeeklySummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "week_start", nullable = false)
    private LocalDate weekStart;

    @Column(name = "summary_json", nullable = false, columnDefinition = "TEXT")
    private String summaryJson;

    @Column(name = "goals_json", columnDefinition = "TEXT")
    private String goalsJson;

    @Column(name = "missed_items_json", columnDefinition = "TEXT")
    private String missedItemsJson;

    @Column(name = "streaks_json", columnDefinition = "TEXT")
    private String streaksJson;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
