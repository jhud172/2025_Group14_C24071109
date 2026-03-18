package uk.ac.cf._5.group14.One_To_One.Vault;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface VaultNoteRepository extends JpaRepository<VaultNote, Long> {

    List<VaultNote> findByUserIdOrderByPinnedDescUpdatedAtDesc(Long userId);

    List<VaultNote> findByUserIdAndNoteTypeOrderByPinnedDescUpdatedAtDesc(Long userId, VaultNoteType noteType);

    List<VaultNote> findByUserIdAndPinnedTrueOrderByUpdatedAtDesc(Long userId);

    Optional<VaultNote> findByIdAndUserId(Long id, Long userId);

    List<VaultNote> findByIdInAndUserId(List<Long> ids, Long userId);

    boolean existsByUserIdAndLinkedDateAndTrainerTemplateEntryId(Long userId, LocalDate linkedDate, Long trainerTemplateEntryId);

    long countByUserId(Long userId);

    @Query("SELECT COUNT(n) FROM VaultNote n WHERE n.userId = :userId AND n.createdAt >= :from")
    long countByUserIdAndCreatedAtAfter(@Param("userId") Long userId, @Param("from") java.time.Instant from);

    @Query("SELECT n FROM VaultNote n WHERE n.userId = :userId " +
           "AND (:search IS NULL OR LOWER(n.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(n.content) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:noteType IS NULL OR n.noteType = :noteType) " +
           "AND (:pinnedOnly = false OR n.pinned = true) " +
           "AND (:fromDate IS NULL OR n.linkedDate >= :fromDate) " +
           "AND (:toDate IS NULL OR n.linkedDate <= :toDate) " +
           "ORDER BY n.pinned DESC, n.updatedAt DESC")
    List<VaultNote> search(@Param("userId") Long userId,
                           @Param("search") String search,
                           @Param("noteType") VaultNoteType noteType,
                           @Param("pinnedOnly") boolean pinnedOnly,
                           @Param("fromDate") LocalDate fromDate,
                           @Param("toDate") LocalDate toDate);

    // Legacy methods for backward compatibility
    default List<VaultNote> findByUserIdOrderByUpdatedAtDesc(Long userId) {
        return findByUserIdOrderByPinnedDescUpdatedAtDesc(userId);
    }

    default List<VaultNote> findByUserIdAndNoteTypeOrderByUpdatedAtDesc(Long userId, VaultNoteType noteType) {
        return findByUserIdAndNoteTypeOrderByPinnedDescUpdatedAtDesc(userId, noteType);
    }
}
