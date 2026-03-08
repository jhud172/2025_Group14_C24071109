package uk.ac.cf._5.group14.BehaviourChangeGroupProject.ConditionsPreferences.UserPreference;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
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

    @Pattern(regexp = "^(en|cy)$", message = "Please select a valid language.")
    String language = "en";

    @Pattern(regexp = "^(SYSTEM|LIGHT|DARK)$", message = "Please select a valid theme.")
    String theme = "SYSTEM";

    boolean easyMode = false;
    boolean colorBlindMode = false;

    @Min(value = 1, message = "Default sets must be at least 1.")
    @Max(value = 20, message = "Default sets must be 20 or fewer.")
    Integer defaultSets;

    @Min(value = 1, message = "Minimum reps must be at least 1.")
    @Max(value = 30, message = "Minimum reps must be 30 or fewer.")
    Integer defaultRepMin;

    @Min(value = 1, message = "Maximum reps must be at least 1.")
    @Max(value = 50, message = "Maximum reps must be 50 or fewer.")
    Integer defaultRepMax;

    boolean preferredEquipmentBodyweight = false;
    boolean preferredEquipmentDumbbell = false;
    boolean preferredEquipmentBarbell = false;
    boolean preferredEquipmentMachine = false;
    boolean preferredEquipmentBands = false;
    boolean preferredEquipmentKettlebell = false;
    boolean preferredEquipmentCable = false;
    boolean preferredEquipmentPullupBar = false;
    boolean preferredEquipmentJumpRope = false;

    @Min(value = 0, message = "Calories must be 0 or more.")
    @Max(value = 20000, message = "Calories must be 20000 or less.")
    Integer macroTargetCalories;

    @Min(value = 0, message = "Protein must be 0 or more.")
    @Max(value = 1000, message = "Protein must be 1000 g or less.")
    Integer macroTargetProtein;

    @Min(value = 0, message = "Carbs must be 0 or more.")
    @Max(value = 1000, message = "Carbs must be 1000 g or less.")
    Integer macroTargetCarbs;

    @Min(value = 0, message = "Fat must be 0 or more.")
    @Max(value = 1000, message = "Fat must be 1000 g or less.")
    Integer macroTargetFat;
}
