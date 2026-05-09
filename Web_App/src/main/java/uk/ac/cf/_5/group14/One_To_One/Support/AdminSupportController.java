package uk.ac.cf._5.group14.One_To_One.Support;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.cf._5.group14.One_To_One.DevMode.DevModePageAccessMode;
import uk.ac.cf._5.group14.One_To_One.DevMode.DevModePageAccessService;
import uk.ac.cf._5.group14.One_To_One.GymApplications.GymApplicationService;
import uk.ac.cf._5.group14.One_To_One.Membership.EmailService;
import uk.ac.cf._5.group14.One_To_One.Security.SecurityUtils;
import uk.ac.cf._5.group14.One_To_One.Users.AuthHelper;
import uk.ac.cf._5.group14.One_To_One.Users.User;
import uk.ac.cf._5.group14.One_To_One.Users.UserRepository;
import uk.ac.cf._5.group14.One_To_One.Waitlist.WaitlistEmail;
import uk.ac.cf._5.group14.One_To_One.Waitlist.WaitlistEmailRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Controller
public class AdminSupportController {

    private final SupportRequestRepository supportRequestRepository;
    private final WaitlistEmailRepository waitlistEmailRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final AuthHelper authHelper;
    private final DevModePageAccessService devModePageAccessService;
    private final GymApplicationService gymApplicationService;

    public AdminSupportController(SupportRequestRepository supportRequestRepository,
                                  WaitlistEmailRepository waitlistEmailRepository,
                                  UserRepository userRepository,
                                  EmailService emailService,
                                  AuthHelper authHelper,
                                  DevModePageAccessService devModePageAccessService,
                                  GymApplicationService gymApplicationService) {
        this.supportRequestRepository = supportRequestRepository;
        this.waitlistEmailRepository = waitlistEmailRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.authHelper = authHelper;
        this.devModePageAccessService = devModePageAccessService;
        this.gymApplicationService = gymApplicationService;
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Authentication authentication, Model model) {
        if (!isAdmin(authentication)) {
            return "redirect:/access-denied";
        }

        List<SupportRequest> latestFeedback = supportRequestRepository.findAllByOrderBySubmittedAtDesc();
        List<WaitlistEmail> waitlist = waitlistEmailRepository.findAll();

        model.addAttribute("pageTitle", "Admin Dashboard");
        model.addAttribute("feedbackUnreadCount", supportRequestRepository.countByViewedFalse());
        model.addAttribute("feedbackOngoingCount", supportRequestRepository.countByStatus(SupportRequestStatus.ONGOING));
        model.addAttribute("feedbackNewCount", supportRequestRepository.countByStatus(SupportRequestStatus.NEW));
        model.addAttribute("latestFeedback", latestFeedback.stream().limit(8).toList());
        model.addAttribute("waitlistCount", waitlist.size());
        model.addAttribute("waitlistEntries", waitlist.stream().sorted((a, b) -> b.getSignedUpAt().compareTo(a.getSignedUpAt())).limit(25).toList());
        model.addAttribute("userCount", userRepository.count());
        model.addAttribute("gymApplicationCount", gymApplicationService.countOpenApplications());
        model.addAttribute("latestGymApplications", gymApplicationService.getAllApplications().stream().limit(6).toList());
        model.addAttribute("devPageSummary", devModePageAccessService.buildAdminSummary());
        model.addAttribute("devPageRows", devModePageAccessService.buildAdminRows());
        model.addAttribute("devPageModes", DevModePageAccessMode.values());
        return "admin-views/dashboard/admin-dashboard";
    }

    @PostMapping("/admin/dev-pages/{pageKey}")
    public String updateDevPageAccess(@PathVariable("pageKey") String pageKey,
                                      @RequestParam("mode") String mode,
                                      RedirectAttributes redirectAttributes,
                                      Authentication authentication) {
        if (!isAdmin(authentication)) {
            return "redirect:/access-denied";
        }

        if (!devModePageAccessService.hasPage(pageKey)) {
            redirectAttributes.addFlashAttribute("devPageAccessError", "Unknown Dev Hub page.");
            return "redirect:/admin/dashboard";
        }

        try {
            DevModePageAccessMode accessMode = DevModePageAccessMode.valueOf(mode);
            devModePageAccessService.updateMode(pageKey, accessMode);
            redirectAttributes.addFlashAttribute("devPageAccessSuccess", "Updated " + pageKey + " to " + accessMode.name().toLowerCase() + ".");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("devPageAccessError", "Invalid access mode selected.");
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("devPageAccessError", ex.getMessage());
        }

        return "redirect:/admin/dashboard";
    }

    @GetMapping("/admin/feedback")
    public String feedbackInbox(Authentication authentication, Model model) {
        if (!isAdmin(authentication)) {
            return "redirect:/access-denied";
        }

        model.addAttribute("pageTitle", "Admin Feedback Inbox");
        model.addAttribute("feedbackItems", supportRequestRepository.findAllByOrderBySubmittedAtDesc());
        model.addAttribute("statuses", SupportRequestStatus.values());
        return "admin-views/admin/feedback";
    }

    @PostMapping("/admin/feedback/{id}/viewed")
    public String toggleViewed(@PathVariable("id") Long id,
                               RedirectAttributes redirectAttributes,
                               Authentication authentication) {
        if (!isAdmin(authentication)) {
            return "redirect:/access-denied";
        }

        SupportRequest req = supportRequestRepository.findById(id).orElse(null);
        if (req == null) {
            redirectAttributes.addFlashAttribute("adminFeedbackError", "Feedback item not found.");
            return "redirect:/admin/feedback";
        }

        req.setViewed(!req.isViewed());
        if (req.isViewed() && req.getStatus() == SupportRequestStatus.NEW) {
            req.setStatus(SupportRequestStatus.VIEWED);
        }
        supportRequestRepository.save(req);
        return "redirect:/admin/feedback";
    }

    @PostMapping("/admin/feedback/{id}/status")
    public String updateStatus(@PathVariable("id") Long id,
                               @RequestParam("status") String status,
                               RedirectAttributes redirectAttributes,
                               Authentication authentication) {
        if (!isAdmin(authentication)) {
            return "redirect:/access-denied";
        }

        SupportRequest req = supportRequestRepository.findById(id).orElse(null);
        if (req == null) {
            redirectAttributes.addFlashAttribute("adminFeedbackError", "Feedback item not found.");
            return "redirect:/admin/feedback";
        }

        try {
            req.setStatus(SupportRequestStatus.valueOf(status));
            req.setViewed(true);
            supportRequestRepository.save(req);
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("adminFeedbackError", "Invalid status selected.");
        }
        return "redirect:/admin/feedback";
    }

    @PostMapping("/admin/feedback/{id}/respond")
    public String respond(@PathVariable("id") Long id,
                          @RequestParam("response") String response,
                          RedirectAttributes redirectAttributes,
                          Authentication authentication) {
        if (!isAdmin(authentication)) {
            return "redirect:/access-denied";
        }

        SupportRequest req = supportRequestRepository.findById(id).orElse(null);
        if (req == null) {
            redirectAttributes.addFlashAttribute("adminFeedbackError", "Feedback item not found.");
            return "redirect:/admin/feedback";
        }

        String cleanResponse = response == null ? "" : response.trim();
        if (cleanResponse.isBlank()) {
            redirectAttributes.addFlashAttribute("adminFeedbackError", "Response message cannot be blank.");
            return "redirect:/admin/feedback";
        }

        if (!req.isAllowEmailReply() || req.getSubmitterEmail() == null || req.getSubmitterEmail().isBlank()) {
            redirectAttributes.addFlashAttribute("adminFeedbackError", "This requester did not opt in for email responses.");
            return "redirect:/admin/feedback";
        }

        String subject = "Update on your support request: " + req.getSubject();
        emailService.sendAdminMessage(req.getSubmitterEmail(), subject, cleanResponse);

        User admin = authHelper.getAuthenticatedUser();
        req.setAdminResponse(cleanResponse);
        req.setRespondedAt(Instant.now());
        req.setRespondedBy(admin);
        req.setViewed(true);
        req.setStatus(SupportRequestStatus.RESOLVED);
        supportRequestRepository.save(req);

        redirectAttributes.addFlashAttribute("adminFeedbackSuccess", "Response sent to " + req.getSubmitterEmail());
        return "redirect:/admin/feedback";
    }

    @PostMapping("/admin/outreach/send")
    public String sendOutreach(@RequestParam("audience") String audience,
                               @RequestParam(value = "specificEmail", required = false) String specificEmail,
                               @RequestParam("subject") String subject,
                               @RequestParam("message") String message,
                               RedirectAttributes redirectAttributes,
                               Authentication authentication) {
        if (!isAdmin(authentication)) {
            return "redirect:/access-denied";
        }

        String cleanSubject = subject == null ? "" : subject.trim();
        String cleanMessage = message == null ? "" : message.trim();
        if (cleanSubject.isBlank() || cleanMessage.isBlank()) {
            redirectAttributes.addFlashAttribute("adminOutreachError", "Subject and message are required.");
            return "redirect:/admin/dashboard";
        }

        Set<String> recipients = new LinkedHashSet<>();
        switch (audience) {
            case "ALL_USERS" -> userRepository.findAll().forEach(u -> {
                if (u.getEmail() != null && !u.getEmail().isBlank()) recipients.add(u.getEmail().trim());
            });
            case "WAITLIST" -> waitlistEmailRepository.findAll().forEach(w -> {
                if (w.getEmail() != null && !w.getEmail().isBlank()) recipients.add(w.getEmail().trim());
            });
            case "SPECIFIC" -> {
                String email = specificEmail == null ? "" : specificEmail.trim();
                if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]{2,}$")) {
                    redirectAttributes.addFlashAttribute("adminOutreachError", "Enter a valid recipient email.");
                    return "redirect:/admin/dashboard";
                }
                recipients.add(email);
            }
            default -> {
                redirectAttributes.addFlashAttribute("adminOutreachError", "Invalid audience selected.");
                return "redirect:/admin/dashboard";
            }
        }

        if (recipients.isEmpty()) {
            redirectAttributes.addFlashAttribute("adminOutreachError", "No recipients found for this audience.");
            return "redirect:/admin/dashboard";
        }

        List<String> failed = new ArrayList<>();
        for (String recipient : recipients) {
            try {
                emailService.sendAdminMessage(recipient, cleanSubject, cleanMessage);
            } catch (Exception ex) {
                failed.add(recipient);
            }
        }

        int sent = recipients.size() - failed.size();
        if (failed.isEmpty()) {
            redirectAttributes.addFlashAttribute("adminOutreachSuccess", "Message sent to " + sent + " recipient(s).");
        } else {
            redirectAttributes.addFlashAttribute("adminOutreachError", "Sent to " + sent + " recipient(s), failed for " + failed.size() + ".");
        }

        return "redirect:/admin/dashboard";
    }

    private boolean isAdmin(Authentication authentication) {
        return SecurityUtils.hasRole(authentication, "GYM_ADMIN")
                || SecurityUtils.hasRole(authentication, "PLATFORM_ADMIN")
                || SecurityUtils.hasRole(authentication, "SUPER_ADMIN");
    }
}
