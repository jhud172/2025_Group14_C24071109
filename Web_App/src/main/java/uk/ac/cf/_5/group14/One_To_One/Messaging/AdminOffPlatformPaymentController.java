package uk.ac.cf._5.group14.One_To_One.Messaging;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.ac.cf._5.group14.One_To_One.Users.AuthHelper;
import uk.ac.cf._5.group14.One_To_One.Users.Role;
import uk.ac.cf._5.group14.One_To_One.Users.User;
import uk.ac.cf._5.group14.One_To_One.Users.UserRepository;
import uk.ac.cf._5.group14.One_To_One.Users.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AdminOffPlatformPaymentController {

    private final AuthHelper authHelper;
    private final UserService userService;
    private final UserRepository userRepository;
    private final OffPlatformPaymentAttemptRepository attemptRepository;

    public AdminOffPlatformPaymentController(AuthHelper authHelper,
                                             UserService userService,
                                             UserRepository userRepository,
                                             OffPlatformPaymentAttemptRepository attemptRepository) {
        this.authHelper = authHelper;
        this.userService = userService;
        this.userRepository = userRepository;
        this.attemptRepository = attemptRepository;
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

    @GetMapping("/admin/off-platform-payments")
    public ModelAndView offPlatformPayments() {
        User admin = currentUserOrThrow();
        if (admin.getRole() != Role.PLATFORM_ADMIN && admin.getRole() != Role.SUPER_ADMIN) {
            throw new org.springframework.security.access.AccessDeniedException("Admin only");
        }

        List<OffPlatformPaymentAttempt> attempts = attemptRepository.findAllByOrderByCreatedAtDesc();
        Map<Long, User> usersById = new HashMap<>();
        for (OffPlatformPaymentAttempt attempt : attempts) {
            userRepository.findById(attempt.getSenderUserId())
                    .ifPresent(user -> usersById.put(user.getId(), user));
        }

        ModelAndView mav = new ModelAndView("admin/off-platform-payments");
        mav.addObject("pageTitle", "Off-Platform Payment Attempts");
        mav.addObject("attempts", attempts);
        mav.addObject("usersById", usersById);
        return mav;
    }
}
