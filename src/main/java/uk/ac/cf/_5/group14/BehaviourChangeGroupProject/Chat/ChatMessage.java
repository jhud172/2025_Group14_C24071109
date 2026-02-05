package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Chat;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

@Entity
@Table(name = "chat_messages")
@Getter
@Setter
public class ChatMessage {

    public enum Role {
        USER,
        AI
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 8)
    private Role role;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
}
