package uk.ac.cf._5.group14.One_To_One.Chat;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uk.ac.cf._5.group14.One_To_One.Users.User;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByUserOrderByCreatedAtAsc(User user, Pageable pageable);

    List<ChatMessage> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    void deleteByUser(User user);
}
