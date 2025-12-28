package uk.ac.cf._5.group14.BehaviourChangeGroupProject.ConditionsPreferences.UserPreference;

import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ConditionsPreferences.Preference.Preference;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.HealthDataInput.PhysicalCondition.PhysicalCondition;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserPreferenceService {
    List<PhysicalCondition> getUsersPhysicalConditions(User user);

    UserPreferenceForm getUserPreferenceForm(User user);

    void selectPreferences(User user, UserPreferenceForm userPreferenceForm);

    List<Preference> getUserPreferences(User user);

    void selectConditions(User user, UserPreferenceForm userPreferenceForm);

    Set<Long> getLockedConditions(User user );

    Map<String, List<Preference>> getUserPreferencesByCategory(User user);
}
