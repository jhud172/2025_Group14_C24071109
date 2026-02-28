package uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class DayOptimisationKey implements Serializable {

    private Long user;
    private LocalDate date;

    public DayOptimisationKey() {
    }

    public DayOptimisationKey(Long user, LocalDate date) {
        this.user = user;
        this.date = date;
    }

    public Long getUser() { return user; }
    public void setUser(Long user) { this.user = user; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DayOptimisationKey that = (DayOptimisationKey) o;
        return Objects.equals(user, that.user) && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, date);
    }
}
