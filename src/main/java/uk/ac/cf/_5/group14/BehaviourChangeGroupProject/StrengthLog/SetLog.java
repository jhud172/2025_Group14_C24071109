package uk.ac.cf._5.group14.BehaviourChangeGroupProject.StrengthLog;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "set_logs")
@Getter
@Setter
public class SetLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "exercise_session_id", nullable = false)
    private ExerciseSession exerciseSession;

    private int setNumber;

    private Double weight;

    private Integer reps;

    private String notes;

    private boolean completed = false;

    @Column(name = "set_type", nullable = false)
    private String setType = "NORMAL";
}
