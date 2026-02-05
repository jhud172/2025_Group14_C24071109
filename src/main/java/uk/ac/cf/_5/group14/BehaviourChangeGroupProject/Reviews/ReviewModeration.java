package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Reviews;

import jakarta.persistence.*;

import java.time.Instant;

/**
 * Tracks reported reviews for moderation.
 */
@Entity
@Table(name = "review_moderations")
public class ReviewModeration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "review_id", nullable = false)
    private Long reviewId;

    @Column(name = "reported_by_user_id", nullable = false)
    private Long reportedByUserId;

    @Column(name = "reason", length = 1000)
    private String reason;

    @Column(name = "reported_at", nullable = false)
    private Instant reportedAt;

    @Column(name = "resolved", nullable = false)
    private boolean resolved = false;

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    @Column(name = "resolved_by_user_id")
    private Long resolvedByUserId;

    @Column(name = "resolution_notes", columnDefinition = "TEXT")
    private String resolutionNotes;

    public ReviewModeration() {
    }

    public ReviewModeration(Long reviewId, Long reportedByUserId, String reason) {
        this.reviewId = reviewId;
        this.reportedByUserId = reportedByUserId;
        this.reason = reason;
    }

    @PrePersist
    void prePersist() {
        if (reportedAt == null) {
            reportedAt = Instant.now();
        }
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public Long getReviewId() {
        return reviewId;
    }

    public void setReviewId(Long reviewId) {
        this.reviewId = reviewId;
    }

    public Long getReportedByUserId() {
        return reportedByUserId;
    }

    public void setReportedByUserId(Long reportedByUserId) {
        this.reportedByUserId = reportedByUserId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Instant getReportedAt() {
        return reportedAt;
    }

    public boolean isResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    public Instant getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(Instant resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public Long getResolvedByUserId() {
        return resolvedByUserId;
    }

    public void setResolvedByUserId(Long resolvedByUserId) {
        this.resolvedByUserId = resolvedByUserId;
    }

    public String getResolutionNotes() {
        return resolutionNotes;
    }

    public void setResolutionNotes(String resolutionNotes) {
        this.resolutionNotes = resolutionNotes;
    }
}
