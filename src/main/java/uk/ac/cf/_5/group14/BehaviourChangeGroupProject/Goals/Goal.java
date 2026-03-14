package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Goals;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "goals")
@Getter
@Setter
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_user_id")
    private User ownerUser;

    @ManyToOne(optional = false)
    @JoinColumn(name = "created_by_user_id")
    private User createdByUser;

    @ManyToOne
    @JoinColumn(name = "trainer_user_id")
    private User trainerUser;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "goal_type", nullable = false, length = 30)
    private GoalType goalType = GoalType.CUSTOM;

    @Enumerated(EnumType.STRING)
    @Column(name = "timeframe", nullable = false, length = 20)
    private GoalTimeframe timeframe = GoalTimeframe.TARGET;

    @Column(name = "target_metric_name", length = 120)
    private String targetMetricName;

    @Column(name = "target_metric_value")
    private Double targetMetricValue;

    @Column(name = "target_metric_unit", length = 30)
    private String targetMetricUnit;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "target_date")
    private LocalDate targetDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private GoalStatus status = GoalStatus.DRAFT;

    @Column(name = "priority")
    private Integer priority = 0;

    @Column(name = "archived", nullable = false)
    private boolean archived = false;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }
}
