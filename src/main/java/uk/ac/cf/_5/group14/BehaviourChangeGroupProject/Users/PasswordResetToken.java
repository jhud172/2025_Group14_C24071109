package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "password_reset_tokens",
    indexes = {
        @Index(name = "idx_password_reset_tokens_user", columnList = "user_id"),
        @Index(name = "idx_password_reset_tokens_token", columnList = "token")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_password_reset_token", columnNames = "token")
    })
@Getter
@Setter
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "token", nullable = false, length = 120)
    private String token;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "used_at")
    private Instant usedAt;
}
