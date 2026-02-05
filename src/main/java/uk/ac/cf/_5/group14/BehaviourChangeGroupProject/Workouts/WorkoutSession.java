package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Workouts;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "WorkoutPlayerSession")
@Table(name = "workout_player_sessions")
@Getter
@Setter
public class WorkoutSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "template_id")
    private WorkoutTemplate template;

    @Column(name = "name_snapshot", length = 200)
    private String nameSnapshot;

    @Column(nullable = false)
    private boolean completed = false;

    @Column(name = "total_volume")
    private Double totalVolume = 0.0;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt = LocalDateTime.now();

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkoutSetLog> setLogs = new ArrayList<>();
}
