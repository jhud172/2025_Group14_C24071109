package uk.ac.cf._5.group14.One_To_One.ChatV2;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.time.Instant;

@Entity
@Table(name = "chat_threads")
@Getter
@Setter
public class ChatThread {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "chat_type", nullable = false, length = 32)
    private ChatType type = ChatType.AI_PERSONAL;

    @Column(name = "peer_user_id")
    private Long peerUserId;

    @ManyToOne
    @JoinColumn(name = "folder_id")
    private ChatFolder folder;

    @Column(nullable = false, length = 160)
    private String title;

    @Column(name = "color_hex", nullable = false, length = 10)
    private String colorHex;

    @Column(name = "icon_key", nullable = false, length = 40)
    private String iconKey;

    @Column(nullable = false)
    private boolean pinned = false;

    @Column(nullable = false)
    private boolean archived = false;

    @Column(name = "custom_instructions", columnDefinition = "TEXT")
    private String customInstructions;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }
}
