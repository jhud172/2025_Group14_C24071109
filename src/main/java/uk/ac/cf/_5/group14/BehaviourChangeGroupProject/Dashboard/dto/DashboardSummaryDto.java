package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Dashboard.dto;

import java.time.LocalDate;
import java.util.List;

public class DashboardSummaryDto {

    private int tasksDueToday;
    private int workoutsDueToday;
    private LocalDate today;
    private List<WeekDaySummary> week;

    public DashboardSummaryDto(int tasksDueToday, int workoutsDueToday, LocalDate today, List<WeekDaySummary> week) {
        this.tasksDueToday = tasksDueToday;
        this.workoutsDueToday = workoutsDueToday;
        this.today = today;
        this.week = week;
    }

    public int getTasksDueToday() {
        return tasksDueToday;
    }

    public int getWorkoutsDueToday() {
        return workoutsDueToday;
    }

    public LocalDate getToday() {
        return today;
    }

    public List<WeekDaySummary> getWeek() {
        return week;
    }

    public static class WeekDaySummary {
        private final LocalDate date;
        private final String label;
        private final String dayNumber;
        private final int tasksCount;
        private final int workoutsCount;
        private final boolean today;

        public WeekDaySummary(LocalDate date, String label, String dayNumber, int tasksCount, int workoutsCount, boolean today) {
            this.date = date;
            this.label = label;
            this.dayNumber = dayNumber;
            this.tasksCount = tasksCount;
            this.workoutsCount = workoutsCount;
            this.today = today;
        }

        public LocalDate getDate() {
            return date;
        }

        public String getLabel() {
            return label;
        }

        public String getDayNumber() {
            return dayNumber;
        }

        public int getTasksCount() {
            return tasksCount;
        }

        public int getWorkoutsCount() {
            return workoutsCount;
        }

        public boolean isToday() {
            return today;
        }
    }
}
