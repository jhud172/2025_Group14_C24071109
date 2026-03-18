package uk.ac.cf._5.group14.One_To_One.Notifications;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import uk.ac.cf._5.group14.One_To_One.Nutrition.DailyNutritionLog;
import uk.ac.cf._5.group14.One_To_One.Nutrition.DailyNutritionLogRepository;
import uk.ac.cf._5.group14.One_To_One.Nutrition.ProteinTargetService;
import uk.ac.cf._5.group14.One_To_One.StrengthLog.Repository.WorkoutSessionRepository;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
public class ProteinNudgeNotificationScheduler {

    private final WorkoutSessionRepository workoutSessionRepository;
    private final DailyNutritionLogRepository dailyNutritionLogRepository;
    private final NotificationService notificationService;
    private final ProteinTargetService proteinTargetService;
    private final Clock clock;

    public ProteinNudgeNotificationScheduler(WorkoutSessionRepository workoutSessionRepository,
                                             DailyNutritionLogRepository dailyNutritionLogRepository,
                                             NotificationService notificationService,
                                             ProteinTargetService proteinTargetService,
                                             Clock clock) {
        this.workoutSessionRepository = workoutSessionRepository;
        this.dailyNutritionLogRepository = dailyNutritionLogRepository;
        this.notificationService = notificationService;
        this.proteinTargetService = proteinTargetService;
        this.clock = clock;
    }

    @Scheduled(cron = "${app.notifications.proteinNudgeCron:0 0 18 * * *}")
    public void sendProteinNudges() {
        LocalDate today = LocalDate.now(clock);
        List<User> users = workoutSessionRepository.findDistinctUsersWithCompletedWorkouts(today);
        if (users == null || users.isEmpty()) {
            return;
        }

        Instant dayStart = today.atStartOfDay(ZoneId.systemDefault()).toInstant();

        for (User user : users) {
            if (user == null) {
                continue;
            }
            if (notificationService.existsRecentByType(user, NotificationType.PROTEIN_NUDGE, dayStart)) {
                continue;
            }

            DailyNutritionLog log = dailyNutritionLogRepository.findByUserAndDate(user, today).orElse(null);
            int target = proteinTargetService.resolveTargetGrams(user);
            if (log != null) {
                int protein = log.getProteinGrams() != null ? log.getProteinGrams() : 0;
                if (protein >= target) {
                    continue;
                }
            }

            String title = "Protein-first check-in";
            String message = log == null
                    ? "Nice work on today's training. Log your protein so recovery stays smooth."
                    : "Solid session today. A quick protein log helps you close the loop.";
            String ctaUrl = "/nutrition?date=" + today;

            notificationService.create(user, NotificationType.PROTEIN_NUDGE, title, message, ctaUrl);
        }
    }
}
