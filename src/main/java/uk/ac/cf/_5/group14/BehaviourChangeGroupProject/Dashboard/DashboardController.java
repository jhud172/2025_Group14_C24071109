package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Dashboard;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Dashboard.dto.DashboardSummaryDto;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Goals.Goal;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Goals.GoalService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Goals.GoalStatus;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.HealthDataInput.HealthRecord;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.HealthDataInput.HealthRecordRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.HomePage.MiniWeekDay;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Security.SecurityUtils;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerClient.TrainerClientLinkRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.UserSettings.UserSettings;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.UserSettings.UserSettingsService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserService;

@Controller
public class DashboardController {

    private static final DateTimeFormatter PRETTY_DATE_FMT =
            DateTimeFormatter.ofPattern("d MMM", Locale.UK);

    private final AuthHelper authHelper;
    private final UserService userService;
    private final DashboardSummaryService dashboardSummaryService;
    private final TrainerClientLinkRepository trainerClientLinkRepository;
    private final UserSettingsService userSettingsService;
    private final HealthRecordRepository healthRecordRepository;
    private final GoalService goalService;

    public DashboardController(AuthHelper authHelper,
                               UserService userService,
                               DashboardSummaryService dashboardSummaryService,
                               TrainerClientLinkRepository trainerClientLinkRepository,
                               UserSettingsService userSettingsService,
                               HealthRecordRepository healthRecordRepository,
                               GoalService goalService) {
        this.authHelper = authHelper;
        this.userService = userService;
        this.dashboardSummaryService = dashboardSummaryService;
        this.trainerClientLinkRepository = trainerClientLinkRepository;
        this.userSettingsService = userSettingsService;
        this.healthRecordRepository = healthRecordRepository;
        this.goalService = goalService;
    }

    private User currentUserOrThrow(Authentication authentication) {
        User sessionUser = authHelper.getAuthenticatedUser();
        if (sessionUser != null) {
            return sessionUser;
        }
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new org.springframework.security.access.AccessDeniedException("Not authenticated");
        }
        User user = userService.findByUsername(authentication.getName());
        if (user == null) {
            throw new org.springframework.security.access.AccessDeniedException("User not found");
        }
        return user;
    }

    @GetMapping("/dashboard")
    public String dashboardRouter(Authentication authentication, Model model) {
        if (SecurityUtils.hasRole(authentication, "SUPER_ADMIN") || SecurityUtils.hasRole(authentication, "PLATFORM_ADMIN")) {
            return "redirect:/admin/dashboard";
        }
        if (SecurityUtils.hasRole(authentication, "GYM_ADMIN")) {
            return "redirect:/gym/dashboard";
        }
        if (SecurityUtils.hasRole(authentication, "TRAINER")) {
            return "redirect:/trainer/dashboard";
        }
        model.addAttribute("pageTitle", "Dashboard");
        model.addAttribute("disableChatHistory", true);
        model.addAttribute("compactTopContent", true);
        User user = currentUserOrThrow(authentication);
        DashboardSummaryDto summary = dashboardSummaryService.getSummary(user);
        model.addAttribute("summary", summary);

        // Build miniWeek list for the client dashboard template
        List<MiniWeekDay> miniWeek = summary.getWeek().stream()
                .map(day -> new MiniWeekDay(
                        day.getDate(),
                        day.getLabel(),
                        day.getDate().format(PRETTY_DATE_FMT),
                        day.getTasksCount(),
                        day.getWorkoutsCount(),
                        day.isToday(),
                    day.isTomorrow(),
                    day.getDayStatus(),
                        day.getWorkoutsCount() > 0,
                        day.getTaskTitles(),
                        day.getWorkoutTitles()
                ))
                .toList();
        model.addAttribute("miniWeek", miniWeek);

        // Workout and activity stats for progress indicators
        int weekPlannedCount = summary.getWeek().stream()
                .mapToInt(DashboardSummaryDto.WeekDaySummary::getWorkoutsCount)
                .sum();
        model.addAttribute("logsThisWeekCount", summary.getLogsThisWeekCount());
        model.addAttribute("weekCompletedCount", summary.getLogsThisWeekCount());
        model.addAttribute("weekPlannedCount", weekPlannedCount);
        model.addAttribute("streakCount", summary.getWorkoutStreak());
        model.addAttribute("logsLast7DaysCount", summary.getLogsThisWeekCount());
        model.addAttribute("charlieContext", summary.getCharlieContext());

        List<Goal> userGoals = goalService.listGoalsForViewer(user, null, null, null, false);
        long activeGoalsCount = userGoals.stream().filter(goal -> goal.getStatus() == GoalStatus.ACTIVE).count();
        long completedGoalsCount = userGoals.stream().filter(goal -> goal.getStatus() == GoalStatus.COMPLETED).count();
        List<GoalPreviewCard> goalPreviewCards = userGoals.stream()
            .limit(3)
            .map(goal -> new GoalPreviewCard(
                goal.getId(),
                goal.getTitle(),
                goal.getStatus() != null ? goal.getStatus().name() : "DRAFT"))
            .toList();
        model.addAttribute("goalPreviewCards", goalPreviewCards);
        model.addAttribute("goalCount", userGoals.size());
        model.addAttribute("activeGoalsCount", activeGoalsCount);
        model.addAttribute("completedGoalsCount", completedGoalsCount);

        int weekTasksTotal = summary.getWeek().stream().mapToInt(DashboardSummaryDto.WeekDaySummary::getTasksCount).sum();
        int weekTasksCompleted = summary.getWeek().stream().mapToInt(DashboardSummaryDto.WeekDaySummary::getTasksCompletedCount).sum();
        int weekTasksNotCompleted = Math.max(weekTasksTotal - weekTasksCompleted, 0);
        int weekWorkoutsTotal = summary.getWeek().stream().mapToInt(DashboardSummaryDto.WeekDaySummary::getWorkoutsCount).sum();
        int weekWorkoutsCompleted = summary.getWeek().stream().mapToInt(DashboardSummaryDto.WeekDaySummary::getWorkoutsCompletedCount).sum();
        int weekWorkoutsRemaining = Math.max(weekWorkoutsTotal - weekWorkoutsCompleted, 0);

        UserSettings settings = userSettingsService.getOrCreate(user);
        String profileBannerTheme = settings != null && settings.getProfileBannerTheme() != null
            ? settings.getProfileBannerTheme()
            : "AURORA";
        String profileRingStyle = settings != null && settings.getProfileRingStyle() != null
            ? settings.getProfileRingStyle()
            : "NEON_DUAL";
        String profileCardBackStyle = settings != null && settings.getProfileCardBackStyle() != null
            ? settings.getProfileCardBackStyle()
            : "GLASS";

        List<String> selectedMilestoneTitles = parseProfileMilestones(settings != null ? settings.getProfileMilestoneKeys() : null).stream()
            .map(this::mapMilestoneTitle)
            .toList();

        model.addAttribute("profileBannerTheme", profileBannerTheme);
        model.addAttribute("profileRingStyle", profileRingStyle);
        model.addAttribute("profileCardBackStyle", profileCardBackStyle);
        model.addAttribute("selectedProfileMilestoneTitles", selectedMilestoneTitles);
        model.addAttribute("weeklySummaryCards",
            buildWeeklySummaryCards(
                user,
                settings,
                summary,
                weekWorkoutsCompleted,
                weekWorkoutsRemaining,
                weekTasksCompleted,
                weekTasksNotCompleted,
                weekTasksTotal));

        // Flag to show "log workout" call-to-action when there are pending workouts today
        model.addAttribute("todayHasWorkoutToLog", summary.getWorkoutsDueToday() > 0);

        // User identity and subscription
        model.addAttribute("userFirstName", user.getFirstName());
        model.addAttribute("userIsPremium", summary.isPremium());
        model.addAttribute("hasTrainerConnected", trainerClientLinkRepository.findActiveByClientId(user.getId()).isPresent());

        return "dashboard/client-dashboard";
    }

    @GetMapping("/client/dashboard")
    public String clientDashboard() {
        return "redirect:/dashboard";
    }

    @GetMapping({"/dashboard/public", "/client/dashboard/public"})
    public String clientDashboardPublic(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/dashboard";
        }
        model.addAttribute("pageTitle", "One to One");
        return "dashboard/client-dashboard-public";
    }

    @GetMapping("/trainer/dashboard")
    public String trainerDashboard(Authentication authentication, Model model) {
        model.addAttribute("pageTitle", "Dashboard");
        model.addAttribute("disableChatHistory", true);
        User trainer = currentUserOrThrow(authentication);
        DashboardSummaryDto summary = dashboardSummaryService.getSummary(trainer);
        model.addAttribute("summary", summary);

        return "dashboard/trainer-dashboard";
    }

    @GetMapping("/gym/dashboard")
    public String gymDashboard(Authentication authentication, Model model) {
        model.addAttribute("pageTitle", "Dashboard");
        model.addAttribute("disableChatHistory", true);
        User admin = currentUserOrThrow(authentication);
        DashboardSummaryDto summary = dashboardSummaryService.getSummary(admin);
        model.addAttribute("summary", summary);
        return "dashboard/gym-dashboard";
    }

    private List<WeeklySummaryCard> buildWeeklySummaryCards(User user,
                                                             UserSettings settings,
                                                             DashboardSummaryDto summary,
                                                             int weekWorkoutsCompleted,
                                                             int weekWorkoutsRemaining,
                                                             int weekTasksCompleted,
                                                             int weekTasksNotCompleted,
                                                             int weekTasksTotal) {
        List<String> selected = parseWeeklyMetricKeys(settings != null ? settings.getWeeklySummaryMetrics() : null);

        List<HealthRecord> topHealthRecords = user != null
            ? healthRecordRepository.findTop2ByUserOrderByBaselineDateDescIdDesc(user)
                : List.of();

        List<WeeklySummaryCard> cards = new ArrayList<>();
        for (String key : selected) {
            switch (key) {
                case "WORKOUTS_COMPLETED" -> cards.add(new WeeklySummaryCard("Workouts completed", weekWorkoutsCompleted + " / " + (weekWorkoutsCompleted + weekWorkoutsRemaining), "Completed in your selected week"));
                case "WORKOUTS_REMAINING" -> cards.add(new WeeklySummaryCard("Workouts remaining", String.valueOf(weekWorkoutsRemaining), "Still to complete this week"));
                case "TASKS_COMPLETED" -> cards.add(new WeeklySummaryCard("Tasks completed", weekTasksCompleted + " / " + weekTasksTotal, "Completed in your selected week"));
                case "TASKS_NOT_COMPLETED" -> cards.add(new WeeklySummaryCard("Tasks not completed", String.valueOf(weekTasksNotCompleted), "Not yet completed this week"));
                case "MEALS_LOGGED" -> cards.add(new WeeklySummaryCard("Meals logged", String.valueOf(summary.getLogsThisWeekCount()), "Nutrition logs captured"));
                case "HABITS_COMPLETED" -> cards.add(new WeeklySummaryCard("Habits completed", String.valueOf(weekTasksCompleted), "Marked complete across the week"));
                case "HABITS_NOT_COMPLETED" -> cards.add(new WeeklySummaryCard("Habits not completed", String.valueOf(weekTasksNotCompleted), "Not yet marked complete"));
                case "WEIGHT_TREND" -> cards.add(buildWeightTrendCard(topHealthRecords));
                case "WORKOUT_STREAK" -> cards.add(new WeeklySummaryCard("Workout streak", summary.getWorkoutStreak() + " days", "Consecutive active days"));
                case "NUTRITION_STREAK" -> cards.add(new WeeklySummaryCard("Nutrition streak", summary.getLogsThisWeekCount() + " days", "Recent nutrition consistency"));
                default -> {
                }
            }
            if (cards.size() >= 6) {
                break;
            }
        }

        if (cards.isEmpty()) {
            cards.add(new WeeklySummaryCard("Workouts completed", weekWorkoutsCompleted + " / " + (weekWorkoutsCompleted + weekWorkoutsRemaining), "Completed in your selected week"));
            cards.add(new WeeklySummaryCard("Meals logged", String.valueOf(summary.getLogsThisWeekCount()), "Nutrition logs captured"));
            cards.add(new WeeklySummaryCard("Habits completed", String.valueOf(weekTasksCompleted), "Marked complete across the week"));
        }
        return cards;
    }

    private WeeklySummaryCard buildWeightTrendCard(List<HealthRecord> records) {
        if (records == null || records.isEmpty() || records.get(0).getWeightKg() == null) {
            return new WeeklySummaryCard("Weight trend", "Not tracked", "Add body records to track progress");
        }

        Double latest = records.get(0).getWeightKg();
        if (records.size() < 2 || records.get(1).getWeightKg() == null) {
            return new WeeklySummaryCard("Weight trend", String.format(Locale.UK, "%.1f kg", latest), "Latest logged weight");
        }

        Double previous = records.get(1).getWeightKg();
        double delta = latest - previous;
        String direction = delta == 0 ? "no change" : (delta < 0 ? "down" : "up");
        String value = String.format(Locale.UK, "%+.1f kg", delta);
        return new WeeklySummaryCard("Weight trend", value, "Compared to previous log (" + direction + ")");
    }

    private List<String> parseWeeklyMetricKeys(String raw) {
        Set<String> keys = new LinkedHashSet<>();
        if (raw != null && !raw.isBlank()) {
            for (String value : raw.split(",")) {
                if (value != null && !value.isBlank()) {
                    keys.add(value.trim().toUpperCase(Locale.ROOT));
                }
                if (keys.size() >= 6) {
                    break;
                }
            }
        }
        if (keys.isEmpty()) {
            keys.add("WORKOUTS_COMPLETED");
            keys.add("MEALS_LOGGED");
            keys.add("HABITS_COMPLETED");
        }
        return List.copyOf(keys);
    }

    private List<String> parseProfileMilestones(String raw) {
        Set<String> keys = new LinkedHashSet<>();
        if (raw == null || raw.isBlank()) {
            return List.of();
        }
        for (String part : raw.split(",")) {
            if (part != null && !part.isBlank()) {
                keys.add(part.trim().toUpperCase(Locale.ROOT));
            }
            if (keys.size() >= 6) {
                break;
            }
        }
        return List.copyOf(keys);
    }

    private String mapMilestoneTitle(String key) {
        return switch (key) {
            case "FIRST_WORKOUT" -> "First workout logged";
            case "TEN_WORKOUTS" -> "10 workouts";
            case "LEVEL_5" -> "Reached Level 5";
            case "LEVEL_10" -> "Reached Level 10";
            case "VERIFIED_EMAIL" -> "Verified email";
            case "VERIFIED_PHONE" -> "Verified phone";
            case "PREMIUM_MEMBER" -> "Premium member";
            default -> "Milestone";
        };
    }

    public record WeeklySummaryCard(String title, String value, String subtitle) {
    }

    public record GoalPreviewCard(Long id, String title, String status) {
    }
}
