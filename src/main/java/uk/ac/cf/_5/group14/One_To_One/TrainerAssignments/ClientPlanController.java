package uk.ac.cf._5.group14.One_To_One.TrainerAssignments;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import uk.ac.cf._5.group14.One_To_One.Users.AuthHelper;
import uk.ac.cf._5.group14.One_To_One.Users.Role;
import uk.ac.cf._5.group14.One_To_One.Users.User;
import uk.ac.cf._5.group14.One_To_One.Users.UserService;

@Controller
@RequestMapping("/client/plan")
public class ClientPlanController {

    private final TrainerAssignmentService trainerAssignmentService;
    private final AuthHelper authHelper;
    private final UserService userService;

    public ClientPlanController(TrainerAssignmentService trainerAssignmentService,
                                AuthHelper authHelper,
                                UserService userService) {
        this.trainerAssignmentService = trainerAssignmentService;
        this.authHelper = authHelper;
        this.userService = userService;
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

    @GetMapping
    public ModelAndView plan() {
        User client = currentUserOrThrow();
        if (client.getRole() != Role.CLIENT) {
            return new ModelAndView("redirect:/access-denied");
        }

        ModelAndView mav = new ModelAndView("client/plan");
        mav.addObject("pageTitle", "Assigned Plan");
        mav.addObject("assignedWorkouts", trainerAssignmentService.listWorkoutsForClient(client.getId()));
        mav.addObject("assignedSchedules", trainerAssignmentService.listSchedulesForClient(client.getId()));
        return mav;
    }

    @PostMapping("/workouts/{id}")
    public ModelAndView updateWorkout(@PathVariable Long id,
                                      @RequestParam(required = false) String clientNotes,
                                      @RequestParam(required = false) String clientFeedback,
                                      @RequestParam(name = "completed", defaultValue = "false") boolean completed) {
        User client = currentUserOrThrow();
        if (client.getRole() != Role.CLIENT) {
            return new ModelAndView("redirect:/access-denied");
        }
        trainerAssignmentService.updateClientWorkout(client.getId(), id, clientNotes, clientFeedback, completed);
        return new ModelAndView("redirect:/client/plan");
    }
}
