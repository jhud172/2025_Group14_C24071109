package uk.ac.cf._5.group14.BehaviourChangeGroupProject.ConditionsPreferences.UserPreference;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.LocaleResolver;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ConditionsPreferences.Preference.Preference;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ConditionsPreferences.Preference.PreferenceService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.HealthDataInput.PhysicalCondition.PhysicalCondition;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.HealthDataInput.PhysicalCondition.PhysicalConditionService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.UserSettings.ThemePreference;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.UserSettings.UserSettings;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.UserSettings.UserSettingsService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.util.Locale;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
        ModelAndView modelAndView = new ModelAndView("conditions-preference/preference-form");
        User user = authHelper.getAuthenticatedUser();

        List<PhysicalCondition> allPhysicalConditions = physicalConditionService.getAllPhysicalConditions();
        List<PhysicalCondition> userPhysicalConditions = userPreferenceService.getUsersPhysicalConditions(user);

        UserPreferenceForm userPreferenceForm = userPreferenceService.getUserPreferenceForm(user);
        UserSettings settings = userSettingsService.getOrCreate(user);
        if (settings != null) {
            userPreferenceForm.setLanguage(settings.getLanguage());
            userPreferenceForm.setTheme(settings.getTheme() != null ? settings.getTheme().name() : "SYSTEM");
            userPreferenceForm.setEasyMode(settings.isEasyMode());
            userPreferenceForm.setColorBlindMode(settings.isColorBlindMode());
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
        UserSettings updated = userSettingsService.update(user, userPreferenceForm.getLanguage(), theme, userPreferenceForm.isEasyMode());
        if (updated != null) {
            boolean hearing = updated.isDisabilityHearing();
            boolean mobility = updated.isDisabilityMobility();
            boolean vision = updated.isDisabilityVision();
            userSettingsService.updateAccessibility(user, userPreferenceForm.isColorBlindMode(), hearing, mobility, vision);

            String language = updated.getLanguage() != null ? updated.getLanguage() : "en";
            localeResolver.setLocale(request, response, Locale.forLanguageTag(language));
        }

        return new ModelAndView("redirect:/select-preferences?saved=1");
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
