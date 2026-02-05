package uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerTemplates;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrainerScheduleTemplateRepository extends JpaRepository<TrainerScheduleTemplate, Long> {

    List<TrainerScheduleTemplate> findByTrainerIdOrderByUpdatedAtDesc(Long trainerId);

    Optional<TrainerScheduleTemplate> findByIdAndTrainerId(Long id, Long trainerId);
}
