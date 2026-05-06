package uk.ac.cf._5.group14.One_To_One.Goals;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.cf._5.group14.One_To_One.Security.AccessGuard;
import uk.ac.cf._5.group14.One_To_One.Users.Role;
import uk.ac.cf._5.group14.One_To_One.Users.User;
import uk.ac.cf._5.group14.One_To_One.Users.UserRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final AccessGuard accessGuard;

    public GoalService(GoalRepository goalRepository,
                       UserRepository userRepository,
                       AccessGuard accessGuard) {
        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
        this.accessGuard = accessGuard;
    }

    public Goal getGoalForViewer(User viewer, Long goalId) {
        Goal goal = goalRepository.findById(goalId)
            .orElseThrow(() -> new IllegalArgumentException("Goal not found"));
        if (viewer == null || viewer.getId() == null) {
            throw new AccessDeniedException("Not authenticated");
        }
        if (Objects.equals(goal.getOwnerUser().getId(), viewer.getId())) {
            return goal;
        }
        if (viewer.getRole() == Role.TRAINER) {
            accessGuard.requireTrainerAccessClient(viewer.getId(), goal.getOwnerUser().getId());
            return goal;
        }
        throw new AccessDeniedException("Access denied");
    }

    public List<Goal> listGoalsForViewer(User viewer, Long ownerUserId, GoalStatus status, GoalType type, Boolean archived) {
        User owner = resolveOwner(viewer, ownerUserId);
        List<Goal> goals = (archived == null)
            ? goalRepository.findByOwnerUserOrderByUpdatedAtDesc(owner)
            : goalRepository.findByOwnerUserAndArchivedOrderByUpdatedAtDesc(owner, archived);

        Stream<Goal> stream = goals.stream();
        if (status != null) {
            stream = stream.filter(goal -> goal.getStatus() == status);
        }
        if (type != null) {
            stream = stream.filter(goal -> goal.getGoalType() == type);
        }
        return stream
            .sorted(Comparator.comparing(Goal::getUpdatedAt).reversed())
            .toList();
    }

    @Transactional
    public Goal createGoal(User actor, Long ownerUserId, GoalForm form) {
        if (actor == null || actor.getId() == null) {
            throw new AccessDeniedException("Not authenticated");
        }
        User owner = resolveOwner(actor, ownerUserId);

        Goal goal = new Goal();
        goal.setOwnerUser(owner);
        goal.setCreatedByUser(actor);
        if (actor.getRole() == Role.TRAINER && !Objects.equals(actor.getId(), owner.getId())) {
            goal.setTrainerUser(actor);
        }
        applyForm(goal, form, true);
        return goalRepository.save(goal);
    }

    @Transactional
    public Goal updateGoal(User actor, Long goalId, GoalForm form) {
        Goal goal = getGoalForViewer(actor, goalId);
        boolean canEditTargets = canEditTargetMetrics(actor, goal);
        applyForm(goal, form, canEditTargets);
        return goalRepository.save(goal);
    }

    public boolean canEditTargetMetrics(User actor, Goal goal) {
        if (actor == null || goal == null) {
            return false;
        }
        if (actor.getRole() == Role.TRAINER) {
            return true;
        }
        return goal.getTrainerUser() == null;
    }

    private User resolveOwner(User actor, Long ownerUserId) {
        if (ownerUserId == null || Objects.equals(actor.getId(), ownerUserId)) {
            return actor;
        }
        if (actor.getRole() != Role.TRAINER) {
            throw new AccessDeniedException("Access denied");
        }
        accessGuard.requireTrainerAccessClient(actor.getId(), ownerUserId);
        return userRepository.findById(ownerUserId)
            .orElseThrow(() -> new IllegalArgumentException("Owner not found"));
    }

    private void applyForm(Goal goal, GoalForm form, boolean canEditTargets) {
        if (form == null) {
            return;
        }
        if (form.getTitle() != null) {
            goal.setTitle(form.getTitle());
        }
        goal.setDescription(form.getDescription());
        if (form.getGoalType() != null) {
            goal.setGoalType(form.getGoalType());
        }
        if (form.getTimeframe() != null) {
            goal.setTimeframe(form.getTimeframe());
        }
        if (canEditTargets) {
            goal.setTargetMetricName(form.getTargetMetricName());
            goal.setTargetMetricValue(form.getTargetMetricValue());
            goal.setTargetMetricUnit(form.getTargetMetricUnit());
        }
        goal.setStartDate(form.getStartDate());
        goal.setTargetDate(form.getTargetDate());
        if (form.getStatus() != null) {
            goal.setStatus(form.getStatus());
        }
        if (form.getPriority() != null) {
            goal.setPriority(form.getPriority());
        }
        if (form.getArchived() != null) {
            goal.setArchived(form.getArchived());
        }
        if (goal.getGoalType() == null) {
            goal.setGoalType(GoalType.CUSTOM);
        }
        if (goal.getTimeframe() == null) {
            goal.setTimeframe(GoalTimeframe.TARGET);
        }
        if (goal.getStatus() == null) {
            goal.setStatus(GoalStatus.DRAFT);
        }
    }
}
