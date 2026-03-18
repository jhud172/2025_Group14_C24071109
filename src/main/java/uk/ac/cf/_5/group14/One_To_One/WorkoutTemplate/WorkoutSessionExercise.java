package uk.ac.cf._5.group14.One_To_One.WorkoutTemplate;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "WorkoutTemplateExercise")
@Table(name = "workout_session_exercises")
@Getter
@Setter
public class WorkoutSessionExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private WorkoutSession session;

    @Column(name = "exercise_id")
    private Long exerciseId;

    @Column(name = "custom_exercise_id")
    private Long customExerciseId;

    @Column(name = "order_index", nullable = false)
    private int orderIndex = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode", nullable = false, length = 20)
    private ExerciseMode mode = ExerciseMode.NORMAL;

    @Column(name = "group_key", length = 100)
    private String groupKey;

    @Column(name = "notes", length = 1000)
    private String notes;

    @OneToMany(mappedBy = "sessionExercise", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("setIndex ASC")
    private List<WorkoutSessionSet> sets = new ArrayList<>();
}
