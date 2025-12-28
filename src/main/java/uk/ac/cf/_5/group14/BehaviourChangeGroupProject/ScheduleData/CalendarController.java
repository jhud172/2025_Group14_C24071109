package uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTask;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTaskService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/calendar")
public class CalendarController {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private CalendarTaskService taskService;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private ScheduleOccurrenceService scheduleOccurrenceService;

    @GetMapping("")
    public String calendarView(
            @RequestParam(required = false, defaultValue = "month") String view,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer week,
            @RequestParam(required = false) Integer weekYear,
            @SessionAttribute("user") User user,
            Model model
    ) {
        LocalDate today = LocalDate.now();
        if ("week".equals(view)) {
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
            model.addAttribute("occurrences", occ);
            model.addAttribute("today", today);
            model.addAttribute("view", "week");
            model.addAttribute("schedules", scheduleService.findByUser(user));
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
        model.addAttribute("prevMonth", prev.getMonthValue());
        model.addAttribute("prevYear", prev.getYear());
        model.addAttribute("nextMonth", next.getMonthValue());
        model.addAttribute("nextYear", next.getYear());
        model.addAttribute("tasksByDate", taskService.getTasksGroupedByDate(user));
        model.addAttribute("occurrences", scheduleOccurrenceService.getOccurrencesForUserInMonth(user, year, month));
        model.addAttribute("schedules", scheduleService.findByUser(user));
        model.addAttribute("view", "month");
        return "calendar/month";
    }

    @GetMapping("/day/{dateStr}")
    public String dayView(
            @PathVariable String dateStr,
            @SessionAttribute("user") User user,
            Model model
    ) {
        LocalDate date = LocalDate.parse(dateStr, DATE_FORMAT);

        model.addAttribute("date", date);
        model.addAttribute("tasks", taskService.getTasks(user, date));
        model.addAttribute("occurrences",
                scheduleOccurrenceService.getOccurrencesForUserOnDate(user, date));

        return "calendar/day";
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

}
