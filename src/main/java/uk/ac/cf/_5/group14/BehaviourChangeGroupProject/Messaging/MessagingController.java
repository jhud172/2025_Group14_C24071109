package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Messaging;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerClient.TrainerClientLink;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerClient.TrainerClientLinkRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerClient.TrainerClientLinkStatus;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserService;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class MessagingController {

    private final AuthHelper authHelper;
    private final UserService userService;
    private final UserRepository userRepository;
    private final TrainerClientLinkRepository linkRepository;
    private final MessagingService messagingService;
    private final uk.ac.cf._5.group14.BehaviourChangeGroupProject.Security.AccessGuard accessGuard;

    public MessagingController(AuthHelper authHelper,
                               UserService userService,
                               UserRepository userRepository,
                               TrainerClientLinkRepository linkRepository,
                               MessagingService messagingService,
                               uk.ac.cf._5.group14.BehaviourChangeGroupProject.Security.AccessGuard accessGuard) {
        this.authHelper = authHelper;
        this.userService = userService;
        this.userRepository = userRepository;
        this.linkRepository = linkRepository;
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
    public String trainerInbox(Model model) {
        User trainer = currentUserOrThrow();

        List<MessageThread> threads = messagingService.getTrainerInboxThreads(trainer.getId());
        List<Long> linkIds = threads.stream().map(MessageThread::getLinkId).distinct().toList();
        Map<Long, TrainerClientLink> linksById = linkRepository.findAllById(linkIds)
                .stream()
                .collect(Collectors.toMap(TrainerClientLink::getId, l -> l));

        List<Long> clientIds = threads.stream().map(MessageThread::getClientId).distinct().toList();
        Map<Long, User> clientsById = userRepository.findAllById(clientIds)
                .stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        model.addAttribute("pageTitle", "Messages");
        model.addAttribute("threads", threads);
        model.addAttribute("linksById", linksById);
        model.addAttribute("clientsById", clientsById);
        return "messages/trainer-inbox";
    }

    @GetMapping("/client/messages")
    public String clientInbox(Model model) {
        User client = currentUserOrThrow();

        List<MessageThread> threads = messagingService.getClientInboxThreads(client.getId());
        MessageThread primaryThread = null;
        if (!threads.isEmpty()) {
            Map<Long, TrainerClientLink> linksById = linkRepository.findAllById(threads.stream().map(MessageThread::getLinkId).distinct().toList())
                    .stream()
                    .collect(Collectors.toMap(TrainerClientLink::getId, l -> l));

            primaryThread = threads.stream()
                    .sorted(Comparator.comparing((MessageThread t) -> {
                        TrainerClientLink link = linksById.get(t.getLinkId());
                        return (link != null && link.getStatus() == TrainerClientLinkStatus.ACTIVE) ? 0 : 1;
                    }).thenComparing(MessageThread::getCreatedAt).reversed())
                    .findFirst()
                    .orElse(null);

            model.addAttribute("linksById", linksById);
        }

        User trainerUser = null;
        if (primaryThread != null) {
            trainerUser = userRepository.findById(primaryThread.getTrainerId()).orElse(null);
        }

        model.addAttribute("pageTitle", "Messages");
        model.addAttribute("thread", primaryThread);
        model.addAttribute("trainerUser", trainerUser);
        return "messages/client-inbox";
    }

    @GetMapping("/messages/{threadId}")
    public String thread(@PathVariable Long threadId, Model model) {
        User user = currentUserOrThrow();

        try {
            MessageThread thread = messagingService.getThreadForUser(threadId, user.getId());
            List<Message> messages = messagingService.getMessagesForThread(threadId, user.getId());

            TrainerClientLink link = linkRepository.findById(thread.getLinkId()).orElse(null);
            TrainerClientLinkStatus linkStatus = link == null ? null : link.getStatus();

            Long otherUserId = user.getId().equals(thread.getClientId()) ? thread.getTrainerId() : thread.getClientId();
            User otherUser = userRepository.findById(otherUserId).orElse(null);

            boolean canSend = thread.getStatus() == MessageThreadStatus.OPEN && linkStatus == TrainerClientLinkStatus.ACTIVE;

            model.addAttribute("pageTitle", "Messages");
            model.addAttribute("thread", thread);
            model.addAttribute("linkStatus", linkStatus);
            model.addAttribute("messages", messages);
            model.addAttribute("currentUserId", user.getId());
            model.addAttribute("otherUser", otherUser);
            model.addAttribute("canSend", canSend);

            return "messages/thread";
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
        } catch (IllegalStateException ex) {
            if ("OFF_PLATFORM_PAYMENT".equals(ex.getMessage())) {
                redirectAttributes.addFlashAttribute("offPlatformBlocked", true);
                return "redirect:/messages/" + threadId;
            }
            // LOCKED or not ACTIVE; keep UX simple and return to thread view.
            return "redirect:/messages/" + threadId;
        } catch (IllegalArgumentException ex) {
            return "redirect:/messages/" + threadId;
        }

        return "redirect:/messages/" + threadId;
    }
}
