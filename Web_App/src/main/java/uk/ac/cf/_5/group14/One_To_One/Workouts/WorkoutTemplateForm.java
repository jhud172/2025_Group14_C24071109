package uk.ac.cf._5.group14.One_To_One.Workouts;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class WorkoutTemplateForm {
    private String name;
    private String description;
    private List<WorkoutExerciseForm> exercises = new ArrayList<>();
}
