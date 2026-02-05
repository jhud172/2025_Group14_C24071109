package uk.ac.cf._5.group14.BehaviourChangeGroupProject.ChatV2;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.Instant;

@Entity
@Table(name = "chat_folders")
@Getter
@Setter
public class ChatFolder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(name = "color_hex", nullable = false, length = 10)
    private String colorHex;

    @Column(name = "icon_key", nullable = false, length = 40)
    private String iconKey;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder = 0;

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
