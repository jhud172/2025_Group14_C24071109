package uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ScheduleEntryServiceImpl implements ScheduleEntryService {

    private final ScheduleEntryRepository repo;

    public ScheduleEntryServiceImpl(ScheduleEntryRepository repo) {
        this.repo = repo;
    }
    @Override
    public void save(ScheduleEntry entry) {
        repo.save(entry);
    }
    @Override
    public List<ScheduleEntry> getEntries(Long scheduleId) {
        return repo.findByScheduleIdOrderByOrderNumber(scheduleId);
    }
    @Override
    public List<ScheduleEntry> getEntriesBySchedule(Schedule schedule) {
        if (schedule == null) return List.of();
        return repo.findBySchedule(schedule);
    }
}