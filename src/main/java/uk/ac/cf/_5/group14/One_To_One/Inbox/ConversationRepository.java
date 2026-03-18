package uk.ac.cf._5.group14.One_To_One.Inbox;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
}
