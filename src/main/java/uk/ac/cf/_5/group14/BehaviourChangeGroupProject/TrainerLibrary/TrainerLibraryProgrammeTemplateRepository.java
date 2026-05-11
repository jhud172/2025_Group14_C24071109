package uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerLibrary;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrainerLibraryProgrammeTemplateRepository extends JpaRepository<TrainerLibraryProgrammeTemplate, Long> {
    List<TrainerLibraryProgrammeTemplate> findByTrainerIdOrderByCreatedAtDesc(Long trainerId);

    Optional<TrainerLibraryProgrammeTemplate> findByIdAndTrainerId(Long id, Long trainerId);
}
