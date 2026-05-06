package uk.ac.cf._5.group14.One_To_One.Reviews;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewModerationRepository extends JpaRepository<ReviewModeration, Long> {

    /**
     * Find all pending moderation requests.
     */
    List<ReviewModeration> findByResolvedOrderByReportedAtDesc(boolean resolved);

    /**
     * Find all moderation records for a specific review.
     */
    List<ReviewModeration> findByReviewIdOrderByReportedAtDesc(Long reviewId);

    /**
     * Check if a user has already reported this review.
     */
    boolean existsByReviewIdAndReportedByUserId(Long reviewId, Long reportedByUserId);
}
