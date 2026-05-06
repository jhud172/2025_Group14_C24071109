package uk.ac.cf._5.group14.One_To_One.TrainerLibrary;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrainerLibraryWorkoutTemplateRepository extends JpaRepository<TrainerLibraryWorkoutTemplate, Long> {
    List<TrainerLibraryWorkoutTemplate> findByTrainerIdOrderByCreatedAtDesc(Long trainerId);

    Optional<TrainerLibraryWorkoutTemplate> findByIdAndTrainerId(Long id, Long trainerId);
}
