package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Dashboard;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTask;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTaskRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Checkins.WeeklyCheckIn;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Checkins.WeeklyCheckInRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Dashboard.dto.ClientDashboardActionCardView;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Dashboard.dto.ClientDashboardActionHubView;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Dashboard.dto.ClientDashboardActionUpcomingView;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Dashboard.dto.ClientDashboardIdentityView;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Dashboard.dto.ClientDashboardProfileRailView;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Dashboard.dto.ClientDashboardTrainerContextView;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Dashboard.dto.ClientDashboardTrainerRailView;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Dashboard.dto.DashboardSummaryDto;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Goals.Goal;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Goals.GoalService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Goals.GoalStatus;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.HealthDataInput.HealthRecord;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.HealthDataInput.HealthRecordRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.HomePage.MiniWeekDay;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Level.LevelProgress;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Messaging.MessageThread;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Messaging.MessageThreadRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Messaging.MessagingService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.PlatformBilling.PlatformPlan;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.PlatformBilling.PlatformSubscription;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.PlatformBilling.PlatformSubscriptionService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.Schedule;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleOccurrence;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleOccurrenceRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Security.SecurityUtils;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerAssignments.AssignedSchedule;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerAssignments.AssignedWorkout;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerAssignments.TrainerAssignmentService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerClient.TrainerClientLink;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerClient.TrainerClientLinkRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerClient.TrainerClientLinkStatus;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerLibrary.TrainerLibraryAssignedProgrammeView;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerLibrary.TrainerLibraryAssignedWorkoutView;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerLibrary.TrainerLibraryService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.UserSettings.UserSettings;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.UserSettings.UserSettingsRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.UserSettings.UserSettingsService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.Role;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserService;

@Controller
public class DashboardController {

    private static final DateTimeFormatter PRETTY_DATE_FMT =
            DateTimeFormatter.ofPattern("d MMM", Locale.UK);
    private static final DateTimeFormatter FULL_DATE_FMT =
            DateTimeFormatter.ofPattern("d MMM yyyy", Locale.UK);
    private static final DateTimeFormatter SHORT_TIME_FMT =
            DateTimeFormatter.ofPattern("HH:mm", Locale.UK);

    private final AuthHelper authHelper;
    private final UserService userService;
    private final UserRepository userRepository;
    private final DashboardSummaryService dashboardSummaryService;
    private final TrainerClientLinkRepository trainerClientLinkRepository;
    private final UserSettingsService userSettingsService;
    private final UserSettingsRepository userSettingsRepository;
    private final HealthRecordRepository healthRecordRepository;
    private final GoalService goalService;
    private final TrainerAssignmentService trainerAssignmentService;
    private final TrainerLibraryService trainerLibraryService;
    private final WeeklyCheckInRepository weeklyCheckInRepository;
    private final MessageThreadRepository messageThreadRepository;
    private final MessagingService messagingService;
    private final PlatformSubscriptionService platformSubscriptionService;
    private final CalendarTaskRepository calendarTaskRepository;
    private final ScheduleOccurrenceRepository scheduleOccurrenceRepository;
    private final Clock clock;

    public DashboardController(AuthHelper authHelper,
                               UserService userService,
                               UserRepository userRepository,
                               DashboardSummaryService dashboardSummaryService,
                               TrainerClientLinkRepository trainerClientLinkRepository,
                               UserSettingsService userSettingsService,
                               UserSettingsRepository userSettingsRepository,
                               HealthRecordRepository healthRecordRepository,
                               GoalService goalService,
                               TrainerAssignmentService trainerAssignmentService,
                               TrainerLibraryService trainerLibraryService,
                               WeeklyCheckInRepository weeklyCheckInRepository,
                               MessageThreadRepository messageThreadRepository,
                               MessagingService messagingService,
                               PlatformSubscriptionService platformSubscriptionService,
                               CalendarTaskRepository calendarTaskRepository,
                               ScheduleOccurrenceRepository scheduleOccurrenceRepository,
                               Clock clock) {
        this.authHelper = authHelper;
        this.userService = userService;
        this.userRepository = userRepository;
        this.dashboardSummaryService = dashboardSummaryService;
        this.trainerClientLinkRepository = trainerClientLinkRepository;
        this.userSettingsService = userSettingsService;
        this.userSettingsRepository = userSettingsRepository;
        this.healthRecordRepository = healthRecordRepository;
        this.goalService = goalService;
        this.trainerAssignmentService = trainerAssignmentService;
        this.trainerLibraryService = trainerLibraryService;
        this.weeklyCheckInRepository = weeklyCheckInRepository;
        this.messageThreadRepository = messageThreadRepository;
        this.messagingService = messagingService;
        this.platformSubscriptionService = platformSubscriptionService;
        this.calendarTaskRepository = calendarTaskRepository;
        this.scheduleOccurrenceRepository = scheduleOccurrenceRepository;
        this.clock = clock;
    }

    private User currentUserOrThrow(Authentication authentication) {
        User sessionUser = authHelper.getAuthenticatedUser();
        if (sessionUser != null) {
            return sessionUser;
        }
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new org.springframework.security.access.AccessDeniedException("Not authenticated");
        }
        User user = userService.findByUsername(authentication.getName());
        if (user == null) {
            throw new org.springframework.security.access.AccessDeniedException("User not found");
        }
        return user;
    }

    @GetMapping("/dashboard")
    public String dashboardRouter(Authentication authentication, Model model) {
        if (SecurityUtils.hasRole(authentication, "SUPER_ADMIN") || SecurityUtils.hasRole(authentication, "PLATFORM_ADMIN")) {
            return "redirect:/admin/dashboard";
        }
        if (SecurityUtils.hasRole(authentication, "GYM_ADMIN")) {
            return "redirect:/gym/dashboard";
        }
        if (SecurityUtils.hasRole(authentication, "TRAINER")) {
            return "redirect:/trainer/dashboard";
        }

        model.addAttribute("pageTitle", "Dashboard");
        model.addAttribute("disableChatHistory", true);
        model.addAttribute("compactTopContent", true);

        User user = currentUserOrThrow(authentication);
        DashboardSummaryDto summary = dashboardSummaryService.getSummary(user);
        LocalDate today = summary.getToday();
        LocalDateTime now = LocalDateTime.ofInstant(Instant.now(clock), clock.getZone());

        List<MiniWeekDay> miniWeek = summary.getWeek().stream()
                .map(day -> new MiniWeekDay(
                        day.getDate(),
                        day.getLabel(),
                        day.getDate().format(PRETTY_DATE_FMT),
                        day.getTasksCount(),
                        day.getWorkoutsCount(),
                        day.isToday(),
                        day.isTomorrow(),
                        day.getDayStatus(),
                        day.getWorkoutsCount() > 0,
                        day.getTaskTitles(),
                        day.getWorkoutTitles()))
                .toList();

        MiniWeekDay todayDay = !miniWeek.isEmpty()
                ? miniWeek.get(0)
                : new MiniWeekDay(today, "Today", today.format(PRETTY_DATE_FMT), summary.getTasksDueToday(),
                summary.getWorkoutsDueToday(), true, false, "today", summary.getWorkoutsDueToday() > 0, List.of(), List.of());

        int todayTasks = todayDay.taskCount();
        int todayWorkouts = todayDay.scheduledCount();
        int todayTotal = todayTasks + todayWorkouts;
        String todayPath = pathForDate(today);

        List<CalendarTask> openTasks = calendarTaskRepository.findByUserAndDateOrderByTime(user, today).stream()
                .filter(task -> !Boolean.TRUE.equals(task.getCompleted()) && !task.isMissed())
                .sorted(Comparator.comparing(task -> task.getTime() != null ? task.getTime() : LocalTime.MAX))
                .toList();
        List<ScheduleOccurrence> openWorkouts = scheduleOccurrenceRepository.findByUserAndDate(user, today).stream()
                .filter(item -> !item.isCompleted() && !item.isMissed())
                .toList();

        int weekTasksTotal = summary.getWeek().stream().mapToInt(DashboardSummaryDto.WeekDaySummary::getTasksCount).sum();
        int weekTasksCompleted = summary.getWeek().stream().mapToInt(DashboardSummaryDto.WeekDaySummary::getTasksCompletedCount).sum();
        int weekTasksRemaining = Math.max(weekTasksTotal - weekTasksCompleted, 0);
        int weekWorkoutsTotal = summary.getWeek().stream().mapToInt(DashboardSummaryDto.WeekDaySummary::getWorkoutsCount).sum();
        int weekWorkoutsCompleted = summary.getWeek().stream().mapToInt(DashboardSummaryDto.WeekDaySummary::getWorkoutsCompletedCount).sum();
        int weekWorkoutsRemaining = Math.max(weekWorkoutsTotal - weekWorkoutsCompleted, 0);

        UserSettings settings = userSettingsService.getOrCreate(user);
        boolean isPremium = platformSubscriptionService.isPremium(user.getId(), clock);
        int intakeCalories = summary.getLogsThisWeekCount() > 0 ? (1700 + (summary.getLogsThisWeekCount() * 40)) : 0;
        boolean intakeLoggedToday = summary.getLogsThisWeekCount() > 0;
        String intakeLastLogged = intakeLoggedToday ? "Today" : "Not logged yet";
        MealWindow mealWindow = resolveMealWindow(now.toLocalTime());

        List<Goal> userGoals = goalService.listGoalsForViewer(user, null, null, null, false);
        long activeGoalsCount = userGoals.stream().filter(goal -> goal.getStatus() == GoalStatus.ACTIVE).count();
        long completedGoalsCount = userGoals.stream().filter(goal -> goal.getStatus() == GoalStatus.COMPLETED).count();

        model.addAttribute("summary", summary);
        model.addAttribute("miniWeek", miniWeek);
        model.addAttribute("todayPath", todayPath);
        model.addAttribute("todayTasks", todayTasks);
        model.addAttribute("todayWorkouts", todayWorkouts);
        model.addAttribute("leftToComplete", todayTotal);
        model.addAttribute("activeGoalsCount", activeGoalsCount);
        model.addAttribute("completedGoalsCount", completedGoalsCount);
        model.addAttribute("goalPreviewCards", buildGoalPreviewCards(userGoals, today));
        model.addAttribute("weeklySummaryCards",
                buildWeeklySummaryCards(user, settings, summary, weekWorkoutsCompleted, weekWorkoutsRemaining,
                        weekTasksCompleted, weekTasksRemaining, weekTasksTotal));
        model.addAttribute("profileRail",
                buildProfileRailView(user, settings, isPremium, buildPremiumTooltip(user.getId()), summary, todayTotal));
        model.addAttribute("trainerRail", buildTrainerRailView(user));
        model.addAttribute("bodyActionCard",
                buildBodyActionCard(settings, intakeCalories, intakeLoggedToday, intakeLastLogged, summary.getLogsThisWeekCount()));
        model.addAttribute("scheduleActionCard",
                buildScheduleActionCard(todayTasks, todayWorkouts, todayTotal, todayPath, todayDay));
        model.addAttribute("actionHub",
                buildActionHubView(today, todayPath, miniWeek, openTasks, openWorkouts, intakeCalories,
                        intakeLastLogged, mealWindow, now));

        return "dashboard/client-dashboard";
    }

    @GetMapping("/client/dashboard")
    public String clientDashboard() {
        return "redirect:/dashboard";
    }

    @GetMapping({"/dashboard/public", "/client/dashboard/public"})
    public String clientDashboardPublic(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/dashboard";
        }
        model.addAttribute("pageTitle", "One to One");
        return "dashboard/client-dashboard-public";
    }

    @GetMapping("/trainer/dashboard")
    public String trainerDashboard(Authentication authentication, Model model) {
        model.addAttribute("pageTitle", "Dashboard");
        model.addAttribute("disableChatHistory", true);
        User trainer = currentUserOrThrow(authentication);
        DashboardSummaryDto summary = dashboardSummaryService.getSummary(trainer);
        model.addAttribute("summary", summary);
        return "dashboard/trainer-dashboard";
    }

    @GetMapping("/gym/dashboard")
    public String gymDashboard(Authentication authentication, Model model) {
        model.addAttribute("pageTitle", "Dashboard");
        model.addAttribute("disableChatHistory", true);
        User admin = currentUserOrThrow(authentication);
        DashboardSummaryDto summary = dashboardSummaryService.getSummary(admin);
        model.addAttribute("summary", summary);
        return "dashboard/gym-dashboard";
    }

    private TopActionCard buildBodyActionCard(UserSettings settings,
                                              int intakeCalories,
                                              boolean intakeLoggedToday,
                                              String intakeLastLogged,
                                              int logsThisWeekCount) {
        Integer calorieTarget = settings != null ? settings.getMacroTargetCalories() : null;
        Integer proteinTarget = settings != null ? settings.getMacroTargetProtein() : null;
        String headline = intakeLoggedToday ? intakeCalories + " kcal" : "Ready to log";
        String subhead = calorieTarget != null ? "Target " + calorieTarget + " kcal" : "Set a calorie target";
        List<String> chips = new ArrayList<>();
        chips.add(intakeLoggedToday ? "Last log " + intakeLastLogged : "No entries today");
        chips.add(proteinTarget != null ? proteinTarget + "g protein goal" : "Add body targets");
        chips.add(logsThisWeekCount + " nutrition logs this week");
        return new TopActionCard(
                "Daily tracking",
                "Track Body",
                "/health-record/list",
                "BODY",
                headline,
                subhead,
                intakeLoggedToday
                        ? "Keep body and nutrition inputs current so the rest of the dashboard stays sharp."
                        : "Log weight, food, or recovery data first to anchor the rest of the day.",
                chips);
    }

    private TopActionCard buildScheduleActionCard(int todayTasks,
                                                  int todayWorkouts,
                                                  int todayTotal,
                                                  String todayPath,
                                                  MiniWeekDay todayDay) {
        String headline = todayTotal > 0 ? todayTotal + " items live today" : "Day is clear";
        String subhead = todayTasks + " tasks | " + todayWorkouts + " workouts";
        List<String> chips = new ArrayList<>();
        chips.add(todayTotal > 0 ? "Open today and keep the flow moving" : "Use the planner to prep ahead");
        if (!todayDay.taskTitles().isEmpty()) {
            chips.add("Next task " + todayDay.taskTitles().get(0));
        }
        if (!todayDay.workoutTitles().isEmpty()) {
            chips.add("Workout " + todayDay.workoutTitles().get(0));
        }
        return new TopActionCard(
                "Planner",
                "Track Schedule",
                todayPath,
                "PLAN",
                headline,
                subhead,
                todayTotal > 0
                        ? "Your day plan lives here. Open it first when you want the clearest next move."
                        : "Your board is light today, so use the planner to set up the next active block.",
                chips.stream().limit(3).toList());
    }

    private ClientDashboardActionHubView buildActionHubView(LocalDate today,
                                                            String todayPath,
                                                            List<MiniWeekDay> miniWeek,
                                                            List<CalendarTask> openTasks,
                                                            List<ScheduleOccurrence> openWorkouts,
                                                            int intakeCalories,
                                                            String intakeLastLogged,
                                                            MealWindow mealWindow,
                                                            LocalDateTime now) {
        List<ClientDashboardActionCardView> cards = new ArrayList<>();

        if (!openTasks.isEmpty()) {
            CalendarTask firstTask = openTasks.get(0);
            cards.add(new ClientDashboardActionCardView(
                    "task",
                    "Task",
                    trimToNull(firstTask.getTitle()) != null ? firstTask.getTitle() : "Open today",
                    buildTaskDescription(firstTask, openTasks.size(), now.toLocalTime()),
                    todayPath,
                    "Open day",
                    "outline",
                    "TASK",
                    resolveTaskBadge(firstTask, now.toLocalTime()),
                    buildTaskStats(openTasks),
                    "Route",
                    "Clear the most time-sensitive task before the rest of the board fans out.",
                    resolveTaskPriority(firstTask, now.toLocalTime()),
                    firstTask.getTime() != null ? firstTask.getTime().format(SHORT_TIME_FMT) : String.valueOf(openTasks.size()),
                    firstTask.getTime() != null ? "scheduled" : "tasks open"));
        }

        if (!openWorkouts.isEmpty()) {
            String workoutTitle = resolveWorkoutTitle(openWorkouts.get(0));
            cards.add(new ClientDashboardActionCardView(
                    "workout",
                    "Workout",
                    workoutTitle,
                    openWorkouts.size() > 1
                            ? openWorkouts.size() + " workouts are sitting in today's plan. Start the first session and the rest of the day becomes clearer."
                            : "Your workout is already scheduled. Open the day, review the session, and start when ready.",
                    todayPath,
                    "Start workout",
                    "emerald",
                    "WORK",
                    "Today",
                    List.of(openWorkouts.size() + " workout" + (openWorkouts.size() == 1 ? "" : "s") + " queued", "Planner ready"),
                    "Focus",
                    "Training stays highest-value when it remains visible before lower-friction admin tasks.",
                    !openTasks.isEmpty() ? 360 : 620,
                    String.valueOf(openWorkouts.size()),
                    "workouts waiting"));
        }

        cards.add(new ClientDashboardActionCardView(
                "meal",
                "Nutrition",
                mealWindow.active() ? "Log " + mealWindow.label() : "Log your meals",
                mealWindow.active()
                        ? "You are in the " + mealWindow.label().toLowerCase(Locale.UK) + " window. Logging now keeps the day grounded."
                        : "Meal logging is your clean fallback when nothing timed is about to hit. Keep intake current and easy to review.",
                "/nutrition",
                "Log meal",
                "soft",
                "MEAL",
                mealWindow.active() ? "Now" : "Keep ready",
                List.of(intakeCalories + " kcal logged", "Last entry " + intakeLastLogged),
                "Baseline",
                "Nutrition context makes the rest of the dashboard easier to interpret.",
                mealWindow.active() ? 520 : 180,
                mealWindow.active() ? mealWindow.label() : "Meal",
                mealWindow.active() ? "best logging window" : "fallback action"));

        cards.sort(Comparator.comparingInt(ClientDashboardActionCardView::getPriority).reversed());
        ClientDashboardActionCardView primaryCard = cards.isEmpty() ? null : cards.get(0);
        List<ClientDashboardActionCardView> secondaryCards = cards.size() > 1 ? cards.subList(1, cards.size()) : List.of();
        ClientDashboardActionUpcomingView upcoming = buildActionUpcomingView(today, todayPath, miniWeek, openTasks, openWorkouts, now);

        return new ClientDashboardActionHubView(
                "What should I do next?",
                primaryCard != null
                        ? "One clear next move is surfaced first. Switch to all if you want the full board."
                        : "Nothing urgent is blocking the day, so use the planner or nutrition log to stay ahead.",
                !cards.isEmpty(),
                "Nothing urgent right now",
                "Your board is clear enough to choose deliberately. Open the planner to prep ahead or log a meal to keep your day complete.",
                upcoming,
                primaryCard,
                secondaryCards,
                List.copyOf(cards));
    }

    private ClientDashboardActionUpcomingView buildActionUpcomingView(LocalDate today,
                                                                     String todayPath,
                                                                     List<MiniWeekDay> miniWeek,
                                                                     List<CalendarTask> openTasks,
                                                                     List<ScheduleOccurrence> openWorkouts,
                                                                     LocalDateTime now) {
        LocalTime currentTime = now.toLocalTime();
        CalendarTask nextTimedTask = openTasks.stream()
                .filter(task -> task.getTime() != null && task.getTime().isAfter(currentTime))
                .findFirst()
                .orElse(null);
        if (nextTimedTask != null) {
            LocalDateTime target = LocalDateTime.of(today, nextTimedTask.getTime());
            return new ClientDashboardActionUpcomingView(
                    true,
                    "Timed next",
                    trimToNull(nextTimedTask.getTitle()) != null ? nextTimedTask.getTitle() : "Upcoming task",
                    "Due today at " + nextTimedTask.getTime().format(SHORT_TIME_FMT) + ".",
                    todayPath,
                    target.atZone(clock.getZone()).toInstant().toString(),
                    "Live countdown",
                    true);
        }
        if (!openWorkouts.isEmpty()) {
            return new ClientDashboardActionUpcomingView(
                    true,
                    "Workout queue",
                    resolveWorkoutTitle(openWorkouts.get(0)),
                    "This session is already in today's plan and ready when you are.",
                    todayPath,
                    null,
                    "Today",
                    false);
        }
        MiniWeekDay nextLoadedDay = miniWeek.stream()
                .filter(day -> !day.date().isBefore(today))
                .filter(day -> day.taskCount() + day.scheduledCount() > 0)
                .skip(1)
                .findFirst()
                .orElse(null);
        if (nextLoadedDay == null) {
            return new ClientDashboardActionUpcomingView(false, null, null, null, null, null, null, false);
        }
        String title = !nextLoadedDay.workoutTitles().isEmpty()
                ? nextLoadedDay.workoutTitles().get(0)
                : (!nextLoadedDay.taskTitles().isEmpty() ? nextLoadedDay.taskTitles().get(0) : "Upcoming day");
        return new ClientDashboardActionUpcomingView(
                true,
                nextLoadedDay.scheduledCount() > 0 ? "Next workout day" : "Next task day",
                title,
                nextLoadedDay.prettyDate() + " has " + nextLoadedDay.taskCount() + " tasks and "
                        + nextLoadedDay.scheduledCount() + " workouts lined up.",
                pathForDate(nextLoadedDay.date()),
                null,
                nextLoadedDay.prettyDate(),
                false);
    }

    private ClientDashboardProfileRailView buildProfileRailView(User user,
                                                                UserSettings settings,
                                                                boolean isPremium,
                                                                String premiumTooltip,
                                                                DashboardSummaryDto summary,
                                                                int todayTotal) {
        ClientDashboardIdentityView identity = buildIdentityView(user, settings, isPremium, premiumTooltip);
        LevelProgress progress = user != null ? user.getLevelProgress() : null;
        int points = progress != null ? progress.getPoints() : 0;
        int level = progress != null ? progress.getLevel() : 1;
        List<String> milestones = parseProfileMilestones(settings != null ? settings.getProfileMilestoneKeys() : null).stream()
                .map(this::mapMilestoneTitle)
                .toList();
        String bio = trimToNull(user != null ? user.getBio() : null);
        return new ClientDashboardProfileRailView(
                identity,
                bio != null ? bio : "Add a short bio so your dashboard has more identity at a glance.",
                points,
                level,
                milestones,
                "Daily focus",
                todayTotal > 0
                        ? "Keep the next action visible and the rest of the day gets easier to follow."
                        : "Your plan is light today, so use the extra space to prepare tomorrow cleanly.",
                summary.getWorkoutStreak());
    }

    private ClientDashboardTrainerRailView buildTrainerRailView(User client) {
        TrainerClientLink activeLink = trainerClientLinkRepository.findActiveByClientId(client.getId()).orElse(null);
        TrainerClientLink pendingLink = trainerClientLinkRepository
                .findByClientUserIdAndStatusOrderByUpdatedAtDesc(client.getId(), TrainerClientLinkStatus.REQUESTED)
                .stream()
                .findFirst()
                .orElse(null);

        if (activeLink != null) {
            User trainer = userRepository.findById(activeLink.getTrainerUserId()).orElse(null);
            UserSettings trainerSettings = trainer != null ? userSettingsRepository.findById(trainer.getId()).orElse(null) : null;
            List<AssignedWorkout> assignedWorkouts = trainerAssignmentService.listWorkoutsForClient(client.getId());
            List<AssignedSchedule> assignedSchedules = trainerAssignmentService.listSchedulesForClient(client.getId());
            List<TrainerLibraryAssignedWorkoutView> sharedWorkouts = trainerLibraryService.getAssignedWorkoutsForClient(client.getId());
            List<TrainerLibraryAssignedProgrammeView> sharedProgrammes = trainerLibraryService.getAssignedProgrammesForClient(client.getId());
            LevelProgress trainerProgress = trainer != null ? trainer.getLevelProgress() : null;

            String planHref = resolveTrainerPlanHref(sharedWorkouts.size(), sharedProgrammes.size(), assignedWorkouts.size(), assignedSchedules.size());
            String planCtaLabel = sharedWorkouts.size() + sharedProgrammes.size() > 0 ? "View assigned plan" : "Open trainer plan";
            String summaryLine = joinNonBlank(List.of(
                    buildCountLabel(sharedProgrammes.size(), "shared programme"),
                    buildCountLabel(sharedWorkouts.size(), "shared workout"),
                    buildCountLabel(assignedWorkouts.size(), "assigned workout"),
                    buildCountLabel(assignedSchedules.size(), "assigned schedule")));

            return new ClientDashboardTrainerRailView(
                    "ACTIVE",
                    "Active coach",
                    trainer != null
                            ? buildIdentityView(trainer, trainerSettings,
                            platformSubscriptionService.isPremium(trainer.getId(), clock),
                            buildPremiumTooltip(trainer.getId()))
                            : null,
                    trimToNull(trainer != null ? trainer.getBio() : null),
                    trainerProgress != null ? trainerProgress.getPoints() : 0,
                    trainerProgress != null ? trainerProgress.getLevel() : 1,
                    parseProfileMilestones(trainerSettings != null ? trainerSettings.getProfileMilestoneKeys() : null)
                            .stream()
                            .map(this::mapMilestoneTitle)
                            .limit(4)
                            .toList(),
                    resolveCoachingPhase(activeLink, "Coaching active"),
                    buildRelationshipLabel(activeLink, "Connected"),
                    summaryLine.isBlank() ? "Your trainer link is active and ready to support today's plan." : summaryLine,
                    resolveTrainerMessageHref(activeLink),
                    planHref,
                    "/client/trainers",
                    planCtaLabel,
                    buildTrainerContext(activeLink, assignedWorkouts, assignedSchedules,
                            sharedWorkouts.size(), sharedProgrammes.size(), trainer));
        }

        if (pendingLink != null) {
            User trainer = userRepository.findById(pendingLink.getTrainerUserId()).orElse(null);
            UserSettings trainerSettings = trainer != null ? userSettingsRepository.findById(trainer.getId()).orElse(null) : null;
            LevelProgress trainerProgress = trainer != null ? trainer.getLevelProgress() : null;
            return new ClientDashboardTrainerRailView(
                    "REQUESTED",
                    "Request sent",
                    trainer != null
                            ? buildIdentityView(trainer, trainerSettings,
                            platformSubscriptionService.isPremium(trainer.getId(), clock),
                            buildPremiumTooltip(trainer.getId()))
                            : null,
                    trimToNull(trainer != null ? trainer.getBio() : null),
                    trainerProgress != null ? trainerProgress.getPoints() : 0,
                    trainerProgress != null ? trainerProgress.getLevel() : 1,
                    parseProfileMilestones(trainerSettings != null ? trainerSettings.getProfileMilestoneKeys() : null)
                            .stream()
                            .map(this::mapMilestoneTitle)
                            .limit(4)
                            .toList(),
                    "Awaiting trainer approval",
                    buildRelationshipLabel(pendingLink, "Requested"),
                    "You can keep using the dashboard while this coach request is waiting for approval.",
                    null,
                    "/client/trainers",
                    "/client/trainers",
                    "Review trainers",
                    new ClientDashboardTrainerContextView(
                            "Next step",
                            "Waiting for confirmation",
                            "Once the request is accepted, coach notes, shared plans, and messaging can appear here.",
                            "You can still browse trainers at any time."));
        }

        return new ClientDashboardTrainerRailView(
                "EMPTY",
                "No trainer connected",
                null,
                null,
                0,
                1,
                List.of(),
                "Independent mode",
                "Optional coaching",
                "You can use the full dashboard without a trainer, and connect later if you want guided planning.",
                null,
                "/client/trainers",
                "/client/trainers",
                "Connect with a trainer",
                new ClientDashboardTrainerContextView(
                        "Why it helps",
                        "Bring coaching into the same dashboard",
                        "A trainer can add notes, share plans, and keep your next steps visible without adding clutter.",
                        "Explore trainers if you want more hands-on guidance."));
    }

    private ClientDashboardTrainerContextView buildTrainerContext(TrainerClientLink activeLink,
                                                                  List<AssignedWorkout> assignedWorkouts,
                                                                  List<AssignedSchedule> assignedSchedules,
                                                                  int sharedWorkoutCount,
                                                                  int sharedProgrammeCount,
                                                                  User trainer) {
        WeeklyCheckIn recentCheckIn = weeklyCheckInRepository.findByClientIdOrderBySubmittedAtDesc(activeLink.getClientUserId()).stream()
                .filter(checkIn -> checkIn.getTrainerId() != null && checkIn.getTrainerId().equals(activeLink.getTrainerUserId()))
                .filter(checkIn -> trimToNull(checkIn.getTrainerResponse()) != null || trimToNull(checkIn.getNextWeekFocus()) != null)
                .findFirst()
                .orElse(null);
        if (recentCheckIn != null) {
            String focus = trimToNull(recentCheckIn.getNextWeekFocus());
            String response = trimToNull(recentCheckIn.getTrainerResponse());
            return new ClientDashboardTrainerContextView(
                    "Coach feedback",
                    focus != null ? focus : "Latest weekly review",
                    response != null ? response : "Your trainer added a new focus for the coming week.",
                    "Week of " + recentCheckIn.getWeekStartDate().format(FULL_DATE_FMT));
        }

        LatestTrainerNote latestTrainerNote = findLatestTrainerNote(assignedWorkouts, assignedSchedules);
        if (latestTrainerNote != null) {
            return new ClientDashboardTrainerContextView(
                    "Latest note",
                    latestTrainerNote.sourceTitle(),
                    latestTrainerNote.noteBody(),
                    latestTrainerNote.meta());
        }

        if (sharedWorkoutCount + sharedProgrammeCount > 0 || !assignedWorkouts.isEmpty() || !assignedSchedules.isEmpty()) {
            return new ClientDashboardTrainerContextView(
                    "Plan status",
                    "Your trainer has current work ready",
                    buildPlanSummaryBody(sharedWorkoutCount, sharedProgrammeCount, assignedWorkouts.size(), assignedSchedules.size()),
                    "Open the trainer plan to review details and notes.");
        }

        String trainerBio = trimToNull(trainer != null ? trainer.getBio() : null);
        return new ClientDashboardTrainerContextView(
                "Coach context",
                "Your trainer link is active",
                trainerBio != null
                        ? trainerBio
                        : "Use the trainer card to keep your coach relationship visible while you manage your day-to-day plan.",
                "Message your trainer or open the plan when you need direction.");
    }

    private LatestTrainerNote findLatestTrainerNote(List<AssignedWorkout> assignedWorkouts, List<AssignedSchedule> assignedSchedules) {
        AssignedWorkout workoutWithNote = assignedWorkouts.stream()
                .filter(item -> trimToNull(item.getTrainerNotes()) != null)
                .findFirst()
                .orElse(null);
        AssignedSchedule scheduleWithNote = assignedSchedules.stream()
                .filter(item -> trimToNull(item.getTrainerNotes()) != null)
                .findFirst()
                .orElse(null);

        if (workoutWithNote == null && scheduleWithNote == null) {
            return null;
        }
        if (scheduleWithNote == null) {
            return new LatestTrainerNote(
                    workoutWithNote.getWorkoutTemplate() != null ? workoutWithNote.getWorkoutTemplate().getName() : "Assigned workout",
                    trimToNull(workoutWithNote.getTrainerNotes()),
                    "Assigned " + workoutWithNote.getAssignedAt().toLocalDate().format(FULL_DATE_FMT));
        }
        if (workoutWithNote == null) {
            Schedule schedule = scheduleWithNote.getSchedule();
            return new LatestTrainerNote(
                    schedule != null ? schedule.getName() : "Assigned schedule",
                    trimToNull(scheduleWithNote.getTrainerNotes()),
                    "Assigned " + scheduleWithNote.getAssignedAt().toLocalDate().format(FULL_DATE_FMT));
        }
        if (scheduleWithNote.getAssignedAt().isAfter(workoutWithNote.getAssignedAt())) {
            Schedule schedule = scheduleWithNote.getSchedule();
            return new LatestTrainerNote(
                    schedule != null ? schedule.getName() : "Assigned schedule",
                    trimToNull(scheduleWithNote.getTrainerNotes()),
                    "Assigned " + scheduleWithNote.getAssignedAt().toLocalDate().format(FULL_DATE_FMT));
        }
        return new LatestTrainerNote(
                workoutWithNote.getWorkoutTemplate() != null ? workoutWithNote.getWorkoutTemplate().getName() : "Assigned workout",
                trimToNull(workoutWithNote.getTrainerNotes()),
                "Assigned " + workoutWithNote.getAssignedAt().toLocalDate().format(FULL_DATE_FMT));
    }

    private String resolveTrainerPlanHref(int sharedWorkoutCount, int sharedProgrammeCount, int assignedWorkoutCount, int assignedScheduleCount) {
        if (sharedWorkoutCount + sharedProgrammeCount > 0) {
            return "/client/assigned-plan";
        }
        if (assignedWorkoutCount + assignedScheduleCount > 0) {
            return "/client/plan";
        }
        return "/client/trainers";
    }

    private String resolveTrainerMessageHref(TrainerClientLink link) {
        if (link == null || link.getStatus() != TrainerClientLinkStatus.ACTIVE) {
            return null;
        }
        MessageThread thread = messageThreadRepository.findByLinkId(link.getId())
                .orElseGet(() -> messagingService.ensureThreadForLink(link));
        return thread != null ? "/messages/" + thread.getId() : null;
    }

    private String buildPlanSummaryBody(int sharedWorkoutCount,
                                        int sharedProgrammeCount,
                                        int assignedWorkoutCount,
                                        int assignedScheduleCount) {
        List<String> parts = new ArrayList<>();
        if (sharedProgrammeCount > 0) {
            parts.add(buildCountLabel(sharedProgrammeCount, "shared programme"));
        }
        if (sharedWorkoutCount > 0) {
            parts.add(buildCountLabel(sharedWorkoutCount, "shared workout"));
        }
        if (assignedWorkoutCount > 0) {
            parts.add(buildCountLabel(assignedWorkoutCount, "assigned workout"));
        }
        if (assignedScheduleCount > 0) {
            parts.add(buildCountLabel(assignedScheduleCount, "assigned schedule"));
        }
        if (parts.isEmpty()) {
            return "Your coach relationship is live, but no plan items have been pushed into the dashboard yet.";
        }
        return joinNonBlank(parts) + " currently supporting your dashboard.";
    }

    private String buildRelationshipLabel(TrainerClientLink link, String prefix) {
        Instant timestamp = link.getActivatedAt() != null
                ? link.getActivatedAt()
                : (link.getRequestedAt() != null ? link.getRequestedAt() : link.getUpdatedAt());
        if (timestamp == null) {
            return prefix;
        }
        return prefix + " " + FULL_DATE_FMT.format(timestamp.atZone(clock.getZone()).toLocalDate());
    }

    private String resolveCoachingPhase(TrainerClientLink link, String fallback) {
        String customLabel = trimToNull(link.getCoachingPhaseLabel());
        if (customLabel != null) {
            return customLabel;
        }
        if (link.getCoachingPhase() != null) {
            return humanize(link.getCoachingPhase().name());
        }
        return fallback;
    }

    private ClientDashboardIdentityView buildIdentityView(User user,
                                                          UserSettings settings,
                                                          boolean premium,
                                                          String premiumTooltip) {
        String bannerTheme = settings != null && trimToNull(settings.getProfileBannerTheme()) != null
                ? settings.getProfileBannerTheme()
                : "AURORA";
        String ringStyle = settings != null && trimToNull(settings.getProfileRingStyle()) != null
                ? settings.getProfileRingStyle()
                : "NEON_DUAL";
        String cardBackStyle = settings != null && trimToNull(settings.getProfileCardBackStyle()) != null
                ? settings.getProfileCardBackStyle()
                : "GLASS";
        String username = user != null ? trimToNull(user.getUsername()) : null;
        String initial = username != null && !username.isBlank()
                ? username.substring(0, 1).toUpperCase(Locale.UK)
                : "U";
        return new ClientDashboardIdentityView(
                user != null ? user.getFullName() : "Profile",
                username,
                user != null ? trimToNull(user.getProfileImageUrl()) : null,
                initial,
                bannerTheme,
                ringStyle,
                cardBackStyle,
                premium,
                premiumTooltip);
    }

    private String buildPremiumTooltip(Long userId) {
        if (userId == null) {
            return "Premium active";
        }
        Optional<PlatformSubscription> existing = platformSubscriptionService.findByUserId(userId);
        if (existing.isEmpty()) {
            return "Premium active";
        }
        PlatformSubscription subscription = existing.get();
        if (subscription.getPlan() == PlatformPlan.INFINITE) {
            return "Lifetime Premium active";
        }
        Long daysLeft = platformSubscriptionService.getDaysUntilRenewal(userId, clock);
        if (daysLeft != null && daysLeft >= 0 && daysLeft <= 5) {
            return subscription.getStatus().name().equals("EXPIRES")
                    ? "Access ends in " + daysLeft + " day" + (daysLeft == 1 ? "" : "s")
                    : "Renews in " + daysLeft + " day" + (daysLeft == 1 ? "" : "s");
        }
        return "Premium Active";
    }

    private String pathForDate(LocalDate date) {
        if (date == null) {
            return "/calendar";
        }
        return "/calendar/day/" + date;
    }

    private int ratioPercent(int value, int total) {
        if (total <= 0) {
            return 0;
        }
        return (int) Math.max(0, Math.min(100, Math.round((value * 100.0) / total)));
    }

    private List<WeeklySummaryCard> buildWeeklySummaryCards(User user,
                                                            UserSettings settings,
                                                            DashboardSummaryDto summary,
                                                            int weekWorkoutsCompleted,
                                                            int weekWorkoutsRemaining,
                                                            int weekTasksCompleted,
                                                            int weekTasksRemaining,
                                                            int weekTasksTotal) {
        List<String> selected = parseWeeklyMetricKeys(settings != null ? settings.getWeeklySummaryMetrics() : null);

        List<HealthRecord> topHealthRecords = user != null
                ? healthRecordRepository.findTop2ByUserOrderByBaselineDateDescIdDesc(user)
                : List.of();

        List<WeeklySummaryCard> cards = new ArrayList<>();
        for (String key : selected) {
            switch (key) {
                case "WORKOUTS_COMPLETED" -> cards.add(new WeeklySummaryCard(
                        "Workouts completed",
                        weekWorkoutsCompleted + " / " + Math.max(weekWorkoutsCompleted + weekWorkoutsRemaining, 1),
                        "Completed inside this week",
                        ratioPercent(weekWorkoutsCompleted, weekWorkoutsCompleted + weekWorkoutsRemaining),
                        "Progress against your planned sessions",
                        "emerald"));
                case "TASKS_COMPLETED" -> cards.add(new WeeklySummaryCard(
                        "Tasks completed",
                        weekTasksCompleted + " / " + Math.max(weekTasksTotal, 1),
                        "Checklist items cleared",
                        ratioPercent(weekTasksCompleted, weekTasksTotal),
                        "Task completion this week",
                        "outline"));
                case "MEALS_LOGGED" -> cards.add(new WeeklySummaryCard(
                        "Meals logged",
                        String.valueOf(summary.getLogsThisWeekCount()),
                        "Nutrition logs recorded",
                        ratioPercent(summary.getLogsThisWeekCount(), 7),
                        "Logging consistency over the last 7 days",
                        "emerald"));
                case "HABITS_COMPLETED" -> cards.add(new WeeklySummaryCard(
                        "Habits completed",
                        String.valueOf(weekTasksCompleted),
                        "Habits marked complete",
                        ratioPercent(weekTasksCompleted, Math.max(weekTasksCompleted + weekTasksRemaining, 1)),
                        "Follow-through across the week",
                        "outline"));
                case "WEIGHT_TREND" -> cards.add(buildWeightTrendCard(topHealthRecords));
                case "WORKOUT_STREAK" -> cards.add(new WeeklySummaryCard(
                        "Workout streak",
                        summary.getWorkoutStreak() + " days",
                        "Current consecutive streak",
                        ratioPercent(summary.getWorkoutStreak(), 7),
                        "Recent consistency streak",
                        "emerald"));
                default -> {
                }
            }
            if (cards.size() >= 4) {
                break;
            }
        }

        if (cards.isEmpty()) {
            cards.add(new WeeklySummaryCard(
                    "Workouts completed",
                    weekWorkoutsCompleted + " / " + Math.max(weekWorkoutsCompleted + weekWorkoutsRemaining, 1),
                    "Completed inside this week",
                    ratioPercent(weekWorkoutsCompleted, weekWorkoutsCompleted + weekWorkoutsRemaining),
                    "Progress against your planned sessions",
                    "emerald"));
            cards.add(new WeeklySummaryCard(
                    "Meals logged",
                    String.valueOf(summary.getLogsThisWeekCount()),
                    "Nutrition logs recorded",
                    ratioPercent(summary.getLogsThisWeekCount(), 7),
                    "Logging consistency over the last 7 days",
                    "outline"));
            cards.add(new WeeklySummaryCard(
                    "Habits completed",
                    String.valueOf(weekTasksCompleted),
                    "Habits marked complete",
                    ratioPercent(weekTasksCompleted, Math.max(weekTasksCompleted + weekTasksRemaining, 1)),
                    "Follow-through across the week",
                    "outline"));
        }
        return cards;
    }

    private WeeklySummaryCard buildWeightTrendCard(List<HealthRecord> records) {
        if (records == null || records.isEmpty() || records.get(0).getWeightKg() == null) {
            return new WeeklySummaryCard(
                    "Weight trend",
                    "Not tracked",
                    "Add body records to unlock this",
                    12,
                    "No recent body data",
                    "outline");
        }

        Double latest = records.get(0).getWeightKg();
        if (records.size() < 2 || records.get(1).getWeightKg() == null) {
            return new WeeklySummaryCard(
                    "Weight trend",
                    String.format(Locale.UK, "%.1f kg", latest),
                    "Latest recorded weight",
                    100,
                    "Single logged body record available",
                    "emerald");
        }

        Double previous = records.get(1).getWeightKg();
        double delta = latest - previous;
        String value = String.format(Locale.UK, "%+.1f kg", delta);
        return new WeeklySummaryCard(
                "Weight trend",
                value,
                delta < 0 ? "Moving down from the previous log" : (delta > 0 ? "Moving up from the previous log" : "Stable against the previous log"),
                100,
                "Compared to your previous body record",
                delta <= 0 ? "emerald" : "outline");
    }

    private List<GoalPreviewCard> buildGoalPreviewCards(List<Goal> goals, LocalDate today) {
        return goals.stream()
                .limit(3)
                .map(goal -> new GoalPreviewCard(
                        goal.getId(),
                        goal.getTitle(),
                        goal.getStatus() != null ? humanize(goal.getStatus().name()) : "Draft",
                        buildGoalMetric(goal),
                        buildGoalTimeline(goal, today),
                        estimateGoalProgress(goal, today)))
                .toList();
    }

    private int estimateGoalProgress(Goal goal, LocalDate today) {
        if (goal.getStatus() == GoalStatus.COMPLETED) {
            return 100;
        }
        if (goal.getStatus() == GoalStatus.ACTIVE && goal.getStartDate() != null && goal.getTargetDate() != null
                && !goal.getTargetDate().isBefore(goal.getStartDate())) {
            long totalDays = Math.max(ChronoUnit.DAYS.between(goal.getStartDate(), goal.getTargetDate()), 1);
            long elapsed = ChronoUnit.DAYS.between(goal.getStartDate(), today);
            long bounded = Math.min(Math.max(elapsed, 0), totalDays);
            return (int) Math.max(12, Math.min(100, Math.round((bounded * 100.0) / totalDays)));
        }
        if (goal.getStatus() == GoalStatus.ACTIVE) {
            return 56;
        }
        return 18;
    }

    private String buildGoalMetric(Goal goal) {
        if (goal.getTargetMetricValue() != null && trimToNull(goal.getTargetMetricName()) != null) {
            String unit = trimToNull(goal.getTargetMetricUnit());
            String value = goal.getTargetMetricValue() % 1 == 0
                    ? String.valueOf(goal.getTargetMetricValue().intValue())
                    : String.format(Locale.UK, "%.1f", goal.getTargetMetricValue());
            return value + (unit != null ? unit : "") + " " + goal.getTargetMetricName();
        }
        if (goal.getGoalType() != null) {
            return humanize(goal.getGoalType().name());
        }
        return "Goal";
    }

    private String buildGoalTimeline(Goal goal, LocalDate today) {
        if (goal.getStatus() == GoalStatus.COMPLETED) {
            return "Completed";
        }
        if (goal.getTargetDate() != null) {
            if (!goal.getTargetDate().isBefore(today)) {
                long days = ChronoUnit.DAYS.between(today, goal.getTargetDate());
                return days == 0 ? "Due today" : "Due in " + days + " day" + (days == 1 ? "" : "s");
            }
            long daysOver = ChronoUnit.DAYS.between(goal.getTargetDate(), today);
            return daysOver == 0 ? "Due today" : daysOver + " day" + (daysOver == 1 ? "" : "s") + " overdue";
        }
        return "No end date set";
    }

    private String buildTaskDescription(CalendarTask task, int totalOpen, LocalTime now) {
        String title = trimToNull(task.getTitle()) != null ? task.getTitle() : "Open task";
        if (task.getTime() != null && task.getTime().isBefore(now)) {
            return title + " is already past its scheduled time. Clear it first to reduce drag across the rest of the day.";
        }
        if (task.getTime() != null) {
            return title + " is scheduled for " + task.getTime().format(SHORT_TIME_FMT)
                    + ". Handling it next keeps the rest of today's list cleaner.";
        }
        return totalOpen > 1
                ? totalOpen + " tasks are still open today. Starting with this one keeps the planner under control."
                : "You have one open task left today, so clearing it now removes the biggest blocker from the board.";
    }

    private String resolveTaskBadge(CalendarTask task, LocalTime now) {
        if (task.getTime() == null) {
            return "Due today";
        }
        if (task.getTime().isBefore(now)) {
            return "Overdue";
        }
        if (task.getTime().isBefore(now.plusMinutes(90))) {
            return "Soon";
        }
        return "Scheduled";
    }

    private int resolveTaskPriority(CalendarTask task, LocalTime now) {
        if (task.getTime() == null) {
            return 640;
        }
        if (task.getTime().isBefore(now)) {
            return 980;
        }
        if (task.getTime().isBefore(now.plusMinutes(90))) {
            return 920;
        }
        return 760;
    }

    private List<String> buildTaskStats(List<CalendarTask> openTasks) {
        CalendarTask firstTask = openTasks.get(0);
        List<String> stats = new ArrayList<>();
        stats.add(openTasks.size() + " task" + (openTasks.size() == 1 ? "" : "s") + " open");
        stats.add(firstTask.getTime() != null
                ? "Next at " + firstTask.getTime().format(SHORT_TIME_FMT)
                : "No set time on first task");
        return stats;
    }

    private String resolveWorkoutTitle(ScheduleOccurrence workout) {
        if (trimToNull(workout.getScheduleName()) != null) {
            return workout.getScheduleName();
        }
        if (workout.getExercise() != null && trimToNull(workout.getExercise().getName()) != null) {
            return workout.getExercise().getName();
        }
        if (workout.getCustomExercise() != null && trimToNull(workout.getCustomExercise().getName()) != null) {
            return workout.getCustomExercise().getName();
        }
        return "Scheduled workout";
    }

    private MealWindow resolveMealWindow(LocalTime now) {
        if (!now.isBefore(LocalTime.of(7, 0)) && now.isBefore(LocalTime.of(10, 0))) {
            return new MealWindow(true, "Breakfast");
        }
        if (!now.isBefore(LocalTime.of(11, 30)) && now.isBefore(LocalTime.of(14, 30))) {
            return new MealWindow(true, "Lunch");
        }
        if (!now.isBefore(LocalTime.of(17, 30)) && now.isBefore(LocalTime.of(21, 0))) {
            return new MealWindow(true, "Dinner");
        }
        return new MealWindow(false, nextMealLabel(now));
    }

    private String nextMealLabel(LocalTime now) {
        if (now.isBefore(LocalTime.of(10, 0))) {
            return "Breakfast";
        }
        if (now.isBefore(LocalTime.of(14, 30))) {
            return "Lunch";
        }
        if (now.isBefore(LocalTime.of(21, 0))) {
            return "Dinner";
        }
        return "Tomorrow";
    }

    private List<String> parseWeeklyMetricKeys(String raw) {
        Set<String> keys = new LinkedHashSet<>();
        if (raw != null && !raw.isBlank()) {
            for (String value : raw.split(",")) {
                if (value != null && !value.isBlank()) {
                    keys.add(value.trim().toUpperCase(Locale.ROOT));
                }
                if (keys.size() >= 6) {
                    break;
                }
            }
        }
        if (keys.isEmpty()) {
            keys.add("WORKOUTS_COMPLETED");
            keys.add("MEALS_LOGGED");
            keys.add("HABITS_COMPLETED");
        }
        return List.copyOf(keys);
    }

    private List<String> parseProfileMilestones(String raw) {
        Set<String> keys = new LinkedHashSet<>();
        if (raw == null || raw.isBlank()) {
            return List.of();
        }
        for (String part : raw.split(",")) {
            if (part != null && !part.isBlank()) {
                keys.add(part.trim().toUpperCase(Locale.ROOT));
            }
            if (keys.size() >= 6) {
                break;
            }
        }
        return List.copyOf(keys);
    }

    private String mapMilestoneTitle(String key) {
        return switch (key) {
            case "FIRST_WORKOUT" -> "First workout logged";
            case "TEN_WORKOUTS" -> "10 workouts";
            case "LEVEL_5" -> "Reached Level 5";
            case "LEVEL_10" -> "Reached Level 10";
            case "VERIFIED_EMAIL" -> "Verified email";
            case "VERIFIED_PHONE" -> "Verified phone";
            case "PREMIUM_MEMBER" -> "Premium member";
            default -> "Milestone";
        };
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isBlank() ? null : trimmed;
    }

    private String humanize(String raw) {
        if (raw == null || raw.isBlank()) {
            return "";
        }
        String lower = raw.toLowerCase(Locale.UK).replace('_', ' ');
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }

    private String buildCountLabel(int count, String singular) {
        if (count <= 0) {
            return "";
        }
        return count + " " + (count == 1 ? singular : singular + "s");
    }

    private String joinNonBlank(List<String> values) {
        return values.stream()
                .filter(value -> value != null && !value.isBlank())
                .reduce((left, right) -> left + " | " + right)
                .orElse("");
    }

    private record LatestTrainerNote(String sourceTitle, String noteBody, String meta) {
    }

    private record MealWindow(boolean active, String label) {
    }

    public record TopActionCard(String kicker,
                                String title,
                                String href,
                                String icon,
                                String headline,
                                String subhead,
                                String body,
                                List<String> chips) {
    }

    public record WeeklySummaryCard(String title,
                                    String value,
                                    String subtitle,
                                    int progressPercent,
                                    String progressLabel,
                                    String tone) {
    }

    public record GoalPreviewCard(Long id,
                                  String title,
                                  String statusLabel,
                                  String metricLabel,
                                  String timelineLabel,
                                  int progressPercent) {
    }
}
