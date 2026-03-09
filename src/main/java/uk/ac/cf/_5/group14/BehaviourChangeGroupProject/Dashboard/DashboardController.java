package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Dashboard;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Dashboard.dto.DashboardSummaryDto;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.HomePage.MiniWeekDay;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Security.SecurityUtils;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Controller
public class DashboardController {

    private static final DateTimeFormatter PRETTY_DATE_FMT =
            DateTimeFormatter.ofPattern("d MMM", Locale.UK);

    private final AuthHelper authHelper;
    private final UserService userService;
    private final DashboardSummaryService dashboardSummaryService;

    public DashboardController(AuthHelper authHelper,
                               UserService userService,
                               DashboardSummaryService dashboardSummaryService) {
        this.authHelper = authHelper;
        this.userService = userService;
        this.dashboardSummaryService = dashboardSummaryService;
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

        // Flag to show "log workout" call-to-action when there are pending workouts today
        model.addAttribute("todayHasWorkoutToLog", summary.getWorkoutsDueToday() > 0);

        // User identity and subscription
        model.addAttribute("userFirstName", user.getFirstName());
        model.addAttribute("userIsPremium", summary.isPremium());

        return "dashboard/client-dashboard";
    }

    @GetMapping("/client/dashboard")
    public String clientDashboard() {
        return "redirect:/dashboard";
    }

    @GetMapping({"/dashboard/public", "/client/dashboard/public"})
    public String clientDashboardPublic(Model model) {
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
}
