package uk.ac.cf._5.group14.BehaviourChangeGroupProject.FocusData;

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
@Table(name = "daily_focus")
@IdClass(DailyFocusKey.class)
@Getter
@Setter
public class DailyFocus {

    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Id
    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "daily_focus", nullable = false)
    private String dailyFocus;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    void touchUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
}
