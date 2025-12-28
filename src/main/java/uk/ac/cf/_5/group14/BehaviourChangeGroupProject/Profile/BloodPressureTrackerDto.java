package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Profile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public interface BloodPressureTrackerDto {
    Integer getSystolicBP();

    Integer getDiastolicBP();

    LocalDateTime getDateRated();

    default String getMonthAndDay() {
        return getDateRated().format(DateTimeFormatter.ofPattern("dd MMM"));
    }
}
