package uk.ac.cf._5.group14.One_To_One.Dashboard.dto;

public class TimelineMonthDto {

    private String month;
    private int totalLogs;
    private int totalSessions;
    private int totalTasks;

    public TimelineMonthDto(String month, int totalLogs, int totalSessions, int totalTasks) {
        this.month = month;
        this.totalLogs = totalLogs;
        this.totalSessions = totalSessions;
        this.totalTasks = totalTasks;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public int getTotalLogs() {
        return totalLogs;
    }

    public void setTotalLogs(int totalLogs) {
        this.totalLogs = totalLogs;
    }

    public int getTotalSessions() {
        return totalSessions;
    }

    public void setTotalSessions(int totalSessions) {
        this.totalSessions = totalSessions;
    }

    public int getTotalTasks() {
        return totalTasks;
    }

    public void setTotalTasks(int totalTasks) {
        this.totalTasks = totalTasks;
    }
}
