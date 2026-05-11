package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Reviews;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerProfile.TrainerProfileService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ReviewController {

    private final AuthHelper authHelper;
    private final UserService userService;
    private final UserRepository userRepository;
    private final TrainerReviewService reviewService;
    private final ClientAssessmentService assessmentService;
    private final ReviewModerationService moderationService;
    private final TrainerProfileService profileService;

    public ReviewController(AuthHelper authHelper,
                          UserService userService,
                          UserRepository userRepository,
                          TrainerReviewService reviewService,
                          ClientAssessmentService assessmentService,
                          ReviewModerationService moderationService,
                          TrainerProfileService profileService) {
        this.authHelper = authHelper;
        this.userService = userService;
        this.userRepository = userRepository;
        this.reviewService = reviewService;
        this.assessmentService = assessmentService;
        this.moderationService = moderationService;
        this.profileService = profileService;
    }

    private User currentUserOrThrow() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new org.springframework.security.access.AccessDeniedException("Not authenticated");
        }
        User user = userService.findByUsername(auth.getName());
        if (user == null) {
            throw new org.springframework.security.access.AccessDeniedException("User not found");
        }
        return user;
    }

    // ==================== TRAINER REVIEWS (PUBLIC) ====================

    /**
     * Show trainer public profile with reviews.
     */
    @GetMapping("/trainers/{trainerId}/profile")
    public ModelAndView trainerProfile(@PathVariable Long trainerId) {
        User trainer = userRepository.findById(trainerId)
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found"));

        if (!trainer.isTrainerVerified()) {
            throw new IllegalArgumentException("Trainer not found");
        }

        ModelAndView mav = new ModelAndView("trainer/profile/view");
        mav.addObject("pageTitle", trainer.getFirstName() + " " + trainer.getLastName());
        mav.addObject("trainer", trainer);

        // Add trainer profile with social links
        profileService.getProfileByUserId(trainerId).ifPresent(profile -> 
            mav.addObject("trainerProfile", profile)
        );

        List<TrainerReview> reviews = reviewService.getVisibleReviewsForTrainer(trainerId);
        mav.addObject("reviews", reviews);
        mav.addObject("averageRating", reviewService.getAverageRating(trainerId));
        mav.addObject("reviewCount", reviewService.getReviewCount(trainerId));

        // Add client names
        Map<Long, User> clientsById = new HashMap<>();
        for (TrainerReview review : reviews) {
            userRepository.findById(review.getClientId())
                    .ifPresent(client -> clientsById.put(client.getId(), client));
        }
        mav.addObject("clientsById", clientsById);

        // Check if current user can review
        try {
            User currentUser = currentUserOrThrow();
            boolean canReview = reviewService.canClientReviewTrainer(currentUser.getId(), trainerId);
            mav.addObject("canReview", canReview);
        } catch (Exception e) {
            mav.addObject("canReview", false);
        }

        return mav;
    }

    /**
     * Show review creation form.
     */
    @GetMapping("/trainers/{trainerId}/review")
    public ModelAndView showReviewForm(@PathVariable Long trainerId) {
        User client = currentUserOrThrow();
        User trainer = userRepository.findById(trainerId)
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found"));

        if (!reviewService.canClientReviewTrainer(client.getId(), trainerId)) {
            return new ModelAndView("redirect:/trainers/" + trainerId + "/profile?error=cannot_review");
        }

        ModelAndView mav = new ModelAndView("review/form");
        mav.addObject("pageTitle", "Review " + trainer.getFirstName() + " " + trainer.getLastName());
        mav.addObject("trainer", trainer);
        return mav;
    }

    /**
     * Submit a new review.
     */
    @PostMapping("/trainers/{trainerId}/review")
    public ModelAndView submitReview(@PathVariable Long trainerId,
                                    @RequestParam Integer stars,
                                    @RequestParam(required = false) String tags,
                                    @RequestParam(required = false) String comment,
                                    RedirectAttributes redirectAttributes) {
        User client = currentUserOrThrow();

        try {
            reviewService.createReview(client.getId(), trainerId, stars, tags, comment);
            redirectAttributes.addFlashAttribute("successMessage", "Review submitted successfully!");
            return new ModelAndView("redirect:/trainers/" + trainerId + "/profile");
        } catch (IllegalStateException ex) {
            if (TrainerReviewService.ERROR_REVIEW_ALREADY_EXISTS.equals(ex.getMessage())) {
                return new ModelAndView("redirect:/trainers/" + trainerId + "/profile?error=already_reviewed");
            } else if (TrainerReviewService.ERROR_LINK_NOT_ELIGIBLE.equals(ex.getMessage())) {
                return new ModelAndView("redirect:/trainers/" + trainerId + "/profile?error=not_eligible");
            }
            return new ModelAndView("redirect:/trainers/" + trainerId + "/profile?error=invalid");
        } catch (Exception ex) {
            return new ModelAndView("redirect:/trainers/" + trainerId + "/profile?error=invalid");
        }
    }

    /**
     * Report a review.
     */
    @PostMapping("/reviews/{reviewId}/report")
    public ModelAndView reportReview(@PathVariable Long reviewId,
                                    @RequestParam String reason,
                                    @RequestParam Long trainerId,
                                    RedirectAttributes redirectAttributes) {
        User currentUser = currentUserOrThrow();

        try {
            moderationService.reportReview(reviewId, currentUser.getId(), reason);
            redirectAttributes.addFlashAttribute("successMessage", "Review reported for moderation.");
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to report review.");
        }

        return new ModelAndView("redirect:/trainers/" + trainerId + "/profile");
    }

    // ==================== CLIENT ASSESSMENTS (TRAINER-ONLY) ====================

    /**
     * Show assessment form for a client.
     */
    @GetMapping("/trainer/clients/{clientId}/assessment")
    public ModelAndView showAssessmentForm(@PathVariable Long clientId) {
        User trainer = currentUserOrThrow();
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found"));

        ModelAndView mav = new ModelAndView("client/assessment-form");
        mav.addObject("pageTitle", "Assess " + client.getFirstName() + " " + client.getLastName());
        mav.addObject("client", client);

        // Load existing assessment if any
        assessmentService.getAssessment(trainer.getId(), clientId)
                .ifPresent(assessment -> mav.addObject("assessment", assessment));

        return mav;
    }

    /**
     * Save/update client assessment.
     */
    @PostMapping("/trainer/clients/{clientId}/assessment")
    public ModelAndView saveAssessment(@PathVariable Long clientId,
                                      @RequestParam(required = false) Integer reliabilityScore,
                                      @RequestParam(required = false) Integer communicationScore,
                                      @RequestParam(required = false) String privateNotes,
                                      RedirectAttributes redirectAttributes) {
        User trainer = currentUserOrThrow();

        try {
            assessmentService.saveAssessment(trainer.getId(), clientId, 
                    reliabilityScore, communicationScore, privateNotes);
            redirectAttributes.addFlashAttribute("successMessage", "Assessment saved successfully!");
            return new ModelAndView("redirect:/trainer/clients");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to save assessment: " + ex.getMessage());
            return new ModelAndView("redirect:/trainer/clients/" + clientId + "/assessment");
        }
    }

    /**
     * View all assessments by trainer.
     */
    @GetMapping("/trainer/assessments")
    public ModelAndView viewAssessments() {
        User trainer = currentUserOrThrow();

        ModelAndView mav = new ModelAndView("trainer-assessments");
        mav.addObject("pageTitle", "Client Assessments");

        List<ClientAssessment> assessments = assessmentService.getAllAssessmentsByTrainer(trainer.getId());
        mav.addObject("assessments", assessments);

        // Add client names
        Map<Long, User> clientsById = new HashMap<>();
        for (ClientAssessment assessment : assessments) {
            userRepository.findById(assessment.getClientId())
                    .ifPresent(client -> clientsById.put(client.getId(), client));
        }
        mav.addObject("clientsById", clientsById);

        return mav;
    }

    // ==================== MODERATION (ADMIN-ONLY) ====================

    /**
     * Show moderation dashboard.
     */
    @GetMapping("/admin/moderation")
    public ModelAndView moderationDashboard() {
        User admin = currentUserOrThrow();

        ModelAndView mav = new ModelAndView("moderation-dashboard");
        mav.addObject("pageTitle", "Review Moderation");

        List<ReviewModeration> pending = moderationService.getPendingModerations(admin.getId());
        mav.addObject("pendingModerations", pending);

        // Add review and user details
        Map<Long, TrainerReview> reviewsById = new HashMap<>();
        Map<Long, User> usersById = new HashMap<>();
        
        for (ReviewModeration moderation : pending) {
            reviewService.getVisibleReviewsForTrainer(0L); // This won't work - need to fix
            userRepository.findById(moderation.getReportedByUserId())
                    .ifPresent(user -> usersById.put(user.getId(), user));
        }

        return mav;
    }

    /**
     * Resolve moderation and hide review.
     */
    @PostMapping("/admin/moderation/{moderationId}/hide")
    public ModelAndView hideModerationReview(@PathVariable Long moderationId,
                                            @RequestParam String notes,
                                            RedirectAttributes redirectAttributes) {
        User admin = currentUserOrThrow();

        try {
            moderationService.resolveModerationAndHideReview(moderationId, admin.getId(), notes);
            redirectAttributes.addFlashAttribute("successMessage", "Review hidden successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to hide review: " + ex.getMessage());
        }

        return new ModelAndView("redirect:/admin/moderation");
    }

    /**
     * Resolve moderation but keep review visible.
     */
    @PostMapping("/admin/moderation/{moderationId}/keep")
    public ModelAndView keepModerationReview(@PathVariable Long moderationId,
                                            @RequestParam String notes,
                                            RedirectAttributes redirectAttributes) {
        User admin = currentUserOrThrow();

        try {
            moderationService.resolveModerationKeepVisible(moderationId, admin.getId(), notes);
            redirectAttributes.addFlashAttribute("successMessage", "Review kept visible.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to resolve moderation: " + ex.getMessage());
        }

        return new ModelAndView("redirect:/admin/moderation");
    }
}
