package uk.ac.cf._5.group14.One_To_One.Support;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.time.Instant;

@Entity
@Table(name = "support_requests")
@Getter
@Setter
public class SupportRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_type", nullable = false, length = 30)
    private SupportRequestType requestType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SupportRequestStatus status = SupportRequestStatus.NEW;

    @Column(name = "submitter_name", length = 120)
    private String submitterName;

    @Column(name = "submitter_email", length = 255)
    private String submitterEmail;

    @Column(name = "subject", nullable = false, length = 180)
    private String subject;

    @Column(name = "message", nullable = false, length = 5000)
    private String message;

    @Column(name = "allow_email_reply", nullable = false)
    private boolean allowEmailReply = false;

    @Column(name = "viewed", nullable = false)
    private boolean viewed = false;

    @Column(name = "admin_response", length = 5000)
    private String adminResponse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responded_by_user_id")
    private User respondedBy;

    @Column(name = "responded_at")
    private Instant respondedAt;

    @Column(name = "submitted_at", nullable = false, updatable = false)
    private Instant submittedAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        if (submittedAt == null) {
            submittedAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }
}
