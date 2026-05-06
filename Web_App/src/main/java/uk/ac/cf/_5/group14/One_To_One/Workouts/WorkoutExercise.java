package uk.ac.cf._5.group14.One_To_One.Workouts;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "workout_template_exercises")
@Getter
@Setter
public class WorkoutExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "template_id")
    private WorkoutTemplate template;

    @Column(name = "exercise_id")
    private Long exerciseId;

    @Column(name = "custom_exercise_id")
    private Long customExerciseId;

    @Column(name = "exercise_name", nullable = false, length = 200)
    private String exerciseName;

    @Column(nullable = false)
    private int sets = 3;

    @Column(nullable = false)
    private int reps = 10;

    @Column(name = "rest_seconds", nullable = false)
    private int restSeconds = 60;

    @Column(length = 500)
    private String notes;

    @Column(name = "order_index", nullable = false)
    private int orderIndex = 0;
}
