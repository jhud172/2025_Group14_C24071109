package uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleEntryService;

import java.time.LocalDate;
import java.util.*;

@Service
public class ScheduleOccurrenceServiceImpl implements ScheduleOccurrenceService {
    public ScheduleOccurrenceServiceImpl(
            ScheduleEntryService scheduleEntryService
    ) {
        this.scheduleEntryService = scheduleEntryService;
    }

    @Autowired
    private final ScheduleEntryService scheduleEntryService;

    @Autowired
    private ScheduleEntryRepository scheduleEntryRepository;

    @Autowired
    private ScheduleOccurrenceRepository scheduleOccurrenceRepository;

    @Override
    public List<ScheduleOccurrence> getActiveSchedulesForUser(User user) {
        return scheduleOccurrenceRepository.findActiveByUser(user);
    }

    @Override
    public void generateOccurrencesForSchedule(
            Schedule schedule,
            User user,
            LocalDate startDate,
            LocalDate endDate,
            int everyNWeeks
    ) {
        if (schedule == null || user == null || startDate == null || endDate == null) {
            return;
        }

        List<ScheduleEntry> entries = scheduleEntryService.getEntries(schedule.getId());
        if (entries.isEmpty()) {
            return;
        }

        LocalDate cursor = startDate;
        while (!cursor.isAfter(endDate)) {
            int dow = cursor.getDayOfWeek().getValue(); // 1-7

            for (ScheduleEntry entry : entries) {
                if (entry.getDayOfWeek() == dow) {
                    ScheduleOccurrence occ = new ScheduleOccurrence();
                    occ.setUser(user);
                    if (entry.getExercise() != null) {
                        occ.setExercise(entry.getExercise());
                    }
                    if (entry.getCustomExercise() != null) {
                        occ.setCustomExercise(entry.getCustomExercise());
                    }
                    if (occ.getExercise() == null && occ.getCustomExercise() == null) {
                        continue;
                    }
                    occ.setSchedule(schedule);
                    occ.setScheduleName(schedule.getName());
                    occ.setDate(cursor);
                    scheduleOccurrenceRepository.save(occ);
                }
            }

            cursor = cursor.plusDays(1);
        }
    }

    @Override
    public List<ScheduleOccurrence> getOccurrencesForUserOnDate(User user, LocalDate date) {
        return scheduleOccurrenceRepository.findByUserAndDate(user, date);
    }

    @Override
    public Map<LocalDate, List<ScheduleOccurrence>> getOccurrencesForUserInMonth(
            User user,
            int year,
            int month
    ) {
        LocalDate from = LocalDate.of(year, month, 1);
        LocalDate to = from.withDayOfMonth(from.lengthOfMonth());

        List<ScheduleOccurrence> all =
                scheduleOccurrenceRepository.findByUserAndDateBetween(user, from, to);

        Map<LocalDate, List<ScheduleOccurrence>> map = new HashMap<>();
        for (ScheduleOccurrence occ : all) {
            map.computeIfAbsent(occ.getDate(), d -> new ArrayList<>()).add(occ);
        }
        return map;
    }

    @Override
    public Map<LocalDate, List<ScheduleOccurrence>> getOccurrencesByRange(User user, LocalDate start, LocalDate end) {
        Map<LocalDate, List<ScheduleOccurrence>> map = new HashMap<>();

        List<ScheduleOccurrence> list =
                scheduleOccurrenceRepository.findByUserAndDateBetween(user, start, end);

        for (ScheduleOccurrence occ : list) {
            map.computeIfAbsent(occ.getDate(), d -> new ArrayList<>()).add(occ);
        }

        return map;
    }
}
