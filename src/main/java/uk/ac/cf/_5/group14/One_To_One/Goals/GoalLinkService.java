package uk.ac.cf._5.group14.One_To_One.Goals;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.cf._5.group14.One_To_One.CalendarData.CalendarTask;
import uk.ac.cf._5.group14.One_To_One.CalendarData.CalendarTaskRepository;
import uk.ac.cf._5.group14.One_To_One.ScheduleData.ScheduleOccurrence;
import uk.ac.cf._5.group14.One_To_One.ScheduleData.ScheduleOccurrenceRepository;
import uk.ac.cf._5.group14.One_To_One.Users.User;
import uk.ac.cf._5.group14.One_To_One.Workouts.WorkoutSession;
import uk.ac.cf._5.group14.One_To_One.Workouts.WorkoutSessionRepository;
import uk.ac.cf._5.group14.One_To_One.Workouts.WorkoutTemplate;
import uk.ac.cf._5.group14.One_To_One.Workouts.WorkoutTemplateRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class GoalLinkService {

    private final GoalRepository goalRepository;
    private final GoalLinkRepository goalLinkRepository;
    private final CalendarTaskRepository calendarTaskRepository;
    private final ScheduleOccurrenceRepository scheduleOccurrenceRepository;
    private final WorkoutSessionRepository workoutSessionRepository;
    private final WorkoutTemplateRepository workoutTemplateRepository;
    private final GoalService goalService;

    public GoalLinkService(GoalRepository goalRepository,
                           GoalLinkRepository goalLinkRepository,
                           CalendarTaskRepository calendarTaskRepository,
                           ScheduleOccurrenceRepository scheduleOccurrenceRepository,
                           WorkoutSessionRepository workoutSessionRepository,
                           WorkoutTemplateRepository workoutTemplateRepository,
                           GoalService goalService) {
        this.goalRepository = goalRepository;
        this.goalLinkRepository = goalLinkRepository;
        this.calendarTaskRepository = calendarTaskRepository;
        this.scheduleOccurrenceRepository = scheduleOccurrenceRepository;
        this.workoutSessionRepository = workoutSessionRepository;
        this.workoutTemplateRepository = workoutTemplateRepository;
        this.goalService = goalService;
    }

    public Map<Long, List<Goal>> goalsByTaskIds(User user, Collection<Long> taskIds) {
        if (taskIds == null || taskIds.isEmpty()) {
            return Map.of();
        }
        List<GoalLink> links = goalLinkRepository.findByCalendarTaskIdIn(taskIds);
        if (links.isEmpty()) {
            return Map.of();
        }
        List<Long> goalIds = links.stream().map(link -> link.getGoal().getId()).distinct().toList();
        Map<Long, Goal> goalsById = goalRepository.findAllById(goalIds).stream()
            .filter(goal -> user != null && goal.getOwnerUser() != null && Objects.equals(goal.getOwnerUser().getId(), user.getId()))
            .collect(Collectors.toMap(Goal::getId, g -> g));
        return links.stream()
            .filter(link -> link.getCalendarTaskId() != null)
            .filter(link -> goalsById.containsKey(link.getGoal().getId()))
            .collect(Collectors.groupingBy(
                GoalLink::getCalendarTaskId,
                Collectors.mapping(link -> goalsById.get(link.getGoal().getId()), Collectors.toList())
            ));
    }

    public Goal findGoalForWorkoutSession(User user, Long workoutSessionId) {
        if (workoutSessionId == null) {
            return null;
        }
        return goalLinkRepository.findFirstByWorkoutSessionId(workoutSessionId)
            .map(GoalLink::getGoal)
            .filter(goal -> user != null && goal.getOwnerUser() != null && Objects.equals(goal.getOwnerUser().getId(), user.getId()))
            .orElse(null);
    }

    @Transactional
    public GoalLink linkCalendarTask(User actor, Long goalId, Long taskId, GoalLinkSource source) {
        Goal goal = goalService.getGoalForViewer(actor, goalId);
        CalendarTask task = calendarTaskRepository.findById(taskId)
            .orElseThrow(() -> new IllegalArgumentException("Task not found"));
        enforceOwnership(goal, task.getUser() != null ? task.getUser().getId() : null);
        GoalLink link = new GoalLink();
        link.setGoal(goal);
        link.setLinkType(GoalLinkType.CALENDAR_TASK);
        link.setSource(source);
        link.setCalendarTaskId(taskId);
        return goalLinkRepository.save(link);
    }

    @Transactional
    public GoalLink linkScheduleOccurrence(User actor, Long goalId, Long occurrenceId, GoalLinkSource source) {
        Goal goal = goalService.getGoalForViewer(actor, goalId);
        ScheduleOccurrence occurrence = scheduleOccurrenceRepository.findById(occurrenceId)
            .orElseThrow(() -> new IllegalArgumentException("Occurrence not found"));
        enforceOwnership(goal, occurrence.getUser() != null ? occurrence.getUser().getId() : null);
        GoalLink link = new GoalLink();
        link.setGoal(goal);
        link.setLinkType(GoalLinkType.SCHEDULE_OCCURRENCE);
        link.setSource(source);
        link.setScheduleOccurrenceId(occurrenceId);
        return goalLinkRepository.save(link);
    }

    @Transactional
    public GoalLink linkWorkoutSession(User actor, Long goalId, Long sessionId, GoalLinkSource source) {
        Goal goal = goalService.getGoalForViewer(actor, goalId);
        WorkoutSession session = workoutSessionRepository.findById(sessionId)
            .orElseThrow(() -> new IllegalArgumentException("Workout session not found"));
        enforceOwnership(goal, session.getUser() != null ? session.getUser().getId() : null);
        GoalLink link = new GoalLink();
        link.setGoal(goal);
        link.setLinkType(GoalLinkType.WORKOUT_SESSION);
        link.setSource(source);
        link.setWorkoutSessionId(sessionId);
        return goalLinkRepository.save(link);
    }

    @Transactional
    public GoalLink replaceWorkoutSessionLink(User actor, Long goalId, Long sessionId, GoalLinkSource source) {
        if (sessionId == null) {
            throw new IllegalArgumentException("Workout session is required");
        }
        goalLinkRepository.deleteByWorkoutSessionId(sessionId);
        if (goalId == null) {
            return null;
        }
        return linkWorkoutSession(actor, goalId, sessionId, source);
    }

    @Transactional
    public GoalLink linkWorkoutTemplate(User actor, Long goalId, Long templateId, GoalLinkSource source) {
        Goal goal = goalService.getGoalForViewer(actor, goalId);
        WorkoutTemplate template = workoutTemplateRepository.findById(templateId)
            .orElseThrow(() -> new IllegalArgumentException("Workout template not found"));
        enforceOwnership(goal, template.getOwnerUser() != null ? template.getOwnerUser().getId() : null);
        GoalLink link = new GoalLink();
        link.setGoal(goal);
        link.setLinkType(GoalLinkType.WORKOUT_TEMPLATE);
        link.setSource(source);
        link.setWorkoutTemplateId(templateId);
        return goalLinkRepository.save(link);
    }

    public List<GoalLink> listGoalLinks(Long goalId) {
        if (goalId == null) {
            return List.of();
        }
        return goalLinkRepository.findByGoalId(goalId);
    }

    public GoalLinkedItems loadLinkedItems(Long goalId) {
        if (goalId == null) {
            return new GoalLinkedItems(List.of(), List.of(), List.of());
        }
        List<GoalLink> links = goalLinkRepository.findByGoalId(goalId);
        List<Long> taskIds = links.stream().map(GoalLink::getCalendarTaskId).filter(Objects::nonNull).toList();
        List<Long> occurrenceIds = links.stream().map(GoalLink::getScheduleOccurrenceId).filter(Objects::nonNull).toList();
        List<Long> sessionIds = links.stream().map(GoalLink::getWorkoutSessionId).filter(Objects::nonNull).toList();

        List<CalendarTask> tasks = new ArrayList<>();
        if (!taskIds.isEmpty()) {
            for (CalendarTask task : calendarTaskRepository.findAllById(taskIds)) {
                tasks.add(task);
            }
        }
        List<ScheduleOccurrence> occurrences = occurrenceIds.isEmpty() ? List.of() : scheduleOccurrenceRepository.findAllById(occurrenceIds);
        List<WorkoutSession> sessions = sessionIds.isEmpty() ? List.of() : new ArrayList<>(workoutSessionRepository.findAllById(sessionIds));

        return new GoalLinkedItems(tasks, occurrences, sessions);
    }

    private void enforceOwnership(Goal goal, Long ownerUserId) {
        if (goal == null || goal.getOwnerUser() == null || ownerUserId == null) {
            throw new AccessDeniedException("Access denied");
        }
        if (!Objects.equals(goal.getOwnerUser().getId(), ownerUserId)) {
            throw new AccessDeniedException("Cannot link items from another user");
        }
    }

    public static class GoalLinkedItems {
        private final List<CalendarTask> tasks;
        private final List<ScheduleOccurrence> occurrences;
        private final List<WorkoutSession> sessions;

        public GoalLinkedItems(List<CalendarTask> tasks,
                               List<ScheduleOccurrence> occurrences,
                               List<WorkoutSession> sessions) {
            this.tasks = tasks;
            this.occurrences = occurrences;
            this.sessions = sessions;
        }

        public List<CalendarTask> getTasks() {
            return tasks;
        }

        public List<ScheduleOccurrence> getOccurrences() {
            return occurrences;
        }

        public List<WorkoutSession> getSessions() {
            return sessions;
        }
    }
}
