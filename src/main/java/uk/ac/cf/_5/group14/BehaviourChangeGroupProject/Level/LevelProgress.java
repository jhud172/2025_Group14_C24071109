package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Level;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.LocalDate;

/**
 * Tracks a user's accumulated points and current level.
 * Points are awarded for daily activities and behaviours across the application.
 */
@Entity
@Table(name = "user_points")
@Getter
@Setter
public class LevelProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @Column(nullable = false)
    private int points = 0;

    @Column(nullable = false)
    private int level = 1;

    @Column(nullable = false)
    private LocalDate lastUpdated = LocalDate.now();
}
