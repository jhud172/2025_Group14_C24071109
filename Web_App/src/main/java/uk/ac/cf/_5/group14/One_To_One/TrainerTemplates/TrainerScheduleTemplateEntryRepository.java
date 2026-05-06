package uk.ac.cf._5.group14.One_To_One.TrainerTemplates;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainerScheduleTemplateEntryRepository extends JpaRepository<TrainerScheduleTemplateEntry, Long> {

    List<TrainerScheduleTemplateEntry> findByTemplateIdOrderByOrderIndexAsc(Long templateId);
}
