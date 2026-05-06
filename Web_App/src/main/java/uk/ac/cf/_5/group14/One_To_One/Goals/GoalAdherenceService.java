package uk.ac.cf._5.group14.One_To_One.Goals;

import org.springframework.stereotype.Service;
import uk.ac.cf._5.group14.One_To_One.CalendarData.CalendarTask;
import uk.ac.cf._5.group14.One_To_One.CalendarData.CalendarTaskRepository;
import uk.ac.cf._5.group14.One_To_One.ScheduleData.ScheduleOccurrence;
import uk.ac.cf._5.group14.One_To_One.ScheduleData.ScheduleOccurrenceRepository;
import uk.ac.cf._5.group14.One_To_One.Workouts.WorkoutSession;
import uk.ac.cf._5.group14.One_To_One.Workouts.WorkoutSessionRepository;
import uk.ac.cf._5.group14.One_To_One.Workouts.WorkoutTemplate;
import uk.ac.cf._5.group14.One_To_One.Workouts.WorkoutTemplateRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Service
public class GoalAdherenceService {

    private final GoalRepository goalRepository;
    private final GoalLinkRepository goalLinkRepository;
    private final CalendarTaskRepository calendarTaskRepository;
    private final ScheduleOccurrenceRepository scheduleOccurrenceRepository;
    private final WorkoutSessionRepository workoutSessionRepository;
    private final WorkoutTemplateRepository workoutTemplateRepository;

    public GoalAdherenceService(GoalRepository goalRepository,
                                GoalLinkRepository goalLinkRepository,
                                CalendarTaskRepository calendarTaskRepository,
                                ScheduleOccurrenceRepository scheduleOccurrenceRepository,
                                WorkoutSessionRepository workoutSessionRepository,
                                WorkoutTemplateRepository workoutTemplateRepository) {
        this.goalRepository = goalRepository;
        this.goalLinkRepository = goalLinkRepository;
        this.calendarTaskRepository = calendarTaskRepository;
        this.scheduleOccurrenceRepository = scheduleOccurrenceRepository;
        this.workoutSessionRepository = workoutSessionRepository;
        this.workoutTemplateRepository = workoutTemplateRepository;
    }

    public GoalAdherenceWeek calculateWeek(Long goalId, LocalDate weekStart) {
        Goal goal = goalRepository.findById(goalId)
            .orElseThrow(() -> new IllegalArgumentException("Goal not found"));
        return calculateWeek(goal, weekStart);
    }

    public GoalAdherenceWeek calculateWeek(Goal goal, LocalDate weekStart) {
        LocalDate normalizedStart = normalizeWeekStart(weekStart);
        LocalDate weekEnd = normalizedStart.plusDays(6);

        List<GoalLink> links = goalLinkRepository.findByGoalId(goal.getId());
        int planned = 0;
        int completed = 0;

        for (GoalLink link : links) {
            if (link.getLinkType() == GoalLinkType.CALENDAR_TASK && link.getCalendarTaskId() != null) {
                CalendarTask task = calendarTaskRepository.findById(link.getCalendarTaskId()).orElse(null);
                if (task != null && isWithin(task.getDate(), normalizedStart, weekEnd)) {
                    planned++;
                    if (Boolean.TRUE.equals(task.getCompleted())) {
                        completed++;
                    }
                }
            } else if (link.getLinkType() == GoalLinkType.SCHEDULE_OCCURRENCE && link.getScheduleOccurrenceId() != null) {
                ScheduleOccurrence occurrence = scheduleOccurrenceRepository.findById(link.getScheduleOccurrenceId()).orElse(null);
                if (occurrence != null && isWithin(occurrence.getDate(), normalizedStart, weekEnd)) {
                    planned++;
                    if (occurrence.isCompleted()) {
                        completed++;
                    }
                }
            } else if (link.getLinkType() == GoalLinkType.WORKOUT_SESSION && link.getWorkoutSessionId() != null) {
                WorkoutSession session = workoutSessionRepository.findById(link.getWorkoutSessionId()).orElse(null);
                LocalDate date = session != null && session.getStartedAt() != null ? session.getStartedAt().toLocalDate() : null;
                if (session != null && isWithin(date, normalizedStart, weekEnd)) {
                    planned++;
                    if (session.isCompleted()) {
                        completed++;
                    }
                }
            } else if (link.getLinkType() == GoalLinkType.WORKOUT_TEMPLATE && link.getWorkoutTemplateId() != null) {
                WorkoutTemplate template = workoutTemplateRepository.findById(link.getWorkoutTemplateId()).orElse(null);
                if (template != null) {
                    LocalDateTime start = normalizedStart.atStartOfDay();
                    LocalDateTime end = weekEnd.plusDays(1).atStartOfDay().minusNanos(1);
                    List<WorkoutSession> sessions = workoutSessionRepository
                        .findByUserAndTemplateAndStartedAtBetween(template.getOwnerUser(), template, start, end);
                    for (WorkoutSession session : sessions) {
                        planned++;
                        if (session.isCompleted()) {
                            completed++;
                        }
                    }
                }
            }
        }

        int percent = planned == 0 ? 0 : (int) Math.round((completed * 100.0) / planned);
        boolean streak = planned > 0 && completed >= planned;
        return new GoalAdherenceWeek(normalizedStart, planned, completed, percent, streak);
    }

    public List<GoalAdherenceWeek> calculateRange(Long goalId, LocalDate from, LocalDate to) {
        Goal goal = goalRepository.findById(goalId)
            .orElseThrow(() -> new IllegalArgumentException("Goal not found"));
        LocalDate start = normalizeWeekStart(from);
        LocalDate end = normalizeWeekStart(to);
        if (end.isBefore(start)) {
            return List.of();
        }
        List<GoalAdherenceWeek> weeks = new ArrayList<>();
        LocalDate cursor = start;
        while (!cursor.isAfter(end)) {
            weeks.add(calculateWeek(goal, cursor));
            cursor = cursor.plusWeeks(1);
        }
        return weeks;
    }

    public static LocalDate normalizeWeekStart(LocalDate date) {
        if (date == null) {
            return LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        }
        return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    private boolean isWithin(LocalDate date, LocalDate start, LocalDate end) {
        if (date == null || start == null || end == null) {
            return false;
        }
        return !date.isBefore(start) && !date.isAfter(end);
    }
}
