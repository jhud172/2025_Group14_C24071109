package uk.ac.cf._5.group14.One_To_One.TrainerTemplates;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.cf._5.group14.One_To_One.CalendarData.CalendarTask;
import uk.ac.cf._5.group14.One_To_One.CalendarData.CalendarTaskRepository;
import uk.ac.cf._5.group14.One_To_One.ScheduleData.ScheduleOccurrence;
import uk.ac.cf._5.group14.One_To_One.ScheduleData.ScheduleOccurrenceRepository;
import uk.ac.cf._5.group14.One_To_One.Security.AccessGuard;
import uk.ac.cf._5.group14.One_To_One.Users.Role;
import uk.ac.cf._5.group14.One_To_One.Users.User;
import uk.ac.cf._5.group14.One_To_One.Users.UserRepository;
import uk.ac.cf._5.group14.One_To_One.Vault.VaultNote;
import uk.ac.cf._5.group14.One_To_One.Vault.VaultNoteRepository;
import uk.ac.cf._5.group14.One_To_One.Vault.VaultNoteType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class TrainerScheduleTemplateServiceImpl implements TrainerScheduleTemplateService {

    private final TrainerScheduleTemplateRepository templateRepository;
    private final TrainerScheduleTemplateEntryRepository entryRepository;
    private final CalendarTaskRepository calendarTaskRepository;
    private final ScheduleOccurrenceRepository scheduleOccurrenceRepository;
    private final VaultNoteRepository vaultNoteRepository;
    private final UserRepository userRepository;
    private final AccessGuard accessGuard;

    public TrainerScheduleTemplateServiceImpl(TrainerScheduleTemplateRepository templateRepository,
                                              TrainerScheduleTemplateEntryRepository entryRepository,
                                              CalendarTaskRepository calendarTaskRepository,
                                              ScheduleOccurrenceRepository scheduleOccurrenceRepository,
                                              VaultNoteRepository vaultNoteRepository,
                                              UserRepository userRepository,
                                              AccessGuard accessGuard) {
        this.templateRepository = templateRepository;
        this.entryRepository = entryRepository;
        this.calendarTaskRepository = calendarTaskRepository;
        this.scheduleOccurrenceRepository = scheduleOccurrenceRepository;
        this.vaultNoteRepository = vaultNoteRepository;
        this.userRepository = userRepository;
        this.accessGuard = accessGuard;
    }

    @Override
    public TrainerScheduleTemplate createTemplate(User trainer, String name, String description, String tags) {
        requireTrainer(trainer);
        if (name == null || name.trim().isBlank()) {
            throw new IllegalArgumentException("Template name required");
        }
        TrainerScheduleTemplate template = new TrainerScheduleTemplate();
        template.setTrainerId(trainer.getId());
        template.setName(trimOrNull(name));
        template.setDescription(trimOrNull(description));
        template.setTags(trimOrNull(tags));
        template.setArchived(false);
        template.setVersion(1);
        return templateRepository.save(template);
    }

    @Override
    public TrainerScheduleTemplate updateTemplate(User trainer, Long templateId, String name, String description, String tags, boolean archived) {
        TrainerScheduleTemplate template = getForTrainer(trainer, templateId);
        if (name == null || name.trim().isBlank()) {
            throw new IllegalArgumentException("Template name required");
        }
        template.setName(trimOrNull(name));
        template.setDescription(trimOrNull(description));
        template.setTags(trimOrNull(tags));
        template.setArchived(archived);
        return templateRepository.save(template);
    }

    @Override
    public TrainerScheduleTemplateEntry addEntry(User trainer, Long templateId, TrainerScheduleTemplateEntry entry) {
        TrainerScheduleTemplate template = getForTrainer(trainer, templateId);
        validateEntry(entry);
        entry.setTemplate(template);
        entry.setOrderIndex(nextOrderIndex(templateId));
        return entryRepository.save(entry);
    }

    @Override
    public TrainerScheduleTemplateEntry updateEntry(User trainer, Long templateId, Long entryId, TrainerScheduleTemplateEntry entry) {
        TrainerScheduleTemplate template = getForTrainer(trainer, templateId);
        TrainerScheduleTemplateEntry existing = entryRepository.findById(entryId)
                .orElseThrow(() -> new IllegalArgumentException("Entry not found"));
        if (!existing.getTemplate().getId().equals(template.getId())) {
            throw new AccessDeniedException("Entry not owned by template");
        }
        validateEntry(entry);
        existing.setDayOfWeek(entry.getDayOfWeek());
        existing.setTimeWindowStart(entry.getTimeWindowStart());
        existing.setTimeWindowEnd(entry.getTimeWindowEnd());
        existing.setType(entry.getType());
        existing.setTitle(entry.getTitle());
        existing.setDefaultsJson(entry.getDefaultsJson());
        existing.setIntensityLabel(entry.getIntensityLabel());
        existing.setIntensityLevel(entry.getIntensityLevel());
        existing.setExercise(entry.getExercise());
        existing.setCustomExercise(entry.getCustomExercise());
        return entryRepository.save(existing);
    }

    @Override
    public void deleteEntry(User trainer, Long templateId, Long entryId) {
        TrainerScheduleTemplate template = getForTrainer(trainer, templateId);
        TrainerScheduleTemplateEntry existing = entryRepository.findById(entryId)
                .orElseThrow(() -> new IllegalArgumentException("Entry not found"));
        if (!existing.getTemplate().getId().equals(template.getId())) {
            throw new AccessDeniedException("Entry not owned by template");
        }
        entryRepository.delete(existing);
    }

    @Override
    public TrainerScheduleTemplate cloneTemplate(User trainer, Long templateId) {
        TrainerScheduleTemplate source = getForTrainer(trainer, templateId);
        TrainerScheduleTemplate clone = new TrainerScheduleTemplate();
        clone.setTrainerId(source.getTrainerId());
        clone.setName(source.getName() + " (Copy)");
        clone.setDescription(source.getDescription());
        clone.setTags(source.getTags());
        clone.setArchived(false);
        clone.setVersion(source.getVersion() + 1);
        clone = templateRepository.save(clone);

        List<TrainerScheduleTemplateEntry> entries = entryRepository.findByTemplateIdOrderByOrderIndexAsc(source.getId());
        int order = 1;
        for (TrainerScheduleTemplateEntry entry : entries) {
            TrainerScheduleTemplateEntry copy = new TrainerScheduleTemplateEntry();
            copy.setTemplate(clone);
            copy.setDayOfWeek(entry.getDayOfWeek());
            copy.setTimeWindowStart(entry.getTimeWindowStart());
            copy.setTimeWindowEnd(entry.getTimeWindowEnd());
            copy.setType(entry.getType());
            copy.setTitle(entry.getTitle());
            copy.setDefaultsJson(entry.getDefaultsJson());
            copy.setIntensityLabel(entry.getIntensityLabel());
            copy.setIntensityLevel(entry.getIntensityLevel());
            copy.setExercise(entry.getExercise());
            copy.setCustomExercise(entry.getCustomExercise());
            copy.setOrderIndex(order++);
            entryRepository.save(copy);
        }

        return clone;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainerScheduleTemplatePreviewItem> previewApply(User trainer,
                                                                 Long templateId,
                                                                 Long clientId,
                                                                 LocalDate startDate,
                                                                 LocalDate endDate,
                                                                 boolean idempotent) {
        TrainerScheduleTemplate template = getForTrainer(trainer, templateId);
        validateDateRange(startDate, endDate);
        User client = loadClientForTrainer(trainer, clientId);
        List<TrainerScheduleTemplateEntry> entries = entryRepository.findByTemplateIdOrderByOrderIndexAsc(template.getId());
        List<TrainerScheduleTemplatePreviewItem> preview = new ArrayList<>();

        LocalDate cursor = startDate;
        while (!cursor.isAfter(endDate)) {
            int dow = cursor.getDayOfWeek().getValue();
            for (TrainerScheduleTemplateEntry entry : entries) {
                if (entry.getDayOfWeek() != dow) {
                    continue;
                }
                boolean duplicate = idempotent && isDuplicate(client, cursor, entry);
                preview.add(new TrainerScheduleTemplatePreviewItem(
                        cursor,
                        entry.getType(),
                        entry.getTitle(),
                        entry.getTimeWindowStart(),
                        entry.getTimeWindowEnd(),
                        duplicate
                ));
            }
            cursor = cursor.plusDays(1);
        }

        return preview;
    }

    @Override
    public int applyTemplate(User trainer,
                             Long templateId,
                             Long clientId,
                             LocalDate startDate,
                             LocalDate endDate,
                             boolean idempotent) {
        TrainerScheduleTemplate template = getForTrainer(trainer, templateId);
        validateDateRange(startDate, endDate);
        User client = loadClientForTrainer(trainer, clientId);
        List<TrainerScheduleTemplateEntry> entries = entryRepository.findByTemplateIdOrderByOrderIndexAsc(template.getId());
        int created = 0;

        LocalDate cursor = startDate;
        while (!cursor.isAfter(endDate)) {
            int dow = cursor.getDayOfWeek().getValue();
            for (TrainerScheduleTemplateEntry entry : entries) {
                if (entry.getDayOfWeek() != dow) {
                    continue;
                }
                if (idempotent && isDuplicate(client, cursor, entry)) {
                    continue;
                }
                boolean saved = createFromEntry(template, entry, client, cursor);
                if (saved) {
                    created++;
                }
            }
            cursor = cursor.plusDays(1);
        }

        return created;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainerScheduleTemplate> listForTrainer(User trainer) {
        requireTrainer(trainer);
        return templateRepository.findByTrainerIdOrderByUpdatedAtDesc(trainer.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public TrainerScheduleTemplate getForTrainer(User trainer, Long templateId) {
        requireTrainer(trainer);
        return templateRepository.findByIdAndTrainerId(templateId, trainer.getId())
                .orElseThrow(() -> new AccessDeniedException("Template not found"));
    }

    private void requireTrainer(User trainer) {
        if (trainer == null || trainer.getRole() != Role.TRAINER) {
            throw new AccessDeniedException("Trainer role required");
        }
        if (!trainer.isTrainerVerified() || !trainer.isEnabled()) {
            throw new AccessDeniedException("TRAINER_NOT_VERIFIED");
        }
    }

    private User loadClientForTrainer(User trainer, Long clientId) {
        requireTrainer(trainer);
        accessGuard.requireTrainerAccessClient(trainer.getId(), clientId);
        return userRepository.findById(clientId).orElseThrow(() -> new IllegalArgumentException("Client not found"));
    }

    private void validateEntry(TrainerScheduleTemplateEntry entry) {
        if (entry == null) {
            throw new IllegalArgumentException("Entry is required");
        }
        if (entry.getTitle() == null || entry.getTitle().isBlank()) {
            throw new IllegalArgumentException("Entry title required");
        }
        if (entry.getDayOfWeek() < 1 || entry.getDayOfWeek() > 7) {
            throw new IllegalArgumentException("Day of week invalid");
        }
        if (entry.getType() == null) {
            throw new IllegalArgumentException("Entry type required");
        }
        if (entry.getType() == TrainerScheduleTemplateEntryType.WORKOUT
                && entry.getExercise() == null
                && entry.getCustomExercise() == null) {
            throw new IllegalArgumentException("Workout entry requires an exercise");
        }
    }

    private int nextOrderIndex(Long templateId) {
        return entryRepository.findByTemplateIdOrderByOrderIndexAsc(templateId).size() + 1;
    }

    private boolean isDuplicate(User client, LocalDate date, TrainerScheduleTemplateEntry entry) {
        if (entry.getType() == TrainerScheduleTemplateEntryType.WORKOUT) {
            return scheduleOccurrenceRepository.existsByUserAndDateAndTrainerTemplateEntryId(client, date, entry.getId());
        }
        if (entry.getType() == TrainerScheduleTemplateEntryType.NOTE) {
            return vaultNoteRepository.existsByUserIdAndLinkedDateAndTrainerTemplateEntryId(client.getId(), date, entry.getId());
        }
        return calendarTaskRepository.existsByUserAndDateAndTrainerTemplateEntryId(client, date, entry.getId());
    }

    private boolean createFromEntry(TrainerScheduleTemplate template,
                                    TrainerScheduleTemplateEntry entry,
                                    User client,
                                    LocalDate date) {
        switch (entry.getType()) {
            case WORKOUT -> {
                ScheduleOccurrence occurrence = new ScheduleOccurrence();
                occurrence.setUser(client);
                occurrence.setExercise(entry.getExercise());
                occurrence.setCustomExercise(entry.getCustomExercise());
                occurrence.setScheduleName(template.getName());
                occurrence.setDate(date);
                occurrence.setCompleted(false);
                occurrence.setTrainerTemplateId(template.getId());
                occurrence.setTrainerTemplateEntryId(entry.getId());
                scheduleOccurrenceRepository.save(occurrence);
                return true;
            }
            case NOTE -> {
                VaultNote note = new VaultNote();
                note.setUserId(client.getId());
                note.setNoteType(VaultNoteType.REFLECTION);
                note.setTitle(entry.getTitle());
                note.setContent(defaultNoteBody(entry));
                note.setLinkedDate(date);
                note.setTrainerTemplateId(template.getId());
                note.setTrainerTemplateEntryId(entry.getId());
                vaultNoteRepository.save(note);
                return true;
            }
            case TASK -> {
                CalendarTask task = new CalendarTask();
                task.setUser(client);
                task.setDate(date);
                task.setTime(entry.getTimeWindowStart());
                task.setTitle(entry.getTitle());
                task.setNotes(defaultNoteBody(entry));
                task.setExercise(false);
                task.setCompleted(false);
                task.setTrainerTemplateId(template.getId());
                task.setTrainerTemplateEntryId(entry.getId());
                calendarTaskRepository.save(task);
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    private String defaultNoteBody(TrainerScheduleTemplateEntry entry) {
        if (entry.getDefaultsJson() == null) {
            return null;
        }
        String trimmed = entry.getDefaultsJson().trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String trimOrNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isBlank() ? null : trimmed;
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start and end dates required");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
    }
}
