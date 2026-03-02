package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Health.BloodPressure;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "blood_pressure_readings")
@Getter
@Setter
public class BloodPressureReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "reading_date", nullable = false)
    @NotNull
    private LocalDate readingDate;

    @Column(name = "reading_time")
    private LocalTime readingTime;

    @Column(nullable = false)
    @NotNull
    @Min(60)
    @Max(250)
    private Integer systolic;

    @Column(nullable = false)
    @NotNull
    @Min(40)
    @Max(150)
    private Integer diastolic;

    @Min(30)
    @Max(220)
    private Integer pulse;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Arm arm;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Position position;

    @Size(max = 500)
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ReadingSource source = ReadingSource.MANUAL;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum Arm { LEFT, RIGHT }
    public enum Position { SITTING, STANDING, LYING }
    public enum ReadingSource { MANUAL, IMPORTED }
}
