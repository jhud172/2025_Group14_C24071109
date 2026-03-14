package uk.ac.cf._5.group14.BehaviourChangeGroupProject.ConditionsPreferences.UserPreference;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.LinkedHashSet;
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
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ConditionsPreferences.Preference.Preference;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ConditionsPreferences.Preference.PreferenceService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.HealthDataInput.PhysicalCondition.PhysicalCondition;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.HealthDataInput.PhysicalCondition.PhysicalConditionService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.UserSettings.ThemePreference;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.UserSettings.TimeDisplayFormatPreference;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.UserSettings.WeatherDisplayModePreference;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.UserSettings.WeatherTemperatureUnitPreference;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.UserSettings.UserSettings;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.UserSettings.UserSettingsService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

@Controller
public class UserPreferenceController {

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
        if (userPreferenceService.hasCompletedPreferenceSetup(user)) {
            return new ModelAndView("redirect:/preferences");
        }
        return buildPreferenceFormModelAndView(user, 1);
    }

    @GetMapping("/preferences/edit")
    public ModelAndView editPreferencesForm() {
        User user = authHelper.getAuthenticatedUser();
        if (!userPreferenceService.hasCompletedPreferenceSetup(user)) {
            return new ModelAndView("redirect:/select-preferences");
        }
        return buildPreferenceFormModelAndView(user, 2);
    }

    private ModelAndView buildPreferenceFormModelAndView(User user, int initialStep) {
        ModelAndView modelAndView = new ModelAndView("conditions-preference/preference-form");

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
        modelAndView.addObject("initialStep", initialStep);

        return modelAndView;
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

            return new ModelAndView("conditions-preference/preference-form", model.asMap());
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

        return new ModelAndView("redirect:/preferences");
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
        ModelAndView modelAndView = new ModelAndView("conditions-preference/view-preferences");
        User user = authHelper.getAuthenticatedUser();
        List<Preference> preferences = userPreferenceService.getUserPreferences(user);
        List<PhysicalCondition> physicalConditions = userPreferenceService.getUsersPhysicalConditions(user);

        Map<String, List<Preference>> preferencesByCategory = preferences.stream()
            .collect(Collectors.groupingBy(Preference::getCategory));

        UserSettings settings = userSettingsService.getOrCreate(user);

        modelAndView.addObject("preferences", preferences);
        modelAndView.addObject("preferencesByCategory", preferencesByCategory);
        modelAndView.addObject("physicalConditions", physicalConditions);
        modelAndView.addObject("userSettings", settings);

        return modelAndView;
    }


}
