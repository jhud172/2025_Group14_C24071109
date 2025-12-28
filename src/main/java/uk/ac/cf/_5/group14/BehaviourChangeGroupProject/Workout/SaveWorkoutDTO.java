package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Workout;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SaveWorkoutDTO {

    private Long id;
    private String name;
    private String workoutNotes;
    private List<Long> exerciseIds;


}
