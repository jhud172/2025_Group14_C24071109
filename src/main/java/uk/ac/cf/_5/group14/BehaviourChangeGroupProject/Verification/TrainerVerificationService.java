package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Verification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Membership.EmailService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserRepository;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerVerificationService {
    
    private final TrainerVerificationRequestRepository verificationRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    
    /**
     * Create a verification request for a trainer (called by gym admin)
     */
    @Transactional
    public TrainerVerificationRequest createVerificationRequest(
        Long trainerUserId,
        Long gymId,
        String notes
    ) {
        // Check if trainer exists
        User trainer = userRepository.findById(trainerUserId)
            .orElseThrow(() -> new IllegalArgumentException("Trainer not found"));
        
        // Check if there's already a pending request
        verificationRepository.findByTrainerUserIdAndStatus(trainerUserId, VerificationStatus.PENDING)
            .ifPresent(request -> {
                throw new IllegalStateException("A pending verification request already exists for this trainer");
            });
        
        TrainerVerificationRequest request = new TrainerVerificationRequest();
        request.setTrainerUserId(trainerUserId);
        request.setGymId(gymId);
        request.setNotes(notes);
        request.setStatus(VerificationStatus.PENDING);
        request.setSubmittedAt(Instant.now());
        
        request = verificationRepository.save(request);
        
        log.info("Created verification request {} for trainer {}", request.getId(), trainerUserId);
        
        return request;
    }
    
    /**
     * Get all pending verification requests (for super admin)
     */
    public List<TrainerVerificationRequest> getPendingRequests() {
        return verificationRepository.findByStatusOrderBySubmittedAtAsc(VerificationStatus.PENDING);
    }

    public List<TrainerVerificationRequest> getQueueRequests() {
        List<TrainerVerificationRequest> pending = verificationRepository.findByStatusOrderBySubmittedAtAsc(VerificationStatus.PENDING);
        List<TrainerVerificationRequest> needsInfo = verificationRepository.findByStatusOrderBySubmittedAtAsc(VerificationStatus.NEEDS_INFO);
        pending.addAll(needsInfo);
        pending.sort((a, b) -> a.getSubmittedAt().compareTo(b.getSubmittedAt()));
        return pending;
    }

    public long countByStatus(VerificationStatus status) {
        return verificationRepository.countByStatus(status);
    }
    
    /**
     * Get verification requests for a specific gym
     */
    public List<TrainerVerificationRequest> getRequestsByGym(Long gymId) {
        return verificationRepository.findByGymIdOrderBySubmittedAtDesc(gymId);
    }
    
    /**
     * Get the latest verification request for a trainer
     */
    public TrainerVerificationRequest getLatestRequestForTrainer(Long trainerUserId) {
        return verificationRepository.findTopByTrainerUserIdOrderBySubmittedAtDesc(trainerUserId)
            .orElse(null);
    }
    
    /**
     * Approve a trainer (called by super admin)
     */
    @Transactional
    public TrainerVerificationRequest approveTrainer(
        Long requestId,
        Long reviewerUserId,
        String adminNotes
    ) {
        TrainerVerificationRequest request = verificationRepository.findById(requestId)
            .orElseThrow(() -> new IllegalArgumentException("Verification request not found"));
        
        if (request.getStatus() != VerificationStatus.PENDING && 
            request.getStatus() != VerificationStatus.NEEDS_INFO) {
            throw new IllegalStateException("Can only approve pending or needs-info requests");
        }
        
        request.setStatus(VerificationStatus.APPROVED);
        request.setReviewedAt(Instant.now());
        request.setReviewedByUserId(reviewerUserId);
        request.setAdminNotes(adminNotes);
        
        request = verificationRepository.save(request);
        
        // Update the trainer's verified status
        User trainer = userRepository.findById(request.getTrainerUserId())
            .orElseThrow(() -> new IllegalArgumentException("Trainer not found"));
        
        trainer.setTrainerVerified(true);
        userRepository.save(trainer);
        
        // Send email notification
        try {
            emailService.sendTrainerVerificationUpdate(trainer, "APPROVED", adminNotes);
        } catch (Exception e) {
            log.error("Failed to send verification email to trainer {}", trainer.getId(), e);
        }
        
        log.info("Approved verification request {} for trainer {}", requestId, trainer.getId());
        
        return request;
    }
    
    /**
     * Reject a trainer verification request (called by super admin)
     */
    @Transactional
    public TrainerVerificationRequest rejectTrainer(
        Long requestId,
        Long reviewerUserId,
        String adminNotes
    ) {
        TrainerVerificationRequest request = verificationRepository.findById(requestId)
            .orElseThrow(() -> new IllegalArgumentException("Verification request not found"));
        
        if (request.getStatus() != VerificationStatus.PENDING && 
            request.getStatus() != VerificationStatus.NEEDS_INFO) {
            throw new IllegalStateException("Can only reject pending or needs-info requests");
        }
        
        request.setStatus(VerificationStatus.REJECTED);
        request.setReviewedAt(Instant.now());
        request.setReviewedByUserId(reviewerUserId);
        request.setAdminNotes(adminNotes);
        
        request = verificationRepository.save(request);
        
        // Send email notification
        User trainer = userRepository.findById(request.getTrainerUserId())
            .orElse(null);
        
        if (trainer != null) {
            try {
                emailService.sendTrainerVerificationUpdate(trainer, "REJECTED", adminNotes);
            } catch (Exception e) {
                log.error("Failed to send verification email to trainer {}", trainer.getId(), e);
            }
        }
        
        log.info("Rejected verification request {} for trainer {}", requestId, request.getTrainerUserId());
        
        return request;
    }
    
    /**
     * Request more information from the trainer (called by super admin)
     */
    @Transactional
    public TrainerVerificationRequest requestMoreInfo(
        Long requestId,
        Long reviewerUserId,
        String adminNotes
    ) {
        if (adminNotes == null || adminNotes.isBlank()) {
            throw new IllegalArgumentException("Admin notes are required when requesting more info");
        }
        
        TrainerVerificationRequest request = verificationRepository.findById(requestId)
            .orElseThrow(() -> new IllegalArgumentException("Verification request not found"));
        
        if (request.getStatus() != VerificationStatus.PENDING) {
            throw new IllegalStateException("Can only request info for pending requests");
        }
        
        request.setStatus(VerificationStatus.NEEDS_INFO);
        request.setReviewedAt(Instant.now());
        request.setReviewedByUserId(reviewerUserId);
        request.setAdminNotes(adminNotes);
        
        request = verificationRepository.save(request);
        
        // Send email notification
        User trainer = userRepository.findById(request.getTrainerUserId())
            .orElse(null);
        
        if (trainer != null) {
            try {
                emailService.sendTrainerVerificationUpdate(trainer, "NEEDS_INFO", adminNotes);
            } catch (Exception e) {
                log.error("Failed to send verification email to trainer {}", trainer.getId(), e);
            }
        }
        
        log.info("Requested more info for verification request {}", requestId);
        
        return request;
    }
    
    /**
     * Update trainer notes in response to needs-info request
     */
    @Transactional
    public TrainerVerificationRequest updateTrainerNotes(Long requestId, String notes) {
        TrainerVerificationRequest request = verificationRepository.findById(requestId)
            .orElseThrow(() -> new IllegalArgumentException("Verification request not found"));
        
        if (request.getStatus() != VerificationStatus.NEEDS_INFO) {
            throw new IllegalStateException("Can only update notes for needs-info requests");
        }
        
        request.setNotes(notes);
        request.setStatus(VerificationStatus.PENDING);
        request.setReviewedAt(null);
        request.setReviewedByUserId(null);
        
        return verificationRepository.save(request);
    }
    
    /**
     * Check if a trainer is verified
     */
    public boolean isTrainerVerified(Long trainerId) {
        return userRepository.findById(trainerId)
            .map(User::isTrainerVerified)
            .orElse(false);
    }
}
