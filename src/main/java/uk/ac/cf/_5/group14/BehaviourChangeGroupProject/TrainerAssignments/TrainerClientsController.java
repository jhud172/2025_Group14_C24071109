package uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerAssignments;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Goals.Goal;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Goals.GoalAdherenceService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Goals.GoalAdherenceWeek;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Goals.GoalService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Goals.GoalStatus;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Security.AccessGuard;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerClient.CoachingPhase;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerClient.TrainerClientLink;
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
import java.time.LocalDate;

@Controller
@RequestMapping("/trainer/clients")
public class TrainerClientsController {

    private final AuthHelper authHelper;
    private final UserService userService;
    private final UserRepository userRepository;
    private final WorkoutTemplateRepository workoutTemplateRepository;
    private final ScheduleService scheduleService;
    private final TrainerAssignmentService trainerAssignmentService;
    private final AccessGuard accessGuard;
    private final GoalService goalService;
    private final GoalAdherenceService goalAdherenceService;
    private final TrainerClientLinkService trainerClientLinkService;

    public TrainerClientsController(AuthHelper authHelper,
                                    UserService userService,
                                    UserRepository userRepository,
                                    WorkoutTemplateRepository workoutTemplateRepository,
                                    ScheduleService scheduleService,
                                    TrainerAssignmentService trainerAssignmentService,
                                    AccessGuard accessGuard,
                                    GoalService goalService,
                                    GoalAdherenceService goalAdherenceService,
                                    TrainerClientLinkService trainerClientLinkService) {
        this.authHelper = authHelper;
        this.userService = userService;
        this.userRepository = userRepository;
        this.workoutTemplateRepository = workoutTemplateRepository;
        this.scheduleService = scheduleService;
        this.trainerAssignmentService = trainerAssignmentService;
        this.accessGuard = accessGuard;
        this.goalService = goalService;
        this.goalAdherenceService = goalAdherenceService;
        this.trainerClientLinkService = trainerClientLinkService;
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
        List<Goal> goals = goalService.listGoalsForViewer(trainer, clientId, GoalStatus.ACTIVE, null, false);
        LocalDate weekStart = GoalAdherenceService.normalizeWeekStart(LocalDate.now());
        Map<Long, GoalAdherenceWeek> adherenceByGoalId = goals.stream()
            .collect(Collectors.toMap(Goal::getId, goal -> goalAdherenceService.calculateWeek(goal, weekStart)));
        mav.addObject("clientGoals", goals);
        mav.addObject("goalAdherenceById", adherenceByGoalId);
        mav.addObject("templates", workoutTemplateRepository.findByOwnerUserOrderByUpdatedAtDesc(trainer));
        mav.addObject("schedules", scheduleService.findByUser(trainer));
        TrainerClientLink activeLink = trainerClientLinkService.getActiveLinkForTrainerClient(trainer.getId(), clientId);
        mav.addObject("activeLink", activeLink);
        mav.addObject("coachingPhases", CoachingPhase.values());
        return mav;
    }

    @PostMapping("/{clientId}/phase")
    public ModelAndView updateCoachingPhase(@PathVariable Long clientId,
                                            @RequestParam CoachingPhase phase,
                                            @RequestParam(required = false) String customLabel,
                                            @RequestParam(required = false) String notes) {
        User trainer = currentUserOrThrow();
        if (trainer.getRole() != Role.TRAINER) {
            return new ModelAndView("redirect:/access-denied");
        }
        trainerClientLinkService.changeCoachingPhase(trainer.getId(), clientId, phase, customLabel, notes);
        return new ModelAndView("redirect:/trainer/clients/" + clientId);
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
