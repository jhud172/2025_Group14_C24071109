package uk.ac.cf._5.group14.BehaviourChangeGroupProject.ConditionsPreferences.Preference;

import java.util.List;
import java.util.Map;

public interface PreferenceService {
    List<Preference> getAllPreferences();
    Map<String, List<Preference>> getPreferencesByCategory();
}
