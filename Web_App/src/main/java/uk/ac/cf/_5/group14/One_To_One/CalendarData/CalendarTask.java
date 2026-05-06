package uk.ac.cf._5.group14.One_To_One.CalendarData;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uk.ac.cf._5.group14.One_To_One.ExerciseLog.ExerciseLog;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Instant;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_type", length = 20)
    private ActivityType activityType;

    @Column(nullable = false)
    private Boolean completed = false;

    @Column(nullable = false)
    private boolean missed = false;

    @Column(name = "missed_at")
    private Instant missedAt;

    @Column(name = "grace_period_minutes")
    private Integer gracePeriodMinutes;

    @Transient
    private boolean inGrace;

    @Transient
    private boolean late;

    @OneToOne
    @JoinColumn(name = "exercise_log_id")
    private ExerciseLog exerciseLog;

    @Column(name = "exercise_name")
    private String exerciseName;

    @Column(name = "requires_log")
    private boolean requiresLog;

    @Column(name = "trainer_template_id")
    private Long trainerTemplateId;

    @Column(name = "trainer_template_entry_id")
    private Long trainerTemplateEntryId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Boolean getExercise() {
        return exercise;
    }

    public void setExercise(Boolean exercise) {
        this.exercise = exercise;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public boolean isMissed() {
        return missed;
    }

    public void setMissed(boolean missed) {
        this.missed = missed;
    }

    public Instant getMissedAt() {
        return missedAt;
    }

    public void setMissedAt(Instant missedAt) {
        this.missedAt = missedAt;
    }

    public Integer getGracePeriodMinutes() {
        return gracePeriodMinutes;
    }

    public void setGracePeriodMinutes(Integer gracePeriodMinutes) {
        this.gracePeriodMinutes = gracePeriodMinutes;
    }

    public boolean isInGrace() {
        return inGrace;
    }

    public void setInGrace(boolean inGrace) {
        this.inGrace = inGrace;
    }

    public boolean isLate() {
        return late;
    }

    public void setLate(boolean late) {
        this.late = late;
    }

    public ExerciseLog getExerciseLog() {
        return exerciseLog;
    }

    public void setExerciseLog(ExerciseLog exerciseLog) {
        this.exerciseLog = exerciseLog;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public boolean isRequiresLog() {
        return requiresLog;
    }

    public void setRequiresLog(boolean requiresLog) {
        this.requiresLog = requiresLog;
    }

    public Long getTrainerTemplateId() {
        return trainerTemplateId;
    }

    public void setTrainerTemplateId(Long trainerTemplateId) {
        this.trainerTemplateId = trainerTemplateId;
    }

    public Long getTrainerTemplateEntryId() {
        return trainerTemplateEntryId;
    }

    public void setTrainerTemplateEntryId(Long trainerTemplateEntryId) {
        this.trainerTemplateEntryId = trainerTemplateEntryId;
    }
}
