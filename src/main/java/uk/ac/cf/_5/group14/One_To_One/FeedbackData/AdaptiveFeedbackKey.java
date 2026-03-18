package uk.ac.cf._5.group14.One_To_One.FeedbackData;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class AdaptiveFeedbackKey implements Serializable {

    private Long userId;
    private LocalDate date;

    public AdaptiveFeedbackKey() {
    }

    public AdaptiveFeedbackKey(Long userId, LocalDate date) {
        this.userId = userId;
        this.date = date;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AdaptiveFeedbackKey other)) {
            return false;
        }
        return Objects.equals(userId, other.userId) && Objects.equals(date, other.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, date);
    }
}
