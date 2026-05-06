package uk.ac.cf._5.group14.One_To_One.Workouts;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class WorkoutPlayerExerciseView {
    private String name;
    private int orderIndex;
    private List<WorkoutSetLog> sets = new ArrayList<>();
    private String lastSummary;
    private String bestSummary;
}
