package uk.ac.cf._5.group14.One_To_One.Messaging;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.cf._5.group14.One_To_One.Users.AuthHelper;
import uk.ac.cf._5.group14.One_To_One.Users.User;
import uk.ac.cf._5.group14.One_To_One.Users.UserService;

@Controller
public class MessagingController {

    private final AuthHelper authHelper;
    private final UserService userService;
    private final MessagingService messagingService;
    private final uk.ac.cf._5.group14.One_To_One.Security.AccessGuard accessGuard;

    public MessagingController(AuthHelper authHelper,
                               UserService userService,
                               MessagingService messagingService,
                               uk.ac.cf._5.group14.One_To_One.Security.AccessGuard accessGuard) {
        this.authHelper = authHelper;
        this.userService = userService;
        this.messagingService = messagingService;
        this.accessGuard = accessGuard;
    }

    private User currentUserOrThrow() {
        User sessionUser = authHelper.getAuthenticatedUser();
        if (sessionUser != null) {
            return sessionUser;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Not authenticated");
        }
        User user = userService.findByUsername(auth.getName());
        if (user == null) {
            throw new AccessDeniedException("User not found");
        }
        return user;
    }

    @GetMapping("/trainer/messages")
    public String trainerInbox() {
        currentUserOrThrow();
        return "redirect:/inbox";
    }

    @GetMapping("/client/messages")
    public String clientInbox() {
        currentUserOrThrow();
        return "redirect:/inbox";
    }

    @GetMapping("/messages/{threadId}")
    public String thread(@PathVariable Long threadId) {
        User user = currentUserOrThrow();

        try {
            messagingService.getThreadForUser(threadId, user.getId());
            return "redirect:/inbox/" + threadId;
        } catch (AccessDeniedException ex) {
            return "redirect:/access-denied";
        }
    }

    @PostMapping("/messages/{threadId}/send")
    public String send(@PathVariable Long threadId,
                       @RequestParam("type") MessageType type,
                       @RequestParam(value = "bodyText", required = false) String bodyText,
                       @RequestParam(value = "checkinMood", required = false) Integer checkinMood,
                       @RequestParam(value = "checkinEnergy", required = false) Integer checkinEnergy,
                       @RequestParam(value = "checkinNotes", required = false) String checkinNotes,
                       RedirectAttributes redirectAttributes) {
        User sender = currentUserOrThrow();

        try {
            String finalBody;
            if (type == MessageType.CHECKIN) {
                String notes = checkinNotes == null ? "" : checkinNotes.trim();
                finalBody = "Check-in" +
                        "\nMood: " + (checkinMood == null ? "-" : checkinMood) + "/10" +
                        "\nEnergy: " + (checkinEnergy == null ? "-" : checkinEnergy) + "/10" +
                        (notes.isBlank() ? "" : ("\nNotes: " + notes));
            } else {
                finalBody = bodyText;
            }

            // Enforce Active Link Status
            MessageThread thread = messagingService.getThreadForUser(threadId, sender.getId());
            if (sender.getId().equals(thread.getTrainerId())) {
                accessGuard.requireTrainerAccessClient(sender.getId(), thread.getClientId());
            } else {
                accessGuard.requireClientAccessTrainer(sender.getId(), thread.getTrainerId());
            }

            messagingService.sendMessage(threadId, sender.getId(), type, finalBody);
        } catch (AccessDeniedException ex) {
            return "redirect:/access-denied";
        } catch (MessagingException ex) {
            if (ex.getReason() == MessagingException.Reason.OFF_PLATFORM_PAYMENT) {
                redirectAttributes.addFlashAttribute("offPlatformBlocked", true);
            }
            return "redirect:/inbox/" + threadId;
        } catch (IllegalArgumentException ex) {
            return "redirect:/inbox/" + threadId;
        }

        return "redirect:/inbox/" + threadId;
    }
}
