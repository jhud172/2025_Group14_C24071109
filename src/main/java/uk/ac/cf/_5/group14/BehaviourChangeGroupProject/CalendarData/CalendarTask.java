package uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseLog.ExerciseLog;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Instant;

@Entity
@Table(name = "calendar_tasks")
@Getter
@Setter
public class CalendarTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private LocalDate date;

    private LocalTime time;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "is_exercise", nullable = false)
    private Boolean exercise = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_type", length = 20)
    private ActivityType activityType;

    @Column(nullable = false)
    private Boolean completed = false;

    @Column(nullable = false)
    private boolean missed = false;

    @Column(name = "missed_at")
    private Instant missedAt;

    @Column(name = "grace_period_minutes")
    private Integer gracePeriodMinutes;

    @Transient
    private boolean inGrace;

    @Transient
    private boolean late;

    @OneToOne
    @JoinColumn(name = "exercise_log_id")
    private ExerciseLog exerciseLog;

    @Column(name = "exercise_name")
    private String exerciseName;

    @Column(name = "requires_log")
    private boolean requiresLog;

    @Column(name = "trainer_template_id")
    private Long trainerTemplateId;

    @Column(name = "trainer_template_entry_id")
    private Long trainerTemplateEntryId;

}
