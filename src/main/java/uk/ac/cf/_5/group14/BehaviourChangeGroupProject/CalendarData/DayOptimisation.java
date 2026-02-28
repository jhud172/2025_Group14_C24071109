package uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "day_optimisations")
@IdClass(DayOptimisationKey.class)
@Getter
@Setter
public class DayOptimisation {

    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Id
    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_theme", nullable = false, length = 30)
    private DayTheme dayTheme = DayTheme.CLEAN;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
