package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Vault;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class VaultNoteService {

    private final VaultNoteRepository vaultNoteRepository;

    public VaultNoteService(VaultNoteRepository vaultNoteRepository) {
        this.vaultNoteRepository = vaultNoteRepository;
    }

    public List<VaultNote> listForUser(Long userId, VaultNoteType type) {
        if (userId == null) return Collections.emptyList();
        if (type == null) {
            return vaultNoteRepository.findByUserIdOrderByUpdatedAtDesc(userId);
        }
        return vaultNoteRepository.findByUserIdAndNoteTypeOrderByUpdatedAtDesc(userId, type);
    }

    public Optional<VaultNote> getForUser(Long id, Long userId) {
        if (id == null || userId == null) return Optional.empty();
        return vaultNoteRepository.findByIdAndUserId(id, userId);
    }

    public VaultNote create(Long userId,
                            VaultNoteType type,
                            String title,
                            String content,
                            java.time.LocalDate linkedDate,
                            Long linkedWorkoutSessionId) {
        VaultNote note = new VaultNote(userId, type, title, content);
        note.setLinkedDate(linkedDate);
        note.setLinkedWorkoutSessionId(linkedWorkoutSessionId);
        return vaultNoteRepository.save(note);
    }

    public Optional<VaultNote> update(Long id,
                                     Long userId,
                                     VaultNoteType type,
                                     String title,
                                     String content,
                                     java.time.LocalDate linkedDate,
                                     Long linkedWorkoutSessionId) {
        Optional<VaultNote> existing = getForUser(id, userId);
        if (existing.isEmpty()) return Optional.empty();

        VaultNote note = existing.get();
        note.setNoteType(type);
        note.setTitle(title);
        note.setContent(content);
        note.setLinkedDate(linkedDate);
        note.setLinkedWorkoutSessionId(linkedWorkoutSessionId);
        return Optional.of(vaultNoteRepository.save(note));
    }

    public boolean delete(Long id, Long userId) {
        Optional<VaultNote> existing = getForUser(id, userId);
        if (existing.isEmpty()) return false;
        vaultNoteRepository.delete(existing.get());
        return true;
    }

    public List<VaultNote> getManyForUser(List<Long> ids, Long userId) {
        if (ids == null || ids.isEmpty() || userId == null) return Collections.emptyList();
        return vaultNoteRepository.findByIdInAndUserId(ids, userId);
    }
}
