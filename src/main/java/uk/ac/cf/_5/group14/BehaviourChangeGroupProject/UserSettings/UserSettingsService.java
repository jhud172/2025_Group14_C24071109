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

        UserSettings updateTrainerSharing(
            User user,
            boolean shareRecoverySignals,
            boolean shareNutritionSignals,
            boolean shareSleepSignals,
            boolean shareFatigueSignals,
            boolean shareWeightTrend
        );

            UserSettings updateSmartDefaults(
                User user,
                Integer defaultSets,
                Integer defaultRepMin,
                Integer defaultRepMax,
                boolean preferredEquipmentBodyweight,
                boolean preferredEquipmentDumbbell,
                boolean preferredEquipmentBarbell,
                boolean preferredEquipmentMachine,
                boolean preferredEquipmentBands,
                boolean preferredEquipmentKettlebell,
                Integer macroTargetCalories,
                Integer macroTargetProtein,
                Integer macroTargetCarbs,
                Integer macroTargetFat
            );

            UserSettings resetSmartDefaults(User user);
}
