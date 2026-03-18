package uk.ac.cf._5.group14.One_To_One.ScheduleData;

import java.util.List;

public interface ScheduleEntryService {
    void save(ScheduleEntry entry);
    List<ScheduleEntry> getEntries(Long scheduleId);
    List<ScheduleEntry> getEntriesBySchedule(Schedule schedule);
}
