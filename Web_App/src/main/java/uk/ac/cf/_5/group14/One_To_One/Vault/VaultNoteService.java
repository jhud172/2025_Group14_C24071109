package uk.ac.cf._5.group14.One_To_One.Vault;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
            return vaultNoteRepository.findByUserIdOrderByPinnedDescUpdatedAtDesc(userId);
        }
        return vaultNoteRepository.findByUserIdAndNoteTypeOrderByPinnedDescUpdatedAtDesc(userId, type);
    }

    public List<VaultNote> search(Long userId, String query, VaultNoteType type,
                                   boolean pinnedOnly, LocalDate fromDate, LocalDate toDate) {
        if (userId == null) return Collections.emptyList();
        String searchTerm = (query == null || query.isBlank()) ? null : query.trim();
        return vaultNoteRepository.search(userId, searchTerm, type, pinnedOnly, fromDate, toDate);
    }

    public Optional<VaultNote> getForUser(Long id, Long userId) {
        if (id == null || userId == null) return Optional.empty();
        return vaultNoteRepository.findByIdAndUserId(id, userId);
    }

    public VaultNote create(Long userId,
                             VaultNoteType type,
                             String title,
                             String content,
                             LocalDate linkedDate,
                             Long linkedWorkoutSessionId,
                             String tags,
                             String mood) {
        VaultNote note = new VaultNote(userId, type, title, content);
        note.setLinkedDate(linkedDate);
        note.setLinkedWorkoutSessionId(linkedWorkoutSessionId);
        note.setTags(tags != null ? tags : "");
        note.setMood(mood);
        return vaultNoteRepository.save(note);
    }

    public VaultNote create(Long userId,
                             VaultNoteType type,
                             String title,
                             String content,
                             LocalDate linkedDate,
                             Long linkedWorkoutSessionId) {
        return create(userId, type, title, content, linkedDate, linkedWorkoutSessionId, "", null);
    }

    public Optional<VaultNote> update(Long id,
                                      Long userId,
                                      VaultNoteType type,
                                      String title,
                                      String content,
                                      LocalDate linkedDate,
                                      Long linkedWorkoutSessionId,
                                      String tags,
                                      String mood) {
        Optional<VaultNote> existing = getForUser(id, userId);
        if (existing.isEmpty()) return Optional.empty();

        VaultNote note = existing.get();
        note.setNoteType(type);
        note.setTitle(title);
        note.setContent(content);
        note.setLinkedDate(linkedDate);
        note.setLinkedWorkoutSessionId(linkedWorkoutSessionId);
        note.setTags(tags != null ? tags : "");
        note.setMood(mood);
        return Optional.of(vaultNoteRepository.save(note));
    }

    public Optional<VaultNote> update(Long id,
                                      Long userId,
                                      VaultNoteType type,
                                      String title,
                                      String content,
                                      LocalDate linkedDate,
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

    public Optional<VaultNote> togglePin(Long id, Long userId) {
        Optional<VaultNote> existing = getForUser(id, userId);
        if (existing.isEmpty()) return Optional.empty();
        VaultNote note = existing.get();
        note.setPinned(!note.isPinned());
        return Optional.of(vaultNoteRepository.save(note));
    }

    public Optional<VaultNote> saveAiSummary(Long id, Long userId, String summary) {
        Optional<VaultNote> existing = getForUser(id, userId);
        if (existing.isEmpty()) return Optional.empty();
        VaultNote note = existing.get();
        note.setAiSummary(summary);
        return Optional.of(vaultNoteRepository.save(note));
    }

    public List<VaultNote> getManyForUser(List<Long> ids, Long userId) {
        if (ids == null || ids.isEmpty() || userId == null) return Collections.emptyList();
        return vaultNoteRepository.findByIdInAndUserId(ids, userId);
    }

    public Map<String, Object> getMetrics(Long userId) {
        Map<String, Object> metrics = new HashMap<>();
        if (userId == null) return metrics;

        long total = vaultNoteRepository.countByUserId(userId);
        metrics.put("totalNotes", total);

        LocalDate now = LocalDate.now();
        LocalDate firstOfMonth = now.with(TemporalAdjusters.firstDayOfMonth());
        Instant firstOfMonthInstant = firstOfMonth.atStartOfDay(java.time.ZoneOffset.UTC).toInstant();
        long thisMonth = vaultNoteRepository.countByUserIdAndCreatedAtAfter(userId, firstOfMonthInstant);
        metrics.put("notesThisMonth", thisMonth);

        List<VaultNote> pinned = vaultNoteRepository.findByUserIdAndPinnedTrueOrderByUpdatedAtDesc(userId);
        metrics.put("pinnedCount", pinned.size());

        // Most used tag
        List<VaultNote> allNotes = vaultNoteRepository.findByUserIdOrderByPinnedDescUpdatedAtDesc(userId);
        Map<String, Integer> tagCounts = new HashMap<>();
        for (VaultNote note : allNotes) {
            String tags = note.getTags();
            if (tags != null && !tags.isBlank()) {
                for (String tag : tags.split(",")) {
                    String t = tag.trim().toLowerCase();
                    if (!t.isEmpty()) {
                        tagCounts.merge(t, 1, Integer::sum);
                    }
                }
            }
        }
        String topTag = tagCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
        metrics.put("topTag", topTag);

        return metrics;
    }
}
