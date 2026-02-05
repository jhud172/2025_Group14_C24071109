package uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerAssignments;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Security.AccessGuard;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerClient.TrainerClientLinkService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.Role;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Workouts.WorkoutTemplateRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/trainer/clients")
public class TrainerClientsController {

    private final AuthHelper authHelper;
    private final UserService userService;
    private final UserRepository userRepository;
    private final TrainerClientLinkService trainerClientLinkService;
    private final WorkoutTemplateRepository workoutTemplateRepository;
    private final ScheduleService scheduleService;
    private final TrainerAssignmentService trainerAssignmentService;
    private final AccessGuard accessGuard;

    public TrainerClientsController(AuthHelper authHelper,
                                    UserService userService,
                                    UserRepository userRepository,
                                    TrainerClientLinkService trainerClientLinkService,
                                    WorkoutTemplateRepository workoutTemplateRepository,
                                    ScheduleService scheduleService,
                                    TrainerAssignmentService trainerAssignmentService,
                                    AccessGuard accessGuard) {
        this.authHelper = authHelper;
        this.userService = userService;
        this.userRepository = userRepository;
        this.trainerClientLinkService = trainerClientLinkService;
        this.workoutTemplateRepository = workoutTemplateRepository;
        this.scheduleService = scheduleService;
        this.trainerAssignmentService = trainerAssignmentService;
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

    @GetMapping("/{clientId}")
    public ModelAndView clientDetail(@PathVariable Long clientId, Model model) {
        User trainer = currentUserOrThrow();
        if (trainer.getRole() != Role.TRAINER) {
            return new ModelAndView("redirect:/access-denied");
        }
        accessGuard.requireTrainerAccessClient(trainer.getId(), clientId);

        User client = userRepository.findById(clientId).orElse(null);
        if (client == null) {
            return new ModelAndView("redirect:/trainer/clients");
        }

        ModelAndView mav = new ModelAndView("trainer/client-detail");
        mav.addObject("pageTitle", "Client Overview");
        mav.addObject("client", client);
        mav.addObject("assignedWorkouts", trainerAssignmentService.listWorkoutsForTrainerClient(trainer.getId(), clientId));
        mav.addObject("assignedSchedules", trainerAssignmentService.listSchedulesForTrainerClient(trainer.getId(), clientId));
        mav.addObject("adherence", trainerAssignmentService.getClientAdherence(trainer.getId(), clientId));
        mav.addObject("templates", workoutTemplateRepository.findByOwnerUserOrderByUpdatedAtDesc(trainer));
        mav.addObject("schedules", scheduleService.findByUser(trainer));
        return mav;
    }

    @PostMapping("/{clientId}/assign-workout")
    public ModelAndView assignWorkout(@PathVariable Long clientId,
                                      @RequestParam Long templateId,
                                      @RequestParam(required = false) String trainerNotes) {
        User trainer = currentUserOrThrow();
        if (trainer.getRole() != Role.TRAINER) {
            return new ModelAndView("redirect:/access-denied");
        }
        trainerAssignmentService.assignWorkout(trainer, clientId, templateId, trainerNotes);
        return new ModelAndView("redirect:/trainer/clients/" + clientId);
    }

    @PostMapping("/{clientId}/assign-schedule")
    public ModelAndView assignSchedule(@PathVariable Long clientId,
                                       @RequestParam Long scheduleId,
                                       @RequestParam(required = false) String trainerNotes) {
        User trainer = currentUserOrThrow();
        if (trainer.getRole() != Role.TRAINER) {
            return new ModelAndView("redirect:/access-denied");
        }
        trainerAssignmentService.assignSchedule(trainer, clientId, scheduleId, trainerNotes);
        return new ModelAndView("redirect:/trainer/clients/" + clientId);
    }
}
