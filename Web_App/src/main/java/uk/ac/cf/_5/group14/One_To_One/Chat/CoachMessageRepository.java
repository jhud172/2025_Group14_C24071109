package uk.ac.cf._5.group14.One_To_One.Chat;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoachMessageRepository extends JpaRepository<CoachMessage, Long> {

    List<CoachMessage> findByConversationOrderByCreatedAtAsc(CoachConversation conversation, Pageable pageable);

    List<CoachMessage> findByConversationOrderByCreatedAtDesc(CoachConversation conversation, Pageable pageable);

    void deleteByConversation(CoachConversation conversation);
}
