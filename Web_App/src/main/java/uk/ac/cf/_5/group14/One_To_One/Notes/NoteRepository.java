package uk.ac.cf._5.group14.One_To_One.Notes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.util.List;
import java.util.Optional;

/**
 * Repository for persisting and retrieving notes.
 */
public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByUserOrderByUpdatedAtDesc(User user);
    List<Note> findByFolderOrderByUpdatedAtDesc(NoteFolder folder);
    Optional<Note> findByIdAndUser(Long id, User user);

        @Query("""
                select n from Note n
                where n.user = :user
                    and (:folder is null or n.folder = :folder)
                    and (
                        :q is null or :q = '' or
                        lower(n.title) like lower(concat('%', :q, '%')) or
                        lower(n.content) like lower(concat('%', :q, '%'))
                    )
                order by n.updatedAt desc
                """)
        List<Note> searchNotes(@Param("user") User user,
                                                     @Param("folder") NoteFolder folder,
                                                     @Param("q") String query);
}
