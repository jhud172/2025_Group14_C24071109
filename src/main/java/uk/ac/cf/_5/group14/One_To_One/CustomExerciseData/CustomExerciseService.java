package uk.ac.cf._5.group14.One_To_One.CustomExerciseData;

import java.util.List;



public interface CustomExerciseService {
    List<CustomExercise> getCustomExercisesByUser(Long userId);
    CustomExercise getCustomExerciseById(Long id, Long userId);
    CustomExercise saveCustomExercise(CustomExercise exercise);
    CustomExercise updateCustomExercise(Long userId, Long id, CustomExerciseRequest request);
    void deleteCustomExercise(Long userId, Long id);
}
