package uk.ac.cf._5.group14.One_To_One.Tutorial;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import uk.ac.cf._5.group14.One_To_One.Security.SecurityUtils;
import uk.ac.cf._5.group14.One_To_One.Users.AuthHelper;
import uk.ac.cf._5.group14.One_To_One.Users.User;
import uk.ac.cf._5.group14.One_To_One.Users.UserService;

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
    public String showTutorial(Authentication authentication,
                               HttpSession session,
                               @RequestParam(name = "restart", defaultValue = "false") boolean restart) {
        User user = authHelper.getAuthenticatedUser(session);
        if (user == null && authentication != null) {
            user = userService.findByUsername(authentication.getName());
        }
        if (user == null) {
            return "redirect:/login";
        }

        // Completed users can replay explicitly without resetting completion.
        if (user.isHasSeenTutorial() && !restart) {
            return dashboardRedirect(authentication);
        }

        return "redirect:" + dashboardPath(authentication) + "?tour=start";
    }

    @PostMapping("/complete")
    public String completeTutorial(Authentication authentication, HttpSession session) {
        User user = authHelper.getAuthenticatedUser(session);
        if (user == null && authentication != null) {
            user = userService.findByUsername(authentication.getName());
        }
        if (user != null && !user.isHasSeenTutorial()) {
            userService.markTutorialSeen(user);
        }
        return "redirect:" + dashboardPath(authentication);
    }

    // -------------------------------------------------------------------------

    private String dashboardRedirect(Authentication authentication) {
        return "redirect:" + dashboardPath(authentication);
    }

    private String dashboardPath(Authentication authentication) {
        if (SecurityUtils.hasRole(authentication, "PLATFORM_ADMIN")
                || SecurityUtils.hasRole(authentication, "SUPER_ADMIN")) {
            return "/admin/dashboard";
        }
        if (SecurityUtils.hasRole(authentication, "GYM_ADMIN")) {
            return "/gym/dashboard";
        }
        if (SecurityUtils.hasRole(authentication, "TRAINER")) {
            return "/trainer/dashboard";
        }
        return "/dashboard";
    }
}
