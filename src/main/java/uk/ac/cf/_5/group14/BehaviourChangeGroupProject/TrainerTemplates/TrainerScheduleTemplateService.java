package uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerTemplates;

import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.LocalDate;
import java.util.List;

public interface TrainerScheduleTemplateService {

    TrainerScheduleTemplate createTemplate(User trainer, String name, String description, String tags);

    TrainerScheduleTemplate updateTemplate(User trainer, Long templateId, String name, String description, String tags, boolean archived);

    TrainerScheduleTemplateEntry addEntry(User trainer, Long templateId, TrainerScheduleTemplateEntry entry);

    TrainerScheduleTemplateEntry updateEntry(User trainer, Long templateId, Long entryId, TrainerScheduleTemplateEntry entry);

    void deleteEntry(User trainer, Long templateId, Long entryId);

    TrainerScheduleTemplate cloneTemplate(User trainer, Long templateId);

    List<TrainerScheduleTemplatePreviewItem> previewApply(User trainer,
                                                         Long templateId,
                                                         Long clientId,
                                                         LocalDate startDate,
                                                         LocalDate endDate,
                                                         boolean idempotent);

    int applyTemplate(User trainer,
                      Long templateId,
                      Long clientId,
                      LocalDate startDate,
                      LocalDate endDate,
                      boolean idempotent);

    List<TrainerScheduleTemplate> listForTrainer(User trainer);

    TrainerScheduleTemplate getForTrainer(User trainer, Long templateId);
}
