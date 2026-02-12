package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Verification;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateTrainerNotesForm {

    @NotBlank(message = "Notes are required")
    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;
}
