package uk.ac.cf._5.group14.One_To_One.PublicProfile;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import uk.ac.cf._5.group14.One_To_One.Reviews.TrainerReview;
import uk.ac.cf._5.group14.One_To_One.Reviews.TrainerReviewService;
import uk.ac.cf._5.group14.One_To_One.TrainerProfile.TrainerProfile;
import uk.ac.cf._5.group14.One_To_One.TrainerProfile.TrainerProfileService;
import uk.ac.cf._5.group14.One_To_One.Users.Role;
import uk.ac.cf._5.group14.One_To_One.Users.User;
import uk.ac.cf._5.group14.One_To_One.Users.UserLookupService;

@Controller
@RequestMapping("/u")
public class PublicProfileController {

    private final UserLookupService userLookupService;
    private final TrainerProfileService trainerProfileService;
    private final TrainerReviewService trainerReviewService;

    public PublicProfileController(UserLookupService userLookupService,
                                   TrainerProfileService trainerProfileService,
                                   TrainerReviewService trainerReviewService) {
        this.userLookupService = userLookupService;
        this.trainerProfileService = trainerProfileService;
        this.trainerReviewService = trainerReviewService;
    }

    @GetMapping("/{username}")
    public ModelAndView publicProfile(@PathVariable String username) {
        User user = userLookupService.findByUsernameOrNull(username);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        if (user.getRole() != Role.TRAINER) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        if (!user.isTrainerVerified()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        TrainerProfile trainerProfile = trainerProfileService.getProfileByUserId(user.getId())
                .orElse(null);
        List<TrainerReview> reviews = trainerReviewService.getVisibleReviewsForTrainer(user.getId());

        ModelAndView mav = new ModelAndView("public-views/public/profile");
        mav.addObject("pageTitle", user.getFirstName() + " " + user.getLastName());
        mav.addObject("trainer", user);
        mav.addObject("trainerProfile", trainerProfile);
        mav.addObject("reviews", reviews);
        mav.addObject("averageRating", trainerReviewService.getAverageRating(user.getId()));
        mav.addObject("reviewCount", trainerReviewService.getReviewCount(user.getId()));
        mav.addObject("showGymAffiliation", user.isTrainerVerified() && user.getGymId() != null);
        mav.addObject("gymAffiliation", user.getGymId());
        mav.addObject("initials", initialsFor(user));
        return mav;
    }

    @GetMapping("/{username}/dashboard")
    public String vanityDashboard(@PathVariable String username, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        User currentUser = userLookupService.findByUsernameOrNull(authentication.getName());
        if (currentUser == null || !currentUser.getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return "redirect:/dashboard";
    }

    private String initialsFor(User user) {
        String first = user.getFirstName() != null ? user.getFirstName().trim() : "";
        String last = user.getLastName() != null ? user.getLastName().trim() : "";
        String firstInitial = first.isEmpty() ? "" : first.substring(0, 1).toUpperCase();
        String lastInitial = last.isEmpty() ? "" : last.substring(0, 1).toUpperCase();
        String initials = (firstInitial + lastInitial).trim();
        return initials.isEmpty() ? "?" : initials;
    }
}
