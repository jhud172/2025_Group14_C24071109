package uk.ac.cf._5.group14.BehaviourChangeGroupProject.HealthDataInput.PhysicalCondition;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PhysicalConditionRepository extends JpaRepository<PhysicalCondition, Long> {

    @Override
    List<PhysicalCondition> findAllById(Iterable<Long> longs);
}
