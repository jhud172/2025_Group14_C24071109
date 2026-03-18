package uk.ac.cf._5.group14.One_To_One.Messaging;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "message_threads")
public class MessageThread {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @Column(name = "trainer_id", nullable = false)
    private Long trainerId;

    @Column(name = "link_id", nullable = false)
    private Long linkId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private MessageThreadStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public MessageThread() {
    }

    public MessageThread(Long clientId, Long trainerId, Long linkId, MessageThreadStatus status) {
        this.clientId = clientId;
        this.trainerId = trainerId;
        this.linkId = linkId;
        this.status = status;
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

    public Long getClientId() {
        return clientId;
    }

    public Long getTrainerId() {
        return trainerId;
    }

    public Long getLinkId() {
        return linkId;
    }

    public MessageThreadStatus getStatus() {
        return status;
    }

    public void setStatus(MessageThreadStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
