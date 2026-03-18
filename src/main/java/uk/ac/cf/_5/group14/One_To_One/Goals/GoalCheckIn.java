package uk.ac.cf._5.group14.One_To_One.Goals;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uk.ac.cf._5.group14.One_To_One.Users.Role;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "goal_check_ins")
@Getter
@Setter
public class GoalCheckIn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "goal_id")
    private Goal goal;

    @ManyToOne(optional = false)
    @JoinColumn(name = "created_by_user_id")
    private User createdByUser;

    @Enumerated(EnumType.STRING)
    @Column(name = "created_by_role", nullable = false, length = 30)
    private Role createdByRole;

    @Column(name = "week_start_date", nullable = false)
    private LocalDate weekStartDate;

    @Column(columnDefinition = "TEXT")
    private String reflection;

    @Column(name = "confidence_rating")
    private Integer confidenceRating;

    @Column(name = "trainer_comment", columnDefinition = "TEXT")
    private String trainerComment;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
