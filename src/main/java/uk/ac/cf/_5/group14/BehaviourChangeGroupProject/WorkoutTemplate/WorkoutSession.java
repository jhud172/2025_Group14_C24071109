package uk.ac.cf._5.group14.BehaviourChangeGroupProject.WorkoutTemplate;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Workout.Workout;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "WorkoutTemplateSession")
@Table(name = "workout_template_sessions")
@Getter
@Setter
public class WorkoutSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "workout_id", nullable = false)
    private Workout workout;

    @ManyToOne(optional = true)
    @JoinColumn(name = "template_id", nullable = true)
    private WorkoutTemplate templateUsed;

    @Column(name = "template_name_snapshot", length = 200)
    private String templateNameSnapshot;

    @Column(name = "config_json_snapshot", columnDefinition = "TEXT")
    private String configJsonSnapshot;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "ended_at")
    private Instant endedAt;

    @Column(name = "mood_before")
    private Integer moodBefore;

    @Column(name = "mood_after")
    private Integer moodAfter;

    @Column(name = "confidence")
    private Integer confidence;

    @Column(name = "total_volume", precision = 10, scale = 2)
    private BigDecimal totalVolume;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SessionStatus status = SessionStatus.IN_PROGRESS;

    @Column(name = "allow_completed_without_log", nullable = false)
    private boolean allowCompletedWithoutLog = true;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private List<WorkoutSessionExercise> exercises = new ArrayList<>();
}
