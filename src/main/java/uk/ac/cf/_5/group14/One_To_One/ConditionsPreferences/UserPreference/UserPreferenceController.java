package uk.ac.cf._5.group14.One_To_One.ConditionsPreferences.UserPreference;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import uk.ac.cf._5.group14.One_To_One.ConditionsPreferences.Preference.Preference;
import uk.ac.cf._5.group14.One_To_One.ConditionsPreferences.Preference.PreferenceService;
import uk.ac.cf._5.group14.One_To_One.HealthDataInput.PhysicalCondition.PhysicalCondition;
import uk.ac.cf._5.group14.One_To_One.HealthDataInput.PhysicalCondition.PhysicalConditionService;
import uk.ac.cf._5.group14.One_To_One.UserSettings.ThemePreference;
import uk.ac.cf._5.group14.One_To_One.UserSettings.TimeDisplayFormatPreference;
import uk.ac.cf._5.group14.One_To_One.UserSettings.WeatherDisplayModePreference;
import uk.ac.cf._5.group14.One_To_One.UserSettings.WeatherTemperatureUnitPreference;
import uk.ac.cf._5.group14.One_To_One.UserSettings.UserSettings;
import uk.ac.cf._5.group14.One_To_One.UserSettings.UserSettingsService;
import uk.ac.cf._5.group14.One_To_One.Users.AuthHelper;
import uk.ac.cf._5.group14.One_To_One.Users.User;

@Controller
public class UserPreferenceController {
    private static final Map<String, QuickPreset> QUICK_PRESETS = createQuickPresets();

    private final UserPreferenceService userPreferenceService;
    private final PreferenceService preferenceService;
    private final PhysicalConditionService physicalConditionService;
    private final UserSettingsService userSettingsService;
    private final LocaleResolver localeResolver;

    @Autowired
    private AuthHelper authHelper;

    public UserPreferenceController(UserPreferenceService userPreferenceService,
                                    PreferenceService preferenceService,
                                    PhysicalConditionService physicalConditionService,
                                    UserSettingsService userSettingsService,
                                    LocaleResolver localeResolver) {
        this.userPreferenceService = userPreferenceService;
        this.preferenceService = preferenceService;
        this.physicalConditionService = physicalConditionService;
        this.userSettingsService = userSettingsService;
        this.localeResolver = localeResolver;
    }

    @GetMapping("/select-preferences")
    public ModelAndView getPreferenceForm() {
        User user = authHelper.getAuthenticatedUser();
        if (!userSettingsService.isQuickPreferencesCompleted(user)) {
            return buildQuickPreferencesModelAndView(user);
        }
        return buildPreferenceEditorModelAndView(user);
    }

    @GetMapping("/preferences/edit")
    public ModelAndView editPreferencesForm() {
        return new ModelAndView("redirect:/select-preferences");
    }

    private ModelAndView buildQuickPreferencesModelAndView(User user) {
        ModelAndView modelAndView = new ModelAndView("client-views/conditions-preference/quick-preferences");
        modelAndView.addObject("userPreferenceForm", new UserPreferenceForm());
        modelAndView.addObject("quickPresets", QUICK_PRESETS);
        return modelAndView;
    }

    private ModelAndView buildPreferenceEditorModelAndView(User user) {
        ModelAndView modelAndView = new ModelAndView("client-views/conditions-preference/select-preferences");

        List<PhysicalCondition> allPhysicalConditions = physicalConditionService.getAllPhysicalConditions();
        List<PhysicalCondition> userPhysicalConditions = userPreferenceService.getUsersPhysicalConditions(user);

        UserPreferenceForm userPreferenceForm = userPreferenceService.getUserPreferenceForm(user);
        UserSettings settings = userSettingsService.getOrCreate(user);
        if (settings != null) {
            userPreferenceForm.setLanguage(settings.getLanguage());
            userPreferenceForm.setTheme(settings.getTheme() != null ? settings.getTheme().name() : "SYSTEM");
            userPreferenceForm.setWeatherTemperatureUnit(settings.getWeatherTemperatureUnit() != null
                    ? settings.getWeatherTemperatureUnit().name()
                    : "CELSIUS");
            userPreferenceForm.setWeatherDisplayMode(settings.getWeatherDisplayMode() != null
                    ? settings.getWeatherDisplayMode().name()
                    : "VISUAL");
            userPreferenceForm.setTimeDisplayFormat(settings.getTimeDisplayFormat() != null
                    ? settings.getTimeDisplayFormat().name()
                    : "TWELVE_HOUR");
            userPreferenceForm.setEasyMode(settings.isEasyMode());
            userPreferenceForm.setColorBlindMode(settings.isColorBlindMode());
            userPreferenceForm.setDefaultSets(settings.getDefaultSets());
            userPreferenceForm.setDefaultRepMin(settings.getDefaultRepMin());
            userPreferenceForm.setDefaultRepMax(settings.getDefaultRepMax());
            userPreferenceForm.setPreferredEquipmentBodyweight(settings.isPreferredEquipmentBodyweight());
            userPreferenceForm.setPreferredEquipmentDumbbell(settings.isPreferredEquipmentDumbbell());
            userPreferenceForm.setPreferredEquipmentBarbell(settings.isPreferredEquipmentBarbell());
            userPreferenceForm.setPreferredEquipmentMachine(settings.isPreferredEquipmentMachine());
            userPreferenceForm.setPreferredEquipmentBands(settings.isPreferredEquipmentBands());
            userPreferenceForm.setPreferredEquipmentKettlebell(settings.isPreferredEquipmentKettlebell());
            userPreferenceForm.setPreferredEquipmentCable(settings.isPreferredEquipmentCable());
            userPreferenceForm.setPreferredEquipmentPullupBar(settings.isPreferredEquipmentPullupBar());
            userPreferenceForm.setPreferredEquipmentJumpRope(settings.isPreferredEquipmentJumpRope());
            userPreferenceForm.setPreferredEquipmentMedicineBall(settings.isPreferredEquipmentMedicineBall());
            userPreferenceForm.setPreferredEquipmentFoamRoller(settings.isPreferredEquipmentFoamRoller());
            userPreferenceForm.setPreferredEquipmentTrx(settings.isPreferredEquipmentTrx());
            userPreferenceForm.setPreferredEquipmentOther(settings.isPreferredEquipmentOther());
            userPreferenceForm.setPreferredEquipmentOtherSpecify(settings.getPreferredEquipmentOtherSpecify());
            userPreferenceForm.setMacroTargetCalories(settings.getMacroTargetCalories());
            userPreferenceForm.setMacroTargetProtein(settings.getMacroTargetProtein());
            userPreferenceForm.setMacroTargetCarbs(settings.getMacroTargetCarbs());
            userPreferenceForm.setMacroTargetFat(settings.getMacroTargetFat());
            userPreferenceForm.setWeeklySummaryMetrics(parseWeeklySummaryMetrics(settings.getWeeklySummaryMetrics()));
        }
        Map<String, List<Preference>> preferencesByCategory = preferenceService.getPreferencesByCategory();
        Set<Long> lockedConditions = userPreferenceService.getLockedConditions(user);

        modelAndView.addObject("lockedConditions", lockedConditions);
        modelAndView.addObject("preferencesByCategory", preferencesByCategory);
        modelAndView.addObject("allPhysicalConditions", allPhysicalConditions);
        modelAndView.addObject("userPreferenceForm", userPreferenceForm);
        modelAndView.addObject("physicalConditions", userPhysicalConditions);

        return modelAndView;
    }

    @PostMapping("/select-preferences/quick")
    public ModelAndView completeQuickPreferences(@ModelAttribute("userPreferenceForm") UserPreferenceForm userPreferenceForm,
                                                 HttpServletRequest request,
                                                 HttpServletResponse response) {
        User user = authHelper.getAuthenticatedUser();
        if (user == null) {
            return new ModelAndView("redirect:/login");
        }

        if (!userSettingsService.isQuickPreferencesCompleted(user)) {
            applyQuickPreset(user, userPreferenceForm != null ? userPreferenceForm.getQuickPreset() : null);
            userSettingsService.updateQuickPreferencesCompleted(user, true);
        }

        UserSettings settings = userSettingsService.getOrCreate(user);
        if (settings != null) {
            String language = settings.getLanguage() != null ? settings.getLanguage() : "en";
            localeResolver.setLocale(request, response, Locale.forLanguageTag(language));
        }

        return new ModelAndView("redirect:/select-preferences?quickSetup=1");
    }

    @PostMapping("/select-preferences")
    public ModelAndView selectPreferences(@Valid @ModelAttribute("userPreferenceForm") UserPreferenceForm userPreferenceForm,
                                          BindingResult bindingResult,
                                          Model model,
                                          HttpServletRequest request,
                                          HttpServletResponse response) {
        User user = authHelper.getAuthenticatedUser();

        if (bindingResult.hasErrors()) {
            model.addAttribute("lockedConditions", userPreferenceService.getLockedConditions(user));
            model.addAttribute("allPhysicalConditions", physicalConditionService.getAllPhysicalConditions());
            model.addAttribute("preferencesByCategory", preferenceService.getPreferencesByCategory());

            return new ModelAndView("client-views/conditions-preference/select-preferences", model.asMap());
        }

        userPreferenceService.selectPreferences(user, userPreferenceForm);
        userPreferenceService.selectConditions(user, userPreferenceForm);

        ThemePreference theme = ThemePreference.SYSTEM;
        try {
            if (userPreferenceForm.getTheme() != null) {
                theme = ThemePreference.valueOf(userPreferenceForm.getTheme());
            }
        } catch (IllegalArgumentException ignored) {
            theme = ThemePreference.SYSTEM;
        }
        WeatherTemperatureUnitPreference temperatureUnit = WeatherTemperatureUnitPreference.CELSIUS;
        try {
            if (userPreferenceForm.getWeatherTemperatureUnit() != null) {
                temperatureUnit = WeatherTemperatureUnitPreference.valueOf(userPreferenceForm.getWeatherTemperatureUnit());
            }
        } catch (IllegalArgumentException ignored) {
            temperatureUnit = WeatherTemperatureUnitPreference.CELSIUS;
        }
        WeatherDisplayModePreference displayMode = WeatherDisplayModePreference.VISUAL;
        try {
            if (userPreferenceForm.getWeatherDisplayMode() != null) {
                displayMode = WeatherDisplayModePreference.valueOf(userPreferenceForm.getWeatherDisplayMode());
            }
        } catch (IllegalArgumentException ignored) {
            displayMode = WeatherDisplayModePreference.VISUAL;
        }
        TimeDisplayFormatPreference timeDisplayFormat = TimeDisplayFormatPreference.TWELVE_HOUR;
        try {
            if (userPreferenceForm.getTimeDisplayFormat() != null) {
                timeDisplayFormat = TimeDisplayFormatPreference.valueOf(userPreferenceForm.getTimeDisplayFormat());
            }
        } catch (IllegalArgumentException ignored) {
            timeDisplayFormat = TimeDisplayFormatPreference.TWELVE_HOUR;
        }
        UserSettings updated = userSettingsService.update(user, userPreferenceForm.getLanguage(), theme, userPreferenceForm.isEasyMode());
        if (updated != null) {
            userSettingsService.updateWeatherPreferences(user, temperatureUnit, displayMode);
            userSettingsService.updateTimeDisplayPreference(user, timeDisplayFormat);
            boolean hearing = updated.isDisabilityHearing();
            boolean mobility = updated.isDisabilityMobility();
            boolean vision = updated.isDisabilityVision();
            userSettingsService.updateAccessibility(user, userPreferenceForm.isColorBlindMode(), hearing, mobility, vision);
            userSettingsService.updateSmartDefaults(
                    user,
                    userPreferenceForm.getDefaultSets(),
                    userPreferenceForm.getDefaultRepMin(),
                    userPreferenceForm.getDefaultRepMax(),
                    userPreferenceForm.isPreferredEquipmentBodyweight(),
                    userPreferenceForm.isPreferredEquipmentDumbbell(),
                    userPreferenceForm.isPreferredEquipmentBarbell(),
                    userPreferenceForm.isPreferredEquipmentMachine(),
                    userPreferenceForm.isPreferredEquipmentBands(),
                    userPreferenceForm.isPreferredEquipmentKettlebell(),
                    userPreferenceForm.isPreferredEquipmentCable(),
                    userPreferenceForm.isPreferredEquipmentPullupBar(),
                    userPreferenceForm.isPreferredEquipmentJumpRope(),
                        userPreferenceForm.isPreferredEquipmentMedicineBall(),
                        userPreferenceForm.isPreferredEquipmentFoamRoller(),
                        userPreferenceForm.isPreferredEquipmentTrx(),
                        userPreferenceForm.isPreferredEquipmentOther(),
                        userPreferenceForm.getPreferredEquipmentOtherSpecify(),
                    userPreferenceForm.getMacroTargetCalories(),
                    userPreferenceForm.getMacroTargetProtein(),
                    userPreferenceForm.getMacroTargetCarbs(),
                        userPreferenceForm.getMacroTargetFat(),
                        userPreferenceForm.getWeeklySummaryMetrics()
            );

            String language = updated.getLanguage() != null ? updated.getLanguage() : "en";
            localeResolver.setLocale(request, response, Locale.forLanguageTag(language));
        }

        userSettingsService.updateQuickPreferencesCompleted(user, true);
        return new ModelAndView("redirect:/select-preferences?saved=1");
    }

    private Set<String> parseWeeklySummaryMetrics(String raw) {
        Set<String> selected = new LinkedHashSet<>();
        if (raw == null || raw.isBlank()) {
            selected.add("WORKOUTS_COMPLETED");
            selected.add("MEALS_LOGGED");
            selected.add("HABITS_COMPLETED");
            return selected;
        }
        for (String value : raw.split(",")) {
            if (value != null && !value.isBlank()) {
                selected.add(value.trim().toUpperCase(Locale.ROOT));
            }
        }
        if (selected.isEmpty()) {
            selected.add("WORKOUTS_COMPLETED");
            selected.add("MEALS_LOGGED");
            selected.add("HABITS_COMPLETED");
        }
        return selected;
    }

    @PostMapping("/select-preferences/reset")
    public ModelAndView resetSmartDefaults(HttpServletRequest request, HttpServletResponse response) {
        User user = authHelper.getAuthenticatedUser();
        UserSettings updated = userSettingsService.resetSmartDefaults(user);
        if (updated != null) {
            String language = updated.getLanguage() != null ? updated.getLanguage() : "en";
            localeResolver.setLocale(request, response, Locale.forLanguageTag(language));
        }
        return new ModelAndView("redirect:/select-preferences?reset=1");
    }

    @GetMapping("/preferences")
    public ModelAndView viewPreferences() {
        return new ModelAndView("redirect:/select-preferences");
    }

    private void applyQuickPreset(User user, String presetKey) {
        QuickPreset preset = QUICK_PRESETS.get(presetKey);
        if (user == null || preset == null) {
            return;
        }

        Map<String, Long> preferenceIdsByDescription = preferenceService.getAllPreferences().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        preference -> normalizePreferenceDescription(preference.getDescription()),
                        Preference::getId,
                        (left, right) -> left,
                        LinkedHashMap::new
                ));

        Set<Long> selectedPreferenceIds = preset.preferenceDescriptions.stream()
                .map(UserPreferenceController::normalizePreferenceDescription)
                .map(preferenceIdsByDescription::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        UserPreferenceForm form = userPreferenceService.getUserPreferenceForm(user);
        form.setSelectedPreferenceIds(selectedPreferenceIds);
        form.setDefaultSets(preset.defaultSets);
        form.setDefaultRepMin(preset.defaultRepMin);
        form.setDefaultRepMax(preset.defaultRepMax);
        form.setPreferredEquipmentBodyweight(preset.equipmentKeys.contains("bodyweight"));
        form.setPreferredEquipmentDumbbell(preset.equipmentKeys.contains("dumbbell"));
        form.setPreferredEquipmentBarbell(preset.equipmentKeys.contains("barbell"));
        form.setPreferredEquipmentMachine(preset.equipmentKeys.contains("machine"));
        form.setPreferredEquipmentBands(preset.equipmentKeys.contains("bands"));
        form.setPreferredEquipmentKettlebell(preset.equipmentKeys.contains("kettlebell"));
        form.setPreferredEquipmentCable(preset.equipmentKeys.contains("cable"));
        form.setPreferredEquipmentPullupBar(preset.equipmentKeys.contains("pullupBar"));
        form.setPreferredEquipmentJumpRope(preset.equipmentKeys.contains("jumpRope"));
        form.setPreferredEquipmentMedicineBall(preset.equipmentKeys.contains("medicineBall"));
        form.setPreferredEquipmentFoamRoller(preset.equipmentKeys.contains("foamRoller"));
        form.setPreferredEquipmentTrx(preset.equipmentKeys.contains("trx"));
        form.setPreferredEquipmentOther(false);
        form.setPreferredEquipmentOtherSpecify(null);
        form.setMacroTargetCalories(preset.macroTargetCalories);
        form.setMacroTargetProtein(preset.macroTargetProtein);
        form.setMacroTargetCarbs(preset.macroTargetCarbs);
        form.setMacroTargetFat(preset.macroTargetFat);

        userPreferenceService.selectPreferences(user, form);
        userSettingsService.updateSmartDefaults(
                user,
                form.getDefaultSets(),
                form.getDefaultRepMin(),
                form.getDefaultRepMax(),
                form.isPreferredEquipmentBodyweight(),
                form.isPreferredEquipmentDumbbell(),
                form.isPreferredEquipmentBarbell(),
                form.isPreferredEquipmentMachine(),
                form.isPreferredEquipmentBands(),
                form.isPreferredEquipmentKettlebell(),
                form.isPreferredEquipmentCable(),
                form.isPreferredEquipmentPullupBar(),
                form.isPreferredEquipmentJumpRope(),
                form.isPreferredEquipmentMedicineBall(),
                form.isPreferredEquipmentFoamRoller(),
                form.isPreferredEquipmentTrx(),
                form.isPreferredEquipmentOther(),
                form.getPreferredEquipmentOtherSpecify(),
                form.getMacroTargetCalories(),
                form.getMacroTargetProtein(),
                form.getMacroTargetCarbs(),
                form.getMacroTargetFat(),
                form.getWeeklySummaryMetrics()
        );
    }

    private static String normalizePreferenceDescription(String value) {
        if (value == null) {
            return "";
        }
        return value
                .trim()
                .replace('\u2013', '-')
                .replace('\u2014', '-')
                .replace('\u2212', '-')
                .toLowerCase(Locale.ROOT);
    }

    private static Map<String, QuickPreset> createQuickPresets() {
        Map<String, QuickPreset> presets = new LinkedHashMap<>();
        presets.put("weight-loss-beginner", new QuickPreset(
                "Weight Loss - Beginner",
                List.of("Weight Loss", "Beginner (New to exercise)", "3-4 times per week",
                        "Home Workouts", "Low Impact", "Calorie Deficit (Cutting)", "High Protein", "Prioritise Recovery Days"),
                Set.of("bodyweight"),
                3, 12, 20, 1800, 140, 160, 60
        ));
        presets.put("weight-loss-intermediate", new QuickPreset(
                "Weight Loss - Intermediate",
                List.of("Weight Loss", "Intermediate (Exercising regularly)", "3-4 times per week",
                        "Gym Workouts", "HIIT / High Intensity", "Calorie Deficit (Cutting)", "High Protein"),
                Set.of("dumbbell", "machine"),
                4, 10, 15, 2000, 160, 180, 65
        ));
        presets.put("muscle-building", new QuickPreset(
                "Muscle Building",
                List.of("Muscle Gain / Hypertrophy", "Intermediate (Exercising regularly)",
                        "3-4 times per week", "Gym Workouts", "Solo Training", "Calorie Surplus (Bulking)", "High Protein"),
                Set.of("barbell", "dumbbell", "machine"),
                4, 8, 12, 3000, 180, 300, 90
        ));
        presets.put("strength", new QuickPreset(
                "Strength Training",
                List.of("Increase Strength", "Intermediate (Exercising regularly)",
                        "3-4 times per week", "Gym Workouts", "Solo Training", "High Protein"),
                Set.of("barbell", "dumbbell"),
                5, 3, 6, 2800, 175, 280, 85
        ));
        presets.put("endurance", new QuickPreset(
                "Endurance / Cardio",
                List.of("Improve Endurance", "Intermediate (Exercising regularly)",
                        "5+ times per week", "Outdoor Activities", "Solo Training", "Active Recovery (light movement)"),
                Set.of("bodyweight"),
                3, 15, 25, 2600, 130, 320, 70
        ));
        presets.put("flexibility", new QuickPreset(
                "Flexibility & Mobility",
                List.of("Improve Flexibility & Mobility", "Beginner (New to exercise)",
                        "3-4 times per week", "Home Workouts", "Low Impact", "Regular Stretching / Yoga", "Active Recovery (light movement)"),
                Set.of("bodyweight"),
                3, 10, 20, null, null, null, null
        ));
        presets.put("general-fitness", new QuickPreset(
                "General Health & Fitness",
                List.of("General Health & Fitness", "Beginner (New to exercise)",
                        "3-4 times per week", "Gym Workouts", "Solo Training", "Prioritise Recovery Days"),
                Set.of("bodyweight", "dumbbell"),
                3, 10, 15, 2200, 130, 230, 70
        ));
        presets.put("home-beginner", new QuickPreset(
                "Home Workout - Beginner",
                List.of("General Health & Fitness", "Beginner (New to exercise)",
                        "1-2 times per week", "Home Workouts", "Low Impact", "Prioritise Recovery Days"),
                Set.of("bodyweight"),
                3, 10, 15, null, null, null, null
        ));
        return presets;
    }

    private record QuickPreset(
            String label,
            List<String> preferenceDescriptions,
            Set<String> equipmentKeys,
            Integer defaultSets,
            Integer defaultRepMin,
            Integer defaultRepMax,
            Integer macroTargetCalories,
            Integer macroTargetProtein,
            Integer macroTargetCarbs,
            Integer macroTargetFat
    ) {
    }

}
