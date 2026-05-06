package uk.ac.cf._5.group14.One_To_One.Verification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainerVerificationRequestRepository extends JpaRepository<TrainerVerificationRequest, Long> {
    
    List<TrainerVerificationRequest> findByStatusOrderBySubmittedAtAsc(VerificationStatus status);
    
    List<TrainerVerificationRequest> findByGymIdOrderBySubmittedAtDesc(Long gymId);
    
    Optional<TrainerVerificationRequest> findByTrainerUserIdAndStatus(Long trainerUserId, VerificationStatus status);
    
    Optional<TrainerVerificationRequest> findTopByTrainerUserIdOrderBySubmittedAtDesc(Long trainerUserId);

    Optional<TrainerVerificationRequest> findByIdAndGymId(Long id, Long gymId);

    long countByStatus(VerificationStatus status);
}
