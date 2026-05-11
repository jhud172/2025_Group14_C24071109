package uk.ac.cf._5.group14.BehaviourChangeGroupProject.HealthConditions;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface UserHealthConditionRepository extends JpaRepository<UserHealthCondition, Long> {
    List<UserHealthCondition> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<UserHealthCondition> findByUserIdAndConditionTypeOrderByCreatedAtDesc(Long userId, HealthConditionType conditionType);
    List<UserHealthCondition> findByConditionTypeAndStatusAndEndDateBefore(HealthConditionType conditionType, HealthConditionStatus status, LocalDate date);
}
