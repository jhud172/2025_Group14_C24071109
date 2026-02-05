package uk.ac.cf._5.group14.BehaviourChangeGroupProject.CustomExerciseData;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomExerciseRepository extends CrudRepository<CustomExercise, Long> {

	List<CustomExercise> findByUserIdOrderByNameAsc(Long userId);

	Optional<CustomExercise> findByIdAndUserId(Long id, Long userId);

}
