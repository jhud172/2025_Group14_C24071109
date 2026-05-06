package uk.ac.cf._5.group14.One_To_One.ScheduleData;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "schedules")
@Getter
@Setter
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScheduleType scheduleType = ScheduleType.WEEKLY;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RotationMode rotationMode = RotationMode.WEEKLY_REPEAT;

    /**
     * For CUSTOM schedules: number of days in the schedule cycle
     * For WEEKLY: always 7
     * For DAILY: always 1
     */
    @Column(nullable = false)
    private Integer customDayCount = 7;

    /**
     * Optional template identifier if this schedule was created from a template
     */
    @Column(length = 100)
    private String templateId;

    @OneToMany(
            mappedBy = "schedule",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ScheduleEntry> entries = new ArrayList<>();
}
