package uk.ac.cf._5.group14.BehaviourChangeGroupProject.UserSettings;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserRepository;

@Service
public class UserSettingsServiceImpl implements UserSettingsService {

    private final UserSettingsRepository userSettingsRepository;
    private final UserRepository userRepository;

    public UserSettingsServiceImpl(UserSettingsRepository userSettingsRepository, UserRepository userRepository) {
        this.userSettingsRepository = userSettingsRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserSettings getOrCreate(User user) {
        if (user == null || user.getId() == null) {
            return null;
        }
        return userSettingsRepository.findById(user.getId())
                .orElseGet(() -> {
                    UserSettings settings = new UserSettings();
                    settings.setUser(userRepository.getReferenceById(user.getId()));
                    settings.setLanguage("en");
                    settings.setTheme(ThemePreference.SYSTEM);
                    settings.setEasyMode(false);
                    settings.setColorBlindMode(false);
                    settings.setDisabilityHearing(false);
                    settings.setDisabilityMobility(false);
                    settings.setDisabilityVision(false);
                    return userSettingsRepository.save(settings);
                });
    }

    @Override
    @Transactional
    public UserSettings update(User user, String language, ThemePreference theme, boolean easyMode) {
        UserSettings settings = getOrCreate(user);
        if (settings == null) {
            return null;
        }

        if (language != null && !language.isBlank()) {
            settings.setLanguage(language);
        }
        if (theme != null) {
            settings.setTheme(theme);
        }
        settings.setEasyMode(easyMode);

        return userSettingsRepository.save(settings);
    }

    @Override
    @Transactional
    public UserSettings updateCalendarPreferences(
            User user,
            CalendarTaskOrderingPreference calendarTaskOrdering,
            CalendarTaskLayoutPreference calendarTaskLayout
    ) {
        UserSettings settings = getOrCreate(user);
        if (settings == null) {
            return null;
        }

        if (calendarTaskOrdering != null) {
            settings.setCalendarTaskOrdering(calendarTaskOrdering);
        }
        if (calendarTaskLayout != null) {
            settings.setCalendarTaskLayout(calendarTaskLayout);
        }

        return userSettingsRepository.save(settings);
    }

    @Override
    @Transactional
    public UserSettings updateWorkoutCalendarPreferences(User user, CalendarWorkoutOrderingPreference calendarWorkoutOrdering) {
        UserSettings settings = getOrCreate(user);
        if (settings == null) {
            return null;
        }

        if (calendarWorkoutOrdering != null) {
            settings.setCalendarWorkoutOrdering(calendarWorkoutOrdering);
        }

        return userSettingsRepository.save(settings);
    }

    @Override
    @Transactional
    public UserSettings updateAccessibility(User user,
                                            boolean colorBlindMode,
                                            boolean disabilityHearing,
                                            boolean disabilityMobility,
                                            boolean disabilityVision) {
        UserSettings settings = getOrCreate(user);
        if (settings == null) {
            return null;
        }

        settings.setColorBlindMode(colorBlindMode);
        settings.setDisabilityHearing(disabilityHearing);
        settings.setDisabilityMobility(disabilityMobility);
        settings.setDisabilityVision(disabilityVision);

        return userSettingsRepository.save(settings);
    }
}
