package uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ScheduleEntryRepository extends JpaRepository<ScheduleEntry, Long> {

    List<ScheduleEntry> findByScheduleId(Long scheduleId);
    List<ScheduleEntry> findByScheduleIdOrderByOrderNumber(Long scheduleId);
    void deleteByScheduleId(Long scheduleId);
    List<ScheduleEntry> findBySchedule(Schedule schedule);
}
