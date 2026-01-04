package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Level;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

/**
 * Controller exposing endpoints for viewing the leaderboard and a user's progress.
 */
@Controller
@RequestMapping("/levels")
public class LevelController {

    private final LevelService levelService;
    private final AuthHelper authHelper;

    public LevelController(LevelService levelService, AuthHelper authHelper) {
        this.levelService = levelService;
        this.authHelper = authHelper;
    }

    @GetMapping
    public String leaderboard(Model model) {
        model.addAttribute("leaderboard", levelService.getLeaderboard());
        return "levels/leaderboard";
    }

    @GetMapping("/me")
    public String myLevel(Model model, HttpSession session) {
        User user = authHelper.getAuthenticatedUser(session);
        model.addAttribute("progress", levelService.getProgress(user));
        model.addAttribute("leaderboard", levelService.getLeaderboard());
        return "levels/me";
    }
}
