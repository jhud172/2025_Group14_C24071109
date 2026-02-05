package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CoachConversationRepository extends JpaRepository<CoachConversation, Long> {

    List<CoachConversation> findByUserIdOrderByUpdatedAtDesc(Long userId);

    Optional<CoachConversation> findFirstByUserIdOrderByUpdatedAtDesc(Long userId);

    Optional<CoachConversation> findByIdAndUserId(Long id, Long userId);
}
