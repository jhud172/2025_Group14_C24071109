package uk.ac.cf._5.group14.One_To_One.ScheduleData;

import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ScheduleOccurrenceService {

    void generateOccurrencesForSchedule(
            Schedule schedule,
            User user,
            LocalDate startDate,
            LocalDate endDate,
            int everyNWeeks
    );

    List<ScheduleOccurrence> getOccurrencesForUserOnDate(User user, LocalDate date);

    Map<LocalDate, List<ScheduleOccurrence>> getOccurrencesForUserInMonth(
            User user,
            int year,
            int month
    );

    List<ScheduleOccurrence> getActiveSchedulesForUser(User user);

    Map<LocalDate, List<ScheduleOccurrence>> getOccurrencesByRange(User user, LocalDate start, LocalDate end);
}
