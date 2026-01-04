package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Notes;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.util.List;
import java.util.Optional;

/**
 * Repository for persisting and retrieving note folders.
 */
public interface NoteFolderRepository extends JpaRepository<NoteFolder, Long> {
    List<NoteFolder> findByUserOrderByNameAsc(User user);
    Optional<NoteFolder> findByIdAndUser(Long id, User user);
}
