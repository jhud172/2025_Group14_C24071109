package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Goals;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class GoalCheckInForm {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate weekStartDate;

    private String reflection;
    private Integer confidenceRating;
    private String trainerComment;

    public LocalDate getWeekStartDate() {
        return weekStartDate;
    }

    public void setWeekStartDate(LocalDate weekStartDate) {
        this.weekStartDate = weekStartDate;
    }

    public String getReflection() {
        return reflection;
    }

    public void setReflection(String reflection) {
        this.reflection = reflection;
    }

    public Integer getConfidenceRating() {
        return confidenceRating;
    }

    public void setConfidenceRating(Integer confidenceRating) {
        this.confidenceRating = confidenceRating;
    }

    public String getTrainerComment() {
        return trainerComment;
    }

    public void setTrainerComment(String trainerComment) {
        this.trainerComment = trainerComment;
    }
}
