package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Reviews;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerClient.TrainerClientLinkRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerClient.TrainerClientLinkStatus;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.Role;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserRepository;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing private client assessments (trainer-only).
 */
@Service
public class ClientAssessmentService {

    private final ClientAssessmentRepository assessmentRepository;
    private final TrainerClientLinkRepository linkRepository;
    private final UserRepository userRepository;

    public ClientAssessmentService(ClientAssessmentRepository assessmentRepository,
                                  TrainerClientLinkRepository linkRepository,
                                  UserRepository userRepository) {
        this.assessmentRepository = assessmentRepository;
        this.linkRepository = linkRepository;
        this.userRepository = userRepository;
    }

    /**
     * Create or update a client assessment.
     * Only trainers who have/had a link with the client can assess them.
     */
    @Transactional
    public ClientAssessment saveAssessment(Long trainerId, Long clientId, 
                                          Integer reliabilityScore, Integer communicationScore, 
                                          String privateNotes) {
        // Verify trainer
        User trainer = userRepository.findById(trainerId)
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found"));
        if (trainer.getRole() != Role.TRAINER) {
            throw new IllegalArgumentException("User is not a trainer");
        }

        // Verify client exists
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found"));
        if (client.getRole() != Role.CLIENT) {
            throw new IllegalArgumentException("User is not a client");
        }

        // Verify trainer has/had a relationship with this client
        boolean hasRelationship = linkRepository.existsByTrainerUserIdAndClientUserIdAndStatus(trainerId, clientId, TrainerClientLinkStatus.ACTIVE)
                || linkRepository.existsByTrainerUserIdAndClientUserIdAndStatus(trainerId, clientId, TrainerClientLinkStatus.ENDED);

        if (!hasRelationship) {
            throw new AccessDeniedException("Trainer must have an active or past relationship with client");
        }

        // Find or create assessment
        ClientAssessment assessment = assessmentRepository.findByTrainerIdAndClientId(trainerId, clientId)
                .orElse(new ClientAssessment(trainerId, clientId));

        assessment.setReliabilityScore(reliabilityScore);
        assessment.setCommunicationScore(communicationScore);
        assessment.setPrivateNotes(privateNotes);

        return assessmentRepository.save(assessment);
    }

    /**
     * Get assessment for a specific client (trainer-only).
     */
    public Optional<ClientAssessment> getAssessment(Long trainerId, Long clientId) {
        return assessmentRepository.findByTrainerIdAndClientId(trainerId, clientId);
    }

    /**
     * Get all assessments by a trainer.
     */
    public List<ClientAssessment> getAllAssessmentsByTrainer(Long trainerId) {
        return assessmentRepository.findByTrainerIdOrderByUpdatedAtDesc(trainerId);
    }

    /**
     * Delete an assessment.
     */
    @Transactional
    public void deleteAssessment(Long trainerId, Long clientId) {
        ClientAssessment assessment = assessmentRepository.findByTrainerIdAndClientId(trainerId, clientId)
                .orElseThrow(() -> new IllegalArgumentException("Assessment not found"));

        assessmentRepository.delete(assessment);
    }
}
