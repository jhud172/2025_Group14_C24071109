package uk.ac.cf._5.group14.BehaviourChangeGroupProject.DayHealthData;

import org.springframework.stereotype.Service;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.LocalDate;
import java.util.List;

@Service
public class DayHealthPersistenceService {

    private final DayHealthRepository dayHealthRepository;
    private final DayHealthService dayHealthService;

    public DayHealthPersistenceService(DayHealthRepository dayHealthRepository, DayHealthService dayHealthService) {
        this.dayHealthRepository = dayHealthRepository;
        this.dayHealthService = dayHealthService;
    }

    public DayHealthService.DayHealthAdvice getSavedAdvice(User user, LocalDate date) {
        if (user == null || user.getId() == null || date == null) {
            return null;
        }

        return dayHealthRepository.findById(new DayHealthKey(user.getId(), date))
                .map(row -> new DayHealthService.DayHealthAdvice(
                        row.getPrimaryMessage(),
                        List.of(row.getSuggestionA(), row.getSuggestionB()),
                        row.getWatchOut()
                ))
                .orElse(null);
    }

    public DayHealthService.DayHealthAdvice generateOnce(User user, LocalDate date) {
        if (user == null || user.getId() == null || date == null) {
            return null;
        }

        DayHealthKey key = new DayHealthKey(user.getId(), date);
        var existing = dayHealthRepository.findById(key);
        if (existing.isPresent()) {
            DayHealth row = existing.get();
            return new DayHealthService.DayHealthAdvice(
                    row.getPrimaryMessage(),
                    List.of(row.getSuggestionA(), row.getSuggestionB()),
                    row.getWatchOut()
            );
        }

        DayHealthService.DayHealthAdvice advice = dayHealthService.getDayHealthAdvice(user, date);
        if (advice == null || advice.primaryMessage() == null || advice.primaryMessage().isBlank()) {
            return null;
        }

        List<String> suggestions = advice.suggestions();
        if (suggestions == null || suggestions.size() != 2) {
            return null;
        }

        DayHealth row = new DayHealth();
        row.setUser(user);
        row.setDate(date);
        row.setPrimaryMessage(advice.primaryMessage().trim());
        row.setSuggestionA(suggestions.get(0));
        row.setSuggestionB(suggestions.get(1));
        row.setWatchOut(advice.watchOut());

        dayHealthRepository.save(row);

        return advice;
    }
}
