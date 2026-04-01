package uk.ac.cf._5.group14.One_To_One.Reviews;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.cf._5.group14.One_To_One.TrainerClient.TrainerClientLink;
import uk.ac.cf._5.group14.One_To_One.TrainerClient.TrainerClientLinkRepository;
import uk.ac.cf._5.group14.One_To_One.TrainerClient.TrainerClientLinkStatus;
import uk.ac.cf._5.group14.One_To_One.Users.Role;
import uk.ac.cf._5.group14.One_To_One.Users.User;
import uk.ac.cf._5.group14.One_To_One.Users.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TrainerReviewService {

    public static final String ERROR_REVIEW_ALREADY_EXISTS = "REVIEW_ALREADY_EXISTS_FOR_LINK";
    public static final String ERROR_INVALID_LINK = "INVALID_TRAINER_CLIENT_LINK";
    public static final String ERROR_LINK_NOT_ELIGIBLE = "LINK_NOT_ELIGIBLE_FOR_REVIEW";
    public static final String ERROR_NOT_CLIENT = "USER_IS_NOT_CLIENT";

    private final TrainerReviewRepository reviewRepository;
    private final TrainerClientLinkRepository linkRepository;
    private final UserRepository userRepository;

    public TrainerReviewService(TrainerReviewRepository reviewRepository,
                               TrainerClientLinkRepository linkRepository,
                               UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.linkRepository = linkRepository;
        this.userRepository = userRepository;
    }

    /**
     * Create a new review. Only clients with ACTIVE or ENDED link can review.
     * One review per client per trainer per link period.
     */
    @Transactional
    public TrainerReview createReview(Long clientUserId, Long trainerId, Integer stars, String tags, String comment) {
        // Verify client role
        User client = userRepository.findById(clientUserId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found"));
        if (client.getRole() != Role.CLIENT) {
            throw new TrainerReviewException(TrainerReviewException.Reason.USER_NOT_CLIENT, ERROR_NOT_CLIENT);
        }

        // Verify trainer exists
        User trainer = userRepository.findById(trainerId)
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found"));
        if (trainer.getRole() != Role.TRAINER) {
            throw new IllegalArgumentException("User is not a trainer");
        }

        // Find active or most recent ended link
        TrainerClientLink link = findEligibleLink(clientUserId, trainerId);
        if (link == null) {
            throw new TrainerReviewException(TrainerReviewException.Reason.LINK_NOT_ELIGIBLE, ERROR_LINK_NOT_ELIGIBLE);
        }

        // Check if review already exists for this link
        if (reviewRepository.existsByTrainerIdAndClientIdAndLinkId(trainerId, clientUserId, link.getId())) {
            throw new TrainerReviewException(TrainerReviewException.Reason.REVIEW_ALREADY_EXISTS, ERROR_REVIEW_ALREADY_EXISTS);
        }

        // Create review
        TrainerReview review = new TrainerReview(trainerId, clientUserId, link.getId(), stars, tags, comment);
        return reviewRepository.save(review);
    }

    /**
     * Find eligible link for review (ACTIVE or ENDED).
     * Returns the most recent eligible link.
     */
    private TrainerClientLink findEligibleLink(Long clientUserId, Long trainerId) {
        // First check for active link
        Optional<TrainerClientLink> activeLink = linkRepository
                .findFirstByClientUserIdAndStatusOrderByUpdatedAtDesc(clientUserId, TrainerClientLinkStatus.ACTIVE)
                .filter(link -> link.getTrainerUserId().equals(trainerId));

        if (activeLink.isPresent()) {
            return activeLink.get();
        }

        // If no active link, find most recent ended link with this trainer
        List<TrainerClientLink> endedLinks = linkRepository
                .findByTrainerUserIdAndStatusOrderByUpdatedAtDesc(trainerId, TrainerClientLinkStatus.ENDED);

        return endedLinks.stream()
                .filter(link -> link.getClientUserId().equals(clientUserId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Check if client can leave a review for this trainer.
     */
    public boolean canClientReviewTrainer(Long clientUserId, Long trainerId) {
        TrainerClientLink link = findEligibleLink(clientUserId, trainerId);
        if (link == null) {
            return false;
        }
        return !reviewRepository.existsByTrainerIdAndClientIdAndLinkId(trainerId, clientUserId, link.getId());
    }

    /**
     * Get all visible reviews for a trainer.
     */
    public List<TrainerReview> getVisibleReviewsForTrainer(Long trainerId) {
        return reviewRepository.findByTrainerIdAndStatusOrderByCreatedAtDesc(trainerId, ReviewStatus.VISIBLE);
    }

    /**
     * Get average rating for a trainer.
     */
    public Double getAverageRating(Long trainerId) {
        Double avg = reviewRepository.calculateAverageRating(trainerId);
        return avg != null ? avg : 0.0;
    }

    /**
     * Get review count for a trainer.
     */
    public long getReviewCount(Long trainerId) {
        return reviewRepository.countByTrainerIdAndStatus(trainerId, ReviewStatus.VISIBLE);
    }

    /**
     * Report a review for moderation.
     */
    @Transactional
    public void reportReview(Long reviewId, Long reportedByUserId, String reason) {
        TrainerReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));

        // Update review status to REPORTED
        if (review.getStatus() == ReviewStatus.VISIBLE) {
            review.setStatus(ReviewStatus.REPORTED);
            reviewRepository.save(review);
        }
    }

    /**
     * Hide a review (admin only).
     */
    @Transactional
    public void hideReview(Long reviewId, Long adminUserId) {
        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        if (admin.getRole() != Role.PLATFORM_ADMIN && admin.getRole() != Role.SUPER_ADMIN) {
            throw new AccessDeniedException("Only admins can hide reviews");
        }

        TrainerReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));
        
        review.setStatus(ReviewStatus.HIDDEN);
        reviewRepository.save(review);
    }

    /**
     * Make a review visible again (admin only).
     */
    @Transactional
    public void makeReviewVisible(Long reviewId, Long adminUserId) {
        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        if (admin.getRole() != Role.PLATFORM_ADMIN && admin.getRole() != Role.SUPER_ADMIN) {
            throw new AccessDeniedException("Only admins can change review visibility");
        }

        TrainerReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));
        
        review.setStatus(ReviewStatus.VISIBLE);
        reviewRepository.save(review);
    }

    /**
     * Get all reported reviews (admin only).
     */
    public List<TrainerReview> getReportedReviews() {
        return reviewRepository.findByStatusOrderByCreatedAtDesc(ReviewStatus.REPORTED);
    }
}
