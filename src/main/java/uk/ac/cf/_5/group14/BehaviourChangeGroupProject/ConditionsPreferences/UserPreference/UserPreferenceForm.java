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

    String language = "en";
    String theme = "SYSTEM";
    boolean easyMode = false;
    boolean colorBlindMode = false;

    Integer defaultSets;
    Integer defaultRepMin;
    Integer defaultRepMax;

    boolean preferredEquipmentBodyweight = false;
    boolean preferredEquipmentDumbbell = false;
    boolean preferredEquipmentBarbell = false;
    boolean preferredEquipmentMachine = false;
    boolean preferredEquipmentBands = false;
    boolean preferredEquipmentKettlebell = false;

    Integer macroTargetCalories;
    Integer macroTargetProtein;
    Integer macroTargetCarbs;
    Integer macroTargetFat;
}
