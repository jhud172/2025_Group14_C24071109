package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Messaging;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MessageThreadRepository extends JpaRepository<MessageThread, Long> {

    Optional<MessageThread> findByLinkId(Long linkId);

    List<MessageThread> findByTrainerIdAndStatusOrderByCreatedAtDesc(Long trainerId, MessageThreadStatus status);

    List<MessageThread> findByClientIdAndStatusOrderByCreatedAtDesc(Long clientId, MessageThreadStatus status);
}
