package uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "daily_completion")
@IdClass(DailyCompletionKey.class)
@Getter
@Setter
public class DailyCompletion {

    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Id
    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(name = "completion_status", nullable = false)
    private DailyCompletionStatus completionStatus = DailyCompletionStatus.GREY;

    @Column(name = "completion_percentage", nullable = false)
    private int completionPercentage = 0;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    void touchUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
}
