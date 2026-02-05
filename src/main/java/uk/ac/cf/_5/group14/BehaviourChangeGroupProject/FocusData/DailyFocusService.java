package uk.ac.cf._5.group14.BehaviourChangeGroupProject.FocusData;

import org.springframework.stereotype.Service;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.LocalDate;

@Service
public class DailyFocusService {

    private final DailyFocusRepository dailyFocusRepository;

    public DailyFocusService(DailyFocusRepository dailyFocusRepository) {
        this.dailyFocusRepository = dailyFocusRepository;
    }

    public String getDailyFocus(User user, LocalDate date) {
        if (user == null || user.getId() == null || date == null) {
            return null;
        }
        return dailyFocusRepository.findById(new DailyFocusKey(user.getId(), date))
                .map(DailyFocus::getDailyFocus)
                .orElse(null);
    }

    public void setDailyFocus(User user, LocalDate date, String dailyFocus) {
        if (user == null || user.getId() == null || date == null) {
            return;
        }
        if (dailyFocus == null || dailyFocus.isBlank()) {
            return;
        }

        DailyFocus row = dailyFocusRepository.findById(new DailyFocusKey(user.getId(), date))
                .orElseGet(DailyFocus::new);
        row.setUser(user);
        row.setDate(date);
        row.setDailyFocus(dailyFocus.trim());
        dailyFocusRepository.save(row);
    }
}
