package uk.ac.cf._5.group14.One_To_One.Accountability;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "user_streaks")
@IdClass(UserStreakKey.class)
@Getter
@Setter
public class UserStreak {

    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "streak_type", nullable = false, length = 20)
    private StreakType streakType;

    @Column(name = "current_count", nullable = false)
    private int currentCount = 0;

    @Column(name = "longest_count", nullable = false)
    private int longestCount = 0;

    @Column(name = "last_completed_date")
    private LocalDate lastCompletedDate;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    @PreUpdate
    void touchUpdatedAt() {
        updatedAt = Instant.now();
    }
}
