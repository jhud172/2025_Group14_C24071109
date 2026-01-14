package uk.ac.cf._5.group14.BehaviourChangeGroupProject.HomePage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTaskRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseLog.ExerciseLog;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseLog.ExerciseLogService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Inbox.ConversationParticipant;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Inbox.ConversationParticipantRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Inbox.RoleInConversation;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleOccurrenceRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

@Controller
public class HomePageController {

    private final ExerciseLogService exerciseLogService;
    private final CalendarTaskRepository calendarTaskRepository;
    private final ScheduleOccurrenceRepository scheduleOccurrenceRepository;
    private final ConversationParticipantRepository conversationParticipantRepository;
    private final AuthHelper authHelper;

    private static final DateTimeFormatter PRETTY_DATE = DateTimeFormatter.ofPattern("d MMM", Locale.UK);

    public HomePageController(
            ExerciseLogService exerciseLogService,
            CalendarTaskRepository calendarTaskRepository,
            ScheduleOccurrenceRepository scheduleOccurrenceRepository,
            ConversationParticipantRepository conversationParticipantRepository,
            AuthHelper authHelper
    ) {
        this.exerciseLogService = exerciseLogService;
        this.calendarTaskRepository = calendarTaskRepository;
        this.scheduleOccurrenceRepository = scheduleOccurrenceRepository;
        this.conversationParticipantRepository = conversationParticipantRepository;
        this.authHelper = authHelper;
    }

    @GetMapping("/")
    public ModelAndView homePage() {
        User user = authHelper.getAuthenticatedUser();

        if (user == null) {
            return new ModelAndView("home-public");
        }

        ModelAndView modelAndView = new ModelAndView("home-auth");

        modelAndView.addObject("userFirstName", user.getFirstName());

        List<ExerciseLog> recentExerciseLogs = exerciseLogService.findTop5RecentExerciseLogs(user);
        modelAndView.addObject("recentExerciseLogs", recentExerciseLogs);

        LocalDate today = LocalDate.now();

        long logsLast7DaysCount = exerciseLogService.getLogsForUser(user).stream()
            .map(ExerciseLog::getDate)
            .filter(d -> d != null)
            .filter(d -> !d.isBefore(today.minusDays(6)) && !d.isAfter(today))
            .count();

        Long trainerConversationId = findTrainerConversationId(user);
        modelAndView.addObject("logsLast7DaysCount", logsLast7DaysCount);
        modelAndView.addObject("trainerConversationId", trainerConversationId);

        List<MiniWeekDay> miniWeek = new ArrayList<>(7);
        for (int i = 0; i < 7; i++) {
            LocalDate date = today.plusDays(i);
            int taskCount = (int) calendarTaskRepository.countByUserAndDate(user, date);
            int scheduledCount = (int) scheduleOccurrenceRepository.countByUserAndDate(user, date);
            boolean isToday = i == 0;
            boolean hasUncompletedWorkout = hasWorkoutToLog(user, date);

            miniWeek.add(new MiniWeekDay(
                    date,
                    date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.UK),
                    date.format(PRETTY_DATE),
                    taskCount,
                    scheduledCount,
                    isToday,
                    hasUncompletedWorkout
            ));
        }

        boolean todayHasWorkoutToLog = miniWeek.get(0).hasUncompletedWorkout();
        modelAndView.addObject("miniWeek", miniWeek);
        modelAndView.addObject("todayHasWorkoutToLog", todayHasWorkoutToLog);

        return modelAndView;
    }

    private boolean hasWorkoutToLog(User user, LocalDate date) {
        boolean scheduleNeedsLog = scheduleOccurrenceRepository.existsByUserAndDateAndCompletedFalse(user, date)
                || scheduleOccurrenceRepository.existsByUserAndDateAndExerciseLogIsNull(user, date);

        boolean calendarNeedsLog = calendarTaskRepository.existsByUserAndDateAndRequiresLogTrueAndCompletedFalse(user, date)
                || calendarTaskRepository.existsByUserAndDateAndRequiresLogTrueAndExerciseLogIsNull(user, date);

        return scheduleNeedsLog || calendarNeedsLog;
    }

    private Long findTrainerConversationId(User user) {
        List<ConversationParticipant> mine = conversationParticipantRepository.findByUser(user);
        for (ConversationParticipant myParticipant : mine) {
            Long conversationId = myParticipant.getConversation().getId();
            List<ConversationParticipant> participants = conversationParticipantRepository.findAllByConversationId(conversationId);

            for (ConversationParticipant participant : participants) {
                if (participant.getUser() == null || participant.getUser().getId() == null) {
                    continue;
                }

                boolean isOtherUser = !participant.getUser().getId().equals(user.getId());
                boolean isTrainer = participant.getRoleInConversation() == RoleInConversation.TRAINER;
                if (isOtherUser && isTrainer) {
                    return conversationId;
                }
            }
        }
        return null;
    }

}
