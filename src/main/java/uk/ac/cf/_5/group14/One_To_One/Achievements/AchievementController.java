package uk.ac.cf._5.group14.One_To_One.Achievements;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import uk.ac.cf._5.group14.One_To_One.ExerciseLog.ExerciseLog;
import uk.ac.cf._5.group14.One_To_One.ExerciseLog.ExerciseLogService;
import uk.ac.cf._5.group14.One_To_One.Level.LevelProgress;
import uk.ac.cf._5.group14.One_To_One.Level.LevelService;
import uk.ac.cf._5.group14.One_To_One.PlatformBilling.PlatformSubscription;
import uk.ac.cf._5.group14.One_To_One.PlatformBilling.PlatformSubscriptionService;
import uk.ac.cf._5.group14.One_To_One.Users.AuthHelper;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
public class AchievementController {

    private final AuthHelper authHelper;
    private final ExerciseLogService exerciseLogService;
    private final LevelService levelService;
    private final PlatformSubscriptionService platformSubscriptionService;

    public AchievementController(AuthHelper authHelper,
                                 ExerciseLogService exerciseLogService,
                                 LevelService levelService,
                                 PlatformSubscriptionService platformSubscriptionService) {
        this.authHelper = authHelper;
        this.exerciseLogService = exerciseLogService;
        this.levelService = levelService;
        this.platformSubscriptionService = platformSubscriptionService;
    }

    @GetMapping("/achievements")
    public String index(Model model) {
        User user = authHelper.getAuthenticatedUser();
        if (user == null) {
            return "redirect:/login";
        }

        PlatformSubscription platformSubscription = platformSubscriptionService.findByUserId(user.getId()).orElse(null);
        LevelProgress levelProgress = levelService.getProgress(user);
        List<ExerciseLog> logs = new ArrayList<>(exerciseLogService.getLogsByUser(user));
        logs.sort(Comparator.comparing(ExerciseLog::getDate, Comparator.nullsLast(LocalDate::compareTo)));

        List<MilestoneCardView> unlockedMilestones = new ArrayList<>();
        List<MilestoneCardView> undiscoveredMilestones = new ArrayList<>();

        for (MilestoneDefinition definition : milestoneCatalog()) {
            LocalDate achievedDate = resolveAchievedDate(definition.key(), user, platformSubscription, levelProgress, logs);
            MilestoneCardView card = new MilestoneCardView(
                definition.key(),
                definition.title(),
                definition.howToObtain(),
                achievedDate,
                achievedDate != null
            );

            if (card.unlocked()) {
                unlockedMilestones.add(card);
            } else {
                undiscoveredMilestones.add(card);
            }
        }

        unlockedMilestones.sort(Comparator.comparing(MilestoneCardView::achievedDate, Comparator.reverseOrder()));

        model.addAttribute("unlockedMilestones", unlockedMilestones);
        model.addAttribute("undiscoveredMilestones", undiscoveredMilestones);
        return "achievements/index";
    }

    private List<MilestoneDefinition> milestoneCatalog() {
        return List.of(
            new MilestoneDefinition("FIRST_WORKOUT", "First workout logged", "Log your very first workout session."),
            new MilestoneDefinition("TEN_WORKOUTS", "10 workouts", "Complete and log ten workout sessions."),
            new MilestoneDefinition("LEVEL_5", "Reached Level 5", "Reach Level 5 through consistent activity and points."),
            new MilestoneDefinition("LEVEL_10", "Reached Level 10", "Reach Level 10 through continued progress."),
            new MilestoneDefinition("VERIFIED_EMAIL", "Verified email", "Verify your account email address."),
            new MilestoneDefinition("VERIFIED_PHONE", "Verified phone", "Verify your mobile phone number."),
            new MilestoneDefinition("PREMIUM_MEMBER", "Premium member", "Activate your premium membership.")
        );
    }

    private LocalDate resolveAchievedDate(String key,
                                          User user,
                                          PlatformSubscription subscription,
                                          LevelProgress levelProgress,
                                          List<ExerciseLog> logs) {
        return switch (key) {
            case "FIRST_WORKOUT" -> logs.size() >= 1 ? logs.get(0).getDate() : null;
            case "TEN_WORKOUTS" -> logs.size() >= 10 ? logs.get(9).getDate() : null;
            case "LEVEL_5" -> levelProgress != null && levelProgress.getLevel() >= 5 ? levelProgress.getLastUpdated() : null;
            case "LEVEL_10" -> levelProgress != null && levelProgress.getLevel() >= 10 ? levelProgress.getLastUpdated() : null;
            case "VERIFIED_EMAIL" -> user != null && user.isEmailVerified()
                ? instantToDateOrToday(user.getEmailVerifiedAt())
                : null;
            case "VERIFIED_PHONE" -> user != null && user.isPhoneVerified() && user.getPhoneNumber() != null
                ? instantToDateOrToday(user.getPhoneVerifiedAt())
                : null;
            case "PREMIUM_MEMBER" -> subscription != null
                ? instantToDateOrToday(subscription.getCurrentPeriodEnd())
                : null;
            default -> null;
        };
    }

    private LocalDate instantToDateOrToday(Instant instant) {
        if (instant == null) {
            return LocalDate.now();
        }
        return instant.atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public record MilestoneDefinition(String key, String title, String howToObtain) {
    }

    public record MilestoneCardView(String key, String title, String howToObtain, LocalDate achievedDate, boolean unlocked) {
    }
}
