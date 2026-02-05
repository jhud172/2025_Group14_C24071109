package uk.ac.cf._5.group14.BehaviourChangeGroupProject.FeedbackData;

import java.io.Serializable;
import java.time.LocalDate;

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
}
