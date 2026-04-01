package uk.ac.cf._5.group14.One_To_One.TrainerProfile;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.cf._5.group14.One_To_One.Users.AuthHelper;
import uk.ac.cf._5.group14.One_To_One.Users.User;
import uk.ac.cf._5.group14.One_To_One.Users.UserRepository;

@Controller
@RequestMapping("/trainer/profile")
public class TrainerProfileController {

    private final TrainerProfileService profileService;
    private final AuthHelper authHelper;
    private final UserRepository userRepository;

    public TrainerProfileController(TrainerProfileService profileService,
                                   AuthHelper authHelper,
                                   UserRepository userRepository) {
        this.profileService = profileService;
        this.authHelper = authHelper;
        this.userRepository = userRepository;
    }

    /**
     * Show trainer profile edit page.
     */
    @GetMapping("/edit")
    public ModelAndView showEditProfile() {
        User currentUser = authHelper.getAuthenticatedUser();
        TrainerProfile profile = profileService.getOrCreateProfile(currentUser.getId());

        ModelAndView mav = new ModelAndView("trainer-views/trainer/profile/edit");
        mav.addObject("pageTitle", "Edit Profile");
        mav.addObject("profile", profile);
        mav.addObject("user", currentUser);
        return mav;
    }

    /**
     * Save trainer profile updates.
     */
    @PostMapping("/save")
    public String saveProfile(@ModelAttribute TrainerProfile updatedProfile,
                            RedirectAttributes redirectAttributes) {
        try {
            User currentUser = authHelper.getAuthenticatedUser();
            profileService.updateProfile(currentUser.getId(), updatedProfile);
            
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
            return "redirect:/trainer/profile/edit";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/trainer/profile/edit";
        }
    }
}
