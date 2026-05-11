package uk.ac.cf._5.group14.BehaviourChangeGroupProject.DayHealthData;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "day_health")
@IdClass(DayHealthKey.class)
@Getter
@Setter
public class DayHealth {

    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Id
    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "primary_message", nullable = false, columnDefinition = "TEXT")
    private String primaryMessage;

    @Column(name = "suggestion_a", nullable = false, columnDefinition = "TEXT")
    private String suggestionA;

    @Column(name = "suggestion_b", nullable = false, columnDefinition = "TEXT")
    private String suggestionB;

    @Column(name = "watch_out", columnDefinition = "TEXT")
    private String watchOut;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    void touchUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
}
