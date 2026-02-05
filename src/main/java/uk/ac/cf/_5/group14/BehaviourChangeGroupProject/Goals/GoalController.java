package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Goals;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTaskRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleOccurrenceRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.Role;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/goals")
public class GoalController {

    private final AuthHelper authHelper;
    private final GoalService goalService;
    private final GoalLinkService goalLinkService;
    private final GoalCheckInService goalCheckInService;
    private final GoalAdherenceService goalAdherenceService;
    private final UserRepository userRepository;
    private final CalendarTaskRepository calendarTaskRepository;
    private final ScheduleOccurrenceRepository scheduleOccurrenceRepository;

    public GoalController(AuthHelper authHelper,
                          GoalService goalService,
                          GoalLinkService goalLinkService,
                          GoalCheckInService goalCheckInService,
                          GoalAdherenceService goalAdherenceService,
                          UserRepository userRepository,
                          CalendarTaskRepository calendarTaskRepository,
                          ScheduleOccurrenceRepository scheduleOccurrenceRepository) {
        this.authHelper = authHelper;
        this.goalService = goalService;
        this.goalLinkService = goalLinkService;
        this.goalCheckInService = goalCheckInService;
        this.goalAdherenceService = goalAdherenceService;
        this.userRepository = userRepository;
        this.calendarTaskRepository = calendarTaskRepository;
        this.scheduleOccurrenceRepository = scheduleOccurrenceRepository;
    }

    @GetMapping
    public String index(@RequestParam(required = false) Long clientId,
                        @RequestParam(required = false) String status,
                        @RequestParam(required = false) String type,
                        @RequestParam(required = false) Boolean archived,
                        Model model) {
        User user = authHelper.getAuthenticatedUser();
        if (user == null) {
            return "redirect:/login";
        }

        GoalStatus statusFilter = parseStatus(status);
        GoalType typeFilter = parseType(type);

        List<Goal> goals = goalService.listGoalsForViewer(user, clientId, statusFilter, typeFilter, archived);
        LocalDate weekStart = GoalAdherenceService.normalizeWeekStart(LocalDate.now());
        Map<Long, GoalAdherenceWeek> adherenceByGoalId = goals.stream()
            .collect(java.util.stream.Collectors.toMap(
                Goal::getId,
                goal -> goalAdherenceService.calculateWeek(goal, weekStart)
            ));

        model.addAttribute("goals", goals);
        model.addAttribute("adherenceByGoalId", adherenceByGoalId);
        model.addAttribute("statusFilter", statusFilter);
        model.addAttribute("typeFilter", typeFilter);
        model.addAttribute("archived", archived);
        model.addAttribute("clientId", clientId);
        model.addAttribute("goalStatuses", GoalStatus.values());
        model.addAttribute("goalTypes", GoalType.values());

        if (clientId != null && user.getRole() == Role.TRAINER) {
            userRepository.findById(clientId).ifPresent(client -> model.addAttribute("client", client));
        }

        return "goals/index";
    }

    @GetMapping("/create")
    public String create(@RequestParam(required = false) Long clientId, Model model) {
        User user = authHelper.getAuthenticatedUser();
        if (user == null) {
            return "redirect:/login";
        }
        if (clientId != null && user.getRole() == Role.TRAINER) {
            userRepository.findById(clientId).ifPresent(client -> model.addAttribute("client", client));
        }
        model.addAttribute("form", new GoalForm());
        model.addAttribute("goalStatuses", GoalStatus.values());
        model.addAttribute("goalTypes", GoalType.values());
        model.addAttribute("clientId", clientId);
        return "goals/create";
    }

    @PostMapping("/create")
    public ModelAndView createSubmit(@RequestParam(required = false) Long clientId,
                                     @ModelAttribute("form") GoalForm form) {
        User user = authHelper.getAuthenticatedUser();
        if (user == null) {
            return new ModelAndView("redirect:/login");
        }
        Goal goal = goalService.createGoal(user, clientId, form);
        return new ModelAndView("redirect:/goals/" + goal.getId());
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        User user = authHelper.getAuthenticatedUser();
        if (user == null) {
            return "redirect:/login";
        }
        Goal goal = goalService.getGoalForViewer(user, id);
        GoalLinkService.GoalLinkedItems linked = goalLinkService.loadLinkedItems(goal.getId());

        LocalDate weekStart = GoalAdherenceService.normalizeWeekStart(LocalDate.now().minusWeeks(3));
        LocalDate weekEnd = GoalAdherenceService.normalizeWeekStart(LocalDate.now());
        List<GoalAdherenceWeek> adherenceWeeks = goalAdherenceService.calculateRange(goal.getId(), weekStart, weekEnd);

        LocalDate rangeStart = LocalDate.now().minusDays(14);
        LocalDate rangeEnd = LocalDate.now().plusDays(14);
        List<uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTask> linkableTasks =
            calendarTaskRepository.findByUserAndDateBetween(goal.getOwnerUser(), rangeStart, rangeEnd);
        List<uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleOccurrence> linkableOccurrences =
            scheduleOccurrenceRepository.findByUserAndDateBetween(goal.getOwnerUser(), rangeStart, rangeEnd);

        model.addAttribute("goal", goal);
        model.addAttribute("linkedItems", linked);
        model.addAttribute("adherenceWeeks", adherenceWeeks);
        model.addAttribute("linkableTasks", linkableTasks);
        model.addAttribute("linkableOccurrences", linkableOccurrences);
        model.addAttribute("canEditTargets", goalService.canEditTargetMetrics(user, goal));
        return "goals/detail";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        User user = authHelper.getAuthenticatedUser();
        if (user == null) {
            return "redirect:/login";
        }
        Goal goal = goalService.getGoalForViewer(user, id);
        GoalForm form = toForm(goal);
        model.addAttribute("goal", goal);
        model.addAttribute("form", form);
        model.addAttribute("goalStatuses", GoalStatus.values());
        model.addAttribute("goalTypes", GoalType.values());
        model.addAttribute("canEditTargets", goalService.canEditTargetMetrics(user, goal));
        return "goals/edit";
    }

    @PostMapping("/{id}/edit")
    public ModelAndView editSubmit(@PathVariable Long id, @ModelAttribute("form") GoalForm form) {
        User user = authHelper.getAuthenticatedUser();
        if (user == null) {
            return new ModelAndView("redirect:/login");
        }
        goalService.updateGoal(user, id, form);
        return new ModelAndView("redirect:/goals/" + id);
    }

    @GetMapping("/{id}/checkins")
    public String checkins(@PathVariable Long id, Model model) {
        User user = authHelper.getAuthenticatedUser();
        if (user == null) {
            return "redirect:/login";
        }
        Goal goal = goalService.getGoalForViewer(user, id);
        List<GoalCheckIn> checkIns = goalCheckInService.listForGoal(user, id);
        GoalCheckInForm form = new GoalCheckInForm();
        form.setWeekStartDate(GoalAdherenceService.normalizeWeekStart(LocalDate.now()));

        model.addAttribute("goal", goal);
        model.addAttribute("checkIns", checkIns);
        model.addAttribute("form", form);
        model.addAttribute("isTrainer", user.getRole() == Role.TRAINER);
        return "goals/checkins";
    }

    @PostMapping("/{id}/checkins")
    public ModelAndView createCheckIn(@PathVariable Long id,
                                      @ModelAttribute("form") GoalCheckInForm form) {
        User user = authHelper.getAuthenticatedUser();
        if (user == null) {
            return new ModelAndView("redirect:/login");
        }
        goalCheckInService.createCheckIn(user, id, form);
        return new ModelAndView("redirect:/goals/" + id + "/checkins");
    }

    @PostMapping("/{id}/links")
    public ModelAndView createLink(@PathVariable Long id,
                                   @RequestParam String linkType,
                                   @RequestParam(required = false) Long targetId) {
        User user = authHelper.getAuthenticatedUser();
        if (user == null) {
            return new ModelAndView("redirect:/login");
        }
        if (targetId == null) {
            return new ModelAndView("redirect:/goals/" + id);
        }
        GoalLinkType parsed = GoalLinkType.valueOf(linkType);
        GoalLinkSource source = user.getRole() == Role.TRAINER ? GoalLinkSource.TRAINER_ASSIGNED : GoalLinkSource.SELF;
        if (parsed == GoalLinkType.CALENDAR_TASK) {
            goalLinkService.linkCalendarTask(user, id, targetId, source);
        } else if (parsed == GoalLinkType.SCHEDULE_OCCURRENCE) {
            goalLinkService.linkScheduleOccurrence(user, id, targetId, source);
        }
        return new ModelAndView("redirect:/goals/" + id);
    }

    private GoalForm toForm(Goal goal) {
        GoalForm form = new GoalForm();
        form.setTitle(goal.getTitle());
        form.setDescription(goal.getDescription());
        form.setGoalType(goal.getGoalType());
        form.setTargetMetricName(goal.getTargetMetricName());
        form.setTargetMetricValue(goal.getTargetMetricValue());
        form.setTargetMetricUnit(goal.getTargetMetricUnit());
        form.setStartDate(goal.getStartDate());
        form.setTargetDate(goal.getTargetDate());
        form.setStatus(goal.getStatus());
        form.setPriority(goal.getPriority());
        form.setArchived(goal.isArchived());
        return form;
    }

    private GoalStatus parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        try {
            return GoalStatus.valueOf(status);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private GoalType parseType(String type) {
        if (type == null || type.isBlank()) {
            return null;
        }
        try {
            return GoalType.valueOf(type);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
