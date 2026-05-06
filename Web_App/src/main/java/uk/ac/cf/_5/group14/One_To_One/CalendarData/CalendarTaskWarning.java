package uk.ac.cf._5.group14.One_To_One.CalendarData;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalTime;

@Entity
@Table(name = "calendar_task_warnings")
@Getter
@Setter
public class CalendarTaskWarning {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "calendar_task_id", nullable = false)
    private CalendarTask task;

    @Enumerated(EnumType.STRING)
    @Column(name = "trigger_type", nullable = false)
    private CalendarTaskWarningTriggerType triggerType;

    @Column(name = "trigger_time")
    private LocalTime triggerTime;

    @ManyToOne
    @JoinColumn(name = "trigger_task_id")
    private CalendarTask triggerTask;

    @Column(name = "triggered_at")
    private Instant triggeredAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
