package uk.ac.cf._5.group14.BehaviourChangeGroupProject.FeedbackData;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "adaptive_feedback")
@IdClass(AdaptiveFeedbackKey.class)
public class AdaptiveFeedback {

    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Id
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "feedback_text", nullable = false, length = 1000)
    private String feedbackText;

    @Column(name = "tone", nullable = false, length = 32)
    private String tone;

    @Column(name = "feedback_hash", nullable = false, length = 64)
    private String feedbackHash;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public AdaptiveFeedback() {
    }

    public AdaptiveFeedback(Long userId, LocalDate date, String feedbackText, String tone, String feedbackHash) {
        this.userId = userId;
        this.date = date;
        this.feedbackText = feedbackText;
        this.tone = tone;
        this.feedbackHash = feedbackHash;
    }

    @PrePersist
    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getFeedbackText() {
        return feedbackText;
    }

    public void setFeedbackText(String feedbackText) {
        this.feedbackText = feedbackText;
    }

    public String getTone() {
        return tone;
    }

    public void setTone(String tone) {
        this.tone = tone;
    }

    public String getFeedbackHash() {
        return feedbackHash;
    }

    public void setFeedbackHash(String feedbackHash) {
        this.feedbackHash = feedbackHash;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
