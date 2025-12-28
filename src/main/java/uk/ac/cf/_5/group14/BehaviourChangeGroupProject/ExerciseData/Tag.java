package uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseData;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Tags")
@Getter
@Setter
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    //Using both size and column length restrictions for extra protection
    @Column(length = 200, unique = true)
    @Size(max = 200)
    @NotNull
    String name;

    @Column(length = 200)
    @Size(max = 200)
    @NotNull
    String category;


}
