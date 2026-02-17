package uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import jakarta.servlet.http.HttpServletRequest;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTask;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTaskService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTaskWarning;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTaskWarningService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.DailyCompletionCalculator;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.DailyCompletionStatus;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.DailyStreakService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.TaskAiGenerationService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.TaskTemplateService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.DayHealthData.DayHealthPersistenceService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Goals.GoalLinkService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.PlatformBilling.PlatformSubscriptionService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ReflectionData.ReflectionResult;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ReflectionData.ReflectionService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.UserSettings.CalendarTaskLayoutPreference;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.UserSettings.CalendarTaskOrderingPreference;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.UserSettings.CalendarViewPreference;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.UserSettings.CalendarWorkoutOrderingPreference;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.UserSettings.UserSettings;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.UserSettings.UserSettingsService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

@Controller
@RequestMapping("/calendar")
public class CalendarController {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private CalendarTaskService taskService;

    @Autowired
    private CalendarTaskWarningService taskWarningService;

    @Autowired
    private TaskTemplateService taskTemplateService;

    @Autowired
    private TaskAiGenerationService taskAiGenerationService;

    @Autowired
    private UserSettingsService userSettingsService;

    @Autowired
    private PlatformSubscriptionService platformSubscriptionService;

    @Autowired
    private Clock clock;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private ScheduleOccurrenceService scheduleOccurrenceService;
    
    @Autowired
    private uk.ac.cf._5.group14.BehaviourChangeGroupProject.StrengthLog.Service.WorkoutScheduleService workoutScheduleService;

    @Autowired
    private uk.ac.cf._5.group14.BehaviourChangeGroupProject.StrengthLog.Service.WorkoutSessionService workoutSessionService;

    @Autowired
    private ObjectProvider<uk.ac.cf._5.group14.BehaviourChangeGroupProject.FocusData.TimedFocusService> timedFocusServiceProvider;

    @Autowired
    private ObjectProvider<uk.ac.cf._5.group14.BehaviourChangeGroupProject.FocusData.DailyFocusAiService> dailyFocusAiServiceProvider;

    @Autowired
    private ObjectProvider<uk.ac.cf._5.group14.BehaviourChangeGroupProject.FocusData.DailyFocusService> dailyFocusServiceProvider;

    @Autowired
    private ObjectProvider<DayHealthPersistenceService> dayHealthPersistenceServiceProvider;

    @Autowired
    private ObjectProvider<ReflectionService> reflectionServiceProvider;

    @Autowired
    private ObjectProvider<DailyStreakService> dailyStreakServiceProvider;

    @Autowired
    private ObjectProvider<uk.ac.cf._5.group14.BehaviourChangeGroupProject.ConditionsPreferences.UserPreference.UserPreferenceService> userPreferenceServiceProvider;

    @Autowired
    private GoalLinkService goalLinkService;

    @GetMapping("")
    public String calendarView(
            @RequestParam(required = false) String view,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer week,
            @RequestParam(required = false) Integer weekYear,
            @RequestParam(required = false) String fragment,
            @SessionAttribute("user") User user,
            Model model,
            HttpServletRequest request
    ) {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        boolean isPremium = platformSubscriptionService.isPremium(user.getId(), clock);
        UserSettings userSettings = userSettingsService.getOrCreate(user);
        CalendarTaskLayoutPreference layoutPreference = userSettings.getCalendarTaskLayout();
        
        // Determine view based on: explicit parameter, stored preference, or device-based default
        String targetView = view;
        if (targetView == null) {
            // Check if user has a stored preference
            CalendarViewPreference storedView = userSettings.getCalendarViewPreference();
            if (storedView != null) {
                targetView = storedView.name().toLowerCase();
            } else {
                // Default based on device: mobile/tablet -> week, desktop -> month
                String userAgent = request.getHeader("User-Agent");
                boolean isMobile = userAgent != null && 
                    (userAgent.toLowerCase().contains("mobile") || 
                     userAgent.toLowerCase().contains("iphone") || 
                     userAgent.toLowerCase().contains("ipad") ||
                     userAgent.toLowerCase().contains("android"));
                targetView = isMobile ? "week" : "month";
            }
        }
        
        // Save the view preference when explicitly switched (not on initial load)
        if (view != null && !view.isEmpty()) {
            CalendarViewPreference newPref = "week".equals(view) ? CalendarViewPreference.WEEK : CalendarViewPreference.MONTH;
            if (userSettings.getCalendarViewPreference() != newPref) {
                userSettingsService.updateCalendarViewPreference(user, newPref);
            }
        }
        
        if ("week".equals(targetView)) {
            int currentWeekYear = (weekYear != null)
                    ? weekYear
                    : today.get(java.time.temporal.WeekFields.ISO.weekBasedYear());
            int targetWeek = (week != null)
                    ? week
                    : today.get(java.time.temporal.WeekFields.ISO.weekOfWeekBasedYear());
            LocalDate base = LocalDate.of(currentWeekYear, 1, 4);
            int maxWeek = (int) base.range(java.time.temporal.WeekFields.ISO.weekOfWeekBasedYear()).getMaximum();
            if (targetWeek < 1) targetWeek = 1;
            if (targetWeek > maxWeek) targetWeek = maxWeek;
            LocalDate weekStart = base
                    .with(java.time.temporal.WeekFields.ISO.weekOfWeekBasedYear(), targetWeek)
                    .with(java.time.DayOfWeek.MONDAY);
            LocalDate weekEnd = weekStart.plusDays(6);
            List<LocalDate> weekDays = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                weekDays.add(weekStart.plusDays(i));
            }
            Map<LocalDate, List<CalendarTask>> tasks = taskService.getTasksByRange(user, weekStart, weekEnd);
            Map<LocalDate, List<ScheduleOccurrence>> occ = scheduleOccurrenceService.getOccurrencesByRange(user, weekStart, weekEnd);

            int prevWeek = targetWeek - 1;
            int prevWeekYear = currentWeekYear;
            if (prevWeek < 1) {
                prevWeekYear = currentWeekYear - 1;
                LocalDate prevBase = LocalDate.of(prevWeekYear, 1, 4);
                prevWeek = (int) prevBase.range(java.time.temporal.WeekFields.ISO.weekOfWeekBasedYear()).getMaximum();
            }

            int nextWeek = targetWeek + 1;
            int nextWeekYear = currentWeekYear;
            if (nextWeek > maxWeek) {
                nextWeek = 1;
                nextWeekYear = currentWeekYear + 1;
            }

            model.addAttribute("week", targetWeek);
            model.addAttribute("weekYear", currentWeekYear);
            model.addAttribute("prevWeek", prevWeek);
            model.addAttribute("prevWeekYear", prevWeekYear);
            model.addAttribute("nextWeek", nextWeek);
            model.addAttribute("nextWeekYear", nextWeekYear);
            model.addAttribute("weekStart", weekStart);
            model.addAttribute("weekEnd", weekEnd);
            model.addAttribute("weekDays", weekDays);
            model.addAttribute("tasksByDate", tasks);
            model.addAttribute("tasksByDateIso", toIsoDateKeyedTaskMap(tasks));
            model.addAttribute("occurrences", occ);
            model.addAttribute("today", today);
            model.addAttribute("tomorrow", tomorrow);
            model.addAttribute("isPremium", isPremium);
            model.addAttribute("calendarLayout", layoutPreference);
            model.addAttribute("view", "week");
            model.addAttribute("schedules", scheduleService.findByUser(user));
            if ("weekPane".equals(fragment)) {
                return "calendar/week :: weekPane";
            }
            return "calendar/week";
        }

        if (month == null || year == null) {
            month = today.getMonthValue();
            year = today.getYear();
        }
        if (month < 1) {
            month = 12;
            year = year - 1;
        }
        if (month > 12) {
            month = 1;
            year = year + 1;
        }

        LocalDate current = LocalDate.of(year, month, 1);
        LocalDate prev = current.minusMonths(1);
        LocalDate next = current.plusMonths(1);

        model.addAttribute("month", month);
        model.addAttribute("year", year);
        model.addAttribute("lengthOfMonth", current.lengthOfMonth());
        model.addAttribute("offset", current.getDayOfWeek().getValue());
        model.addAttribute("today", today);
        model.addAttribute("tomorrow", tomorrow);
        model.addAttribute("isPremium", isPremium);
        model.addAttribute("calendarLayout", layoutPreference);
        model.addAttribute("prevMonth", prev.getMonthValue());
        model.addAttribute("prevYear", prev.getYear());
        model.addAttribute("nextMonth", next.getMonthValue());
        model.addAttribute("nextYear", next.getYear());
        Map<LocalDate, List<CalendarTask>> tasksByDate = taskService.getTasksGroupedByDate(user);
        model.addAttribute("tasksByDate", tasksByDate);
        model.addAttribute("tasksByDateIso", toIsoDateKeyedTaskMap(tasksByDate));
        model.addAttribute("occurrences", scheduleOccurrenceService.getOccurrencesForUserInMonth(user, year, month));
        model.addAttribute("schedules", scheduleService.findByUser(user));
        model.addAttribute("view", "month");

        if ("monthPane".equals(fragment)) {
            return "calendar/month :: monthPane";
        }
        return "calendar/month";
    }

    @GetMapping("/month-fragment")
    public String monthFragment(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @SessionAttribute("user") User user,
            Model model,
            HttpServletRequest request
    ) {
        return calendarView("month", month, year, null, null, "monthPane", user, model, request);
    }

    @GetMapping("/week-fragment")
    public String weekFragment(
            @RequestParam(required = false) Integer week,
            @RequestParam(required = false) Integer weekYear,
            @SessionAttribute("user") User user,
            Model model,
            HttpServletRequest request
    ) {
        return calendarView("week", null, null, week, weekYear, "weekPane", user, model, request);
    }

    @PostMapping("/preferences")
    public String updateCalendarPreferences(
            @SessionAttribute(name = "user") User user,
            @RequestParam(name = "layout", required = false) CalendarTaskLayoutPreference layout,
            @RequestParam(name = "redirect", required = false) String redirect
    ) {
        userSettingsService.updateCalendarPreferences(user, null, layout);
        if (redirect != null && redirect.startsWith("/calendar")) {
            return "redirect:" + redirect;
        }
        return "redirect:/calendar";
    }

    @GetMapping("/day/{dateStr}")
    public String dayView(
            @PathVariable String dateStr,
            @RequestParam(name = "dailyFocus", required = false) String dailyFocus,
            @SessionAttribute("user") User user,
            Model model
    ) {
        LocalDate date = LocalDate.parse(dateStr, DATE_FORMAT);

        model.addAttribute("isToday", date.equals(LocalDate.now()));

        model.addAttribute("date", date);
        model.addAttribute("prevDate", date.minusDays(1).format(DATE_FORMAT));
        model.addAttribute("nextDate", date.plusDays(1).format(DATE_FORMAT));

        var timedFocusService = timedFocusServiceProvider.getIfAvailable();
        var timedFocus = (timedFocusService != null)
            ? timedFocusService.getTimedFocus()
            : uk.ac.cf._5.group14.BehaviourChangeGroupProject.FocusData.TimedFocus.defaultFocus();
        model.addAttribute("timedFocus", timedFocus);

        String timeTheme = computeTimeThemeValue(timedFocus != null ? timedFocus.label() : null);
        model.addAttribute("timeTheme", timeTheme);

        String timeOfDayMoodClass = computeTimeOfDayMoodClass(timedFocus != null ? timedFocus.label() : null);
        model.addAttribute("timeOfDayMoodClass", timeOfDayMoodClass);

        var dailyFocusService = dailyFocusServiceProvider.getIfAvailable();
        if (dailyFocusService != null) {
            String persisted = dailyFocusService.getDailyFocus(user, date);
            if (persisted != null && !persisted.isBlank()) {
                dailyFocus = persisted;
            }
        }

        List<CalendarTask> tasks = taskService.getTasks(user, date);

        CalendarTaskOrderingPreference ordering = CalendarTaskOrderingPreference.CHRONOLOGICAL;
        var settings = userSettingsService.getOrCreate(user);
        if (settings != null && settings.getCalendarTaskOrdering() != null) {
            ordering = settings.getCalendarTaskOrdering();
        }

        final String finalOrdering = ordering.name();
        if ("ALPHABETICAL".equals(finalOrdering)) {
            tasks = tasks.stream()
                .sorted(
                    Comparator.comparing(
                            (CalendarTask t) -> t.getTitle() == null ? "" : t.getTitle().toLowerCase()
                        )
                        .thenComparing(CalendarTask::getTime, Comparator.nullsLast(Comparator.naturalOrder()))
                )
                .toList();
        } else {
            tasks = tasks.stream()
                .sorted(
                    Comparator.comparing(CalendarTask::getTime, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(t -> t.getTitle() == null ? "" : t.getTitle().toLowerCase())
                )
                .toList();
        }

        model.addAttribute("tasks", tasks);
        model.addAttribute("exerciseTasks", tasks.stream().filter(CalendarTask::getExercise).toList());
        model.addAttribute("otherTasks", tasks.stream().filter(t -> !t.getExercise()).toList());

        List<Long> taskIds = tasks.stream()
            .map(CalendarTask::getId)
            .filter(Objects::nonNull)
            .toList();
        model.addAttribute("taskWarningsByTaskId", taskWarningService.listWarningsForTasks(taskIds));
        model.addAttribute("taskGoalsById", goalLinkService.goalsByTaskIds(user, taskIds));

        model.addAttribute("occurrences",
                scheduleOccurrenceService.getOccurrencesForUserOnDate(user, date));

        // scheduled workouts for this weekday
        int dow = date.getDayOfWeek().getValue();
        var schedules = workoutScheduleService.findByUserAndDayOfWeek(user, dow);
        List<uk.ac.cf._5.group14.BehaviourChangeGroupProject.StrengthLog.WorkoutSession> sessions = new java.util.ArrayList<>();
        for (var s : schedules) {
            var ws = workoutSessionService.findByUserDateAndWorkout(user, date, s.getWorkout())
                .orElseGet(() -> workoutSessionService.createIfMissing(user, date, s.getWorkout()));
            sessions.add(ws);
        }

        CalendarWorkoutOrderingPreference workoutOrdering = CalendarWorkoutOrderingPreference.SCHEDULE_ORDER;
        if (settings != null && settings.getCalendarWorkoutOrdering() != null) {
            workoutOrdering = settings.getCalendarWorkoutOrdering();
        }
        if (workoutOrdering == CalendarWorkoutOrderingPreference.ALPHABETICAL) {
            sessions = sessions.stream()
                .sorted(
                    Comparator.comparing(
                            (uk.ac.cf._5.group14.BehaviourChangeGroupProject.StrengthLog.WorkoutSession s) -> {
                                if (s == null) return "";
                                if (s.getNameSnapshot() != null) return s.getNameSnapshot().toLowerCase();
                                if (s.getWorkout() != null && s.getWorkout().getName() != null) return s.getWorkout().getName().toLowerCase();
                                return "";
                            }
                        )
                )
                .toList();
        }
        model.addAttribute("workoutSessions", sessions);

        // Preferences-driven Daily Focus options.
        boolean hasAnyPreferences = true; // fail-open: keep existing UX if prefs subsystem not available
        List<String> preferenceFocusOptions = new java.util.ArrayList<>();
        var userPreferenceService = userPreferenceServiceProvider.getIfAvailable();
        if (userPreferenceService != null) {
            var trainingPreferences = userPreferenceService.getUserPreferences(user);
            if (trainingPreferences != null) {
                for (var p : trainingPreferences) {
                    if (p != null && p.getDescription() != null && !p.getDescription().isBlank()) {
                        preferenceFocusOptions.add(p.getDescription().trim());
                    }
                }
            }

            var physicalConditions = userPreferenceService.getUsersPhysicalConditions(user);
            if (physicalConditions != null) {
                for (var c : physicalConditions) {
                    if (c != null && c.getName() != null && !c.getName().isBlank()) {
                        preferenceFocusOptions.add(c.getName().trim());
                    }
                }
            }

            hasAnyPreferences = preferenceFocusOptions.stream().anyMatch(s -> s != null && !s.isBlank());
        }
        model.addAttribute("hasAnyPreferences", hasAnyPreferences);

        List<String> dailyFocusOptions = new java.util.ArrayList<>();
        if (timedFocus != null && timedFocus.label() != null && !timedFocus.label().isBlank()) {
            dailyFocusOptions.add(timedFocus.label().trim());
        }
        dailyFocusOptions.addAll(preferenceFocusOptions);
        if (tasks != null) {
            for (var task : tasks) {
                if (task != null && task.getTitle() != null && !task.getTitle().isBlank()) {
                    dailyFocusOptions.add(task.getTitle().trim());
                }
            }
        }
        if (sessions != null) {
            for (var session : sessions) {
                if (session == null) {
                    continue;
                }
                String workoutName = null;
                if (session.getNameSnapshot() != null && !session.getNameSnapshot().isBlank()) {
                    workoutName = session.getNameSnapshot();
                } else if (session.getWorkout() != null && session.getWorkout().getName() != null && !session.getWorkout().getName().isBlank()) {
                    workoutName = session.getWorkout().getName();
                }
                if (workoutName != null && !workoutName.isBlank()) {
                    dailyFocusOptions.add(workoutName.trim());
                }
            }
        }
        dailyFocusOptions.add("Custom focus");
        dailyFocusOptions = dailyFocusOptions.stream()
            .filter(s -> s != null && !s.isBlank())
            .distinct()
            .toList();
        model.addAttribute("dailyFocusOptions", dailyFocusOptions);

        Map<Long, String> exerciseStateById = new HashMap<>();
        Map<Long, List<uk.ac.cf._5.group14.BehaviourChangeGroupProject.StrengthLog.ExerciseSession>> orderedExercisesByWorkoutSessionId = new HashMap<>();
        for (var ws : sessions) {
            var ordered = ws.getExerciseSessions().stream()
                    .sorted(Comparator.comparingInt(uk.ac.cf._5.group14.BehaviourChangeGroupProject.StrengthLog.ExerciseSession::getOrderIndex))
                    .toList();
            orderedExercisesByWorkoutSessionId.put(ws.getId(), ordered);
            for (var es : ordered) {
                exerciseStateById.put(es.getId(), computeExerciseState(es));
            }
        }
        model.addAttribute("exerciseStateById", exerciseStateById);
        model.addAttribute("orderedExercisesByWorkoutSessionId", orderedExercisesByWorkoutSessionId);

        int totalTasks = tasks.size();
        int completedTasks = (int) tasks.stream().filter(CalendarTask::getCompleted).count();
        int totalWorkouts = sessions.size();
        int completedWorkouts = (int) sessions.stream().filter(uk.ac.cf._5.group14.BehaviourChangeGroupProject.StrengthLog.WorkoutSession::isCompleted).count();

        if ((dailyFocus == null || dailyFocus.isBlank()) && hasAnyPreferences) {
            var dailyFocusAiService = dailyFocusAiServiceProvider.getIfAvailable();
            if (dailyFocusAiService != null) {
                dailyFocus = dailyFocusAiService.suggestDailyFocus(date, timedFocus.label(), totalTasks, totalWorkouts);
            }
        }

        model.addAttribute("dailyFocus", dailyFocus);

        int remainingTasks = Math.max(0, totalTasks - completedTasks);
        int remainingWorkouts = Math.max(0, totalWorkouts - completedWorkouts);

        int completedItems = completedTasks + completedWorkouts;
        int totalItems = totalTasks + totalWorkouts;
        int completionPercentage = DailyCompletionCalculator.computeCompletionPercentage(completedTasks, totalTasks, completedWorkouts, totalWorkouts);

        DailyCompletionStatus dayCompletionStatus = DailyCompletionCalculator.computeStatus(
            date,
            completedItems,
            totalItems,
            LocalDate.now()
        );

        model.addAttribute("dayCompletionCompletedCount", completedItems);
        model.addAttribute("dayCompletionTotalCount", totalItems);
        model.addAttribute("dayCompletionPercentage", completionPercentage);
        model.addAttribute("dayCompletionSummary", completedItems + "/" + totalItems + " completed (" + completionPercentage + "%)");
        model.addAttribute("dayCompletionRemainingTasks", remainingTasks);
        model.addAttribute("dayCompletionRemainingWorkouts", remainingWorkouts);
        model.addAttribute("dayCompletionStatus", dayCompletionStatus);

        var dayHealthPersistenceService = dayHealthPersistenceServiceProvider.getIfAvailable();
        if (dayHealthPersistenceService != null) {
            var advice = dayHealthPersistenceService.getSavedAdvice(user, date);
            if (advice != null && advice.primaryMessage() != null && !advice.primaryMessage().isBlank()) {
                model.addAttribute("dayHealthPrimary", advice.primaryMessage());
                model.addAttribute("dayHealthSuggestions", advice.suggestions());
                model.addAttribute("dayHealthWatchOut", advice.watchOut());

                // Backwards-compatible model attribute (older templates/tests may rely on this)
                model.addAttribute("dayHealth", advice.primaryMessage());
            }
        }

        model.addAttribute("taskTemplateRecents", taskTemplateService.listRecents(user, 6));
        model.addAttribute("taskTemplateFavourites", taskTemplateService.listFavourites(user));
        model.addAttribute("taskTemplateAll", taskTemplateService.listAll(user));

        var dailyStreakService = dailyStreakServiceProvider.getIfAvailable();
        if (dailyStreakService != null) {
            LocalDate streakEnd = date;
            LocalDate streakStart = date.minusDays(13);
            model.addAttribute(
                "dailyStreakDays",
                dailyStreakService.calculateRange(user, streakStart, streakEnd, LocalDate.now())
            );
        }

        return "calendar/day";
    }

    private static String computeTimeOfDayMoodClass(String timedFocusLabel) {
        if (timedFocusLabel == null || timedFocusLabel.isBlank()) {
            return null;
        }

        return switch (timedFocusLabel.trim().toLowerCase()) {
            case "morning" -> "bg-gradient-to-b from-slate-50 to-slate-100 dark:from-slate-950 dark:to-slate-900";
            case "midday" -> "bg-gradient-to-b from-slate-50 to-slate-200 dark:from-slate-950 dark:to-slate-900";
            case "evening" -> "bg-gradient-to-b from-slate-100 to-slate-200 dark:from-slate-950 dark:to-slate-900";
            case "night" -> "bg-gradient-to-b from-slate-100 to-slate-300 dark:from-slate-950 dark:to-slate-900";
            default -> null;
        };
    }

    private static String computeTimeThemeValue(String timedFocusLabel) {
        if (timedFocusLabel == null || timedFocusLabel.isBlank()) {
            return "midday";
        }

        return switch (timedFocusLabel.trim().toLowerCase()) {
            case "morning" -> "morning";
            case "noon", "midday" -> "midday";
            case "evening" -> "evening";
            case "night" -> "night";
            default -> "midday";
        };
    }

    @PostMapping("/day/{dateStr}/reflection")
    public String submitReflection(
            @PathVariable String dateStr,
            @SessionAttribute(name = "user") User user,
            @RequestParam(name = "reflection", required = false) String reflection,
            @RequestParam(name = "notes", required = false) String notes,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes
    ) {
        LocalDate date = LocalDate.parse(dateStr, DATE_FORMAT);

        if (reflection == null || reflection.isBlank()) {
            return "redirect:/calendar/day/" + dateStr;
        }

        List<CalendarTask> tasks = taskService.getTasks(user, date);

        int dow = date.getDayOfWeek().getValue();
        var schedules = workoutScheduleService.findByUserAndDayOfWeek(user, dow);
        int totalWorkouts = schedules == null ? 0 : schedules.size();
        int completedWorkouts = 0;
        if (schedules != null) {
            for (var s : schedules) {
                var maybe = workoutSessionService.findByUserDateAndWorkout(user, date, s.getWorkout());
                if (maybe.isPresent() && maybe.get().isCompleted()) {
                    completedWorkouts++;
                }
            }
        }

        int totalTasks = tasks == null ? 0 : tasks.size();
        int completedTasks = (tasks == null) ? 0 : (int) tasks.stream().filter(CalendarTask::getCompleted).count();

        int completedItems = completedTasks + completedWorkouts;
        int totalItems = totalTasks + totalWorkouts;
        DailyCompletionStatus status = DailyCompletionCalculator.computeStatus(date, completedItems, totalItems, LocalDate.now());
        if (status != DailyCompletionStatus.GREEN) {
            return "redirect:/calendar/day/" + dateStr;
        }

        var reflectionService = reflectionServiceProvider.getIfAvailable();
        if (reflectionService == null) {
            return "redirect:/calendar/day/" + dateStr;
        }

        String dailyFocus = null;
        var dailyFocusService = dailyFocusServiceProvider.getIfAvailable();
        if (dailyFocusService != null) {
            dailyFocus = dailyFocusService.getDailyFocus(user, date);
        }

        ReflectionResult result = reflectionService.generate(
            user,
                date,
                dailyFocus,
                tasks,
                completedWorkouts,
                totalWorkouts,
                reflection,
                notes
        );

        redirectAttributes.addFlashAttribute("reflectionPerformanceSummary", result.performanceSummary());
        redirectAttributes.addFlashAttribute("reflectionImprovementSuggestions", result.improvementSuggestions());

        return "redirect:/calendar/day/" + dateStr;
    }

    @PostMapping("/day/{dateStr}/daily-focus")
    public Object updateDailyFocus(
            @PathVariable String dateStr,
            @RequestParam(name = "dailyFocus", required = false) String dailyFocus,
            @SessionAttribute(name = "user", required = false) User user,
            HttpServletRequest request
    ) {
        boolean isAjax = request != null && "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        if (dailyFocus == null || dailyFocus.isBlank()) {
            return isAjax ? ResponseEntity.noContent().build() : "redirect:/calendar/day/" + dateStr;
        }

        var dailyFocusService = dailyFocusServiceProvider.getIfAvailable();
        if (dailyFocusService != null && user != null) {
            LocalDate date = LocalDate.parse(dateStr, DATE_FORMAT);
            dailyFocusService.setDailyFocus(user, date, dailyFocus);
        }

        if (isAjax) {
            return ResponseEntity.noContent().build();
        }
        return "redirect:/calendar/day/" + dateStr + "?dailyFocus=" + java.net.URLEncoder.encode(dailyFocus, java.nio.charset.StandardCharsets.UTF_8);
    }

    @PostMapping("/day/{dateStr}/task-preferences")
    public Object updateTaskPreferences(
            @PathVariable String dateStr,
            @SessionAttribute(name = "user") User user,
            @RequestParam(name = "ordering", required = false) CalendarTaskOrderingPreference ordering,
            @RequestParam(name = "layout", required = false) CalendarTaskLayoutPreference layout,
            HttpServletRequest request
    ) {
        userSettingsService.updateCalendarPreferences(user, ordering, layout);
        boolean isAjax = request != null && "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        return isAjax ? ResponseEntity.noContent().build() : "redirect:/calendar/day/" + dateStr;
    }

    @PostMapping("/day/{dateStr}/workout-preferences")
    public Object updateWorkoutPreferences(
            @PathVariable String dateStr,
            @SessionAttribute(name = "user") User user,
            @RequestParam(name = "ordering", required = false) CalendarWorkoutOrderingPreference ordering,
            HttpServletRequest request
    ) {
        userSettingsService.updateWorkoutCalendarPreferences(user, ordering);
        boolean isAjax = request != null && "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        return isAjax ? ResponseEntity.noContent().build() : "redirect:/calendar/day/" + dateStr;
    }

    @PostMapping("/day/{dateStr}/day-health/generate")
    public String generateDayHealthOnce(
            @PathVariable String dateStr,
            @SessionAttribute(name = "user", required = false) User user
    ) {
        var dayHealthPersistenceService = dayHealthPersistenceServiceProvider.getIfAvailable();
        if (dayHealthPersistenceService != null && user != null) {
            LocalDate date = LocalDate.parse(dateStr, DATE_FORMAT);
            dayHealthPersistenceService.generateOnce(user, date);
        }

        return "redirect:/calendar/day/" + dateStr;
    }

    private String computeExerciseState(uk.ac.cf._5.group14.BehaviourChangeGroupProject.StrengthLog.ExerciseSession es) {
        if (es.getSetLogs() == null || es.getSetLogs().isEmpty()) return "NOT_STARTED";
        boolean all = es.getSetLogs().stream().allMatch(uk.ac.cf._5.group14.BehaviourChangeGroupProject.StrengthLog.SetLog::isCompleted);
        if (all) return "COMPLETED";
        boolean any = es.getSetLogs().stream().anyMatch(uk.ac.cf._5.group14.BehaviourChangeGroupProject.StrengthLog.SetLog::isCompleted);
        return any ? "IN_PROGRESS" : "NOT_STARTED";
    }

    @PostMapping("/day/{dateStr}/add-task")
    public String addTask(
            @PathVariable String dateStr,
            @SessionAttribute(name = "user") User user,
            @RequestParam String title,
            @RequestParam(required = false) String time,
            @RequestParam(required = false) String notes,
            @RequestParam(defaultValue = "false") boolean exercise,
            @RequestParam(defaultValue = "false") boolean completed
    ) {
        LocalDate date = LocalDate.parse(dateStr, CalendarTaskService.DATE_FORMAT);

        LocalTime parsedTime = (time == null || time.isBlank())
                ? LocalTime.NOON
                : LocalTime.parse(time);

        taskService.createTask(user, date, parsedTime, title, notes, exercise, completed);
        taskTemplateService.upsertFromTask(user, title, notes, exercise);

        return "redirect:/calendar/day/" + dateStr;
    }

    @PostMapping("/day/{dateStr}/add-task-ai")
    public String addTaskFromAi(
            @PathVariable String dateStr,
            @SessionAttribute(name = "user") User user,
            @RequestParam(name = "prompt") String prompt
    ) {
        LocalDate date = LocalDate.parse(dateStr, CalendarTaskService.DATE_FORMAT);

        TaskAiGenerationService.GeneratedTask generated = taskAiGenerationService.generateFromFreeText(prompt);

        LocalTime parsedTime = LocalTime.NOON;
        if (generated.time() != null && !generated.time().isBlank()) {
            try {
                parsedTime = LocalTime.parse(generated.time());
            } catch (Exception ignored) {
                parsedTime = LocalTime.NOON;
            }
        }

        taskService.createTask(user, date, parsedTime, generated.title(), generated.notes(), generated.exercise(), false);
        taskTemplateService.upsertFromTask(user, generated.title(), generated.notes(), generated.exercise());

        return "redirect:/calendar/day/" + dateStr;
    }

    @PostMapping("/day/{dateStr}/toggle-complete")
    public String toggleComplete(
            @PathVariable String dateStr,
            @SessionAttribute(name = "user") User user,
            @RequestParam Long taskId
    ) {
        taskService.toggleCompleted(taskId, user);

        return "redirect:/calendar/day/" + dateStr;
    }

    @PostMapping("/task/{id}/edit-inline")
    public String inlineUpdate(
            @PathVariable Long id,
            @SessionAttribute("user") User user,
            @RequestParam String title,
            @RequestParam(required = false) String time,
            @RequestParam(required = false) String notes,
            @RequestParam(defaultValue = "false") boolean exercise
    ) {
        CalendarTask task = taskService.getTaskById(id);
        if (task == null) return "redirect:/calendar";
        taskService.updateTask(id, user, title, time, notes, exercise);
        return "redirect:/calendar/day/" + task.getDate();
    }

    @PostMapping("/task/{id}/delete")
    public String deleteTask(
            @PathVariable Long id,
            @SessionAttribute("user") User user
    ) {
        CalendarTask task = taskService.getTaskById(id);
        if (task == null || task.getDate() == null) return "redirect:/calendar";

        taskService.deleteTask(id, user);
        return "redirect:/calendar/day/" + task.getDate();
    }

    @GetMapping("/task/{id}")
    public String taskDetail(
            @PathVariable Long id,
            @SessionAttribute("user") User user,
            Model model
    ) {
        CalendarTask task = taskService.getTaskById(id);
        if (task == null || task.getUser() == null || task.getUser().getId() == null) {
            return "redirect:/calendar";
        }
        if (user == null || user.getId() == null || !task.getUser().getId().equals(user.getId())) {
            return "redirect:/calendar";
        }

        model.addAttribute("task", task);
        model.addAttribute("date", task.getDate());

        List<CalendarTaskWarning> warnings = taskWarningService.listWarningsForTask(task.getId());
        model.addAttribute("taskWarnings", warnings);

        List<CalendarTask> sameDayTasks = new ArrayList<>(taskService.getTasks(user, task.getDate()));
        sameDayTasks.removeIf(t -> t == null || t.getId() == null || t.getId().equals(task.getId()));
        model.addAttribute("warningTriggerTasks", sameDayTasks);

        return "calendar/task-detail";
    }

    @PostMapping("/task/{id}/grace-period")
    public String updateGracePeriod(
            @PathVariable Long id,
            @SessionAttribute("user") User user,
            @RequestParam(required = false) Integer gracePeriodMinutes
    ) {
        CalendarTask task = taskService.getTaskById(id);
        if (task == null || task.getDate() == null) return "redirect:/calendar";

        taskService.updateGracePeriodMinutes(id, user, gracePeriodMinutes);
        return "redirect:/calendar/task/" + id;
    }

    @PostMapping("/task/{id}/warning-time")
    public String addTimeWarning(
            @PathVariable Long id,
            @SessionAttribute("user") User user,
            @RequestParam String triggerTime
    ) {
        CalendarTask task = taskService.getTaskById(id);
        if (task == null) return "redirect:/calendar";
        if (task.getUser() == null || task.getUser().getId() == null || user == null || user.getId() == null) {
            return "redirect:/calendar";
        }
        if (!task.getUser().getId().equals(user.getId())) {
            return "redirect:/calendar";
        }

        try {
            LocalTime t = LocalTime.parse(triggerTime);
            taskWarningService.addTimeWarning(task, t);
        } catch (Exception ignored) {
            // ignore invalid time input
        }

        return "redirect:/calendar/task/" + id;
    }

    @PostMapping("/task/{id}/warning-on-complete")
    public String addOnCompleteWarning(
            @PathVariable Long id,
            @SessionAttribute("user") User user,
            @RequestParam Long triggerTaskId
    ) {
        CalendarTask task = taskService.getTaskById(id);
        CalendarTask triggerTask = taskService.getTaskById(triggerTaskId);
        if (task == null || triggerTask == null) return "redirect:/calendar";

        if (task.getUser() == null || task.getUser().getId() == null || user == null || user.getId() == null) {
            return "redirect:/calendar";
        }
        if (!task.getUser().getId().equals(user.getId())) {
            return "redirect:/calendar";
        }

        taskWarningService.addOnTaskCompleteWarning(task, triggerTask);
        return "redirect:/calendar/task/" + id;
    }

    private Map<String, List<CalendarTask>> toIsoDateKeyedTaskMap(Map<LocalDate, List<CalendarTask>> source) {
        Map<String, List<CalendarTask>> byIsoDate = new HashMap<>();
        if (source == null || source.isEmpty()) {
            return byIsoDate;
        }

        for (Map.Entry<LocalDate, List<CalendarTask>> entry : source.entrySet()) {
            LocalDate date = entry.getKey();
            if (date == null) {
                continue;
            }
            byIsoDate.put(date.toString(), entry.getValue());
        }

        return byIsoDate;
    }

}
