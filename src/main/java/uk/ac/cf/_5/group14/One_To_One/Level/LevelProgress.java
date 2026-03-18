package uk.ac.cf._5.group14.One_To_One.Level;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uk.ac.cf._5.group14.One_To_One.Users.User;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public LocalDate getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDate lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
