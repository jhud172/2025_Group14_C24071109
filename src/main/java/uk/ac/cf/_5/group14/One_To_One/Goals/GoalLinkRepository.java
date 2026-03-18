package uk.ac.cf._5.group14.One_To_One.Goals;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface GoalLinkRepository extends JpaRepository<GoalLink, Long> {
    List<GoalLink> findByGoalId(Long goalId);
    List<GoalLink> findByGoalIdAndLinkType(Long goalId, GoalLinkType linkType);
    List<GoalLink> findByCalendarTaskIdIn(Collection<Long> taskIds);
    List<GoalLink> findByScheduleOccurrenceIdIn(Collection<Long> occurrenceIds);
    Optional<GoalLink> findFirstByWorkoutSessionId(Long workoutSessionId);
    void deleteByWorkoutSessionId(Long workoutSessionId);
}
