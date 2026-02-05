package uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerClient;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CoachingPhaseChangeRepository extends JpaRepository<CoachingPhaseChange, Long> {

    List<CoachingPhaseChange> findByLinkIdOrderByChangedAtDesc(Long linkId);
}
