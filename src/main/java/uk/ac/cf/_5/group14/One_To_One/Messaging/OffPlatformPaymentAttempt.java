package uk.ac.cf._5.group14.One_To_One.Messaging;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "off_platform_payment_attempts")
public class OffPlatformPaymentAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "thread_id", nullable = false)
    private Long threadId;

    @Column(name = "sender_user_id", nullable = false)
    private Long senderUserId;

    @Column(name = "matched_keyword", length = 100)
    private String matchedKeyword;

    @Column(name = "body_text", nullable = false, columnDefinition = "TEXT")
    private String bodyText;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public OffPlatformPaymentAttempt() {
    }

    public OffPlatformPaymentAttempt(Long threadId, Long senderUserId, String matchedKeyword, String bodyText) {
        this.threadId = threadId;
        this.senderUserId = senderUserId;
        this.matchedKeyword = matchedKeyword;
        this.bodyText = bodyText;
    }

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public Long getId() {
        return id;
    }

    public Long getThreadId() {
        return threadId;
    }

    public Long getSenderUserId() {
        return senderUserId;
    }

    public String getMatchedKeyword() {
        return matchedKeyword;
    }

    public String getBodyText() {
        return bodyText;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
