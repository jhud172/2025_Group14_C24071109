package uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerClient;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.Role;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserService;

import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class TrainerClientLinkController {

    private final AuthHelper authHelper;
    private final UserService userService;
    private final UserRepository userRepository;
    private final TrainerClientLinkService trainerClientLinkService;

    public TrainerClientLinkController(AuthHelper authHelper,
                                     UserService userService,
                                     UserRepository userRepository,
                                     TrainerClientLinkService trainerClientLinkService) {
        this.authHelper = authHelper;
        this.userService = userService;
        this.userRepository = userRepository;
        this.trainerClientLinkService = trainerClientLinkService;
    }

    private User currentUserOrThrow() {
        User sessionUser = authHelper.getAuthenticatedUser();
        if (sessionUser != null) {
            return sessionUser;
        }

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

    @PostMapping({"/client/trainers/{trainerId}/request", "/trainers/{trainerId}/request"})
    public ModelAndView requestTrainer(@PathVariable Long trainerId, RedirectAttributes redirectAttributes) {
        User client = currentUserOrThrow();

        if (client.getRole() != Role.CLIENT) {
            return new ModelAndView("redirect:/access-denied");
        }

        if (isAccountIncomplete(client)) {
            redirectAttributes.addFlashAttribute(
                "verifyError",
                "Verify your email and phone before requesting a trainer."
            );
            return new ModelAndView("redirect:/client/trainers");
        }

        try {
            trainerClientLinkService.requestLink(client.getId(), trainerId);
            redirectAttributes.addFlashAttribute("successMessage", "Trainer request sent.");
        } catch (IllegalStateException ex) {
            if (TrainerClientLinkService.ERROR_CLIENT_ALREADY_HAS_ACTIVE_TRAINER.equals(ex.getMessage())) {
                return new ModelAndView("redirect:/client/trainers?error=active");
            }
            if (TrainerClientLinkService.ERROR_TRAINER_NOT_VERIFIED.equals(ex.getMessage())) {
                return new ModelAndView("redirect:/client/trainers?error=trainer-unverified");
            }
            return new ModelAndView("redirect:/client/trainers?error=invalid");
        } catch (IllegalArgumentException ex) {
            return new ModelAndView("redirect:/client/trainers?error=invalid");
        }

        return new ModelAndView("redirect:/client/trainers");
    }

    private boolean isAccountIncomplete(User user) {
        if (user == null) {
            return true;
        }
        boolean emailUnverified = !user.isEmailVerified();
        boolean phoneUnverified = user.getPhoneNumber() != null && !user.getPhoneNumber().isBlank() && !user.isPhoneVerified();
        return emailUnverified || phoneUnverified;
    }

    @GetMapping("/trainer/requests")
    public ModelAndView trainerRequestsRedirect() {
        return new ModelAndView("redirect:/trainer/clients");
    }

    @GetMapping("/trainer/clients")
    public ModelAndView trainerClients() {
        User trainer = currentUserOrThrow();
        ModelAndView mav = new ModelAndView("trainer/clients");
        mav.addObject("pageTitle", "Trainer Clients");

        List<TrainerClientLink> allLinks = trainerClientLinkService.listTrainerClients(trainer.getId());
        List<TrainerClientLink> requests = allLinks.stream()
                .filter(link -> link.getStatus() == TrainerClientLinkStatus.REQUESTED)
                .toList();
        List<TrainerClientLink> current = allLinks.stream()
                .filter(link -> link.getStatus() == TrainerClientLinkStatus.ACTIVE || link.getStatus() == TrainerClientLinkStatus.PAUSED)
                .toList();

        mav.addObject("requests", requests);
        mav.addObject("currentLinks", current);

        List<Long> clientIds = allLinks.stream().map(TrainerClientLink::getClientUserId).distinct().toList();
        Map<Long, User> clientsById = userRepository.findAllById(clientIds)
                .stream()
                .collect(Collectors.toMap(User::getId, u -> u));
        mav.addObject("clientsById", clientsById);
        return mav;
    }

    @PostMapping("/trainer/clients/{clientId}/accept")
    public ModelAndView accept(@PathVariable Long clientId, RedirectAttributes redirectAttributes) {
        User trainer = currentUserOrThrow();
        try {
            trainerClientLinkService.acceptRequest(trainer.getId(), clientId);
            redirectAttributes.addFlashAttribute("successMessage", "Client request accepted.");
        } catch (org.springframework.security.access.AccessDeniedException ex) {
            return new ModelAndView("redirect:/access-denied");
        } catch (IllegalStateException ex) {
            if (TrainerClientLinkService.ERROR_CLIENT_ALREADY_HAS_ACTIVE_TRAINER.equals(ex.getMessage())) {
                return new ModelAndView("redirect:/trainer/clients?error=client-active");
            }
            if (TrainerClientLinkService.ERROR_TRAINER_NOT_VERIFIED.equals(ex.getMessage())) {
                return new ModelAndView("redirect:/trainer/clients?error=trainer-unverified");
            }
            return new ModelAndView("redirect:/trainer/clients?error=invalid");
        } catch (IllegalArgumentException ex) {
            return new ModelAndView("redirect:/trainer/clients?error=invalid");
        }
        return new ModelAndView("redirect:/trainer/clients");
    }

    @PostMapping("/trainer/clients/{clientId}/pause")
    public ModelAndView pause(@PathVariable Long clientId, RedirectAttributes redirectAttributes) {
        User trainer = currentUserOrThrow();
        try {
            trainerClientLinkService.pauseLink(trainer.getId(), clientId);
            redirectAttributes.addFlashAttribute("successMessage", "Client relationship paused.");
        } catch (org.springframework.security.access.AccessDeniedException ex) {
            return new ModelAndView("redirect:/access-denied");
        } catch (IllegalStateException | IllegalArgumentException ex) {
            return new ModelAndView("redirect:/trainer/clients?error=invalid");
        }
        return new ModelAndView("redirect:/trainer/clients");
    }

    @PostMapping("/trainer/clients/{clientId}/end")
    public ModelAndView end(@PathVariable Long clientId, RedirectAttributes redirectAttributes) {
        User trainer = currentUserOrThrow();
        try {
            trainerClientLinkService.endLink(trainer.getId(), clientId);
            redirectAttributes.addFlashAttribute("successMessage", "Client relationship ended.");
        } catch (org.springframework.security.access.AccessDeniedException ex) {
            return new ModelAndView("redirect:/access-denied");
        } catch (IllegalStateException | IllegalArgumentException ex) {
            return new ModelAndView("redirect:/trainer/clients?error=invalid");
        }
        return new ModelAndView("redirect:/trainer/clients");
    }

    @GetMapping("/client/my-trainer")
    public ModelAndView myTrainerRedirect() {
        return new ModelAndView("redirect:/client/trainers");
    }

    @GetMapping("/client/trainers")
    public ModelAndView myTrainers(@RequestParam(value = "error", required = false) String error,
                                   @RequestParam(value = "q", required = false) String q) {
        User client = currentUserOrThrow();

        ModelAndView mav = new ModelAndView("client/trainers");
        mav.addObject("pageTitle", "Trainers");

        TrainerClientLink active = trainerClientLinkService.getActiveLinkForClient(client.getId());
        mav.addObject("activeLink", active);

        if (active != null) {
            mav.addObject("trainer", userRepository.findById(active.getTrainerUserId()).orElse(null));
        } else {
            mav.addObject("trainer", null);
        }

        List<User> trainers = userRepository.findByRoleAndTrainerVerifiedTrue(Role.TRAINER);
        if (q != null && !q.isBlank()) {
            String query = q.trim().toLowerCase();
            trainers = trainers.stream()
                    .filter(t -> {
                        String fullName = (t.getFullName() == null) ? "" : t.getFullName().toLowerCase();
                        String username = (t.getUsername() == null) ? "" : t.getUsername().toLowerCase();
                        return fullName.contains(query) || username.contains(query);
                    })
                    .toList();
        }

        mav.addObject("q", q);
        mav.addObject("trainers", trainers);
        mav.addObject("error", error);
        return mav;
    }
}
