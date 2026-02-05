package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Goals;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class GoalForm {

    private String title;
    private String description;
    private GoalType goalType;
    private String targetMetricName;
    private Double targetMetricValue;
    private String targetMetricUnit;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate targetDate;

    private GoalStatus status;
    private Integer priority;
    private Boolean archived;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public GoalType getGoalType() {
        return goalType;
    }

    public void setGoalType(GoalType goalType) {
        this.goalType = goalType;
    }

    public String getTargetMetricName() {
        return targetMetricName;
    }

    public void setTargetMetricName(String targetMetricName) {
        this.targetMetricName = targetMetricName;
    }

    public Double getTargetMetricValue() {
        return targetMetricValue;
    }

    public void setTargetMetricValue(Double targetMetricValue) {
        this.targetMetricValue = targetMetricValue;
    }

    public String getTargetMetricUnit() {
        return targetMetricUnit;
    }

    public void setTargetMetricUnit(String targetMetricUnit) {
        this.targetMetricUnit = targetMetricUnit;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    public GoalStatus getStatus() {
        return status;
    }

    public void setStatus(GoalStatus status) {
        this.status = status;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Boolean getArchived() {
        return archived;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }
}
