package uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseLog.ExerciseLog;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.LocalDate;
import java.time.LocalTime;

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

    @Column(nullable = false)
    private Boolean completed = false;

    @OneToOne
    @JoinColumn(name = "exercise_log_id")
    private ExerciseLog exerciseLog;

    @Column(name = "exercise_name")
    private String exerciseName;

    @Column(name = "requires_log")
    private boolean requiresLog;

}
