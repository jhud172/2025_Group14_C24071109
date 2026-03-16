package uk.ac.cf._5.group14.BehaviourChangeGroupProject.StrengthLog;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CustomExerciseData.CustomExercise;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseData.Exercise;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "exercise_sessions")
@Getter
@Setter
public class ExerciseSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "workout_session_id", nullable = false)
    private WorkoutSession workoutSession;

    @ManyToOne
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;

    @ManyToOne
    @JoinColumn(name = "custom_exercise_id")
    private CustomExercise customExercise;

    private int orderIndex = 0;

    @Column(nullable = false)
    private String mode = "NORMAL";

    @Column(name = "group_key")
    private String groupKey;

    private boolean completed = false;

    @OneToMany(mappedBy = "exerciseSession", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SetLog> setLogs = new ArrayList<>();
}
