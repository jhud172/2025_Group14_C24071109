package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Dashboard.dto;

import java.time.LocalDate;
import java.util.List;

public class DashboardSummaryDto {

    private int tasksDueToday;
    private int workoutsDueToday;
    private LocalDate today;
    private List<WeekDaySummary> week;
    private int weeklyWorkoutsCompleted;
    private int workoutStreak;
    private int daysSinceLastWorkout;
    private boolean premium;
    private int logsThisWeekCount;
    private int lastWeekLogsCount;
    private String charlieContext;

    public DashboardSummaryDto(int tasksDueToday, int workoutsDueToday, LocalDate today,
                               List<WeekDaySummary> week, int weeklyWorkoutsCompleted,
                               int workoutStreak, int daysSinceLastWorkout, boolean premium,
                               int logsThisWeekCount, int lastWeekLogsCount, String charlieContext) {
        this.tasksDueToday = tasksDueToday;
        this.workoutsDueToday = workoutsDueToday;
        this.today = today;
        this.week = week;
        this.weeklyWorkoutsCompleted = weeklyWorkoutsCompleted;
        this.workoutStreak = workoutStreak;
        this.daysSinceLastWorkout = daysSinceLastWorkout;
        this.premium = premium;
        this.logsThisWeekCount = logsThisWeekCount;
        this.lastWeekLogsCount = lastWeekLogsCount;
        this.charlieContext = charlieContext;
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

    public int getWeeklyWorkoutsCompleted() {
        return weeklyWorkoutsCompleted;
    }

    public int getWorkoutStreak() {
        return workoutStreak;
    }

    public int getDaysSinceLastWorkout() {
        return daysSinceLastWorkout;
    }

    public boolean isPremium() {
        return premium;
    }

    public int getLogsThisWeekCount() {
        return logsThisWeekCount;
    }

    public int getLastWeekLogsCount() {
        return lastWeekLogsCount;
    }

    public String getCharlieContext() {
        return charlieContext;
    }

    public static class WeekDaySummary {
        private final LocalDate date;
        private final String label;
        private final String dayNumber;
        private final int tasksCount;
        private final int workoutsCount;
        private final boolean today;
        private final List<String> taskTitles;
        private final List<String> workoutTitles;

        public WeekDaySummary(LocalDate date, String label, String dayNumber, int tasksCount, int workoutsCount, boolean today) {
            this(date, label, dayNumber, tasksCount, workoutsCount, today, List.of(), List.of());
        }

        public WeekDaySummary(LocalDate date, String label, String dayNumber, int tasksCount, int workoutsCount, boolean today, List<String> taskTitles, List<String> workoutTitles) {
            this.date = date;
            this.label = label;
            this.dayNumber = dayNumber;
            this.tasksCount = tasksCount;
            this.workoutsCount = workoutsCount;
            this.today = today;
            this.taskTitles = taskTitles;
            this.workoutTitles = workoutTitles;
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

        public List<String> getTaskTitles() {
            return taskTitles;
        }

        public List<String> getWorkoutTitles() {
            return workoutTitles;
        }
    }
}
