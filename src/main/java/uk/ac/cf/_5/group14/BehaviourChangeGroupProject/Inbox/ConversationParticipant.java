package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Inbox;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.Instant;

@Entity
@Table(
        name = "conversation_participant",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_convo_participant_unique", columnNames = {"conversation_id", "user_id"})
        }
)
@Getter
@Setter
public class ConversationParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_in_convo", nullable = false, length = 20)
    private RoleInConversation roleInConversation;

    @Column(name = "last_read_at")
    private Instant lastReadAt;
}
