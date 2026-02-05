package uk.ac.cf._5.group14.BehaviourChangeGroupProject.ChatV2;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatFolderRepository extends JpaRepository<ChatFolder, Long> {
    List<ChatFolder> findByUserOrderBySortOrderAscNameAsc(User user);
    Optional<ChatFolder> findByIdAndUser(Long id, User user);
}
