package uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CustomExerciseData.CustomExercise;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseLog.ExerciseLog;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseData.Exercise;

import java.time.LocalDate;

@Entity
@Table(name = "schedule_occurrences")
@Getter
@Setter
public class ScheduleOccurrence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;

    @ManyToOne
    @JoinColumn(name = "custom_exercise_id")
    private CustomExercise customExercise;

    @Column(name = "schedule_name", nullable = false, length = 200)
    private String scheduleName;

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    @OneToOne
    @JoinColumn(name = "exercise_log_id")
    private ExerciseLog exerciseLog;

    @Column(name = "completed")
    private boolean completed = false;

}
