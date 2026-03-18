package uk.ac.cf._5.group14.One_To_One.Messaging;

import jakarta.persistence.*;

import java.time.Instant;

@Entity(name = "MessageThreadMessage")
@Table(name = "message_thread_messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "thread_id", nullable = false)
    private MessageThread thread;

    @Column(name = "sender_user_id", nullable = false)
    private Long senderUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private MessageType type;

    @Column(name = "body_text", nullable = false, columnDefinition = "TEXT")
    private String bodyText;

    @Column(name = "attachment_name", length = 200)
    private String attachmentName;

    @Column(name = "attachment_url", length = 500)
    private String attachmentUrl;

    @Column(name = "attachment_type", length = 100)
    private String attachmentType;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public Message() {
    }

    public Message(MessageThread thread, Long senderUserId, MessageType type, String bodyText) {
        this.thread = thread;
        this.senderUserId = senderUserId;
        this.type = type;
        this.bodyText = bodyText;
    }

    public Message(MessageThread thread,
                   Long senderUserId,
                   MessageType type,
                   String bodyText,
                   String attachmentName,
                   String attachmentUrl,
                   String attachmentType) {
        this.thread = thread;
        this.senderUserId = senderUserId;
        this.type = type;
        this.bodyText = bodyText;
        this.attachmentName = attachmentName;
        this.attachmentUrl = attachmentUrl;
        this.attachmentType = attachmentType;
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

    public MessageThread getThread() {
        return thread;
    }

    public Long getSenderUserId() {
        return senderUserId;
    }

    public MessageType getType() {
        return type;
    }

    public String getBodyText() {
        return bodyText;
    }

    public String getAttachmentName() {
        return attachmentName;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public String getAttachmentType() {
        return attachmentType;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
