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
                    settings.setShareRecoverySignals(false);
                    settings.setShareNutritionSignals(false);
                    settings.setShareSleepSignals(false);
                    settings.setShareFatigueSignals(false);
                    settings.setShareWeightTrend(false);
                    settings.setDefaultSets(3);
                    settings.setDefaultRepMin(8);
                    settings.setDefaultRepMax(12);
                    settings.setPreferredEquipmentBodyweight(false);
                    settings.setPreferredEquipmentDumbbell(false);
                    settings.setPreferredEquipmentBarbell(false);
                    settings.setPreferredEquipmentMachine(false);
                    settings.setPreferredEquipmentBands(false);
                    settings.setPreferredEquipmentKettlebell(false);
                    settings.setMacroTargetCalories(null);
                    settings.setMacroTargetProtein(null);
                    settings.setMacroTargetCarbs(null);
                    settings.setMacroTargetFat(null);
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
    public UserSettings updateCalendarViewPreference(User user, CalendarViewPreference calendarViewPreference) {
        UserSettings settings = getOrCreate(user);
        if (settings == null) {
            return null;
        }

        if (calendarViewPreference != null) {
            settings.setCalendarViewPreference(calendarViewPreference);
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

    @Override
    @Transactional
    public UserSettings updateTrainerSharing(User user,
                                             boolean shareRecoverySignals,
                                             boolean shareNutritionSignals,
                                             boolean shareSleepSignals,
                                             boolean shareFatigueSignals,
                                             boolean shareWeightTrend) {
        UserSettings settings = getOrCreate(user);
        if (settings == null) {
            return null;
        }

        settings.setShareRecoverySignals(shareRecoverySignals);
        settings.setShareNutritionSignals(shareNutritionSignals);
        settings.setShareSleepSignals(shareSleepSignals);
        settings.setShareFatigueSignals(shareFatigueSignals);
        settings.setShareWeightTrend(shareWeightTrend);

        return userSettingsRepository.save(settings);
    }

    @Override
    @Transactional
    public UserSettings updateSmartDefaults(User user,
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
                                            Integer macroTargetCalories,
                                            Integer macroTargetProtein,
                                            Integer macroTargetCarbs,
                                            Integer macroTargetFat) {
        UserSettings settings = getOrCreate(user);
        if (settings == null) {
            return null;
        }

        int sets = clamp(defaultSets, 1, 20, settings.getDefaultSets());
        int repMin = clamp(defaultRepMin, 1, 30, settings.getDefaultRepMin());
        int repMax = clamp(defaultRepMax, repMin, 50, Math.max(repMin, settings.getDefaultRepMax()));

        settings.setDefaultSets(sets);
        settings.setDefaultRepMin(repMin);
        settings.setDefaultRepMax(repMax);
        settings.setPreferredEquipmentBodyweight(preferredEquipmentBodyweight);
        settings.setPreferredEquipmentDumbbell(preferredEquipmentDumbbell);
        settings.setPreferredEquipmentBarbell(preferredEquipmentBarbell);
        settings.setPreferredEquipmentMachine(preferredEquipmentMachine);
        settings.setPreferredEquipmentBands(preferredEquipmentBands);
        settings.setPreferredEquipmentKettlebell(preferredEquipmentKettlebell);
        settings.setPreferredEquipmentCable(preferredEquipmentCable);
        settings.setPreferredEquipmentPullupBar(preferredEquipmentPullupBar);
        settings.setPreferredEquipmentJumpRope(preferredEquipmentJumpRope);
        settings.setMacroTargetCalories(normalizeOptional(macroTargetCalories, 0, 20000));
        settings.setMacroTargetProtein(normalizeOptional(macroTargetProtein, 0, 1000));
        settings.setMacroTargetCarbs(normalizeOptional(macroTargetCarbs, 0, 1000));
        settings.setMacroTargetFat(normalizeOptional(macroTargetFat, 0, 1000));

        return userSettingsRepository.save(settings);
    }

    @Override
    @Transactional
    public UserSettings resetSmartDefaults(User user) {
        UserSettings settings = getOrCreate(user);
        if (settings == null) {
            return null;
        }

        settings.setDefaultSets(3);
        settings.setDefaultRepMin(8);
        settings.setDefaultRepMax(12);
        settings.setPreferredEquipmentBodyweight(false);
        settings.setPreferredEquipmentDumbbell(false);
        settings.setPreferredEquipmentBarbell(false);
        settings.setPreferredEquipmentMachine(false);
        settings.setPreferredEquipmentBands(false);
        settings.setPreferredEquipmentKettlebell(false);
        settings.setPreferredEquipmentCable(false);
        settings.setPreferredEquipmentPullupBar(false);
        settings.setPreferredEquipmentJumpRope(false);
        settings.setMacroTargetCalories(null);
        settings.setMacroTargetProtein(null);
        settings.setMacroTargetCarbs(null);
        settings.setMacroTargetFat(null);

        return userSettingsRepository.save(settings);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public UserSettings updateHideAiOneShotWarning(User user, boolean hide) {
        UserSettings settings = getOrCreate(user);
        if (settings == null) {
            return null;
        }
        settings.setHideAiOneShotWarning(hide);
        return userSettingsRepository.save(settings);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public UserSettings updatePreferredWorkoutTemplate(User user, Long templateId) {
        UserSettings settings = getOrCreate(user);
        if (settings == null) {
            return null;
        }
        settings.setPreferredWorkoutTemplateId(templateId);
        return userSettingsRepository.save(settings);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public UserSettings updateStickerPreferences(User user, StickerPackPreference stickerPack, int monthlyWorkoutTarget) {
        UserSettings settings = getOrCreate(user);
        if (settings == null) {
            return null;
        }
        if (stickerPack != null) {
            settings.setStickerPack(stickerPack);
        }
        int target = Math.min(Math.max(monthlyWorkoutTarget, 1), 31);
        settings.setMonthlyWorkoutTarget(target);
        return userSettingsRepository.save(settings);
    }

    private int clamp(Integer value, int min, int max, int fallback) {
        if (value == null) {
            return fallback;
        }
        return Math.min(Math.max(value, min), max);
    }

    private Integer normalizeOptional(Integer value, int min, int max) {
        if (value == null) {
            return null;
        }
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }
}
