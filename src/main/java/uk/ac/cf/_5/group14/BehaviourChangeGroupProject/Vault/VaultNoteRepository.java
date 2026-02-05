package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Vault;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VaultNoteRepository extends JpaRepository<VaultNote, Long> {

    List<VaultNote> findByUserIdOrderByUpdatedAtDesc(Long userId);

    List<VaultNote> findByUserIdAndNoteTypeOrderByUpdatedAtDesc(Long userId, VaultNoteType noteType);

    Optional<VaultNote> findByIdAndUserId(Long id, Long userId);

    List<VaultNote> findByIdInAndUserId(List<Long> ids, Long userId);

    boolean existsByUserIdAndLinkedDateAndTrainerTemplateEntryId(Long userId, java.time.LocalDate linkedDate, Long trainerTemplateEntryId);
}
