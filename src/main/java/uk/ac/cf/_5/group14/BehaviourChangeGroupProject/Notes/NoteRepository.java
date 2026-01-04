package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Notes;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.util.List;
import java.util.Optional;

/**
 * Repository for persisting and retrieving notes.
 */
public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByUserOrderByUpdatedAtDesc(User user);
    List<Note> findByFolderOrderByUpdatedAtDesc(NoteFolder folder);
    Optional<Note> findByIdAndUser(Long id, User user);
}
