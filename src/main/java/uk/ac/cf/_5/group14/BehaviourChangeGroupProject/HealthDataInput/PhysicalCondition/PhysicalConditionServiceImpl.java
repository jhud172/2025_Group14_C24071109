package uk.ac.cf._5.group14.BehaviourChangeGroupProject.HealthDataInput.PhysicalCondition;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhysicalConditionServiceImpl implements PhysicalConditionService {
    private final PhysicalConditionRepository physicalConditionRepository;

    public PhysicalConditionServiceImpl(PhysicalConditionRepository physicalConditionRepository) {
        this.physicalConditionRepository = physicalConditionRepository;
    }

    public List<PhysicalCondition> getAllPhysicalConditions(){
        return physicalConditionRepository.findAll();
    }

    public List<PhysicalCondition> getPhysicalConditionsById(List<Long> ids){
        return physicalConditionRepository.findAllById(ids);
    }
}
