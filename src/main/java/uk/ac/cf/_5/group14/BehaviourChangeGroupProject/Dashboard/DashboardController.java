package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Dashboard;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Dashboard.dto.DashboardSummaryDto;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Security.SecurityUtils;

@Controller
public class DashboardController {

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
        return "dashboard/client-dashboard";
    }

    @GetMapping("/client/dashboard")
    public String clientDashboard() {
        return "redirect:/dashboard";
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
