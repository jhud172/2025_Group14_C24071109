package uk.ac.cf._5.group14.One_To_One.Verification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.cf._5.group14.One_To_One.Users.User;
import uk.ac.cf._5.group14.One_To_One.Users.UserRepository;
import uk.ac.cf._5.group14.One_To_One.Users.UserService;
import uk.ac.cf._5.group14.One_To_One.Users.Role;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/gym/admin/trainers")
@RequiredArgsConstructor
public class GymAdminTrainerController {
    
    private final TrainerVerificationService verificationService;
    private final UserRepository userRepository;
    private final UserService userService;
    
    /**
     * List trainers and verification requests for the gym
     */
    @GetMapping
    public String listTrainers(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User admin = getUserFromDetails(userDetails);
        
        if (admin.getGymId() == null) {
            model.addAttribute("error", "You must be associated with a gym");
            return "system-views/error/403";
        }

        populateTrainerModel(admin, model);
        
        return "gym-views/gym-admin/trainers";
    }
    
    /**
     * Create a new trainer and submit for verification
     */
    @PostMapping("/create")
    public String createTrainer(
        @AuthenticationPrincipal UserDetails userDetails,
        @Valid @ModelAttribute("newRequest") TrainerVerificationRequestForm form,
        BindingResult result,
        RedirectAttributes redirectAttributes,
        Model model
    ) {
        User admin = getUserFromDetails(userDetails);
        
        if (admin.getGymId() == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "You must be associated with a gym");
            return "redirect:/gym/admin/trainers";
        }

        if (userService.emailExists(form.getEmail())) {
            result.rejectValue("email", "Duplicate", "Email already registered");
        }
        if (userService.usernameExists(form.getUsername())) {
            result.rejectValue("username", "Duplicate", "Username already taken");
        }
        if (result.hasErrors()) {
            populateTrainerModel(admin, model);
            model.addAttribute("errorMessage", "Please correct the form errors");
            return "gym-views/gym-admin/trainers";
        }

        try {
            User trainer = new User(
                form.getEmail(),
                form.getFirstName(),
                form.getLastName(),
                form.getUsername(),
                form.getTemporaryPassword()
            );
            trainer.setRole(Role.TRAINER);
            trainer.setGymId(admin.getGymId());
            trainer.setTrainerVerified(false);

            User savedTrainer = userService.saveUser(trainer);

            verificationService.createVerificationRequest(
                savedTrainer.getId(),
                admin.getGymId(),
                form.getNotes()
            );

            redirectAttributes.addFlashAttribute("successMessage",
                "Trainer invite created and submitted for verification.");
        } catch (Exception e) {
            log.error("Error creating trainer verification request", e);
            populateTrainerModel(admin, model);
            model.addAttribute("errorMessage", e.getMessage());
            return "gym-views/gym-admin/trainers";
        }
        
        return "redirect:/gym/admin/trainers";
    }
    
    /**
     * Update notes for a verification request that needs more info
     */
    @PostMapping("/{id}/update-notes")
    public String updateNotes(
        @PathVariable Long id,
        @Valid @ModelAttribute("updateNotesForm") UpdateTrainerNotesForm form,
        BindingResult result,
        @AuthenticationPrincipal UserDetails userDetails,
        RedirectAttributes redirectAttributes,
        Model model
    ) {
        User admin = getUserFromDetails(userDetails);
        
        if (admin.getGymId() == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Access denied");
            return "redirect:/gym/admin/trainers";
        }

        TrainerVerificationRequest request;
        try {
            request = verificationService.getRequestForGym(id, admin.getGymId());
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", "Access denied");
            return "system-views/error/403";
        }

        if (result.hasErrors()) {
            populateTrainerModel(admin, model);
            model.addAttribute("updateNotesRequestId", id);
            model.addAttribute("updateNotesTrainerName", resolveTrainerName(request.getTrainerUserId()));
            model.addAttribute("updateNotesNotes", form.getNotes());
            return "gym-views/gym-admin/trainers";
        }

        try {
            verificationService.updateTrainerNotes(id, form.getNotes());
            redirectAttributes.addFlashAttribute("successMessage", 
                "Notes updated. Request resubmitted for review.");
        } catch (Exception e) {
            log.error("Error updating trainer notes", e);
            populateTrainerModel(admin, model);
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("updateNotesRequestId", id);
            model.addAttribute("updateNotesTrainerName", resolveTrainerName(request.getTrainerUserId()));
            model.addAttribute("updateNotesNotes", form.getNotes());
            return "gym-views/gym-admin/trainers";
        }
        
        return "redirect:/gym/admin/trainers";
    }
    
    private User getUserFromDetails(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
            .orElseThrow(() -> new IllegalStateException("User not found"));
    }

    private void populateTrainerModel(User admin, Model model) {
        List<User> trainers = userRepository.findByRoleAndGymId(Role.TRAINER, admin.getGymId());
        Map<Long, TrainerVerificationRequest> latestRequests = new HashMap<>();
        for (User trainer : trainers) {
            TrainerVerificationRequest latest = verificationService.getLatestRequestForTrainer(trainer.getId());
            if (latest != null) {
                latestRequests.put(trainer.getId(), latest);
            }
        }

        model.addAttribute("trainers", trainers);
        model.addAttribute("latestRequests", latestRequests);
        if (!model.containsAttribute("newRequest")) {
            model.addAttribute("newRequest", new TrainerVerificationRequestForm());
        }
        if (!model.containsAttribute("updateNotesForm")) {
            model.addAttribute("updateNotesForm", new UpdateTrainerNotesForm());
        }
    }

    private String resolveTrainerName(Long trainerUserId) {
        return userRepository.findById(trainerUserId)
            .map(user -> user.getFirstName() + " " + user.getLastName())
            .orElse("Trainer");
    }
}
