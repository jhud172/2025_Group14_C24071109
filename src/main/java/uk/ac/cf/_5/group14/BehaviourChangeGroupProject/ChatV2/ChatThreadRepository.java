package uk.ac.cf._5.group14.BehaviourChangeGroupProject.ChatV2;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatThreadRepository extends JpaRepository<ChatThread, Long> {
    List<ChatThread> findByUserAndArchivedFalseOrderByPinnedDescUpdatedAtDesc(User user);
    List<ChatThread> findByUserAndFolderAndArchivedFalseOrderByPinnedDescUpdatedAtDesc(User user, ChatFolder folder);
    Optional<ChatThread> findByIdAndUser(Long id, User user);
    long countByUserAndArchivedFalse(User user);
}
