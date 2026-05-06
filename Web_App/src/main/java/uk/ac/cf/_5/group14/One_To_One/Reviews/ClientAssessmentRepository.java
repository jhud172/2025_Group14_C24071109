package uk.ac.cf._5.group14.One_To_One.Reviews;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClientAssessmentRepository extends JpaRepository<ClientAssessment, Long> {

    /**
     * Find assessment by trainer and client.
     */
    Optional<ClientAssessment> findByTrainerIdAndClientId(Long trainerId, Long clientId);

    /**
     * Find all assessments by a trainer.
     */
    List<ClientAssessment> findByTrainerIdOrderByUpdatedAtDesc(Long trainerId);

    /**
     * Check if assessment exists.
     */
    boolean existsByTrainerIdAndClientId(Long trainerId, Long clientId);
}
