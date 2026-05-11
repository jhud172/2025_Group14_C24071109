package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Verification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.Role;

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
            return "error/403";
        }

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
        model.addAttribute("newRequest", new TrainerVerificationRequestForm());
        
        return "gym-admin/trainers";
    }
    
    /**
     * Create a new trainer and submit for verification
     */
    @PostMapping("/create")
    public String createTrainer(
        @AuthenticationPrincipal UserDetails userDetails,
        @Valid @ModelAttribute TrainerVerificationRequestForm form,
        BindingResult result,
        RedirectAttributes redirectAttributes
    ) {
        User admin = getUserFromDetails(userDetails);
        
        if (admin.getGymId() == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "You must be associated with a gym");
            return "redirect:/gym/admin/trainers";
        }
        
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please correct the form errors");
            return "redirect:/gym/admin/trainers";
        }
        
        try {
            if (userService.emailExists(form.getEmail())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Email already registered");
                return "redirect:/gym/admin/trainers";
            }
            if (userService.usernameExists(form.getUsername())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Username already taken");
                return "redirect:/gym/admin/trainers";
            }

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
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/gym/admin/trainers";
    }
    
    /**
     * Update notes for a verification request that needs more info
     */
    @PostMapping("/{id}/update-notes")
    public String updateNotes(
        @PathVariable Long id,
        @RequestParam String notes,
        @AuthenticationPrincipal UserDetails userDetails,
        RedirectAttributes redirectAttributes
    ) {
        User admin = getUserFromDetails(userDetails);
        
        if (admin.getGymId() == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Access denied");
            return "redirect:/gym/admin/trainers";
        }
        
        try {
            verificationService.updateTrainerNotes(id, notes);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Notes updated. Request resubmitted for review.");
        } catch (Exception e) {
            log.error("Error updating trainer notes", e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/gym/admin/trainers";
    }
    
    private User getUserFromDetails(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
            .orElseThrow(() -> new IllegalStateException("User not found"));
    }
}
