package uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseLog;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.LocalDate;

@Getter
@Setter
public class ExerciseLogForm {
    @NotNull(message = "Date is required.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @NotNull(message = "Mood rating is required.")
    @Min(value = 1, message = "Mood must be between 1 and 4.")
    @Max(value = 4, message = "Mood must be between 1 and 4.")
    private Integer moodBefore;

    @NotNull(message = "Mood rating is required.")
    @Min(value = 1, message = "Mood must be between 1 and 4.")
    @Max(value = 4, message = "Mood must be between 1 and 4.")
    private Integer moodAfter;

    @NotNull(message = "Confidence rating is required.")
    @Min(value = 1, message = "Confidence must be between 1 and 4.")
    @Max(value = 4, message = "Confidence must be between 1 and 4.")
    private Integer confidence;

    @Size(max = 300, message = "Comments cannot exceed 300 characters.")
    private String comments;

    private Long occurrenceId;
    private String exerciseType;
    private Long calendarTaskId;
    private Integer durationMinutes;


}
