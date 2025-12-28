package uk.ac.cf._5.group14.BehaviourChangeGroupProject.HealthDataInput.PhysicalCondition;

import java.util.List;

public interface PhysicalConditionService {
    List<PhysicalCondition> getAllPhysicalConditions();
    List<PhysicalCondition> getPhysicalConditionsById(List<Long> ids);
}
