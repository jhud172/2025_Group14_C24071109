package uk.ac.cf._5.group14.One_To_One.TrainerAssignments;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uk.ac.cf._5.group14.One_To_One.Workouts.WorkoutTemplate;

import java.time.LocalDateTime;

@Entity
@Table(name = "assigned_workouts")
@Getter
@Setter
public class AssignedWorkout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trainer_id", nullable = false)
    private Long trainerUserId;

    @Column(name = "client_id", nullable = false)
    private Long clientUserId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "workout_template_id")
    private WorkoutTemplate workoutTemplate;

    @Column(name = "trainer_notes", length = 800)
    private String trainerNotes;

    @Column(name = "client_notes", length = 1200)
    private String clientNotes;

    @Column(name = "client_feedback", length = 1200)
    private String clientFeedback;

    @Column(nullable = false)
    private boolean completed = false;

    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt = LocalDateTime.now();

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (assignedAt == null) {
            assignedAt = now;
        }
        updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
