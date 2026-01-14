package uk.ac.cf._5.group14.BehaviourChangeGroupProject.HomePage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTaskRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTask;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseLog.ExerciseLog;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseLog.ExerciseLogService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Inbox.ConversationParticipant;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Inbox.ConversationParticipantRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Inbox.RoleInConversation;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleOccurrenceRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleOccurrence;
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

        List<UpcomingItem> upcomingItems = buildUpcomingItems(user, today, today.plusDays(14));
        modelAndView.addObject("upcomingItems", upcomingItems);

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

    private List<UpcomingItem> buildUpcomingItems(User user, LocalDate from, LocalDate to) {
        List<CalendarTask> tasks = calendarTaskRepository.findByUserAndDateBetween(user, from, to);
        List<ScheduleOccurrence> occurrences = scheduleOccurrenceRepository.findByUserAndDateBetween(user, from, to);

        List<UpcomingItemCandidate> all = new ArrayList<>();

        for (CalendarTask task : tasks) {
            if (Boolean.TRUE.equals(task.getCompleted())) {
                continue;
            }
            LocalDate date = task.getDate();
            if (date == null || date.isBefore(from)) {
                continue;
            }
            LocalTime time = task.getTime() != null ? task.getTime() : LocalTime.MIDNIGHT;
            String dateLabel = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.UK) + " " + date.format(PRETTY_DATE);
            String href = "/calendar/day/" + date;
            all.add(new UpcomingItemCandidate(date, time, new UpcomingItem("Task", task.getTitle(), dateLabel, href)));
        }

        for (ScheduleOccurrence occ : occurrences) {
            if (occ.isCompleted()) {
                continue;
            }
            LocalDate date = occ.getDate();
            if (date == null || date.isBefore(from)) {
                continue;
            }
            String dateLabel = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.UK) + " " + date.format(PRETTY_DATE);
            String href = "/calendar/day/" + date;
            String title = occ.getScheduleName() != null ? occ.getScheduleName() : "Workout session";
            all.add(new UpcomingItemCandidate(date, LocalTime.NOON, new UpcomingItem("Session", title, dateLabel, href)));
        }

        all.sort(Comparator
                .comparing(UpcomingItemCandidate::date)
                .thenComparing(UpcomingItemCandidate::time));

        List<UpcomingItem> top3 = new ArrayList<>(3);
        for (UpcomingItemCandidate c : all) {
            top3.add(c.item());
            if (top3.size() == 3) {
                break;
            }
        }
        return top3;
    }

    private record UpcomingItemCandidate(LocalDate date, LocalTime time, UpcomingItem item) {
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
