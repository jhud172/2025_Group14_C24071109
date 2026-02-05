package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Chat;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class DailyUsageId implements Serializable {

    private Long userId;
    private LocalDate date;

    public DailyUsageId() {
    }

    public DailyUsageId(Long userId, LocalDate date) {
        this.userId = userId;
        this.date = date;
    }

    public Long getUserId() {
        return userId;
    }

    public LocalDate getDate() {
        return date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DailyUsageId that = (DailyUsageId) o;
        return Objects.equals(userId, that.userId) && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, date);
    }
}
