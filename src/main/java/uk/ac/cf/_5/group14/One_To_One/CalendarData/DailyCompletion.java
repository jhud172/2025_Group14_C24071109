package uk.ac.cf._5.group14.One_To_One.CalendarData;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_completion")
@IdClass(DailyCompletionKey.class)
@Getter
@Setter
public class DailyCompletion {

    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Id
    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(name = "completion_status", nullable = false)
    private DailyCompletionStatus completionStatus = DailyCompletionStatus.GREY;

    @Column(name = "completion_percentage", nullable = false)
    private int completionPercentage = 0;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    void touchUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
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

    public DailyCompletionStatus getCompletionStatus() {
        return completionStatus;
    }

    public void setCompletionStatus(DailyCompletionStatus completionStatus) {
        this.completionStatus = completionStatus;
    }

    public int getCompletionPercentage() {
        return completionPercentage;
    }

    public void setCompletionPercentage(int completionPercentage) {
        this.completionPercentage = completionPercentage;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
