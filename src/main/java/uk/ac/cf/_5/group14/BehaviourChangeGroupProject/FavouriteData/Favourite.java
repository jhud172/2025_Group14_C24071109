package uk.ac.cf._5.group14.BehaviourChangeGroupProject.FavouriteData;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "favourites")
@Getter
@Setter
public class Favourite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "exercise_id")
    private Long exerciseId;

    @Column(name = "custom_exercise_id")
    private Long customExerciseId;
}