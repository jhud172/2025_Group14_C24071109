package uk.ac.cf._5.group14.BehaviourChangeGroupProject.GymProfile;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GymProfileRepository extends JpaRepository<GymProfile, Long> {
    Optional<GymProfile> findByUserId(Long userId);
}
