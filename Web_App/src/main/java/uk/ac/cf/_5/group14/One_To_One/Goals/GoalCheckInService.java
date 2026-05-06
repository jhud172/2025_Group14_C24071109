package uk.ac.cf._5.group14.One_To_One.Goals;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.cf._5.group14.One_To_One.Users.Role;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.time.LocalDate;
import java.util.List;

@Service
public class GoalCheckInService {

    private final GoalCheckInRepository goalCheckInRepository;
    private final GoalService goalService;

    public GoalCheckInService(GoalCheckInRepository goalCheckInRepository,
                              GoalService goalService) {
        this.goalCheckInRepository = goalCheckInRepository;
        this.goalService = goalService;
    }

    public List<GoalCheckIn> listForGoal(User viewer, Long goalId) {
        goalService.getGoalForViewer(viewer, goalId);
        return goalCheckInRepository.findByGoalIdOrderByWeekStartDateDesc(goalId);
    }

    @Transactional
    public GoalCheckIn createCheckIn(User actor, Long goalId, GoalCheckInForm form) {
        Goal goal = goalService.getGoalForViewer(actor, goalId);
        if (actor == null || actor.getRole() == null) {
            throw new AccessDeniedException("Not authenticated");
        }

        GoalCheckIn checkIn = new GoalCheckIn();
        checkIn.setGoal(goal);
        checkIn.setCreatedByUser(actor);
        checkIn.setCreatedByRole(actor.getRole());
        LocalDate weekStart = form.getWeekStartDate();
        if (weekStart == null) {
            weekStart = GoalAdherenceService.normalizeWeekStart(LocalDate.now());
        }
        checkIn.setWeekStartDate(weekStart);
        checkIn.setReflection(form.getReflection());
        checkIn.setConfidenceRating(form.getConfidenceRating());
        if (actor.getRole() == Role.TRAINER) {
            checkIn.setTrainerComment(form.getTrainerComment());
        }
        return goalCheckInRepository.save(checkIn);
    }
}
