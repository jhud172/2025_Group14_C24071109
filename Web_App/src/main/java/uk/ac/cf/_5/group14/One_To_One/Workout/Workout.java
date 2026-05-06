package uk.ac.cf._5.group14.One_To_One.Workout;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uk.ac.cf._5.group14.One_To_One.CustomExerciseData.CustomExercise;
import uk.ac.cf._5.group14.One_To_One.ExerciseData.Exercise;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "workouts")
public class Workout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long userId;

    @NotNull
    private String name;

    private String notes;

    @ManyToMany
    @JoinTable(
            name = "workouts_exercises",
            joinColumns = @JoinColumn(name = "workout_id"),
            inverseJoinColumns = @JoinColumn(name = "exercise_id")
    )
    private List<Exercise> exercises;

        @ManyToMany
        @JoinTable(
            name = "workouts_custom_exercises",
            joinColumns = @JoinColumn(name = "workout_id"),
            inverseJoinColumns = @JoinColumn(name = "custom_exercise_id")
        )
        private List<CustomExercise> customExercises;
}
