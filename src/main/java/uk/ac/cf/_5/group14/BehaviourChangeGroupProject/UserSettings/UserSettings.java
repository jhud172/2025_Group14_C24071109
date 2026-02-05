package uk.ac.cf._5.group14.BehaviourChangeGroupProject.UserSettings;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.Instant;
import java.time.LocalTime;

@Entity
@Table(name = "user_settings")
@Getter
@Setter
public class UserSettings {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "language", nullable = false, length = 20)
    private String language = "en";

    @Enumerated(EnumType.STRING)
    @Column(name = "theme", nullable = false, length = 20)
    private ThemePreference theme = ThemePreference.SYSTEM;

    @Column(name = "easy_mode", nullable = false)
    private boolean easyMode = false;

    @Column(name = "color_blind_mode", nullable = false)
    private boolean colorBlindMode = false;

    @Column(name = "disability_hearing", nullable = false)
    private boolean disabilityHearing = false;

    @Column(name = "disability_mobility", nullable = false)
    private boolean disabilityMobility = false;

    @Column(name = "disability_vision", nullable = false)
    private boolean disabilityVision = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "calendar_task_ordering", nullable = false, length = 30)
    private CalendarTaskOrderingPreference calendarTaskOrdering = CalendarTaskOrderingPreference.CHRONOLOGICAL;

    @Enumerated(EnumType.STRING)
    @Column(name = "calendar_task_layout", nullable = false, length = 30)
    private CalendarTaskLayoutPreference calendarTaskLayout = CalendarTaskLayoutPreference.COMBINED_LIST;

    @Enumerated(EnumType.STRING)
    @Column(name = "calendar_workout_ordering", nullable = false, length = 30)
    private CalendarWorkoutOrderingPreference calendarWorkoutOrdering = CalendarWorkoutOrderingPreference.SCHEDULE_ORDER;

    @Column(name = "quiet_hours_start")
    private LocalTime quietHoursStart;

    @Column(name = "quiet_hours_end")
    private LocalTime quietHoursEnd;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void onCreate() {
        if (updatedAt == null) {
            updatedAt = Instant.now();
        }
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = Instant.now();
    }
}
