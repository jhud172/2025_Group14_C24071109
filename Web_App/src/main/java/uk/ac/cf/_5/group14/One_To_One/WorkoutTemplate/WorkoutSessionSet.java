package uk.ac.cf._5.group14.One_To_One.WorkoutTemplate;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity(name = "WorkoutTemplateSet")
@Table(name = "workout_session_sets")
@Getter
@Setter
public class WorkoutSessionSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "session_exercise_id", nullable = false)
    private WorkoutSessionExercise sessionExercise;

    @Column(name = "set_index", nullable = false)
    private int setIndex = 0;

    @Column(name = "reps")
    private Integer reps;

    @Column(name = "weight", precision = 8, scale = 2)
    private BigDecimal weight;

    @Column(name = "rpe", precision = 4, scale = 1)
    private BigDecimal rpe;

    @Column(name = "tempo", length = 20)
    private String tempo;

    @Column(name = "is_drop", nullable = false)
    private boolean isDrop = false;

    @Column(name = "completed_at")
    private Instant completedAt;
}
