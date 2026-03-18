package uk.ac.cf._5.group14.One_To_One.CalendarData;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface CalendarTaskWarningRepository extends JpaRepository<CalendarTaskWarning, Long> {

    List<CalendarTaskWarning> findByTaskIdOrderByCreatedAtAsc(Long taskId);

    List<CalendarTaskWarning> findByTaskIdIn(Collection<Long> taskIds);

    List<CalendarTaskWarning> findByTriggerTaskIdAndTriggeredAtIsNull(Long triggerTaskId);

    void deleteByTaskId(Long taskId);

    void deleteByTriggerTaskId(Long triggerTaskId);
}
