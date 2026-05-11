package uk.ac.cf._5.group14.BehaviourChangeGroupProject.ChatV2;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("chatV2MessageRepository")
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByThreadOrderByCreatedAtAsc(ChatThread thread);
    long countByThread(ChatThread thread);
}
