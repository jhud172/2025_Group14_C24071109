package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Goals;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoalCheckInRepository extends JpaRepository<GoalCheckIn, Long> {
    List<GoalCheckIn> findByGoalIdOrderByWeekStartDateDesc(Long goalId);
}
