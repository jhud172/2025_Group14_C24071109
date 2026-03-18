package uk.ac.cf._5.group14.One_To_One.Chat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "coach_messages",
        indexes = {
                @Index(name = "idx_coach_messages_conversation", columnList = "conversation_id"),
                @Index(name = "idx_coach_messages_conversation_created", columnList = "conversation_id, created_at")
        })
@Getter
@Setter
public class CoachMessage {

    public enum Role {
        USER,
        ASSISTANT
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "conversation_id")
    private CoachConversation conversation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private Role role;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
