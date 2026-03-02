package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Notifications;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTask;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTaskRepository;

import lombok.extern.slf4j.Slf4j;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
public class UpcomingTaskNotificationScheduler {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private final CalendarTaskRepository calendarTaskRepository;
    private final NotificationService notificationService;
    private final Clock clock;

    public UpcomingTaskNotificationScheduler(CalendarTaskRepository calendarTaskRepository,
                                             NotificationService notificationService,
                                             Clock clock) {
        this.calendarTaskRepository = calendarTaskRepository;
        this.notificationService = notificationService;
        this.clock = clock;
    }

    @Scheduled(fixedDelay = 60_000)
    public void sendUpcomingTaskNotifications() {
        try {
            LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.MINUTES);
            LocalDate date = now.toLocalDate();
            LocalTime from = now.toLocalTime();
            LocalTime to = from.plusMinutes(15);

            List<CalendarTask> tasks = calendarTaskRepository.findUpcomingTasks(date, from, to);
            if (tasks == null || tasks.isEmpty()) return;

            Instant cutoff = Instant.now(clock).minus(30, ChronoUnit.MINUTES);
            for (CalendarTask task : tasks) {
                try {
                    if (task == null || task.getUser() == null) continue;
                    if (Boolean.TRUE.equals(task.getCompleted())) continue;

                    String title = task.getTitle() != null ? task.getTitle().trim() : "Task";
                    String timeLabel = task.getTime() != null ? task.getTime().format(TIME_FORMAT) : "soon";
                    String message = "Upcoming: " + title + " starts at " + timeLabel;

                    if (notificationService.existsRecent(task.getUser(), NotificationType.SYSTEM, message, cutoff)) {
                        continue;
                    }

                    notificationService.create(task.getUser(), NotificationType.SYSTEM, "Schedule reminder", message);
                } catch (Exception e) {
                    // Log and continue with next task - don't let one failure break the scheduler
                    log.warn("Failed to send notification for task {}: {}", task != null ? task.getId() : "null", e.getMessage());
                }
            }
        } catch (Exception e) {
            // Log but don't throw - scheduled tasks should be resilient
            log.error("Error in upcoming task notification scheduler: {}", e.getMessage(), e);
        }
    }
}
