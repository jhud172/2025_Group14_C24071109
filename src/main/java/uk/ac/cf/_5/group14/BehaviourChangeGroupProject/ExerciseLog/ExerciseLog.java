package uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseLog;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTask;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleOccurrence;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.LocalDate;


@Entity
@Table(name = "exercise_log")
@Getter
@Setter
public class ExerciseLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDate date;

    private Integer moodBefore;

    private Integer moodAfter;

    private Integer confidence;

    private String comments;

    @OneToOne
    @JoinColumn(name = "occurrence_id")
    private ScheduleOccurrence occurrence;

    @OneToOne
    @JoinColumn(name = "calendar_task_id")
    private CalendarTask calendarTask;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

}
