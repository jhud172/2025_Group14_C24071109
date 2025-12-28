package uk.ac.cf._5.group14.BehaviourChangeGroupProject.ConditionsPreferences.UserPreference;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ConditionsPreferences.Preference.Preference;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ConditionsPreferences.Preference.PreferenceService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.HealthDataInput.PhysicalCondition.PhysicalCondition;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.HealthDataInput.PhysicalCondition.PhysicalConditionService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserService;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
public class UserPreferenceController {

    private final UserPreferenceService userPreferenceService;
    private final PreferenceService preferenceService;
    private final PhysicalConditionService physicalConditionService;

    @Autowired
    private AuthHelper authHelper;

    public UserPreferenceController(UserPreferenceService userPreferenceService, PreferenceService preferenceService, PhysicalConditionService physicalConditionService) {
        this.userPreferenceService = userPreferenceService;
        this.preferenceService = preferenceService;
        this.physicalConditionService = physicalConditionService;
    }

    @GetMapping("/select-preferences")
    public ModelAndView getPreferenceForm() {
        ModelAndView modelAndView = new ModelAndView("conditions-preference/preference-form");
        User user = authHelper.getAuthenticatedUser();

        List<PhysicalCondition> allPhysicalConditions = physicalConditionService.getAllPhysicalConditions();
        List<PhysicalCondition> userPhysicalConditions = userPreferenceService.getUsersPhysicalConditions(user);

        UserPreferenceForm userPreferenceForm = userPreferenceService.getUserPreferenceForm(user);
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
    public ModelAndView selectPreferences(@Valid @ModelAttribute("userPreferenceForm") UserPreferenceForm userPreferenceForm, BindingResult bindingResult, Model model) {
        User user = authHelper.getAuthenticatedUser();

        if (bindingResult.hasErrors()) {
            model.addAttribute("lockedConditions", userPreferenceService.getLockedConditions(user));
            model.addAttribute("allPhysicalConditions", physicalConditionService.getAllPhysicalConditions());
            model.addAttribute("preferencesByCategory", preferenceService.getPreferencesByCategory());

            return new ModelAndView("conditions-preference/preference-form", model.asMap());
        }

        userPreferenceService.selectPreferences(user, userPreferenceForm);
        userPreferenceService.selectConditions(user, userPreferenceForm);
        return new ModelAndView("redirect:/workout");
    }

    @GetMapping("/preferences")
    public ModelAndView viewPreferences() {
        ModelAndView modelAndView = new ModelAndView("conditions-preference/view-preferences");
        User user = authHelper.getAuthenticatedUser();
        List<Preference> preferences = userPreferenceService.getUserPreferences(user);
        List<PhysicalCondition> physicalConditions = userPreferenceService.getUsersPhysicalConditions(user);

        modelAndView.addObject("preferences", preferences);
        modelAndView.addObject("physicalConditions", physicalConditions);

        return modelAndView;
    }


}
