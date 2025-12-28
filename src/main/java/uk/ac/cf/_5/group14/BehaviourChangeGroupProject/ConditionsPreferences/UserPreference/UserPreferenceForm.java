package uk.ac.cf._5.group14.BehaviourChangeGroupProject.ConditionsPreferences.UserPreference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPreferenceForm {
    Set<Long> selectedPreferenceIds = new HashSet<>();
    Set<Long> selectedConditionIds = new HashSet<>();
}
