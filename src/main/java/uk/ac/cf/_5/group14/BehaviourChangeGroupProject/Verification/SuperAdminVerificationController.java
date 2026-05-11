package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Verification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserRepository;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/super-admin/verification")
@PreAuthorize("hasRole('SUPER_ADMIN')")
@RequiredArgsConstructor
public class SuperAdminVerificationController {
    
    private final TrainerVerificationService verificationService;
    private final UserRepository userRepository;
    
    /**
     * View the verification queue (pending requests)
     */
    @GetMapping("/queue")
    public String viewQueue(Model model) {
        List<TrainerVerificationRequest> verificationRequests = verificationService.getQueueRequests();
        long pendingCount = verificationService.countByStatus(VerificationStatus.PENDING);
        long needsInfoCount = verificationService.countByStatus(VerificationStatus.NEEDS_INFO);
        long approvedCount = verificationService.countByStatus(VerificationStatus.APPROVED);
        long rejectedCount = verificationService.countByStatus(VerificationStatus.REJECTED);
        
        java.util.Map<Long, User> trainers = new java.util.HashMap<>();
        for (TrainerVerificationRequest request : verificationRequests) {
            userRepository.findById(request.getTrainerUserId())
                .ifPresent(trainer -> trainers.put(request.getTrainerUserId(), trainer));
        }

        model.addAttribute("verificationRequests", verificationRequests);
        model.addAttribute("trainers", trainers);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("needsInfoCount", needsInfoCount);
        model.addAttribute("approvedCount", approvedCount);
        model.addAttribute("rejectedCount", rejectedCount);
        return "super-admin/verification-queue";
    }
    
    /**
     * View details of a specific verification request
     */
    @GetMapping("/{id}")
    public String viewRequest(@PathVariable Long id, Model model) {
        TrainerVerificationRequest request = verificationService.getLatestRequestForTrainer(id);
        
        if (request == null) {
            model.addAttribute("error", "Verification request not found");
            return "error/404";
        }
        
        User trainer = userRepository.findById(request.getTrainerUserId())
            .orElseThrow(() -> new IllegalArgumentException("Trainer not found"));
        
        model.addAttribute("request", request);
        model.addAttribute("trainer", trainer);
        
        return "super-admin-verification-detail";
    }
    
    /**
     * Approve a trainer verification request
     */
    @PostMapping("/{id}/approve")
    public String approveRequest(
        @PathVariable Long id,
        @RequestParam(required = false) String adminNotes,
        @AuthenticationPrincipal UserDetails userDetails,
        RedirectAttributes redirectAttributes
    ) {
        User admin = getUserFromDetails(userDetails);
        
        try {
            verificationService.approveTrainer(id, admin.getId(), adminNotes);
            redirectAttributes.addFlashAttribute("successMessage", "Trainer approved successfully");
        } catch (Exception e) {
            log.error("Error approving trainer", e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/super-admin/verification/queue";
    }
    
    /**
     * Reject a trainer verification request
     */
    @PostMapping("/{id}/reject")
    public String rejectRequest(
        @PathVariable Long id,
        @RequestParam(required = false) String adminNotes,
        @AuthenticationPrincipal UserDetails userDetails,
        RedirectAttributes redirectAttributes
    ) {
        User admin = getUserFromDetails(userDetails);
        
        try {
            verificationService.rejectTrainer(id, admin.getId(), adminNotes);
            redirectAttributes.addFlashAttribute("successMessage", "Trainer verification rejected");
        } catch (Exception e) {
            log.error("Error rejecting trainer", e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/super-admin/verification/queue";
    }
    
    /**
     * Request more information from the trainer
     */
    @PostMapping("/{id}/request-info")
    public String requestMoreInfo(
        @PathVariable Long id,
        @RequestParam String adminNotes,
        @AuthenticationPrincipal UserDetails userDetails,
        RedirectAttributes redirectAttributes
    ) {
        User admin = getUserFromDetails(userDetails);
        
        if (adminNotes == null || adminNotes.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please provide details about what information is needed");
            return "redirect:/super-admin/verification/queue";
        }
        
        try {
            verificationService.requestMoreInfo(id, admin.getId(), adminNotes);
            redirectAttributes.addFlashAttribute("successMessage", "Information requested from trainer");
        } catch (Exception e) {
            log.error("Error requesting more info", e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/super-admin/verification/queue";
    }
    
    private User getUserFromDetails(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
            .orElseThrow(() -> new IllegalStateException("User not found"));
    }
}
