package uk.ac.cf._5.group14.One_To_One.Reviews;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.cf._5.group14.One_To_One.Users.Role;
import uk.ac.cf._5.group14.One_To_One.Users.User;
import uk.ac.cf._5.group14.One_To_One.Users.UserRepository;

import java.time.Instant;
import java.util.List;

/**
 * Service for managing review moderation.
 */
@Service
public class ReviewModerationService {

    private final ReviewModerationRepository moderationRepository;
    private final TrainerReviewRepository reviewRepository;
    private final UserRepository userRepository;

    public ReviewModerationService(ReviewModerationRepository moderationRepository,
                                  TrainerReviewRepository reviewRepository,
                                  UserRepository userRepository) {
        this.moderationRepository = moderationRepository;
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
    }

    /**
     * Report a review for moderation.
     */
    @Transactional
    public ReviewModeration reportReview(Long reviewId, Long reportedByUserId, String reason) {
        // Verify review exists
        TrainerReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));

        // Check if user has already reported this review
        if (moderationRepository.existsByReviewIdAndReportedByUserId(reviewId, reportedByUserId)) {
            throw new IllegalStateException("You have already reported this review");
        }

        // Create moderation record
        ReviewModeration moderation = new ReviewModeration(reviewId, reportedByUserId, reason);
        moderation = moderationRepository.save(moderation);

        // Update review status to REPORTED
        if (review.getStatus() == ReviewStatus.VISIBLE) {
            review.setStatus(ReviewStatus.REPORTED);
            reviewRepository.save(review);
        }

        return moderation;
    }

    /**
     * Get all pending moderation requests (admin only).
     */
    public List<ReviewModeration> getPendingModerations(Long adminUserId) {
        verifyAdmin(adminUserId);
        return moderationRepository.findByResolvedOrderByReportedAtDesc(false);
    }

    /**
     * Get all resolved moderation requests (admin only).
     */
    public List<ReviewModeration> getResolvedModerations(Long adminUserId) {
        verifyAdmin(adminUserId);
        return moderationRepository.findByResolvedOrderByReportedAtDesc(true);
    }

    /**
     * Resolve a moderation request and hide the review (admin only).
     */
    @Transactional
    public void resolveModerationAndHideReview(Long moderationId, Long adminUserId, String resolutionNotes) {
        verifyAdmin(adminUserId);

        ReviewModeration moderation = moderationRepository.findById(moderationId)
                .orElseThrow(() -> new IllegalArgumentException("Moderation record not found"));

        if (moderation.isResolved()) {
            throw new IllegalStateException("This moderation request has already been resolved");
        }

        TrainerReview review = reviewRepository.findById(moderation.getReviewId())
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));

        // Hide the review
        review.setStatus(ReviewStatus.HIDDEN);
        reviewRepository.save(review);

        // Mark moderation as resolved
        moderation.setResolved(true);
        moderation.setResolvedAt(Instant.now());
        moderation.setResolvedByUserId(adminUserId);
        moderation.setResolutionNotes(resolutionNotes);
        moderationRepository.save(moderation);
    }

    /**
     * Resolve a moderation request but keep the review visible (admin only).
     */
    @Transactional
    public void resolveModerationKeepVisible(Long moderationId, Long adminUserId, String resolutionNotes) {
        verifyAdmin(adminUserId);

        ReviewModeration moderation = moderationRepository.findById(moderationId)
                .orElseThrow(() -> new IllegalArgumentException("Moderation record not found"));

        if (moderation.isResolved()) {
            throw new IllegalStateException("This moderation request has already been resolved");
        }

        TrainerReview review = reviewRepository.findById(moderation.getReviewId())
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));

        // Keep review visible
        review.setStatus(ReviewStatus.VISIBLE);
        reviewRepository.save(review);

        // Mark moderation as resolved
        moderation.setResolved(true);
        moderation.setResolvedAt(Instant.now());
        moderation.setResolvedByUserId(adminUserId);
        moderation.setResolutionNotes(resolutionNotes);
        moderationRepository.save(moderation);
    }

    /**
     * Get all moderation records for a specific review.
     */
    public List<ReviewModeration> getModerationsForReview(Long reviewId, Long adminUserId) {
        verifyAdmin(adminUserId);
        return moderationRepository.findByReviewIdOrderByReportedAtDesc(reviewId);
    }

    private void verifyAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        if (user.getRole() != Role.PLATFORM_ADMIN && user.getRole() != Role.SUPER_ADMIN) {
            throw new AccessDeniedException("Only admins can access moderation functions");
        }
    }
}
