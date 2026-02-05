package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Checkins;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainerCheckInQuestionRepository extends JpaRepository<TrainerCheckInQuestion, Long> {

    List<TrainerCheckInQuestion> findByTemplateIdOrderByOrderIndexAsc(Long templateId);
}
