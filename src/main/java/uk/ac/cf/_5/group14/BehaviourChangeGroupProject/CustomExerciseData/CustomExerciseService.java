package uk.ac.cf._5.group14.BehaviourChangeGroupProject.CustomExerciseData;

import java.util.List;



public interface CustomExerciseService {
    List<CustomExercise> getCustomExercisesByUser(Long userId);
    CustomExercise getCustomExerciseById(Long id);
    void saveCustomExercise(CustomExercise exercise);
}
