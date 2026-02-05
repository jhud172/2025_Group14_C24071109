package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Messaging;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(
        name = "message_read_states",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_message_read_state", columnNames = {"message_id", "user_id"})
        }
)
public class MessageReadState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message_id", nullable = false)
    private Long messageId;

    @Column(name = "thread_id", nullable = false)
    private Long threadId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "read_at", nullable = false)
    private Instant readAt;

    public MessageReadState() {
    }

    public MessageReadState(Long messageId, Long threadId, Long userId, Instant readAt) {
        this.messageId = messageId;
        this.threadId = threadId;
        this.userId = userId;
        this.readAt = readAt;
    }

    public Long getId() {
        return id;
    }

    public Long getMessageId() {
        return messageId;
    }

    public Long getThreadId() {
        return threadId;
    }

    public Long getUserId() {
        return userId;
    }

    public Instant getReadAt() {
        return readAt;
    }
}
