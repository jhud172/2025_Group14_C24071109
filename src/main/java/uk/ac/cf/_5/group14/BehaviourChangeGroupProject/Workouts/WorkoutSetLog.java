package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Workouts;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "workout_set_logs")
@Getter
@Setter
public class WorkoutSetLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "session_id")
    private WorkoutSession session;

    @Column(name = "exercise_name", nullable = false, length = 200)
    private String exerciseName;

    @Column(name = "exercise_order", nullable = false)
    private int exerciseOrder = 0;

    @Column(name = "set_number", nullable = false)
    private int setNumber = 1;

    @Column(name = "target_reps")
    private Integer targetReps;

    @Column(name = "rest_seconds")
    private Integer restSeconds;

    @Column
    private Double weight;

    @Column
    private Integer reps;

    @Column(length = 500)
    private String notes;

    @Column(nullable = false)
    private boolean completed = false;
}
