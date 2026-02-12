package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Nutrition;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class DailyNutritionLogForm {

    @NotNull(message = "Date is required.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @NotNull(message = "Calories are required.")
    @Min(value = 0, message = "Calories must be 0 or more.")
    @Max(value = 20000, message = "Calories must be 20000 or less.")
    private Integer calories;

    @NotNull(message = "Protein grams are required.")
    @Min(value = 0, message = "Protein must be 0 or more.")
    @Max(value = 1000, message = "Protein must be 1000 or less.")
    private Integer proteinGrams;

    @NotNull(message = "Carbs grams are required.")
    @Min(value = 0, message = "Carbs must be 0 or more.")
    @Max(value = 1000, message = "Carbs must be 1000 or less.")
    private Integer carbsGrams;

    @NotNull(message = "Fat grams are required.")
    @Min(value = 0, message = "Fat must be 0 or more.")
    @Max(value = 1000, message = "Fat must be 1000 or less.")
    private Integer fatGrams;

    @Min(value = 0, message = "Fibre must be 0 or more.")
    @Max(value = 200, message = "Fibre must be 200 or less.")
    private Integer fibreGrams;

    @Min(value = 0, message = "Water must be 0 or more.")
    @Max(value = 10000, message = "Water must be 10000 or less.")
    private Integer waterMl;

    @Size(max = 1000, message = "Notes cannot exceed 1000 characters.")
    private String notes;
}
