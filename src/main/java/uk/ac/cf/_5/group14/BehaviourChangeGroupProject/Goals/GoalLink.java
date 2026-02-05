package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Goals;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "goal_links")
@Getter
@Setter
public class GoalLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "goal_id")
    private Goal goal;

    @Enumerated(EnumType.STRING)
    @Column(name = "link_type", nullable = false, length = 30)
    private GoalLinkType linkType;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false, length = 30)
    private GoalLinkSource source = GoalLinkSource.SELF;

    @Column(name = "calendar_task_id")
    private Long calendarTaskId;

    @Column(name = "schedule_occurrence_id")
    private Long scheduleOccurrenceId;

    @Column(name = "workout_session_id")
    private Long workoutSessionId;

    @Column(name = "workout_template_id")
    private Long workoutTemplateId;

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
