package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Profile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public interface WaistHeightRatioTrackerDto {
    Double getWaistHeightRatio();

    LocalDateTime getDateRated();

    default String getMonthAndDay() {
        return getDateRated().format(DateTimeFormatter.ofPattern("dd MMM"));
    }
}
