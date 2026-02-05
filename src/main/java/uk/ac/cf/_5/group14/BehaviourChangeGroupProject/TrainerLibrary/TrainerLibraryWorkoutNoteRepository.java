package uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerLibrary;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainerLibraryWorkoutNoteRepository extends JpaRepository<TrainerLibraryWorkoutNote, Long> {
    List<TrainerLibraryWorkoutNote> findByWorkoutIdOrderByIdAsc(Long workoutId);

    void deleteByWorkoutId(Long workoutId);
}
