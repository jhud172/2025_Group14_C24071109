package uk.ac.cf._5.group14.BehaviourChangeGroupProject.CustomExerciseData;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CustomExerciseData.CustomExercise;

@Repository
public interface CustomExerciseRepository extends CrudRepository<CustomExercise, Long> {

}
