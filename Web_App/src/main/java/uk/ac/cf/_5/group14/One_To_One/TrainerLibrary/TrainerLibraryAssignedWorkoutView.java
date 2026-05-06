package uk.ac.cf._5.group14.One_To_One.TrainerLibrary;

import java.util.List;
import java.util.Map;

public class TrainerLibraryAssignedWorkoutView {

    private final TrainerLibraryWorkoutTemplate workout;
    private final List<TrainerLibraryWorkoutItem> items;
    private final List<TrainerLibraryWorkoutNote> notes;
    private final Map<Long, TrainerLibraryExercise> exercisesById;

    public TrainerLibraryAssignedWorkoutView(TrainerLibraryWorkoutTemplate workout,
                                            List<TrainerLibraryWorkoutItem> items,
                                            List<TrainerLibraryWorkoutNote> notes,
                                            Map<Long, TrainerLibraryExercise> exercisesById) {
        this.workout = workout;
        this.items = items;
        this.notes = notes;
        this.exercisesById = exercisesById;
    }

    public TrainerLibraryWorkoutTemplate getWorkout() {
        return workout;
    }

    public List<TrainerLibraryWorkoutItem> getItems() {
        return items;
    }

    public List<TrainerLibraryWorkoutNote> getNotes() {
        return notes;
    }

    public Map<Long, TrainerLibraryExercise> getExercisesById() {
        return exercisesById;
    }
}
