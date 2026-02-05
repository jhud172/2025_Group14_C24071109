package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Workouts;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkoutSetUpdateRequest {
    private Double weight;
    private Integer reps;
    private String notes;
    private Boolean completed;
}
