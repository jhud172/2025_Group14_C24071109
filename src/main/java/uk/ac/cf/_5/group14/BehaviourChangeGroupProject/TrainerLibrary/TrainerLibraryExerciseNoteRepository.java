package uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerLibrary;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainerLibraryExerciseNoteRepository extends JpaRepository<TrainerLibraryExerciseNote, Long> {
    List<TrainerLibraryExerciseNote> findByExerciseIdOrderByIdAsc(Long exerciseId);

    void deleteByExerciseId(Long exerciseId);
}
