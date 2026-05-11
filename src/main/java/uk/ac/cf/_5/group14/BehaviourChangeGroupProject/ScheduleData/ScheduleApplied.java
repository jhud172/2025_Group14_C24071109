package uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.LocalDate;

@Entity
@Table(name = "schedule_applied")
@Getter
@Setter
public class ScheduleApplied {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDate dateApplied;

    @Column(name = "shown_on_calendar", nullable = false)
    private boolean shownOnCalendar = true;

    @Column(name = "requires_logging", nullable = false)
    private boolean requiresLogging = false;

    @Column(name = "duration_weeks", nullable = false)
    private int durationWeeks = 4;
}