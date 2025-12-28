package uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseData;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Workout.Workout;
import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "exercises")
@Getter
@Setter
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String category;

    @NotNull
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "video_url")
    private String videoUrl;

    @NotNull
    private Integer difficulty;

    @NotNull
    private String type;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToMany
    @JoinTable(name = "exercises_Tags",
            joinColumns = @JoinColumn(name = "exercise_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )

    // Initialises an empty set of tags for a given exercise object (Makes tag retrieval smoother)
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "workouts_exercises",
            joinColumns = @JoinColumn(name = "exercise_id"),
            inverseJoinColumns = @JoinColumn(name = "workout_id")
    )
    private Set<Workout> workouts = new HashSet<>();
}
