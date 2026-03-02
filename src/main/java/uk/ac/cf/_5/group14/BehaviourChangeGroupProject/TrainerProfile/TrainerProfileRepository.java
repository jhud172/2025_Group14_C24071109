package uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerProfile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrainerProfileRepository extends JpaRepository<TrainerProfile, Long> {
    
    /**
     * Find a trainer profile by user ID.
     */
    Optional<TrainerProfile> findByUserId(Long userId);
    
    /**
     * Check if a trainer profile exists for a user.
     */
    boolean existsByUserId(Long userId);

    /**
     * Check if a trainer code is already in use.
     */
    boolean existsByTrainerCode(String trainerCode);
}
