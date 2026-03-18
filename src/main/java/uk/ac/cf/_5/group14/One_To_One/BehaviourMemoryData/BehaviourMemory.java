package uk.ac.cf._5.group14.One_To_One.BehaviourMemoryData;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "behaviour_memory")
@Getter
@Setter
public class BehaviourMemory {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "as_of_date", nullable = false)
    private LocalDate asOfDate;

    @Column(name = "window_days", nullable = false)
    private int windowDays;

    @Column(name = "green_days", nullable = false)
    private int greenDays;

    @Column(name = "orange_days", nullable = false)
    private int orangeDays;

    @Column(name = "red_days", nullable = false)
    private int redDays;

    @Column(name = "grey_days", nullable = false)
    private int greyDays;

    @Column(name = "avg_completion_percentage", nullable = false)
    private int avgCompletionPercentage;

    @Column(name = "avg_tasks_per_day", nullable = false)
    private double avgTasksPerDay;

    @Column(name = "high_load_days", nullable = false)
    private int highLoadDays;

    @Column(name = "time_pressure_score", nullable = false)
    private int timePressureScore;

    @Column(name = "last_ai_reference_at")
    private Instant lastAiReferenceAt;

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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getAsOfDate() {
        return asOfDate;
    }

    public void setAsOfDate(LocalDate asOfDate) {
        this.asOfDate = asOfDate;
    }

    public int getWindowDays() {
        return windowDays;
    }

    public void setWindowDays(int windowDays) {
        this.windowDays = windowDays;
    }

    public int getGreenDays() {
        return greenDays;
    }

    public void setGreenDays(int greenDays) {
        this.greenDays = greenDays;
    }

    public int getOrangeDays() {
        return orangeDays;
    }

    public void setOrangeDays(int orangeDays) {
        this.orangeDays = orangeDays;
    }

    public int getRedDays() {
        return redDays;
    }

    public void setRedDays(int redDays) {
        this.redDays = redDays;
    }

    public int getGreyDays() {
        return greyDays;
    }

    public void setGreyDays(int greyDays) {
        this.greyDays = greyDays;
    }

    public int getAvgCompletionPercentage() {
        return avgCompletionPercentage;
    }

    public void setAvgCompletionPercentage(int avgCompletionPercentage) {
        this.avgCompletionPercentage = avgCompletionPercentage;
    }

    public double getAvgTasksPerDay() {
        return avgTasksPerDay;
    }

    public void setAvgTasksPerDay(double avgTasksPerDay) {
        this.avgTasksPerDay = avgTasksPerDay;
    }

    public int getHighLoadDays() {
        return highLoadDays;
    }

    public void setHighLoadDays(int highLoadDays) {
        this.highLoadDays = highLoadDays;
    }

    public int getTimePressureScore() {
        return timePressureScore;
    }

    public void setTimePressureScore(int timePressureScore) {
        this.timePressureScore = timePressureScore;
    }

    public Instant getLastAiReferenceAt() {
        return lastAiReferenceAt;
    }

    public void setLastAiReferenceAt(Instant lastAiReferenceAt) {
        this.lastAiReferenceAt = lastAiReferenceAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
