package uk.ac.cf._5.group14.BehaviourChangeGroupProject.UserSettings;

import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

public interface UserSettingsService {
    UserSettings getOrCreate(User user);
    UserSettings update(User user, String language, ThemePreference theme, boolean easyMode);

    UserSettings updateCalendarPreferences(
            User user,
            CalendarTaskOrderingPreference calendarTaskOrdering,
            CalendarTaskLayoutPreference calendarTaskLayout
    );

        UserSettings updateWorkoutCalendarPreferences(
            User user,
            CalendarWorkoutOrderingPreference calendarWorkoutOrdering
        );

    UserSettings updateAccessibility(
            User user,
            boolean colorBlindMode,
            boolean disabilityHearing,
            boolean disabilityMobility,
            boolean disabilityVision
    );
}
