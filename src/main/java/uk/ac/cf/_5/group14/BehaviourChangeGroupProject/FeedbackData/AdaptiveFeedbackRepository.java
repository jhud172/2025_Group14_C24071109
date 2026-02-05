package uk.ac.cf._5.group14.BehaviourChangeGroupProject.FeedbackData;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AdaptiveFeedbackRepository extends JpaRepository<AdaptiveFeedback, AdaptiveFeedbackKey> {

    Optional<AdaptiveFeedback> findByUserIdAndDate(Long userId, LocalDate date);

    List<AdaptiveFeedback> findTop7ByUserIdOrderByDateDesc(Long userId);
}
