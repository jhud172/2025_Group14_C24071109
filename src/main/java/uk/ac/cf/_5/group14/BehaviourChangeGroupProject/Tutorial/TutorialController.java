package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Tutorial;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Security.SecurityUtils;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserService;

@Controller
@RequestMapping("/tutorial")
public class TutorialController {

    private final AuthHelper authHelper;
    private final UserService userService;

    public TutorialController(AuthHelper authHelper, UserService userService) {
        this.authHelper = authHelper;
        this.userService = userService;
    }

    @GetMapping
    public String showTutorial(Authentication authentication, Model model, HttpSession session) {
        User user = authHelper.getAuthenticatedUser(session);
        if (user == null && authentication != null) {
            user = userService.findByUsername(authentication.getName());
        }
        if (user == null) {
            return "redirect:/login";
        }

        // If they have already seen the tutorial, send them to their dashboard
        if (user.isHasSeenTutorial()) {
            return dashboardRedirect(authentication);
        }

        String role = resolveRole(authentication);
        model.addAttribute("tutorialRole", role);
        model.addAttribute("user", user);
        return "tutorial/tutorial";
    }

    @PostMapping("/complete")
    public String completeTutorial(Authentication authentication, HttpSession session) {
        User user = authHelper.getAuthenticatedUser(session);
        if (user == null && authentication != null) {
            user = userService.findByUsername(authentication.getName());
        }
        if (user != null && !user.isHasSeenTutorial()) {
            userService.markTutorialSeen(user);
            // Refresh session copy so future checks are correct
            user.setHasSeenTutorial(true);
            session.setAttribute("user", user);
        }
        return "redirect:" + dashboardPath(authentication);
    }

    // -------------------------------------------------------------------------

    private String resolveRole(Authentication authentication) {
        if (SecurityUtils.hasRole(authentication, "GYM_ADMIN")) {
            return "GYM";
        }
        if (SecurityUtils.hasRole(authentication, "TRAINER")) {
            return "TRAINER";
        }
        return "CLIENT";
    }

    private String dashboardRedirect(Authentication authentication) {
        return "redirect:" + dashboardPath(authentication);
    }

    private String dashboardPath(Authentication authentication) {
        if (SecurityUtils.hasRole(authentication, "GYM_ADMIN")) {
            return "/gym/dashboard";
        }
        if (SecurityUtils.hasRole(authentication, "TRAINER")) {
            return "/trainer/dashboard";
        }
        return "/dashboard";
    }
}
