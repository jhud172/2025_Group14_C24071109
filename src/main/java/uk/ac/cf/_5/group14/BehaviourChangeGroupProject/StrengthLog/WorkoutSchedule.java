package uk.ac.cf._5.group14.BehaviourChangeGroupProject.StrengthLog;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Workout.Workout;

@Entity
@Table(name = "workout_schedule")
@Getter
@Setter
public class WorkoutSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ISO day of week 1-7
    private int dayOfWeek;

    @ManyToOne
    @JoinColumn(name = "workout_id", nullable = false)
    private Workout workout;

    private int orderIndex = 0;
}
