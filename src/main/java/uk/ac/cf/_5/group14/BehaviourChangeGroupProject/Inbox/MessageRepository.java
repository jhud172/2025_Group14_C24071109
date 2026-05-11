package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Inbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByConversationIdOrderByCreatedAtAsc(Long conversationId);

    Optional<Message> findTop1ByConversationIdOrderByCreatedAtDesc(Long conversationId);

    long countByConversationIdAndSenderUserIdNot(Long conversationId, Long senderUserId);

    long countByConversationIdAndCreatedAtAfterAndSenderUserIdNot(Long conversationId, Instant createdAt, Long senderUserId);

    @Query("select substring(m.body, 1, 180) from Message m where m.conversation.id = :conversationId order by m.createdAt desc")
    List<String> findLastMessageSnippet(@Param("conversationId") Long conversationId);
}
