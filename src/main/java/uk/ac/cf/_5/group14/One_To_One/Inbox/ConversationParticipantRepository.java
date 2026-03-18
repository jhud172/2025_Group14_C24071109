package uk.ac.cf._5.group14.One_To_One.Inbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.util.List;
import java.util.Optional;

public interface ConversationParticipantRepository extends JpaRepository<ConversationParticipant, Long> {

    List<ConversationParticipant> findByUser(User user);

    Optional<ConversationParticipant> findByConversationIdAndUserId(Long conversationId, Long userId);

    @Query("select cp.conversation.id from ConversationParticipant cp where cp.user.id in (:a, :b) group by cp.conversation.id having count(distinct cp.user.id) = 2 and count(cp.id) = 2")
    List<Long> findDirectConversationIdsBetween(@Param("a") Long userA, @Param("b") Long userB);

    @Query("select cp from ConversationParticipant cp where cp.conversation.id = :conversationId")
    List<ConversationParticipant> findAllByConversationId(@Param("conversationId") Long conversationId);
}
