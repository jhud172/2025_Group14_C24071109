package uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData;

import org.springframework.stereotype.Service;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.LocalDate;

@Service
public class DailyCompletionService {

    private final DailyCompletionRepository dailyCompletionRepository;

    public DailyCompletionService(DailyCompletionRepository dailyCompletionRepository) {
        this.dailyCompletionRepository = dailyCompletionRepository;
    }

    public DailyCompletionStatus getCompletionStatus(User user, LocalDate date) {
        if (user == null || user.getId() == null || date == null) {
            return DailyCompletionStatus.GREY;
        }

        DailyCompletionKey key = new DailyCompletionKey(user.getId(), date);
        return dailyCompletionRepository.findById(key)
                .map(DailyCompletion::getCompletionStatus)
                .orElse(DailyCompletionStatus.GREY);
    }
}
