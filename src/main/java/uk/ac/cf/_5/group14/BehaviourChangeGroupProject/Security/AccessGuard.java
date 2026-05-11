package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerClient.TrainerClientLinkRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerClient.TrainerClientLinkStatus;

import java.util.Objects;

@Service
public class AccessGuard {

    private final TrainerClientLinkRepository linkRepository;

    public AccessGuard(TrainerClientLinkRepository linkRepository) {
        this.linkRepository = linkRepository;
    }

    /**
     * Checks if a trainer has an ACTIVE link with a client.
     */
    public boolean canTrainerAccessClient(Long trainerId, Long clientId) {
        if (trainerId == null || clientId == null) return false;
        return linkRepository.existsByTrainerUserIdAndClientUserIdAndStatus(
                trainerId, clientId, TrainerClientLinkStatus.ACTIVE);
    }

    /**
     * Enforces that a trainer has an ACTIVE link with a client.
     * @throws AccessDeniedException if not allowed.
     */
    public void requireTrainerAccessClient(Long trainerId, Long clientId) {
        if (!canTrainerAccessClient(trainerId, clientId)) {
            throw new AccessDeniedException("Access Denied: Trainer is not linked to this client.");
        }
    }

    /**
     * Checks if a client has an ACTIVE link with a trainer.
     */
    public boolean canClientAccessTrainer(Long clientId, Long trainerId) {
        if (trainerId == null || clientId == null) return false;
        return linkRepository.existsByTrainerUserIdAndClientUserIdAndStatus(
                trainerId, clientId, TrainerClientLinkStatus.ACTIVE);
    }
    
    /**
     * Enforces that a client has an ACTIVE link with a trainer.
     * @throws AccessDeniedException if not allowed.
     */
    public void requireClientAccessTrainer(Long clientId, Long trainerId) {
        if (!canClientAccessTrainer(clientId, trainerId)) {
            throw new AccessDeniedException("Access Denied: Client is not linked to this trainer.");
        }
    }

    /**
     * Checks if the user is the owner of the resource.
     */
    public void requireOwnership(Long currentUserId, Long resourceOwnerId) {
        if (!Objects.equals(currentUserId, resourceOwnerId)) {
            throw new AccessDeniedException("Access Denied: You do not have permission to access this resource.");
        }
    }

    /**
     * Checks if two users have an ACTIVE trainer-client relationship (in either direction).
     */
    public boolean hasActiveRelationship(Long u1, Long u2) {
        if (u1 == null || u2 == null) return false;
        // Check u1 as trainer, u2 as client
        boolean t1 = linkRepository.existsByTrainerUserIdAndClientUserIdAndStatus(u1, u2, TrainerClientLinkStatus.ACTIVE);
        if (t1) return true;
        // Check u2 as trainer, u1 as client
        return linkRepository.existsByTrainerUserIdAndClientUserIdAndStatus(u2, u1, TrainerClientLinkStatus.ACTIVE);
    }

    public void requireActiveRelationship(Long u1, Long u2) {
        if (!hasActiveRelationship(u1, u2)) {
            throw new AccessDeniedException("Access Denied: Active relationship required for messaging.");
        }
    }
}
