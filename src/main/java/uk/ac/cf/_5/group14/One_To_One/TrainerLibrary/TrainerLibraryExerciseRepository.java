package uk.ac.cf._5.group14.One_To_One.TrainerLibrary;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrainerLibraryExerciseRepository extends JpaRepository<TrainerLibraryExercise, Long> {
    List<TrainerLibraryExercise> findByTrainerIdOrderByCreatedAtDesc(Long trainerId);

    Optional<TrainerLibraryExercise> findByIdAndTrainerId(Long id, Long trainerId);
}
