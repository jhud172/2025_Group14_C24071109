package uk.ac.cf._5.group14.One_To_One.ExerciseLog;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uk.ac.cf._5.group14.One_To_One.CalendarData.CalendarTask;
import uk.ac.cf._5.group14.One_To_One.ScheduleData.ScheduleOccurrence;
import uk.ac.cf._5.group14.One_To_One.Users.User;

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

    public Integer getMoodBefore() {
        return moodBefore;
    }

    public void setMoodBefore(Integer moodBefore) {
        this.moodBefore = moodBefore;
    }

    public Integer getMoodAfter() {
        return moodAfter;
    }

    public void setMoodAfter(Integer moodAfter) {
        this.moodAfter = moodAfter;
    }

    public Integer getConfidence() {
        return confidence;
    }

    public void setConfidence(Integer confidence) {
        this.confidence = confidence;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public ScheduleOccurrence getOccurrence() {
        return occurrence;
    }

    public void setOccurrence(ScheduleOccurrence occurrence) {
        this.occurrence = occurrence;
    }

    public CalendarTask getCalendarTask() {
        return calendarTask;
    }

    public void setCalendarTask(CalendarTask calendarTask) {
        this.calendarTask = calendarTask;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
}
