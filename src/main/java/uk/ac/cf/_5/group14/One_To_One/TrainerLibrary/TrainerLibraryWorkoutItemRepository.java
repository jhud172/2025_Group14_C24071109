package uk.ac.cf._5.group14.One_To_One.TrainerLibrary;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainerLibraryWorkoutItemRepository extends JpaRepository<TrainerLibraryWorkoutItem, Long> {
    List<TrainerLibraryWorkoutItem> findByWorkoutIdOrderByOrderIndexAsc(Long workoutId);

    void deleteByWorkoutId(Long workoutId);
}
