package uk.ac.cf._5.group14.One_To_One.UserSettings;

import uk.ac.cf._5.group14.One_To_One.Users.User;

public interface UserSettingsService {
    UserSettings getOrCreate(User user);
    UserSettings update(User user, String language, ThemePreference theme, boolean easyMode);

    UserSettings updateWeatherPreferences(
            User user,
            WeatherTemperatureUnitPreference weatherTemperatureUnit,
            WeatherDisplayModePreference weatherDisplayMode
    );

    UserSettings updateTimeDisplayPreference(
            User user,
            TimeDisplayFormatPreference timeDisplayFormat
    );

    boolean isQuickPreferencesCompleted(User user);

    UserSettings updateQuickPreferencesCompleted(User user, boolean completed);

    UserSettings updateCalendarPreferences(
            User user,
            CalendarTaskOrderingPreference calendarTaskOrdering,
            CalendarTaskLayoutPreference calendarTaskLayout
    );

        UserSettings updateWorkoutCalendarPreferences(
            User user,
            CalendarWorkoutOrderingPreference calendarWorkoutOrdering
        );

    UserSettings updateCalendarViewPreference(
            User user,
            CalendarViewPreference calendarViewPreference
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
                boolean preferredEquipmentCable,
                boolean preferredEquipmentPullupBar,
                boolean preferredEquipmentJumpRope,
                boolean preferredEquipmentMedicineBall,
                boolean preferredEquipmentFoamRoller,
                boolean preferredEquipmentTrx,
                boolean preferredEquipmentOther,
                String preferredEquipmentOtherSpecify,
                Integer macroTargetCalories,
                Integer macroTargetProtein,
                Integer macroTargetCarbs,
                Integer macroTargetFat,
                java.util.Set<String> weeklySummaryMetrics
            );

            UserSettings resetSmartDefaults(User user);

    UserSettings updateHideAiOneShotWarning(User user, boolean hide);

    UserSettings updateStickerPreferences(User user, StickerPackPreference stickerPack, int monthlyWorkoutTarget);

    UserSettings updatePreferredWorkoutTemplate(User user, Long templateId);

    UserSettings updateProfileCustomizer(
            User user,
            String bannerTheme,
            String ringStyle,
            String cardBackStyle,
            String textColor,
            String generalTextColor,
            java.util.Set<String> milestoneKeys
    );
}
