package uk.ac.cf._5.group14.One_To_One.Checkins;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import uk.ac.cf._5.group14.One_To_One.Goals.Goal;
import uk.ac.cf._5.group14.One_To_One.Goals.GoalService;
import uk.ac.cf._5.group14.One_To_One.Goals.GoalStatus;
import uk.ac.cf._5.group14.One_To_One.TrainerClient.TrainerClientLink;
import uk.ac.cf._5.group14.One_To_One.TrainerClient.TrainerClientLinkService;
import uk.ac.cf._5.group14.One_To_One.TrainerTemplates.TrainerScheduleTemplate;
import uk.ac.cf._5.group14.One_To_One.TrainerTemplates.TrainerScheduleTemplateRepository;
import uk.ac.cf._5.group14.One_To_One.Users.AuthHelper;
import uk.ac.cf._5.group14.One_To_One.Users.Role;
import uk.ac.cf._5.group14.One_To_One.Users.User;
import uk.ac.cf._5.group14.One_To_One.Users.UserService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/checkins")
public class WeeklyCheckInController {

    private final AuthHelper authHelper;
    private final UserService userService;
    private final TrainerClientLinkService trainerClientLinkService;
    private final TrainerScheduleTemplateRepository templateRepository;
    private final WeeklyCheckInService weeklyCheckInService;
    private final GoalService goalService;
    private final ObjectMapper objectMapper;

    public WeeklyCheckInController(AuthHelper authHelper,
                                   UserService userService,
                                   TrainerClientLinkService trainerClientLinkService,
                                   TrainerScheduleTemplateRepository templateRepository,
                                   WeeklyCheckInService weeklyCheckInService,
                                   GoalService goalService,
                                   ObjectMapper objectMapper) {
        this.authHelper = authHelper;
        this.userService = userService;
        this.trainerClientLinkService = trainerClientLinkService;
        this.templateRepository = templateRepository;
        this.weeklyCheckInService = weeklyCheckInService;
        this.goalService = goalService;
        this.objectMapper = objectMapper;
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

    @GetMapping("/client-submit")
    public ModelAndView clientSubmit(@RequestParam(required = false) Long templateId) {
        User client = currentUserOrThrow();
        if (client.getRole() != Role.CLIENT) {
            return new ModelAndView("redirect:/access-denied");
        }

        TrainerClientLink link = trainerClientLinkService.getActiveLinkForClient(client.getId());
        ModelAndView mav = new ModelAndView("client-views/checkins/client-submit");
        mav.addObject("pageTitle", "Weekly Check-in");
        mav.addObject("activeLink", link);

        if (link != null) {
            List<TrainerScheduleTemplate> templates = templateRepository.findByTrainerIdOrderByUpdatedAtDesc(link.getTrainerUserId());
            mav.addObject("templates", templates);
            Long selectedId = templateId != null ? templateId : (templates.isEmpty() ? null : templates.get(0).getId());
            mav.addObject("selectedTemplateId", selectedId);
            mav.addObject("questions", weeklyCheckInService.listQuestions(selectedId));
        }

        return mav;
    }

    @PostMapping("/client-submit")
    public ModelAndView submitCheckIn(@RequestParam Long templateId,
                                      @RequestParam(required = false) String clientNotes,
                                      @RequestParam(required = false) String weekStart,
                                      @RequestParam Map<String, String> params) {
        User client = currentUserOrThrow();
        if (client.getRole() != Role.CLIENT) {
            return new ModelAndView("redirect:/access-denied");
        }

        Map<Long, String> answers = new HashMap<>();
        List<TrainerCheckInQuestion> questions = weeklyCheckInService.listQuestions(templateId);
        for (TrainerCheckInQuestion question : questions) {
            String key = "q_" + question.getId();
            String value = params.get(key);
            if (value != null) {
                answers.put(question.getId(), value);
            }
        }

        LocalDate weekStartDate = weekStart != null ? LocalDate.parse(weekStart) : null;
        weeklyCheckInService.submitCheckIn(client, templateId, answers, clientNotes, weekStartDate);
        return new ModelAndView("redirect:/checkins/client-submit?templateId=" + templateId);
    }

    @GetMapping("/trainer-review/{id}")
    public ModelAndView trainerReview(@PathVariable Long id) {
        User trainer = currentUserOrThrow();
        if (trainer.getRole() != Role.TRAINER) {
            return new ModelAndView("redirect:/access-denied");
        }

        WeeklyCheckIn checkIn = weeklyCheckInService.getForTrainer(trainer, id);
        List<Map<String, String>> responses = parseResponses(checkIn.getResponsesJson());
        List<Goal> goals = goalService.listGoalsForViewer(trainer, checkIn.getClientId(), GoalStatus.ACTIVE, null, false);

        ModelAndView mav = new ModelAndView("trainer-views/checkins/trainer-review");
        mav.addObject("pageTitle", "Weekly Check-in Review");
        mav.addObject("checkIn", checkIn);
        mav.addObject("responses", responses);
        mav.addObject("goals", goals);
        return mav;
    }

    @PostMapping("/trainer-review/{id}")
    public ModelAndView trainerRespond(@PathVariable Long id,
                                       @RequestParam(required = false) String trainerResponse,
                                       @RequestParam(required = false) String nextWeekFocus,
                                       @RequestParam(required = false) Long goalId) {
        User trainer = currentUserOrThrow();
        if (trainer.getRole() != Role.TRAINER) {
            return new ModelAndView("redirect:/access-denied");
        }
        weeklyCheckInService.respondToCheckIn(trainer, id, trainerResponse, nextWeekFocus, goalId);
        return new ModelAndView("redirect:/checkins/trainer-review/" + id);
    }

    private List<Map<String, String>> parseResponses(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            return List.of();
        }
    }
}
