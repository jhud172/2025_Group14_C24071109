package uk.ac.cf._5.group14.BehaviourChangeGroupProject.DayHealthData;

import java.io.Serializable;
import java.time.LocalDate;

public class DayHealthKey implements Serializable {

    private Long user;
    private LocalDate date;

    public DayHealthKey() {
    }

    public DayHealthKey(Long userId, LocalDate date) {
        this.user = userId;
        this.date = date;
    }

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DayHealthKey that)) return false;
        if (user == null ? that.user != null : !user.equals(that.user)) return false;
        return date == null ? that.date == null : date.equals(that.date);
    }

    @Override
    public int hashCode() {
        int result = (user != null ? user.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }
}
